package InetAddress.UDP_;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class NewUdp_Fa
{
    public static void main(String[] args) throws Exception
    {
        //创建发送端
        DatagramSocket ds = new DatagramSocket();
        //创建输入
        //Scanner sc = new Scanner(System.in);
        //字符缓冲输入流 数据来源于键盘输入
        BufferedReader sc = new BufferedReader(new InputStreamReader(System.in));
        //存储数据
        byte[] cy;
        String st;
        //创建打包数据
        DatagramPacket dp = new DatagramPacket(new byte[1024],1024,
                InetAddress.getByName("60.215.128.117"),63085);
        while (true)
        {
            System.out.print("请输入内容 : ");
            st = sc.readLine();
            //cy = sc.next().getBytes();
            cy = st.getBytes();
            dp.setData(cy);
            ds.send(dp);
            if (st.equals("886"))
            {
                break;
            }
        }
        ds.close();
    }
}