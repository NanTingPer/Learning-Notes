using System.ComponentModel.DataAnnotations;
using System.IO.Ports;
using System.Text.Json;
using System.ComponentModel.DataAnnotations.Schema;
using System.Text.Json.Serialization;
using System.Text;
using System.Threading.Tasks;
namespace SoilSensorPut;

internal class Program
{
    static async Task Main(string[] args)
    {
        ushort[] deviceAddr = [0, 1, 2, 3, 4, 5, 6, 7, 8];
        var httpClient = new HttpClient()
        {
            Timeout = TimeSpan.FromSeconds(5)
        };
        string postUri = "";
        GetConf(ref postUri);

    getPortName:
        Console.WriteLine();
        Console.WriteLine("请输入端口名称: ");
        var openPortNames = SerialPort.GetPortNames();
        var portIndex = GetPortNames();
        if (!portIndex.HasValue) {
            goto getPortName;
        }

        var serialPort = new SerialPort(openPortNames[portIndex.Value])
        {
            ReadTimeout = 500,
            WriteTimeout = 500,
        };
        Console.WriteLine($"尝试开启{openPortNames[portIndex.Value]}!");
        try {
            serialPort.Open();
        } catch {
            Console.WriteLine("传感器开启失败，请重新选择!");
            serialPort.Dispose();
            goto getPortName;
        }

        while (true) {
            //定义用于存储结果的变量，因为在try catch语句块内定义的对象无法在 语句块外使用，所以要提前定义
            ushort[] values;
            //等待0.5秒避免土壤传感器性能问题，出现数据冲突
            await Task.Delay(500);
            try {
                //0x03代表读取保持寄存器0x00 0x01代表读取 1个地址，也是0号寄存器
                //如果要读取1号寄存器 只需要设置为 0x01 0x01即可后面的0x01表示读取一个寄存器地址
                //0xD5 0xCA为CRC校验码，由方法自行计算并替换
                byte[] query = [0x01, 0x03, 0x00, 0x01, 0x00, 0x01, 0xD5, 0xCA];

                //获取寄存器的全部数据，deviceAddr是已经定义好的数据
                //所使用的传感器具有9个地址，9种数据，所以他的值是 0 - 8
                //serialPort是已经打开的串口
                values = await GetValues(deviceAddr, query, serialPort);
            } catch {
                //如果执行GetValues的过程中报错了，则所使用的资源，并进行重试，重新打开给定的串口并建立通信
                Console.WriteLine("传感器数据获取失败!");
                httpClient.Dispose();
                serialPort.Dispose();
                goto getPortName;
            }

            //如果数据中"几乎全零"那么说明此次数据不正确，那么给定的串口可能不是所需要的
            //因此释放资源，并回退到选择串口
            if (values.Where(f => f == 0).Count() >= values.Length - 1) {
                Console.WriteLine("传感器数据格式不正确!");
                httpClient.Dispose();
                serialPort.Dispose();
                goto getPortName;
            }

            //将返回的数据数组，转换为服务器所需要的数据类型，并使用主机名+串口号作为唯一标识符，方便故障寻回
            var updateData = GetDataInfo(values, serialPort);

            //将数据上传至服务器
            await UpdateToServer(updateData, postUri, httpClient);
        }
    }

    static async Task<bool> UpdateToServer(SoilSensorDataInfo updateData, string postUri, HttpClient htc)
    {
        Console.WriteLine("获取如下数据 : ");
        try {
            Console.WriteLine(JsonSerializer.Serialize(updateData));
            var htvalue = await htc.PostAsync(postUri, new StringContent(JsonSerializer.Serialize(updateData), Encoding.UTF8, "application/json"));
            Console.WriteLine("响应: " + (int)htvalue.StatusCode);
            Console.WriteLine("信息: " + await htvalue.Content.ReadAsStringAsync() ?? "");
        } catch {
            Console.WriteLine("服务器响应超时。");
            return false;
        }
        return true;
    }

    static int? GetPortNames()
    {
        var openPortNames = SerialPort.GetPortNames();
        int index = 0;
        foreach (var name in openPortNames) {
            Console.WriteLine(index + "\t" + name);
            index += 1;
        }
        string? indexstr = Console.ReadLine();
        if (indexstr == null) {
            Console.WriteLine("请输入正确的端口索引!");
            return null;
        }

        if (!int.TryParse(indexstr.Trim(), out int portIndex)) {
            Console.WriteLine("请输入正确的端口索引!");
            return null;
        }
        return portIndex;
    }

    static void GetConf(ref string postUri)
    {
        var cudir = Environment.CurrentDirectory;
        var confPath = Path.Combine(cudir, "conf.conf");
        if (!File.Exists(confPath)) {
            var filestream = File.CreateText(confPath);
            filestream.WriteLine("http://10.20.240.19:5141/soilsensor/add");
            filestream.Dispose();
        }
        var line = File.ReadLines(confPath).FirstOrDefault();
        if (line == null || string.IsNullOrEmpty(line)) {
            var filestream = File.CreateText(confPath);
            filestream.WriteLine("http://10.20.240.19:5141/soilsensor/add");
            filestream.Dispose();
            postUri = "http://10.20.240.19:5141/soilsensor/add";
        } else {
            postUri = line;
        }
        Console.WriteLine("postUri : " + postUri);
    }

    static async Task<ushort[]> GetValues(ushort[] addrs, byte[] query, SerialPort serialPort)
    {
        ushort[] values = new ushort[addrs.Length];
        foreach (var addr in addrs) {
            serialPort.DiscardInBuffer();
            serialPort.DiscardOutBuffer();
            SetStartAddr(ref query, addr);
            var crc = GetCRCCode(query[..^2]);
            query[^2] = crc[0];
            query[^1] = crc[1];
            serialPort.Write(query, 0, query.Length);
            await Task.Delay(200);
            byte[] bufferReadByte = new byte[7];
            serialPort.Read(bufferReadByte, 0, 7);
            ushort value = (ushort)((bufferReadByte[3] << 8) | bufferReadByte[4]);
            values[addr] = value;
        }
        return values;
    }

    static SoilSensorDataInfo GetDataInfo(ushort[] values, SerialPort serialPort)
    {
        return new SoilSensorDataInfo()
        {
            EC = values[(int)DataType.电导率],
            K = values[(int)DataType.钾],
            N = values[(int)DataType.氮],
            NaCl = values[(int)DataType.盐分],
            P = values[(int)DataType.磷],
            PH = values[(int)DataType.酸碱度],
            TDS = values[(int)DataType.盐度],
            Tg = values[(int)DataType.温度],
            Water = values[(int)DataType.水分],
            Name = Environment.MachineName + serialPort.PortName,
            Key = 0,
            UTCTick = 0,
        };
    }
    static void SetStartAddr(ref byte[] bytes, ushort value)
    {
        var up = (byte)((value >> 8) & 0xFF);   // 高字节
        var down = (byte)(value & 0xFF);        // 低字节
        bytes[2] = up;
        bytes[3] = down;
    }

    static byte[] GetCRCCode(byte[] value)
    {
        ushort polynomial = 0xA001; //0xA001
        ushort crc = 0xFFFF;
        foreach (byte b in value) {
            crc ^= b;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x0001) != 0)
                    crc = (ushort)((crc >> 1) ^ polynomial);
                else
                    crc >>= 1;
            }
        }

        byte[] bytes = new byte[2];

        byte down = (byte)(crc & 0xFF);//低字节 (前面的是低， 后面的是高)
        byte up = (byte)((crc >> 8) & 0xFF); //8位一字节，高位在右边
        bytes[0] = down;
        bytes[1] = up;
        return bytes;
    }
}
public class SoilModel
{
    public float 温度 { get; set; }
    public float 湿度 { get; set; }
    public float PH值 { get; set; }
    public float 电导率 { get; set; }
}

enum DataType
{
    水分 = 0,
    温度 = 1,
    电导率 = 2,
    酸碱度 = 3,
    氮 = 4,
    磷 = 5,
    钾 = 6,
    盐分 = 7,
    盐度 = 8
}
public class SoilSensorDataInfo
{
    public const string id = "id";
    public const string name = "name";

    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public long Key { get; set; }

    public string Name { get; set; } = string.Empty;

    /// <summary>
    /// 温度
    /// </summary>
    public double Tg { get; set; }

    /// <summary>
    /// 水分
    /// </summary>
    public double Water { get; set; }

    /// <summary>
    /// 电导度
    /// </summary>
    public double EC { get; set; }

    /// <summary>
    /// 酸碱度
    /// </summary>
    public double PH { get; set; }

    /// <summary>
    /// 氮
    /// </summary>
    public double N { get; set; }

    /// <summary>
    /// 磷
    /// </summary>
    public double P { get; set; }

    /// <summary>
    /// 钾
    /// </summary>
    public double K { get; set; }

    /// <summary>
    /// 盐分
    /// </summary>
    public double NaCl { get; set; }

    /// <summary>
    /// 盐度
    /// </summary>
    public double TDS { get; set; }

    public long UTCTick { get; set; } = DateTime.UtcNow.Ticks;
}

[JsonSerializable(typeof(SoilSensorDataInfo))]
internal partial class AppJsonContext : JsonSerializerContext
{
}