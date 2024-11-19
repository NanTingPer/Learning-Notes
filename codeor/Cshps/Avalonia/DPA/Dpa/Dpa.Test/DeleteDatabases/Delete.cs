using Dpa.Library.Services;

namespace Dpa.Test.DeleteDatabases;
public class Delete
{
    public static void Del() => File.Delete(PoetrySty.DbPath);
}