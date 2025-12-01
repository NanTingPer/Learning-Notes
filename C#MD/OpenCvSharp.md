# Open CV Sharp

## 打开视频

使用类型`VideoCapture`可以创建一个视频读取器，用来按帧读取视频。
```cs
using var video = new VideoCapture();
//打开视频 如果成功返回true
if (!video.Open(@"D:\bilibili\未分类\20250504-115820.mp4")) {
    return;
}
```

使用对象的`Read`方法可以进行读取，由于其采用`C++`的风格，因此需要手动创建`Mat`，读取后，会将其覆盖

```cs
//读取一个视频帧，如果没有更多 会返回false
var mat = new Mat();
int i = 0;
while (video.Read(mat)) {
    var bytes = mat.ToBytes(".png");
    File.WriteAllBytes($@"D:\1\2\{i}.png", bytes);
    i++;
}
mat.Dispose();
```

对于`ToBytes`的媒体类型，由于需要`.`开头，我们不能使用枚举，但是我们可以自定义类型，并定义类型转换，下面是使用隐式类型转换实现的

```cs
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
```

```cs
var bytes = mat.ToBytes(ExtName.Png);
```

对于`VideoCapture.Read()`，其实自己传递`Mat`并不是没有好处，这样不用频繁创建对象，而是复用。如果想要使用`out`输出，也可以自定义一个方法，但是要记得`using / Dispose`