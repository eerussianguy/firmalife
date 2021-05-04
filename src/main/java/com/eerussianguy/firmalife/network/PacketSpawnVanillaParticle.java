package com.eerussianguy.firmalife.network;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;

public class PacketSpawnVanillaParticle implements IMessage
{
    private int particleID;
    private double x, y, z;
    private double speedX, speedY, speedZ;

    @SuppressWarnings("unused")
    @Deprecated
    public PacketSpawnVanillaParticle()
    {
    }

    public PacketSpawnVanillaParticle(EnumParticleTypes particle, double x, double y, double z, double speedX, double speedY, double speedZ)
    {
        this.particleID = particle.ordinal();
        this.x = x;
        this.y = y;
        this.z = z;
        this.speedX = speedX;
        this.speedY = speedY;
        this.speedZ = speedZ;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        this.particleID = buffer.readInt();
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
        this.speedX = buffer.readDouble();
        this.speedY = buffer.readDouble();
        this.speedZ = buffer.readDouble();
    }

    @Override
    public void toBytes(ByteBuf byteBuf)
    {
        byteBuf.writeInt(particleID);
        byteBuf.writeDouble(x);
        byteBuf.writeDouble(y);
        byteBuf.writeDouble(z);
        byteBuf.writeDouble(speedX);
        byteBuf.writeDouble(speedY);
        byteBuf.writeDouble(speedZ);
    }

    public static class Handler implements IMessageHandler<PacketSpawnVanillaParticle, IMessage>
    {
        @Override
        public IMessage onMessage(PacketSpawnVanillaParticle message, MessageContext ctx)
        {
            if (ctx.side.isClient()) // always true but we'll be defensive here
            {
                Minecraft mc = Minecraft.getMinecraft();
                mc.addScheduledTask(() -> {
                    EnumParticleTypes particle = EnumParticleTypes.getParticleFromId(message.particleID);
                    if (particle != null)
                    {
                        mc.world.spawnParticle(particle,  message.x, message.y, message.z, message.speedX, message.speedY, message.speedZ);
                    }
                });
            }
            return null;
        }
    }
}
