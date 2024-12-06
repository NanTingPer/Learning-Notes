package OldJobTest

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment
import org.apache.flink.table.api._

import java.text.SimpleDateFormat
import java.util.Date

object SQLTest {
    def main(args: Array[String]): Unit = {
        val env = StreamExecutionEnvironment.getExecutionEnvironment
        val envtable = StreamTableEnvironment.create(env)

        envtable.executeSql("create table tab(name string,age string)")
        val table = envtable.sqlQuery("select * from tab")

        val str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()))
        envtable.executeSql(
            s"""
              |select concat(${str}),*
              |from tab
              |""".stripMargin)
    }
}
