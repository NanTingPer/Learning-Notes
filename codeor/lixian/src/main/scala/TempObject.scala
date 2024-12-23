import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object TempObject {
    def main(args: Array[String]): Unit = {
        val sparkSession = SparkSession.builder()
                .appName("Offline Data Collection")
                .master("local[*]")
                .getOrCreate()

        import sparkSession.implicits._

        val df = Seq(
            ("Alice", "Sales", 60000),
            ("Bob", "Sales", 50000),
            ("Carol", "IT", 70000),
            ("David", "IT", 60000),
            ("Eve", "Finance", 65000),
            ("Timothy Liu", "IT", 99999999),
            ("Waterkuiiiiiii", "IT", 88888888)
        ).toDF("Name", "Department", "Salary")

        val avgSalaryByDept = df.
                groupBy(df("Department").as("department"))
                .agg(avg("Salary").as("averageSalary"))
                .sort(col("averageSalary").asc)

        avgSalaryByDept.show()

        sparkSession.stop()
    }
}
