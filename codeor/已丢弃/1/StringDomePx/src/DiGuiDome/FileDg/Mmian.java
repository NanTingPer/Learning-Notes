package DiGuiDome.FileDg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/***
 *
 * 递归给定的目录下的文件 给出绝对路径
 *
 * 关于File
 *      getAbsolutePath - 获取文件的绝对路径
 *      isFile - 判断文件是否为 文件
 *      listFiles() - 返回给定目录的 File 数组
 *      createNewFile() - 创建抽象对象给定的  文件
 *      mkdirs - 创建抽象给的目录,多级
 *      mkdir - 同上 不能多级
 *
 */
public class Mmian
{
    public static void main(String[] args)
    {
        File a = new File("C:\\Program Files\\JiJiDown\\Download");
        FileDg(a);
    }

    public static void FileDg(File file)
    {
        File[] files  = file.listFiles();
        if (files != null)
        {
            for (File f : files)
            {
                if (f.isFile())
                {
                    System.out.println(f.getAbsolutePath());
                }
                else
                {
                    FileDg(f);
                }
            }
        }
    }
}
