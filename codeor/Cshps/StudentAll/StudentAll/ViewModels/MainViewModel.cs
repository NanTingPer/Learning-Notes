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
            AddDataCommand = new AsyncRelayCommand(AddData);
            DeleteDataCommand = new AsyncRelayCommand(DeleteData);
            InitializedCommand = new AsyncRelayCommand(Initialized);
            AlterDataCommand = new AsyncRelayCommand(AlterData);
            SelectDataCommand = new AsyncRelayCommand(SelectData);

            _sQLiteService = sQLiteService;

            if(Directory.Exists(AppDataPath) == false)
            {
                Directory.CreateDirectory(AppDataPath);
            }
        }

        public async Task AddData()
        {
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
            await _sQLiteService.AddData(DataPath, e);
            await foreach (var item in _sQLiteService.GetData(DataPath, Obc.Count))
            {
                Obc.Add(item);
            };
        }

        public async Task SelectData()
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
            

            await foreach (var item in _sQLiteService.GetData(DataPath, 0, id, Name, BanJi, age))
            {
                Obc.Add(item);
            };

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

        public void Select()
        {

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
    }

}
