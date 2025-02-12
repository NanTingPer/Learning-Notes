using GensokyoWPNACC.PacketMode.Attributes;
using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using Terraria;
using Terraria.DataStructures;
using Terraria.ModLoader;
using Terraria.ModLoader.Core;
using static GensokyoWPNACC.PacketMode.NetMethod.NetMethods;

namespace GensokyoWPNACC.PacketMode
{
    [Obsolete("应当使用新的NetType")]
    public partial class AllNetContent : ModSystem
    {
        public delegate void HookProjectileOnSpawn(ModProjectile obj, IEntitySource source);
        public static void HookProjectileOnSpawnMethod(HookProjectileOnSpawn orig, ModProjectile projectile, IEntitySource source)
        {
            ActiveProjectiles.Add(projectile);
            orig.Invoke(projectile, source);
        }

        /// <summary>
        /// 使用类型名称从EnumValueMap拿取方法，存放Reader的方法
        /// </summary>
        public static Dictionary<string, MethodInfo> ReaderMethodInfo = [];

        /// <summary>
        /// 使用参数类型名获取相应的包写入方法
        /// </summary>
        public static Dictionary<string, MethodInfo> ModPacketMethodInfo = [];

        /// <summary>
        /// 当前活跃的弹幕
        /// </summary>

        public static List<ModProjectile> ActiveProjectiles = new List<ModProjectile>();

        /// <summary>
        /// 类型对应的读取方法
        /// </summary>
        public static Dictionary<Type, string> EnumValueMap = new Dictionary<Type, string>()
        {
            {typeof(uint),      "ReadInt16"},
            {typeof(int),       "ReadInt32"},
            {typeof(long),      "ReadInt64"},
            {typeof(ushort),    "ReadUInt16"},
            {typeof(ulong),     "ReadUInt64"},
            {typeof(double),    "ReadDouble"},
            {typeof(float),     "ReadSingle"},
            {typeof(bool),      "ReadBoolean"},
            {typeof(Vector2),   "ReadVector2"},
        };

        /// <summary>
        /// 本模组的全部弹幕类型
        /// </summary>
        public static List<Tuple<int, Type>> ModProjectleTypes = [];

        /// <summary>
        /// 本模组弹幕的全部同步字段
        /// </summary>
        public static Dictionary<Type, List<FieldInfo>> ModProjectleFields = [];

        /// <summary>
        /// 本模组弹幕的全部同步属性
        /// </summary>

        public static Dictionary<Type, List<PropertyInfo>> ModProjectilePropertys = [];

        public override void Load()
        {

            #region 包写入方法与流读取方法
            foreach (var item in typeof(BinaryReader).GetMethods(CanReflectionWell()).Where(method => (method.Name == "ReadInt16" || method.Name == "ReadInt32" || method.Name == "ReadInt64" || method.Name == "ReadUInt16" || method.Name == "ReadUInt32" || method.Name == "ReadUInt64" || method.Name == "ReadDouble" || method.Name == "ReadSingle" || method.Name == "ReadBoolean") && method.GetParameters().Length == 0))
            {
                ReaderMethodInfo.Add(item.Name, item);
            }
            ReaderMethodInfo.Add("ReadVector2", typeof(Utils).GetMethod("ReadVector2", BindingFlags.Static | BindingFlags.Public | BindingFlags.NonPublic));

            foreach (var item in typeof(ModPacket).GetMethods(CanReflectionWell()).Where(f => (f.Name == "Write" || f.Name == "WriteVector2") && f.GetParameters().Length == 1 && !f.GetParameters()[0].ParameterType.Name.Contains("ReadOnlySpan")))
            {
                ModPacketMethodInfo.Add(item.GetParameters()[0].ParameterType.Name, item);
            }
            ModPacketMethodInfo.Add(nameof(Vector2), typeof(Utils).GetMethod("WriteVector2", BindingFlags.Static | BindingFlags.Public | BindingFlags.NonPublic));
            #endregion 包写入方法与流读取方法

            #region Projectile
            int typeProjectileCount = 0;
            foreach (Type item in AssemblyManager.GetLoadableTypes(typeof(GensokyoWPNACC).Assembly).Where(type => type.IsSubclassOf(typeof(ModProjectile))))
            {
                ModProjectleTypes.Add(new Tuple<int, Type>(typeProjectileCount, item));
                
            };

            #region 字段 和 属性
            ModProjectleTypes.ForEach(tuple =>
            {
                Type type = tuple.Item2;
                var fields = type.GetFields(CanReflectionWell()).Where(field => field.GetCustomAttribute<ProjectileNetFieldAttribute>() != null && TypeCheck(field));
                if(fields.Count() >= 1)
                {
                    ModProjectleFields.Add(type, fields.ToList());
                }

                var propertys = type.GetProperties(CanReflectionWell()).Where(proper => proper.GetCustomAttribute<ProjectileNetPropertyAttribute>() != null && TypeCheck(proper));
                if(propertys.Count() >= 1)
                {
                    ModProjectilePropertys.Add(type, propertys.ToList());
                }
            });
            #endregion 字段 和 属性
            foreach (var item in ModProjectleTypes)
            {
                MethodInfo method = item.Item2.GetMethod("OnSpawn");
                MonoModHooks.Add(method, HookProjectileOnSpawnMethod);
            }
            #endregion
        }

        /// <summary>
        /// 获取万用反射条件
        /// </summary>
        public static BindingFlags CanReflectionWell()
        {
            return
                BindingFlags.Instance |
                BindingFlags.Public |
                BindingFlags.NonPublic;
        }

        /// <summary>
        /// 给定类型 判断该类型是否支持自动同步
        /// </summary>
        public static bool TypeCheck(Type type)
        {
            return EnumValueMap.TryGetValue(type, out string value);
        }

        /// <summary>
        /// 给定一个字段 判断该字段是否支持自动同步
        /// </summary>
        public static bool TypeCheck(FieldInfo fieldInfo)
        {
            return EnumValueMap.TryGetValue(fieldInfo.FieldType, out string value);
        }

        /// <summary>
        /// 给定一个属性 判断该属性是否支持自动同步
        /// </summary>
        public static bool TypeCheck(PropertyInfo propertyInfo)
        {
            return EnumValueMap.TryGetValue(propertyInfo.PropertyType, out string value);
        }

        public enum NetDataFormat
        {
            //BinaryReader
            ReadInt16 = 1,
            ReadInt32 = 2, 
            ReadInt64 = 3,
            ReadUInt16 = 4,
            ReadUInt32 = 5,
            ReadUInt64 = 6,
            ReadDouble = 7,
            ReadSingle = 8,
            ReadBoolean = 9,
            ReadVector2 = 10//Utils ReadVector2(this BinaryReader bb) => new Vector2(bb.ReadSingle(), bb.ReadSingle());
        }

        public enum NetPacketType
        {
            弹幕 = 1,
            物品 = 2
        }

        public static void RemoveNoActiveProjectile()
        {
            ActiveProjectiles.RemoveAll(f =>
            {
                var p = f.Projectile;
                return !p.active;
            });
        }
    }
}
