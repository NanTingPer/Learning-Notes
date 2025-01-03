#pragma warning disable CS8602
#pragma warning disable CS8618
#pragma warning disable CS8600
#pragma warning disable CS8601
#pragma warning disable CS8603
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

        private string _id = string.Empty;
        private string _name = string.Empty;
        private string _banji = string.Empty;
        private string _age = string.Empty;
        private string _viewText = string.Empty;
        private StudentInfo _studentInfo;
        private int skip = 0;

        public static string AppDataPath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),"StudentAll");
        public static string DataPath = Path.Combine(AppDataPath, "StudentInfo");


        public ICommand AddDataCommand { get; private set; }
        public ICommand DeleteDataCommand { get; private set; }
        public ICommand InitializedCommand { get; private set; }    
        public ICommand AlterDataCommand { get; private set; }
        public ICommand SelectDataCommand { get; private set; }

        public string Id { get => _id; set => SetProperty(ref _id, value); }                                //学号
        public string Age { get => _age; set => SetProperty(ref _age, value); }                             //年龄
        public string Name { get => _name; set => SetProperty(ref _name, value); }                          //名字
        public string BanJi { get => _banji; set => SetProperty(ref _banji, value); }                       //班级
        public string ViewText { get => _viewText; set => SetProperty(ref _viewText, value); }              //错误提示
        public StudentInfo SelectItem { get => _studentInfo; set => SetProperty(ref _studentInfo, value); } //选中信息


        public ObservableCollection<StudentInfo> Obc { get; set; } = new ObservableCollection<StudentInfo>();
        public MainViewModel(SQLiteService sQLiteService)
        {
            AddDataCommand = new AsyncRelayCommand(AddDataAsync);
            DeleteDataCommand = new AsyncRelayCommand(DeleteDataAsync);
            InitializedCommand = new AsyncRelayCommand(InitializedAsync);
            AlterDataCommand = new AsyncRelayCommand(AlterDataAsync);
            SelectDataCommand = new AsyncRelayCommand(SelectDataAsync);

            _sQLiteService = sQLiteService;

            if(Directory.Exists(AppDataPath) == false)
            {
                Directory.CreateDirectory(AppDataPath);
            }
        }

        public async Task AddDataAsync()
        {
            ViewText = "";
            short age;
            long id;

            if (!short.TryParse(Age, out age))
            {
                ViewText = "年龄错误";
                return;
            }

            if (!long.TryParse(Id, out id))
            {
                ViewText = "学号错误";
                return;
            }

            //20245230315
            //20245230315
            if (id > 99999999999 || id < 10000000000 || age > 120 || age <= 0 || BanJi == "" || Name == "")
            {
                ViewText = "学号过大/过小 或 年龄不正确 或 班级不能为空 或 名字不能为空";
                return;
            }

            StudentInfo e = new StudentInfo() { Age = age, BanJi = BanJi, Id = id, Name = Name };
            await _sQLiteService.AddDataAsync(DataPath, e);
            await foreach (var item in _sQLiteService.GetDataAsync(DataPath, Obc.Count))
            {
                Obc.Add(item);
            };
        }

        public async Task SelectDataAsync()
        {
            short age;
            long id;

            if (!short.TryParse(Age, out age))
            {
                age = short.MaxValue;
            }

            if (!long.TryParse(Id, out id))
            {
                id = long.MaxValue;
            }

            for (int i = Obc.Count; i > 0; i--)
            {
                Obc.RemoveAt(i-1);
            }
            

            await foreach (var item in _sQLiteService.GetDataAsync(DataPath, 0, id, Name, BanJi, age))
            {
                Obc.Add(item);
            };

        }

        public async Task DeleteDataAsync()
        {
            if (SelectItem != null)
            {
                await _sQLiteService.DeleteAsync(DataPath, SelectItem);
                Obc.Remove(SelectItem);
            }
        }

        public async Task AlterDataAsync()
        {
            await _sQLiteService.AlterDataAsync(DataPath, SelectItem);
        }

        public async Task InitializedAsync()
        {
            await foreach (var item in _sQLiteService.GetDataAsync(DataPath, 0))
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
        public long Id { get; set; } = 0L;

        [Column("Name")]
        public string Name { get; set; } = string.Empty;

        [Column("BanJi")]
        public string BanJi { get; set; } = string.Empty;

        [Column("Age")]
        public short Age { get; set; } = 0;

        [Column("Adder")]
        public string Adder { get; set; } = string.Empty;

        [Column("AdderToAdder")]
        public string AdderToAdder { get; set; } = string.Empty;
    }

}
