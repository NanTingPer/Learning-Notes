namespace StringNeiCha
{
    internal class Program
    {
        //2024 9 10
        static void Main(string[] args)
        {
            
            for(int i =0;i< 100;i++)
            {
                string A = $"XXXX{i}";
                Console.WriteLine(A.Equals($"XXXX{i}"));
            }
            
        }
    }
}
