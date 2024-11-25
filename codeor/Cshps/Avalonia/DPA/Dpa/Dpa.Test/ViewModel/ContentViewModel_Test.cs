using AvaloniaInfiniteScrolling;
using Dpa.Library.Models;
using Dpa.Library.Services;
using Dpa.Library.ViewModel;
using Dpa.Test.DeleteDatabases;
using Xunit;

namespace Dpa.Test.ViewModel;

public class ContentViewModel_Test
{
    [Fact]
    public async void AvaloniaInfiniteScrolling_Default()
    {
       PoetryStyService poStyService = await PublicMethod.GetPoetryStyAndInitia();
       ContentViewModel Cv = new ContentViewModel(poStyService);
       AvaloniaInfiniteScrollCollection<Poetry> count = Cv.AvaloniaInfiniteScrolling;
       
       //翻阅源码得知，调用这个方法会对数据进行Load
       //OnLoadMore只是对加载方法的定义
       await Cv.AvaloniaInfiniteScrolling.LoadMoreAsync();
       Assert.Equal(10,getCount(count));

       Cv.PropertyChanged += (sender, args) =>
       {
           Assert.True(nameof(Cv.ScrollingState).Equals(args.PropertyName));
       };

       int getCount<T>(IEnumerable<T> ie)
       {
           return ie.Count();
       }
    }
}