using BlackTenText.EntityModels;
using BlackTenText.UtilityType;
using Microsoft.EntityFrameworkCore;
using Pomelo.EntityFrameworkCore.MySql.Infrastructure;

namespace BlackTenText.DbContexts;

public class AppDbContext(IConfiguration configuration): DbContext
{
    static AppDbContext()
    {
        AppDbContext? dbContext = null;
        try {
            dbContext = new AppDbContext(Program.Application!.Configuration);
            dbContext.Database.EnsureCreated();
        } catch {
        } finally {
            dbContext?.Dispose();
        }
    }

    public DbSet<User> UserTable { get; private set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        //获取配置块
        var connectStr = MySQLConnectionStringBuild.Parser(configuration, "mysql").Build();
        var ver = ServerVersion.Create(8, 0, 11, ServerType.MySql);
        optionsBuilder.UseMySql(connectStr, ver, option => {
            option.EnableRetryOnFailure(2);
        });
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<User>(user => {
            #region Id
            user.HasKey(u => u.Id);
            user.HasIndex(u => u.Id, "idIndex");
            user.Property(u => u.Id)
                .ValueGeneratedOnAdd()
                .HasColumnName(User.id)
                .HasComment("用户唯一id")
                ;
            #endregion

            #region name
            user.HasAlternateKey(u => u.Name);
            user.Property(u => u.Name)
                .HasColumnName(User.name)
                .HasComment("用户名称")
                ;
            #endregion

            #region password
            user.Property(u => u.Password)
                .HasColumnName(User.password)
                .HasComment("用户密码has")
                ;
            #endregion

            #region 密码盐
            user.Property(u => u.Salt)
                .HasColumnName(User.salt)   
                .HasComment("用户密码盐")
                ;
            #endregion

            #region head sculpture 头像
            user.Property(u => u.HeadSculpture)
                .HasColumnName(User.headSculpture)
                .HasComment("用户头像")
                ;
            #endregion

            //是外键
            #region regtime
            user.HasIndex(u => u.Regtime);
            user.Property(u => u.Regtime)
                .HasColumnName(User.regtime)
                .HasComment("注册时间")
                ;
            #endregion
        });
        base.OnModelCreating(modelBuilder);
    }
}