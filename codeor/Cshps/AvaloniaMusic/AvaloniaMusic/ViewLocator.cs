using System;
using System.Diagnostics;
using Avalonia.Controls;
using Avalonia.Controls.Templates;
using AvaloniaMusic.UserControl;
using AvaloniaMusic.ViewModels;

namespace AvaloniaMusic;

public class ViewLocator : IDataTemplate
{
    public Control? Build(object? param)
    {
        if (param is null)
            return null;

        if (param.GetType() == typeof(AlbumViewModel))
        {
            Debug.WriteLine("Yes!");
            return (Control)Activator.CreateInstance(typeof(AlbumView))!;
        }
        
        var name = param.GetType().FullName!.Replace("ViewModel", "View", StringComparison.Ordinal);
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