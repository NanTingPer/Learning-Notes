// Depended by: Text.cs (uses HlslMethod/HlslParameter), HlslParser.cs
using System.Collections.Generic;
using System.Linq;
using System.Text;
namespace GLSLText2HLSLText.TextType;

/// <summary>
/// 表示 HLSL 参数信息。
/// </summary>
public sealed class HlslParameter
{
    /// <summary>参数方向，in 或 out。</summary>
    public string Direction { get; set; } = "in";
    /// <summary>HLSL 类型，如 float2、float4。</summary>
    public string Type { get; set; } = "float";
    /// <summary>参数名称。</summary>
    public string Name { get; set; } = string.Empty;
    /// <summary>参数语义，如 POSITION、COLOR0。</summary>
    public string? Semantic { get; set; }
}

/// <summary>
/// 表示 HLSL 方法信息。
/// </summary>
public sealed class HlslMethod
{
    /// <summary>返回类型，如 void、float4。</summary>
    public string ReturnType { get; set; } = "void";
    /// <summary>方法名称。</summary>
    public string Name { get; set; } = string.Empty;
    /// <summary>返回语义，如 COLOR、SV_Target。</summary>
    public string? ReturnSemantic { get; set; }
    /// <summary>从 GLSL out 参数转换得到的返回变量名，用于将赋值替换为 return。</summary>
    public string? OutVariableName { get; set; }
    /// <summary>参数集合。</summary>
    public List<HlslParameter> Parameters { get; } = [];
    /// <summary>方法体令牌。</summary>
    public List<string> BodyTokens { get; } = [];

    /// <summary>
    /// 生成 HLSL 方法代码。
    /// </summary>
    /// <returns>方法的 HLSL 源码。</returns>
    public string GenerateCode()
    {
        var sig = $"{ReturnType} {Name}(" +
                  string.Join(", ", Parameters.Select(RenderParameter)) +
                  ")";
        if (ReturnSemantic is not null && ReturnSemantic.Length > 0) {
            sig += $" : {ReturnSemantic}";
        }
        if (BodyTokens.Count == 0) {
            return sig + ";";
        }
        SetOutVarContext();
        var body = ReconstructBody(BodyTokens);
        return sig + "\n" + body;
    }

    private static string RenderParameter(HlslParameter p)
    {
        var semantic = p.Semantic ?? (p.Direction == "out" ? "COLOR" : "SV_Position");
        if (string.Equals(p.Direction, "out", System.StringComparison.OrdinalIgnoreCase) ||
            string.Equals(p.Direction, "inout", System.StringComparison.OrdinalIgnoreCase)) {
            return $"{p.Direction} {p.Type} {p.Name} : {semantic}";
        }
        return $"{p.Type} {p.Name} : {semantic}";
    }

    /// <summary>
    /// 将令牌重建为方法体文本。
    /// </summary>
    /// <param name="tokens">方法体令牌。</param>
    /// <returns>重建的文本。</returns>
    private static string ReconstructBody(List<string> tokens)
    {
        var sb = new StringBuilder();
        int i = 0;
        int depth = 0;
        bool atLineStart = true;
        while (i < tokens.Count) {
            var t = tokens[i];
            if (t == "{" || t == "}" || t == ";" || t == "(" || t == ")" || t == ",") {
                // fall through to existing branches below
            }
            else if (!string.IsNullOrEmpty(CurrentOutVarName) && t == CurrentOutVarName && i + 1 < tokens.Count && tokens[i + 1] == "=") {
                if (atLineStart) {
                    sb.Append(new string(' ', depth * 4));
                }
                sb.Append("return ");
                i += 2;
                while (i < tokens.Count && tokens[i] != ";") {
                    var tt = tokens[i];
                    char last = sb.Length > 0 ? sb[^1] : '\0';
                    if (last != '(' && last != ',' && last != ' ' && tt != ")" && tt != "(" && tt != ",") {
                        sb.Append(' ');
                    }
                    sb.Append(tt);
                    i++;
                }
                sb.AppendLine(";");
                atLineStart = true;
                i++; // skip ';'
                continue;
            }
            if (t == "{") {
                sb.Append(new string(' ', depth * 4));
                sb.AppendLine("{");
                depth++;
                atLineStart = true;
            } else if (t == "}") {
                depth = Math.Max(0, depth - 1);
                sb.Append(new string(' ', depth * 4));
                sb.AppendLine("}");
                atLineStart = true;
            } else if (t == ";") {
                sb.AppendLine(";");
                atLineStart = true;
            } else if (t == "(" || t == ")" || t == "," ) {
                sb.Append(t);
                atLineStart = false;
            } else {
                if (atLineStart) {
                    sb.Append(new string(' ', depth * 4));
                    atLineStart = false;
                } else {
                    char last = sb.Length > 0 ? sb[^1] : '\0';
                    if (last != '(' && last != ',' && last != ' ') {
                        sb.Append(' ');
                    }
                }
                sb.Append(t);
            }
            i++;
        }
        return sb.ToString();
    }

    private static string? CurrentOutVarName { get; set; }

    public void SetOutVarContext()
    {
        CurrentOutVarName = OutVariableName;
    }
}
