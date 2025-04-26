using Microsoft.OpenApi.Models;
var builder = WebApplication.CreateBuilder(args);
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(sw => {
    sw.SwaggerDoc("HelloWorld", new OpenApiInfo() { Title = "HelloWorldAPI", Description = "第一个API", Version = "v0.1" });
});

var app = builder.Build();

if (app.Environment.IsDevelopment()) //如果是开发环境
{
    app.UseSwagger(); //注册Swagger
    app.UseSwaggerUI(suo => {
        suo.SwaggerEndpoint("/swagger/HelloWorld/swagger.json", "HelloWorld v0.1");
    });
}
app.MapGet("/", () => "Hello World!");

app.Run();
