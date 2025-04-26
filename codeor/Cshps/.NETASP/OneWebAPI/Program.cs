using Microsoft.OpenApi.Models;
var builder = WebApplication.CreateBuilder(args);
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(sw => {
    sw.SwaggerDoc("HelloWorld", new OpenApiInfo() { Title = "HelloWorldAPI", Description = "��һ��API", Version = "v0.1" });
});

var app = builder.Build();

if (app.Environment.IsDevelopment()) //����ǿ�������
{
    app.UseSwagger(); //ע��Swagger
    app.UseSwaggerUI(suo => {
        suo.SwaggerEndpoint("/swagger/HelloWorld/swagger.json", "HelloWorld v0.1");
    });
}
app.MapGet("/", () => "Hello World!");

app.Run();
