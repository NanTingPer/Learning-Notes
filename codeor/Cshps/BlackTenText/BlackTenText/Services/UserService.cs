using BlackTenText.DbContexts;
using BlackTenText.EntityModels;
using Microsoft.EntityFrameworkCore;
using System.Security.Cryptography;
using System.Text;

namespace BlackTenText.Services;

public class UserService(AppDbContext db, ILogger<UserService> log) : 
    BaseDbService<User, UserService>(db, log)
{
    /// <summary>
    /// 使用UserDto创建用户
    /// </summary>
    /// <returns></returns>
    public async Task<IResult> Region(UserDto user)
    {
        var newUserName = user.UserName;

        var oldUser = await DbSet.FirstOrDefaultAsync(f => newUserName.Equals(f.Name));
        if(oldUser != null) {
            return Results.BadRequest("用户名已经存在");
        }

        //加密密码
        var passwordPbkdf2 = Encipher(user.Password, out var salts);
        //创建User对象
        var newUser = new User()
        {
            Salt = salts,
            Password = passwordPbkdf2,
            Name = user.UserName,
            Regtime = DateTime.Now
        };
        await DbSet.AddAsync(newUser);
        await DbContext.SaveChangesAsync();
        return Results.Ok();
    }

    /// <summary>
    /// 验证用户
    /// </summary>
    /// <param name="dto"></param>
    /// <returns></returns>
    public async Task<IResult> Comparison(UserDto dto)
    {
        var password = dto.Password;
        var dbUser = await DbSet.AsNoTracking().FirstOrDefaultAsync(u => u.Name == dto.UserName);
        if (dbUser == null)
            return Results.BadRequest("验证失败，用户不存在");

        if (Comparison(password, dbUser.Salt, dbUser.Password)){
            return Results.Ok();
        } else {
            return Results.BadRequest("验证失败");
        }
    }


    /// <summary>
    /// 对密码进行加密 并返回盐
    /// </summary>
    /// <returns></returns>
    private static string Encipher(string password, out string saltsBase64)
    {
        var pwsdBytes = Encoding.UTF8.GetBytes(password);
        byte[] bytes = new byte[16]; // 16 * 4 bit
        RandomNumberGenerator.Fill(bytes);
        //将盐转换为 base64
        saltsBase64 = Convert.ToBase64String(bytes); /*Encoding.UTF8.GetString(bytes);*/

        //使用Pbkdf2迭代加密
        var passwd = Rfc2898DeriveBytes.Pbkdf2(pwsdBytes, bytes, 100_000, HashAlgorithmName.SHA256, 32); //32字节 32 * 4 bit
        //Encoding.UTF8.GetString(pwsdHash);
        return Convert.ToBase64String(passwd);
    }

    /// <summary>
    /// 对密码以及盐进行比对
    /// </summary>
    /// <param name="password"> 密码 </param>
    /// <param name="saltsBase64"> 盐 </param>
    /// <param name="encipherBase64"> 原始加密值 </param>
    /// <returns></returns>
    private static bool Comparison(string password, string saltsBase64, string encipherBase64)
    {
        var saltsBytes = Convert.FromBase64String(saltsBase64); //盐的字节
        var pwsdBytes = Encoding.UTF8.GetBytes(password);
        var passwd = Rfc2898DeriveBytes.Pbkdf2(pwsdBytes, saltsBytes, 100_000, HashAlgorithmName.SHA256, 32);

        var encipherBytes = Convert.FromBase64String(encipherBase64);
        return CryptographicOperations.FixedTimeEquals(passwd, encipherBytes);
    }
}
