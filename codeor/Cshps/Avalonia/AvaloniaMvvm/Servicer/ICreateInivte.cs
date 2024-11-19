using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.Threading.Tasks;
using AvaloniaMvvm.Models;

namespace AvaloniaMvvm.Servicer;

public interface ICreateInivte
{
    /// <summary>
    /// 插入数据
    /// </summary>
    /// <param name="Proces"> 要被插入的数据 </param>
    /// <returns></returns>
    Task InsterAsync(SQLDataType sqlDataType);

    /// <summary>
    /// 初始化
    /// </summary>
    Task InitiaAsync();
    
    /// <summary>
    /// 查
    /// </summary>
    /// <returns>返回全部数据</returns>
    Task<List<Models.SQLDataType>> ScanAsync();
    
    /// <summary>
    /// 删
    /// </summary>
    Task DeleteAsync(SQLDataType sqlDataType);
}