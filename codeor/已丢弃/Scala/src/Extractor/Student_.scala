package Extractor

class Studen(var name : String,var age : Int) { }
object Studen {
    def apply(name: String, age: Int): Studen = new Studen(name, age);

    def unapply(s: Studen) = {
        if (s == null) {
            None;
        } else {
            Some(s.name, s.age);
        }
    }
}
