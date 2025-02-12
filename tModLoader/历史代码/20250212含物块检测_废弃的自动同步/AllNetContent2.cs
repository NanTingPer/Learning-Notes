using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Terraria.ID;
using Terraria;
using System.IO;
using Terraria.ModLoader;
using System.Reflection;
using Microsoft.VisualBasic.FileIO;
using System.Threading;
using Newtonsoft.Json.Linq;
using static GensokyoWPNACC.PacketMode.NetMethod.NetMethods;

namespace GensokyoWPNACC.PacketMode
{
    public partial class AllNetContent//发包 与 包处理办法
    {
        private Thread ProjectileNetPacketThread;
        private CancellationTokenSource cts;
        public static bool OFF = false;

        /*
        public override void UpdateUI(GameTime gameTime)
        {
            if (Main.netMode == NetmodeID.Server || Main.netMode == NetmodeID.SinglePlayer)
                return;
            //if (OFF == true)
            //    return;

            RemoveNoActiveProjectile();

            foreach (var proj in ActiveProjectiles)
            {
                var projType = proj.GetType();
                var projectile = proj.Projectile;
                if (projectile.owner == Main.myPlayer)
                {
                    bool f = false;
                    bool p = false;
                    List<FieldInfo> fields;
                    List<PropertyInfo> properties;
                    if (ModProjectleFields.TryGetValue(projType, out fields))
                        f = true;

                    if (ModProjectilePropertys.TryGetValue(projType, out properties))
                        p = true;

                    if (f || p) {
                    } else { 
                        continue;
                    }

                    ModPacket pt = Mod.GetPacket();
                    pt.Write((int)NetPacketType.弹幕);
                    pt.Write(projectile.whoAmI);
                    if (f)
                    {
                        foreach (var field in fields)
                        {
                            Type fieldType = field.FieldType;
                            object value = field.GetValue(proj);
                            WritePacket(pt, value, fieldType.Name);
                        }
                    }

                    if (p)
                    {
                        p = true;
                        foreach (var propertie in properties)
                        {
                            Type propertieType = propertie.PropertyType;
                            object value = propertie.GetValue(proj);
                            WritePacket(pt, value, propertieType.Name);
                        }
                    }
                    pt.Send(-1, Main.myPlayer);
                }
            }

           


            #region delete
            //Task.Run(() =>
            //{
            //while (true)
            //{
            //    //if (cts.IsCancellationRequested)
            //    //    break;
            //    await Task.Delay(500);
            //}
            //});

            //OFF = true;
            //cts = new CancellationTokenSource();
            //ProjectileNetPacketThread = new Thread(() =>
            //{

            //});
            #endregion delete
            base.UpdateUI(gameTime);
        }
         */

        public override void OnWorldUnload()
        {
            cts?.Cancel();
            base.OnWorldUnload();
        }


        public static void OHandlePacket(ModPacket pt,BinaryReader reader, int whoAmI)
        {
            NetPacketType npt = (NetPacketType)reader.ReadInt32();
            if (Main.netMode == NetmodeID.Server)
            {
                if(npt == NetPacketType.弹幕)
                {
                    ProjectilePacketWrite(pt, reader, whoAmI);
                }
            }

            if(Main.netMode == NetmodeID.MultiplayerClient)
            {
                if(npt == NetPacketType.弹幕)
                {
                    ProjectilePacketReader(reader);
                }
            }
        }


        /// <summary>
        /// 处理基础弹幕包(客户端同步)
        /// </summary>
        public static void ProjectilePacketReader(BinaryReader reader)
        {
            int index = reader.ReadInt32();
            Projectile proje = Main.projectile[index];
            var fieldinfos = GetFieldInfos(proje, out var modProj, out var projType);
            var properts = GetPropertyInfos(proje, out modProj, out projType);

            foreach (var item in Reader(reader, modProj, fieldinfos)) ;
            foreach (var item1 in Reader(reader, modProj, properts));
        }

        /// <summary>
        /// 处理基础弹幕包(服务端同步并转发)
        /// </summary>
        public static void ProjectilePacketWrite(ModPacket pt, BinaryReader reader, int whoAmI)
        {
            int index = reader.ReadInt32();
            pt.Write((int)NetPacketType.弹幕);
            pt.Write(index);
            Projectile proje = Main.projectile[index];
            var fields = GetFieldInfos(proje, out ModProjectile modProje, out Type type);
            var properts = GetPropertyInfos(proje, out modProje, out type);

            foreach (object item in Reader(reader, modProje, fields))
            {
                WritePacket(pt, item);
            }

            foreach (var item in Reader(reader, modProje, properts))
            {
                WritePacket(pt, item);
            }

            pt.Send(-1, whoAmI);
        }
    }
}
