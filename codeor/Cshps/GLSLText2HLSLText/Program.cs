using GLSLText2HLSLText;
using GLSLText2HLSLText.TextType;

string glslText = """
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
    """;
//1. 删除所有内容的前后空字符
string noTrimText = glslText.Trim();
//2. 删除所有 // 开头的行
var glslTextLine = glslText
    .Split(Environment.NewLine)
    .Where(s => !string.IsNullOrWhiteSpace(s))
    .ToList();
glslTextLine.RemoveAll(line => line.StartsWith("//"));
//3. 删除嵌入的 注释内容 (//)
var delsumary = glslTextLine
    .Select(line => {
        int index = line.FindIndex("//");
        if (index != -1) {
            return line[.. index];
        }
        return line;
    });

//4. 删除空行
var noEmptyLine = delsumary.Where(s => !s.IsWhiteSpace()/*f => f != Environment.NewLine*/);

//5. 删除每行的空格
var noSpac = noEmptyLine.Select(s => s.Trim());

//5. 形成单一行
var finlstr = string.Join(string.Empty, noSpac);
Console.WriteLine(finlstr);

var hlsl = Text.ToHlsl(finlstr);
Console.WriteLine(hlsl);
