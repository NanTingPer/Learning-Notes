package IO.StringIO_Input;

import java.io.*;

public class Mmain
{
    public static void main(String[] args) throws Exception
    {

        InputStreamReader isr = new InputStreamReader(new FileInputStream("C:\\Users\\23759\\Desktop\\1\\StringDomePx\\src\\IO\\CopyMy.java"));
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("StringDomePx\\src\\IO\\StringIO_Input\\CopyMy.java"));
        char[] be = new char[1024];
        int e;
        while((e = isr.read(be)) != -1)
        {

            osw.write(be,0,e);
            //osw.flush();
        }

        isr.close();
        osw.close();
    }
}
