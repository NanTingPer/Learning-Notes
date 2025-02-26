package Enum_;

public enum Red_Bule_Green
{
    Red("红") {
                @Override
                public void show()
                {
                    System.out.println("红色");
                }
            },
    Blue("蓝"){
        @Override
        public void show()
        {
            System.out.println("蓝色");
        }
    },
    Green("绿"){
        @Override
        public void show()
        {
            System.out.println("绿色");
        }
    };
    String name = null;
    int a = 0;

    private Red_Bule_Green(String name)
    {
        a ++;
        this.name =  name;
    }

    public String getName()
    {
        return name + " -> " + a;
    }

    public  abstract void  show();

}
