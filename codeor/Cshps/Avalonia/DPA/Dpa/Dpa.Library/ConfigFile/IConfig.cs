namespace Dpa.Library.ConfigFile;

public interface IConfig
{
    void Set(string key,string value);

    string Get(string key, string value);
    
    void Set(string key,int value);

    int Get(string key, int value);
    
    void Set(string key,DateTime value);

    DateTime Get(string key, DateTime value);
}