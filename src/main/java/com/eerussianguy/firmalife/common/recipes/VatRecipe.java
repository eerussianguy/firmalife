package com.eerussianguy.firmalife.common.recipes;

import java.util.Optional;
import com.eerussianguy.firmalife.common.blockentities.VatBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.recipes.ISimpleRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.network.StreamCodecs;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Codecs;

public class VatRecipe implements ISimpleRecipe<VatBlockEntity.VatInventory>
{
    public static final MapCodec<VatRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        SizedIngredient.FLAT_CODEC.fieldOf("input_item").forGetter(c -> c.inputItem),
        SizedFluidIngredient.FLAT_CODEC.fieldOf("input_fluid").forGetter(c -> c.inputFluid),
        //TODO codec does not currently support ItemStackProvider.empty(), if that changes this does not need to be optional
        ItemStackProvider.CODEC.optionalFieldOf("output_item").forGetter(c -> c.outputItem),
        FluidStack.OPTIONAL_CODEC.optionalFieldOf("output_fluid").forGetter(c -> c.outputFluid),
        Codecs.POSITIVE_INT.fieldOf("length").forGetter(c -> c.length),
        Codec.FLOAT.fieldOf("temperature").forGetter(c -> c.temperature),
        ItemStack.OPTIONAL_CODEC.optionalFieldOf("jar_output").forGetter(c -> c.jarOutput),
        ResourceLocation.CODEC.optionalFieldOf("output_texture").forGetter(c -> c.outputTexture)
    ).apply(i, VatRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VatRecipe> STREAM_CODEC = StreamCodecs.composite(
        SizedIngredient.STREAM_CODEC, c -> c.inputItem,
        SizedFluidIngredient.STREAM_CODEC, c -> c.inputFluid,
        ByteBufCodecs.optional(ItemStackProvider.STREAM_CODEC), c -> c.outputItem,
        ByteBufCodecs.optional(FluidStack.OPTIONAL_STREAM_CODEC), c -> c.outputFluid,
        ByteBufCodecs.INT, c -> c.length,
        ByteBufCodecs.FLOAT, c -> c.temperature,
        ByteBufCodecs.optional(ItemStack.OPTIONAL_STREAM_CODEC), c -> c.jarOutput,
        ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), c -> c.outputTexture,
        VatRecipe::new
    );

    private final SizedIngredient inputItem;
    private final SizedFluidIngredient inputFluid;
    private final Optional<ItemStackProvider> outputItem;
    private final Optional<FluidStack> outputFluid;
    private final int length;
    private final float temperature;
    private final Optional<ItemStack> jarOutput;
    private final Optional<ResourceLocation> outputTexture;

    public VatRecipe(SizedIngredient ingredient, SizedFluidIngredient fluidInput, Optional<ItemStackProvider> output, Optional<FluidStack> outputFluid, int length, float temperature, Optional<ItemStack> jarOutput, Optional<ResourceLocation> outputTexture)
    {
        this.inputItem = ingredient;
        this.inputFluid = fluidInput;
        this.outputItem = output;
        this.outputFluid = outputFluid;
        this.length = length;
        this.temperature = temperature;
        this.jarOutput = jarOutput;
        this.outputTexture = outputTexture;
    }

    public void assembleOutputs(VatBlockEntity vat, VatBlockEntity.VatInventory inventory)
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
        if (outputFluid.isPresent() && !outputFluid.get().isEmpty())
        {
            int capacity = VatBlockEntity.CAPACITY;
            if (FluidStack.isSameFluidSameComponents(outputFluid.get(), fluid))
            {
                capacity -= fluid.getAmount();
            }
            int maxMultiplier = capacity / outputFluid.get().getAmount();
            multiplier = Math.min(multiplier, maxMultiplier);
        }

        // Output items
        // All output items, and then remaining input items, get inserted into the output overflow
        final ItemStack outputItem = this.outputItem.map(provider -> provider.getSingleStack(stack)).orElse(ItemStack.EMPTY);
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
        final FluidStack outputFluid = this.outputFluid.orElse(FluidStack.EMPTY);
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
            if (FluidStack.isSameFluidSameComponents(outputFluid, fluid))
            {
                amount = amount + fluid.getAmount();
            }
            outputFluid.setAmount(Math.min(VatBlockEntity.CAPACITY, amount));
            inventory.fill(outputFluid, IFluidHandler.FluidAction.EXECUTE);
        }
        final ItemStack jar = jarOutput.orElse(ItemStack.EMPTY);
        if (!jar.isEmpty())
        {
            jar.setCount(jar.getCount() * multiplier);
            vat.setOutput(jar, outputTexture.orElse(null));
        }
    }

    @Override
    public boolean matches(VatBlockEntity.VatInventory container, Level level)
    {
        return inputItem.test(container.getStackInSlot(0))
            && inputFluid.test(container.getFluidInTank(0))
            && (inputFluid.amount() == 0
            || inputItem.count() == 0
            || (inputItem.count() > 0 && inputFluid.amount() > 0 && container.getFluidInTank(0).getAmount() / this.inputFluid.amount() <= container.getStackInSlot(0).getCount() / this.inputItem.count())
        );
    }

    @Override
    public ItemStack assemble(VatBlockEntity.VatInventory vatInventory, HolderLookup.Provider provider)
    {
        return null;
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

    public SizedIngredient getInputItem()
    {
        return inputItem;
    }

    public SizedFluidIngredient getInputFluid()
    {
        return inputFluid;
    }

    public Optional<FluidStack> getOutputFluid()
    {
        return outputFluid;
    }

    public Optional<ItemStackProvider> getOutputItem()
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

    public Optional<ItemStack> getJarOutput()
    {
        return jarOutput;
    }

    public Optional<ResourceLocation> getOutputTexture()
    {
        return outputTexture;
    }

}
