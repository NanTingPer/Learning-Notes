package IO.Properties.N_001;

import java.io.*;
import java.util.Properties;

public class Mmian
{
    public static void main(String[] args) throws Exception
    {
        Properties pr = new Properties();
        FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\23759\\Desktop\\1\\StringDomePx\\src\\IO\\Properties\\N_001\\Mmian.txt"));
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File("C:\\Users\\23759\\Desktop\\1\\StringDomePx\\src\\IO\\Properties\\N_001\\Mmian.txt")));
        FileWriter fw = new FileWriter("C:\\Users\\23759\\Desktop\\1\\StringDomePx\\src\\IO\\Properties\\N_001\\Mmian.txt");
        pr.put("001","历史剧");
        pr.store(fos,null);

        FileReader fr = new FileReader("C:\\Users\\23759\\Desktop\\1\\StringDomePx\\src\\IO\\Properties\\N_001\\Mmian.txt");
        Properties pr01 = new Properties();
        pr01.load(fr);
        System.out.println(pr01.get("001"));

    }
}
