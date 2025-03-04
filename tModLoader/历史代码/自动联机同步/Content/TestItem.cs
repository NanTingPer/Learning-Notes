using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using ReLogic.Content;
using System.Collections.ObjectModel;
using Terraria;
using Terraria.ID;
using Terraria.ModLoader;

namespace Attri.Content
{
    // This is a basic item template.
    // Please see tModLoader's ExampleMod for every other example:
    // https://github.com/tModLoader/tModLoader/tree/stable/ExampleMod
    public class TestItem : ModItem
    {
        public static Asset<Texture2D> ItemTexture;
        public override void SetDefaults()
        {
            Item.damage = 50;
            Item.DamageType = DamageClass.Melee;
            Item.width = 40;
            Item.height = 40;
            Item.useTime = 20;
            Item.useAnimation = 20;
            Item.useStyle = ItemUseStyleID.Swing;
            Item.knockBack = 6;
            Item.value = Item.buyPrice(silver: 1);
            Item.rare = ItemRarityID.Blue;
            Item.UseSound = SoundID.Item1;
            Item.shoot = ModContent.ProjectileType<TestProje>();
            Item.shootSpeed = 1f;
            Item.autoReuse = true;
        }

        public override void SetStaticDefaults()
        {
            ItemTexture = ModContent.Request<Texture2D>("Attri/Content/山");
            base.SetStaticDefaults();
        }

        public override void AddRecipes()
        {
            Recipe recipe = CreateRecipe();
            recipe.AddIngredient(ItemID.DirtBlock, 10);
            recipe.AddTile(TileID.WorkBenches);
            recipe.Register();
        }

        public override bool PreDrawTooltip(ReadOnlyCollection<TooltipLine> lines, ref int x, ref int y)
        {
            var sb = Main.spriteBatch;
            sb.Draw(ItemTexture.Value, new Vector2(x - 20 ,y - 10), null, Color.White, 0f, Vector2.Zero, 0.1f, SpriteEffects.None, 1f);
            return base.PreDrawTooltip(lines, ref x, ref y);
        }

        public override void PostDrawTooltip(ReadOnlyCollection<DrawableTooltipLine> lines)
        {
            base.PostDrawTooltip(lines);
        }
    }
}
