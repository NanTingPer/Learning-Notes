package InetAddress.TCP;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Shou
{
    public static void main(String[] args) throws Exception
    {
        //与发送端相同 创建服务端接收数据
        ServerSocket ss = new ServerSocket(10088);
        Socket so = ss.accept();//监听连接 返回Socket对象
        InputStream is = so.getInputStream();//获取输入流
        byte[] by = new byte[1024];
        //read(byte[]) => 将数据给到byte 并返回数据长度
        String st = new String(by,0,is.read(by));
        System.out.println(st);
        ss.close();
    }
}
