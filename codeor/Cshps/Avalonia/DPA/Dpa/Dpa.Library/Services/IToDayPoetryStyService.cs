using Dpa.Library.Models;

namespace Dpa.Library.Services;

public interface IToDayPoetryStyService
{
    Task<ToDayPoetry> GetToDayPoetryAsync();
}