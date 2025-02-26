package IO.TxtToArray;

public class Xs
{
    private Xs(){}
    private int gae;
    private String name;

    @Override
    public String toString()
    {
        return
                "年龄=" + gae +
                "\t姓名=" + name +
                "\t学号=" + Xh;
    }

    public int getGae()
    {
        return gae;
    }

    public void setGae(int gae)
    {
        this.gae = gae;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getXh()
    {
        return Xh;
    }

    public void setXh(String xh)
    {
        Xh = xh;
    }

    private String Xh;
    public Xs(String name,int gae,  String xh)
    {
        this.gae = gae;
        this.name = name;
        Xh = xh;
    }
    public Xs(int gae, String name, String xh)
    {
        this.gae = gae;
        this.name = name;
        Xh = xh;
    }
}
