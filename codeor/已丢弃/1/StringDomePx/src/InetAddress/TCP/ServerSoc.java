package InetAddress.TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSoc implements Runnable
{
    private Socket s;
    public ServerSoc(Socket s)
    {
        this.s = s;
    }
    @Override
    public void run()
    {
        try
        {

//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
//            BufferedReader br = new BufferedReader(new FileReader("src/InetAddress/TCP/2.txt"));
            int num = 0;//判断文件重复
            String st_ = "src\\InetAddress\\TCP\\2" + "[" + num + "]" + ".txt";
            File fw = new File(st_);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

            //判断文件重复
            while (true)
            {

                if (fw.exists())
                {
                    num++;
                    st_ = "src\\InetAddress\\TCP\\2" + "[" + num + "]" + ".txt";
                    fw = new File(st_);
                }
                else
                {
                    break;
                }
            }

            //传输数据
            BufferedWriter bw = new BufferedWriter(new FileWriter(fw));
            String st;
            while ((st = br.readLine()) != null)
            {
                bw.write(st);
                bw.newLine();
                bw.flush();
            }
            BufferedWriter bw_ = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            bw_.write("传输完成!");
            bw_.newLine();
            bw_.flush();

            //释放资源
            bw_.close();
            br.close();
            s.close();
            bw.close();
        }
        catch (IOException e)
        {

        }
    }
}
