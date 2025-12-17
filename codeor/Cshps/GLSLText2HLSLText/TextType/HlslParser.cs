// Depended by: Text.cs (calls HlslParser.ParseMethods), HlslModel.cs
using System.Collections.Generic;
namespace GLSLText2HLSLText.TextType;

/// <summary>
/// 提供从令牌到 HLSL 模型的解析能力。
/// </summary>
public static class HlslParser
{
    /// <summary>
    /// 从令牌解析方法集合，包括签名与方法体。
    /// </summary>
    /// <param name="tokens">输入令牌集合。</param>
    /// <returns>解析得到的 HLSL 方法列表。</returns>
    public static List<HlslMethod> ParseMethods(List<string> tokens)
    {
        var methods = new List<HlslMethod>();
        int i = 0;
        while (i < tokens.Count) {
            if (tokens[i] == "(" && i >= 1) {
                var name = tokens[i - 1];
                var ret = i >= 2 ? MapType(tokens[i - 2]) : "void";
                var method = new HlslMethod { Name = name, ReturnType = ret };

                int j = i + 1;
                var paramTokens = new List<string>();
                while (j < tokens.Count && tokens[j] != ")") {
                    paramTokens.Add(tokens[j]);
                    j++;
                }
                var parameters = ParseParameters(paramTokens);
                method.Parameters.AddRange(parameters);
                var outParam = method.Parameters.Find(p => p.Direction == "out");
                if (outParam is not null) {
                    method.ReturnType = outParam.Type;
                    method.ReturnSemantic = "COLOR";
                    method.OutVariableName = outParam.Name;
                    method.Parameters.Remove(outParam);
                }

                if (j + 1 < tokens.Count && tokens[j + 1] == "{") {
                    int k = j + 1;
                    int depth = 0;
                    while (k < tokens.Count) {
                        var tk = tokens[k];
                        method.BodyTokens.Add(tk);
                        if (tk == "{") depth++;
                        if (tk == "}") {
                            depth--;
                            if (depth == 0) {
                                break;
                            }
                        }
                        k++;
                    }
                    i = k + 1;
                } else {
                    i = j + 1;
                }

                methods.Add(method);
            } else {
                i++;
            }
        }
        return methods;
    }

    /// <summary>
    /// 解析参数列表令牌为 HLSL 参数集合。
    /// </summary>
    /// <param name="paramTokens">参数令牌。</param>
    /// <returns>参数集合。</returns>
    public static List<HlslParameter> ParseParameters(List<string> paramTokens)
    {
        var result = new List<HlslParameter>();
        int i = 0;
        while (i < paramTokens.Count) {
            if (paramTokens[i] == ",") { i++; continue; }
            string direction = "in";
            if (i < paramTokens.Count && (paramTokens[i] == "in" || paramTokens[i] == "out")) {
                direction = paramTokens[i];
                i++;
            }
            if (i >= paramTokens.Count) break;
            var typeGlsl = paramTokens[i];
            i++;
            if (i >= paramTokens.Count) break;
            var name = paramTokens[i];
            i++;
            result.Add(new HlslParameter {
                Direction = direction,
                Type = MapType(typeGlsl),
                Name = name
            });
        }
        return result;
    }

    /// <summary>
    /// 将 GLSL 类型映射到 HLSL 类型。
    /// </summary>
    /// <param name="glslType">GLSL 类型标识。</param>
    /// <returns>对应的 HLSL 类型。</returns>
    public static string MapType(string glslType) => glslType switch
    {
        "vec2" => "float2",
        "vec3" => "float3",
        "vec4" => "float4",
        _ => glslType
    };
}
