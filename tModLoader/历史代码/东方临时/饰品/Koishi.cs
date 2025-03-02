using System.Collections.Generic;
using System.Linq;
using System.Reflection.Metadata;
using Terraria;
using Terraria.GameContent.ItemDropRules;
using Terraria.ID;
using Terraria.ModLoader;

namespace 东方临时.饰品
{
    public class Koishi : ModItem
    {
        public class DropRule : AutoDropRule
        {
            public DropRule()
            {
                ChainedRules = new List<IItemDropRuleChainAttempt>();
            }

            public List<IItemDropRuleChainAttempt> ChainedRules { get; }

            //在这里判断什么环境下掉落
            public bool CanDrop(DropAttemptInfo info)
            {
                //血腥地
                return info.player.ZoneCrimson;
            }

            public void ReportDroprates(List<DropRateInfo> drops, DropRateInfoChainFeed ratesInfo)
            {
            }

            public ItemDropAttemptResult TryDroppingItem(DropAttemptInfo info)
            {
                if (Main.rand.Next(0, 20) == 1)
                {
                    CommonCode.DropItem(info, ModContent.ItemType<Koishi>(), 1);
                    return new ItemDropAttemptResult() { State = ItemDropAttemptResultState.Success };
                }
                return new ItemDropAttemptResult() { State = ItemDropAttemptResultState.FailedRandomRoll };
            }
        }

        public override void SetDefaults()
        {
            Item.damage = 0;            //攻击力
            Item.value = 20;            //价格
            Item.rare = ItemRarityID.Gray; //稀有度
            Item.maxStack = 1;          //最大堆叠
            Item.consumable = false;    //是否消耗品
            Item.axe = 0;               //斧
            Item.pick = 0;              //镐
            Item.hammer = 0;            //锤
            Item.accessory = true;      //饰品

            base.SetDefaults();
        }

        public override void SetStaticDefaults()
        {
            base.SetStaticDefaults();
        }

        //饰品效果写这里
        public override void UpdateAccessory(Player player, bool hideVisual)
        {
            player.dashType = 2;    //冲刺
            var modPlayer = player.GetModPlayer<东方临时Player>();
            var meths = typeof(东方临时Player).GetMethods().Where(f => f.Name.StartsWith("东方"));
            foreach (var item in meths)
            {
                item.Invoke(modPlayer, []);
            }

            base.UpdateAccessory(player, hideVisual);
        }

        //背包检查写这里
        public override void UpdateInfoAccessory(Player player)
        {
            base.UpdateInfoAccessory(player);
        }

        //让玩家无法装备此物品
        public override bool CanEquipAccessory(Player player, int slot, bool modded)
        {
            return base.CanEquipAccessory(player, slot, modded);
        }

        //防止被多次装备
        public override bool CanAccessoryBeEquippedWith(Item equippedItem, Item incomingItem, Player player)
        {
            return base.CanAccessoryBeEquippedWith(equippedItem, incomingItem, player);
        }
    }
}
