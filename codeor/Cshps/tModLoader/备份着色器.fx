//对于这些 是必须的
sampler uImage0 : register(s0);     //当前绘制的纹理 (传入的参数)
sampler uImage1 : register(s1);     //次级纹理 噪声贴图，用来与绘制纹理进行叠层
float3 uColor;                      //主颜色
float3 uSecondaryColor;             //次颜色，用于叠色或其他
float uOpacity;                     //不透明度
float uSaturation;                  //饱和度
float uRotation;                    //旋转纹理 旋转角度
float uTime;                        //时间变量

//绘制的位置 以及大小
float4 uSourceRect;                 //绘制的区域和大小
float2 uWorldPosition;              //绘制的位置 世界坐标
float uDirection;                   //方向变量 用于控制纹理方向

//用于反光染料
float3 uLightSource;                //光源位置
float2 uImageSize0;                 //第一个纹理的尺寸
float2 uImageSize1;                 //第二个纹理的尺寸
float2 uTargetPosition;             //目标位置 定义纹理绘制的最终位置
float4 uLegacyArmorSourceRect;      //
float2 uLegacyArmorSheetSize;       //

struct App2Vertex
{
    //需要注册为 VertexShader技术
    //顶点的坐标信息 POSITIONT是语义
    float4 position : POSITIONT;
};

struct Vertex2Pixel
{
    float4 position : POSITIONT;
    float4 color : COLOR;
};

//float4 sampleColor : COLOR0 是获取寄存器 COLOR0 上的值
//: COLOR0是输出颜色
//TEXCOORD0 纹理坐标 第一张图的
float4 PixelShaderFunction(float4 sampleColor : COLOR0, float2 coords : TEXCOORD0) : COLOR0
{
    float4 color = tex2D(uImage0, coords) * float4(0.2, 0.2, 0.2, 1);
    uSecondaryColor = float3(0.5, 0.2, 0.6);
    return color * sampleColor * float4(0, 0, 0, 1);
}

//COLOR0 语义 => 输出颜色到 Tex2D0色彩
float4 PixelShaderFunction() : COLOR0
{
    return float4(0.2, 0.5, 0.5, 0.2);
}

//SV_Target0 语义 => 直接将颜色输出到屏幕
float4 PixelShaderFunction2() : SV_Target0
{
    float r = sin(0.1 * 8f + uTime) / 8f;
    r = 0.02 / r;
    float3 col = (1f, 2f, 3f);
    
    col *= r;
    //变色
    //return float4(sin(uTime), cos(uTime), sin(uTime), sin(uTime));
    return float4(col, r);
}

technique Technique1
{
    pass ModdersToolkitShaderPass
    {
        PixelShader = compile ps_2_0 PixelShaderFunction();
    }
}
