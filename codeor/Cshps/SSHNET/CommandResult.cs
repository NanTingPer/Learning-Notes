namespace SSHNET;

public class CommandResult
{
    public string Hand { get; set; }
    public string Value { get; private set; }
    private string[] cols = [];
    private Dictionary<string, int> colOffset = [];

    public IEnumerable<string> this[string arg]
    {
        get
        {
            return ColValues(arg);
        }
    }

    /// <summary>
    /// 表头文本的全部列
    /// </summary>
    public string[] Columns
    {
        get
        {
            if(cols.Length == 0) {
                foreach (var item in Hand.Trim().Split(' ', StringSplitOptions.TrimEntries | StringSplitOptions.RemoveEmptyEntries)) {
                    cols = [.. cols, item];
                }
            }
            return cols[..];
        }
    }
    
    /// <summary>
    /// 值的表头值与偏移值
    /// </summary>
    public Dictionary<string, int> ColumnOffset
    {
        get
        {
            if (colOffset.Count == 0) {
                foreach (var hand in Columns) {
                    var index = Index(hand);
                    colOffset[hand] = index;
                }
            }
            return colOffset;
        }
    }
    

    public CommandResult(string value)
    {
        Value = value;
        Hand = Value.Split('\n')[0];
    }

    /// <summary>
    /// 获取<paramref name="target"/>在<see cref="Hand"/>中的索引
    /// </summary>
    /// <param name="target"> 目标字符串 </param>
    /// <returns></returns>
    public int Index(string target)
    {
        return Hand.IndexOf(target, StringComparison.Ordinal);

        for (int i = 0; i < Hand.Length; i++) {
            if (i + target.Length > Hand.Length) continue;
            var range = i + target.Length;
            if (Hand[i..range] == target) {
                return i;
            }
        }
        return -1;
    }

    /// <summary>
    /// 获取此列名的全部值
    /// </summary>
    public IEnumerable<string> ColValues(string colName)
    {
        if (!ColumnOffset.TryGetValue(colName, out int value)) {
            throw new Exception($"不存在的列名! {colName}");
        }

        var lines = Value.Split('\n');
        if (lines.Length <= 1)
            return [];

        //有效行
        var yesLines = lines[1..].Where(f => !string.IsNullOrEmpty(f));

        var orderCOffset = ColumnOffset
            .OrderBy(f => f.Value)
            .ToList();

        var retValue = new List<string>();

        var orderIndex = orderCOffset.FindIndex(v => v.Key == colName);
        if(orderIndex == orderCOffset.Count - 1) { //最后一个
            foreach (var lineValue in yesLines) {
                if (value > lineValue.Length) continue;
                retValue.Add(lineValue[value..].Trim());
            }
            return retValue;
        }

        var sIndex = orderCOffset[orderIndex].Value; //起点
        var endIndex = orderCOffset[orderIndex + 1].Value; //终点

        foreach (var lineValue in yesLines) { //中间
            if (endIndex > lineValue.Length || sIndex > lineValue.Length) continue;
            retValue.Add(lineValue[sIndex ..endIndex].Trim());
        }

        return retValue;
    }
}
