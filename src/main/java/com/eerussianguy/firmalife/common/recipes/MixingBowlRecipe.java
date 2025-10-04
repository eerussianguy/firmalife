package com.eerussianguy.firmalife.common.recipes;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;


import com.eerussianguy.firmalife.common.blockentities.MixingBowlBlockEntity;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.recipes.ISimpleRecipe;
import net.dries007.tfc.util.Helpers;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class MixingBowlRecipe implements ISimpleRecipe<MixingBowlBlockEntity.MixingBowlInventory>
{
    public static final MapCodec<MixingBowlRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.listOf().fieldOf("item_ingredients").forGetter(c -> c.itemIngredients),
        SizedFluidIngredient.FLAT_CODEC.fieldOf("fluid_ingredients").forGetter(c -> c.fluidIngredient),
        ItemStack.CODEC.fieldOf("result_item").forGetter(c -> c.resultItem),
        FluidStack.CODEC.fieldOf("result_fluid").forGetter(c -> c.resultFluid)
    ).apply(instance, MixingBowlRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MixingBowlRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list(5)), c -> c.itemIngredients,
        SizedFluidIngredient.STREAM_CODEC, c -> c.fluidIngredient,
        ItemStack.STREAM_CODEC, c -> c.resultItem,
        FluidStack.STREAM_CODEC, c -> c.resultFluid,
        MixingBowlRecipe::new
    );

    private final List<Ingredient> itemIngredients;
    private final SizedFluidIngredient fluidIngredient;
    private final ItemStack resultItem;
    private final FluidStack resultFluid;

    protected MixingBowlRecipe(List<Ingredient> itemIngredients, SizedFluidIngredient fluidIngredient, ItemStack resultItem, FluidStack resultFluid)
    {
        this.itemIngredients = itemIngredients;
        this.fluidIngredient = fluidIngredient;
        this.resultItem = resultItem;
        this.resultFluid = resultFluid;
        FoodCapability.setNonDecaying(resultItem);
    }

    @Override
    public boolean matches(MixingBowlBlockEntity.MixingBowlInventory inventory, Level level)
    {
        if (!fluidIngredient.test(inventory.getFluidInTank(0)))
        {
            return false;
        }
        final List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < MixingBowlBlockEntity.SLOTS; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                stacks.add(stack);
            }
        }
        // we allow no ingredients, in this case we are only testing the fluid. but we need an empty inventory. just an efficiency thing.
        return itemIngredients.isEmpty() ? stacks.isEmpty() : Helpers.perfectMatchExists(stacks, itemIngredients);
    }

    @Override
    public ItemStack assemble(MixingBowlBlockEntity.MixingBowlInventory inventory, HolderLookup.Provider provider)
    {
        return resultItem.copy();
    }

    public SizedFluidIngredient getFluidIngredient()
    {
        return fluidIngredient;
    }

    public FluidStack getDisplayFluid()
    {
        return resultFluid;
    }

    public FluidStack getResultFluid()
    {
        return resultFluid.copy();
    }

    public List<Ingredient> getItemIngredients()
    {
        return itemIngredients;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider access)
    {
        return resultItem.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.MIXING_BOWL.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return FLRecipeTypes.MIXING_BOWL.get();
    }

}
