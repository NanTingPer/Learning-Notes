using Dpa.Library.Models;

namespace Dpa.Library.Services;

public interface IToDayPoetrySty
{
    Task<ToDayPoetry> GetToDayPoetry();
}