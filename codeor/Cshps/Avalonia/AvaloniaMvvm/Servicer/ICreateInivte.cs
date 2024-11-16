using System.Diagnostics;
using System.Threading.Tasks;

namespace AvaloniaMvvm.Servicer;

public interface ICreateInivte
{
    /// <summary>
    /// 插入数据
    /// </summary>
    /// <param name="process"> 要被插入的数据 </param>
    /// <returns></returns>
    Task InsterAsync(Process process);

    Task InitiaAsync();
}