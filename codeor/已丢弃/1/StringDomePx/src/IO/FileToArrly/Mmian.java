package IO.FileToArrly;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Mmian
{
    public static void main(String[] args) throws Exception
    {
        BufferedReader br = new BufferedReader(new FileReader("StringDomePx\\src\\IO\\复制我.txt"));
        ArrayList<String> ar = new ArrayList<String >();
        String brLine;
        while ((brLine = br.readLine()) != null)
        {
            ar.add(brLine);
        }
        for(String s : ar)
        {
            System.out.println(s);
        }

        br.close();
    }
}
