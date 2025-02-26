package IO.OutIO;

import HasMapL.Stend.Sten;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Mmain
{
    public static void main(String[] args) throws IOException
    {
        FileOutputStream file = new FileOutputStream("StringDomePx\\src\\IO\\OutIO\\复制我.txt.txt");
        String a = "abcd\r\n";
//        file.write(a.getBytes());
        //\r\n Windows

        for (int i = 0; i < 10; i++)
        {
            file.write(a.getBytes());
        }

            //关闭
            file.close();
    }
}
