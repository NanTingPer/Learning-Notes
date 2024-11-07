package SourceDome;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.File;

public class FileSourceDome
{
    public static void main(String[] args) throws Exception
    {
        StreamExecutionEnvironment Env = StreamExecutionEnvironment.getExecutionEnvironment();

//        FileSource.FileSourceBuilder<String> filesource = FileSource.forRecordStreamFormat(
//            new TextLineInputFormat(),
//            Path.fromLocalFile(
//                new File("C:\\LiMGren\\codeor\\Flink\\src\\main\\java\\_001\\word.txt")
//            )
//        );

        //创建文件 数据源
        FileSource<String> filesource = FileSource
            .forRecordStreamFormat(
                new TextLineInputFormat(),
                new Path("C:\\LiMGren\\codeor\\Flink\\src\\main\\java\\_001\\word.txt")
            ).build();

        //创建数据流
        DataStreamSource<String> FileSourceData = Env.fromSource(filesource, WatermarkStrategy.noWatermarks(), "666");
        //打印
        FileSourceData.print();
        //行动
        Env.execute();
    }
}
