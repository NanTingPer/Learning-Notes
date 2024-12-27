using System;
using System.Collections.Generic;
using System.Runtime.Intrinsics.Arm;
using Avalonia.Controls;
using Avalonia.Controls.Templates;
using Dpa.Library.ViewModel;

namespace Dpa;

public class ViewLocator : IDataTemplate
{
    public Control? Build(object? param)
    {
        if (param is null)
            return null;

        var name2 = param.GetType().FullName!
            .Replace("Views", "View")
            .Replace("ViewModel", "Views", StringComparison.Ordinal)
            .Replace("Dpa.Library", "Dpa");
        string name = name2.Substring(0, name2.Length - 1);

        var type = Type.GetType(name);

        if (type != null)
        {
            return (Control)Activator.CreateInstance(type)!;
        }

        return new TextBlock { Text = "Not Found: " + name };
    }

    public bool Match(object? data)
    {
        return data is ViewModelBase;
    }
}