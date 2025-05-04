using System.Threading.Tasks;
using System.Windows.Input;
using Avalonia.Controls;
using Avalonia.ReactiveUI;
using AvaloniaMusic.ViewModels;
using ReactiveUI;

namespace AvaloniaMusic.Views;

public partial class MainWindow : ReactiveWindow<MainWindowViewModel>
{
    public MainWindow()
    {
        this.WhenActivated(action =>
        {
            action.Invoke(this.ViewModel!.ShowStoreInteraction.RegisterHandler(HandleMethod));
        });
        InitializeComponent();
    }

    public async Task HandleMethod(IInteractionContext<MusicStoreViewModel, AlbumViewModel> context)
    {
        //创建音乐商店窗口，并设置其内容为输入值，输入值为AlbumViewModel
        var musicStore = new MusicStoreWindow();
        musicStore.DataContext = context.Input;
        
        //显示这个商店窗口，父窗口就是MainWindow
        var r = await musicStore.ShowDialog<AlbumViewModel>(this);
        //设置输出
        context.SetOutput(r);
    }
}