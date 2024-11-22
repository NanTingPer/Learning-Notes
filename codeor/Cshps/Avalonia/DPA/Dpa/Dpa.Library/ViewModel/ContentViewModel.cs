using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq.Expressions;
using System.Text.Json;
using System.Text.Json.Nodes;
using System.Windows.Input;
using AvaloniaInfiniteScrolling;
using CommunityToolkit.Mvvm.Input;
using Dpa.Library.Models;
using Dpa.Library.Services;

namespace Dpa.Library.ViewModel;

public class ContentViewModel : ViewModelBase
{
    //Fact
    public ICommand GetPoetryAllICommand { get; }

    private readonly IPoetrySty _poetrySty;

    //状态值
    private string _state;
    //状态更新
    public string ScrollingState
    {
        get => _state;
        set => SetProperty(ref _state, value);
    }
    
    public AvaloniaInfiniteScrollCollection<Poetry> AvaloniaInfiniteScrolling { get; }
    
    public ContentViewModel(IPoetrySty poetrySty)
    {
        _poetrySty = poetrySty;
        _poetrySty.InitializeAsync();
        //Fact
        GetPoetryAllICommand = new AsyncRelayCommand(GetPoetryAsyncAll);
        AvaloniaInfiniteScrolling = new AvaloniaInfiniteScrollCollection<Poetry>()
        {
            //条件永远为True
            OnCanLoadMore = () => true,
            //载入数据
            OnLoadMore = () =>
            {
                int count = AvaloniaInfiniteScrolling.Count;
                //需求IEnumerable
                Task<List<Poetry>> tlp = _poetrySty.GetPoetryAsync(
                    f => true, 
                    0,
                    10);
                //获取长度
                int ListCount = tlp.Result.Count;
                if (ListCount >= 10)
                {
                    ScrollingState = _Load;
                }
                else
                {
                    ScrollingState = _Run;
                }
                
                return tlp.ContinueWith(t => t.Result.AsEnumerable());
            }
        };
    }

    private string _Load = "加载中";
    private string _Run = "没有数据了";
    

    //Fact
    public ObservableCollection<Poetry> PoetryList { get; } = new();

    /// <summary>
    /// 获取全部数据
    /// //Fact
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