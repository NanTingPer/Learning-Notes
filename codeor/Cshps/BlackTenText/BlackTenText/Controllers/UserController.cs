using BlackTenText.EntityModels;
using BlackTenText.Services;
using Microsoft.AspNetCore.Mvc;

namespace BlackTenText.Controllers;

[Route("api/[controller]")]
[ApiController]
public class UserController(UserService service) : ControllerBase
{
    [HttpPost("region")]
    public async Task<IResult> RegionUser(UserDto user)
    {
        return await service.Region(user);
    }

    [HttpPost("login")]
    public async Task<IResult> Login(UserDto user)
    {
        return await service.Comparison(user);
    }
}
