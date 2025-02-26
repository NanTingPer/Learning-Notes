package IO.Objet.Noe;

import IO.Objet.Student;

import java.io.*;

public class Mmian
{
    public static void main(String[] args) throws Exception
    {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("C:\\Users\\23759\\Desktop\\1\\StringDomePx\\src\\IO\\Objet\\Noe\\Student.txt"));
        Student st = new Student(12,"力宏");
        oos.writeObject(st);
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("C:\\Users\\23759\\Desktop\\1\\StringDomePx\\src\\IO\\Objet\\Noe\\Student.txt"));
        Object ob = ois.readObject();
        Student oe = (Student)ob;
        System.out.println(oe.getAge() + "," + oe.getName());

        oos.close();
        ois.close();
    }
}
