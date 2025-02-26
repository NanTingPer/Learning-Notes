package PoKer.Pokerf;

import java.util.ArrayList;
import java.util.Collections;

//发扑克
public class Mmian
{
    public static void main(String[] arsg)
    {
        //扑克堆
        ArrayList<String> Poker = new ArrayList<String>();
        //导入扑克
        String[] pkhs = {"♦","♠","♥","♣"};
        String[] pkds = {"1","2","3","4","5","6","7","8","9","10","J","Q","K","A"};
        for (int i = 0;i<pkhs.length;i++)
        {
            for (int j = 0;j < pkds.length;j++)
            {
                Poker.add(pkhs[i] + pkds[j]);
            }
        }
        Poker.add("大王");Poker.add("小王");
        //打乱
        Collections.shuffle(Poker);
        //创建角色 三人
        ArrayList<String> LinArr = new ArrayList<String>();
        ArrayList<String> LiArr = new ArrayList<String>();
        ArrayList<String> QiangArr = new ArrayList<String>();
        ArrayList<String> pd = new ArrayList<String>();//拍底
        for (int i=0;i<Poker.size();i++)
        {
            if (i >= Poker.size()-3)
            {
                pd.add(Poker.get(i));
            }
            if (i % 3 == 1)
            {
                LinArr.add(Poker.get(i));
            }
            if (i % 3 == 2)
            {
                LiArr.add(Poker.get(i));
            }
            if (i % 3 == 0)
            {
                QiangArr.add(Poker.get(i));
            }
        }
        fp.fenpei("林",LinArr);
        fp.fenpei("李",LiArr);
        fp.fenpei("强",QiangArr);
    }
}
