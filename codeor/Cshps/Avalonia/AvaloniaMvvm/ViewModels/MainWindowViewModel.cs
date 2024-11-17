using System.Windows.Input;
using AvaloniaMvvm.Servicer;
using AvaloniaMvvm.Views;
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
    /// 构造函数直接指明依赖关系
    /// </summary>
    /// <param name="icreateInivte"></param>
    public MainWindowViewModel(ICreateInivte icreateInivte)
    {
        _icreateInivte = icreateInivte;
        //绑定SayHello
        //@ 5
        SayHelloCommand = new RelayCommand(SayHello);
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
    private void SayHello(){Message = "Hello";}
    
    
    public void 
}