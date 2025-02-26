package TreeRand;

import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;

public class Main_1
{
    public static void main(String[] args)
    {
        Random random = new Random();
        TreeSet<Integer> treeet = new TreeSet<Integer>(new Comparator<Integer>()
        {
            @Override
            public int compare(Integer int1, Integer int2)
            {
                if (int1 == int2)
                {
                    return 0;
                }
                else if (int1 > int2)
                {
                    return 1;
                }
                else
                {
                    return -1;
                }
            }
        });
        while (treeet.size() < 10)
        {
            treeet.add(random.nextInt(19) + 1);
        }
        for (int in : treeet)
        {
            {
                {
                    {
                        {
                            {
                                {
                                    {
                                        {
                                            {
                                                {
                                                    {
                                                        {
                                                            {
                                                                System.out.print(in + " ");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
