package FunctionalInter_._006CONSUMER;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.function.Consumer;

public class Main_
{
    public static void main(String[] args)
    {
            String[] str = new String[]{"老角色,20","卡里的,30","祁连山,40"};
            for (String s : str)
            {
                PrintName_Age(s,(String st) -> {
                    System.out.print("姓名: " + s.split(",")[0]);
                }, (String str2) -> {
                    System.out.print("\t年龄: " + s.split(",")[1]);
                });
                System.out.println();
            }
    }

    private static void PrintName_Age(String s, Consumer<String> e, Consumer<String> e2)
    {
        e.andThen(e2).accept(s);
    }
}
