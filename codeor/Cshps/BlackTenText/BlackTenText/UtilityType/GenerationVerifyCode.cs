using SkiaSharp;
using System.Security.Cryptography;

namespace BlackTenText.UtilityType;

/// <summary>
/// 使用文本生成图片Base64字符
/// </summary>
public class GenerationVerifyCode
{
    /// <summary>
    /// 获取给定文本的Base64图像
    /// </summary>
    /// <param name="text"></param>
    /// <returns></returns>
    public string GetImageBase64(string text)
        => GenerationVerifyCodeUtils.GetImageBase64(text);

    /// <summary>
    /// 获取随机文本
    /// </summary>
    /// <returns></returns>
    public string GetRandomText(int length = 6)
        => GenerationVerifyCodeUtils.GetRandomText(length);

    /// <summary>
    /// 获取随机文本的字符串及其图像Base64
    /// </summary>
    /// <returns></returns>
    public (string text, string base64) GetRandomTextImageBase64(int length = 6)
        => GenerationVerifyCodeUtils.GetRandomTextImageBase64(length);
}
public class GenerationVerifyCodeUtils
{
    private readonly static Random ran = new Random();

    private readonly static string LUCONPATH = Path.Combine(Environment.CurrentDirectory, "font", "LUCON.TTF");

    private readonly static SKFont skfont = new SKFont(SKTypeface.FromFile(LUCONPATH));


    /// <summary>
    /// 获取给定文本的Base64图像
    /// </summary>
    /// <param name="text"></param>
    /// <returns></returns>
    public static string GetImageBase64(string text)
    {
        var bitmapWidth = skfont.GetGlyphWidths(text).Sum() + 42;
        var imageInfo = new SKImageInfo((int)bitmapWidth, 20, SKColorType.Rgba8888, SKAlphaType.Premul);
        using var bit = new SKBitmap(imageInfo);
        using var canvas = new SKCanvas(bit);
        canvas.DrawText(text, 5, 14, SKTextAlign.Left, skfont, new SKPaint());
        for (int i = 0; i < 200; i++) {
            canvas.DrawPoint(ran.Next(0, 42), ran.Next(0, 20), new SKColor((byte)ran.Next(0, 255), (byte)ran.Next(0, 255), (byte)ran.Next(0, 255), (byte)ran.Next(0, 255)));
        }
        using var imgbit = SKImage.FromBitmap(bit);
        using var data = imgbit.Encode(SKEncodedImageFormat.Png, 1000);

        var values = data.ToArray();
        return Convert.ToBase64String(values);
    }

    private readonly static char[] a_z_1_9 = [
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G' ,'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '1', '2', '3', '4', '5', '6', '7', '8', '9'];

    /// <summary>
    /// 获取随机文本
    /// </summary>
    /// <returns></returns>
    public static string GetRandomText(int length = 6)
    {
        return RandomNumberGenerator.GetString(a_z_1_9, length);
    }

    /// <summary>
    /// 获取随机文本的字符串及其图像Base64
    /// </summary>
    /// <returns></returns>
    public static (string text, string base64) GetRandomTextImageBase64(int length = 6)
    {
        var text = GetRandomText(length);
        return (text, GetImageBase64(text));
    }
}
