namespace BlackTenText.EntityModels;

public class User
{
    public long Id { get; set; }

    public string Name { get; set; } = string.Empty;

    public string Password { get; set; } = string.Empty;
    
    public string HeadSculpture { get; set; } = string.Empty;

    public DateTime Regtime { get; set; } = DateTime.Now;

    public string Salt { get; set; } = string.Empty;

    public const string id = "id";
    public const string name = "name";
    public const string password = "password";
    public const string headSculpture = "headSculpture";
    public const string regtime = "regtime";
    public const string salt = "salt";
}


public record UserDto(string UserName, string Password);