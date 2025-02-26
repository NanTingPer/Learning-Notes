using System.Windows.Input;
using CommunityToolkit.Mvvm.Input;
using Dpa.Library.Models;
using Dpa.Library.Services;

namespace Dpa.Library.ViewModel;

public class ToDayViewModel : ViewModelBase
{
    private int _fontSize = 28;
    private IToDayPoetryStyService _jinRiShiCiGet;
    private ToDayPoetry _toDayPoetry;
    /// <summary>
    /// 用于表示加载是否完成
    /// </summary>
    private bool isLoad = false;
    public ICommand InitiailzationCommand { get; }

    public int FontSize
    {
        get => _fontSize;
        set => SetProperty(ref _fontSize, value);
    }
    
    public ToDayPoetry ToDayPoetry
    {
        get => _toDayPoetry;
        set => SetProperty(ref _toDayPoetry, value);
    }
    
    public ToDayViewModel(IToDayPoetryStyService jinRiShiCiGet)
    {
        _jinRiShiCiGet = jinRiShiCiGet;
        InitiailzationCommand = new AsyncRelayCommand(Initiailzation);
    }
    
    /// <summary>
    /// 用于初始化诗歌
    /// </summary>
    /// <returns> 诗歌 </returns>
    private async System.Threading.Tasks.Task Initiailzation()
    {
        ToDayPoetry = await _jinRiShiCiGet.GetToDayPoetryAsync();
        isLoad = true;
    }
}