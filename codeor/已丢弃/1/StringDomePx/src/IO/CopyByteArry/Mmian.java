package IO.CopyByteArry;

import java.io.FileInputStream;
import java.io.IOException;

public class Mmian
{
    public static void main(String[] args) throws IOException
    {
        FileInputStream file = null;
        try
        {
            file = new FileInputStream("StringDomePx\\src\\IO\\复制我.txt");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        byte[] by =  new byte[1024];
        int len;
        while((len = file.read(by)) != -1)
        {
            System.out.println(new String(by,0,len));
        }

        file.close();
    }
}
