using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Terraria;
using Terraria.ModLoader;

namespace GetItemName
{
	// Please read https://github.com/tModLoader/tModLoader/wiki/Basic-tModLoader-Modding-Guide#mod-skeleton-contents for more information about the various files in a mod.
	public class GetItemName : Mod
	{
	}

	public class PostUpDate : ModSystem
	{
        public override void PostSetupContent()
		{
			StreamWriter itemsStream = File.CreateText(@"D:\1\Items.txt");
			for (int i = 0; i < 99999; i++) {
				try {
					var item = new Item(i);
					if(item.ModItem is null) {
						itemsStream.WriteLine("\"" + item.Name + "\"" + ",");
						itemsStream.Flush();
					}
                } catch {
					break;
				}
			}
			itemsStream.Dispose();
			itemsStream.Close();

            StreamWriter NPCsStream = File.CreateText(@"D:\1\NPCs.txt");
			for(int i = 0; i < 999999; i++) {
				try {
					NPC npc = new NPC();
					npc.SetDefaults(i);
                    if (npc.ModNPC is null) {
                        NPCsStream.WriteLine("\"" + npc.FullName + "\"" + ",");
                        NPCsStream.Flush();
                    }
                } catch {
					break;
				}
			}
			NPCsStream.Dispose();
			NPCsStream.Close();
            base.PostSetupContent();
        }
    }
}
