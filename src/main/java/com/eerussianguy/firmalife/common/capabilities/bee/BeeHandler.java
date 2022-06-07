package com.eerussianguy.firmalife.common.capabilities.bee;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeeHandler implements IBee, ICapabilitySerializable<CompoundTag>
{
    private final LazyOptional<IBee> capability;
    private final ItemStack stack;
    private int[] abilities;
    private boolean hasQueen;

    private boolean initialized = false;

    public BeeHandler(ItemStack stack)
    {
        capability = LazyOptional.of(() -> this);
        hasQueen = false;
        this.stack = stack;
        this.abilities = new int[BeeAbility.SIZE];
    }

    @Override
    public int[] getAbilityMap()
    {
        return abilities;
    }

    @Override
    public void setAbilities(int[] abilities)
    {
        this.abilities = abilities;
        save();
    }

    @Override
    public void setAbility(BeeAbility ability, int value)
    {
        abilities[ability.ordinal()] = value;
        save();
    }

    @Override
    public boolean hasQueen()
    {
        return hasQueen;
    }

    @Override
    public void setHasQueen(boolean exists)
    {
        hasQueen = exists;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == BeeCapability.CAPABILITY)
        {
            load();
            return capability.cast();
        }
        return LazyOptional.empty();
    }

    private void load()
    {
        if (!initialized)
        {
            initialized = true;

            final CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("abilities")) abilities = tag.getIntArray("abilities");
        }
    }

    private void save()
    {
        final CompoundTag tag = stack.getOrCreateTag();
        tag.putIntArray("abilities", abilities);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) { }
}
