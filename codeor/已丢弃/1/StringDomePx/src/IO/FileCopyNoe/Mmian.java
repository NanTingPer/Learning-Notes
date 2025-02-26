package IO.FileCopyNoe;

import java.io.*;
import java.text.FieldPosition;

/***
 * 要求:
 *  复制一个目录到当前目录下
 */
public class Mmian
{
    public static void main(String[] args) throws Exception
    {
        File ysFile = new File("StringDomePx\\src\\CopyMy");
        String mdd = "StringDomePx\\src\\IO\\FileCopyNoe\\" + ysFile.getName() + "\\";
        File mdFile = new File("StringDomePx\\src\\IO\\FileCopyNoe\\" + ysFile.getName());
        //判断文件夹是否存在
        if (!mdFile.exists())
        {
            mdFile.mkdir();
        }
        //获取内部数组 文件
        File[] fl =  ysFile.listFiles();
        //遍历并复制
        for (File fl01 : fl)
        {
            File fileMdd = new File(mdd + fl01.getName());
            Copy(fl01.getAbsoluteFile(),fileMdd);
        }

    }

    public static void Copy(File ydz,File Mdd) throws Exception
    {
        //字节输入流
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(ydz.getAbsolutePath()));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(Mdd.getAbsolutePath()));
        byte[] by = new byte[1024];
        int js;
        while ((js = bis.read(by)) != -1)
        {
            bos.write(by,0,js);
            bos.flush();
        }
        bos.close();
        bis.close();
    }
}
