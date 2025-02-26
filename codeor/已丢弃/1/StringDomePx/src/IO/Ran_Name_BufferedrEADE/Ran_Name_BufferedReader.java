package IO.Ran_Name_BufferedrEADE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class Ran_Name_BufferedReader
{
    public static void main(String[] sarg) throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader("StringDomePx" +
                "\\src\\" +
                "IO\\Ran_Name_BufferedrEADE\\Name.txt"));
        ArrayList<String> ar = new ArrayList<String>();
        Random ra = new Random();//创建随机数
        int files;//索引
        String Line = "";
        while ((Line = br.readLine()) != null)
        {
            ar.add(Line);
        }
        br.close();
        Line = "";
        files =  ra.nextInt(0,ar.size());
        Line = ar.get(files);

        int js = 0;//计数
        for (String s : ar)
        {
            System.out.println(js + ": " +s);
            js++;
        }
        System.out.println(files + ": 随机到 :" + Line);

    }
}
