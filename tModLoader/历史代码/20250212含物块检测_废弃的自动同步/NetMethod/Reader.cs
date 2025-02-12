using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace GensokyoWPNACC.PacketMode.NetMethod
{
    public static partial class NetMethods //Reader方法
    {
        /// <summary>
        /// 给定一个流，给定一个对象，给定一个字段列表，读取并设置对象相应字段的值，返回该值
        /// </summary>
        public static IEnumerable<object> Reader(BinaryReader reader, object obj, IList<FieldInfo> fields)
        {
            foreach (var field in fields)
            {
                if (reader.BaseStream.CanRead)
                {
                    if (GetReaderMethod(field, out MethodInfo readerMethod))
                    {
                        if (Reader(reader, readerMethod, out object value))
                        {
                            field.SetValue(obj, value);
                            yield return value;
                        }
                    }
                }
            }
        }

        /// <summary>
        /// 给定一个流，给定一个对象，给定一个属性列表，读取并设置对象相应属性的值，返回该值
        /// </summary>
        public static IEnumerable<object> Reader(BinaryReader reader, object obj, IList<PropertyInfo> properties)
        {
            foreach (var property in properties)
            {
                if (reader.BaseStream.CanRead)
                {
                    if (GetReaderMethod(property, out MethodInfo method))
                    {
                        if (Reader(reader, method, out object value))
                        {
                            property.SetValue(obj, value);
                            yield return value;
                        }
                    }
                }

            }
        }


        /// <summary>
        /// 使用readerMethod读取一次值
        /// </summary>
        public static bool Reader(BinaryReader reader, MethodInfo readerMethod, out object value)
        {
            if (reader.BaseStream.CanRead)
            {
                if (readerMethod.GetParameters().Length != 0)
                    value = readerMethod.Invoke(reader, [reader]);
                else
                    value = readerMethod.Invoke(reader, []);

                return true;
            }
            value = null;
            return false;
        }
    }
}
