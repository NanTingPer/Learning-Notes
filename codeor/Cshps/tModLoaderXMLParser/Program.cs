using System.Xml.Linq;
using System.Text;

// 硬编码路径
const string InputXmlPath = @"D:\SteamLibrary\steamapps\common\tModLoader\tModLoader.xml";
const string OutputDirPath = @"D:\SteamLibrary\steamapps\common\tModLoader\outPut\";
const string OutputFilePath = OutputDirPath + "tModLoader_Docs.md";

Directory.CreateDirectory(OutputDirPath);

if (!File.Exists(InputXmlPath)) {
    Console.WriteLine($"❌ 输入文件不存在: {InputXmlPath}");
    return;
}

var doc = XDocument.Load(InputXmlPath);
var members = ParseMembers(doc).Where(m => !string.IsNullOrWhiteSpace(m.Summary)).ToList();

// 按类型分组以便组织内容（但不拆文件）
var grouped = members
    .GroupBy(m => m.TypeName)
    .OrderBy(g => g.Key)
    .ToList();

var sb = new StringBuilder();
sb.AppendLine("# tModLoader 模组开发文档（自动生成）");
sb.AppendLine();
sb.AppendLine("> 由 XML 文档注释解析生成，仅包含有效内容。");
sb.AppendLine();

foreach (var group in grouped) {
    var typeName = group.Key;
    var memberList = group.ToList();

    sb.AppendLine($"## 📦 `{typeName}`");
    sb.AppendLine();

    foreach (var member in memberList.OrderBy(m => m.Kind).ThenBy(m => m.MemberName)) {
        var icon = member.Kind switch
        {
            "T" => "🟦 类型",
            "F" => "🟩 字段",
            "P" => "🟨 属性",
            "M" => "🟥 方法",
            _ => "⬜ 其他"
        };

        sb.AppendLine($"### {icon}: `{member.MemberName}`");
        if (!string.IsNullOrEmpty(member.Summary)) {
            sb.AppendLine(member.Summary);
            sb.AppendLine();
        }

        if (member.Parameters?.Count > 0) {
            sb.AppendLine("**参数:**");
            foreach (var (name, desc) in member.Parameters.OrderBy(p => p.Key)) {
                sb.AppendLine($"- `{name}`: {desc}");
            }
            sb.AppendLine();
        }

        if (!string.IsNullOrEmpty(member.Returns)) {
            sb.AppendLine($"**返回值:** {member.Returns}");
            sb.AppendLine();
        }

        if (!string.IsNullOrEmpty(member.Remarks)) {
            sb.AppendLine("**备注:**");
            sb.AppendLine(member.Remarks);
            sb.AppendLine();
        }

        sb.AppendLine("---");
    }

    sb.AppendLine("<br><br>");
}

await File.WriteAllTextAsync(OutputFilePath, sb.ToString(), Encoding.UTF8);
Console.WriteLine($"✅ 已生成完整文档: {OutputFilePath}");
Console.WriteLine($"📄 共包含 {grouped.Count} 个类型，{members.Count} 个有效成员。");

// ========== 辅助方法 ==========

static IEnumerable<DocMember> ParseMembers(XDocument doc)
{
    foreach (var member in doc.Descendants("member")) {
        var nameAttr = member.Attribute("name")?.Value;
        if (string.IsNullOrEmpty(nameAttr)) continue;

        var parts = nameAttr.Split(':', 2);
        if (parts.Length != 2) continue;

        var kind = parts[0];
        var fullName = parts[1];

        (string typeName, string memberName) = kind switch
        {
            "T" => (fullName, "(Type)"),
            _ => ExtractTypeNameAndMember(fullName)
        };

        var summary = member.Element("summary")?.Value.Trim();
        var returns = member.Element("returns")?.Value.Trim();
        var remarks = member.Element("remarks")?.Value.Trim();

        var parameters = member.Elements("param")
            .ToDictionary(
                p => p.Attribute("name")?.Value ?? "unknown",
                p => p.Value.Trim(),
                StringComparer.OrdinalIgnoreCase);

        yield return new DocMember(
            TypeName: typeName,
            MemberName: memberName,
            Kind: kind,
            Summary: summary,
            Parameters: parameters,
            Returns: returns,
            Remarks: remarks
        );
    }
}

static (string TypeName, string MemberName) ExtractTypeNameAndMember(string fullName)
{
    if (fullName.Contains(".#ctor")) {
        var typeNamea = fullName[..fullName.IndexOf(".#ctor", StringComparison.Ordinal)];
        return (typeNamea, ".ctor");
    }

    var lastDot = fullName.LastIndexOf('.');
    if (lastDot < 0) return (fullName, fullName);

    var typeName = fullName[..lastDot];
    var memberName = fullName[(lastDot + 1)..];

    if (memberName.IndexOf('(') is var idx and >= 0) {
        memberName = memberName[..idx];
    }

    return (typeName, memberName);
}

// ========== 内联 record ==========

record DocMember(
    string TypeName,
    string MemberName,
    string Kind,
    string? Summary,
    Dictionary<string, string> Parameters,
    string? Returns,
    string? Remarks
);