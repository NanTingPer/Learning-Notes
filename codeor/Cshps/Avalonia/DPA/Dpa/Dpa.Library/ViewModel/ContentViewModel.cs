using System.Collections.ObjectModel;
using System.Linq.Expressions;
using System.Windows.Input;
using CommunityToolkit.Mvvm.Input;
using Dpa.Library.Models;
using Dpa.Library.Services;

namespace Dpa.Library.ViewModel;

public class ContentViewModel : ViewModelBase
{
    public ICommand GetPoetryAllICommand { get; }

    private readonly IPoetrySty _poetrySty;
    
    public ContentViewModel(IPoetrySty poetrySty)
    {
        _poetrySty = poetrySty;
        GetPoetryAllICommand = new AsyncRelayCommand(GetPoetryAsyncAll);
    }

    public ObservableCollection<Poetry> PoetryList { get; } = new();
    
    /// <summary>
    /// 获取全部数据
    /// </summary>
    private async System.Threading.Tasks.Task GetPoetryAsyncAll()
    {
        await _poetrySty.InitializeAsync();
        //每次调用
        PoetryList.Clear();
        
        List<Poetry> Poetrys = await _poetrySty.GetPoetryAsync(
            //方法传参数 要求Expression<Func<Poetry,bool>>
            //设置始终返回 true  Expression.Constant(true)
            Expression.Lambda<Func<Poetry,bool>>(Expression.Constant(true),
                Expression.Parameter(typeof(Poetry),"p")),0,int.MaxValue);
        foreach (Poetry poetry in Poetrys)
        {
            PoetryList.Add(poetry);
        }
    }
    
    
    
}