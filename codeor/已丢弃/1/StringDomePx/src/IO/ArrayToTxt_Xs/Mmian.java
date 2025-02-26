package IO.ArrayToTxt_Xs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/***
 *要求 : 写入ArrayList集合的学生类 格式: 学号,姓名,年龄
 */
public class Mmian
{
    public static void main(String[] args) throws Exception
    {
        ArrayList<Xs> xs = new ArrayList<Xs>();
        xs.add(new Xs(12,"力宏","Iten001"));
        xs.add(new Xs(13,"力后","Iten002"));
        xs.add(new Xs(12,"力花","Iten003"));
        xs.add(new Xs(12,"力林","Iten004"));
        File file = new File("StringDomePx\\src\\" +
                "IO\\ArrayToTxt_Xs\\Xs.txt");
        file.delete();

        BufferedWriter bw = new BufferedWriter(new FileWriter("StringDomePx\\src\\" +
                                                "IO\\ArrayToTxt_Xs\\Xs.txt"));
        for (Xs xs1 : xs)
        {
            bw.write(xs1.getXh() + "," + xs1.getName() + "," + xs1.getGae());
            bw.newLine();
            bw.flush();
        }
        bw.close();
    }
}
