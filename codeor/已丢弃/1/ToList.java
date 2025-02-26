package you.lx;

import java.util.ArrayList;

public class ToList
{
    public static boolean AddList(ArrayList<XueShen> ListXs,XueShen Xs)
    {
        ListXs.add(Xs);
        System.out.println("添加成功");
        return true;
    }

    public static boolean DelList(ArrayList<XueShen> ListXs,int Xh)
    {
        /*
        for (int i = 0; i < ListXs.size();i++)
        {
            if (ListXs.get(i).getName() == Mz)
            {
                ListXs.remove(i);
                System.out.println("删除成功");
                return true;
            }
        }
         */
        if
        (
                Xh > ListXs.size()
                ||
               Xh < 0
        )
        {
            System.out.println("删除失败");
            return false;
        }
        ListXs.remove(Xh - 1);
        System.out.println("删除成功");
        return  true;
    }

    public  static void AddListFile(ArrayList<XueShen> ListXs)
    {
        for (int i = 0; i<ListXs.size();i++)
        {
            System.out.println(
                    (i+1) + " , " +
                    "姓名: " +
                    ListXs.get(i).getName() +
                    "   " +
                    "年龄: " +
                    ListXs.get(i).getAge()
            );
        }
        System.out.println("已全部列出");
        return;
    }
}
