package com.eerussianguy.firmalife.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.eerussianguy.firmalife.init.RegistriesFL;

public class NutRecipe extends IForgeRegistryEntry.Impl<NutRecipe>
{
    protected Block inputLog;
    protected Block inputLeaves;
    protected ItemStack outputItem;

    @Nullable
    public static NutRecipe get(Block block)
    {
        return RegistriesFL.NUT_TREES.getValuesCollection().stream().filter(x -> x.isValidInput(block)).findFirst().orElse(null);
    }

    public NutRecipe(Block inputLog, Block inputLeaves, ItemStack outputItem)
    {
        this.inputLog = inputLog;
        this.inputLeaves = inputLeaves;
        this.outputItem = outputItem;

        if (inputLog == null || inputLeaves == null || outputItem == null)
        {
            throw new IllegalArgumentException("Sorry, something was null in your nut tree registry.");
        }
    }

    @Nonnull
    public Block getLeaves()
    {
        return inputLeaves;
    }

    @Nonnull
    public ItemStack getNut()
    {
        return outputItem;
    }

    private boolean isValidInput(Block input)
    {
        return this.inputLog == input;
    }
}
