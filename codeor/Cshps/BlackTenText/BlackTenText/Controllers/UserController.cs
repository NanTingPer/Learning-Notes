using BlackTenText.EntityModels;
using BlackTenText.Services;
using BlackTenText.UtilityType;
using Microsoft.AspNetCore.Mvc;
//using Microsoft.Extensions.Caching.Memory;
using System.Collections.Concurrent;

namespace BlackTenText.Controllers;

[Route("api/[controller]")]
[ApiController]
public class UserController(GenerationVerifyCode verify, UserService service/*, IMemoryCache cache*/) : ControllerBase
{
    public static readonly ConcurrentDictionary<string, string> VerifyCodes = [];

    private static DateTime LastClearTime { get; set; }

    [HttpPost("region")]
    public async Task<IResult> RegionUser(UserDto user)
    {
        var result = VerifyCode(user);
        if (result != null)
            return result;
        return await service.Region(user);
    }

    [HttpPost("login")]
    public async Task<IResult> Login(UserDto user)
    {
        var result = VerifyCode(user);
        if (result != null)
            return result;
        return await service.Comparison(user);
    }

    /// <summary>
    /// 获取验证码信息
    /// </summary>
    [HttpPost("verifycode")]
    public IResult VerifyIamge()
    {
        if(DateTime.Now - LastClearTime > TimeSpan.FromSeconds(30)) { //30秒清理一次残存
            VerifyCodes.Clear();
            LastClearTime = DateTime.Now;
        }

        var verifyI = new VerifyIamge();
        //验证码唯一标识
        var guid = Guid.NewGuid().ToString();
        verifyI.UUID = guid;

        //获取验证码文本和图片信息
        (string text, string base64) = verify.GetRandomTextImageBase64();
        verifyI.Base64 = base64;
        VerifyCodes.AddOrUpdate(guid, text, (f, f1) => f1);

        return Results.Ok(verifyI);
    }


    private static IResult? VerifyCode(UserDto user)
    {
        if (user.UUID == string.Empty) {
            return Results.BadRequest("请给出验证码");
        }
        if (VerifyCodes.TryGetValue(user.UUID, out var verifyText)) {
            return Results.BadRequest("验证码已过期或无效");
        }
        if (!verifyText!.Equals(user.Text, StringComparison.OrdinalIgnoreCase)) {
            return Results.BadRequest("验证码对吗");
        }
        VerifyCodes.Remove(user.UUID, out _);
        return null;
    }
}

public record class VerifyIamge
{
    public string UUID { get; set; } = string.Empty;
    public string Base64 { get; set; } = string.Empty;
}