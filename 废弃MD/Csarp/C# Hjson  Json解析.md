# C# Hjson / Json解析

## 对象序列化为Json

---

> 这样操作后，会返回该对象序列化为字符串后的值

```cs
var Object = new Object();
string json = JsonSerializer.Serialize(Object);
```



```cs
var jsonObj = new JsonObject();//创建JsonObject对象
foreach (var pd in preLoadDatas) {
    jsonObj.Add(pd.Id.ToString(), JsonValue.Parse(JsonSerializer.Serialize(pd)));
}
var jsonValue = HjsonValue.Parse(jsonObj.ToString());
jsonValue.Save("C:/BACK_Mods.LibTest.hjson", Stringify.Hjson);

```





## Json字符串反序列化为对象

---

> 这样操作后，会得到反序列化后的对象

```cs
//jsonString为上面的json
var jsv = JsonValue.Parse(jsonStirng);
//JsonValue是一个kv对可以从中取Key然后找到对应的值，也可以foreach直接遍历jsv
object obj = jsv["1001"];
var @object = JsonSerializer.Deserialize<Object>(obj.ToString());
```

