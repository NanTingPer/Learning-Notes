namespace Dpa.Library.Services;

public interface IRootNavigationService
{
    void NavigateTo(string view);
}

public static class ViewInfo
{
    public const string InitializationView = nameof(InitializationView);
    public const string MainView = nameof(MainView);
    
}