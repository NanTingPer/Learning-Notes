package rc;

import java.util.ArrayList;
import java.util.Arrays;

public class DomeMain
{
    public static void main(String[] asge)
    {
        String zfc ="12 23 45 22";
        //分隔字符串变为字符数组 传递一个空格
        String[] fg = zfc.split(" ");
        int[] aa = new int[4];
        //转换并赋值
        for (int i = 0; i < fg.length; i ++)
        {
            aa[i] = Integer.parseInt(fg[i]);
        }

        Arrays.sort(aa);//sort排序
        StringBuilder px = new StringBuilder();
        for (int i = 0; i< aa.length;i++)
        {
            if (i != aa.length - 1)
            {
                px.append(aa[i] + " ");
            }
            else
            {
                px.append(aa[i]);
            }
        }
        px.toString();
        System.out.println(px);
        /*
        int cca;
        for(int i =0 ; i<aa.length;i++)
        {
            for (int j = 0;j< aa.length - i ;i++)
            {
                if (aa[i] < aa[j])
                {
                    cca = aa[i];
                    aa[i] = aa[j];
                    aa[j] = cca;
                }
            }
        }
         */

        /*
        ArrayList<Integer> 复制我.txt = new ArrayList<Integer>();
        int cc = 0;
        //charAt提取索引字符
        //System.out.println(zfc.charAt(0));
        for (int i = 0;i < zfc.length();i++)
        {
            if (" ".equals(zfc.charAt(i)))
            {
                for (int j = cc;j < i;i++)
                {
                    cd += zfc.charAt(j);
                }
                cc = i+1;
                复制我.txt.add(Integer.parseInt(cd));
                cd ="";
            }
        }

        for (int i = 0;i< 复制我.txt.size();i++)
        {
            System.out.println(复制我.txt.get(i));
        } */
    }

}
