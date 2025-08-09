using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using ReLogic.Content;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Terraria;
using Terraria.DataStructures;
using Terraria.ID;
using Terraria.Localization;
using Terraria.ModLoader;

using static KillLife.KillLife;
using static Terraria.ModLoader.PlayerDrawLayer;

namespace KillLife;

/// <summary>
/// 左键进行蓄力，最低蓄力时间一秒
/// 蓄力时间长短决定治疗量
/// 蓄力时生成光球，光球随蓄力时间增大
/// 松开右键，光球向鼠标方向行进，
/// 对命中的目标进行治疗，记录治疗量，
/// 若治疗溢出，
/// 则在完成治疗后扣除溢出值
/// 
/// 进度 :
///     1. 完成了治疗与治疗的联机同步
/// 
/// 计划 :
///     1. 物品使用时的绘制问题
///     2. 弹幕蓄力才能治疗 最低1s
///     3. 弹幕蓄力时的行为(在剑的上面)
///     4. 弹幕蓄力完成后的行为
/// </summary>
// Please read https://github.com/tModLoader/tModLoader/wiki/Basic-tModLoader-Modding-Guide#mod-skeleton-contents for more information about the various files in a mod.
public class KillLife : Mod
{
    public static Mod Mod => ModLoader.GetMod(nameof(KillLife));

    #region NetAsycn
    /// <summary>
    /// <para> 由于 <see cref="Main.projectile"/> 不保证弹幕顺序 </para> 
    /// <para> 此字段用于 <see cref="LifeProjectile"/> 唯一标识符 </para>
    /// </summary>
    public static int lifeProjectileNetField = 0;
    public override void HandlePacket(BinaryReader reader, int whoAmI)
    {
        NetType type = (NetType)reader.ReadInt32();
        switch (type) {
            case NetType.LifeProjectilePosition:
                if (Main.netMode == NetmodeID.Server)
                    ServerLifeProjectilePosition(reader, whoAmI);
                else
                    ClientLifeProjectilePosition(reader);
                break;

            case NetType.LifeProjectileNetField:
                if(Main.netMode == NetmodeID.Server)
                    ServerLifeProjectileNetField(whoAmI);
                else
                    ClientLifeProjectileNetField(reader);
                break;

            default:
                var sr = new StreamReader(reader.BaseStream);
                sr.ReadToEnd();
                sr.Dispose();
                break;
        }
        base.HandlePacket(reader, whoAmI);
    }

    #region Server Hander
    /// <summary>
    /// 同步网络字段
    /// <para> 包类型 <see cref="NetType"/> </para> 
    /// <para> 包值 <see cref="lifeProjectileNetField"/>  <see cref="System.Int32"/> </para>
    /// </summary>
    private static void ServerLifeProjectileNetField(int whoAmI)
    {
        if (lifeProjectileNetField + 1 == int.MaxValue) {
            lifeProjectileNetField = 0;
        } else {
            lifeProjectileNetField += 1;
        }
        var packet = Mod.GetPacket();
        packet.Write((int)NetType.LifeProjectileNetField);
        packet.Write(lifeProjectileNetField);
        packet.Send();
    }

    /// <summary>
    /// 用于同步 <see cref="LifeProjectile.Position"/>
    /// <para> 会转发到其他客户端  </para>
    /// <para> 包类型 <see cref="NetType.LifeProjectilePosition"/> </para>
    /// <para> 位置  <see cref="Vector2"/> </para>
    /// <para> 标识  <see cref="System.Int32"/> </para>
    /// </summary>
    private static void ServerLifeProjectilePosition(BinaryReader reader, int whoAmI)
    {
        if(Main.netMode != NetmodeID.Server) {
            using var sr = new StreamReader(reader.BaseStream);
            sr.ReadToEnd();
            throw new System.Exception($"{nameof(ServerLifeProjectilePosition)}不应该在非服务端调用！");
        }

        //1. Vector2 位置
        //2. NetField Int32
        Vector2 position = reader.ReadVector2();
        int netfieldValue = reader.ReadInt32();

        var tarProjectile =
            from proj in Main.projectile.AsEnumerable()
            where proj.ModProjectile != null && proj.ModProjectile is LifeProjectile
            select proj.ModProjectile as LifeProjectile
            into lifep
            where lifep.netField == netfieldValue
            select lifep;

        foreach(var proj in tarProjectile) {
            proj.Position = position;
        }

        var packet = Mod.GetPacket();
        packet.Write((int)NetType.LifeProjectilePosition);
        packet.WriteVector2(position);
        packet.Write(netfieldValue);
        packet.Send(ignoreClient: whoAmI);
    }
    #endregion

    #region Client Hander
    /// <summary>
    /// <para> 用于处理服务器传过来的数据 </para>
    /// <para> <see cref="HandlePacket(BinaryReader, int)"/> 中 </para> 
    /// <para> 当<see cref="Main.netMode"/> == <see cref="NetmodeID.MultiplayerClient"/> </para>
    /// </summary>
    private static void ClientLifeProjectileNetField(BinaryReader reader)
    {
        _ = nameof(ServerLifeProjectileNetField);
        var netFieldValue = reader.ReadInt32();
        lifeProjectileNetField = netFieldValue;
    }

    /// <summary>
    /// <para> 用于处理服务器传过来的数据 </para>
    /// <para> <see cref="HandlePacket(BinaryReader, int)"/> 中 </para> 
    /// <para> 当<see cref="Main.netMode"/> == <see cref="NetmodeID.MultiplayerClient"/> </para>
    /// </summary>
    private static void ClientLifeProjectilePosition(BinaryReader reader)
    {
        _ = nameof(ServerLifeProjectilePosition);
        var pos = reader.ReadVector2();
        var netField = reader.ReadInt32();

        var projs =
            from p in Main.projectile
            where p.active == true && p.ModProjectile != null && p.ModProjectile is LifeProjectile
            select p.ModProjectile as LifeProjectile
            into p1
            where p1.netField == netField
            select p1;

        foreach (var proj in projs) {
            proj.Position = pos;
        }
    }
    #endregion
    /// <summary>
    /// 当<see cref="NetmodeID.MultiplayerClient"/>生成新需要<see cref="lifeProjectileNetField"/>的弹幕时，调用此方法，通知服务器同步<see cref="lifeProjectileNetField"/>
    /// </summary>
    /// <exception cref="System.Exception">非客户端调用</exception>
    public static void SendNetField()
    {
        if (Main.netMode == NetmodeID.SinglePlayer)
            return;
        if (NetmodeID.MultiplayerClient != Main.netMode) {
            throw new System.Exception("不能在非客户端使用" + nameof(SendNetField) + "方法");
        }
        var packet = Mod.GetPacket();
        packet.Write((int)NetType.LifeProjectileNetField);
        packet.Send();
    }
    #endregion
}

public class LifeProjectile : ModProjectile
{
    public static Asset<Texture2D> LifeItemTexture { get; private set; }

    public override string Texture => "Terraria/Images/Projectile_" + ProjectileID.EmpressBlade;
    public Vector2 Position = Main.player[Main.myPlayer].Center;
    public int netTimer = 0;
    public int netField => (int)Projectile.ai[0];
    public bool isLife = false;
    public Player Owner => Main.player[Projectile.owner];
    public Dictionary<int, Action> EffectApplay = [];

    public override void SetStaticDefaults()
    {
        LifeItemTexture = ModContent.Request<Texture2D>("KillLife/LifeItem", AssetRequestMode.ImmediateLoad);
        base.SetStaticDefaults();
    }

    public override void SetDefaults()
    {
        EffectApplay.TryAdd(NetmodeID.MultiplayerClient, KillLifePlayer);
        EffectApplay.TryAdd(NetmodeID.Server, KillLifeNPC);
        EffectApplay.TryAdd(NetmodeID.SinglePlayer,() => { KillLifeNPC(); KillLifePlayer(); });
    }

    public override bool PreAI()
    {
        if (Projectile.owner == Main.myPlayer) {
            Position = Main.MouseWorld;
            if (netTimer % 2 == 0) {
                SendPosition(this);
            }

            if (Owner == null || Owner.channel != true) {
                Projectile.Kill();
            }
        }
        Projectile.Center = Position;
        netTimer++;
        Owner.heldProj = Projectile.whoAmI;
        EffectApplay[Main.netMode]();
        return base.PreAI();
    }

    public override bool PreDraw(ref Color lightColor)
    {
        float basicRotation = -45f;
        #region 蓄力时的绘制行为
        Owner.SetCompositeArmFront(true, Player.CompositeArmStretchAmount.Full, MathHelper.ToRadians(90f) * Owner.direction * -1);
        Owner.SetCompositeArmBack(true, Player.CompositeArmStretchAmount.Full, MathHelper.ToRadians(90f) * Owner.direction * -1);
        /*LifePlayerLayer.FrontDraw += () => */
        Main.spriteBatch.Draw(LifeItemTexture.Value, Owner.SmoothCenter() + new Vector2(20f * Owner.direction,-15f) - Main.screenPosition, null, Color.White, MathHelper.ToRadians(basicRotation) + Owner.fullRotation, LifeItemTexture.Center(), 2f, SpriteEffects.None, 1f);
        #endregion
        return false;
    }

    public static void SendPosition(LifeProjectile projectile)
    {
        if (Main.netMode == NetmodeID.SinglePlayer)
            return;
        if (NetmodeID.MultiplayerClient != Main.netMode) {
            throw new System.Exception("不能在非客户端使用" + nameof(SendPosition) + "方法");
        }
        var packet = projectile.Mod.GetPacket();
        packet.Write((int)NetType.LifeProjectilePosition);
        packet.WriteVector2(projectile.Position);
        packet.Write(projectile.netField);
        packet.Send(); //客户端唯一的发送方只有服务器
    }

    #region EffectAppaly
    /// <summary>
    /// 对弹幕碰撞箱内的npc造成治疗
    /// </summary>
    /// <exception cref="Exception">不应该在非 Server || SinglePlayer 下调用此方法</exception>
    private void KillLifeNPC()
    {
        if (Main.netMode == NetmodeID.Server || Main.netMode == NetmodeID.SinglePlayer) {
            foreach (var npc in Main.ActiveNPCs) {
                if (npc.CollidingAABB(Projectile)) {
                    var lifeAdd = 50;
                    var addlife = npc.life + 50;
                    var subLife = addlife - npc.lifeMax;
                    npc.life += npc.life + lifeAdd > npc.lifeMax ? npc.life - npc.lifeMax : lifeAdd;
                    npc.HealEffect(lifeAdd);
                    npc.netUpdate = true;
                    Owner.dpsDamage += 50;
                    if (subLife > 0) {
                        Owner.dpsDamage -= subLife;
                        npc.StrikeNPC(new NPC.HitInfo() { Damage = subLife });
                    }
                }
            }
        } else {
            throw new Exception($"不应该在非 Server || SinglePlayer 下调用此方法 {GetType().FullName}.{nameof(KillLifeNPC)}");
        }
    }

    /// <summary>
    /// 对碰撞箱内的玩家进行治疗
    /// </summary>
    /// <exception cref="Exception">不应该在非 Server || SinglePlayer 下调用此方法</exception>
    private void KillLifePlayer()
    {
        if (Main.netMode == NetmodeID.SinglePlayer || Main.netMode == NetmodeID.MultiplayerClient) {
            foreach (var player in Main.ActivePlayers) {
                if (player.CollidingAABB(Projectile)) {
                    var lifeAdd = 50;
                    var addlife = player.statLife + 50;
                    var subLife = addlife - player.statLifeMax2;
                    player.Heal(lifeAdd);//会调用SendMessage() 向服务器同步
                    if (subLife > 0) {
                        player.Hurt(PlayerDeathReason.ByCustomReason(NetworkText.FromLiteral("好像挂掉了")), subLife, 0);
                        player.immuneTime = 0;
                    }
                }
            }
        } else {
            throw new Exception($"不应该在非 Server || SinglePlayer 下调用此方法 {GetType().FullName}.{nameof(KillLifePlayer)}");
        }
    }
    #endregion

}

public class LifeItem : ModItem
{
    //public override string Texture => "Terraria/Images/Projectile_" + ProjectileID.EmpressBlade;
    public override void SetDefaults()
    {
        Item.damage = 0;
        Item.autoReuse = false;
        Item.useTime = 20;
        Item.useAnimation = 20;

        Item.useStyle = ItemUseStyleID.HoldUp;
        Item.DamageType = DamageClass.Generic;

        Item.noUseGraphic = true;
        Item.noMelee = true;

        Item.shoot = projType;

        Item.channel = true;
        base.SetDefaults();
    }

    public override bool AltFunctionUse(Player player)
    {
        return base.AltFunctionUse(player);
    }

    private readonly static int projType = ModContent.ProjectileType<LifeProjectile>();

    public override bool CanUseItem(Player player)
    {
        if(player.ownedProjectileCounts[projType] > 0) {
            return false;
        }
        return base.CanUseItem(player);
    }

    public override bool Shoot(Player player, EntitySource_ItemUse_WithAmmo source, Vector2 position, Vector2 velocity, int type, int damage, float knockback)
    {
        if (player.ownedProjectileCounts[projType] == 0) {
            Projectile.NewProjectile(player.GetSource_ItemUse(this.Item), Main.MouseWorld, default, ModContent.ProjectileType<LifeProjectile>(), default, default, player.whoAmI, lifeProjectileNetField);
            SendNetField();
            Main.NewText("NetField: " + lifeProjectileNetField);
        }

        return false;
    }
}


public enum NetType
{
    LifeProjectilePosition,
    LifeProjectileNetField
}

public static class EntityExtension
{
    public static bool CollidingAABB(this Entity entity1, Entity entity2)
    {
        var projHitbox = new Rectangle((int)entity1.position.X, (int)entity1.position.Y, entity1.width, entity1.height);
        var targetHitbox = new Rectangle((int)entity2.position.X, (int)entity2.position.Y, entity2.width, entity2.height);
        Vector2 pos1 = new(projHitbox.X, projHitbox.Y);
        Vector2 boom1 = new(projHitbox.Width, projHitbox.Height);

        Vector2 pos2 = new(targetHitbox.X, targetHitbox.Y);
        Vector2 boom2 = new(targetHitbox.Width, targetHitbox.Height);
        return Collision.CheckAABBvAABBCollision(pos1, boom1, pos2, boom2);
    }
}

public static class AssetExtension
{
    public static Vector2 Center(this Asset<Texture2D> asset)
    {
        var height = asset.Height();
        var width = asset.Width();

        return new Vector2(width / 2, height / 2);
    }
}

public static class PlayerExtension
{
    public static Vector2 SmoothCenter(this Player player)
    {
        return player.MountedCenter.Floor() + new Vector2(0, player.gfxOffY);
    }
}

#region Del
//public class LifePlayerLayer : PlayerDrawLayer
//{
//    public override void Load()
//    {
//        base.Load();
//    }

//    public override Position GetDefaultPosition()
//    {
//        //return new AfterParent(PlayerDrawLayers.FrontAccBack);
//        //return new AfterParent(PlayerDrawLayers.FrontAccFront);
//        //return new AfterParent(PlayerDrawLayers.HeldItem);

//        return new BeforeParent(PlayerDrawLayers.HeldItem);
//    }

//    public override bool GetDefaultVisibility(PlayerDrawSet drawInfo)
//    {
//        return !(FrontDraw == null);
//    }

//    /// <summary>
//    /// 在玩家双手之间绘制
//    /// </summary>
//    public static event Action FrontDraw;
//    protected override void Draw(ref PlayerDrawSet drawInfo)
//    {
//        FrontDraw?.Invoke();
//        foreach (var item in FrontDraw.GetInvocationList()) {
//            FrontDraw -= (Action)item;
//        }
//    }
//}
#endregion
