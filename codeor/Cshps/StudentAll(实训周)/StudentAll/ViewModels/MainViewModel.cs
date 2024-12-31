using CommunityToolkit.Mvvm.Input;
using SQLite;
using StudentAll.SQLite;
using System;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.IO;
using System.Threading.Tasks;
using System.Windows.Input;

namespace StudentAll.ViewModels
{
    public class MainViewModel : ViewModelBase, INotifyPropertyChanged
    {
        private SQLiteService _sQLiteService;

        private ulong _id;
        private string _name;
        private string _banji;
        private uint _age;
        private StudentInfo _studentInfo;
        private int skip = 0;

        public static string AppDataPath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),"StudentAll");
        public static string DataPath = Path.Combine(AppDataPath, "StudentInfo");


        public ICommand AddDataCommand { get; private set; }
        public ICommand DeleteDataCommand { get; private set; }
        public ICommand InitializedCommand { get; private set; }    
        public ICommand AlterDataCommand { get; private set; }

        public ulong Id { get => _id; set => SetProperty(ref _id, value); }
        public uint Age { get => _age; set => SetProperty(ref _age, value); }
        public string Name { get => _name; set => SetProperty(ref _name, value); }
        public string BanJi { get => _banji; set => SetProperty(ref _banji, value); }
        public StudentInfo SelectItem { get => _studentInfo; set => SetProperty(ref _studentInfo, value); }


        public ObservableCollection<StudentInfo> Obc { get; set; } = new ObservableCollection<StudentInfo>();
        public MainViewModel(SQLiteService sQLiteService)
        {
            AddDataCommand = new AsyncRelayCommand(AddData);
            DeleteDataCommand = new AsyncRelayCommand(DeleteData);
            InitializedCommand = new AsyncRelayCommand(Initialized);
            AlterDataCommand = new AsyncRelayCommand(AlterData);

            _sQLiteService = sQLiteService;

            if(Directory.Exists(AppDataPath) == false)
            {
                Directory.CreateDirectory(AppDataPath);
            }
        }

        public async Task AddData()
        {
            if (Age > 0 && BanJi != "" && Id > 0 && Name != "")
            {
                StudentInfo e = new StudentInfo() { Age = Age, BanJi = BanJi, Id = Id, Name = Name };
                await _sQLiteService.AddData(DataPath, e);
                //if(Obc.Count < 10)
                //{
                await foreach (var item in _sQLiteService.GetData(DataPath, Obc.Count))
                {
                    Obc.Add(item);
                };
                //}
            }
        }

        public async Task DeleteData()
        {
            if (SelectItem != null)
            {
                await _sQLiteService.Delete(DataPath, SelectItem);
                Obc.Remove(SelectItem);
            }
        }

        public async Task AlterData()
        {
            await _sQLiteService.AlterData(DataPath, SelectItem);
        }

        public async Task Initialized()
        {
            await foreach (var item in _sQLiteService.GetData(DataPath, 0))
            {
                Obc.Add(item);
            }
        }

    }

    public class StudentInfo
    {
        [Column("Key"),PrimaryKey,AutoIncrement]
        public ulong Key { get; set; }

        [Column("Id")]
        public ulong Id { get; set; } = 0UL;

        [Column("Name")]
        public string Name { get; set; } = string.Empty;

        [Column("BanJi")]
        public string BanJi { get; set; } = string.Empty;

        [Column("Age")]
        public uint Age { get; set; } = 0;
    }

}
