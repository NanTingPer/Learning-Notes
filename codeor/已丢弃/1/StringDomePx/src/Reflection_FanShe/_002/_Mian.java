package Reflection_FanShe._002;

import Reflection_FanShe.Student;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class _Mian
{
    public static void main(String[] args) throws Exception
    {
        //获取构造方法
        Class<Student> s1 = Student.class;
        Constructor<Student> con = s1.getDeclaredConstructor();
        Object e = con.newInstance();
        Class<?> e2 = e.getClass();
        Method[]  met = e2.getDeclaredMethods();
        for(Method me : met)
        {
            me.setAccessible(true);
            if(me.getName().equals("setName"))
            {
                System.out.println(true);
                me.invoke(e,"林青霞");
                System.out.println(true);
            }
        }
        System.out.println(e.toString());
    }
}
