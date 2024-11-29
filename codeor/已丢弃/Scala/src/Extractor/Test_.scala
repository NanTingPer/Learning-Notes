package Extractor
import Extractor.Studen

object Test_ {
    def main(args: Array[String]): Unit = {
        var student : Studen = new Studen("梨花",13);
        student match{
            case Studen(name,age) => print(name,age);
            case _ => print("错");
        }
    }
}
