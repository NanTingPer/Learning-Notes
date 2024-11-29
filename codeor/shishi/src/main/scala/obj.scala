import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.table.api
import org.apache.flink.table.api.Expressions.{dateFormat, randInteger}
import org.apache.flink.table.api.{AnyWithOperations, DataTypes, FieldExpression, currentTimestamp, row}
import org.apache.flink.table.api.bridge.scala.StreamTableEnvironment

object obj {
        def main(args: Array[String]): Unit = {
                val env = StreamExecutionEnvironment.getExecutionEnvironment
                val envtable = StreamTableEnvironment.create(env)

                        envtable.from("666")
                        .select(api.concat(randInteger(10).cast(DataTypes.STRING()),dateFormat(currentTimestamp(),"yyyyMMddHHmmssSSS")) as "row_key",
                                row(
                                        $"order_id",
                                        $"order_sn",
                                        $"customer_id",
                                        $"shipping_user",
                                        $"province",
                                        $"city",
                                        $"address",
                                        $"order_source",
                                        $"payment_method",
                                        $"order_money",
                                        $"district_money",
                                        $"shipping_money",
                                        $"payment_money",
                                        $"shipping_comp_name",
                                        $"shipping_sn",
                                        $"create_time",
                                        $"shipping_time",
                                        $"pay_time",
                                        $"receive_time",
                                        $"order_status",
                                        $"order_point",
                                        $"invoice_title",
                                        $"modified_time") as "cf")
                        // 将表写入HBase表
                        .executeInsert("order_master_hbase")

        }
}
