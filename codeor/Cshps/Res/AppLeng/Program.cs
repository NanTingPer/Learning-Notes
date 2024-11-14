using System.Diagnostics.Tracing;
using System.IO;
using System.Reflection;
using System.Runtime.Loader;

namespace AppLeng
{
    internal class Program
    {
        static void Main(string[] args)
        {
            //利用反射加载类
            //查看程序所运行在的文件夹
            //Console.WriteLine(Environment.CurrentDirectory);

            //创建文件夹
            Directory.CreateDirectory(Environment.CurrentDirectory + "/Animals");

            //获取一个组合路径 Combine用于将路径组合起来 (string)
            string folder = Path.Combine(Environment.CurrentDirectory, "Animals");
            Console.WriteLine(folder);
            string[] files;
            //返回指定文件内的所有文件名
            files = Directory.GetFiles(folder);

            List<Type> animalTypes = [];

            //遍历文件内的所有文件名
            foreach(string filePath in files)
            {
                //加载指定位置的程序集
                Assembly assembly = AssemblyLoadContext.Default.LoadFromAssemblyPath(filePath);
                //获取该程序集内的所有类型(类)
                Type[] types = assembly.GetTypes();

                //遍历该程序集内的所有类型
                //如果有指定类型 那么就加载到类型集合
                foreach(Type type in types)
                {
                    if(type.GetMethod("Voice") != null)
                    {
                        animalTypes.Add(type);
                    }
                }
            }

            while (true)
            {
                for(int i = 0;i< animalTypes.Count;i++)
                {
                    Console.WriteLine($"{i + 1} , {animalTypes[i].Name}");
                }
                Console.WriteLine("======================================");

                Console.WriteLine("选择动物！!");

                //输入
                int index = How();
                if (index == -1) continue;

                if(index > animalTypes.Count || index < 1){Console.WriteLine("不要乱按哦！");continue;}

                Console.WriteLine("要叫几次呢?");

                //输入要叫多少次
                int num = How();
                if (num == 1) continue;

                //获取指定动物的类型
                Type animalType = animalTypes[index - 1];
                
                //获取方法对象用于调用
                MethodInfo AMethod = animalType.GetMethod("Voice");
                
                object AnimalType = Activator.CreateInstance(animalType);
                //设置参数列表
                object[] obj2 = [num];
                
                //调用
                AMethod.Invoke(AnimalType, obj2);

            }

        }

        public static int How()
        {
            string nums = Console.ReadLine();
            int num = 0;
            if (nums == null) { Console.WriteLine("小调皮"); return -1; }

            try { num = Convert.ToInt32(nums); }catch { Console.WriteLine("小调皮"); return -1; }

            if (num == 0) { Console.WriteLine("小调皮"); return -1; }

            return num;
        }
    }
}
