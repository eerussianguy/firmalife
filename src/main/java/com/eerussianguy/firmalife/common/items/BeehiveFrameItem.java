package com.eerussianguy.firmalife.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import com.eerussianguy.firmalife.common.capabilities.bee.BeeCapability;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeHandler;
import com.eerussianguy.firmalife.common.capabilities.bee.IBee;
import org.jetbrains.annotations.Nullable;

public class BeehiveFrameItem extends Item
{
    public BeehiveFrameItem(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new BeeHandler(stack);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return stack.getCapability(BeeCapability.CAPABILITY).map(IBee::hasQueen).orElse(false);
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        return new ItemStack(this);
    }
}
