package PoKer.Pokerf;

import java.util.ArrayList;
import java.util.List;

public class fp
{
    /**
     * 输出牌
     */
    public static void fenpei(String name, List list)
    {
        System.out.print(name + "的牌为: ");
        for (int i=0;i<list.size();i++)
        {
            if (i >=list.size()-1)
            {
                System.out.print(list.get(i));
            }
            else
            {
                System.out.print(list.get(i) + ",");
            }

        }
        System.out.println("");
    }

}
