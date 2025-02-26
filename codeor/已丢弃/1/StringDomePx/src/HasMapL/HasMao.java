package HasMapL;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HasMao
{
    public static void main(String[] args)
    {
        Map<String,String> map = new HashMap<String,String>();
        map.put("001","李家明");
        map.put("002","李家换");
        map.put("003","李宏亮");

        Set<String> set = map.keySet();
        for (String se : set)
        {
            if (se == "002")
            {
                System.out.println(map.get(se));
            }
        }

    }
}
