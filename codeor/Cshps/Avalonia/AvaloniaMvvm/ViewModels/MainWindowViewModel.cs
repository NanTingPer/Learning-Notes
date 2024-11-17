using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;
using System.Windows.Input;
using AvaloniaMvvm.Models;
using AvaloniaMvvm.Servicer;
using AvaloniaMvvm.Views;
using CommunityToolkit.Mvvm.Collections;
using CommunityToolkit.Mvvm.Input;

namespace AvaloniaMvvm.ViewModels;
/// <summary>
/// @ => 显示Hello的步骤
/// </summary>
public partial class MainWindowViewModel : ViewModelBase
{
    private readonly ICreateInivte _icreateInivte;

    //包装成ICommand
    //@ 4
    public ICommand SayHelloCommand { get; }

    /// <summary>
    /// 用来获取全部数据的 绑定GetSQLData
    /// </summary>
    public ICommand GetSQLDataCommand { get; }

    /// <summary>
    /// 用来删除数据 绑定 DeleteData
    /// </summary>
    public ICommand DeleteDataCommand { get; }

    /// <summary>
    /// 用来插入数据 绑定 InsData
    /// </summary>
    public ICommand InsDataCommand { get; }
    public ICommand InitiaCommand { get; }

    /// <summary>
    /// 构造函数直接指明依赖关系
    /// </summary>
    /// <param name="icreateInivte"></param>
    public MainWindowViewModel(ICreateInivte icreateInivte)
    {
        _icreateInivte = icreateInivte;
        //绑定SayHello
        //@ 5
        SayHelloCommand = new RelayCommand(SayHello);
        GetSQLDataCommand = new AsyncRelayCommand(GetSQLData);
        DeleteDataCommand = new AsyncRelayCommand(DeleteData);
        InsDataCommand = new AsyncRelayCommand(InsData);
        InitiaCommand = new AsyncRelayCommand(InitiaData);
    }

    //要显示在View的数据
    //@ 1
    private string _message;

    //包装一下要显示在View的数据
    //@ 2
    public string Message
    {
        get => _message;
        set => SetProperty(ref _message, value);
    }

    //赋给定的值
    //@ 3
    private void SayHello() { Message = "adawfa"; }

    private string _sqldataone;
    public string SQLDataOne
    {
        get => _sqldataone; 
        set => SetProperty(ref _sqldataone,value);
    }

    /// <summary>
    /// 数据集
    /// </summary>
    public ObservableCollection<SQLDataType> SQLDataList { get; set; } = new();
    /// <summary>
    /// 用于获取全部数据的方法
    /// </summary>
    /// <returns></returns>
    private async Task GetSQLData()
    {
        SQLDataList.Clear();
        List<SQLDataType> e = await _icreateInivte.ScanAsync();
        foreach (SQLDataType sqlDataType in e)
        {
            SQLDataList.Add(sqlDataType);
        }
        SQLDataOne = SQLDataList[0].Id.ToString() + SQLDataList[0].Name.ToString();
    }

    /// <summary>
    /// 用于删除数据
    /// </summary>
    /// <returns></returns>
    private async Task DeleteData()
    {
        await _icreateInivte.DeleteAsync(new SQLDataType() {Id = 1});
    }

    /// <summary>
    /// 用于插入数据
    /// </summary>
    /// <returns></returns>
    private async Task InsData()
    {
        await _icreateInivte.InsterAsync(new SQLDataType { Name = "1"/*new Random().NextInt64().ToString()*/ });
    }

    private async Task InitiaData()
    {
        await _icreateInivte.InitiaAsync();
    }
    
}