package PoKer.PokerPx;

import ArrlyListOfHashMap.HsahMap;

import javax.print.DocFlavor;
import java.util.*;

public class Mmian
{
    public static void main(String[] args)
    {
        //玩家
        List<Integer> wj1 = new ArrayList<Integer>();
        List<Integer> wj2 = new ArrayList<Integer>();
        List<Integer> wj3 = new ArrayList<Integer>();
        List<Integer> en3 = new ArrayList<Integer>();
        //获取完整扑克牌组
        HashMap<Integer,String> Poker = ProkGjl.Prokje();
        //将键交给List
        ArrayList<Integer> PokerJian = new ArrayList<Integer>();
        Set<Map.Entry<Integer,String>> entries = Poker.entrySet();
        for (int i = 0; i<Poker.size();i++)
        {
            PokerJian.add(i);
        }
        Collections.shuffle(PokerJian);//打散
        //分配
        int fp = 54;
        for (int i = 0;i<Poker.size();i++)
        {
            if (i >= Poker.size()-3)
            {
                en3.add(PokerJian.get(i));
            }
            if (i % 3 == 1)
            {
                wj1.add(PokerJian.get(i));
            }
            if (i % 3 == 2)
            {
                wj2.add(PokerJian.get(i));
            }
            if (i % 3 == 0)
            {
                wj3.add(PokerJian.get(i));
            }
        }

        ProkGjl.PorkHq("wj1",wj1,Poker);
        ProkGjl.PorkHq("wj2",wj2,Poker);
        ProkGjl.PorkHq("wj3",wj3,Poker);

        Collections.sort(wj1);
        Collections.sort(wj2);
        Collections.sort(wj3);
        //有排序
        ProkGjl.PorkHq("wj1",wj1,Poker);
        ProkGjl.PorkHq("wj2",wj2,Poker);
        ProkGjl.PorkHq("wj3",wj3,Poker);
    }
}
