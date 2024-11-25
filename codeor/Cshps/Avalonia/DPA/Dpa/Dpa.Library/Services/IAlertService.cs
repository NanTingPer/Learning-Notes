namespace Dpa.Library.Services;

public interface IAlertService
{
    /// <summary>
    /// 报错
    /// </summary>
    /// <param name="title"> 标题 </param>
    /// <param name="mseeage"> 消息 </param>
    /// <returns></returns>
    System.Threading.Tasks.Task AlertAsync(string title, string mseeage);
}