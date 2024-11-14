import org.apache.spark.sql.SparkSession

object lee
{
    //        编写Scala 工程代码，根据dwd 的订单表dwd.fact_order_master，
    //        求各省份下单时间为2022 年的支付转化率，
    //        并将计算结果按照下述表结构写入clickhouse 的ds_result 库的payment_cvr 表。
    //        在Linux 的clickhouse 命令行中根据ranking 字段查询出转化率前三的省份，
    //        将SQL 语句与执行结果截图粘贴至客户端桌面【Release\模块D 提交结果.docx】中对应的任务序号下；

    //        注:支付转化率= 完成支付的订单数/ 已下单数。
    //————————————————————————————————————————
    // |       字段	             |     类型	        |    中文含义	          |                   备注                        |
    // |      province	         |     string	      |        省份名            |                                                  |
    // |       creat_order	   |       int	        |       已下单数         |                                                   |
    // |       payment	       |       int	        |   已支付的订单数   |                                                   |
    // |       payCVR	         |    float64	    |      支付转化率	      |       四舍五入保留三位小数         |
    // |       ranking	         |       int	        |      转化率排名       |                                                   |
    //————————————————————————————————————————
    def main(args: Array[String]): Unit = {
        System.setProperty("HADOOP_USER_NAME","root")
        val spark = SparkSession.builder().master("local[*]").appName("afw").enableHiveSupport().getOrCreate()
        //全部抽出来再说
        val ALLData = spark.sql("select * from ss2024_ds_dwd.fact_order_master")
        ALLData.createOrReplaceTempView("ALL");
        ALLData.show

        //下单时间为2022
        spark.sql("select * from ALL where substr(create_time,0,4) = '2022' ").show()
        spark.table("ss2024_ds_dwd.fact_order_master").show()
    }
}

