using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using Terraria.ModLoader;
using Terraria;
using static GensokyoWPNACC.PacketMode.AllNetContent;

namespace GensokyoWPNACC.PacketMode.NetMethod
{
    [Obsolete("应当使用新的OneLoading")]
    public static partial class NetMethods //获取字段 / 属性引用 / 字段相应的读取or写入方法
    {
        /// <summary>
        /// 给定一个弹幕 获取该弹幕的字段列表
        /// </summary>
        public static List<FieldInfo> GetFieldInfos(Projectile projectile, out ModProjectile modProjectile, out Type projectileType)
        {
            modProjectile = projectile.ModProjectile;
            projectileType = modProjectile.GetType();
            ModProjectleFields.TryGetValue(projectileType, out var value);
            return value;
        }

        public static List<PropertyInfo> GetPropertyInfos(Projectile projectile, out ModProjectile modProjectile, out Type projectileType)
        {
            modProjectile = projectile.ModProjectile;
            projectileType = modProjectile.GetType();
            ModProjectilePropertys.TryGetValue(projectileType, out var value);
            return value;
        }

        /// <summary>
        /// 给定一个属性 获取适用于该属性的读取方法
        /// </summary>
        public static bool GetReaderMethod(PropertyInfo propertyInfo, out MethodInfo method)
        {
            if (EnumValueMap.TryGetValue(propertyInfo.PropertyType, out string methodName))
            {
                ReaderMethodInfo.TryGetValue(methodName, out method);
                return true;
            }
            method = null;
            return false;
        }

        /// <summary>
        /// 给定一个字段 获取适用于该字段的读取方法
        /// </summary>
        public static bool GetReaderMethod(FieldInfo field, out MethodInfo method)
        {
            if (EnumValueMap.TryGetValue(field.FieldType, out string methodName))
            {
                ReaderMethodInfo.TryGetValue(methodName, out method);
                return true;
            }
            method = null;
            return false;
        }

    }
}
