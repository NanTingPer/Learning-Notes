package SparkSQL;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.Serializable;

public class SQL2024_10_26_17_bean
{
    public static void main(String[] args)
    {
        SparkSession ss = SparkSession
            .builder()
            .master("local[*]")
            .appName("SQLbean")
            .getOrCreate();

        Dataset<Row> json = ss.read().json("src/main/java/SparkSQL/Json.json");
        Dataset<User> userDataset = json.as(Encoders.bean(User.class));

        userDataset.show();

        ss.close();

    }
}
class User implements Serializable {
    private long id;
    private long age;
    private String name;

    // 默认构造函数
    public User() {}

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
