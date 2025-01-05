//对于这些 是必须的

//当前绘制的纹理 (传入的参数)
sampler uImage0 : register(s0);
//次级纹理 噪声贴图，用来与绘制纹理进行叠层
sampler uImage1 : register(s1);

float3 uColor;
float3 uSecondaryColor;
float uOpacity;
float uSaturation;
float uRotation;
float uTime;

//绘制的位置 以及大小
float4 uSourceRect;
float2 uWorldPosition;
float uDirection;

//用于反光染料
float3 uLightSource;
float2 uImageSize0;
float2 uImageSize1;
float2 uTargetPosition;
float4 uLegacyArmorSourceRect;
float2 uLegacyArmorSheetSize;


//float4是返回值 ArmoBasic是方法名
//float4 sampleColor : COLOR0 是获取寄存器 COLOR0 上的值
// : COLOR0是将 值返回到 COLOR0
float4 ArmoBasic1(float4 sampleColor : COLOR0) : COLOR0
{
    return sampleColor;
}


//TEXCOORD0是 当前象素相对于纹理的坐标
//  左上角是0,0 右上角是0,1
//  左下角是1,0 右下角是1,1

//将TEXCOORD0的值赋给 coords
float4 ArmoBasic2(float4 samp : COLOR0, float2 coords : TEXCOORD0) : COLOR0
{
    //获取传入纹理的coords的颜色
    //这是基于每个象素的 相当于绘制纹理
    float4 color = tex2D(uImage0, coords);
    
    //为了不影响透明度 需要与原来的值相乘
    return color * samp;
}

//现在我们将传入的 float3 uColor; 与color相乘
float4 ArmoBasic3(float4 samp : COLOR0, float2 coords : TEXCOORD0) : COLOR0
{
    float4 color = tex2D(uImage0, coords);
    
    //rgb分别相乘 
    //等效 
    //color.r *= uColor.r
    //color.r *= uColor.r
    //color.r *= uColor.r
    //这是一种重排 也可以使用 rrr
    //color.rgb *= uColor.rrr
    color.rgb *= uColor;
    
    return color * samp;
}

//亮度平均化
float4 ArmoBasic4(float4 samp : COLOR0, float2 coords : TEXCOORD0) : COLOR0
{
    //先取每个像素点
    float4 color = tex2D(uImage0, coords);
    //取平均值
    float avgColor = (color.r + color.b + color.g) / 3;
    
    //乘回去
    color.rgb *= uColor;

    return color * samp;
}



//定义渲染程序
technique Technique1
{

    //定义渲染程序的方法
    pass ArmoBasic
    {
        //Tr使用版本是 ps_2_0 必须指明
        PixelShader = compile ps_2_0 ArmoBasic();
    }
}