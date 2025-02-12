using Microsoft.Xna.Framework;
using Terraria;
using Terraria.GameContent.Drawing;
using Terraria.ID;
using Terraria.ModLoader;
using static System.Math;

namespace GensokyoWPNACC.TestContent.Dusts.螺旋上升
{
    public class 螺旋上升粒子 : ModDust
    {
        private int timers = 0;
        private Vector2 initPos;
        public override void OnSpawn(Dust dust)
        {
            initPos = dust.position;
            timers = 0;
            base.OnSpawn(dust);
        }

        public override bool Update(Dust dust)
        {
            dust.scale = 1f;
            dust.active = true;
            dust.rotation += 0.01f;
            timers++;
            var temTimers = timers * 0.1f;
            initPos.Y -= 0.5f;
            dust.position = initPos + new Vector2(50f * (float)Cos(temTimers), 10f * (float)Sin(temTimers));
            if (timers > 240)
            {
                //dust.active = false;
            }
            Dust.NewDustPerfect(dust.position, DustID.FireflyHit).noGravity = true;
            return false;
        }
    }
}