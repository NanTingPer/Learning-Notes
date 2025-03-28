using StructureHelperZh.Systems;
using System.Collections.Generic;
using Terraria.ModLoader;
namespace StructureHelperZh.StructureHelperTranslator
{
	public class StructureHelperTranslator
	{
		private class StructureHelper{}
		[ExtendsFromMod("StructureHelper"), JITWhenModsEnabled("StructureHelper")]
		private class TranslatorLoad : ForceLocalizeSystem<TranslatorLoad, StructureHelper> {}
		public static void LoadTranslator()
		{
			if(ModLoader.TryGetMod("StructureHelper",out var mod))
			{
				#region StructureHelper.Generator
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Generator", "GenerateStructure", new ()
				{
					{"Legacy structures from 1.3 versions of this mod are not supported.","不支持该模组1.3遗留下来的结构"},
					{"Attempted to generate a multistructure '","尝试生成多个建筑'"},
					{"' as a structure. Use GenerateMultistructureRandom or GenerateMultistructureSpecific instead.","' 要生成多个建筑，请改用 GenerateMultistructureRandom 或 GenerateMultistructureSpecific。"},
					{"Attempted to generate a structure out of bounds! ","超出边界！"},
					{" is not a valid position for the structure at "," 不是有效位置!"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Generator", "GenerateMultistructureRandom", new ()
				{
					{"Legacy structures from 1.3 versions of this mod are not supported.","不支持该模组1.3遗留下来的结构"},
					{"Attempted to generate a structure '","尝试生成单个建筑 '"},
					{"' as a multistructure. use GenerateStructure instead.","' 要生成多建筑。请改用 GenerateStructure。"},
					{"Attempted to generate a structure out of bounds! ","生成的建筑物越界！"},
					{" is not a valid position for the structure at "," 不是有效位置 "},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Generator", "GenerateMultistructureSpecific", new ()
				{
					{"Legacy structures from 1.3 versions of this mod are not supported.","不支持该模组1.3遗留下来的结构"},
					{"Attempted to generate structure ","尝试生成 "},
					{" in mutistructure containing "," 包含到多建筑中"},
					{" structures."," 建筑物。"},
					{"Attempted to generate a structure out of bounds! ","越界! "},
					{" is not a valid position for the structure at "," 不是有效位置 "},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Generator", "GetMultistructureDimensions", new ()
				{
					{" in mutistructure containing "," 包含到多建筑中"},
					{" structures."," 建筑物."},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Generator", "Generate", new ()
				{
					{"Corrupt or Invalid structure data.","此建筑数据无效或者已经损坏"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Generator", "LoadFile", new ()
				{
					{"A structure at the path ","路径上的建筑物"},
					{" could not be found."," 找不到."},
				});
				#endregion StructureHelper.Generator


				#region StructureHelper.NullBlockItem
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.NullBlockItem", "SetStaticDefaults", new ()
				{
					{"Null Block","空的物块"},
					{"Use these in a structure to indicate where the generator\n should leave whatever already exists in the world untouched\n ignores walls, use null walls for that :3","在建筑中使用这些来指示生成器的位置\n 请不要修改世界上任何已经存在的东西\n 墙壁会被忽略并替换为空墙 ：3"},
				});
				#endregion StructureHelper.NullBlockItem


				#region StructureHelper.NullWallItem
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.NullWallItem", "SetStaticDefaults", new ()
				{
					{"Null Wall","空的墙"},
					{"Use these in a structure to indicate where the generator\n should leave walls that already exists in the world untouched\n for walls only, use null blocks for other things","在建筑使用这些来指示生成器的位置\n 应保持世界中已存在的墙壁不变\n 只对墙有效，其他的物块会变为空块。"},
				});
				#endregion StructureHelper.NullWallItem


				#region StructureHelper.NullTileAndWallPlacer
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.NullTileAndWallPlacer", "SetStaticDefaults", new ()
				{
					{"Places a null tile and null wall at the same time!","同时放置空块和空墙！"},
				});
				#endregion StructureHelper.NullTileAndWallPlacer


				#region StructureHelper.Saver
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Saver", "SaveToFile", new ()
				{
					{"unnamed structure","未知的建筑"},
					{"Structure saved as ","将建筑保存为 "},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Saver", "SaveMultistructureToFile", new ()
				{
					{"Structure saved as ","将建筑保存为 "},
				});
				#endregion StructureHelper.Saver


				#region StructureHelper.Items.ChestWand
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Items.ChestWand", "SetStaticDefaults", new ()
				{
					{"Chest Wand","宝箱魔棒"},
					{"Right click to open the chest rule menu\nLeft click a chest to set the current rules on it\nRight click a chest with rules to copy them","右键打开箱子规则菜单\n左键一个箱子以设置它的规则为当前规则\n右键带有规则的箱子可以复制规则"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Items.ChestWand", "UseItem", new ()
				{
					{"Copied chest rules from chest at ","复制宝箱规则(Copied chest rules from chest at )"},
					{"Removed chest rules for chest at ","删除宝箱规则"},
					{"Overwritten chest rules for chest at ","覆盖宝箱规则"},
					{"Set chest rules for chest at ","设置为宝箱规则"},
				});
				#endregion StructureHelper.Items.ChestWand


				#region StructureHelper.Items.MultistructureWand
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Items.MultistructureWand", "SetStaticDefaults", new ()
				{
					{"Multistructure Wand","多建筑魔杖"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Items.MultistructureWand", "RightClick", new ()
				{
					{"Not enough structures! If you want to save a single structure, use the normal structure wand instead!","建筑不够大！如果要保存单个建筑，请使用普通结构棒！"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Items.MultistructureWand", "OnConfirmRectangle", new ()
				{
					{"Structures to save: ","要保存的建筑: "},
				});
				#endregion StructureHelper.Items.MultistructureWand


				#region StructureHelper.Items.StructureWand
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Items.StructureWand", "SetStaticDefaults", new ()
				{
					{"Structure Wand","建筑魔棒"},
					{"Select 2 points in the world, then right click to save a structure","选择两个点，然后右键保存被包围的建筑。"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Items.StructureWand", "UseItem", new ()
				{
					{"Select Second Point","选择第二个点(左键)"},
					{"Ready to save! Right click to save this structure...","确定的话，就右键保存此建筑吧！"},
				});
				#endregion StructureHelper.Items.StructureWand


				#region StructureHelper.Items.TestWand
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Items.TestWand", "SetStaticDefaults", new ()
				{
					{"Structure Placer Wand","建筑放置魔棒"},
					{"left click to place the selected structure, right click to open the structure selector","左键放置所选建筑，右键打开建筑选择器"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Items.TestWand", "UseItem", new ()
				{
					{"No structure selected! Right click and select a structure from the menu to generate it.","请选择建筑！请右键打开菜单并选择一个建筑。"},
				});
				#endregion StructureHelper.Items.TestWand


				#region StructureHelper.Helpers.ErrorHelper
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Helpers.ErrorHelper", "GenerateErrorMessage", new ()
				{
					{") <-- has caused an issue with Structure helper! \n\n",") <-- 导致Structure helper出现问题!\n\n"},
					{"\n\nIf you are a player, please report this to the developers of ","\n\n如果你是玩家的话，请将此错误报告给开发者。"},
					{", NOT StructureHelper!",", 不是 StructureHelper!"},
				});
				#endregion StructureHelper.Helpers.ErrorHelper


				#region StructureHelper.Helpers.GUIHelper
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.Helpers.GUIHelper", "WrapString", new ()
				{
					{" NEWBLOCK"," 新物块"},
				});
				#endregion StructureHelper.Helpers.GUIHelper


				#region StructureHelper.GUI.ManualGeneratorMenu
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.GUI.ManualGeneratorMenu", "SafeUpdate", new ()
				{
					{"Place with null tiles: ","放置空物块: "},
					{"If the structure placed manually should have it's null tiles placed or not. Turn this off to get a realistic generation, or on if you want to edit the structure.","你自己放置的物块是否应该被替换为空物块。禁用这个选项可以得到真实的生成，如果要编辑建筑，那就打开这个选项。"},
					{"Reload structures from the folder, use this if you change the folders contents externally and want to see it reflected here.","如果你从外部更改了文件内容，并且想要在这里看到它，请使用此选项。"},
					{"Close this menu","关闭菜单"},
				});
				#endregion StructureHelper.GUI.ManualGeneratorMenu


				#region StructureHelper.GUI.NameConfirmPopup
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.GUI.NameConfirmPopup", "Draw", new ()
				{
					{"Name your creation:","名字:"},
				});
				#endregion StructureHelper.GUI.NameConfirmPopup


				#region StructureHelper.ChestHelper.ChestEntity
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.ChestEntity", "SaveChestRulesFile", new ()
				{
					{"Chest data saved as ","宝箱数据保存为"},
				});
				#endregion StructureHelper.ChestHelper.ChestEntity


				#region StructureHelper.ChestHelper.ChestRule
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.ChestRule", "get_Name", new ()
				{
					{"Unknown Rule","未知的规则"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.ChestRule", "get_Tooltip", new ()
				{
					{"Probably a bug! Report me!","可能是一个Bug! 请报告给开发者!"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.ChestRule", "Deserialize", new ()
				{
					{"Chance","概率"},
				});
				#endregion StructureHelper.ChestHelper.ChestRule


				#region StructureHelper.ChestHelper.ChestRuleChance
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.ChestRuleChance", "get_Name", new ()
				{
					{"Chance Rule","概率规则"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.ChestRuleChance", "get_Tooltip", new ()
				{
					{"Attempts to generate all items in the rule, \nwith a configurable chance to generate each.\nItems are attempted in the order they appear here","按照规则进行生成物品\n按照配置的概率进行生成\n物品按照顺序进行生成"},
				});
				#endregion StructureHelper.ChestHelper.ChestRuleChance


				#region StructureHelper.ChestHelper.ChestRuleGuaranteed
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.ChestRuleGuaranteed", "get_Name", new ()
				{
					{"Guaranteed Rule","固定规则"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.ChestRuleGuaranteed", "get_Tooltip", new ()
				{
					{"Always generates every item in the rule\nItems are generated in the order they appear here","始终生成规则中的每个物品\n物品将按照它们在此处显示的顺序生成"},
				});
				#endregion StructureHelper.ChestHelper.ChestRuleGuaranteed


				#region StructureHelper.ChestHelper.ChestRulePool
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.ChestRulePool", "get_Name", new ()
				{
					{"Pool Rule","池规则"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.ChestRulePool", "get_Tooltip", new ()
				{
					{"Generates a configurable amount of items \nrandomly selected from the rule.\nCan make use of weight.","生成可配置的物品数量\n从规则中随机选择一个规则\n可以利用分配权重"},
				});
				#endregion StructureHelper.ChestHelper.ChestRulePool


				#region StructureHelper.ChestHelper.ChestRulePoolChance
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.ChestRulePoolChance", "get_Name", new ()
				{
					{"Chance + Pool Rule","概率 + 规则池"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.ChestRulePoolChance", "get_Tooltip", new ()
				{
					{"Has a configurable chance to generate a \nconfigurable amount of items randomly \nselected from the rule. \nCan make use of weight.","可以配置生成概率\n可以配置物品的数量(随机)\n从规则里面选择规则\n可以利用分配权重"},
				});
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.ChestRulePoolChance", "Serizlize", new ()
				{
					{"Chance","概率"},
				});
				#endregion StructureHelper.ChestHelper.ChestRulePoolChance


				#region StructureHelper.ChestHelper.GUI.ChestCustomizerState
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.GUI.ChestCustomizerState", "Draw", new ()
				{
					{"New 'Guaranteed' rule","新 '固定' 规则\n首次创建的固定规则有bug(小眼睛无效)，先创建一次然后再创建一个"},
					{"Guaranteed rules will always place every item in them in this chest, in the order they appear in the rule.","‘固定’规则将始终按照它们在规则中出现的顺序将箱子中的所有物品放入箱子中。"},
					{"New 'Chance' rule","新 '概率' 规则"},
					{"Chance rules give all items in the rule a chance to generate. You can customize the value for this chance from 0% to 100%","‘概率’规则为规则中的所有物品提供生成的概率。可以自定义该几率的值，范围为 0% 到 100%"},
					{"New 'Pool' rule","新 '池' 规则"},
					{"Pool rules will select a customizable amount of items from them and place them in the chest.","‘池’规则选择自定义数量的物品,将它们放入箱子中."},
					{"New 'Pool + Chance' rule","新 '池 + 概率' 规则"},
					{"Pool + Chance rules act as a combination of a chance and pool rule -- they have a customizable chance to occur, and if they do, act as a pool rule.","‘池 + 概率’ 规则是两者的组合，具有可自定义的出现概率，如果被触发，使用 池 规则。"},
					{"Close","关闭"},
					{"Close this menu","关闭菜单"},
				});
				#endregion StructureHelper.ChestHelper.GUI.ChestCustomizerState


				#region StructureHelper.ChestHelper.GUI.ChestRuleElement
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.GUI.ChestRuleElement", "Draw", new ()
				{
					{"Left click with an item to add to this rule","单机可以往规则添加物品(在背包拿着物品，然后点这里)"},
					{"Remove rule","删除规则"},
					{"Removes this rule.","删除这个规则"},
					{"Move up","上移"},
					{"Moves this rule earlier in priority","增加此规则的优先级"},
					{"Move down","下移"},
					{"Moves this rule later in priority","降低此规则的优先级"},
					//{"Collapse","折叠"},
					{"Collapse or expand this rule","折叠 / 展开规则"},
				});
				#endregion StructureHelper.ChestHelper.GUI.ChestRuleElement


				#region StructureHelper.ChestHelper.GUI.LootElement
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.GUI.LootElement", "Draw", new ()
				{
					{"Remove item","删除物品"},
					{"Remove this item from the rule","从规则内删除这个物品"},
				});
				#endregion StructureHelper.ChestHelper.GUI.LootElement


				#region StructureHelper.ChestHelper.GUI.NumberSetter
				TranslatorLoad.LocalizeByTypeFullName("StructureHelper.ChestHelper.GUI.NumberSetter", "Draw", new ()
				{
					{"Click to type","点击输入"},
				});
				#endregion StructureHelper.ChestHelper.GUI.NumberSetter


			}
		}
	}
}
