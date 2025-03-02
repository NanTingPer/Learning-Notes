using Microsoft.Xna.Framework;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using Terraria;
using Terraria.GameContent.ItemDropRules;
using Terraria.ID;
using Terraria.ModLoader;
using 东方临时.饰品;

namespace 东方临时
{
    // Please read https://github.com/tModLoader/tModLoader/wiki/Basic-tModLoader-Modding-Guide#mod-skeleton-contents for more information about the various files in a mod.
    public class 东方临时 : Mod
	{
    }

    public partial class 东方临时Player : ModPlayer
	{
        public bool 恋之瞳;

        public override void ResetInfoAccessories()
        {
            //Player.dye;   染料
            //Player.bank;  储蓄罐
            //Player.bank2; 保险箱子
            //Player.armor; 盔甲

            恋之瞳 = false;
            
            base.ResetInfoAccessories();
        }

        public override void ResetEffects()
        {
            base.ResetEffects();
        }
    }

    public partial class 东方临时GNPC : GlobalNPC
    {
        public override void ModifyGlobalLoot(GlobalLoot globalLoot)
        {
            foreach (var item in Mod.Code.GetTypes())
            {
                if(item.GetInterface("AutoDropRule") is Type)
                {
                    if (item.GetConstructor([]) is ConstructorInfo method)
                    {
                        object r = method.Invoke([]);
                        globalLoot.Add((IItemDropRule)r);
                    }
                }
            }

            base.ModifyGlobalLoot(globalLoot);
        }
    }
}
