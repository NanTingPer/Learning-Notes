import com.mysql.jdbc.Driver
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.{RichSourceFunction, SourceFunction}

import java.sql.{Connection, DriverManager, PreparedStatement}

class JDBCSource extends RichSourceFunction{

    var conn : Connection = null;
    var booe = false;
    var ps : PreparedStatement = null;

    override def close(): Unit = {
        super.close()
        conn.close();
        ps.close();
    }



    override def run(ctx: SourceFunction.SourceContext[Nothing]): Unit = {

    }

    override def cancel(): Unit = {

    }

    override def open(parameters: Configuration): Unit = {
        super.open(parameters)
        //TODO 数据库驱动
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://192.168.45.13:3306/xxx?useSSL=false","root","123456")
        conn.prepareStatement("select * from xxx")
    }
}
