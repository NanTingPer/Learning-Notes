package HasMapL;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HashMap_2
{
    public static void main(String[] args)
    {
        Map<String,String> map = new HashMap<String,String>();
        map.put("001","李家的");
        map.put("002","林继欢");
        map.put("003","李宏亮");

        Set<Map.Entry<String,String>> SetMap = map.entrySet();

        for (Map.Entry<String,String> mapke : SetMap)
        {
            System.out.println(mapke.getKey() + " , " + map.get(mapke.getKey()));
        }

    }
}
