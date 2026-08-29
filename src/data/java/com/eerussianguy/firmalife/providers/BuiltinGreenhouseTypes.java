package com.eerussianguy.firmalife.providers;

import java.util.concurrent.CompletableFuture;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.util.GreenhouseType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;

public class BuiltinGreenhouseTypes extends DataManagerProvider<GreenhouseType>
{
    public BuiltinGreenhouseTypes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(GreenhouseType.MANAGER, output, lookup, FirmaLife.MOD_ID);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add("treated_wood", new GreenhouseType(BlockIngredient.of(FLTags.Blocks.ALL_TREATED_WOOD_GREENHOUSE), 5, Component.translatable("greenhouse.firmalife.treated_wood")));
        add("copper", new GreenhouseType(BlockIngredient.of(FLTags.Blocks.ALL_COPPER_GREENHOUSE), 10, Component.translatable("greenhouse.firmalife.copper")));
        add("iron", new GreenhouseType(BlockIngredient.of(FLTags.Blocks.ALL_IRON_GREENHOUSE), 15, Component.translatable("greenhouse.firmalife.iron")));
        add("stainless_steel", new GreenhouseType(BlockIngredient.of(FLTags.Blocks.STAINLESS_STEEL_GREENHOUSE), 20, Component.translatable("greenhouse.firmalife.stainless_steel")));
    }
}
