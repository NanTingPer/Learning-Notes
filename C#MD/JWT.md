# JWT

## 组成

> 三部分

`Hash算法`.`Claims 用户角色信息`.`Claims哈希 用户信息`

对于`Claims角色信息` 用第一个`Hash算法`进行解密，因此是明文的，而用户信息，使用私钥解密。
在`角色信息`中不应存放重要信息，因为他是明文的。



## 验证

使用私钥解密 `Claims哈希 用户信息`的值，或者比对其值与加密后的值是否一样。如果相同则这个`JWT`是有效的。



## 使用

由于`JWT`验证方只需要拥有`私钥`就可以验证令牌，因此无需重服务器中获取`token`

1. 客户端使用`API` / `终结点` 进行获取`Token`，在`注册端`(`服务端`) 创建`Token`

   ```cs
   //这里clientId可以改成 userInfo 判断用户权限后，在token负载中存入
   pubilc static string CreateToken(string clientId) 
   {
       //1. 安全算法
       //2. 负载 (claims) 包含信息
       //3. 签名密钥
      
       //第一步 创建签名凭证
       //私钥的获取可以使用Config文件进行定义 使用.NET ASP的依赖注入，注入IConfiguration可以获取配置
       //HmacSha256Signature 算法至少需要32个字符
       //签名密钥需要对称认证
       var signingCredentials = new SigningCredentials(new SymmetricSecurityKey(Encoding.UTF8.GetBytes("私钥")), SecurityAlgorithms.HmacSha256Signature); 
       
       
       //第二步 创建负载
       var claimsDictionary = new Dictionary<string, object>
       {
           { "client_id", clientId }, //这里可以放用户权限信息
       }
       
       //第三步 创建Token
       var toeknDescriptor = new SecurityTokenDescriptor//创建token描述符 用于tokenHandler创建token
       {
           //这里面包含 上面说的三个核心特征
           SigningCredentials = signingCredentials;
           Claims = claimsDictionary,
           Expires = DateTime.UtcNow.AddMinutes(10), //过期时间(到这个时间过期 而不是指定时长)
           NotBefore = DateTime.UtcNow, //合适可以使用 (创建时)
       } 
       
       var tokenHandler = new JsonWebTokenHandler(); //来自微软的Identity WebToken包
       return tokenHandler.CreateToken(toeknDescriptor);
   }
   ```

2. API应用

   > 使用NuGet包 / 外部中间件

   ```cs
   //在依赖注入中添加 JwtBearer 这个是使用NuGet包实现的
   builder.Services.AddAuthentication("Bearer")
       .AddJwtBearer("Bearer", options => {
           options.Authority = "http://127.0.0.1:5141"; //验证服务器
           options.Audience = "认证API";
       })
   ```

   > 手动实现

   - 创建一个特性， 并实现接口

   ```cs
   public class JwtTokenAuthFilterAttribute : Attribute, IAsyncAuthorizationFilter
   {
       public async Task OnAuthorizationAysnc(AuthorizationFilterContext context)
       {
           //1. 获取请求头的认证部分
           
           //2. 删除 `Bearer` 字符串
           
           //3. 获取Token内容，取出签名密钥
           
           //4. 进行Token认证
       }
   }
   ```

   - 获取请求头

   ```cs
   if(!context.HttpContext.Request.Headers.TryGetValue("Authorization", out var authHander)){
       context.Result = new NotFountResult();
       return;
   }
   string token = autHander.ToString(); // ReadAsString()
   ```

   - 获取`Bearer` 部分

   ```cs
   if(!token.StartsWith("Bearer ", StringComparison.OrdinalIgnoreCase)) {//不区分大小写
       context.Result = new NotFount();
       return;
   }
   token = token["Bearer ".Length ..];//  .Substring("Bearer ".Length);
   ```

   - 使用配置获取签名密钥

   ```cs
   var config = context.HttpContext.RequestServices.GetService<IConfiguration>(); //或者依赖注入
   var securityKey = config["key"]; // ? 空处理
   ```

   - 验证令牌 创建验证方法

   ```cs
   public static async Task<bool> VerifyTokenAsync(string token, string securityKey)
   {
       if(string.IsNullOrWhiteSpace(token) || string.IsNullOrWhiteSpace(securityKey)){
           return false;
       }
       var securityKeyBytes = Encoding.UTF8.GetBytes(securityKey); //获取key字节
       var tokenHander = new JsonWebTokenHandler();
       
       var validationParameters = new TokenValidationParameters//创建验证参数
       {
           ValidateIssuerSigningKey = true, //验证私钥
           IssuerSigningKey = new SymmetricSecurityKey(securityKeyBytes), //传递私钥用于实际验证
           ValidateIssuer = false, //是否验证 发布方
           ValidateAudience = false, //是否验证 用户
           ValidateLifetime = true, //验证有效时间
           ClockSkew = TimeSpan.Zero //时差
       };
       
       try {
           var result = await tokenHander.ValidateTokenAsync(token, validationParameters); //返回验证结果 同时处理异常
           return result.IsValid;
       } catch {
           return false;
       }
   }
   ```

   - 使用验证方法

   ```cs
   if(!(await VerifyTokenAsync(token, securityKey)){
       context.Result = new NotFountResult();
       return;
   }
   ```

   - 为`api`端点 / `终结点` 使用

   ```cs
   [JwtTokenAuthFilter]
   public class TestAPIClass : ControllerBase
   ```

   