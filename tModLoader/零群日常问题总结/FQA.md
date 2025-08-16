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

后面的结果

---

> javidpack说他会修这个

```cs
public class AddMissingTileMapEntriesSystem : ModSystem
{
    public override void Load()
    {
        Lang._mapLegendCache[MapHelper.TileToLookup(TileID.BlooMoonMonolith, 0)] = Lang.GetItemName(ItemID.BloodMoonMonolith);
    }
    
    public override void Unload()
    {
        Lang._mapLegendCache[MapHelper.TileToLookup(TileID.BlooMoonMonolith, 0)] = LocalizedText.Empty;
    }
    
}
```



一开始

---

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



## 2025/08/13

### 其他玩家受伤也调用方法问题

> 其他玩家的行为导致的方法调用，从而达到计划之外的内容

在代码中使用`if(Main.myPlayer == player.whoAmI)` 只有本地玩家触发此方法才调用



### 世界生成时如何生成NPC

```cs
public class WorldGenNPCSystem : ModSystem
{
    public override void ModifyWorldGenTasks(List<GenPass> tasks, ref double totalWeight)
    {
        var task = tasks.FindIndex(f => f.Name == "Guide"); //向导
        tasks.Insert(task, new WorldGenNPCSystemGenPass("SpawnDemolitionist", 1.0));
        base.ModifyWorldGenTasks(tasks, ref totalWeight);
    }

    public override void PreWorldGen()
    {
        base.PreWorldGen();
    }

    public class WorldGenNPCSystemGenPass : GenPass
    {
        public WorldGenNPCSystemGenPass(string name, double loadWeight) : base(name, loadWeight)
        {
        }

        protected override void ApplyPass(GenerationProgress progress, GameConfiguration configuration)
        {
            //实际 直接生成就好了
            int entityId = NPC.NewNPC(new EntitySource_WorldGen(), Main.spawnTileX * 16, Main.spawnTileY * 16, NPCID.Demolitionist);
            Main.npc[entityId].homeTileX = Main.spawnTileX;
            Main.npc[entityId].homeTileY = Main.spawnTileY;
            Main.npc[entityId].direction = 1;
            Main.npc[entityId].homeless = true;
        }
    }
}
```





## 2025/08/14

### 关于静态类

IL层面其实是sealed abstract`250343`



### 加载XNB字体

```cs
private static Asset<DynamicSpriteFont> font = ModContent.Request<DynamicSpriteFont>("ModName/华文细黑", AssetRequestMode.ImmediateLoad);
```



## 2025/08/15

### 关于Dust的问题

在`ModDust`的`MidUpdate`里，无论最后是`return true`（不跑原版dust update）还是`return false`（跑原版dust update），都无法正常操作`dust.fadein`
想要像npc或proj的ai[n]一样操作`fadein`，请使用`ModDust`的`Update` `updatetype是不是-1都有问题`

如果想写一个完全属于自己的`Dust`就需要在`Update`返回`false`



## 2025/08/16

### 模组加载顺序问题

> 好的我无法分析

```tex
ModA:
displayName = A
author = a
sortAfter = B

ModB:
displayName = B
author = b
weakReferences = C
side = NoSync

ModC:
displayName = C
author = c
```

```tex
螺线: 
	有B是 -> CBA
	没有B是 -> AC
	A和C都是Both,所以可能会错位
	B换成client也一样
	反正都是可能加载可能不加载的意思
	
依稀:
	这里报错是要求如果你sortafter一个client或者nosync的话，就必须sortafter那个模组的一切引用
	按照螺线说的情况，确实必须这么要求
	
螺线:
	比如我模组本身是内容向，需要Both
	但是我需要用一个UI库
	而UI库这种东西其实Client就行了
	这种时候是不是可以把UI库标NoSync
	这样Both的模组引用的时候服务端加载
	只有Client模组引用的时候就客户端加载就够了
	我觉得NoSync应该这么用
```



### 音频播放的位置移动

> 音频在生成后 更改其位置可以改变播放位置

```cs
public class SoundPlayer : ModPlayer
{
    public static SlotId slotid;
    public override async void OnEnterWorld()
    {
        var ss = new SoundStyle("ModName/路径"); //wav . mp3 . ogg . xnb 等都行 不需要后缀
        await Task.Delay(1000);
        slotid = SoundEngine.PlaySound(ss);
        base.OnEnterWorld();
    }

    public override void PreUpdate()
    {
        if(SoundEngine.TryGetActiveSound(slotid, out var acs)) {
            acs.Position = Main.MouseWorld;
        }
        base.PreUpdate();
    }
}
```

