package InetAddress.TCP;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Fa
{
    public static void main(String[] args)
    {
        Socket so = new Socket();
        try
        {
           so  = new Socket(InetAddress.getByName("127.0.0.1"),10088);
        } catch (IOException e)
        {
            System.out.println("未处理的错误，或许是 : 链接建立失败");
        }
//        finally
//        {
//            try
//            {
//                so = new Socket(InetAddress.getByName("127.0.0.1"),10088);
//            } catch (IOException e)
//            {
//                throw new RuntimeException(e);
//            }
//        }
        byte[] by = "你好".getBytes();
        try
        {
            so.getOutputStream().write(by);
        } catch (IOException e)
        {
        }

        try
        {
            so.close();
        } catch (IOException e)
        {
            System.out.println("错误");
        }
    }
}
