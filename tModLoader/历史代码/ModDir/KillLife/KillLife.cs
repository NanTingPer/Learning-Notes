using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using Terraria;
using Terraria.ID;
using Terraria.ModLoader;

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
///     2. 物品使用时的绘制问题
///     3. 弹幕蓄力才能治疗 最低1s
///     4. 弹幕蓄力时的行为(在剑的上面)
///     5. 弹幕蓄力完成后的行为
/// 计划 :
///     需求更改:
///     1. 完成后朝鼠标方向运动
///     2. 公式 
///         900治疗量
///                玩家200生命上限，当前50生命
///                +150-200+200-200+150→150生命
///     
///     
/// </summary>
// Please read https://github.com/tModLoader/tModLoader/wiki/Basic-tModLoader-Modding-Guide#mod-skeleton-contents for more information about the various files in a mod.
public class KillLife : Mod
{
    public static Mod Mod => ModLoader.GetMod(nameof(KillLife));

    /// <summary>
    /// key是 (<see cref="NetmodeID"/>, <see cref="NetType"/>)
    /// </summary>
    public readonly static Dictionary<(int, NetType), Action<BinaryReader, int>> PacketHandles;

    static KillLife()
    {
        PacketHandles = [];
        PacketHandles.TryAdd((NetmodeID.Server, NetType.LifeProjectilePosition), ServerLifeProjectilePosition);
        PacketHandles.TryAdd((NetmodeID.Server, NetType.LifeProjectileNetField), ServerLifeProjectileNetField);
        PacketHandles.TryAdd((NetmodeID.Server, NetType.LifeProjectileGloexScale), ServerLifeProjectileGloexScale);

        PacketHandles.TryAdd((NetmodeID.MultiplayerClient, NetType.LifeProjectilePosition), ClientLifeProjectilePosition);
        PacketHandles.TryAdd((NetmodeID.MultiplayerClient, NetType.LifeProjectileNetField), ClientLifeProjectileNetField);
        PacketHandles.TryAdd((NetmodeID.MultiplayerClient, NetType.LifeProjectileGloexScale), ClientLifeProjectileGloexScale);
    }

    #region NetAsycn
    /// <summary>
    /// <para> 由于 <see cref="Main.projectile"/> 不保证弹幕顺序 </para> 
    /// <para> 此字段用于 <see cref="LifeProjectile"/> 唯一标识符 </para>
    /// <para> 事已至此 先睡觉吧 <see cref="Projectile.identity"/> <see cref="Main.projectileIdentity"/> </para> 
    /// </summary>
    public static int lifeProjectileNetField = 0;
    public override void HandlePacket(BinaryReader reader, int whoAmI)
    {
        NetType type = (NetType)reader.ReadInt32();

        PacketHandles[(Main.netMode, type)](reader, whoAmI);
        base.HandlePacket(reader, whoAmI);
    }

    #region Server Hander
    /// <summary>
    /// 同步网络字段
    /// <para> 包类型 <see cref="NetType"/> </para> 
    /// <para> 包值 <see cref="lifeProjectileNetField"/>  <see cref="System.Int32"/> </para>
    /// </summary>
    private static void ServerLifeProjectileNetField(BinaryReader _x, int whoAmI)
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
    /// <see cref="ClientLifeProjectileGloexScale(BinaryReader, int)"/>
    /// <see cref="LifeProjectile.SendGloexScale(LifeProjectile)"/>
    /// </summary>
    /// <param name="reader"></param>
    /// <param name="whoAmI"></param>
    /// <exception cref="System.Exception"></exception>
    private static void ServerLifeProjectileGloexScale(BinaryReader reader, int whoAmI)
    {
        if (Main.netMode != NetmodeID.Server) {
            using var sr = new StreamReader(reader.BaseStream);
            sr.ReadToEnd();
            throw new System.Exception($"{nameof(ServerLifeProjectileGloexScale)}不应该在非服务端调用！");
        }

        var gloexScale = reader.ReadSingle();
        var netField = reader.ReadInt32();
        var projs = GetLifeProjectiles(netField);
        foreach(var p in projs) {
            p.gloexScale = gloexScale;
        }
        var modPacket = Mod.GetPacket();
        var tp = projs.FirstOrDefault();
        if (tp == null)
            return;
        LifeProjectile.WriteGloexScale(modPacket, tp);
        modPacket.Send(ignoreClient: whoAmI);
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

        var tarProjectile = GetLifeProjectiles(netfieldValue);
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
    private static void ClientLifeProjectileNetField(BinaryReader reader, int _x)
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
    private static void ClientLifeProjectilePosition(BinaryReader reader, int _x)
    {
        _ = nameof(ServerLifeProjectilePosition);
        var pos = reader.ReadVector2();
        var netField = reader.ReadInt32();

        var projs = GetLifeProjectiles(netField);
        foreach (var proj in projs) {
            proj.Position = pos;
        }
    }

    /// <summary>
    /// <see cref="LifeProjectile.SendGloexScale(LifeProjectile)"/>
    /// </summary>
    /// <param name="reader"></param>
    /// <param name="_x"></param>
    private static void ClientLifeProjectileGloexScale(BinaryReader reader, int _x)
    {
        var gloexScale = reader.ReadSingle();
        var netField = reader.ReadInt32();

        var projs = GetLifeProjectiles(netField);
        foreach (var lifeProjectile in projs) {
            lifeProjectile.gloexScale = gloexScale;
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

    public static IEnumerable<LifeProjectile> GetLifeProjectiles(int netField)
    {
        return 
            from p in Main.projectile
            where p.active == true && p.ModProjectile != null && p.ModProjectile is LifeProjectile
            select p.ModProjectile as LifeProjectile
            into p1
            where p1.netField == netField
            select p1;
    }
}