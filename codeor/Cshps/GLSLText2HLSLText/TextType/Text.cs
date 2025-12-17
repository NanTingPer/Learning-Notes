namespace GLSLText2HLSLText.TextType;

/// <summary>
/// 提供 GLSL 文本的分词与基础解析能力，支持分阶段正则匹配：
/// 先按代码块符号分割，再按空白与运算符分割，最后识别关键字。
/// </summary>
public abstract partial class Text
{
    /// <summary>
    /// 文本所属元类型，如方法、语句、字段等。
    /// </summary>
    public abstract MetaType MetaType { get; }
    /// <summary>
    /// 当前文本片段的令牌集合。
    /// </summary>
    public List<string> Tokens { get; } = [];
    internal string Code { get; set; } = string.Empty;

    /// <summary>
    /// 解析 GLSL 文本为内部 <see cref="Text"/> 结构集合。
    /// </summary>
    /// <param name="glslText">已预处理的 GLSL 文本（去除注释与空行）。</param>
    /// <returns>解析得到的文本结构列表。</returns>
    public static List<Text> Parse(string glslText)
    {
        var tokens = Tokenizer.Tokenize(glslText);
        foreach (var item in tokens) {
            Console.WriteLine(item);
        }

        var currentText = new MethodText();
        List<Text> parseTexts = [];
        return parseTexts;
    }

    /// <summary>
    /// 将 GLSL 文本转换为 HLSL 代码，包含方法签名与参数类型映射。
    /// </summary>
    /// <param name="glslText">输入的 GLSL 文本。</param>
    /// <returns>生成的 HLSL 代码。</returns>
    public static string ToHlsl(string glslText)
    {
        var tokens = Tokenizer.Tokenize(glslText);
        var methods = HlslParser.ParseMethods(tokens);
        var sb = new System.Text.StringBuilder();
        foreach (var m in methods) {
            sb.AppendLine(m.GenerateCode());
            sb.AppendLine();
        }
        return sb.ToString();
    }
}
