using TerrariaServerAPI;
using TerrariaServerSystemTestRun;

var builder = WebApplication.CreateBuilder(args);

Environment.SetEnvironmentVariable("TSPATH", @"C:\Program Files (x86)\Steam\steamapps\common\Terraria\TerrariaServer.exe");

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddSingleton<ServerManager>();
builder.Services.AddHostedService<ExitServer>();
var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment()) {
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();
app.Run();
