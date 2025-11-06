package com.eerussianguy.firmalife.common.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.eerussianguy.firmalife.common.blockentities.MixingBowlBlockEntity;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.recipes.ISimpleRecipe;
import net.dries007.tfc.util.Helpers;

public class MixingBowlRecipe implements ISimpleRecipe<MixingBowlBlockEntity.MixingBowlInventory>
{
    public static final MapCodec<MixingBowlRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.listOf().fieldOf("item_ingredients").forGetter(c -> c.itemIngredients),
        //TODO cant serialize SizedFluidIngredients that use FluidIngredient.empty(), and cant use FluidStack.EMPTY for SizedFluidIngredient
        // Can this be done without just using null?
        SizedFluidIngredient.FLAT_CODEC.optionalFieldOf("fluid_ingredients").forGetter(c -> c.fluidIngredient),
        ItemStack.CODEC.optionalFieldOf("result_item", ItemStack.EMPTY).forGetter(c -> c.resultItem),
        FluidStack.CODEC.optionalFieldOf("result_fluid", FluidStack.EMPTY).forGetter(c -> c.resultFluid)
    ).apply(instance, MixingBowlRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MixingBowlRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list(5)), c -> c.itemIngredients,
        ByteBufCodecs.optional(SizedFluidIngredient.STREAM_CODEC), c -> c.fluidIngredient,
        ItemStack.OPTIONAL_STREAM_CODEC, c -> c.resultItem,
        FluidStack.OPTIONAL_STREAM_CODEC, c -> c.resultFluid,
        MixingBowlRecipe::new
    );

    private final List<Ingredient> itemIngredients;
    private final Optional<SizedFluidIngredient> fluidIngredient;
    private final ItemStack resultItem;
    private final FluidStack resultFluid;

    public MixingBowlRecipe(List<Ingredient> itemIngredients, Optional<SizedFluidIngredient> fluidIngredient, ItemStack resultItem, FluidStack resultFluid)
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
        if (fluidIngredient.map(fluid -> fluid.test(inventory.getFluidInTank(0))).orElse(false))
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

    public Optional<SizedFluidIngredient> getFluidIngredient()
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
