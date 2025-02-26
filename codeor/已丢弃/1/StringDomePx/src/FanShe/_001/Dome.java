package FanShe._001;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Dome
{
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException
    {
         Class<?> c = Class.forName("FanShe._001.Student");
        //获取单个构造函数
         //        c.getConstructor()
        //获取单个构造函数 可以是私有的
//        c.getDeclaredConstructor()
         Constructor<?> con = c.getConstructor(String.class,int.class,String.class);
         Object stu = con.newInstance("李家",12,"男");
        System.out.println(stu.toString());

        System.out.println("_________________________");

        //获取私有构造方法
        Constructor<?> con_01 = c.getDeclaredConstructor(String.class,int.class);
        con_01.setAccessible(true);//取消访问检查
        Object obj = con_01.newInstance("记录",13);
        System.out.println(obj);


    }
    
}
