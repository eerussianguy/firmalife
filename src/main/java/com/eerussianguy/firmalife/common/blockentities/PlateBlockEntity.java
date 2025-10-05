package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.component.food.FoodCapability;

public class PlateBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    private float rot = 0f;

    public PlateBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.PLATE.get(), pos, state, defaultInventory(1), FirmaLife.MOD_ID);

        sidedInventory
            .on(new PartialItemHandler(inventory).insert(0), Direction.Plane.HORIZONTAL)
            .on(new PartialItemHandler(inventory).extract(0), Direction.DOWN);
    }


    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        super.loadAdditional(nbt, access);
        rot = nbt.getFloat("rot");
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        super.saveAdditional(nbt, access);
        nbt.putFloat("rot", rot);
    }

    public void setRotation(Player player)
    {
        this.rot = player.getYRot();
        markForSync();
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
        return FoodCapability.get(stack) != null;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        markForSync();
    }

    public ItemStack viewStack()
    {
        return inventory.getStackInSlot(0);
    }
}
