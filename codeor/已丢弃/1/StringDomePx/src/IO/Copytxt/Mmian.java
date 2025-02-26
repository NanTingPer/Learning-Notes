package IO.Copytxt;

import java.io.*;

public class Mmian
{
    public static void main(String[] args) throws IOException
    {
        //file.getName : 获取文件名字
        File file = new File("StringDomePx\\src\\IO\\复制我.txt");
        File file2 = new File("StringDomePx\\src\\IO\\Copytxt\\" + file.getName());
        //创建字节输入流 即 读取数据
        FileInputStream input = new FileInputStream(file);
        //创建字节输出流 即 写入数据
        FileOutputStream output = new FileOutputStream(file2);

        //存储读取的数据
        int cha;
        while ((cha = input.read()) != -1)
        {
            output.write(cha);
        }

        //释放
        input.close();
        output.close();
    }
}
