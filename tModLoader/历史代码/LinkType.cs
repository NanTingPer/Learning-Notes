using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using ReLogic.Content;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using Terraria;
using Terraria.Localization;
using Terraria.ModLoader;

namespace AddBossListItem;

public class BossListUtils
{
    protected readonly static MethodInfo ItemTypeMethod;
    protected readonly static MethodInfo NPCTypeMethod;
    static BossListUtils()
    {
        Type modContentType = typeof(ModContent);
        ItemTypeMethod = modContentType.GetMethod("ItemType", BindingFlags.Static | BindingFlags.Public);
        NPCTypeMethod = modContentType.GetMethod("NPCType", BindingFlags.Static | BindingFlags.Public);
    }

    public static ILogType Build()
    {
        return new LogType();
    }

    /// <summary>
    /// 是否可见
    /// </summary>
    protected const string Availability = "availability";

    /// <summary>
    /// 收藏品列表
    /// </summary>
    protected const string Collectibles = "collectibles";

    /// <summary>
    /// 生成物品
    /// </summary>
    protected const string SpawnItems = "spawnItems";

    /// <summary>
    /// 生成信息
    /// </summary>
    protected const string SpawnInfo = "spawnInfo";

    /// <summary>
    /// 显示名称
    /// </summary>
    protected const string DisplayName = "displayName";

    /// <summary>
    /// 覆盖头材质
    /// </summary>
    protected const string OverrideHeadTextures = "overrideHeadTextures";

    /// <summary>
    /// 肢体击败通知
    /// </summary>
    protected const string Limbs = "limbs";

    /// <summary>
    /// 脱战消息
    /// </summary>
    protected const string DespawnMessage = "DespawnMessage";

    /// <summary>
    /// 自定义绘制
    /// </summary>
    protected const string CustomPortrait = "customPortrait";

    protected const string Base_1_Type = "type";
    protected const string Base_2_Mod = "mod";
    protected const string Base_3_InternalName = "internalName";
    protected const string Base_4_Weight = "weight";
    protected const string Base_5_Downed = "downed";
    protected const string Base_6_BossType = "bossType";

    protected Dictionary<string, object> BossListConfig = [];
    protected Dictionary<string, object> ExtraConfig = [];

    protected class LogType : BossListUtils, ILogType
    {
        /// <summary>
        /// 设置日志类型
        /// </summary>
        public IModType SetLogType(BossLogType type)
        {
            BossListConfig[Base_1_Type] = type.ToString();
            return new ModType() { BossListConfig = BossListConfig, ExtraConfig = ExtraConfig };
        }
    }

    protected class ModType : BossListUtils, IModType
    {
        public IInternalName SetModType(Mod mod)
        {
            BossListConfig[Base_2_Mod] = mod;
            return new InternalName() { BossListConfig = BossListConfig, ExtraConfig = ExtraConfig };
        }
    }

    protected class InternalName : BossListUtils, IInternalName
    {
        public IWeight SetInternalName(string internalName)
        {
            BossListConfig[Base_3_InternalName] = internalName;
            return new Weight() { BossListConfig = BossListConfig, ExtraConfig = ExtraConfig };
        }
    }

    protected class Weight : BossListUtils, IWeight
    {
        public IDowned SetWeight(float weight)
        {
            BossListConfig[Base_4_Weight] = weight;
            return new Downed() { BossListConfig = BossListConfig, ExtraConfig = ExtraConfig };
        }
    }

    protected class Downed : BossListUtils, IDowned
    {
        public IBossID SetDowned(Func<bool> downedFunc)
        {
            BossListConfig[Base_5_Downed] = downedFunc;
            return new BossID() { BossListConfig = BossListConfig, ExtraConfig = ExtraConfig };
        }
    }

    protected class BossID : BossListUtils, IBossID
    {
        public IExtra SetBossID(params Type[] ids)
        {
            List<int> ints = [];

            foreach (var item in ids) {
                ints.Add((int)NPCTypeMethod.MakeGenericMethod(item).Invoke(null, null));
            }

            BossListConfig[Base_6_BossType] = ints;
            return new Extra() { BossListConfig = BossListConfig, ExtraConfig = ExtraConfig };
        }

        IExtra IBossID.SetBossID(params int[] ids)
        {
            BossListConfig[Base_6_BossType] = ids.ToList();
            return new Extra() { BossListConfig = BossListConfig, ExtraConfig = ExtraConfig };
        }

        IExtra IBossID.SetBossID<T1>()
        {
            List<int> ints = [ModContent.NPCType<T1>()];
            BossListConfig[Base_6_BossType] = ints;
            return new Extra() { BossListConfig = BossListConfig, ExtraConfig = ExtraConfig };
        }

        IExtra IBossID.SetBossID<T1, T2>()
        {
            List<int> ints = [
                ModContent.NPCType<T1>(),
                ModContent.NPCType<T2>(),
            ];

            BossListConfig[Base_6_BossType] = ints;
            return new Extra() { BossListConfig = BossListConfig, ExtraConfig = ExtraConfig };
        }

        IExtra IBossID.SetBossID<T1, T2, T3>()
        {
            List<int> ints = [
                ModContent.NPCType<T1>(),
                ModContent.NPCType<T2>(),
                ModContent.NPCType<T3>(),
            ];

            BossListConfig[Base_6_BossType] = ints;
            return new Extra() { BossListConfig = BossListConfig, ExtraConfig = ExtraConfig };
        }

        IExtra IBossID.SetBossID<T1, T2, T3, T4>()
        {
            List<int> ints = [
                ModContent.NPCType<T1>(),
                ModContent.NPCType<T2>(),
                ModContent.NPCType<T3>(),
                ModContent.NPCType<T4>(),
            ];

            BossListConfig[Base_6_BossType] = ints;
            return new Extra() { BossListConfig = BossListConfig, ExtraConfig = ExtraConfig };
        }

        IExtra IBossID.SetBossID<T1, T2, T3, T4, T5>()
        {
            List<int> ints = [
                ModContent.NPCType<T1>(),
                ModContent.NPCType<T2>(),
                ModContent.NPCType<T3>(),
                ModContent.NPCType<T4>(),
                ModContent.NPCType<T5>(),
            ];

            BossListConfig[Base_6_BossType] = ints;
            return new Extra() { BossListConfig = BossListConfig, ExtraConfig = ExtraConfig };
        }
    }

    protected class Extra : BossListUtils, IExtra
    {
        public IExtra SetAvailability(Func<bool> availability)
        {
            ExtraConfig[Availability] = availability;
            return this;
        }

        public IExtra SetCollectibles(params int[] collectibles)
        {
            ExtraConfig[Collectibles] = collectibles.ToList();
            return this;
        }

        public IExtra SetCollectibles<T1>() where T1 : ModItem
        {
            List<int> ints = [ModContent.ItemType<T1>()];
            ExtraConfig[Collectibles] = ints;
            return this;
        }

        public IExtra SetCollectibles<T1, T2>()
            where T1 : ModItem
            where T2 : ModItem
        {
            List<int> ints = [
                ModContent.ItemType<T1>(),
                ModContent.ItemType<T2>(),
            ];

            ExtraConfig[Collectibles] = ints;
            return this;
        }

        public IExtra SetCollectibles<T1, T2, T3>()
            where T1 : ModItem
            where T2 : ModItem
            where T3 : ModItem
        {
            List<int> ints = [
                ModContent.ItemType<T1>(),
                ModContent.ItemType<T2>(),
                ModContent.ItemType<T3>(),
            ];

            ExtraConfig[Collectibles] = ints;
            return this;
        }

        public IExtra SetCollectibles(params Type[] collectibles)
        {
            List<int> ints = [];
            foreach (var item in collectibles) {
                ints.Add((int)ItemTypeMethod.MakeGenericMethod(item).Invoke(null, null));
            }

            ExtraConfig[Collectibles] = ints;
            return this;
        }

        public IExtra SetCustomPortrait(Action<SpriteBatch, Rectangle, Color> customPortrait)
        {
            ExtraConfig[CustomPortrait] = customPortrait;
            return this;
        }

        public IExtra SetDespawnMessage(Func<NPC, LocalizedText> despawnMessage)
        {
            ExtraConfig[DespawnMessage] = despawnMessage;
            return this;
        }

        public IExtra SetDespawnMessage(LocalizedText despawnMessage)
        {
            ExtraConfig[DespawnMessage] = despawnMessage;
            return this;
        }

        public IExtra SetDisplayName(string displayName)
        {
            ExtraConfig[DisplayName] = displayName;
            return this;
        }

        public IExtra SetLimbs(Dictionary<int, LocalizedText> limbs)
        {
            ExtraConfig[Limbs] = limbs;
            return this;
        }

        public IExtra SetOverrideHeadTextures(string overrideHeadTextures)
        {
            ExtraConfig[OverrideHeadTextures] = overrideHeadTextures;
            return this;
        }

        public IExtra SetOverrideHeadTextures(params string[] overrideHeadTextures)
        {
            ExtraConfig[OverrideHeadTextures] = overrideHeadTextures.ToList();
            return this;
        }

        public IExtra SetOverrideHeadTextures(Func<List<Asset<Texture2D>>> overrideHeadTextures)
        {
            ExtraConfig[OverrideHeadTextures] = overrideHeadTextures;
            return this;
        }

        public IExtra SetSpawnInfo(LocalizedText spawnInfo)
        {
            ExtraConfig[SpawnInfo] = spawnInfo;
            return this;
        }

        public IExtra SetSpawnInfo(Func<LocalizedText> spawnInfo)
        {
            ExtraConfig[SpawnInfo] = spawnInfo;
            return this;
        }

        public IExtra SetSpawnItems(params int[] spawnItems)
        {
            ExtraConfig[SpawnItems] = spawnItems.ToList();
            return this;
        }

        public IExtra SetSpawnItems<T1>() where T1 : ModItem
        {
            List<int> ints = [
                ModContent.ItemType<T1>()
            ];

            ExtraConfig[SpawnItems] = ints;
            return this;
        }

        public IExtra SetSpawnItems<T1, T2>()
            where T1 : ModItem
            where T2 : ModItem
        {
            List<int> ints = [
                ModContent.ItemType<T1>(),
                ModContent.ItemType<T2>(),
            ];

            ExtraConfig[SpawnItems] = ints;
            return this;
        }

        public IExtra SetSpawnItems<T1, T2, T3>()
            where T1 : ModItem
            where T2 : ModItem
            where T3 : ModItem
        {
            List<int> ints = [
                ModContent.ItemType<T1>(),
                ModContent.ItemType<T2>(),
                ModContent.ItemType<T3>(),
            ];

            ExtraConfig[SpawnItems] = ints;
            return this;
        }

        public IExtra SetSpawnItems(params Type[] spawnItems)
        {
            List<int> ints = [];
            foreach (var item in spawnItems) {
                ints.Add((int)ItemTypeMethod.MakeGenericMethod(item).Invoke(null, null));
            }

            ExtraConfig[SpawnItems] = ints;

            return this;
        }

        public void Call()
        {
            if (!ModLoader.TryGetMod("BossChecklist", out Mod bossChecklistMod)) {
                return;
            }
            bossChecklistMod.Call(
                BossListConfig[Base_1_Type],
                BossListConfig[Base_2_Mod],
                BossListConfig[Base_3_InternalName],
                BossListConfig[Base_4_Weight],
                BossListConfig[Base_5_Downed],
                BossListConfig[Base_6_BossType],
                ExtraConfig
            );
        }
    }
}

public interface ILogType
{
    IModType SetLogType(BossLogType type);
}

public interface IModType
{
    /// <summary>
    /// 设置所属模组
    /// </summary>
    IInternalName SetModType(Mod mod);
}

public interface IInternalName
{
    /// <summary>
    /// 设置内部名称
    /// </summary>
    IWeight SetInternalName(string internalName);
}

public interface IWeight
{
    /// <summary>
    /// 设置进度
    /// </summary>
    IDowned SetWeight(float weight);
}

public interface IDowned
{
    /// <summary>
    /// 此委托用于判断此日志是否应该显示为击败
    /// <para> 会在多处判断 </para>
    /// </summary>
    IBossID SetDowned(Func<bool> downedFunc);
}

public interface IBossID
{
    /// <summary>
    ///  使用 <see cref="ModContent.NPCType"/>
    /// </summary>
    IExtra SetBossID(params int[] ids);

    IExtra SetBossID<T1>() 
        where T1 : ModNPC;

    IExtra SetBossID<T1, T2>()
        where T1 : ModNPC
        where T2 : ModNPC;

    IExtra SetBossID<T1, T2, T3>()
        where T1 : ModNPC
        where T2 : ModNPC
        where T3 : ModNPC;

    IExtra SetBossID<T1, T2, T3, T4>()
        where T1 : ModNPC
        where T2 : ModNPC
        where T3 : ModNPC
        where T4 : ModNPC;

    IExtra SetBossID<T1, T2, T3, T4, T5>()
        where T1 : ModNPC
        where T2 : ModNPC
        where T3 : ModNPC
        where T4 : ModNPC
        where T5 : ModNPC;

    IExtra SetBossID(params Type[] ids);
}

public interface IExtra
{
    /// <summary>
    /// 设置Boss昵称 这通常会自动获取
    /// </summary>
    IExtra SetDisplayName(string displayName);

    /// <summary>
    /// 设置生成信息
    /// </summary>
    IExtra SetSpawnInfo(LocalizedText spawnInfo);

    /// <summary>
    /// 设置生成信息，如果在不同条件下显示不同内容，使用这个重载
    /// </summary>
    IExtra SetSpawnInfo(Func<LocalizedText> spawnInfo);

    /// <summary>
    /// 设置生成物品，如果没有可以不设置
    /// </summary>
    IExtra SetSpawnItems(params int[] spawnItems);

    /// <summary>
    /// 设置生成物品，如果没有可以不设置
    /// </summary>
    IExtra SetSpawnItems<T1>()
        where T1 : ModItem;

    /// <summary>
    /// 设置生成物品，如果没有可以不设置
    /// </summary>
    IExtra SetSpawnItems<T1, T2>()
        where T1 : ModItem
        where T2 : ModItem;

    /// <summary>
    /// 设置生成物品，如果没有可以不设置
    /// </summary>
    IExtra SetSpawnItems<T1, T2, T3>()
        where T1 : ModItem
        where T2 : ModItem
        where T3 : ModItem;

    /// <summary>
    /// 设置生成物品，如果没有可以不设置
    /// </summary>
    IExtra SetSpawnItems(params Type[] spawnItems);

    /// <summary>
    /// 设置可收藏物品
    /// <para>圣物、奖杯、面具、音乐盒、宠物物品</para>
    /// </summary>
    IExtra SetCollectibles(params int[] collectibles);

    /// <summary>
    /// 设置可收藏物品
    /// <para>圣物、奖杯、面具、音乐盒、宠物物品</para>
    /// </summary>
    IExtra SetCollectibles<T1>()
        where T1 : ModItem;

    /// <summary>
    /// 设置可收藏物品
    /// <para>圣物、奖杯、面具、音乐盒、宠物物品</para>
    /// </summary>
    IExtra SetCollectibles<T1, T2>()
        where T1 : ModItem
        where T2 : ModItem;

    /// <summary>
    /// 设置可收藏物品
    /// <para>圣物、奖杯、面具、音乐盒、宠物物品</para>
    /// </summary>
    IExtra SetCollectibles<T1, T2, T3>()
        where T1 : ModItem
        where T2 : ModItem
        where T3 : ModItem;

    /// <summary>
    /// 设置可收藏物品
    /// <para>圣物、奖杯、面具、音乐盒、宠物物品</para>
    /// </summary>
    IExtra SetCollectibles(params Type[] collectibles);

    /// <summary>
    /// 设置可见性，例如克脑只有在猩红地图可见
    /// </summary>
    IExtra SetAvailability(Func<bool> availability);

    /// <summary>
    /// 覆盖自动生成的 / 自动获取的 头部纹理
    /// </summary>
    /// <param name="overrideHeadTextures">纹理路径</param>
    /// <returns></returns>
    IExtra SetOverrideHeadTextures(string overrideHeadTextures);

    /// <summary>
    /// 覆盖自动生成的 / 自动获取的 头部纹理
    /// </summary>
    /// <param name="overrideHeadTextures">纹理路径</param>
    /// <returns></returns>
    IExtra SetOverrideHeadTextures(params string[] overrideHeadTextures);/// <summary>

    /// 覆盖自动生成的 / 自动获取的 头部纹理
    /// </summary>
    /// <param name="overrideHeadTextures">纹理路径</param>
    /// <returns></returns>
    IExtra SetOverrideHeadTextures(Func<List<Asset<Texture2D>>> overrideHeadTextures);

    /// <summary>
    /// 设置肢体击败通知，例如"骷髅王之手被击败了"，需要确保此NPC的BossID为复数
    /// </summary>
    /// <param name="limbs"></param>
    /// <returns></returns>
    IExtra SetLimbs(Dictionary<int, LocalizedText> limbs);

    /// <summary>
    /// 设置NPC在脱离战斗时的消息
    /// </summary>
    IExtra SetDespawnMessage(Func<NPC, LocalizedText> despawnMessage);

    /// <summary>
    /// 设置NPC在脱离战斗时的消息
    /// </summary>
    IExtra SetDespawnMessage(LocalizedText despawnMessage);

    /// <summary>
    /// 设置NPC大屏为自定义绘制
    /// </summary>
    /// <param name="customPortrait"></param>
    /// <returns></returns>
    IExtra SetCustomPortrait(Action<SpriteBatch, Rectangle, Color> customPortrait);
    void Call();
}

public enum BossLogType
{
    /// <summary>
    /// Boss
    /// </summary>
    LogBoss,

    /// <summary>
    /// MiniBoss
    /// </summary>
    LogMiniBoss,

    /// <summary>
    /// 事件
    /// </summary>
    LogEvent
}
