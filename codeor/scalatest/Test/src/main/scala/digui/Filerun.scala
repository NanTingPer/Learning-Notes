package digui

import java.io.File

object Filerun {
    def Fileruns(file: File): Unit = {
        if (!file.isDirectory || file.listFiles().length == 0) {
        }
        else {
            var f = file.listFiles();
            for (fil <- f) {
                if (fil.isFile) {
                    println(fil.getAbsoluteFile)
                }
                else {
                    Fileruns(fil);
                }
            }
        }
    }

    def main(args: Array[String]): Unit = {
        Fileruns(new File("C:/Program Files/JetBrains"));
    }
}
