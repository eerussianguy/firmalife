package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.util.Plantable;
import org.jetbrains.annotations.Nullable;

public class QuadPlanterBlockEntity extends LargePlanterBlockEntity
{
    public static final Component NAME = FLHelpers.blockEntityName("quad_planter");
    private static final int NUM_SLOTS = 4;

    private final Plantable[] cachedPlants;
    private float[] growth;

    public QuadPlanterBlockEntity(BlockPos pos, BlockState state)
    {
        this(FLBlockEntities.QUAD_PLANTER.get(), pos, state, NAME);
    }

    public QuadPlanterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Component name)
    {
        super(type, pos, state, defaultInventory(NUM_SLOTS), name);
        cachedPlants = new Plantable[] {null, null, null, null};
        growth = new float[] {0, 0, 0, 0};
    }

    @Override
    protected void loadUnique(CompoundTag nbt)
    {
        growth = new float[] {nbt.getFloat("grow0"), nbt.getFloat("grow1"), nbt.getFloat("grow2"), nbt.getFloat("grow3")};
    }

    @Override
    protected void saveUnique(CompoundTag nbt)
    {
        for (int i = 0; i < 4; i++)
        {
            nbt.putFloat("grow" + i, growth[i]);
        }
    }

    @Override
    public void updateCache()
    {
        for (int i = 0; i < 4; i++)
        {
            cachedPlants[i] = Plantable.get(inventory.getStackInSlot(i));
        }
        markForSync();
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    public int slots()
    {
        return NUM_SLOTS;
    }

    @Override
    public float getGrowth(int slot)
    {
        return growth[slot];
    }

    @Override
    public void setGrowth(int slot, float growth)
    {
        this.growth[slot] = growth;
        markForSync();
    }

    @Nullable
    public Plantable getPlantable(int slot)
    {
        return cachedPlants[slot];
    }
}
