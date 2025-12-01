// See https://aka.ms/new-console-template for more information
using OpenCvSharp;
using OpenCvSharpExample;

Console.WriteLine("Hello, World!");
using var video = new VideoCapture();
//打开视频 如果成功返回true
if (!video.Open(@"D:\bilibili\未分类\20250504-115820.mp4")) {
    return;
}
//读取一个视频帧，如果没有更多 会返回false
var mat = new Mat();
int i = 0;
while (video.Read(mat)) {
    var bytes = mat.ToBytes(ExtName.Png);
    File.WriteAllBytes($@"D:\1\2\{i}.png", bytes);
    i++;
}
mat.Dispose();
