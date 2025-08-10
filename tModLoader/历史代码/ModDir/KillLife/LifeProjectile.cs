using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using ReLogic.Content;
using System;
using System.Collections.Generic;
using System.IO;
using Terraria;
using Terraria.DataStructures;
using Terraria.ID;
using Terraria.Localization;
using Terraria.ModLoader;

namespace KillLife;

public class LifeProjectile : ModProjectile
{
    public static Asset<Texture2D> LifeItemTexture { get; private set; }
    public static Asset<Texture2D> LifeProjectileTexture { get; private set; }
    public readonly HashSet<Entity> damageEntity = [];

    public override string Texture => "Terraria/Images/Projectile_" + ProjectileID.EmpressBlade;
    /// <summary>
    /// 此弹幕的中心位置
    /// </summary>
    public Vector2 Position = Main.player[Main.myPlayer].Center;
    public Vector2 tarGetPosition;
    /// <summary>
    /// <see cref="netTimer"/> % 2 == 0 则对 <see cref="Position"/> 进行同步
    /// </summary>
    public int netTimer = 0;
    public int netField => (int)Projectile.ai[0];
    /// <summary>
    /// 是否出于可以进行治疗阶段，如果为 false 则是蓄力
    /// </summary>
    public Status statu = Status.Golex;
    public bool ToLife => Owner.channel == false && gloexTimer > 60;
    /// <summary>
    /// 蓄力大小 每 Tick + 0.1f
    /// </summary>
    public float gloexScale = 0f;
    public Player Owner => Main.player[Projectile.owner];
    public readonly Dictionary<int, Action> EffectApply = [];
    public readonly Dictionary<int, Action> PreDrawApply = [];
    public int gloexTimer = 0;
    public override void SetStaticDefaults()
    {
        LifeItemTexture = ModContent.Request<Texture2D>("KillLife/LifeItem", AssetRequestMode.ImmediateLoad);
        LifeProjectileTexture = ModContent.Request<Texture2D>("KillLife/LifeProjectile", AssetRequestMode.ImmediateLoad);
        base.SetStaticDefaults();
    }

    private void nullm() { }

    public override void SetDefaults()
    {
        EffectApply.TryAdd(NetmodeID.MultiplayerClient, () => { KillLifeNPC(); KillLifePlayer(); });
        EffectApply.TryAdd(NetmodeID.Server, () => { KillLifeNPC(); KillLifePlayer(); });
        EffectApply.TryAdd(NetmodeID.SinglePlayer,() => { KillLifeNPC(); KillLifePlayer(); });

        PreDrawApply.TryAdd((int)Status.Golex, () => { ProjectileGolexDraw(); ItemGolexDraw(); });
        PreDrawApply.TryAdd((int)Status.Life, ProjectileLifeDraw);
    }

    public override bool PreAI()
    {
        if (Projectile.owner == Main.myPlayer) {
            if(Owner.channel == true && statu == Status.Golex) {
                gloexScale = Math.Clamp(gloexScale + 0.1f, 0f, 9f);
                gloexTimer += 1;
                Position = ProjectilePosition;
                Projectile.timeLeft = 999;
                Projectile.velocity = default;
            }
            
            if (netTimer % 2 == 0 && statu == Status.Golex) { //蓄力完就没必要继续同步了
                SendPosition(this);
                SendGloexScale(this);
                Projectile.netUpdate = true;
            }

            if (Owner == null || Owner.dead || Owner.CCed || (Owner.channel == false && gloexTimer < 60)) {
                Projectile.Kill();
            }

            if (ToLife && statu == Status.Golex) {
                statu = Status.Life;
                Projectile.timeLeft = 240;
                Projectile.velocity = (Main.MouseWorld  - Projectile.Center).SafeNormalize(default) * 6f;
                Projectile.netUpdate = true;
            }
            Owner.itemTime = 2;
            //Owner.itemAnimation = 2;
        }
        
        netTimer++;
        Owner.heldProj = Projectile.whoAmI;
        if(statu == Status.Life) {
            EffectApply[Main.netMode]();
            //if((Main.netMode == NetmodeID.SinglePlayer || Main.netMode == NetmodeID.MultiplayerClient) && Projectile.owner == Main.myPlayer)
            //    Position = Vector2.Lerp(Projectile.Center, Main.MouseWorld, Math.Abs((Projectile.timeLeft / 600f - 1f)));
        }

        if(statu == Status.Golex)
            Projectile.Center = Position;
        return base.PreAI();
    }

    public override bool PreDraw(ref Color lightColor)
    {
        PreDrawApply[(int)statu]();
        return false;
    }

    /// <summary>
    /// <see cref="KillLife.ClientLifeProjectileGloexScale(BinaryReader, int)"/>
    /// <see cref="KillLife.ServerLifeProjectileGloexScale(BinaryReader, int)"/>
    /// </summary>
    /// <param name="projectile"></param>
    /// <exception cref="System.Exception"></exception>
    public static void SendGloexScale(LifeProjectile projectile)
    {
        if (Main.netMode == NetmodeID.SinglePlayer)
            return;
        if (NetmodeID.MultiplayerClient != Main.netMode) {
            throw new System.Exception("不能在非客户端使用" + nameof(SendGloexScale) + "方法");
        }

        var packet = projectile.Mod.GetPacket();
        WriteGloexScale(packet, projectile);
        packet.Send(); //客户端唯一的发送方只有服务器
    }

    public static void WriteGloexScale(ModPacket packet, LifeProjectile projectile)
    {
        packet.Write((int)NetType.LifeProjectileGloexScale);
        packet.Write(projectile.gloexScale);
        packet.Write(projectile.netField);
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
    /// <exception cref="Exception">不应该在非 MultiplayerClient || SinglePlayer 下调用此方法</exception>
    private void KillLifeNPC()
    {
        if ((Main.netMode == NetmodeID.MultiplayerClient || Main.netMode == NetmodeID.SinglePlayer) && Projectile.owner == Main.myPlayer) {
        }
        foreach (var npc in Main.ActiveNPCs) {
            if (npc.CollidingAABB(Projectile) && !damageEntity.Contains(npc)) {
                damageEntity.Add(npc);
                var lifeAdd = (int)(gloexScale * 15f);//治疗量
                var value = GetKillOrAddLifeValue(npc, lifeAdd);
                if (value == 0) {
                    continue;
                } else if (value > 0) {
                    npc.life += Math.Abs(value);
                    npc.HealEffect(Math.Abs(value));
                } else if (value < 0) {
                    int subLife = Math.Abs(value);
                    var info = new NPC.HitInfo() { Damage = subLife, HideCombatText = false };
                    Owner.dpsDamage -= subLife;
                    npc.StrikeNPC(info); //对NPC造成溢出量的伤害
                    npc.immune[Owner.whoAmI] = 0;
                }
            }
        }
    }

    /// <summary>
    /// 对碰撞箱内的玩家进行治疗
    /// </summary>
    /// <exception cref="Exception">不应该在非 MultiplayerClient || SinglePlayer 下调用此方法</exception>
    private void KillLifePlayer()
    {
        if ((Main.netMode == NetmodeID.MultiplayerClient || Main.netMode == NetmodeID.SinglePlayer)) {
        }
        foreach (var player in Main.ActivePlayers) {
            if (player.CollidingAABB(Projectile) && !damageEntity.Contains(player)) {
                damageEntity.Add(player);
                var lifeAdd = (int)(gloexScale * 15f);
                var value = GetKillOrAddLifeValue(player, lifeAdd);
                if (value == 0) {
                    continue;
                } else if (value > 0) {
                    player.Heal(value);
                } else if(value < 0) {
                    int subLife = Math.Abs(value);
                    var damageSource = PlayerDeathReason.ByCustomReason(NetworkText.FromLiteral("好像挂掉了"));
                    var info = new Player.HurtInfo()
                    {
                        Damage = subLife,
                        DustDisabled = true,  //不生成粒子
                        Dodgeable = false,  //不能闪避
                        SoundDisabled = true, //不播放声音
                        //CooldownCounter = ImmunityCooldownID.WrongBugNet, //吃哪种无敌帧
                        DamageSource = damageSource,
                    };
                    player.Hurt(info);
                    player.immuneTime = 0;
                }
            }
        }
    }
    #endregion

    #region PreDrawApply
    private static SpriteBatchParams NonPremultipliedParams => new SpriteBatchParams()
    {
        BlendState = BlendState.NonPremultiplied,
        SamplerState = SamplerState.PointClamp,
        DepthStencilState = DepthStencilState.None,
        RasterizerState = RasterizerState.CullNone,
        Effect = null,
        SortMode = SpriteSortMode.Immediate,
        TransformMatrix = Main.GameViewMatrix.TransformationMatrix
    };

    private float ProjectileDrawOffsety => LifeItemTexture.Height() * itemDrawScale;
    private float projectileDrawScale = 0f;
    private Vector2 ProjectileDrawPosition => ProjectilePosition - Main.screenPosition;

    private Vector2 ProjectilePosition => statu == Status.Golex ? (ItemPosition + new Vector2(0f, ProjectileDrawOffsety * -1).RotatedBy(Owner.fullRotation)) : (Projectile.Center);

    /// <summary>
    /// 弹幕蓄力的绘制
    /// </summary>
    private void ProjectileGolexDraw()
    {
        projectileDrawScale = Math.Clamp(projectileDrawScale + gloexScale * 0.01f, 0f, 0.5f);
        Main.spriteBatch.TakeSnapshotAndEnd(out var snapshot);
        NonPremultipliedParams.TransformMatrix = snapshot.TransformMatrix;
        Main.spriteBatch.Begin(NonPremultipliedParams);
        Main.spriteBatch.Draw(LifeProjectileTexture.Value, ProjectileDrawPosition, null, Color.Red, 0f, LifeProjectileTexture.Center(), projectileDrawScale, SpriteEffects.None, 1f);
        Main.spriteBatch.Restart(snapshot);
    }

    private void ProjectileLifeDraw()
    {
        Main.spriteBatch.Draw(LifeProjectileTexture.Value, ProjectileDrawPosition, null, Color.Red, 0f, LifeProjectileTexture.Center(), projectileDrawScale, SpriteEffects.None, 1f);
    }

    /// <summary>
    /// 物品绘制缩放大小
    /// </summary>
    private float itemDrawScale = 2f;
    /// <summary>
    /// 物品绘制基于玩家中心偏移的x值
    /// </summary>
    private float itemDrawPlayerOffsetx = 20;
    /// <summary>
    /// 物品绘制基于玩家中心偏移的y值
    /// </summary>
    private float itemDrawPlayerOffsety = -15;
    /// <summary>
    /// 物品绘制基于玩家中心的坐标
    /// </summary>
    private Vector2 ItemDrawPlayerOffset
    {
        get
        {
            return new Vector2(itemDrawPlayerOffsetx * Owner.direction, itemDrawPlayerOffsety);
        }
        set
        {
            itemDrawPlayerOffsetx = value.X;
            itemDrawPlayerOffsety = value.Y;
        }
    }

    /// <summary>
    /// 物品绘制的屏幕坐标
    /// </summary>
    private Vector2 ItemDrawPosition => ItemPosition - Main.screenPosition;
    private Vector2 ItemPosition => Owner.SmoothCenter() + ItemDrawPlayerOffset;

    /// <summary>
    /// 绘制玩家手的旋转和武器
    /// </summary>
    private void ItemGolexDraw()
    {
        float basicRotation = -45f;
        Owner.SetCompositeArmFront(true, Player.CompositeArmStretchAmount.Full, MathHelper.ToRadians(90f) * Owner.direction * -1);
        Owner.SetCompositeArmBack(true, Player.CompositeArmStretchAmount.Full, MathHelper.ToRadians(90f) * Owner.direction * -1);
        Main.spriteBatch.Draw(LifeItemTexture.Value, ItemDrawPosition, null, Color.White, MathHelper.ToRadians(basicRotation) + Owner.fullRotation, LifeItemTexture.Center(), itemDrawScale, SpriteEffects.None, 1f);
    }
    #endregion

    public override void SendExtraAI(BinaryWriter writer)
    {
        writer.Write(gloexTimer);
        writer.Write((int)statu);
        writer.Write(Projectile.timeLeft);
        writer.WriteVector2(Projectile.velocity);
        base.SendExtraAI(writer);
    }

    public override void ReceiveExtraAI(BinaryReader reader)
    {
        gloexTimer = reader.ReadInt32();
        statu = (Status)reader.ReadInt32();
        Projectile.timeLeft = reader.ReadInt32();
        Projectile.velocity = reader.ReadVector2();
        base.ReceiveExtraAI(reader);
    }

    /// <summary>
    /// 900治疗量
    ///        玩家200生命上限，当前50生命
    ///        +150-200+200-200+150→ 治疗150生命
    /// </summary>
    /// <returns></returns>
    private static int GetKillOrAddLifeValue(Player player, int countValue)
    {
        var currValue = player.statLife;
        var maxValue = player.statLifeMax2;
        return GetKillOrAddLifeValue(maxValue, currValue, countValue);
    }

    private static int GetKillOrAddLifeValue(NPC npc, int countValue)
    {
        var currValue = npc.life;
        var maxValue = npc.lifeMax;
        return GetKillOrAddLifeValue(maxValue, currValue, countValue);
    }

    /// <summary>
    /// 返回负数就扣，返回正数就回，返回0不操作
    /// </summary>
    /// <returns></returns>
    private static int GetKillOrAddLifeValue(int maxLife, int currLife, int countValue)
    {
        int firstHeal = maxLife - currLife;
        if (countValue <= firstHeal) {
            return firstHeal;
        }

        int remaining = countValue - firstHeal;
        bool isDeductPhase = true;
        int finalEffect = 0;

        while (remaining > 0) {
            if (isDeductPhase) {
                int deduct = Math.Min(maxLife, remaining);
                finalEffect = -deduct;
                remaining -= deduct;
            } else {
                int add = Math.Min(maxLife, remaining);
                finalEffect = add;
                remaining -= add;
            }
            isDeductPhase = !isDeductPhase;
        }

        return finalEffect;
    }

    public override bool PreKill(int timeLeft)
    {
        damageEntity.Clear();
        return base.PreKill(timeLeft);
    }
}

public enum Status
{
    Golex,
    Life
}