package com.eerussianguy.firmalife.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientHelpers
{
    public static Vec3d getEntityMovementPartial(Entity entity, float partialTicks)
    {
        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
        final double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
        return new Vec3d(x, y, z);
    }
}
