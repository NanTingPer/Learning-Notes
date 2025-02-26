package Reflection_FanShe._001;
import Reflection_FanShe.Student;

import java.lang.reflect.Method;

//反射获取类
public class _Main
{
    public static void main(String[] args) throws Exception
    {
        Class<Student> s1 = Student.class;
        System.out.println(s1);
        System.out.println("========================================");
        //未知类型 但是继承 Student
        Student student = new Student();
        Class<? extends Student> s2 = student.getClass();
        System.out.println(s1 == s2);

        System.out.println("========================================");
        Class<?> s3 = null;
        try
        {
            s3 = Class.forName("Reflection_FanShe.Student");
        }
        catch (Exception e)
        {
            System.out.println("错误i");
        }
        System.out.println(s3 == s1);

//        Method[] mo = s3.getMethods();
//        for(Method me : mo)
//        {
//            if (me.getName().equals("toString"))
//            {
//                me.
//            }
//        }
    }
}
