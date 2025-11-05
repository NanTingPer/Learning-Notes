namespace BlackTenText.UtilityType;

/// <summary>
/// 构建Mysql链接字符串
/// </summary>
public class MySQLConnectionStringBuild
{
    public static MySQLConnectionStringBuild Parser(IConfiguration configuration, string key, ILogger? log = null)
    {
        var sqlSection = configuration.GetSection(key);
        var uri = sqlSection.GetValue(nameof(Uri), "127.0.0.1")!;
        var port = sqlSection.GetValue(nameof(Port), "3306")!;
        var user = sqlSection.GetValue(nameof(User), "root")!;
        var password = sqlSection.GetValue(nameof(Password), "123456")!;
        var sslmode = sqlSection.GetValue(nameof(SSLMode), "none")!;
        var databasename = sqlSection.GetValue(nameof(DataBaseName), "none")!;

        if(log != null && databasename == "none") {
            log.LogInformation($"{nameof(MySQLConnectionStringBuild)} 你的数据库名称好像是默认的");
        }

        return new MySQLConnectionStringBuild()
        {
            Uri = uri,
            Port = port,
            User = user,
            Password = password,
            SSLMode = sslmode,
            DataBaseName = databasename
        };
    }

    public string Uri { get; set; } = "127.0.0.1";
    public string Port { get; set; } = "3306";
    public string User { get; set; } = "root";
    public string Password { get; set; } = "123456";
    public string SSLMode { get; set; } = "none";
    public string DataBaseName { get; set; } = "none";

    public MySQLConnectionStringBuild SetUri(string? value)
    {
        if (value == null)
            return this;
        Uri = value;
        return this;
    }

    public MySQLConnectionStringBuild SetPort(ushort value)
    {
        if (value == default)
            return this;

        Port = value.ToString();
        return this;
    }

    public MySQLConnectionStringBuild SetUser(string? value)
    {
        if (value == null)
            return this;
        User = value;
        return this;
    }

    public MySQLConnectionStringBuild SetPasswd(string? value)
    {
        if (value == null)
            return this;
        Password = value;
        return this;
    }

    public MySQLConnectionStringBuild SetSSLMode(string? value)
    {
        if (value == null)
            return this;
        SSLMode = value;
        return this;
    }

    public string Build()
    {
        return $"Server={Uri};Port={Port};User Id={User};Password={Password};Database={DataBaseName};sslMode={SSLMode};";
    }
}
