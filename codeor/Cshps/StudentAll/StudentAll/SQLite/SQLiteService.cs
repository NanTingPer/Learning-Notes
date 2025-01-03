#pragma warning disable CS8602
#pragma warning disable CS8618
#pragma warning disable CS8600
#pragma warning disable CS8601
#pragma warning disable CS8603
using SQLite;
using StudentAll.ViewModels;
using System;
using System.Collections.Generic;
using System.Linq.Expressions;
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
        private async Task<ISQLiteAsyncConnection> InitionConnectionAsync(string path)
        {
            if(Connection != null) await Connection.CloseAsync();

            Connection = new SQLiteAsyncConnection(path);
            await Connection.CreateTableAsync<StudentInfo>();
            
            return Connection;
        }

        public async Task DisposeAsync()
        {
            await Connection.CloseAsync();
        }

        public async Task AddDataAsync(string path, params StudentInfo[] student)
        {
            await InitionConnectionAsync(path);
            StudentInfo stu = student[0];

            int r = await Connection.Table<StudentInfo>().Skip(0).Where(f => f.Id == stu.Id).CountAsync();
            if (r <= 0)
            {
                await Connection.InsertAllAsync(student);
            }
            await DisposeAsync();
        }

        public async Task AlterDataAsync(string path, StudentInfo student)
        {
            await InitionConnectionAsync(path);

            StudentInfo std = (StudentInfo)await Connection.Table<StudentInfo>().FirstOrDefaultAsync(f => f.Key == student.Key);
            std.Name = student.Name;
            std.Age = student.Age;
            std.Name = student.Name;
            std.BanJi = student.BanJi;
            std.Adder = student.Adder;
            std.AdderToAdder = student.AdderToAdder;

            await Connection.UpdateAsync(std);
            await DisposeAsync();
        }

        public async Task DeleteAsync(string path,params StudentInfo[] student)
        {
            await InitionConnectionAsync(path);

            foreach (var stu in student)
            {
                if(stu != null)
                {
                    await Connection.DeleteAsync(stu);
                }
            }

            await DisposeAsync();
        }

        public async IAsyncEnumerable<StudentInfo> GetDataAsync(string path,int skip, long id = long.MaxValue, string name = "", string banji = "", short age = short.MaxValue)
        {
            await InitionConnectionAsync(path);

            if(skip > await Connection.Table<StudentInfo>().CountAsync())
            {
                yield break;
            }

            List<StudentInfo> dataList;

            bool banjiF =   !string.IsNullOrWhiteSpace(banji);              //true 说明有
            bool idF    =   (id != long.MaxValue);                          //true 说明有
            bool ageF   =   (age != short.MaxValue);                        //true 说明有
            bool nameF  =   !string.IsNullOrWhiteSpace(name);               //true 说明有


            if (banjiF || idF || ageF || nameF)
            {
                Expression<Func<StudentInfo, bool>> exp = stu =>
                    (!banjiF    ||  stu.BanJi.Contains(banji))   &&
                    (!idF       ||  stu.Id == id)                &&
                    (!ageF      ||  stu.Age == age)              &&
                    (!nameF     ||  stu.Name.Contains(name));


                dataList = await Connection.Table<StudentInfo>().Where(exp).ToListAsync();
            }
            else
            {
                dataList = await Connection.Table<StudentInfo>().Skip(skip).ToListAsync();
            }

            foreach (var item in dataList)
            {
                yield return item;
            }
            await DisposeAsync();
        }

    }
}
