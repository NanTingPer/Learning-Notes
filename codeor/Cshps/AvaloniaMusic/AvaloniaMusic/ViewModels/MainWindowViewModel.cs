using System.Reactive.Linq;
using System.Threading.Tasks;
using System.Windows.Input;
using AvaloniaMusic.MusicService;
using ReactiveUI;

namespace AvaloniaMusic.ViewModels;

public class MainWindowViewModel : ViewModelBase
{
    
    public string Greeting { get; } = "Welcome to Avalonia!";
    public ICommand OpenStoreWindowCommand{get; set;}
    public Interaction<MusicStoreViewModel, AlbumViewModel?> ShowStoreInteraction { get; } = new();
    public MainWindowViewModel()
    {
        OpenStoreWindowCommand = ReactiveCommand.Create(OpenStoreWindow);
    }
    private async Task OpenStoreWindow()
    {
        //传一个MusicStoreViewModel给消息处理程序
        //其调用SetOutput后，这里就得到了一个Album
        this.GetMusic("初音");
        var album = await ShowStoreInteraction.Handle(new MusicStoreViewModel());
    }
}