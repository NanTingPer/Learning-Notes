package IO.StringIO_Input;

import java.io.*;

public class Stirng_IO_HC
{
    public static void main(String[] args) throws Exception
    {
        String url = "StringDomePx\\src\\IO\\";
        File file = new File(url + "StringIO_Input\\CopyMy.java");
        System.out.println(file.delete());

        //缓存写入流 ( Output )
        BufferedWriter bw = new BufferedWriter(new FileWriter(url + "StringIO_Input\\CopyMy.java"));
        //缓冲提取流 ( Input )
        BufferedReader br = new BufferedReader(new FileReader(url + "CopyMy.java"));

        char[]  ch = new char[1024];
        int js;
        while((js = br.read(ch)) !=-1)
        {
            bw.write(ch,0,js);
        }

        bw.close();
        br.close();
    }
}
