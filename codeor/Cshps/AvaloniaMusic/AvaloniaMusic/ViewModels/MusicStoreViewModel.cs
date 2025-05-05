using ReactiveUI;
using System.Collections.ObjectModel;

namespace AvaloniaMusic.ViewModels;

public class MusicStoreViewModel : ViewModelBase
{
    public MusicStoreViewModel()
    {
        for(int i = 0; i < 10; i++) {
            Albums.Add(new AlbumViewModel());
        }
    }

    private AlbumViewModel? _selectAlbum;
    private string? _searchText;
    private bool _isBusy; 
    
    public string? SearchText  {get => _searchText; set => this.RaiseAndSetIfChanged(ref _searchText, value);}
    public bool IsBusy { get=> _isBusy; set => this.RaiseAndSetIfChanged(ref _isBusy, value);}
    public ObservableCollection<AlbumViewModel> Albums { get; } = new();
    public AlbumViewModel? SelectAlbum { get => _selectAlbum; set => this.RaiseAndSetIfChanged(ref _selectAlbum, value); }
}