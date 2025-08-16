using System.Text;

namespace GetNameAndId;

internal class Program
{
    static void Main(string[] args)
    {
        StringBuilder sb = new();
        File.ReadAllLines("D:\\QQ历史记录\\从零开始的mod制作教程.txt")
            .Where(f => f.StartsWith("2024") || f.StartsWith("2025"))
            .Select(f => f.Split(' ')[2..])
            .Select(f => string.Join(' ', f))
            .Select(f => {
                var index = 0;
                for (int i = f.Length - 1; i >= 0; i--) {
                    if (f[i] == '(') {
                        index = i;
                        break;
                    }
                }
                return (value: f, id: f[(index + 1)..]);
            })
            .DistinctBy(f => f.id)
            .ToList()
            .ForEach(f => sb.AppendLine(f.id + "\t" + f.value))
            ;

        File.WriteAllText("D:\\QQ历史记录\\ids.txt", sb.ToString());
    }
}
