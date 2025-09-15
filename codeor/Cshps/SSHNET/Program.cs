using Renci.SshNet;

namespace SSHNET;

internal class Program
{
    static void Main(string[] args)
    {
        using var client = new SshClient("172.27.130.65", 22, "root", "123456");
        client.Connect();
        var command = client.RunCommand("systemctl list-timers");
        var result = command.Execute();
        var splitValue = result.Split("\n");
        var tableHand = splitValue[0];
        const string handNEXT = "NEXT";
        const string handLEFT = "LEFT";
        const string handLAST = "LAST";
        const string handPASSED = "PASSED";
        const string handUNIT = "UNIT";
        const string handACTIVATES = "ACTIVATES";

        var cresult = new CommandResult(result);
        foreach (var handOffset in cresult.ColumnOffset) {
            Console.WriteLine(handOffset);
        }

        foreach(var item in cresult.Columns) {
            Console.WriteLine();
            foreach (var v in cresult.ColValues(item)) {
                Console.WriteLine(v);
            }
        }
        ;

        Console.WriteLine();
        Console.WriteLine(result);
    }
}
