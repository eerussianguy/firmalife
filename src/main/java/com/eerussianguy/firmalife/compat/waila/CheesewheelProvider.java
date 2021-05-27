package com.eerussianguy.firmalife.compat.waila;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.blocks.BlockCheesewheel;
import com.eerussianguy.firmalife.init.AgingFL;
import com.eerussianguy.firmalife.init.StatePropertiesFL;
import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;

public class CheesewheelProvider implements IWailaBlock
{
    @Nonnull
    @Override
    public List<String> getTooltip(World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound NBT)
    {
        List<String> currentTooltip = new ArrayList<>();
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockCheesewheel)
        {
            AgingFL age = state.getValue(StatePropertiesFL.AGE);
            currentTooltip.add(age.getFormat() + new TextComponentTranslation(age.getTranslationKey()).getFormattedText());
        }

        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<Class<?>> getLookupClass()
    {
        return Collections.singletonList(BlockCheesewheel.class);
    }
}
