---
title: "tModLoader IL钩子"
date: 2025-02-17T17:50:00+08:00
draft: false
tags: ["tModLoader"]
---


# IL钩子

1. 目标方法

   `TGlobal`的类型的是`GlobalNPC`

   ```cs
   namespace Terraria.ModLoader;
   public abstract class GlobalType<TGlobal> : ModType where TGlobal : GlobalType<TGlobal>
   
   //目标
   public static bool TryGetGlobal<TResult>(int entityType, ReadOnlySpan<TGlobal> entityGlobals, TResult baseInstance, out TResult result) where TResult : TGlobal
       
   public abstract class GlobalType<TEntity, TGlobal> : GlobalType<TGlobal> where TGlobal : GlobalType<TEntity, TGlobal> where TEntity : IEntityWithGlobals<TGlobal>    
   
   //类型
   namespace Terraria.ModLoader;
   public abstract class GlobalNPC : GlobalType<NPC, GlobalNPC>
   
   ```

## 步骤1 

1. 获取目标方法

```cs
MethodInfo noOnMethod = 
    onGlobalType
    	.GetMethods(BindingFlags.Public | BindingFlags.Static)
    	.FirstOrDefault(method => method.GetParameters().Length == 4 && method.Name == "TryGetGlobal");
```



## 步骤2

2. 由于目标泛型未闭合，我们需要先闭合泛型，否则方法没有实例 无法挂钩

```cs
//获取未闭合的 GlobalType 稍后我们闭合未GlobalType<GlobalNPC>
Type noOnGlobalType = typeof(GlobalType<>);

//闭合GlobalType<>
Type onGlobalType = noOnGlobalType.MakeGenericType(typeof(GlobalNPC));

//获取闭合后的方法
MethodInfo onMethodinfo = noOnMethod.MakeGenericMethod(typeof(GlobalNPC));
```



## 步骤3

3. 挂钩，只能使用IL钩子`ILHook`，如果使用`new Hook(onMethodinfo)` 会报源方法不支持
3. 如果你观察原方法的IL代码就能发现，在最后`if`判断的条件中前两条`IL`语句是`Ldloc,LdcI4`

```cs
ILHook ilHook = new ILHook(onMethodinfo, ilBody => 
{
  	//进行IL编辑  
    ILCursor ilCursor = new ILCursor(il); //获取IL指针
	if(ilCursor.TryGotoNext(	//尝试跳转到指定目标位置之后
    	MoveType.After,
    	x => x.MatchLdloc0(), 
    	x => x.MatchLdcI4(out _)))
	{
        //跳转完后还得加1，因为当前指针位置是那个Label也就是跳转
    	ilCursor.Index++;
        //观察IL发现，if语句有两个Lable，那么两个Lable跳转的地方应该是相同的，我们直接copy
    	ILLabel ilLabel = il.Labels[0];
        //将局部变量压入计算栈(索引0)
    	ilCursor.Emit(OpCodes.Ldloc_0); 
		//将方法传入的参数压入计算栈(索引1) 由于他是结构体，需要使用 Ldarga
        ilCursor.Emit(OpCodes.Ldarga, 1); 
    	ilCursor.Emit(OpCodes.Call, typeof(ReadOnlySpan<GlobalNPC>).GetProperty("Length").GetMethod);
    	ilCursor.Emit(OpCodes.Bge, ilLabel); //如果第一个值大于或等于第二个值，则将控制转移到目标指令。
	}
});
```

