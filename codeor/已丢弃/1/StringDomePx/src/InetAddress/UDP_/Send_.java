package InetAddress.UDP_;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Send_
{
    public static void main(String[] args) throws IOException
    {
        //创建发送端Socket 对象
        DatagramSocket ds = new DatagramSocket();

        //创建数据 并把数据打包 构造参数 :  byte[] ,let,ip,prot
        byte[] by = "Hello World".getBytes();
//        InetAddress ia = InetAddress.getByName("127.0.0.1")
        DatagramPacket dp = new DatagramPacket(by,by.length,
                InetAddress.getByName("127.0.0.1"),10086);
        //从此套接字发送数据
        ds.send(dp);
        //关闭发送
        ds.close();
    }
}
