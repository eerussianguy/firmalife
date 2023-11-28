package com.eerussianguy.firmalife.common.blocks.greenhouse;

import com.eerussianguy.firmalife.common.blockentities.PumpingStationBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.FourWayDeviceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

import net.dries007.tfc.common.blocks.ExtendedProperties;

public class PumpingStationBlock extends FourWayDeviceBlock
{
    public static boolean hasConnection(LevelAccessor level, BlockPos pos)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        cursor.set(pos);
        for (int i = 0; i < 4; i++)
        {
            if (level.getBlockEntity(cursor) instanceof PumpingStationBlockEntity pump)
            {
                return pump.isPumping();
            }
            else if (level.getBlockState(cursor).getBlock() != FLBlocks.IRRIGATION_TANK.get())
            {
                return false;
            }
            cursor.move(0, -1, 0);
        }
        return false;
    }

    public PumpingStationBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.NOOP);
    }


}
