package FunctionalInter_._005SUPPLIER_IntMax;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.util.function.Supplier;

public class Mian_
{
    public  static void  main(String[] sags)
    {
         int Max_ = GetMax(()->
         {
             int[] ints_ = new int[]{10,20,30,40,50,100};
             int Max = ints_[0];
             for (int i : ints_)
             {
                 if (Max < i)
                 {
                     Max = i;
                 }
             }
             return  Max;
         }) ;
        System.out.println(Max_);
    }

    private static  int GetMax(Supplier<Integer> e)
    {
        return e.get();
    }
}
