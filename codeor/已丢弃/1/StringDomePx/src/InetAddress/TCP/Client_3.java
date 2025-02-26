package InetAddress.TCP;

import java.io.*;
import java.net.Socket;

public class Client_3
{
    public static void main(String[] args) throws Exception
    {
        Socket so = new Socket("127.0.0.1",10088);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(so.getOutputStream()));
        BufferedReader br = new BufferedReader(new FileReader("src\\InetAddress\\TCP\\Fa.java"));
        String st;
        while ((st=br.readLine()) != null)
        {
            bw.write(st);
            bw.newLine();
            bw.flush();
        }
        so.shutdownOutput();

        BufferedReader br_ = new BufferedReader(new InputStreamReader(so.getInputStream()));
        System.out.println(br_.readLine());

        br_.close();
        br.close();
        so.close();

    }
}
