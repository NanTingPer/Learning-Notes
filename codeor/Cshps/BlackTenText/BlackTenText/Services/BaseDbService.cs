using BlackTenText.DbContexts;
using BlackTenText.EntityModels;
using Microsoft.EntityFrameworkCore;

namespace BlackTenText.Services;

public abstract class BaseDbService<Entity, TSelf>(AppDbContext db, ILogger<TSelf> log) 
    where Entity : class
    where TSelf : BaseDbService<Entity, TSelf>
{
    /// <summary>
    /// 由依赖注入 注入的DbContext 因此不需要手动释放！
    /// </summary>
    public AppDbContext DbContext { get; init; } = db;

    public DbSet<Entity> DbSet { get; init; } = db.Set<Entity>();
    public ILogger Log { get; init; } = log;
}
