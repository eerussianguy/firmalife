package com.eerussianguy.firmalife.common.capabilities.bee;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.entities.EntityHelpers;

public class BeeHandler implements IBee, ICapabilitySerializable<CompoundTag>
{
    private final LazyOptional<IBee> capability;
    private final ItemStack stack;
    private int[] abilities;
    private boolean hasQueen;
    private int geneticDisease = -1;
    private int parasiticInfection = -1;

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
        abilities[ability.ordinal()] = Mth.clamp(value, 1, 10);
        save();
    }

    @Override
    public void setGeneticDisease(int geneticDisease)
    {
        this.geneticDisease = geneticDisease;
    }

    @Override
    public int getGeneticDisease()
    {
        return geneticDisease;
    }

    @Override
    public void setParasiticInfection(int parasiticInfection)
    {
        this.parasiticInfection = parasiticInfection;
    }

    @Override
    public int getParasiticInfection()
    {
        return parasiticInfection;
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
        save();
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
            if (tag.contains("queen"))
            {
                hasQueen = tag.getBoolean("queen");
                abilities = tag.getIntArray("abilities");
                geneticDisease = EntityHelpers.getIntOrDefault(tag, "geneticDiseases", -1);
                parasiticInfection = EntityHelpers.getIntOrDefault(tag, "parasiticInfections", -1);
            }
        }
    }

    private void save()
    {
        final CompoundTag tag = stack.getOrCreateTag();
        tag.putIntArray("abilities", abilities);
        tag.putBoolean("queen", hasQueen);
        tag.putInt("geneticDiseases", geneticDisease);
        tag.putInt("parasiticInfections", parasiticInfection);
    }

    @Override
    public CompoundTag serializeNBT()
    {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) { }
}
