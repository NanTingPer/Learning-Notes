using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TerrariaServerSystem.Exceptions;

public class NotVariable(string message) : Exception
{
    public override string Message => message;
}
