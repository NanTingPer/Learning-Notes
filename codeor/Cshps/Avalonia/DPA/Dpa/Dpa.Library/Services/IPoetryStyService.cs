using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Threading.Tasks;
using Dpa.Library.Models;

namespace Dpa.Library.Services;

public interface IPoetryStyService
{
    /// <summary>
    /// 判断数据库是否迁移到用户应用目录
    /// </summary>
    bool IsInitialized { get; }
    
    /// <summary>
    /// 用来初始化数据库
    /// </summary>
    System.Threading.Tasks.Task InitializeAsync();

    /// <summary>
    /// 获取数据
    /// </summary>
    /// <param name="poetryName"> 该条数据在数据库中的ID </param>
    /// <returns></returns>
    Task<Poetry> GetPoetryAsync(string id);
    
    /// <summary>
    /// 通过过滤匹配
    /// </summary>
    /// <param name="where"></param>
    /// <param name="skip">跳过多少行</param>
    /// <param name="take">返回多少行</param>
    /// <returns></returns>
    Task<List<Poetry>> GetPoetryAsync(Expression<Func<Poetry,bool>>  where,int skip,int take);
}