using System.Reflection;

namespace 反射
{
    public class 对象
    { }
    public class Program
    {
        public static List<对象> 对象s = [];
        private int r = 0;
        private int rrr { get; set; } = 0;
        static void Main(string[] args)
        {
            while (true)
            {
                new Thread(() =>
                {
                    while (true)
                        对象s.Add(new 对象());
                }).Start();

                Console.WriteLine($"你当前有{对象s.Count}个对象!!!!");
            }

        }
    }

    public class TestClass01
    {
        public int TestField;
    }

    public class TestClass02
    {
        public int TestField;
    }


    public class Student
    {
        public Student(){}

        private int age = 0;
        private string Name = "他还没有名字";

        public override string ToString()
        {
            return Name + ", " + age;
        }

        public string TestMethod<T>(string r, string r1)
        {
            return typeof(T).Name + r + r1;
        }
    }

    public class Dog
    {

    }
}
