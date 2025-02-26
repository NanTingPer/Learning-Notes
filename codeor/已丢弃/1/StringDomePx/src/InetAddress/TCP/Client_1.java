package InetAddress.TCP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client_1
{
    public static void main(String[] args) throws Exception
    {
        //发送数据
        Socket so = new Socket("127.0.0.1",10088);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(so.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String st;
        while ((st = br.readLine())!=null)
        {
            if (st.equals("886"))
            {
                break;
            }
            bw.write(st);
            bw.newLine();//换行
            bw.flush();//刷新
        }
        bw.close();
        br.close();
        so.close();
    }
}
