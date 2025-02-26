package IO.PrintWrit.Byte;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.PrintStream;

public class Mmian
{
    public static void main(String[] args) throws Exception
    {
        BufferedInputStream bs = new BufferedInputStream(new FileInputStream("C:\\Users\\23759\\Desktop\\1\\StringDomePx\\src\\IO\\CopyMy.java"));
        PrintStream ps = new PrintStream("StringDomePx\\src\\IO\\PrintWrit\\Byte\\CopyMy.java");

        int js;
        byte[] be = new byte[1024];

        while ((js = bs.read(be)) != -1)
        {
            ps.write(be,0,js);
            ps.flush();
        }

        bs.close();
        ps.close();
    }
}
