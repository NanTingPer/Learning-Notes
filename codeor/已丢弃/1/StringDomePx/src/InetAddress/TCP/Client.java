package InetAddress.TCP;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/*
* 接收到消息给出反馈
* */
public class Client
{
    public static void main(String[] args) throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //发送数据
        Socket so = new Socket(InetAddress.getByName("127.0.0.1"),10088);
//        OutputStream is =  so.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(so.getOutputStream()));
        InputStream inputStream = so.getInputStream();
        byte[] by_ = new byte[1024];
//        byte[] by = "你好！".getBytes();
        while (true)
        {
            String st = br.readLine();
            byte[] by = st.getBytes();
//            is.write(by);
            bw.write(by.toString());
            bw.newLine();
            bw.flush();
            //接收反馈 inputstream输入流 Out输出流
            int len = inputStream.read(by_);
            System.out.println("收到服务器反馈 内容: " + new String(by_,0,len));


            //判断
            if (st.equals("886"))
            {
                break;
            }
        }
        //释放资源
        so.close();
//        ServerSocket ss = new ServerSocket(10087);
//        Socket so = new Socket(InetAddress.getByName("127.0.0.1"),10088);
//        //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        byte[] by = "你好".getBytes();
//        OutputStream bos =  so.getOutputStream();
//        bos.write(by);
//        so.close();
//        Socket accept = ss.accept();
//        InputStream inputStream = accept.getInputStream();
//        byte[] Server_by = new byte[1024];
//        int len = inputStream.read(Server_by);
//        String st = new String(Server_by,0,len);
//        System.out.println(st);
//        ss.close();
    }
}
