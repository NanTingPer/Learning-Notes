package Trait_

class ConsoleLogger extends Logger()
{
  override def log(msg: String): Unit = {
    print(msg);
  }
}
