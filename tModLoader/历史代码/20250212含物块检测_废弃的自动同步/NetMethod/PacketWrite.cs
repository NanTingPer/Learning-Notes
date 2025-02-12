using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using Terraria;
using Terraria.ModLoader;
using static GensokyoWPNACC.PacketMode.AllNetContent;

namespace GensokyoWPNACC.PacketMode.NetMethod
{
    public static partial class NetMethods //PacketWrite 包写入方法
    {
        /// <summary>
        /// 给定一个模组包，给定一个字段，将value写入模组包
        /// </summary>
        public static bool WritePacket(ModPacket pt, FieldInfo field, object value)
        {
            if (ModPacketMethodInfo.TryGetValue(field.FieldType.Name, out MethodInfo ptMethodInfo))
            {
                if (ptMethodInfo.GetParameters().Length > 1)
                    ptMethodInfo.Invoke(pt, [pt, value]);
                else
                    ptMethodInfo.Invoke(pt, [value]);

                return true;
            }
            return false;
        }

        /// <summary>
        /// 给定一个模组包，给定一个属性，将value写入模组包
        /// </summary>
        public static bool WritePacket(ModPacket pt, PropertyInfo property, object value)
        {
            if (ModPacketMethodInfo.TryGetValue(property.PropertyType.Name, out MethodInfo ptMethodInfo))
            {
                if (ptMethodInfo.GetParameters().Length > 1)
                    ptMethodInfo.Invoke(pt, [pt, value]);
                else
                    ptMethodInfo.Invoke(pt, [value]);
                return true;
            }
            return false;
        }

        /// <summary>
        /// 传入一个Mod包，一个值，将值写入模组包
        /// </summary>
        public static bool WritePacket(ModPacket pt, object value)
        {
            if (ModPacketMethodInfo.TryGetValue(value.GetType().Name, out MethodInfo ptMethodInfo))
            {
                if (ptMethodInfo.GetParameters().Length > 1)
                    ptMethodInfo.Invoke(pt, [pt, value]);
                else
                    ptMethodInfo.Invoke(pt, [value]);
                return true;
            }
            return false;
        }

        /// <summary>
        /// 传入一个Mod包，一个值，一个类型名称，将值写入包
        /// </summary>
        public static void WritePacket(ModPacket pt, object value, string typeName)
        {
            switch (typeName)
            {
                case "Int32":
                    pt.Write((int)value);
                    break;

                case "Int64":
                    pt.Write((long)value);
                    break;

                case "Int16":
                    pt.Write((short)value);
                    break;

                case "UInt16":
                    pt.Write((ushort)value);
                    break;

                case "UInt32":
                    pt.Write((uint)value);
                    break;

                case "UInt64":
                    pt.Write((ulong)value);
                    break;

                case "Single":
                    pt.Write((float)value);
                    break;

                case "Double":
                    pt.Write((double)value);
                    break;

                case "Vector2":
                    pt.WriteVector2((Vector2)value);
                    break;
            }
        }

    }
}
