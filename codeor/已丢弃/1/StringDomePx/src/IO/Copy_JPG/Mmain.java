package IO.Copy_JPG;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/*
* 要求: 将IO目录下的 qiyuCos复制到当前目录
* */
public class Mmain
{
    public static void main(String[] args) throws IOException
    {
        //创建字节输入流(读取)
        FileInputStream fileinput = new FileInputStream("StringDomePx\\src\\IO\\qiyuCos.jpg");

        //创建字节输出流(写入)
        FileOutputStream fileout = new FileOutputStream("StringDomePx\\src\\IO\\Copy_JPG\\qiyuCos.jpg");
        //一个字节数组的读取
        byte[] by = new byte[1024];
        int len;
        while ((len = fileinput.read(by)) != -1)
        {
            fileout.write(by);
        }

        fileinput.close();
        fileout.close();
    }
}
