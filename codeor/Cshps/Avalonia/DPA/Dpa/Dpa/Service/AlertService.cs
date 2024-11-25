using System.Threading.Tasks;
using Dpa.Library.Services;
using Ursa.Controls;

namespace Dpa.Service;

public class AlertService : IAlertService
{
    public async Task AlertAsync(string title, string mseeage)
    {
        await MessageBox.ShowAsync(mseeage, title);
    }
}