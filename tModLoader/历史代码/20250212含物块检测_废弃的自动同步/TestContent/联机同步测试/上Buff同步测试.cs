using System;
using System.IO;
using Terraria;
using Terraria.ID;
using Terraria.ModLoader;

namespace GensokyoWPNACC.TestContent.联机同步测试
{
    public class 上Buff同步测试 : ModItem
    {
        public static int OnNPCIndex = -1;
        public override void SetDefaults()
        {
            Item.damage = 20; //攻击力
            Item.value = 20;    //价格
            Item.rare = ItemRarityID.Gray; //稀有度
            Item.maxStack = 1;  //最大堆叠
            Item.useTime = 15;  //使用时间
            Item.useAnimation = 15; //动画时间
            Item.DamageType = DamageClass.Melee;  //伤害类型
            Item.useStyle = ItemUseStyleID.Swing;   //使用样式
            Item.knockBack = 0.2f;  //击退
            Item.useTurn = true;    //是否可转身
            Item.consumable = false;    //是否消耗品
            Item.axe = 0; //斧
            Item.pick = 0;   //镐
            Item.hammer = 0; //锤
            base.SetDefaults();
        }

        public override void OnHitNPC(Player player, NPC target, NPC.HitInfo hit, int damageDone)
        {
            target.AddBuff(BuffID.OnFire, 666);
            OnNPCIndex = target.whoAmI;

            if (Main.netMode != NetmodeID.SinglePlayer)
            {
                var modp = Mod.GetPacket();
                modp.Write(OnNPCIndex);
                modp.Write(BuffID.OnFire);
                modp.Send(-1, player.whoAmI);
            }
            base.OnHitNPC(player, target, hit, damageDone);
        }

        /// <summary>
        /// 给NPC上Buff会自动同步
        /// </summary>
        /// <param name="reader"></param>
        /// <param name="whoAmI"></param>
        public static void 上Buff同步测试方法(BinaryReader reader, int whoAmI)
        {
            NPC npc = Main.npc[reader.ReadInt32()];
            var mnpc = npc.GetGlobalNPC<WPNACCNPC>();

            int buffid = reader.ReadInt32();
            //npc.AddBuff(buffid, 666);
            //Console.WriteLine(1);
            Console.WriteLine("+++++++++++++++++++++++++");
            Console.WriteLine(npc.HasBuff(BuffID.OnFire));
            Console.WriteLine("-----------------------");
        }
    }
}
