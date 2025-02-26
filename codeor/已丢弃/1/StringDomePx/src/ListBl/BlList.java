package ListBl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlList
{
    public static void BlListr(List<XsClass> _xslist)
    {
        Iterator<XsClass> it = _xslist.iterator();
        XsClass xs = new XsClass();
        while (it.hasNext())
        {
            xs.setAge(it.next().getAge());
            xs.setName(it.next().getName());
            System.out.println("姓名: " + xs.getName() + "  年龄: " + xs.getAge());
        }
    }
}
