using Terraria.ModLoader.IO;
using Terraria.ModLoader;

namespace InfiniteL.Systems
{
    /// <summary>
    /// 克脑，肉山，三王后，世花后，石巨后，月总后，三灾后，神后，龙后，女巫后
    /// <para> 克脑, 肉山, 三王, 世花, 石巨人, 月总 均可以使用原版条件 </para>
    /// <para> 三灾, 神, 龙, 女巫均不可 </para>
    /// </summary>
    public class BossDown : ModSystem
    {
        /// <summary>
        /// 是否击杀三灾
        /// </summary>
        public static bool DownThreeCalamity { get; set; } = false;
        /// <summary>
        /// 是否击杀神吞
        /// </summary>
        public static bool DownGodEngulfed { get; set; } = false;
        /// <summary>
        /// 是否击杀丛林龙
        /// </summary>
        public static bool DownYharon { get; set; } = false;
        private string startChar = "InfiniteL:";
        public override void LoadWorldData(TagCompound tag)
        {
            if (tag.TryGet(startChar + nameof(DownThreeCalamity), out bool value)) DownThreeCalamity = value;
            if (tag.TryGet(startChar + nameof(DownGodEngulfed), out bool value2)) DownGodEngulfed = value;
            if (tag.TryGet(startChar + nameof(DownYharon), out bool value3)) DownYharon = value;
            base.LoadWorldData(tag);
        }

        public override void SaveWorldData(TagCompound tag)
        {
            tag.Set(startChar + nameof(DownThreeCalamity), DownThreeCalamity, true);
            tag.Set(startChar + nameof(DownGodEngulfed), DownGodEngulfed, true);
            tag.Set(startChar + nameof(DownYharon), DownYharon, true);
            base.SaveWorldData(tag);
        }
    }

}
