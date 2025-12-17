## 将GLSL代码文本转换为HLSL文本
只支持简单内容。
- 由Trae进行分词

输入
```glsl
//AAA水果批发
vec2 UV(vec2 pos); //AAA水果批发
void mainImage( out vec4 fragColor, in vec2 fragCoord)
{

    //float color = GetColorBoom(fragCoord);
    float color = GetColorNoBoom(fragCoord);

    //float color = HollowOutCircle(fragCoord);
    fragColor = vec4(color) * vec4(1.,1.,0.,1.);
}
vec2 UV(vec2 pos)
{
    //归一化
    vec2 uv = pos / iResolution.xy;
    uv -= 0.5; //居中
    uv.x *= iResolution.x / iResolution.y;
    return uv;
}
```
---
输出
```hlsl
float2 UV(float2 pos : SV_Position);

float4 mainImage(float2 fragCoord : SV_Position) : COLOR
{
    float color = GetColorNoBoom(fragCoord);
    return vec4(color) * vec4(1.,1.,0.,1.);
}


float2 UV(float2 pos : SV_Position)
{
    vec2 uv = pos / iResolution.xy;
    uv -= 0.5;
    uv.x *= iResolution.x / iResolution.y;
    return uv;
}
```