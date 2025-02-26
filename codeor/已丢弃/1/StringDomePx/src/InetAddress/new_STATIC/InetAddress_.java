package InetAddress.new_STATIC;

import java.net.InetAddress;

public class InetAddress_
{
    public static void main(String[] args) throws Exception
    {
        //得到对象
        InetAddress address  = InetAddress.getByName("127.0.0.1");
        //使用
        //获取IP地址/主机名
        String host  = address.getHostAddress();
        String hostip = address.getHostName();
        System.out.println(host + hostip);
    }
}
