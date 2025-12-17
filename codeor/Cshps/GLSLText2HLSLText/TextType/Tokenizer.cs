// Depended by: Text.cs (calls Tokenizer.Tokenize), Program.cs indirectly via Text.ToHlsl
using System.Text.RegularExpressions;
namespace GLSLText2HLSLText.TextType;

/// <summary>
/// 提供分阶段的分词能力：先按代码块分割，再按空白与运算符分割，最后识别关键字。
/// </summary>
public static partial class Tokenizer
{
    /// <summary>
    /// 代码块分隔符集合，作为独立令牌输出。
    /// </summary>
    private static readonly HashSet<string> BlockDelimiters = [";", "{", "}", "(", ")", ","];
    /// <summary>
    /// 运算符集合，作为独立令牌输出。
    /// </summary>
    private static readonly HashSet<string> Operators = ["+", "-", "*", "/", "+=", "=", "-="];

    /// <summary>
    /// 分阶段对输入代码进行分词：代码块符号 → 空白 → 运算符 → 关键字。
    /// 分隔符与运算符、关键字均作为独立令牌输出，空白仅用于分割。
    /// </summary>
    /// <param name="code">输入的 GLSL 文本。</param>
    /// <returns>分词后的令牌列表。</returns>
    public static List<string> Tokenize(string code)
    {
        var result = new List<string>();
        var stageBlocks = SplitKeep(code, BlockRegex());
        foreach (var blockPiece in stageBlocks) {
            if (string.IsNullOrWhiteSpace(blockPiece)) {
                continue;
            }

            if (BlockDelimiters.Contains(blockPiece)) {
                result.Add(blockPiece);
                continue;
            }

            var whites = SplitKeep(blockPiece, WhitespaceRegex());
            foreach (var w in whites) {
                if (string.IsNullOrWhiteSpace(w)) {
                    continue;
                }

                var stageOps = SplitKeep(w, OperatorRegex());
                foreach (var opPiece in stageOps) {
                    if (string.IsNullOrWhiteSpace(opPiece)) {
                        continue;
                    }

                    if (Operators.Contains(opPiece)) {
                        result.Add(opPiece);
                        continue;
                    }

                    var stageKeywords = SplitKeep(opPiece, KeywordRegex());
                    foreach (var kPiece in stageKeywords) {
                        if (string.IsNullOrWhiteSpace(kPiece)) {
                            continue;
                        }
                        result.Add(kPiece);
                    }
                }
            }
        }
        return result;
    }

    /// <summary>
    /// 使用给定正则进行分割并保留分隔符（通过正则捕获组），返回分段数组。
    /// </summary>
    /// <param name="input">输入字符串。</param>
    /// <param name="regex">用于分割的正则表达式。</param>
    /// <returns>包含分隔符的分段数组。</returns>
    private static string[] SplitKeep(string input, Regex regex) => regex.Split(input);

    /// <summary>
    /// 代码块分隔符正则，匹配 <c>;</c>、<c>{}</c>、<c>()</c>、<c>,</c> 并保留为令牌。
    /// </summary>
    [GeneratedRegex("([;{}(),])")]
    private static partial Regex BlockRegex();

    /// <summary>
    /// 运算符正则，匹配复合与基本运算符并保留为令牌。
    /// </summary>
    [GeneratedRegex("(\\*=|/=|\\+=|-=|\\*|/|\\+|-|=)")]
    private static partial Regex OperatorRegex();

    /// <summary>
    /// 关键字正则，匹配 <c>in</c>、<c>out</c>、<c>vec2</c>、<c>vec3</c>、<c>vec4</c>。
    /// </summary>
    [GeneratedRegex("\\b(in|out|vec2|vec3|vec4)\\b")]
    private static partial Regex KeywordRegex();

    /// <summary>
    /// 空白正则，仅用于分割，不作为令牌输出。
    /// </summary>
    [GeneratedRegex("(\\s+)")]
    private static partial Regex WhitespaceRegex();
}
