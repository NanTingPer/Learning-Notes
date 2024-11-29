import scala.util.control.Breaks

object forbreak
{
  def main(args: Array[String]): Unit = {
    Breaks.breakable
    {
      for (i <- 1 to 10)
        {
          if(i>5) Breaks.break()
          print(i +"\n");
        }
    }
  }
}
