package InetAddress.TCP;

import java.io.*;
import java.net.Socket;

public class Client_2
{
    public static void main(String[] args) throws Exception
    {
        //创建发送Sock
        Socket so = new Socket("127.0.0.1",10088);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(so.getOutputStream()));
        BufferedReader br = new BufferedReader(new FileReader("src\\InetAddress\\TCP\\Fa.java"));
        String st;
        while ((st = br.readLine()) != null)
        {
            bw.write(st);
            bw.newLine();
            bw.flush();//刷新
        }

        //告知对方 我输出完了
        so.shutdownOutput();

        BufferedReader br_ = new BufferedReader(new InputStreamReader(so.getInputStream()));
//        char[] ch = new char[1024];
//        int len = br_.read(ch);
//        String st_ = new String(ch,0,len);
        String st_ = br_.readLine();
        System.out.println("收到回复: " + st_);
        so.close();
        br.close();
    }
}
