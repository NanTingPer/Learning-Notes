package IO.FileCopyNoZero;

import IO.StringIO_Input.Stirng_IO_HC;

import java.io.*;

public class ToMain
{
    public static void main(String[] args) throws Exception
    {
        File SourcFile = new File("C:\\1-个博");
        File ToFile = new File("StringDomePx\\src\\IO\\FileCopyNoZero");
        ToFile.mkdir();
        CopyFiles(SourcFile,ToFile);
    }

    public static void CopyFiles(File SourcFile,File ToFile) throws Exception
    {
        //如果是文件夹
        if (SourcFile.isDirectory())
        {
            //获取源文件的名字
            String url = SourcFile.getName();
            //创建目的文件
            File file = new File(ToFile + "\\" + url);
            //判断目的文件是否存在
            if(!file.exists())
            {
                file.mkdir();
            }
            //获取源文件下的全部文件
            File[] f = SourcFile.listFiles();
            //遍历 并递归
            for (File e : f)
            {
                CopyFiles(e,file);
            }

        }
        else
        {
            File file = new File(ToFile,SourcFile.getName());
            CopyFile(SourcFile,file);
        }
//        File[] fe = SourcFile.listFiles();
//        File fo;
//        //遍历给出的源地址
//        String url = ToFile.getAbsolutePath();
//        for (File fs : fe)
//        {
//            fo = new File(url + "\\" +  fs.getName());
//            //如果不是文件那么就递归
//            if (!fs.isFile())
//            {
//                fo.mkdir();
//
//                CopyFiles(fs,ToFile);
//            }
//            else
//            {
//                CopyFile(fs,fo);
//            }
//        }
    }

    /***
     * 给一个源地址，一个目的地址 进行复制
     * @param SourcFile
     * @param ToFile
     * @throws Exception
     */

    public static void CopyFile(File SourcFile,File ToFile) throws Exception
    {
        //创建字节输入缓冲流
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(SourcFile));
        //创建字节输出缓冲流
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(ToFile));
        //创建存储
        byte[] by = new byte[1024];
        //创建末尾判断
        int pd;
        //循环复制
        while ((pd = (bis.read(by))) != -1)
        {
            //写入 使用 write(内容,开始,结束) 来约束 防止过写
            bos.write(by,0,by.length);
            //刷新
            bos.flush();
        }
        //释放
        bis.close();
        bos.close();
    }
}
