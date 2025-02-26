package IO.TxtToArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class Mmian
{
    public static void main(String[] args) throws Exception
    {
        ArrayList<Xs> xsList= new ArrayList<Xs>();
        //创建字符输入缓冲流
        BufferedReader bw = new BufferedReader(new FileReader("StringDomePx\\src\\" +
                "IO\\TxtToArray\\Xs.txt"));
        String xs = "";
        int age;
        String[] xs01;
        while((xs = bw.readLine()) != null)
        {
            xs01 = xs.split(",");
            xsList.add(new Xs(xs01[1],Integer.parseInt(xs01[2]),xs01[0]));
        }
        for(Xs x : xsList)
        {
            System.out.println(x.toString());
        }
    }
}
