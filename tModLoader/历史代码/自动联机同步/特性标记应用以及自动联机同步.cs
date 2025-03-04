using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Threading.Tasks;
using Terraria;
using Terraria.ID;
using Terraria.ModLoader;
using Terraria.ModLoader.Core;

namespace Attri
{
	// Please read https://github.com/tModLoader/tModLoader/wiki/Basic-tModLoader-Modding-Guide#mod-skeleton-contents for more information about the various files in a mod.
	public class Attri : Mod
	{
        /// <summary>
        /// 存储本模组的弹幕并给定一个编号
        /// </summary>
        public static List<Tuple<int, Type>> ModProjectleAll = [];

        /// <summary>
        /// 存储本模组的弹幕以及对应需要同步的字段
        /// </summary>
        public static Dictionary<Type, List<FieldInfo>> ProjeAllField = [];

        /// <summary>
        /// 在Load方法中，提取全部本模组的需要自动同步的字段
        /// </summary>
        public override void Load()
        {
            //使用AssemblyManager.GetLoadableTypes获取我们程序集的全部类型，可以防止在弱引用时触发找不到程序集
            var AllType = AssemblyManager.GetLoadableTypes(typeof(Attri).Assembly);
            //计算当前是第几个弹幕，用作ModProjectleAll的编号
            int r = 0;
            //遍历并载入ModProjectleAll
            foreach (var item in AllType.Where(f => f.IsSubclassOf(typeof(ModProjectile))))
            {
                ModProjectleAll.Add(new Tuple<int, Type>(r, item));
                r++;
            };

            //遍历我们的模组弹幕，并判断字段是否为需要自动同步的字段
            //如果要 那么需要被TestAttribute特性标记
            ModProjectleAll.ForEach(tuple =>
            {
                var type = tuple.Item2;
                List<FieldInfo> type_fields = [];
                var fields = type
                    .GetFields(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic)
                    .Where(field => field.GetCustomAttribute<TestAttribute>() != null && field.FieldType == typeof(int));
                if (fields.Count() >= 1)
                    ProjeAllField.Add(type, fields.ToList());
            });

            base.Load();
        }

        /// <summary>
        /// 我们在HandlePacket方法中处理我们的包
        /// <para> 如果服务器不需要同步，仅做包转发可以修改代码</para>
        /// <para> 本发包未处理欠读 / 过读 以及错误处理 </para>
        /// </summary>
        public override void HandlePacket(BinaryReader reader, int whoAmI)
        {
            int index = reader.ReadInt32(); //这个是我们ModProjectleAll的索引
            int projectileIndex = reader.ReadInt32();   //这个是该弹幕实例在Main.projectile中的索引
            Tuple<int, Type> item = ModProjectleAll[index];
            ProjeAllField.TryGetValue(item.Item2, out var fieldinfos);
            ModProjectile projectile = Main.projectile[projectileIndex].ModProjectile;

            //服务器负责转发包 / 服务器同步
            if (Main.netMode == NetmodeID.Server)
            {
                List<int> AllBytes = [];
                for (int i = 0; i < fieldinfos.Count; i++)
                {
                    AllBytes.Add(reader.ReadInt32());
                    fieldinfos[i].SetValue(projectile, AllBytes[i]);
                }

                //下发
                var pt = GetPacket();
                pt.Write(index);
                pt.Write(projectileIndex);
                AllBytes.ForEach(pt.Write);
                pt.Send(-1, whoAmI);
            }


            //客户端进行包接收并应用同步
            if (Main.netMode == NetmodeID.MultiplayerClient)
            {
                fieldinfos.ForEach(f => f.SetValue(projectile, reader.ReadInt32()));
            }

            base.HandlePacket(reader, whoAmI);
        }

        public static void WritePacket(ModPacket modpacket, List<int> values)
        {
            values.ForEach(modpacket.Write);
        }
    }

	public class MyModSystem : ModSystem
	{
        /// <summary>
        /// 使用这个方法进行同步，是因为这个方法每游戏刻运行一次 / 每秒运行60次
        /// </summary>
        public override void UpdateUI(GameTime gameTime)
        {
            //单人模式 / 服务器不进行发包同步
            //这里的发包模型是 :  拥有弹幕的客户端 -> 服务器 -> 其他客户端
            if (Main.netMode == NetmodeID.Server || Main.netMode == NetmodeID.SinglePlayer)
                return;

            //为了避免出现遍历弹幕的单核性能损耗，开启一个线程，但是严格来说要上锁
            Task.Run(() =>
            {
                //遍历世界中存在的弹幕
                foreach (var item in Main.projectile)
                {
                    var proj = item.ModProjectile;  //获取ModProjectile 因为自己的字段都是在这里面的
                    if (proj == null || proj.Mod.Name != "Attri") continue; //是否为本模组的弹幕
                    if (item.owner != Main.myPlayer) continue;  //弹幕主人是否为运行的端

                    var type = proj.GetType();  //获取类型，通过类型去字典里面获取字段
                    if (Attri.ProjeAllField.TryGetValue(type, out var fields))
                    {
                        //查找ModProjectleAll中该弹幕的索引 其他端可以用该索引定位弹幕类型
                        var index = Attri.ModProjectleAll.FindIndex(f => f.Item2 == type);
                        var pt = Mod.GetPacket();   //获取发包器
                        pt.Write(index);    //写入索引
                        pt.Write(item.whoAmI);  //写入弹幕索引
                        fields.ForEach(f => //遍历获取字段值
                        {
                            pt.Write((int)f.GetValue(proj));
                        });
                        pt.Send(-1, Main.myPlayer); //发送
                    }

                    #region 抛弃 2025/02/08 11:30
                    /*                    List<int> ints = [];
                                        foreach (var item1 in proj.GetType().GetFields())
                                        {
                                            var att = item1.GetCustomAttribute<TestAttribute>();
                                            if (att == null) continue;
                                            var 值 = item1.GetValue(proj) as int?;
                                            if (值 != null) ints.Add(值.Value);
                                        }
                                        if (ints.Count >= 1)
                                        {
                                            var packet = Mod.GetPacket();
                                            packet.Write(ints.Count);
                                            packet.Write(item.whoAmI);
                                            ints.ForEach(packet.Write);
                                            packet.Send(-1, Main.myPlayer);
                                        }*/
                    #endregion 抛弃
                }
            });

            base.UpdateUI(gameTime);
        }
    }

    [AttributeUsage(AttributeTargets.Field)]
    public class TestAttribute : Attribute
    {

    }
    public class TestProjectile : ModProjectile
    {
        [Test]
        public int 目标X = 0; //自动同步

        [Test]
        public int 目标Y = 0; //自动同步
        public override void SetDefaults()
        {
            Projectile.width = 8;
            Projectile.height = 8;
            Projectile.friendly = true;
            Projectile.DamageType = DamageClass.Summon;
            Projectile.aiStyle = -1;
            Projectile.penetrate = -1;
            Projectile.ignoreWater = true;
            Projectile.tileCollide = true;
            Projectile.scale = 1f;
            Projectile.timeLeft = 60;

            base.SetDefaults();
        }

        public override void AI()
        {
            if (Projectile.owner == Main.myPlayer)
            {
                目标X = (int)Main.MouseWorld.X;
                目标Y = (int)Main.MouseWorld.X;
            }
            base.AI();
        }
    }
    
}
