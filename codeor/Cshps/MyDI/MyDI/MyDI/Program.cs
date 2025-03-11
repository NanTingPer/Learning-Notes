using System.Reflection;

namespace MyDI
{
    internal class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Hello, World!");
            DI.RegisterType<TypeOne>();
            DI.RegisterType<TypeTwo>();
            DI.Loading();
            TypeTwo type = DI.Get<TypeTwo>();
            type._typeOne.Printf();
        }
    }

    public class TypeOne
    {
        public void Printf()
        {
            Console.WriteLine("HelloTypeOne");
        }
    }

    public class TypeTwo
    {
        public TypeOne _typeOne;
        public TypeTwo(TypeOne typeOne)
        {
            _typeOne = typeOne;
        }
    }

    public class DI
    {
        /// <summary>
        /// 类型对应有效构造方法
        /// </summary>
        private static Dictionary<Type, ConstructorInfo> TypeCreateMethods { get; } = [];
        private static Dictionary<Type, object> TypeObjects { get; } = [];
        /// <summary>
        /// 注册类型
        /// </summary>
        public static void RegisterType(Type type)
        {
            foreach (var constructor in type.GetConstructors()) {
                if (constructor.GetParameters().Length == 0) {
                    TypeObjects.Add(type, constructor.Invoke(null));
                    return;
                }
            }

            if (!TypeCreateMethods.ContainsKey(type)) {
                TypeCreateMethods.Add(type, type.GetConstructors(BindingFlags.Instance | BindingFlags.Public).FirstOrDefault(f => true)!);
            }
        }
        public static void RegisterType<Type>() => RegisterType(typeof(Type));

        /// <summary>
        /// 开始创建依赖注入对象
        /// </summary>
        public static void Loading()
        {
            List<Type> types = [];
            foreach (var kv in TypeCreateMethods) {
                types.Add(kv.Key);
            }

            while (types.Count != 0) {
                for (int i = 0; i < types.Count; i++) {
                    var currType = types[i];
                    if (TypeCreateMethods.TryGetValue(currType, out var value)){
                        var allParameter = value.GetParameters();
                        var count = allParameter.Length;
                        int timer = 0;
                        List<object> objs = [];
                        foreach (var item in allParameter) {//遍历构造参数
                            if (TypeObjects.TryGetValue(item.ParameterType, out var obj)) {
                                timer++;
                                objs.Add(obj);
                            }
                        }
                        if(timer == count) {
                            TypeObjects.Add(currType, value.Invoke(objs.ToArray())); //依赖已经全部创建，那么就可以创建该对象了
                            types.Remove(currType);
                        } else {
                            types.Remove(currType);//指向的是对象实例，不是索引
                            types.Add(currType);
                        }
                    } else {
                        types.Remove(currType);
                    }
                }
            }
            
        }
        
        public static T Get<T>()
        {
            if(!TypeObjects.TryGetValue(typeof(T), out var value)) {
                throw new Exception("DI.Get<T> 此类型未找到！");
            }
            return (T)value!;
        }

    }
}
