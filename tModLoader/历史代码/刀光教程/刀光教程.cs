using Microsoft.Xna.Framework;
using Terraria.ModLoader;

namespace 刀光教程
{
	// Please read https://github.com/tModLoader/tModLoader/wiki/Basic-tModLoader-Modding-Guide#mod-skeleton-contents for more information about the various files in a mod.
	public class 刀光教程 : Mod
	{

	}

	public static class VMETHOD
	{
		public static Vector2 ToVector2(this Vector3 v3, out float z)
		{
			z = v3.Z;
			return new Vector2 (v3.X, v3.Y);
		}
        public static Vector2 ToVector2(this Vector3 v3) => new Vector2 (v3.X, v3.Y);
        public static Vector3 ToVector3(this Vector2 v2, float z = 1f) => new Vector3 (v2.X, v2.Y, z);
    }
}
