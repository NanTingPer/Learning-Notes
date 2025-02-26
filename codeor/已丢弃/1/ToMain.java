package you.lx;
import java.util.ArrayList;
import java.util.Scanner;

public class ToMain {
    public static void main(String[] args) {
        ArrayList<XueShen> st = new ArrayList<XueShen>();
        Scanner Sc = new Scanner(System.in);
        String Name;
        int Age;
        while (true) {
            Nen();
            int Xz = Sc.nextInt();
            if (Xz == 1) {
                System.out.println("请输入姓名");
                Name = Sc.next();
                System.out.println("请输入年龄");
                Age = Sc.nextInt();
                XueShen Xs = new XueShen(Name, Age);
                ToList.AddList(st, Xs);
            }
            if (Xz == 2) {
                System.out.println("请输入需要删除的序号");
                //Name = Sc.next();
                int xh = Sc.nextInt();
                ToList.DelList(st, xh);
            }
            if (Xz == 3) {
                ToList.AddListFile(st);
            }
        }
    }

    public static void Nen() {
        System.out.println("");
        System.out.println(" 菜单");
        System.out.println("1,添加");
        System.out.println("2,删除");
        System.out.println("3,列出");
        System.out.println("");
    }
}