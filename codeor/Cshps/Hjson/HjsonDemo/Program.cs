using Hjson;

namespace HjsonDemo
{
    internal class Program
    {
        static void Main(string[] args)
        {
            JsonValue hjson = HjsonValue.Load("C:\\Users\\23759\\Documents\\My Games\\Terraria\\tModLoader\\ModReader\\FargowiltasSouls\\Localization\\zh-Hans\\Mods.FargowiltasSouls.NPCs.hjson");
            //Unable to cast object of type 'System.Collections.Generic.KeyValuePair`2[System.String,Hjson.JsonValue]' 
            
            var list = new List<Tuple<string, string>>();

            

            KeyValue(hjson,"", list);


            Hjson.JsonObject jsonObject = new JsonObject();
            foreach (var item in list)
            {
                jsonObject.Add(item.Item1, item.Item2);
            }

            HjsonValue.Save(jsonObject, "C:\\Users\\23759\\Documents\\My Games\\Terraria\\tModLoader\\ModSources\\Forgotten\\Localization\\en-US_Mods.Forgotten.hjson");

            //foreach (KeyValuePair<string,JsonValue> item in hjson)
            //{
            //    Console.WriteLine(item.Key + "\t" + item.Value.GetType().Name) ;
            //}

            //foreach (var item in list)
            //{
            //    Console.WriteLine(item.Item1 + "\t" + item.Item2);
            //}

            //hjson.Save("C:\\Users\\23759\\Desktop\\新建文件夹\\1.hjson",Stringify.Hjson);

        }

        //递归
        public static List<Tuple<string,string>> KeyValue(JsonValue jsonValue,string key,List<Tuple<string,string>> list)
        {
            if (jsonValue is JsonObject)
            {
                foreach (KeyValuePair<string, JsonValue> item in jsonValue)
                {
                    KeyValue(item.Value, key + "." + item.Key ,list);
                }
            }
            else
            {
                 list.Add(Tuple.Create(key.Substring(1),jsonValue.ToValue().ToString()));
            }

            return list;
        }
    }
}
