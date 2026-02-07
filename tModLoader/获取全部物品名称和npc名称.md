```cs
public void Contents(string modname)
{
    StreamWriter itemsStream = File.CreateText(@"D:\1\Items.txt");
    for (int i = 0; i < 99999; i++) {
        try {
            var item = new Item(i);
            if (item.ModItem is ModItem mItem) {
                if (mItem.Mod.Name.Contains(modname)) {
                    //itemsStream.WriteLine("\"" + item.Name + "\"" + ",");
                    itemsStream.WriteLine("\"" + mItem.DisplayName + "\"" + ",");

                    itemsStream.Flush();
                }
            }
        } catch {
            break;
        }
    }
    itemsStream.Dispose();
    itemsStream.Close();

    StreamWriter NPCsStream = File.CreateText(@"D:\1\NPCs.txt");
    for (int i = 0; i < 999999; i++) {
        try {
            NPC npc = new NPC();
            npc.SetDefaults(i);
            if(npc.ModNPC is not null && npc.ModNPC.Mod.Name.Contains(modname)) {
                NPCsStream.WriteLine("\"" + npc.ModNPC.DisplayName.Value/* + "       ģʽ  \""*/ + ",");
                NPCsStream.Flush();
            }
        } catch {
            break;
        }
    }
    NPCsStream.Dispose();
    NPCsStream.Close();
}

```

