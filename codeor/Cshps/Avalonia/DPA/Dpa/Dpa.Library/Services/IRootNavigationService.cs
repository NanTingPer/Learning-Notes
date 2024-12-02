namespace Dpa.Library.Services;

public interface IRootNavigationService
{
    void NavigateTo(string view);
}

/// <summary>
/// 主要窗口的信息
/// </summary>
public static class ViewInfo
{
    public const string InitializationView = nameof(InitializationView);
    public const string MainView = nameof(MainView);
    
}