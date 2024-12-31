using SQLite;
using StudentAll.ViewModels;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace StudentAll.SQLite
{
    public class SQLiteService
    {
        public ISQLiteAsyncConnection Connection { get; set; }
        public SQLiteService() 
        { 
        
        }

        //初始化
        private async Task<ISQLiteAsyncConnection> InitionConnection(string path)
        {
            if(Connection != null) await Connection.CloseAsync();

            Connection = new SQLiteAsyncConnection(path);
            await Connection.CreateTableAsync<StudentInfo>();

            return Connection;
        }

        public async Task Dispose()
        {
            await Connection.CloseAsync();
        }

        public async Task AddData(string path, params StudentInfo[] student)
        {
            await InitionConnection(path);

            await Connection.InsertAllAsync(student);
            await Dispose();
        }

        public async Task AlterData(string path, StudentInfo student)
        {
            await InitionConnection(path);

            StudentInfo std = (StudentInfo)await Connection.Table<StudentInfo>().FirstOrDefaultAsync(f => f.Key == student.Key);
            std.Name = student.Name;
            std.Age = student.Age;
            std.Name = student.Name;
            std.BanJi = student.BanJi;

            await Connection.UpdateAsync(std);
            await Dispose();
        }

        public async Task Delete(string path,params StudentInfo[] student)
        {
            await InitionConnection(path);

            foreach (var stu in student)
            {
                if(stu != null)
                {
                    await Connection.DeleteAsync(stu);
                }
            }

            await Dispose();
        }

        public async IAsyncEnumerable<StudentInfo> GetData(string path,int skip)
        {
            await InitionConnection(path);

            if(skip > await Connection.Table<StudentInfo>().CountAsync())
            {
                yield break;
            }
            List<StudentInfo> dataList = await Connection.Table<StudentInfo>().Skip(skip).ToListAsync();
            foreach (var item in dataList)
            {
                yield return item;
            }
        }

    }
}
