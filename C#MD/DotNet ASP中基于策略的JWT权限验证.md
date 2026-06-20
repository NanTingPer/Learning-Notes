# .NET ASP中基于策略的JWT权限验证

本文章基于[世纪文明 JWT验证](https://www.bilibili.com/video/BV1bC411479D/)

## 准备内容

- Sdk为Microsoft.NET.Sdk.Web
- 引入包Microsoft.AspNetCore.Authentication.JwtBearer



## 策略参数和处理器

在Asp的Authentication中，我们在添加策略时，其方法签名要求的是一个`IAuthorizationRequirement`对象，但当我们去实现这个接口的时候，我们其实会发现，这个接口没有任何需要实现的属性和方法，那么这个接口是做什么的呢？

其作用是用于使策略处理器拥有强类型支持，我们可以不对他添加任何内容，在官方教程中，其用于判断年龄是否符合要求。也就是说，我们可以在这个`IAuthorizationRequirement`对象中，定义我们策略所需要的数据，而具体的策略通过要求，由于此类型对象初始化时提供。这样我们就可以在`AuthorizationHandler`中，判断给定请求中的验证数据，满足验证参数的需求。

如下面是一个判断用户身份的验证需求。

```cs
/// <summary>
/// 用户验证参数请求
/// </summary>
public class UserRoleRequirement(params UserRole[] roles) : IAuthorizationRequirement
{
    /// <summary>
    /// 角色要求
    /// </summary>
    public List<UserRole> Roles { get; init; } = [.. roles];
}
```

在这个`UserRoleRequirement`中，我们使用主构造函数定义了一组用户角色参数。要求在创建这个对象时，你需要指定在什么样的角色下，你这个请求会被放行，而这就是一个策略。当然，具体实现需要配合验证处理器。

下面是对于这个策略类型的处理器实现。

```cs
/// <summary>
/// 用户验证处理器
/// </summary>
public class UserAuthorizationHandler : AuthorizationHandler<UserRoleRequirement>
{
    /// <summary>
    /// 认证请求
    /// </summary>
    protected override Task HandleRequirementAsync(AuthorizationHandlerContext context, UserRoleRequirement requirement)
    {
        var userRoleString = context.User.FindFirst(CustomClaimTypes.USER_ROLE)?.Value;
        if (userRoleString == null) {
            return Task.CompletedTask;
        }

        var roles = JsonSerializer.Deserialize<List<UserRole>>(userRoleString) ?? [];
		// 只要用户身份中，包含要求的参数 既通过
        // 对于Admin其拥有admin和user身份
        // 对于User其只拥有user身份，而Admin要求只包含admin身份，因此普通用户不会通过
        if (roles.Any(f => requirement.Roles.Any(f1 => f == f1))) {
            context.Succeed(requirement);
            return Task.CompletedTask;
        } 
        return Task.CompletedTask;
    }
}
```

策略处理器会根据你终结点（控制器中的一个API）所使用的策略（[Authorize(Policy = ...)]）来选择性调用，因为我们在使用策略时，需要注册策略，此处的`Policy=...`省略的便是策略名称，如果你注册策略时，这个名称对应的策略需求是`UserRoleRequirement`，那么便会使此处理器。

在处理器中，如果验证通过则调用`context.Succeed(requirement);`，不操作则默认失败。对于策略的处理，我们无需在意上下文中的参数从何而来，在启用验证后，其会被框架自动填充。

也就代表，验证类型和验证处理器是完全解偶的，我们只需能够从用户请求的信息中，获取对应需要被验证的信息即可。



## 注册策略和处理器

对策略的使用无需引入任何额外的外部包，这是内置支持的。

我们需要在`Program.cs`或者某个类型的方法中，为`WebApplicationBuild`的`Services`注册策略。

```cs
builder.Services.AddAuthorizationBuilder()
    .AddPolicy(PolicyTypes.ADMIN, policy => policy.AddRequirements(Policy.AdminPolicyRequirement))
    .AddPolicy(PolicyTypes.USER, policy => policy.AddRequirements(Policy.UserPolicyRequirement))
    ;
```

`AddPolicy`要求传入`策略名称, 策略配置`。此处的`PolicyTypes.ADMIN`实际上是一个常量字符串。而`Policy`是一个自定义类型，里面创建好了策略需求。在注册时我们可以看到，我们为每个策略添加的需求都是`UserRoleRequirement`类型，也就意味着，在验证时，都会使用`UserAuthorizationHandler`，此处的策略中，`admin`需要用户身份为`Admin`才能访问，而`user`策略无论是`Admin`身份，还是`User`身份都能访问。注:  用户身份标识，在颁发JWT令牌时填入

我们`UserAuthorizationHandler.HandleRequirementAsync`中的`requirement`实际上就是我们添加策略时，传入的那个对象。所以他是用来决定策略通过要求的。

```cs
public class PolicyTypes
{
    public const string ADMIN = "admin";
    public const string USER = "user";
}

public class Policy
{
    public static UserRoleRequirement AdminPolicyRequirement { get; } = new UserRoleRequirement(UserRole.Admin);
    public static UserRoleRequirement UserPolicyRequirement { get; } = new UserRoleRequirement(UserRole.User);
}
```

注册完成策略后，还需要注册策略处理器，其生命周期为单例。

```cs
builder.Services.AddSingleton<IAuthorizationHandler, UserAuthorizationHandler>();
```



## 注册JWT服务

由于我们引入了`Microsoft.AspNetCore.Authentication.JwtBearer`包，因此我们在使用`AddAuthentication`后可以`AddJwtBearer`，表示说，我们启用了Jwt的认证，而无论是何种验证类型，策略处理器都是通用的。

我们可以在`AddJwtBearer`中，通过委托配置`JWT`验证参数。对于其`scheme`参数我们可以填写`JwtBearerDefaults.AuthenticationScheme`也可以不用填写，表示请求头中`Authentication`的起始字符。如使用`JwtBearerDefaults.AuthenticationScheme`则为`Bearer Jwt`，需要前端在添加验证头时，添加`Bearer `字串。

在`AddJwtBearer`中，如果需要使用到配置中的值，可以`builder.Configuration.GetSection`直接进行获取，但如果需要利用依赖注入容器中的对象属性值，则需要使用另一种方法。对于添加jwt我们使用默认实现。

```cs
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme).AddJwtBearer();// Bearer
builder.Services.AddOptions<JwtBearerOptions>(JwtBearerDefaults.AuthenticationScheme)
    .Configure<IServiceProvider>((opt, provider) => {
        var jwtService = provider.GetService<JwtService>();
        opt.TokenValidationParameters = jwtService!.JwtValidationParameters;
    });
```

对于验证参数，需要与颁发时的参数一致。参数有很多，具体可以参阅文档[TokenValidationParameters](https://learn.microsoft.com/en-us/dotnet/api/microsoft.identitymodel.tokens.tokenvalidationparameters?view=msal-web-dotnet-latest)。如我的参数为

```cs
public TokenValidationParameters JwtValidationParameters => new TokenValidationParameters()
{
    ValidateIssuer = true,
    ValidateAudience = true,
    ValidIssuer = configService.JwtIssuer,
    ValidAudience = configService.JwtAudience,
    ValidateIssuerSigningKey = true,
    IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(configService.JwtKey))
};
```

最主要的参数其实是`IssuerSigningKey`因为这是你的私钥。在验证此token的有效性时，其会根据你所给定的验证参数进行验证。



## 使用验证服务

在注册完策略和Jwt验证之后，我们仅仅只是注册了对应的服务，而未使用他们。因此我们需要在`WebApplication`对象中进行使用。

```cs
app.UseAuthentication();
app.UseAuthorization();  //先认证 再授权 
```

此调用需要在`UseRouting`之后进行，因为如果还未进行路由就认证，那么如何获取此终结点的元数据（特性标记等）呢？

同时需要先认证再授权，实际上在注册策略时，我们就可以观察到，其使用的是`AddAuthorizationBuilder` 授权构建器。他使用的是jwt令牌中提取出来的用户信息，而jwt令牌是在认证阶段处理的。即，先确认身份，再确认权限。

在使用完成后，我们就可以为我们的终结点（控制器中的方法）添加认证特性了。如，我需要我接口只有管理员能够使用。

```cs
[Authorize(Policy = PolicyTypes.ADMIN)]
[HttpPost("addOrReplace")]
public async Task<ActionResult<BaseResult<string>>> AddOrReplace([FromBody] PostInfo blog)
```



## 颁发JWT令牌

光有验证和授权还不行，外部如何获取符合要求的token呢？因此我们还需要对外部发布能够获取token的终结点，他可以是在用户登录的同时携带，也可以是用户使用已颁发的token重新颁发（这样可以避免重新登录，或携带密码进行刷新token）。

还记得在策略处理器中使用的`User`吗，那其实是从令牌的`Claims`中提取而来，因此我们颁发令牌的核心是，给予用户身份。

如何创建令牌？实际上完全可以根据`JwtSecurityTokenHandler.WriteToken`方法的参数一步一步向上推出，也可以从官方文档中窥看。对于[JWT](https://www.jwt.io/introduction#what-is-json-web-token-structure)的结构，可根据链接查看。

如，下列根据User创建了一个令牌，有效时间为2小时，同时`Claims`包含了用户的角色信息。

```cs
public string CreateToken(User user)
{
    var roleClaim = new Claim(CustomClaimTypes.USER_ROLE, JsonSerializer.Serialize(user.Roles));
    var ssk = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(configService.JwtKey));
    var scredntial = new SigningCredentials(ssk, SecurityAlgorithms.HmacSha256);

    var jwtSecurity = new JwtSecurityToken(
        issuer: configService.JwtIssuer, 
        audience: configService.JwtAudience, 
        claims: [roleClaim], 
        signingCredentials: scredntial,
        expires: DateTime.UtcNow.AddHours(2));
    return handler.WriteToken(jwtSecurity);
}

```

而接下来，只需要在控制器中将此方法暴露出去就可以颁发JWT令牌了。



## 疑惑

你可能会疑惑，认证中间件中的令牌从何而来。实际上我们无需关注整个流程的闭环，我们只需关注数据处理的闭环，因为验证的内容，是从外部携带而来，而外部是未知的。

在此文章中，我们完成了 `颁发 -> 认证` 的闭环，此时数据已经闭环。同时我们也无需关心内部如何处理，这是框架给我们的额外压力，也是框架所带来的便利。

压力来自于失去了对数据流的完全掌控，便利来自于我们无需再写繁琐的解析代码。



# 其他内容

[认证与授权](https://learn.microsoft.com/en-us/aspnet/core/fundamentals/minimal-apis/security?view=aspnetcore-10.0)

为什么是先认证，再授权，因为要先确认用户身份，如是否登录、令牌是否有效，在认证完用户身份后，授权中间件才能确定你的身份是否满足授权要求。否则授权中间件得不到用户信息，则无法完成授权。



# 教学脚本 - 授权

展示路径: 认证特性 -> 添加认证服务 -> 添加策略（等报错）-> 创建策略需求对象 -> 创建策略处理器 -> 在策略中创建策略需求

欢迎收看本期视频，在视频开始之前呢，我们需要准备好一个包含一些控制器的.NET ASP的项目。这边已经创建好了。

对于身份认证，我们一步一步来探索，首先定位到一个需要进行身份认证的终结点上，为其添加`Authorize`特性，在这个特性中，我们可以看到非常多的属性，而我们使用的是基于策略的认证，也可以看到其包含一个`Policy`的属性，并且是`string`的类型，这个`string`是什么呢？其实就是策略的名称。

既然我们知道特性要求我们传递一个策略名称，那我们肯定需要定义策略，才能知道名称。当然，我们可以先为他指定一个名称，再去声明策略。比如这边个一个`admin`，表示使用管理员策略。

接下来，我们来到`Program.cs`或者你依赖注入服务注册的地方。去注册我们的授权服务。使用`builder.Services.AddAuthorizationBuilder()`获取授权构造器，就可以构造我们的授权服务了，由于我们使用基于策略的授权，因此可以使用`AddPolicy`添加一条策略，我们可以查看其参数需求，实际上从参数需求上就可以窥见如何定义了，如第一个参数`name`实际上就是我们在使用`Authorize`特性时所使用的名称， 在两个重载中，我们使用带有`Action<AuthorizationPolicyBuilder>`的重载，使用`builder`可以去添加我们的策略需求。在需求中其要求使用的类型是`IAuthorizationRequirement`。那么我们是不是可以去实现这个接口，然后`new`一个进行。所以我们创建一个类型来实现这个接口。

在实现这个接口时，我们发现一个奇怪的问题，为什么这个接口没有让我们实现任何东西？因为这个接口是一个标志接口。要让他发挥意义，我们还需要实现一个授权处理器，可以看到授权处理器的泛型需求正是`IAuthorizationRequirement`，并且抽象要求我们实现一个`HandleRequirementAsync` 并且其传入了泛型指定的需求，那么意思就很明显了。也就是说我们需要根据授权需求，去决定这个请求携带的身份，是符合 然后进行放行。

那我们刚刚的`IAuthorizationRequirement`实现要做什么也很明显了，我们需要在里面声明一些内容，供处理器使用，并使处理器能根据此实现识别请求是否放行。

来到`Program.cs`，现在，我们已经实现了授权需求，那我们就可以为策略添加需求了，直接`new`需求对象就可以，在执行处理器时，框架会直接把你`new`的这个需求，传递给处理器的那个参数，所以我们处理器使用的那个需求对象，就是你这里创建的。

声明完策略后，我们还需要注册策略处理器。`builder.Services.AddSingleton<IAuthorizationHandler, UserAuthorizationHandler>();` 注册为单例服务。



# 教学脚本 - 认证

## 前言

在编写完成授权后就是认证了，即 如何让授权服务，知道你是否是合法用户。而认证就是那个中间件。同时，我们现在启用了授权，而没有什么东西能够基于用户权限，没有权限怎么授权？接下来我们就需要编写认证标识符的颁发以及辨别了。

此内容使用JWT认证服务，为此，需要引入`Microsoft.AspNetCore.Authentication.JwtBearer`包。如要自定义认证中间件，以及JWT的基本认识，可以查看[世纪文明 JWT验证](https://www.bilibili.com/video/BV1bC411479D/)。

## 颁发JWT

要让外部获取令牌，我们需要在控制器中暴露一个接口，供外部使用，你可以把他写在用户权限相关的控制器中，这个令牌的获取是有许多途径的。

1. 登录成功后携带
2. 使用有效的旧令牌，对其进行续期

我们在控制器中创建一个`CreateToken`的方法，其接收一个`用户`对象作为传入参数。既然已经走到这一步，说明你前面的身份验证已经通过了，也就是 用户是登录成功了的。所以我们就可以根据用户的信息，去创建令牌。

值得注意的是，Claim的内容对于任何人都是公开的，因此，请不要存放任何敏感信息，如用户密码等。其内容仅用于授权服务确定身份。以及在执行一些操作时，知道是某个用户发起的。

```cs
public string CreateToken(User user)
{
    var roleClaim = new Claim(ClaimTypes.Role, JsonSerializer.Serialize(user.Roles));
    var ssk = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(configService.JwtKey));
    var scredntial = new SigningCredentials(ssk, SecurityAlgorithms.HmacSha256);

    var jwtSecurity = new JwtSecurityToken(
        issuer: configService.JwtIssuer, 
        audience: configService.JwtAudience, 
        claims: [roleClaim], 
        signingCredentials: scredntial,
        expires: DateTime.UtcNow.AddHours(2));
    var handler = new JwtSecurityTokenHandler();
    return handler.WriteToken(jwtSecurity);
}
```

使用新的JwtToken

```cs
var jwth = new JsonWebTokenHandler();
var skey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes("66awdawfawfawdawdawdawfawdawdwadawdawdsasdsadsadasdasdasd667"));
var sc = new SigningCredentials(skey, SecurityAlgorithms.HmacSha256);
var tokenDesc = new SecurityTokenDescriptor()
{
    Subject = new ClaimsIdentity(new List<Claim>
    {
        new Claim(ClaimTypes.Role, "admin"),
        new Claim(ClaimTypes.Role, "user")
    }),
    SigningCredentials = sc
};

var token = jwth.CreateToken(tokenDesc);
```



## 验证JWT

颁发完JWT令牌后，我们就需要在请求流中去验证用户携带的令牌了。只需要在`Program.cs`或其他依赖注入容器中，添加`JWT`认证服务就可以了。

这里的`JwtBearerDefaults.AuthenticationScheme`是什么？我们Web端在请求接口时，如果这是一个需要进行身份验证的接口，那么他在请求头中必须包含`Authentication`头，而`Scheme`是这个头的前几个字符，表现出来就是 `Scheme Token`，在定义中 其用于表示验证方式。

对于`TokenValidationParameters`，我们在创建令牌时怎么填，这里就怎么填，表示的是哪些参数是需要被验证的。在验证时，如果发现参数对不上，那就说明这个`Token`是无效的。以`Issuer`为例，`ValidateIssuer`表示是否验证此参数，`ValidIssuer` 表示如果要验证，那么他的值应该是什么。其他参数同理。

最重要的是`ValidateIssuerSigningKey`，因为这你的私钥。

```cs
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme).AddJwtBearer(options => {
    options.TokenValidationParameters = new Microsoft.IdentityModel.Tokens.TokenValidationParameters()
    {
        ...
    };
});
```

至此，我们的Web应用便支持了基于策略的JWT权限验证



# 教学口语

大家好，今天带大家来认识一下ASP .NET中的身份认证，顺便了解一下中间件。

在开始之前呢，我们需要知道一些理论的内容，也希望大家能跟着一起敲一敲。同时准备一个新建的ASP .NET API项目。

需要知道的理论不多，只需要知道 授权和认证的关系就可以了。

那么什么是认证呢？认证就是用来确认你有没有一个合法的身份，就比如你在bilibili里面想进入个人主页，未登录的话，你无法点进去，因为你在bilibili不是一个合法的用户，bilibili也没有你的信息，就不让你访问。但是你登录后就可以了。

那什么是授权呢？我们登录后可以看bilibili的1080p的视频，但是想看4k高清，你得要VIP，但是你只是普通用户，那么我们就没有权限访问，授权那边就不给过。

那我们就知道了，认证就是确认用户身份，授权就是确认用户权限。所以在验证权限之前，需要先验证身份。





# 认证代码

## UserController

```cs
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace JWTRole.Controllers;

[ApiController]
[Route("user")]
public class UserController : ControllerBase
{
    //如果一个ClaimType包含多个值，不建议使用集合序列化，而是以多Claim的方式，及 List<Claim>传递，库在处理时，会自动解析为数组
    //"http://schemas.microsoft.com/ws/2008/06/identity/claims/role": [
    //  "admin",
    //  "user"
    //],

    [HttpPost("login")]
    public ActionResult<RequestResult> Login()
    {
        var jwth = new JwtSecurityTokenHandler();
        var claim = new Claim(ClaimTypes.Role, "admin");
        var cs = new List<Claim>() { claim, new Claim(ClaimTypes.Role, "user") };
        var skey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes("66awdawfawfawdawdawdawfawdawdwadawdawdsasdsadsadasdasdasd667"));

        var sc = new SigningCredentials(skey, SecurityAlgorithms.HmacSha256);
        var tokenDesc = new JwtSecurityToken(issuer: "aabb", audience: "aabb", claims: cs, signingCredentials: sc);
        var token = jwth.WriteToken(tokenDesc);

        return Ok(new RequestResult() { Data = token });
    }

    [HttpPost("safety")]
    [Authorize(Roles = "admin")]
    public ActionResult<string> Safety()
    {
        return Ok(new RequestResult() { Data = "safety" });
    }

    public class RequestResult
    {
        public string Data { get; set; } = "";
    }
}


public class UserModel
{
    public string Id { get; set; } = Guid.NewGuid().ToString();
    public string Role { get; set; } = "user";
    public string Name { get; set; } = "";
    public string Password { get; set; } = "";
}

public class UserDto
{
    public string Role { get; set; } = "";
    public string Name { get; set; } = "";
}
```



## AddJwtBearer

```cs
builder.Services.AddAuthentication().AddJwtBearer(opt => {
    opt.TokenValidationParameters = new Microsoft.IdentityModel.Tokens.TokenValidationParameters
    {
        ValidateIssuer = false,
        ValidateLifetime = false,
        ValidateAudience = false,
        ValidateIssuerSigningKey = true,
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes("66awdawfawfawdawdawdawfawdawdwadawdawdsasdsadsadasdasdasd667"))
    };
});
```

## 两个英文

|                |      |
| -------------- | ---- |
| Authentication | 认证 |
| Authorization  | 授权 |

