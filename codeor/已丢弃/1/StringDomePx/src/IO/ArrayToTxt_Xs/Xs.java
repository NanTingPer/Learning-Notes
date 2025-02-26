package IO.ArrayToTxt_Xs;

import javax.swing.*;

public class Xs
{
    private Xs(){}
    private int gae;

    public int getGae()
    {
        return gae;
    }

    public void setGae(int gae)
    {
        this.gae = gae;
    }

    public String getXh()
    {
        return Xh;
    }

    public void setXh(String xh)
    {
        Xh = xh;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public Xs(int gae, String name, String xh)
    {
        this.gae = gae;
        Xh = xh;
        Name = name;
    }


    public Xs(String name,int gae, String xh)
    {
        this.gae = gae;
        Xh = xh;
        Name = name;
    }

    private String Xh;
    private String Name;


}
