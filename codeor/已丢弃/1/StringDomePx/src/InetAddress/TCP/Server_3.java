package InetAddress.TCP;

import java.lang.Thread;

import java.net.ServerSocket;
import java.net.Socket;

public class Server_3
{
    public static void main(String[] args) throws Exception
    {
        ServerSocket ss = new ServerSocket(10088);
        while (true)
        {
            Socket s = ss.accept();
//            ServerSoc ss_ = new ServerSoc(s);
//            Thread th = new Thread(ss_);
            new Thread(new ServerSoc(s)).start();
        }
    }
}
