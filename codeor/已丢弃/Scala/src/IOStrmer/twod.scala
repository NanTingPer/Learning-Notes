package IOStrmer

import java.io._

object twod {
    def main(args: Array[String]): Unit = {
        //获取文件
        var file = new File("src/IOStrmer/numt2");
        //创建文件输入流
        var files = new FileInputStream(file);
        //创建数组存储内容
        var bye = new Array[Byte](file.length().toInt);
        //写入内容
        files.read(bye);
        //关闭文件输入流
        files.close();

        var file2 = new File("src/IOStrmer/numt3");
        file2.createNewFile()
        var files2 = new FileOutputStream(file2);
        files2.write(bye);
        files2.close();
    }
}
