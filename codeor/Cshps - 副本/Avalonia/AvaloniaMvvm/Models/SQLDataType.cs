using System;
using SQLite;

namespace AvaloniaMvvm.Models;

public class SQLDataType
{
    //PrimaryKey是作为键
    //AutoIncrement是自增
    [PrimaryKey,AutoIncrement]
    public int Id { get; set; }
    public string Name { get; set; } = String.Empty;
    public SQLDataType(){}
}