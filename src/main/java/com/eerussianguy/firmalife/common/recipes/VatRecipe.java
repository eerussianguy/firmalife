package com.eerussianguy.firmalife.common.recipes;

import com.eerussianguy.firmalife.common.blockentities.VatBlockEntity;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.ISimpleRecipe;
import net.dries007.tfc.common.recipes.RecipeSerializerImpl;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public class VatRecipe implements ISimpleRecipe<VatBlockEntity.VatInventory>
{
    private final ResourceLocation id;
    private final ItemStackIngredient inputItem;
    private final FluidStackIngredient inputFluid;
    private final ItemStackProvider outputItem;
    private final FluidStack outputFluid;
    private final int length;
    private final float temperature;

    public VatRecipe(ResourceLocation id, ItemStackIngredient ingredient, FluidStackIngredient fluidInput, ItemStackProvider output, FluidStack outputFluid, int length, float temperature)
    {
        this.id = id;
        this.inputItem = ingredient;
        this.inputFluid = fluidInput;
        this.outputItem = output;
        this.outputFluid = outputFluid;
        this.length = length;
        this.temperature = temperature;
    }

    public void assembleOutputs(VatBlockEntity.VatInventory inventory)
    {
        final ItemStack stack = Helpers.removeStack(inventory, 0);
        final FluidStack fluid = inventory.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);

        // Calculate the multiplier in use for this recipe
        int multiplier;
        if (inputItem.count() == 0)
        {
            multiplier = fluid.getAmount() / inputFluid.amount();
        }
        else if (inputFluid.amount() == 0)
        {
            multiplier = stack.getCount() / inputItem.count();
        }
        else
        {
            multiplier = Math.min(fluid.getAmount() / inputFluid.amount(), stack.getCount() / inputItem.count());
        }

        // Trim multiplier to a maximum fluid capacity of output
        if (!outputFluid.isEmpty())
        {
            int capacity = VatBlockEntity.CAPACITY;
            if (outputFluid.isFluidEqual(fluid))
            {
                capacity -= fluid.getAmount();
            }
            int maxMultiplier = capacity / outputFluid.getAmount();
            multiplier = Math.min(multiplier, maxMultiplier);
        }

        // Output items
        // All output items, and then remaining input items, get inserted into the output overflow
        final ItemStack outputItem = this.outputItem.getSingleStack(stack);
        if (!outputItem.isEmpty())
        {
            Helpers.consumeInStackSizeIncrements(outputItem, multiplier * outputItem.getCount(), inventory::insertItemWithOverflow);
        }
        final int remainingItemCount = stack.getCount() - multiplier * inputItem.count();
        if (remainingItemCount > 0)
        {
            final ItemStack remainingStack = stack.copy();
            remainingStack.setCount(remainingItemCount);
            inventory.insertItemWithOverflow(remainingStack);
        }

        // Output fluid
        // If there's no output fluid, keep as much of the input as possible
        // If there is an output fluid, excess input is voided
        final FluidStack outputFluid = this.outputFluid.copy();
        if (outputFluid.isEmpty())
        {
            // Try and keep as much of the original input as possible
            final int retainAmount = fluid.getAmount() - (multiplier * this.inputFluid.amount());
            if (retainAmount > 0)
            {
                final FluidStack retainedFluid = fluid.copy();
                retainedFluid.setAmount(retainAmount);
                inventory.fill(retainedFluid, IFluidHandler.FluidAction.EXECUTE);
            }
        }
        else
        {
            int amount = outputFluid.getAmount() * multiplier;
            if (outputFluid.isFluidEqual(fluid))
            {
                amount = amount + fluid.getAmount();
            }
            outputFluid.setAmount(Math.min(VatBlockEntity.CAPACITY, amount));
            inventory.fill(outputFluid, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public boolean matches(VatBlockEntity.VatInventory container, Level level)
    {
        return inputItem.test(container.getStackInSlot(0))
            && inputFluid.test(container.getFluidInTank(0))
            && (inputFluid.amount() == 0
            || outputFluid.getAmount() == 0
            || container.getFluidInTank(0).getAmount() / this.inputFluid.amount() <= container.getStackInSlot(0).getCount() / this.inputItem.count()
            );
    }

    @Override
    public ItemStack getResultItem()
    {
        return outputItem.getSingleStack(ItemStack.EMPTY);
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.VAT.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return FLRecipeTypes.VAT.get();
    }

    public ItemStackIngredient getInputItem()
    {
        return inputItem;
    }

    public FluidStackIngredient getInputFluid()
    {
        return inputFluid;
    }

    public FluidStack getOutputFluid()
    {
        return outputFluid;
    }

    public ItemStackProvider getOutputItem()
    {
        return outputItem;
    }

    public int getDuration()
    {
        return length;
    }

    public float getTemperature()
    {
        return temperature;
    }

    public static class Serializer extends RecipeSerializerImpl<VatRecipe>
    {
        @Override
        public VatRecipe fromJson(ResourceLocation id, JsonObject json)
        {
            return new VatRecipe(
                id,
                json.has("input_item") ? ItemStackIngredient.fromJson(json.getAsJsonObject("input_item")) : ItemStackIngredient.EMPTY,
                json.has("input_fluid") ? FluidStackIngredient.fromJson(json.getAsJsonObject("input_fluid")) : FluidStackIngredient.EMPTY,
                json.has("output_item") ? ItemStackProvider.fromJson(json.getAsJsonObject("output_item")) : ItemStackProvider.empty(),
                json.has("output_fluid") ? JsonHelpers.getFluidStack(json, "output_fluid") : FluidStack.EMPTY,
                JsonHelpers.getAsInt(json, "length", 600),
                JsonHelpers.getAsFloat(json, "temperature", 300f)
            );
        }

        @Nullable
        @Override
        public VatRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer)
        {
            final ItemStackIngredient ingredient = ItemStackIngredient.fromNetwork(buffer);
            final FluidStackIngredient fluidIngredient = FluidStackIngredient.fromNetwork(buffer);
            final ItemStackProvider output = ItemStackProvider.fromNetwork(buffer);
            final FluidStack outputFluid = buffer.readFluidStack();
            final int length = buffer.readVarInt();
            final float temp = buffer.readFloat();
            return new VatRecipe(id, ingredient, fluidIngredient, output, outputFluid, length, temp);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, VatRecipe recipe)
        {
            recipe.inputItem.toNetwork(buffer);
            recipe.inputFluid.toNetwork(buffer);
            recipe.outputItem.toNetwork(buffer);
            buffer.writeFluidStack(recipe.outputFluid);
            buffer.writeVarInt(recipe.length);
            buffer.writeFloat(recipe.temperature);
        }
    }
}
