package com.eerussianguy.firmalife.compat.waila;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.blocks.BlockLargePlanter;
import com.eerussianguy.firmalife.blocks.BlockQuadPlanter;
import com.eerussianguy.firmalife.recipe.PlanterRecipe;
import com.eerussianguy.firmalife.te.TEPlanter;
import net.dries007.tfc.compat.waila.interfaces.IWailaBlock;

public class PlanterProvider implements IWailaBlock
{
    @Nonnull
    @Override
    public List<String> getTooltip(World world, @Nonnull BlockPos pos, @Nonnull NBTTagCompound nbt)
    {
        List<String> currentTooltip = new ArrayList<>();
        IBlockState state = world.getBlockState(pos);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TEPlanter)
        {
            Block block = state.getBlock();
            if (block instanceof BlockQuadPlanter)
            {
                PlanterRecipe.PlantInfo[] info = ((BlockQuadPlanter) block).getCrops(world, pos);
                for (int i = 0; i < 4; i++)
                {
                    PlanterRecipe recipe = info[i].getRecipe();
                    if (recipe != null)
                    {
                        int maxStage = PlanterRecipe.getMaxStage(recipe);
                        int curStage = info[i].getStage();
                        if (maxStage == curStage)
                        {
                            currentTooltip.add(new TextComponentTranslation(recipe.getOutputItem().getTranslationKey() + ".name").getFormattedText() + ": Mature");
                        }
                        else
                        {
                            float curStagePercent = (float) curStage * 100 / maxStage;
                            String growth = String.format("%d%%", Math.round(curStagePercent));
                            currentTooltip.add(new TextComponentTranslation(recipe.getOutputItem().getTranslationKey() + ".name").getFormattedText() + ": " + growth);
                        }
                    }
                    else
                    {
                        currentTooltip.add("Empty");
                    }
                }
            }
            else if (block instanceof BlockLargePlanter)
            {
                PlanterRecipe.PlantInfo info = ((BlockLargePlanter) block).getCrop(world, pos);
                PlanterRecipe recipe = info != null ? info.getRecipe() : null;
                if (recipe != null)
                {
                    int maxStage = PlanterRecipe.getMaxStage(recipe);
                    int curStage = info.getStage();
                    if (maxStage == curStage)
                    {
                        currentTooltip.add(new TextComponentTranslation(recipe.getOutputItem().getTranslationKey() + ".name").getFormattedText() + ": Mature");
                    }
                    else
                    {
                        float curStagePercent = (float) curStage * 100 / maxStage;
                        String growth = String.format("%d%%", Math.round(curStagePercent));
                        currentTooltip.add(new TextComponentTranslation(recipe.getOutputItem().getTranslationKey() + ".name").getFormattedText() + ": " + growth);
                    }
                }
                else
                {
                    currentTooltip.add("Empty");
                }
            }
            currentTooltip.add(((TEPlanter) te).isClimateValid ? "Climate Valid" : "Climate Invalid");
        }
        return currentTooltip;
    }

    @Nonnull
    @Override
    public List<Class<?>> getLookupClass()
    {
        return Collections.singletonList(TEPlanter.class);
    }
}
