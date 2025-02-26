package ItForHastSet;

import java.util.HashSet;
import java.util.Set;

public class HasSF
{
    public static void main(String[] args)
    {
        Set<Xs> xs = new HashSet<Xs>();
        xs.add(new Xs(12,"李红"));
        xs.add(new Xs(13,"李小"));
        xs.add(new Xs(14,"张雪"));
        xs.add(new Xs(14,"张雪"));
        for(Xs xs1: xs)
        {
            System.out.println(xs1.toString());
        }
    }
}
