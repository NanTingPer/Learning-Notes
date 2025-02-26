package TreeSetCJPx;

import java.util.Comparator;

public class Xs implements Comparable<Xs>
{
    /**
     * 语文
     */
    private int yw;

    /**
     * 数学
     */
    private int sx;

    /**
     * 英语
     */
    private int yy;

    /**
     * 物理
     */
    private int wl;

    private int age;
    private String name;
    private int zf;
    private Xs(){}

    /**
     *
     * @param yw = 语文
     * @param sx = 数学
     * @param yy = 英语
     * @param wl = 物理
     * @param age = 年龄
     * @param name = 姓名
     *  总分会自动计算
     */
    public Xs(int yw,int sx,int yy,int wl,int age,String name)
    {
        this.age = age;
        this.name = name;
        this.sx = sx;
        this.wl = wl;
        this.yy = yy;
        this.yw = yw;
        this.zf = sx + wl + yy + yw;
    }
    @Override
    public int compareTo(Xs xs)
    {
        if (this.age != xs.age || this.name.compareTo(xs.name) != 0)
        {
            if (this.zf > xs.zf)
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
        else
        {
            return 0;
        }
    }

    @Override
    public String toString()
    {
        return "姓名: " + name +
                " 年龄: " + age +
                " 语文: " + yw +
                " 数学: " + sx +
                " 英语: " + yy +
                " 物理: " + wl +
                " 总分: " + zf;
    }

    public int getYw()
    {
        return yw;
    }

    public void setYw(int yw)
    {
        this.yw = yw;
    }

    public int getSx()
    {
        return sx;
    }

    public void setSx(int sx)
    {
        this.sx = sx;
    }

    public int getYy()
    {
        return yy;
    }

    public void setYy(int yy)
    {
        this.yy = yy;
    }

    public int getWl()
    {
        return wl;
    }

    public void setWl(int wl)
    {
        this.wl = wl;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getZf()
    {
        return zf;
    }

    public void setZf(int zf)
    {
        this.zf = zf;
    }


}
