namespace InfiniteL.Items;

/// <summary>
/// 第一个物品，直接使用默认上限
/// </summary>
public class _1InspirationIncrease : InfiniteInspirationConsumableBase
{
}

/// <summary>
/// 第二个物品，使用要求 + 10
/// </summary>
public class _2InspirationIncrease : InfiniteInspirationConsumableBase
{
    public override int InspirationBase => base.InspirationBase + 10;
}
public class _3InspirationIncrease : InfiniteInspirationConsumableBase
{
    public override int InspirationBase => base.InspirationBase + 20;
}
public class _4InspirationIncrease : InfiniteInspirationConsumableBase
{
    public override int InspirationBase => base.InspirationBase + 30;
}