package com.eerussianguy.firmalife.network;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.eerussianguy.firmalife.render.RenderHandler;
import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;

public class PacketDrawBoundingBox implements IMessage
{
    private BlockPos min;
    private BlockPos max;
    private float red;
    private float green;
    private float blue;
    private boolean isBlockShape;

    @SuppressWarnings("unused")
    @Deprecated
    public PacketDrawBoundingBox()
    {
    }

    public PacketDrawBoundingBox(BlockPos min, BlockPos max, float red, float green, float blue, boolean isBlockShape)
    {
        this.min = min;
        this.max = max;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.isBlockShape = isBlockShape;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        min = BlockPos.fromLong(buf.readLong());
        max = BlockPos.fromLong(buf.readLong());
        red = buf.readFloat();
        green = buf.readFloat();
        blue = buf.readFloat();
        isBlockShape = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(min.toLong());
        buf.writeLong(max.toLong());
        buf.writeFloat(red);
        buf.writeFloat(green);
        buf.writeFloat(blue);
        buf.writeBoolean(isBlockShape);
    }

    public static class Handler implements IMessageHandler<PacketDrawBoundingBox, IMessage>
    {
        @Override
        public IMessage onMessage(PacketDrawBoundingBox message, MessageContext ctx)
        {
            TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(
                () -> {
                    final List<Runnable> runList = Collections.nCopies(200, () -> {
                        AxisAlignedBB box;
                        if (message.isBlockShape)
                        {
                            World world = TerraFirmaCraft.getProxy().getWorld(ctx);
                            if (world != null)
                            {
                                final BlockPos pos = message.min;
                                box = world.getBlockState(pos).getBoundingBox(world, pos).offset(pos.getX(), pos.getY(), pos.getZ()).grow(0.002D);
                            }
                            else return;
                        }
                        else
                        {
                            box = new AxisAlignedBB(message.min, message.max).shrink(0.1D);
                        }
                        RenderGlobal.drawSelectionBoundingBox(box, message.red, message.green, message.blue, 1.0F);
                    });
                    RenderHandler.TO_RUN = new LinkedList<>(runList);
                });
            return null;
        }
    }
}
