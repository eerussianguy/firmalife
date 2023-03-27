package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.AshtrayBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class AshTrayBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public AshTrayBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.ASHTRAY.get(), pos, state, defaultInventory(1), FLHelpers.blockEntityName("ashtray"));

        sidedInventory.on(new PartialItemHandler(inventory).extract(0), d -> d != Direction.UP);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCItems.POWDERS.get(Powder.WOOD_ASH).get());
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 16;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        updateBlockState();
    }

    public void updateBlockState()
    {
        assert level != null;
        final BlockState current = level.getBlockState(getBlockPos());
        final int stage = Mth.clamp(Mth.ceil(inventory.getStackInSlot(0).getCount() * 10f / 16f), 0, 5);
        if (current.hasProperty(AshtrayBlock.STAGE) && current.getValue(AshtrayBlock.STAGE) != stage)
        {
            level.setBlockAndUpdate(getBlockPos(), current.setValue(AshtrayBlock.STAGE, stage));
        }
    }
}
