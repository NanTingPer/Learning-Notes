ModSystem

| 方法名                | 作用                                          |      |
| --------------------- | --------------------------------------------- | ---- |
| SetupContent          | 模组内容初始化，只运行一次                    |      |
| OnModLoad             | 在ModLoad之前被调用，自动加载之后调用         |      |
| OnModUnload           | Mod.UnLoad之前被调用                          |      |
| PostSetupContent      | 内容已经被加载后，在加载额外内容              |      |
| OnLocalizationsLoaded | 语言发生更改后 / 重新加载资源包 运行一次      |      |
| AddRecipes            | 往游戏内添加配方 使用Recipe                   |      |
| PostAddRecipes        | 用来修改配方                                  |      |
| PostSetupRecipes      | 处理已经设置好的配方，不应该在这里修改配方    |      |
| AddRecipeGroups       | 用来添加配方组，使用RecipeGroup.RegisterGroup |      |
|                       |                                               |      |
|                       |                                               |      |
|                       |                                               |      |
|                       |                                               |      |
|                       |                                               |      |
|                       |                                               |      |
|                       |                                               |      |
|                       |                                               |      |
|                       |                                               |      |
|                       |                                               |      |

