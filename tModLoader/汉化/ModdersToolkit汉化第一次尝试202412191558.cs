using ModdersToolkitLanguage.Systems;
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using Terraria.ModLoader;
namespace ModdersToolkitLanguage.ModdersToolkitTranslator
{
    public class DamageClassesUI0 : ModSystem
    {
        public static List<Tuple<string,string>> ValueList = new List<Tuple<string,string>>();
        private class ModdersToolkit { }
        [ExtendsFromMod("ModdersToolkit"), JITWhenModsEnabled("ModdersToolkit")]
        private class TranslatorLoad : ForceLocalizeSystem<ModdersToolkit, TranslatorLoad> { }
        public override void PostSetupRecipes()
        {
            List<Tuple<FieldInfo,object>> allField = new List<Tuple<FieldInfo,object>>();
            List<Tuple<PropertyInfo,object>> allProperty = new List<Tuple<PropertyInfo,object>>();

            if (ModLoader.TryGetMod("ModdersToolkit", out var mod))
            {
                Type[] types = mod.Code.GetTypes();

                Type modType = types.FirstOrDefault(f => f.FullName == "ModdersToolkit.ModdersToolkit");
                Type toolType = types.FirstOrDefault(f => f.FullName == "ModdersToolkit.Tools.Tool");
                FieldInfo tools = modType.GetField("tools", BindingFlags.NonPublic | BindingFlags.Static);
                IList toolsValueObject = (IList)tools.GetValue(null);

                foreach (var obj in toolsValueObject)
                {
                    Type type = obj.GetType();
                    PropertyInfo[] propertyinfos = type.GetProperties(BindingFlags.Public | BindingFlags.Instance | BindingFlags.NonPublic);

                    foreach (PropertyInfo item2 in propertyinfos)
                    {
                        ReturnAllPropertyInfo(item2, obj,allField);
                    }

                    FieldInfo[] fields = type.GetFields(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance);

                    foreach (FieldInfo item2 in fields)
                    {
                        ReturnAllFieldInfo(item2, obj,allProperty);
                    }

                }

                foreach(var item in allField)
                {
                    ReturnAllFieldInfo(item.Item1, item.Item2, null);
                }

                foreach(var item in allProperty)
                {
                    ReturnAllPropertyInfo(item.Item1, item.Item2, null);
                }
                


                TranslatorLoad.LocalizeByTypeFullName("ModdersToolkit.Tools.DamageClasses.DamageClassesUI", "OnInitialize", new()
                {
                    {"Damage Classes:","伤害类型:"},
                });
            }
            base.PostSetupRecipes();
        }

        private static void SetFieldInfoString(FieldInfo field,object obj)
        {
            object objec = field.GetValue(obj);
            if(objec.GetType().FullName == typeof(string).FullName)
            {
                string value = StrReplace((string)field.GetValue(obj));
                ValueList.Add(Tuple.Create((string)field.GetValue(obj), value));
                field.SetValue(obj, value);
            }
        }

        private static void SetPropertyInfoString(PropertyInfo property,object obj)
        {
            object objec = property.GetValue(obj);
            if(objec.GetType().FullName == typeof(string).FullName)
            {
                string value = StrReplace((string)property.GetValue(obj));
                ValueList.Add(Tuple.Create((string)property.GetValue(obj), value));
                property.SetValue(obj, value);
            }
        }

        //设置所有字段的属性值
        private static FieldInfo ReturnAllFieldInfo(FieldInfo field, object obj, List<Tuple<PropertyInfo, object>> list = null)
        {
            object value = field.GetValue(obj);
            if (value is not null)
            {
                Type type = value.GetType();
                if (/*type.FullName.Contains("tModLoader") || type.FullName.Contains("Terraria") ||*/ type.FullName.Contains("ModdersToolkit"))
                {
                    FieldInfo[] twoFields = type.GetFields(BindingFlags.Public | BindingFlags.Instance | BindingFlags.NonPublic);
                    PropertyInfo[] propertyInfo = type.GetProperties(BindingFlags.Public | BindingFlags.Instance | BindingFlags.NonPublic);
                    foreach (var item in twoFields)
                    {
                        ReturnAllFieldInfo(item, value, null);
                    }

                    if (list != null)
                    {
                        foreach (var item in propertyInfo)
                        {
                            list.Add(Tuple.Create(item, value));
                        }
                    }
                }
                else
                {
                    SetFieldInfoString(field, obj);
                }
            }
            return null;
        }






        //设置所有属性的属性值
        private static PropertyInfo ReturnAllPropertyInfo(PropertyInfo propertyInfo, object obj, List<Tuple<FieldInfo, object>> list = null)
        {
            object value = propertyInfo.GetValue(obj);
            if (value is not null)
            {
                Type type = value.GetType();
                if (/*type.FullName.Contains("tModLoader") ||*/ type.FullName.Contains("Terraria") || type.FullName.Contains("ModdersToolkit"))
                {
                    PropertyInfo[] twoPropertys = type.GetProperties(BindingFlags.Public | BindingFlags.Instance | BindingFlags.NonPublic);
                    FieldInfo[] fieldinfos = type.GetFields(BindingFlags.Public | BindingFlags.Instance | BindingFlags.NonPublic);
                    foreach (var item in twoPropertys)
                    {
                        ReturnAllPropertyInfo(item, value, list);
                    }

                    if (list != null)
                    {
                        foreach (var item in fieldinfos)
                        {
                            list.Add(Tuple.Create(item, value));
                        }
                    }
                }
                else
                {
                    SetPropertyInfoString(propertyInfo, obj);
                }
            }
            return null;
        }

        private static void TraverseObject(object obj)
        {
            List<Tuple<object, PropertyInfo>> objAndPropertyList = new();
            List<Tuple<object, FieldInfo>> objAndFieldList = new();
            Stack<object> stack = new Stack<object>();
            stack.Push(obj);
            while (stack.Count > 0)
            {
                object objO = stack.Pop();
                Type Otype = objO.GetType();

                PropertyInfo[] properties = Otype.GetProperties(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance);
                FieldInfo[] fields = Otype.GetFields(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance);

                foreach(var item in properties)
                {
                    var value = item.GetValue(objO);
                    if(value != null && item.PropertyType.IsPrimitive && item.PropertyType != typeof(string))
                    {
                        stack.Push(value);
                    }
                    else if(item.PropertyType == typeof(string))
                    {
                        objAndPropertyList.Add(Tuple.Create(objO,item));
                    }

                }

                foreach(var item in fields)
                {
                    var value = item.GetValue(objO);

                    //IsPrimitive判断是为基元类型之一(原始类型 例如 int 等)
                    if(value != null && item.FieldType.IsPrimitive && item.FieldType != typeof(string))
                    { 
                        stack.Push(value);
                    }
                    else if(item.FieldType == typeof(string))
                    {
                        objAndFieldList.Add(Tuple.Create(objO, item));
                    }

                }

            }

            foreach (var item in objAndFieldList)
            {
                object obj2 = item.Item1;
                FieldInfo field = item.Item2;
                string primStr = (string)field.GetValue(obj2);
                field.SetValue(obj,StrReplace(primStr));
            }

            foreach (var item in objAndPropertyList)
            {
                object obj2 = item.Item1;
                PropertyInfo property = item.Item2;
                string primStr = (string)property.GetValue(obj2);
                property.SetValue(obj,StrReplace(primStr));
            }

        }



        private static string StrReplace(string str)
        {
            switch (str)
            {
                case "Tile Selector":
                    return "选择方块";
                case "Open External Editor":
                    return "打开外部编辑器";
                case "Execute External Code":
                    return "执行外部代码";
                case "clear":
                    return "清除";
                case "reset":
                    return "重置";
                case "UI:":
                    return "UI:";
                case "Tweak":
                    return "调整";
                case "Learn":
                    return "了解";
                case "Example UIText":
                    return "示例UIText";
                case "Example UITextPanel":
                    return "示例 UITextPanel";
                case "UI Playground:":
                    return "用户操作界面:";
                case "Panel Padding:":
                    return "填充面板:";
                case "Show UIText":
                    return "显示UIText";
                case "HAlign:":
                    return "水平排列:";
                case "VAlign:":
                    return "垂直排列:";
                case "LeftPixels:":
                    return "左边大小:";
                case "LeftPercent:":
                    return "左边百分比:";
                case "TopPixels:":
                    return "顶端大小:";
                case "TopPercent:":
                    return "顶端占比:";
                case "Show UITextPanel":
                    return "显示UITextPanel";
                case "UITextPanel Text Here":
                    return "UITextPanel的文本";
                case "TextScale:":
                    return "文本比例:";
                case "Padding:":
                    return "填充:";
                case "WidthPixels:":
                    return "宽度(px):";
                case "WidthPercent:":
                    return "宽度(%):";
                case "HeightPixels:":
                    return "高度(px):";
                case "HeightPercent:":
                    return "高度(%):";
                case "Show UIImageButton":
                    return "显示 UIImageButton";
                case "Tweak UI:":
                    return "调整UI:";
                case "UI Selector":
                    return "UI选中项";
                case "Clear UI Selections":
                    return "清除UI选中";
                case "Copy code of selected element to clipboard":
                    return "将选择的元素代码复制到剪切板";
                case "Highlight Selected":
                    return "高亮显示选中项";
                case "Controls the highlight drawing for selected UIElements":
                    return "使用高亮绘图控制所选择的UIElement";
                case "UI Playground Extras:":
                    return "UI Playground附加功能:";
                case "Draw All UIElement Dimensions":
                    return "绘制所有UIElement层(Dimensions)";
                case "This will draw outer dimensions for Elements in your mod.":
                    return "为你Mod中的Elements绘制外部尺寸。";
                case "Color:":
                    return "颜色(Color):";
                case "Randomize Color":
                    return "随机颜色";
                case "This overrides the above Color and can make overlapping issues easier to notice.":
                    return "覆盖上面的Color，以至于显示出重叠";
                case "Draw All Depth:":
                    return "绘制所有深度(Depth)图:";
                case "Draw with Parallax":
                    return "使用视差(Parallax)绘制";
                case "This will offset UIElements to help visualize their hierarchy":
                    return "偏移UIElements来帮助可视化层次结构";
                case "ParallaxX:":
                    return "视差(Parallax)X:";
                case "ParallaxY:":
                    return "视差(Parallax)Y:";
                case "Append UIElement to Selected:":
                    return "将UIElement添加到选定项:";
                case " does not have an empty constructor, cannot instantiate.":
                    return "没有空构造函数,无法实例化。";
                case "Elements":
                    return "(Elements)元素";
                case "->Parent":
                    return "->(Parent)父";
                case "PaddingLeft":
                    return "左内边距";
                case "PaddingRight":
                    return "右内边距";
                case "PaddingTop":
                    return "顶部填充";
                case "PaddingBottom":
                    return "底部填充";
                case "Textures:":
                    return "贴图:";
                case "Watch ModSources":
                    return "查看模组源码";
                case "Automatically Watch ModSources for Changes":
                    return "自动检测模组源码的更改";
                case "Filter:":
                    return "过滤:";
                case "Search":
                    return "搜索";
                case "Current: None selected":
                    return "当前 : 未选择任何项目";
                case "Open Exported Image in Default Editor":
                    return "在默认的编辑器中打开导出的图像";
                case "No texture selected":
                    return "未选择纹理(贴图)";
                case "Texture updated from watch file.":
                    return "从指定文件更新了纹理(贴图)";
                case "Click to toggle NPC Spawn Tool":
                    return "点击切换到NPC生成工具";
                case "spawnRate":
                    return "生成速度";
                case "maxSpawns":
                    return "最大生成";
                case "NPC Spawns:":
                    return "生成NPC:";
                case "Calculate":
                    return "计算(Calculate)";
                case "Sounds:":
                    return "音频:";
                case "Volume:":
                    return "音量:";
                case "Pitch:":
                    return "高音:";
                case "Pitch Variance:":
                    return "高音变化:";
                case "Max Instances:":
                    return "最大实例数量:";
                case "Play Selected Sound":
                    return "播放指定的音频";
                case "Play Next Sound":
                    return "播放下一个音频";
                case "Copy code to clipboard":
                    return "将代码复制到剪切板";
                case "Log Sounds":
                    return "音频日志";
                case "Log Sound Styles and Types":
                    return "音频的日志样式和类型";
                case " (unloaded)":
                    return " (已卸载)";
                case "No sounds found in ":
                    return "没有找到音频";
                case "Mod Sources:":
                    return "Mod源码:";
                case "Watch Mod Sources":
                    return "查看Mod源(源码?)";
                case "Automatically Compile Mod Sources Shaders when Changed on Disk":
                    return "shader文件发生更改时，自动编译Mod源shader";
                case "Test Shader:":
                    return "测试 Shader:";
                case "Armor":
                    return "盔甲(Armor)";
                case "Generate an Armor Shader":
                    return "创建盔甲(Armor)shader";
                case "Screen":
                    return "屏幕";
                case "Generate a Screen Shader":
                    return "创建屏幕shader";
                case "Edit Test Shader":
                    return "编辑测试shader";
                case "Compile Test Shader":
                    return "编译测试shader";
                case "Watch Test Shader File":
                    return "查看测试的shader文件";
                case "Automatically Compile Test Shader when Changed on Disk":
                    return "测试shader文件发生更改，自动编译shader";
                case "Force Shader Active":
                    return "强制shader处于启动状态";
                case "Automatically enable the last compiled screen shader.":
                    return "自动启用上次编译的屏幕shader.";
                case "uIntensity:":
                    return "光强度(uIntensity):";
                case "Could not open ":
                    return "无法打开";
                case ", check that you have a text editor associated with .fx files.":
                    return ", 请检查是否具有与.fx文件关联的文本编辑器.";
                case "Unpacking fxbuilder.exe and Pipeline dlls":
                    return "解压缩fxbuilder.exe和Pipeline dll";
                case "Unpacking complete":
                    return "解包完成";
                case "No existing bound shader entries found":
                    return "未找到已有的shader使用";
                case " existing bound shader entries updated":
                    return " 更改了已有的shader绑定";
                case "Wear Acid Dye to test.":
                    return "佩戴酸性染料进行测试.";
                case "Failure":
                    return "失败";
                case "No File":
                    return "没有文件";
                case "File Not Found":
                    return "文件未找到";
                case "Shader source changed for file ":
                    return "文件的Shader源已更改";
                case ", attempting to compile.":
                    return ", 尝试编译.";
                case "Compile":
                    return "编译";
                case "Type c# code. Empty line to finish.":
                    return "输入C#代码。空行完成(Empty line to finish.)";
                case "using ":
                    return "引用";
                case "Static field ":
                    return "静态字段";
                case " not found":
                    return " 未找到";
                case "Quick Tweak:":
                    return "快速调整:";
                case "Private":
                    return "私有";
                case "Show Private fields":
                    return "显示私有字段";
                case "Clear Tweaks":
                    return "清除更改";
                case "Edit current mount":
                    return "编辑当前坐骑";
                case "Edit ModPlayers":
                    return "编辑 ModPlayers";
                case "Nearest NPC":
                    return "最近的NPC";
                case "Remove":
                    return "删除";
                case "Projectiles:":
                    return "弹幕:";
                case "Clear Projectiles":
                    return "清除弹幕";
                case "SpeedX:":
                    return "X轴速度:";
                case "SpeedY:":
                    return "Y轴速度:";
                case "ai0:":
                    return "a1:0";
                case "ai1:":
                    return "ai1:";
                case "Knockback:":
                    return "击退力:";
                case "Damage:":
                    return "攻击力:";
                case "AIStyle:":
                    return "AI类型:";
                case "drawOffsetX:":
                    return "绘制偏移 X:";
                case "drawOriginOffsetX:":
                    return "绘制原点偏移 X:";
                case "drawOriginOffsetY:":
                    return "绘制原点偏移 Y:";
                case "Hostile":
                    return "不友好的";
                case "Friendly":
                    return "友好的";
                case "Pause":
                    return "暂停";
                case "Pauses All Projectiles (Prevent AI from running)":
                    return "暂停全部的弹幕(阻止AI运行)";
                case "Step":
                    return "Step(步)";
                case "Freeze":
                    return "暂停";
                case "Zero out velocity during PreAI for All Projectiles":
                    return "在PreAI期间，所有的弹幕速度归零";
                case "Spawn projectile ":
                    return "生成弹幕";
                case "Player Layer:":
                    return "玩家层:";
                case "Reset":
                    return "重置";
                case "SourceDamage: ":
                    return "目标攻击力:";
                case "Defense: ":
                    return "防御力:";
                case "ArmorPenetration: ":
                    return "盔甲穿透:";
                case "Damage: ":
                    return "伤害:";
                case "   knockback: ":
                    return "   击退力:";
                case "   hitDirection: ":
                    return "   命中方向:";
                case "TileTypeData = ":
                    return "方块类型数据 = ";
                case ", WallTypeData = ":
                    return ", 墙类型数据 = ";
                case ", TileWallWireStateData = ":
                    return ", 方块网格状态数据:";
                case "Miscellaneous:":
                    return "杂项:";
                case "NPC Info":
                    return "NPC 信息";
                case "Show NPC ai values and other variables.":
                    return "显示NPC AI 和其他变量";
                case "Projectile Info":
                    return "弹幕信息";
                case "Show Projectile ai values and other variables.":
                    return "显示弹幕的 AI 和其他变量";
                case "Lock Projectile Info":
                    return "锁定弹幕信息";
                case "Lock display to center of screen.":
                    return "将显示内容锁定到屏幕中央";
                case "Tile Grid":
                    return "方块网格";
                case "Show grid lines between tiles.":
                    return "显示方块之间的间隔线条(网格)";
                case "Calculate Chunk Size":
                    return "计算数据块大小";
                case "Generate Town Sprite (WIP)":
                    return "生成城镇居民[Sprite](WIP)";
                case "Collision Circle":
                    return "碰撞圈";
                case "Show a circle of Collision.CanHit":
                    return "显示一个Collision.CanHit的碰撞圈";
                case "CanHitLine":
                    return "能命中线(CanHitLine)";
                case "Show a visualization of Collision.CanHitLine from Player.Center to Mouse":
                    return "显示一个从 Player.Center 到 鼠标处的 Collision.CanHitLine线条(碰撞线)";
                case "Take World Snapshot (WIP)":
                    return "创建世界快照(WIP)";
                case "Restore World Snapshot (WIP)":
                    return "从快照中还原世界(WIP)";
                case "Click to toggle NPC Loot Tool":
                    return "点击切换到NPC掉落物工具";
                case "NPC Loot:":
                    return "NPC掉落物:";
                case "Click to toggle Item Tool":
                    return "点击切换到物品工具";
                case "globalItems":
                    return "全局物品:";
                case "Items:":
                    return "物品:";
                case "Fields":
                    return "字段";
                case "Other":
                    return "其他";
                case "Override HoldoutOffset":
                    return "重写挥舞偏移";
                case "Affects non-staff useStyle 5 weapons. (Guns)":
                    return "影响非玩家的五种枪的类型";
                case "Holdout X:":
                    return "大小 X(挥舞时):";
                case "Holdout Y:":
                    return "大小 Y((挥舞时)):";
                case "Scale:":
                    return "缩放比例:";
                case "Print ItemInfo":
                    return "打印物品信息";
                case "UseStyle:":
                    return "使用类型:";
                case "UseTime:":
                    return "使用时间:";
                case "UseAnimation:":
                    return "使用动画:";
                case "ReuseDelay:":
                    return "使用速度:";
                case "Pick:":
                    return "镐力:";
                case "Axe:":
                    return "斧力:";
                case "Hammer:":
                    return "锤力:";
                case "Shoot:":
                    return "弹幕:";
                case "ShootSpeed:":
                    return "弹幕速度:";
                case "KnockBack:":
                    return "击退力:";
                case "UseAmmo:":
                    return "使用弹药:";
                case "Crit:":
                    return "暴击率:";
                case "Rare:":
                    return "稀有度:";
                case "Value:":
                    return "值:";
                case "AutoReuse":
                    return "自动使用(自动挥舞)";
                case "UseTurn":
                    return "使用 Turn";
                case "SetDefaults":
                    return "设置默认值";
                case "Prefix":
                    return "前缀";
                case "Interface Layer:":
                    return "接口层:";
                case "ModdersToolkit: Tools":
                    return "ModdersToolkit:工具";
                case "Vanilla: Resource Bars":
                    return "原版: 物品栏(Resource Bars)";
                case "Vanilla: Info Accessories Bar":
                    return "原版: 饰品栏信息";
                case "Hitboxes:":
                    return "碰撞箱:";
                case "Keep Showing":
                    return "继续显示";
                case "Show hitboxes after leaving this tool":
                    return "工具关闭后仍然显示碰撞箱";
                case "Player Position":
                    return "玩家位置";
                case "Player Velocity":
                    return "玩家速度";
                case "Projectile Velocity":
                    return "弹幕速度";
                case "Player Melee":
                    return "玩家近战";
                case "NPC":
                    return "NPC";
                case "Projectile C":
                    return "弹幕 C";
                case "Collision: Some projectiles have special collision logic":
                    return "碰撞体: 一些弹幕具有特殊的碰撞逻辑";
                case "Projectile D":
                    return "弹幕 D";
                case "Damage: Hitboxes modified by ModifyDamageHitbox":
                    return "伤害: 修改碰撞箱(ModifyDamageHitbox)";
                case "TE Position":
                    return "放置方块(TE Position):";
                case "World Items":
                    return "世界物品";
                case "Click to toggle Fishing Tool":
                    return "点击切换到钓鱼工具";
                case "Player needs to hold a fishing pole":
                    return "玩家需要拿着鱼竿";
                case "Player needs to be within atleast ":
                    return "玩家必须在里面";
                case " tiles below or above the surface of a pool of liquid":
                    return "液体上面或者下面的方块";
                case "Fishing Catches:":
                    return "捕获(Fishing Catches):";
                case "choose dust ":
                    return "选择粒子";
                case "Dust:":
                    return "粒子:";
                case "Reset Tool":
                    return "重置设置";
                case "No Gravity":
                    return "无视重力";
                case "No Light":
                    return "不发光";
                case "Show Spawn Rectangle":
                    return "可视化";
                case "Width:":
                    return "宽度:";
                case "Height:":
                    return "高度:";
                case "Type:":
                    return "类型:";
                case "Use Custom Color":
                    return "使用自定义颜色";
                case "Alpha:":
                    return "透明度:";
                case "Shader:":
                    return "着色器:";
                case "FadeIn:":
                    return "淡入";
                case "Spawn%:":
                    return "生成百分比:";
                case "NewDust":
                    return "创建粒子";
                case "Damage Classes:":
                    return "伤害类型:";
                case "Click to toggle C# REPL":
                    return "点击切换到 C# REPL";
                case "Click to toggle Hitboxes Tool":
                    return "点击切换到碰撞箱工具";
                case "Click to toggle Dust Tool":
                    return "点击切换到粒子工具";
                case "Click to toggle Projectile Tool":
                    return "点击切换到弹幕工具";
                case "Click to toggle Player Layer Tool":
                    return "点击切换到玩家层工具";
                case "Click to toggle Damage Classes Tool":
                    return "点击切换到伤害类型工具";
                case "Click to toggle Interface Layer Tool":
                    return "点击切换到接口层工具";
                case "Click to toggle Texture Tool":
                    return "点击切换到贴图(Texture)工具";
                case "Click to toggle Sound Tool":
                    return "点击切换到音频工具";
                case "Click to toggle Shader Tool":
                    return "点击切换到Shader工具";
                case "Click to toggle UI Playground Tool":
                    return "点击切换到UI操作工具";
                case "Click to toggle Quick Tweak Tool":
                    return "点击切换到快速调整工具";
                case "Click to toggle Miscellaneous Tool":
                    return "点击切换到杂项工具";






                default:
                    return str;
            }

        }
    }
}
