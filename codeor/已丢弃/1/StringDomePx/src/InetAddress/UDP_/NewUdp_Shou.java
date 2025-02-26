package InetAddress.UDP_;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NewUdp_Shou
{
    public static void main(String[] args) throws Exception
    {

        //创建接收
        DatagramSocket ds = new DatagramSocket(10086,
                InetAddress.getByName("127.0.0.1"));
        while (true)
        {
            DatagramPacket dp = new DatagramPacket(new byte[1024],
                    1024);

            //接收数据 存入dp中
            ds.receive(dp);
            //获取缓冲区数据
            byte[] by = dp.getData();
            //转化为字符串便于输出
            String st = new String(by, 0, dp.getLength());
            System.out.println(st);

        }
        //            ds.close();
    }
}
