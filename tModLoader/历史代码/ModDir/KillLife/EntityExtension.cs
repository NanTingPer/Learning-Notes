using Microsoft.Xna.Framework;
using Terraria;

namespace KillLife;

public static class EntityExtension
{
    public static bool CollidingAABB(this Entity entity1, Entity entity2)
    {
        var projHitbox = new Rectangle((int)entity1.position.X, (int)entity1.position.Y, entity1.width, entity1.height);
        var targetHitbox = new Rectangle((int)entity2.position.X, (int)entity2.position.Y, entity2.width, entity2.height);
        Vector2 pos1 = new(projHitbox.X, projHitbox.Y);
        Vector2 boom1 = new(projHitbox.Width, projHitbox.Height);

        Vector2 pos2 = new(targetHitbox.X, targetHitbox.Y);
        Vector2 boom2 = new(targetHitbox.Width, targetHitbox.Height);
        return Collision.CheckAABBvAABBCollision(pos1, boom1, pos2, boom2);
    }
}