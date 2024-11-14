package Test01;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.ColumnPaginationFilter;
import org.apache.hadoop.hbase.filter.ColumnValueFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;
import sun.nio.cs.ext.GBK;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DML
{
    public static Connection connection;
    static
    {
        try
        {
            connection = ConnectionFactory.createConnection();
        } catch (IOException e)
        {
            System.out.println("链接异常");
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加内容
     * @param nameSpase 命名空间
     * @param tableName 表名
     * @param RowKey RowKey
     * @param RowColumn 列族名称
     * @param RowName_ 列名
     * @param content 内容
     */
    public static void putCall(String nameSpase,String tableName,String RowKey,String RowColumn,String RowName_,  String content) throws IOException
    {
        Table table = null;
        //获取要操作的表格
        try
        {
            table = connection.getTable(TableName.valueOf(nameSpase, tableName));
        }catch (IOException e){
            System.out.println("表格未找到");
            return;
        }

        //创建Put对象
        Put put = new Put(Bytes.toBytes(RowKey));
        put.addColumn(Bytes.toBytes(RowColumn),Bytes.toBytes(RowName_),Bytes.toBytes(content));
        //put
        table.put(put);

        table.close();
    }

    /**
     * Get数据
     * @param nameSpase 命名空间
     * @param tableName 表名
     * @param RowKey 行键
     * @param RowColumn 行族
     * @param RowName 行名
     * @throws IOException 正常抛出
     */
    public static void getCall(String nameSpase,String tableName,String RowKey,String RowColumn,String RowName) throws IOException
    {
        //得到表格
        Table table = connection.getTable(TableName.valueOf(nameSpase,tableName));

        //创建Get对象
        Get get = new Get(Bytes.toBytes(RowKey));
        //添加查询范围
        get.addColumn(Bytes.toBytes(RowColumn),Bytes.toBytes(RowName));

        //得到数据
        Result result = table.get(get);
        //取出数据
        Cell[] cells = result.rawCells();

        //循环输出
        for (Cell cell : cells)
        {
            //底层字节 需要特殊处理
            System.out.println(Bytes.toString(CellUtil.cloneValue(cell)));
        }


        table.close();
    }

    /**
     * 扫描数据
     * @param nameSpace  表命名空间
     * @param tableName 表名称
     * @param startRow 起始行号
     * @param stopRow 结束行号
     * @throws IOException 正常抛出
     */
    public static void ScanContent(String nameSpace,String tableName, String startRow,String stopRow) throws IOException
    {
        //1.得到表
        Table table = connection.getTable(TableName.valueOf(nameSpace,tableName));

        //2.创建Scan对象
        Scan scan = new Scan();

//        //起始位
//        scan.withStartRow(Bytes.toBytes(startRow));
//
//        //结束位
//        scan.withStopRow(Bytes.toBytes(stopRow));
        //得到数据
        ResultScanner scanner = table.getScanner(scan);

        //遍历数据
        for (Result result : scanner)
        {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells)
            {
                System.out.println(new java.lang.String(CellUtil.cloneValue(cell), new GBK()));
            }
        }
        table.close();
    }

    /**
     * 扫描数据 并过滤
     * @param nameSpace  表命名空间
     * @param tableName 表名称
     * @param startRow 起始行号
     * @param stopRow 结束行号
     * @param RowFamily 列族
     * @param RowName 列名
     * @param Value 值
     * @throws IOException 正常抛出
     */
    public static void FilterContent(String nameSpace,String tableName, String startRow,
                                     String stopRow,String RowFamily,
                                     String RowName,String Value) throws IOException
    {
        //1.得到表
        Table table = connection.getTable(TableName.valueOf(nameSpace,tableName));
        //2.创建Scan对象
        Scan scan = new Scan();
        //起始位
        scan.withStartRow(Bytes.toBytes(startRow));
        //结束位
        scan.withStopRow(Bytes.toBytes(stopRow));

        //创建过滤器
        FilterList filterList = new FilterList();
        //只保留值的过滤(列)
        //列族,列名,方式(EQUAL = 等于),具体值
        //整行 => SingleColumnValueFilter 使用方式一样
        ColumnValueFilter columnValueFilter = new ColumnValueFilter(
            Bytes.toBytes(RowFamily),  //列族名称
            Bytes.toBytes(RowName),  //列明
            CompareOperator.EQUAL,  //等于
            Bytes.toBytes(Value)); //具体值
        filterList.addFilter(columnValueFilter);

        //添加过滤器
        scan.setFilter(filterList);

        //得到数据
        ResultScanner scanner = table.getScanner(scan);
        //遍历数据
        for (Result result : scanner)
        {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells)
            {
                System.out.println(new java.lang.String(CellUtil.cloneValue(cell), StandardCharsets.UTF_8));
            }
        }
        table.close();
    }

    /**
     * 删除一列内容
     * @param nameSpace 名称空间
     * @param tableName 表名
     * @param RowFamily 列族
     * @param RowKey RowKey
     * @param RowName 列名
     * @throws IOException 正常抛出
     */
    public static void deltetContentRow(String nameSpace,String tableName, String RowFamily,
                                        String RowKey,String RowName) throws IOException
    {
        //获取表 table
        Table table1 = connection.getTable(TableName.valueOf(nameSpace, tableName));

        //创建删除对象 传入 RowKey
        Delete delete = new Delete(Bytes.toBytes(RowKey));

        //添加删除内容 传入 列族  列名
        delete.addColumn(Bytes.toBytes(RowFamily),Bytes.toBytes(RowName));

        //删除
        table1.delete(delete);

        table1.close();
    }

    public static void main(String[] args) throws IOException
    {
//        添加
//        putCall("NewNameSpace","NewTable","1005","info","666","王五");

        //获取
//        getCall("NewNameSpace","NewTable","1002","info","姓名");

        //扫描
        ScanContent("ods","order_master","d","020241031163240658");

        //过滤
//        FilterContent("NewNameSpace","NewTable","1000","1003","info","姓名","王五");

        //删除
//        deltetContentRow("NewNameSpace","NewTable","info","1005","666");
        connection.close();
    }
}
