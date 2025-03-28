using System.Linq;
using System.Reflection;
using System.Threading;
using Terraria;
using Terraria.ModLoader;
namespace StructureHelperZh
{
	public class StructureHelperZh : Mod
	{
		public override void Load()
		{
			StructureHelperTranslator.StructureHelperTranslator.LoadTranslator();
			base.Load();
		}
	}
}
