package IO.FileCopyNoZero;

import java.io.*;

/***
 * 复制多级目录
 */
public class Mmian
{
    public static void main(String[] args)
    {
        File gFile = new File("StringDomePx\\src");
        File[] f = gFile.listFiles();
        for (File file : f)
        {
            if (file.isFile())
            {

            }
        }

        File mdd = new File("StringDomePx\\src\\IO\\FileCopyNoZero" + gFile.getName());
    }

    public static void Copy(File mdd,File ydz) throws Exception
    {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(mdd));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(ydz));
    }

    public static File[] Dg(File file)
    {
        File[] f = file.listFiles();
        for (File file01 : f)
        {
            if (file01.isFile())
            {
                Dg(file01);
            }
            else
            {
                return  f;
            }
        }

        return f;
    }

}
