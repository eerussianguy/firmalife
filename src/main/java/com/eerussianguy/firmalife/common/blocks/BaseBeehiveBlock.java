package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;

public class BaseBeehiveBlock extends FourWayDeviceBlock implements HoeOverlayBlock
{
    private final int bees;

    public BaseBeehiveBlock(ExtendedProperties properties, int bees)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        this.bees = bees;
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos blockPos, BlockState blockState, Consumer<Component> consumer, boolean b)
    {

    }
}
