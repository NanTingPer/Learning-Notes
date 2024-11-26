using System.Threading.Tasks;
using Dpa.Library.Services;
using Ursa.Controls;

namespace Dpa.Service;

public class AlertService : IAlertService
{
    public async Task AlertAsync(string title, string message)
    {
        // MessageBox.ShowAsync(message, title,button:MessageBoxButton.OK);
        // MessageBox.ShowAsync(message: message, title: title, button: MessageBoxButton.OK);
        MessageBox.ShowAsync(message, title, MessageBoxIcon.Error, button: MessageBoxButton.OK);
    }
}