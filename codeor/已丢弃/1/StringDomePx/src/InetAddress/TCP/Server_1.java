package InetAddress.TCP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server_1
{
    public static void main(String[] args) throws Exception
    {
        ServerSocket ss = new ServerSocket(10088);
        Socket accept = ss.accept();
        BufferedReader br = new BufferedReader(new InputStreamReader(accept.getInputStream()));
        BufferedWriter bw = new BufferedWriter(new FileWriter("src\\InetAddress\\TCP\\2.txt"));
        String lin;
        while ((lin = br.readLine()) != null)
        {
            if (lin.equals("886"))
            {
                break;
            }
            bw.write(lin);
            bw.flush();
            bw.newLine();
            bw.flush();
        }

        ss.close();
        br.close();
        bw.close();
    }
}
