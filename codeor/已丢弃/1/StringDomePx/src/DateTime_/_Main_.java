package DateTime_;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class _Main_
{
    public static void main(String[] args)
    {
//        //获取时间
//        LocalDateTime dt = LocalDateTime.now();
//
//        String time = "2024.9.16 10.24.30";
//        LocalDateTime time_ = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy.MM.dd HH.mm.ss"));
//        String time_2  = time_.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH.mm.ss"));
//        System.out.println(time_2);
        //获取时间
        LocalDateTime now = LocalDateTime.now();
        //创建格式化
        DateTimeFormatter dtf =  DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        //格式时间
        String  time =  now.format(dtf);
        System.out.println(time);

        //自定义时间
        LocalDateTime time_ = LocalDateTime.of(2024,9,16,10,39,40);
        //自定义时间
        LocalDateTime time_1 = LocalDateTime.parse("2024/09/16 10:44:20",DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        String time_2 = time_1.format(dtf);
        System.out.println(time_2);
    }
}
