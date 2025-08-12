## 2025/08/11

### 模组联动中其他模组类型`Type`获取问题

1. 引入目标模组程序集
   ```cs
   1. 使用isual Studio打开模组的项目文件 csproj
   2. 在解决方案资源管理器中右键项目文件
   3. 添加 -> 引用 -> 下方浏览 -> 找到目标程序集 -> 确定 -> 双击项目文件 -> CTRL + S保存
   ```

2. 在代码文件中使用

   > 添加目标模组的物品作为我的物品合成配方

   ```cs
   public override void AddRecipes()
   {
       CreateRecipe()
           //添加天蓝锭, 数量100, 其实与使用自己的物品作为材料是一致的
           //如果显示 AerialiteBar类型未找到,需要检查目标程序集是否被正确引用
           .AddIngredient(ModConten.ItemType<AerialiteBar>(), 100)
           .Register()
           ;
       
   }
   ```






## 2025/08/12

### 无Key物块合成站汉化问题

1. 使用`TileID.Search`，例如血月天塔柱的合成站是无文本的，会被替换为英文

   ```cs
   TileID.Search.Remove(480);
   TileID.Search.Add("血月天塔柱", 480);
   ```

2. 直接删除此合成表所需的制作站，替换为条件

   ```cs
   public class Update : ModSystem
   {
       public static Condition condition =
           new Condition(Language.GetOrRegister(Guid.NewGuid().ToString(), () => "血月天塔柱"), () => Main.player[Main.myPlayer].adjTile[480]);
   
       public override void PostAddRecipes()
       {
           _ = Main.recipe
               .Where(r => r.HasTile(480))
               .Select(r => {
                   r.RemoveTile(480);
                   r.AddCondition(condition);
                   return 1;
               })
               .ToArray()
               ;
           base.PostAddRecipes();
       }
   }
   ```

   

### 如何监听护士治疗等(未试验)

| 类         | 方法               | 回答场景                                      |
| ---------- | ------------------ | --------------------------------------------- |
| ModPlayer  | PostNurseHeal      | 监听护士治疗                                  |
| 搜索breath |                    | 水中窒息                                      |
| ModPlayer  | UpdateBadLifeRegen | 判断lifeRegen值<br>负数就是回复跟不上`debuff` |

