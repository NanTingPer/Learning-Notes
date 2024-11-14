package SparkSQL;

import java.io.Serializable;

public class SQLUDAF_UserDefClass implements Serializable
{
    private long cont = 0;
    private long sum = 0;

    public SQLUDAF_UserDefClass()
    {
    }

    public SQLUDAF_UserDefClass(long cont, long sum)
    {
        this.cont = cont;
        this.sum = sum;
    }

    public long getCont()
    {
        return cont;
    }

    public void setCont(long cont)
    {
        this.cont = cont;
    }

    public long getSum()
    {
        return sum;
    }

    public void setSum(long sum)
    {
        this.sum = sum;
    }
}
