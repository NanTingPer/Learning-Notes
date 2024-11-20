using Dpa.Library.Services;
using Dpa.Library.Task;

namespace Dpa.Test.DeleteDatabases;
public class Delete
{
    public static void Del() => File.Delete(PathFile.getPath());
}