import scala.io.StdIn
import scala.util.control.Breaks

object ForDome_01
{
  def main(args: Array[String]): Unit = {
    //登录 for登录
    var SuerName:String = "";
    var Onerun:Int = 0;
    print("请输入用户名:\n");
    do
    {
      if(Onerun != 0)
      {
        print("用户名错误 请重输\n")
      }
      SuerName = StdIn.readLine();
      print(SuerName + "\n");
      Onerun += 1;
    }
    while(!SuerName.equals("root"))
    var Password : String = "";
    var cishu : Int = 0;
    Breaks.breakable{
      while(true){
        if(cishu != 0)
        {
          print("密码错误请重输\n")
        }
        else
        {
          print("请输入密码\n")
        }
        Password = StdIn.readLine();
        if(Password.equals("root"))
        {
          print("登录成功");
          Breaks.break();
        }
        if(cishu > 4)
        {
          print("登录失败");
          Breaks.break();
        }
        cishu +=1;
      }
    }

  }

}
