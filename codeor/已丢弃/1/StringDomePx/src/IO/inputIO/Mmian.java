package IO.inputIO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Mmian
{
    public static void main(String[] args) throws IOException
    {
        //创建字节输入流 空
        FileInputStream file = null;
//        try
//        {
//            file = new FileInputStream("StringDomePx\\src\\IO\\inputIO\\复制我.txt.txt");
//        } catch (FileNotFoundException e)
//        {
//            throw new RuntimeException(e);
//        }
//        finally
//        {
//
//        }
        //创建
        file = new FileInputStream("StringDomePx\\src\\IO\\inputIO\\复制我.txt.txt");
        int a;
        //读取
        while ((a = file.read()) != -1)
        {
            System.out.print((char) a);
        }


    }
}
