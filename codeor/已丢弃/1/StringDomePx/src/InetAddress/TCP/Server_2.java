package InetAddress.TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_2
{
    public static void main(String[] args) throws Exception
    {
        ServerSocket ss = new ServerSocket(10088);
        BufferedWriter bw = new BufferedWriter(new FileWriter("src\\InetAddress\\TCP\\2.txt"));
        Socket accept = ss.accept();
        BufferedReader br = new BufferedReader(new InputStreamReader(accept.getInputStream()));
        String st;
        while ((st = br.readLine()) != null)
        {
            bw.write(st);
            bw.newLine();
            bw.flush();
        }
        BufferedWriter bw_ = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()));
        bw_.write("收到了!");
        bw_.newLine();
        bw_.flush();
        ss.close();
        bw.close();
        bw_.close();
    }
}
