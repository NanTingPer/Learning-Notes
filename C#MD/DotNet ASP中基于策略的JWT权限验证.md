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