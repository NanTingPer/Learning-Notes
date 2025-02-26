package IO.StringIO_Input;

import java.io.*;

/***
 * 特有:
 *      newLine : 写空行
 *      readLine : 读一行 不包括换行(终止符) 无内容后 null
 */
public class String_Buffered_New
{
    public static void main(String[] args) throws Exception
    {
        String url = "StringDomePx\\src\\IO\\";
        File file = new File(url + "CopyMy01.java");
        file.delete();

        //字符缓冲输入流
        BufferedReader br = new BufferedReader(new FileReader(url + "CopyMy.java"));
        //字符缓冲输出流
        BufferedWriter bw = new BufferedWriter(new FileWriter(url + "StringIO_Input\\CopyMy01.java"));
        String brex = "";
        while ((brex = br.readLine()) != null)
        {
            //写入
            bw.write(brex);
            //写入空行 特有
            bw.newLine();
            bw.flush();
        }
        bw.close();
        br.close();
    }
}
