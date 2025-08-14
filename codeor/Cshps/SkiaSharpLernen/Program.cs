using SkiaSharp;

namespace SkiaSharpLernen;

internal class Program
{
    static void Main(string[] args)
    {
        Console.WriteLine("Hello, World!");
        using var 雏菊 = SkiaSharpMethod.GetBitmap("C:\\雏菊.jpg",10,10);
        using var 山 = SkiaSharpMethod.GetBitmap("C:\\山.png",10,10);

        using var canvas = new SKCanvas(山);
        canvas.DrawBitmap(雏菊, new SKPoint(山.Width / 2, 山.Height / 2));
        var textPoint = new SKPoint(山.Width / 2, 山.Height / 2);
        var paint = new SKPaint();
        var font = new SKFont();

        canvas.DrawText("123456", textPoint, SKTextAlign.Center, font, paint);
        paint.Dispose();
        font.Dispose();

        canvas.Flush();
        var value = 山.Encode(SKEncodedImageFormat.Png, 100).ToArray();
        File.WriteAllBytes("D:\\2\\cjs.png", value);
    }
}

public static class SkiaSharpMethod
{
    public static SKBitmap GetBitmap(string filePath, SKImageInfo info)
    {
        var bitMap = SKBitmap.Decode(filePath, info);
        return bitMap;
    }

    public static SKBitmap GetBitmap(string filePath)
    {
        var bitMap = SKBitmap.Decode(filePath);
        return bitMap;
    }

    public static SKBitmap GetBitmap2x2(string filePath)
    {
        return GetBitmap(filePath, 2, 2);
    }

    public static SKBitmap GetBitmap(string filePath, float heightScala, float widthScala)
    {
        var stream = File.Open(filePath, FileMode.Open);
        var bitmap = GetBitmap(stream, heightScala, widthScala);
        stream.Dispose();
        return bitmap;
    }

    public static SKBitmap GetBitmap(Stream stream, float heightScala, float widthScala)
    {
        using var codec = SKCodec.Create(stream);
        var info = codec.Info;
        int scaledWidth = Math.Max(1, (int)(info.Width / widthScala));
        int scaledHeight = Math.Max(1, (int)(info.Height / heightScala));
        var newInfo = new SKImageInfo()
        {
            AlphaType = info.AlphaType,
            ColorSpace = info.ColorSpace,
            ColorType = info.ColorType,
            Height = scaledHeight,
            Width = scaledWidth
        };
        stream.Seek(0, SeekOrigin.Begin);
        var oldskbit = SKBitmap.Decode(stream, info);
        var sksamp = new SKSamplingOptions(SKFilterMode.Linear, SKMipmapMode.Linear);
        var newbit = oldskbit.Resize(newInfo, sksamp);
        oldskbit.Dispose();
        return newbit;
    }

    public static SKData JpgToPng(string filePath)
    {
        var jpgBit = SKBitmap.Decode(filePath);
        var pngBit = jpgBit.Copy(SKColorType.Rgba8888);
        jpgBit.Dispose();
        return pngBit.Encode(SKEncodedImageFormat.Png, 100);
    }
}
