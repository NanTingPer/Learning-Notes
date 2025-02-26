package IO.PrintWrit.Writ;

import java.io.*;

public class Mmian
{
    public static void main(String[] args) throws Exception
    {
        PrintWriter pw = new PrintWriter("C:\\Users\\23759\\Desktop\\1\\StringDomePx\\src\\IO\\PrintWrit\\Writ\\CopyMy.java");
        InputStreamReader bo = new InputStreamReader(new FileInputStream("C:\\Users\\23759\\Desktop\\1\\StringDomePx\\src\\IO\\CopyMy.java"));

        int cd;//长度
        char[] c = new char[1024];
        while ((cd = bo.read(c)) != -1)
        {
            pw.write(c,0,cd);
            pw.flush();
        }

        pw.close();
        bo.close();
    }
}
