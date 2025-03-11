using System.Reflection;

namespace TypeMap
{
    internal class Program
    {
        static void Main(string[] args)
        {
            TypeMap.RegMap<User, UserV>((User f) => new UserV { Name = f.Name });
            var typeMap = TypeMap.Map<User, UserV>(new User() { Id = 999, Name = "小李" });
            Console.WriteLine(typeMap.ToString());

            var autoMap = AutoMap.Map<UserV>(new User() { Id = 999, Name = "小花" });
            Console.WriteLine(autoMap.ToString());
        }
    }

    public class User
    {
        public int Id;
        public string Name = string.Empty;
        public override string ToString()
        {
            return "User: " + Id + ", " + Name;
        }
    }

    public class UserV
    {
        public string Name = string.Empty;
        public override string ToString()
        {
            return "UserV: " + Name;
        }
    }



    //public class TypeMapModel<Source, TarGet>
    //{
    //    public Type SourceType { get; set; }
    //    public Type TarGetType { get; set; }
    //    public Func<Source, TarGet> ToGoMethod { get; set; }
    //    public TypeMapModel(Type sourceType, Type targettype, Func<Source, TarGet> func)
    //    {
    //        SourceType = sourceType;
    //        TarGetType = targettype;
    //        ToGoMethod = func;
    //    }
    //}

    public static class TypeMap
    {
        private static Dictionary<Tuple<Type, Type>, Delegate> MapMethod { get; } = [];

        private static void RegMap(Type source, Type target, Delegate @delegate)
        {
            MapMethod.Add(Tuple.Create(source, target), @delegate);
        }

        /// <summary>
        /// 注册映射
        /// </summary>
        public static void RegMap<Source, TarGet>(Delegate @delegate)
        {
            if(!(@delegate is Func<Source, TarGet> func)) {
                throw new Exception("TypeMap.RegMap<Source, TarGet> 中 delegate应当为Func<Source, TarGet>");
            }
            RegMap(typeof(Source), typeof(TarGet), func);
        }

        public static TarGet Map<Source, TarGet>(object mapObj)
        {
            var key = Tuple.Create(typeof(Source), typeof(TarGet));
            if(!MapMethod.TryGetValue(key, out var value)) {
                throw new Exception("未找到对应关系映射");
            }
            var func = value as Func<Source, TarGet>;
            return func!.Invoke((Source)mapObj);
        }
    }

    public static class AutoMap
    {
        public static TarGet Map<TarGet>(object source)
        {
            Type sourceType = source.GetType();
            Type tarGetType = typeof(TarGet);
            PropertyInfo[] sourcePropertys = sourceType.GetProperties();
            PropertyInfo[] tarGetPropertys = tarGetType.GetProperties();
            FieldInfo[] sourceFields = sourceType.GetFields();
            FieldInfo[] tarGetFields = tarGetType.GetFields();

            ConstructorInfo coMethod = tarGetType.GetConstructors(BindingFlags.Public | BindingFlags.Instance).First(f => f.GetParameters().Length == 0) ?? throw new Exception("给定目标类型没有无参构造！");
            object targetObj = coMethod.Invoke(null);
            foreach (var pr in tarGetPropertys) {
                var p = sourcePropertys.FirstOrDefault(prope => prope.Name == pr.Name);
                if (p is null) continue;
                pr.SetValue(targetObj, p.GetValue(source));
            }

            foreach (var fd in tarGetFields) {
                var p = sourceFields.FirstOrDefault(field => field.Name == fd.Name);
                if (p is null) continue;
                fd.SetValue(targetObj, p.GetValue(source));
            }

            return (TarGet)targetObj;
        }
    }
}
