package Thread.MaiPiao;

import javax.swing.*;
import javax.swing.plaf.PanelUI;

public class Runnable_ implements Runnable
{
    private int num = 100;
    private Object oj = new Object();
    @Override
    public void run()
    {
        for (int i = 0; i < 100; i++)
        {
            Run();
//            synchronized (oj)
//            {
//                if (num <= 0)
//                {
//                    break;
//                }
//                else
//                {
//                    num -= 1;
//                    System.out.println(Thread.currentThread().getName() + " 当前剩余 : "  + num);
//                }
//                try
//                {
//                    Thread.sleep(100);
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//            }
//
//        }

        }
    }

    public synchronized void Run()
    {
        if (num >0)
        {
            num -= 1;
            System.out.println(Thread.currentThread().getName() + " 当前剩余 : "  + num);
        }
        try
        {
            Thread.sleep(100);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
