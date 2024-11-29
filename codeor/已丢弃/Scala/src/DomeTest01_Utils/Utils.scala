package DomeTest01_Utils

import java.{text, time, util}
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.time.LocalDateTime;

object Utils {
  var sdf : SimpleDateFormat = new SimpleDateFormat();
  //时间到字符串
  def Date_to_String(date : Date,format : String):String = {
    sdf = new SimpleDateFormat(format)
    sdf.format(date);
  }

  def Date_to_String02(time : LocalDateTime,format : String) : String = {
    var time_ : String = time.format(DateTimeFormatter.ofPattern(format));
    return time_;
  }

  def String_to_Date(date: String,format:String): Date = {
    //生成时间格式化
      sdf = new SimpleDateFormat(format);
    //生成日期
      sdf.parse(date);
  }

  def main(args: Array[String]): Unit = {
    //Date是线程不安全的
//    var timeString : String =  Date_to_String(new Date(),"yyyy");
    var timeString : String = Date_to_String02(LocalDateTime.now(),"yyyy");
    print(timeString);

    var stirngTime : Date = String_to_Date("2024","yyyy");
    print("\n" + stirngTime);
  }
}

