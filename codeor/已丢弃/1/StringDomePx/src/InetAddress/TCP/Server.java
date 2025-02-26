package InetAddress.TCP;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
* 收到消息给出反馈
* */
public class Server
{
    public static void main(String[] args) throws Exception
    {
        //接收数据 输出
        ServerSocket ss = new ServerSocket(10088);
        Socket accept = ss.accept();
        InputStream inputStream = accept.getInputStream();

        while (true)
        {
            byte[] by = new byte[1024];
            int len = inputStream.read(by);
            String st = new String(by, 0, len);
            System.out.println("收到客户端数据 : " + st);
            //发送反馈
            if (st.equals("886"))
            {
                OutputStream outputStream = accept.getOutputStream();
                outputStream.write("886!".getBytes());
                break;
            }
            else
            {
                OutputStream outputStream = accept.getOutputStream();
                outputStream.write("收到数据!".getBytes());
            }
        }
        //释放
        ss.close();

//        ServerSocket ss = new ServerSocket(10088);
//        Socket sock = new Socket(InetAddress.getByName("127.0.0.1"),10087);
//        Socket accept = ss.accept();
//        InputStream inputStream = accept.getInputStream();
//        byte[] Server_By = new byte[1024];
//        int len = inputStream.read(Server_By);
//        String st = new String(Server_By,0,len);
//        System.out.println(st);
//        ss.close();
//        OutputStream os = sock.getOutputStream();
//        byte[] by = "收到了!".getBytes();
//        os.write(by);
//        sock.close();
    }
}
