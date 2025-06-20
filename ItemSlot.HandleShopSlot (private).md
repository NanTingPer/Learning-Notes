ItemSlot.HandleShopSlot (private)

```cs
boughtItem.stack = 1; 1 => inv[slot].stack or remove boughtItem.stack
```

or

```cs
int num = Main.superFastStack + 1; => int num = 30
```





```cs
if (!(Main.stackSplit <= 1 && flag) || inv[slot].type <= 0 || (!(Main.mouseItem.IsTheSameAs(inv[slot]) && ItemLoader.CanStack(Main.mouseItem, inv[slot])) && Main.mouseItem.type != 0))
	return;

int num = Main.superFastStack + 1;
```

