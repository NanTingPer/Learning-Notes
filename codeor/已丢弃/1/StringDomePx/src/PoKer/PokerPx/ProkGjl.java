package PoKer.PokerPx;

import ArrlyListOfHashMap.HsahMap;

import javax.imageio.plugins.tiff.ExifInteroperabilityTagSet;
import java.util.*;

public class ProkGjl
{
    public static Set<Integer> px(Set set)
    {

        return set;
    }

    /**
     * 获取一个有顺序的扑克组
     */
    public static final HashMap<Integer,String> Prokje()
    {
        HashMap<Integer,String> map = new HashMap<Integer,String>();
        String[] hse = {"♥","♣","♠","♦"};
        String[] sz = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        int js = 0;
        for (int i = 0; i < sz.length;i++)
        {
            for (int j = 0;j < hse.length;j++)
            {
                map.put(js,hse[j] + sz[i]);
                js++;
            }

        }
//        for (int i =0;i < hse.length;i++)
//        {
//            for (int j = 0 ; j < sz.length ;j++)
//            {
//                map.put(js,hse[i] + sz[j]);
////                if (j == 52)
////                {
////                    js++;
////                    map.put(js,hse[i] + sz[j]);
////                }
//                js++;
//            }
//        }
        map.put(js++,"小王");
        map.put(js++,"大王");
        return map;
    }

    /***
     * 输出/获取玩家的牌
     */
    public static final void PorkHq(String name, List<Integer> list,HashMap<Integer,String > map)
    {
        String name01 = name;
        List<Integer> Lit = list;
        HashMap<Integer,String> mao = map;
        //System.out.println(name01 + " : ");
        String stb = "";
        stb = name01 + " : ";
        for (int i = 0;i<Lit.size();i++)
        {
            if (i == Lit.size()-1)
            {
                //System.out.println(mao.get(Lit.get(i)));
                stb = stb  + mao.get(Lit.get(i));
            }
            else
            {
                //System.out.println(mao.get(Lit.get(i)) + ", ");
                stb = stb + mao.get(Lit.get(i)) + ", ";
            }
        }
        System.out.println(stb + "\n");

    }
}
