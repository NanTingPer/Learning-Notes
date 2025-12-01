1. 使用`JsonElement`搜索`Json`元素时，不应手动遍历字典，而是使用`EnumerateArray or EnumerateObject`
2. `JsonDocument`需要进行释放，在方法中返回时，应当返回创建的`Clone`

```cs
public JsonElement LookAndLoad(JsonElement source)
{
    string json = File.ReadAllText(source.GetProperty("fileName").GetString());

    using (JsonDocument doc = JsonDocument.Parse(json))
    {
        return doc.RootElement.Clone();
    }
}
```

