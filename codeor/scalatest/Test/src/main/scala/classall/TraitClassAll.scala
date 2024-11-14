package classall

object TraitClassAll {
    trait orn[T]{
        var ore : T;
        def show;
    }

    class orn_new(a : String) extends orn[String]{
        override var ore: String = a;

        override def show : Unit = println(ore);
    }

    def main(args: Array[String]): Unit = {
        new orn_new("利好").show;
    }
}
