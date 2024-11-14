package Test01;

import com.sun.org.apache.bcel.internal.generic.PUSH;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class ThreadNeo
{
    /**
     * 判断表格是否存在
     * @param NameSpace 命名空间的名称
     * @param tableName 表的名称
     * @return
     */
    public static boolean isTableExists(String NameSpace,String tableName) throws IOException{
        //如果不传入 那么就是默认命名空间
//        String Namespace = (NameSpace == null ? "default" : NameSpace);
        //1,获取admin
        Admin admin = connection.getAdmin();

        boolean run = false;
        //2,判断
        try
        {
            run = admin.tableExists(TableName.valueOf(NameSpace, tableName));
        }catch (IOException e){e.printStackTrace();}
        finally
        {
            //3,关闭流
            admin.close();
        }

        //返回
        return run;
    }


    /***
     * 创建命名空间
     * @param NameSoace 命名空间的名字
     */
    public static void creaceNameSpace(String NameSoace) throws IOException
    {
        Admin admin = connection.getAdmin();
        NamespaceDescriptor.Builder newNameSpace = NamespaceDescriptor.create(NameSoace);
        newNameSpace.addConfiguration("说明", "这是新表");
        try
        {
            admin.createNamespace(newNameSpace.build());
        }catch (IOException e){
            System.out.println("命名空间已经存在");
            e.printStackTrace();
        }
//        admin.createNamespace(NamespaceDescriptor.create("NewNameSpace").addConfiguration("说明","这是新表").build());
        admin.close();
    }

    /**
     * 用于创建表格
     * @param nameSpace 名称空间
     * @param tableName 表名称
     * @param column 列族s
     * @throws IOException 正常异常
     */
    public static void creaceTable(String nameSpace,String tableName,String... column) throws IOException
    {
        if(column.length ==0)
        {
            System.out.println("至少有一个列族");
            return;
        }

        //1,得到admin
        Admin admin = connection.getAdmin();

        //2,创建表格建造者
        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(nameSpace,tableName));

        //3,往表格建造者添加列族信息
        for(String columnstr : column)
        {
            //3.1 创建列族建造者
            ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columnstr));
            columnFamilyDescriptorBuilder.setMaxVersions(5);//设置保留5个版本
            //3.2 添加表格的列族信息
            tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptorBuilder.build());
        }

        //创建表格
        try{
        admin.createTable(tableDescriptorBuilder.build());
        }catch (IOException e)
        {
            System.out.println("表格已经存在");
            e.printStackTrace();
        }finally {
            admin.close();
        }

    }

    /**
     * 修改表格的版本信息
     * @param nameSpace 名称空间
     * @param tableName 表的名称
     * @param column 列族名称
     * @param ver 要保留的版本数量
     * @throws IOException 正常抛出
     */
    public static void RevTableDescr(String nameSpace,String tableName,String column,int ver) throws IOException
    {
        //判断表格是否存在
        if(!isTableExists(nameSpace,tableName)){
            System.out.println("表格不存在");
            return;
        }

        //1，获取Admin
        Admin admin = connection.getAdmin();

        //2，获取表格的描述

            //获取原表的 描述信息
            TableDescriptor descriptor = admin.getDescriptor(TableName.valueOf(nameSpace,tableName));

            //3,创建表格建造者 使用原表的信息
            TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(descriptor);

            //获取旧的表格列族描述
            ColumnFamilyDescriptor columnFamily = descriptor.getColumnFamily(Bytes.toBytes(column));

            //创建列族描述建造者 传入原表的列族描述信息
            ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(columnFamily);

            //修改版本信息
            columnFamilyDescriptorBuilder.setMaxVersions(ver);

            //将修改后的数据载入表格建造者
            tableDescriptorBuilder.modifyColumnFamily(columnFamilyDescriptorBuilder.build());

            //修改
            admin.modifyTable(tableDescriptorBuilder.build());
        System.out.println("修改成功");
        admin.close();
    }


    /**
     * 删除指定表格
     * @param nameSpace 命名空间
     * @param tableName 表名
     * @return True表示删除成功
     * @throws IOException 正常抛出
     */
    public static boolean DeleteTable(String nameSpace,String tableName) throws IOException
    {
        if(!isTableExists(nameSpace,tableName)) {
            System.out.println("表格不存在");
            return false;
        }

        //获取admin
        Admin admin = connection.getAdmin();

        try
        {
            //获取需要删除的表格
            TableName TABLE = TableName.valueOf(nameSpace, tableName);
            //禁用并删除
            admin.disableTable(TABLE);
            admin.deleteTable(TABLE);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }finally
        {
            admin.close();
        }

        return true;
    }

    public static Connection connection = null;
    static {
        try
        {
            connection = ConnectionFactory.createConnection();
        } catch (IOException e) {}
    }
    public static void closeConnection() throws IOException
    {
        if(connection!=null)
        {
            connection.close();
        }
    }

    public static void main(String[] args) throws IOException
    {
        Connection Myconnection = connection;
//        System.out.println(Myconnection);
//        System.out.println(connection.hashCode() == Myconnection.hashCode());

        //创建名称空间
//        creaceNameSpace("NewNameSpace");
//        System.out.println("执行完毕");

        //查看表是否存在
//        System.out.println(isTableExists("123", "123"));

        //创建表格
        creaceTable("NewNameSpace","NewTable","info");

        //修改表格的版本信息
//        RevTableDescr("NewNameSpace","NewTable","info",7);

        //删除表格
//        System.out.println(DeleteTable("NewNameSpace", "NewTable"));

        Myconnection.close();
    }
}
