namespace DeBug
{
    public class Program
    {
        static void Main()
        {
            string age;
            int age1=0;
            Console.WriteLine("请输入你的年龄");
            age = Console.ReadLine();
            try
            {
                age1 = int.Parse(age);
                
            }
            catch
            {
                Console.WriteLine("请输入数字");
                return;
            }
            Console.WriteLine("你十年后的年龄为: " + age1 );
        }
    }
}