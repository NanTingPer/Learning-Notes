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

   



   
