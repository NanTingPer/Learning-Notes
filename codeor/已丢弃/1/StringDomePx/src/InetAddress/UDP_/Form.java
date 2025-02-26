package InetAddress.UDP_;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Form
{
    public static void main(String[] args) throws Exception
    {
        DatagramSocket ds = new DatagramSocket(10086);

        DatagramPacket dp = new DatagramPacket(new byte[1024],1024);
        ds.receive(dp);//接收数据
        byte[] by = dp.getData();//得到数据
        String st = new String(by,0,dp.getLength());
        System.out.println(st);

        ds.close();
    }
}
