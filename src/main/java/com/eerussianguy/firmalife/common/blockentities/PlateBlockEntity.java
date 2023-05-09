package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.util.Helpers;

public class PlateBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    private float rot = 0f;

    public PlateBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.PLATE.get(), pos, state, defaultInventory(1), FLHelpers.blockEntityName("plate"));
    }


    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        rot = nbt.getFloat("rot");
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putFloat("rot", rot);
    }

    public void setRotation(Player player)
    {
        this.rot = player.getYRot();
        setChanged();
    }

    public float getRotation()
    {
        return rot;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.getCapability(stack, FoodCapability.CAPABILITY) != null;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        setChanged();
    }

    public ItemStack viewStack()
    {
        return inventory.getStackInSlot(0);
    }
}
