// See https://aka.ms/new-console-template for more information
namespace OpenCvSharpExample;

public class ExtName
{
    private ExtName(string name)
    {
        Name = name;
    }
    public string Name { get; init; }
    public static ExtName Png { get; } = new ExtName(".png");
    public static ExtName Jpeg { get; } = new ExtName(".jpg");

    public static implicit operator string(ExtName extName) => extName.Name;
}