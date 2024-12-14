namespace linq
{
    internal class Program
    {
        static void Main(string[] args)
        {
            //第一节 手写简单LINQ
            IEnumerable<int> nums = new List<int> { 1,2,3,4,5,6,7,8,1,1,11,1,1,1,1,1,11,12};
            var value = PartOneMyWhere<int>(nums, f => f > 10);
            foreach (var i in value)
            { 
                Console.WriteLine(i); 
            }
        }

        //第一节
        static IEnumerable<T> PartOneMyWhere<T>(IEnumerable<T> list,Func<T,bool> func)
        {
            foreach(var num in list)
            {
                if (func(num))
                {
                    //流式返回
                    yield return num;
                }
            }
        }
    }
}
