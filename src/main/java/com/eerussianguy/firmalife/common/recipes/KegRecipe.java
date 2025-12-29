package com.eerussianguy.firmalife.common.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import com.eerussianguy.firmalife.common.blockentities.KegBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.InstantBarrelRecipe;
import net.dries007.tfc.common.recipes.InstantFluidBarrelRecipe;
import net.dries007.tfc.common.recipes.SealedBarrelRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.input.BarrelInventory;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;

public class KegRecipe
{
    @Nullable
    public static RecipeHolder<InstantBarrelRecipe> getInstant(BarrelInventory inventory, Level level)
    {
        return level.getRecipeManager().getAllRecipesFor(TFCRecipeTypes.BARREL_INSTANT.get()).stream()
            .filter(r -> matches(r.value(), inventory)).findFirst().orElse(null);
    }

    @Nullable
    public static RecipeHolder<InstantFluidBarrelRecipe> getInstantFluid(BarrelInventory inventory, Level level)
    {
        return level.getRecipeManager().getAllRecipesFor(TFCRecipeTypes.BARREL_INSTANT_FLUID.get()).stream()
            .filter(r -> matches(r.value(), inventory)).findFirst().orElse(null);
    }

    @Nullable
    public static RecipeHolder<SealedBarrelRecipe> getSealed(BarrelInventory inventory, Level level)
    {
        return level.getRecipeManager().getAllRecipesFor(TFCRecipeTypes.BARREL_SEALED.get()).stream()
            .filter(r -> matches(r.value(), inventory)).findFirst().orElse(null);
    }

    public static boolean matches(InstantFluidBarrelRecipe recipe, BarrelInventory inventory)
    {
        final FluidStack extractableFluid = FluidHelpers.getContainedFluid(inventory.getStackInSlot(KegBlockEntity.SLOT_FLUID_CONTAINER_IN));
        return recipe.getInputFluid().test(inventory.getFluidInTank(0)) && recipe.getAddedFluid().test(extractableFluid);
    }

    public static boolean matches(InstantBarrelRecipe recipe, BarrelInventory inventory)
    {
        if (!matchesBase(recipe, inventory))
            return false;
        final int input = countItem(recipe.getInputItem().ingredient(), inventory);

        return recipe.getInputItem().ingredient().isEmpty()
            || recipe.getOutputFluid().isEmpty()
            || inventory.getFluidInTank(0).getAmount() / recipe.getInputFluid().amount() < input / recipe.getInputItem().count();
    }

    public static boolean matches(SealedBarrelRecipe recipe, BarrelInventory inventory)
    {
        if (!matchesBase(recipe, inventory))
            return false;
        if (recipe.getDuration() > 0 || recipe.getInputItem().ingredient().isEmpty())
            return true;
        final int count = countItem(recipe.getInputItem().ingredient(), inventory);
        return inventory.getFluidInTank(0).getAmount() / recipe.getInputFluid().amount() >= count / recipe.getInputItem().count();
    }

    private static int countItem(Predicate<ItemStack> ingredient, BarrelInventory inventory)
    {
        int count = 0;
        for (int i = KegBlockEntity.SLOT_INPUT_START; i <= KegBlockEntity.SLOT_INPUT_END; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (ingredient.test(stack))
                count += stack.getCount();
        }
        return count;
    }

    private static boolean matchesBase(BarrelRecipe recipe, BarrelInventory inventory)
    {
        if (!recipe.getInputItem().ingredient().isEmpty())
        {
            boolean found = false;
            for (int i = KegBlockEntity.SLOT_INPUT_START; i <= KegBlockEntity.SLOT_INPUT_END; i++)
            {
                final ItemStack stack = inventory.getStackInSlot(i);
                if (recipe.getInputItem().ingredient().test(stack))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
                return false;
        }
        return recipe.getInputFluid().test(inventory.getFluidInTank(0));
    }

    public static void assembleInstantFluid(InstantFluidBarrelRecipe recipe, BarrelInventory inventory)
    {
        // Require the inventory to be mutable, as we use insert/extract methods, but will expect it to be modifiable despite being sealed.
        inventory.whileMutable(() -> {

            // Extract input fluid - this will be converted, so we need to unconditionally drain all of it, and void excess.
            final FluidStack primaryFluid = inventory.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);

            final ItemStack originalStack = Helpers.removeStack(inventory, BarrelBlockEntity.SLOT_FLUID_CONTAINER_IN);
            final IFluidHandlerItem fluidHandler = originalStack.copyWithCount(1).getCapability(Capabilities.FluidHandler.ITEM);

            if (fluidHandler == null)
                return;

            final FluidStack addedFluid = fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);

            // Calculate the multiplier in use for this recipe.
            // Both fluid ingredients are required to be > 0
            final int multiplier = Math.min(
                primaryFluid.getAmount() / recipe.getInputFluid().amount(),
                addedFluid.getAmount() / recipe.getAddedFluid().amount()
            );

            // Output fluid
            // Figure out exactly how much of the input fluid to consume, and attempt to consume that amount.
            // If we can't consume exactly that amount, we are aggressive and void excess.
            final int targetAddedFluid = multiplier * recipe.getAddedFluid().amount();
            final FluidStack actualAddedFluid = fluidHandler.drain(targetAddedFluid, IFluidHandler.FluidAction.SIMULATE);
            if (actualAddedFluid.isEmpty() || actualAddedFluid.getAmount() < targetAddedFluid)
            {
                // Drain everything, which was checked earlier
                fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
            }
            else
            {
                // We drained exactly how much we needed to
                fluidHandler.drain(targetAddedFluid, IFluidHandler.FluidAction.EXECUTE);
            }

            // Set the output fluid
            final FluidStack outputFluid = recipe.getOutputFluid().copy();

            outputFluid.setAmount(Math.min(KegBlockEntity.CAPACITY, outputFluid.getAmount() * multiplier));
            inventory.fill(outputFluid, IFluidHandler.FluidAction.EXECUTE);

            // Set the input item
            // We removed it entirely later, so we just need to put it in the slot where the excess is
            FluidHelpers.updateContainerItem(originalStack, fluidHandler, (newOriginalStack, newContainerStack) -> {
                inventory.setStackInSlot(BarrelBlockEntity.SLOT_FLUID_CONTAINER_OUT, newOriginalStack);
                if (!newContainerStack.isEmpty())
                {
                    inventory.insertItemWithOverflow(newContainerStack);
                }
            });
        });
    }

    public static void assemble(BarrelRecipe recipe, BarrelInventory inventory)
    {
        if (recipe instanceof InstantFluidBarrelRecipe instant)
        {
            assembleInstantFluid(instant, inventory);
            return;
        }
        // Require the inventory to be mutable, as we use insert/extract methods, but will expect it to be modifiable despite being sealed.
        inventory.whileMutable(() -> {
            // Remove all inputs

            final List<ItemStack> stacks = new ArrayList<>();
            int accumulatedCount = 0;
            for (int i = KegBlockEntity.SLOT_INPUT_START; i <= KegBlockEntity.SLOT_INPUT_END; i++)
            {
                if (recipe.getInputItem().ingredient().test(inventory.getStackInSlot(i)))
                {
                    final ItemStack stack = Helpers.removeStack(inventory, i);
                    accumulatedCount += stack.getCount();
                    stacks.add(stack);
                }

            }
            final FluidStack fluid = inventory.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);

            // Calculate the multiplier in use for this recipe
            int multiplier;
            if (recipe.getInputItem().ingredient().isEmpty())
            {
                multiplier = fluid.getAmount() / recipe.getInputFluid().amount();
            }
            else
            {
                multiplier = Math.min(fluid.getAmount() / recipe.getInputFluid().amount(), accumulatedCount / recipe.getInputItem().count());
            }

            // Trim multiplier to a maximum fluid capacity of output
            if (!recipe.getOutputFluid().isEmpty())
            {
                int capacity = KegBlockEntity.CAPACITY;
                if (FluidStack.isSameFluidSameComponents(recipe.getOutputFluid(), fluid))
                {
                    capacity -= fluid.getAmount();
                }
                int maxMultiplier = capacity / recipe.getOutputFluid().getAmount();
                multiplier = Math.min(multiplier, maxMultiplier);
            }

            // Output items
            // All output items, and then remaining input items, get inserted into the output overflow
            final ItemStack outputItem = recipe.getOutputItem().getSingleStack(stacks.getFirst());
            if (!outputItem.isEmpty())
            {
                Helpers.consumeInStackSizeIncrements(outputItem, multiplier * outputItem.getCount(), inventory::insertItemWithOverflow);
            }
            int remainingItemCount = accumulatedCount - multiplier * recipe.getInputItem().count();
            while (remainingItemCount > 0)
            {
                final ItemStack remain = stacks.getFirst().copy();
                remain.setCount(Math.min(remain.getMaxStackSize(), remainingItemCount));
                remainingItemCount -= remain.getCount();
                inventory.insertItemWithOverflow(remain);
            }

            // Output fluid
            // If there's no output fluid, keep as much of the input as possible
            // If there is an output fluid, excess input is voided
            final FluidStack outputFluid = recipe.getOutputFluid().copy();
            if (outputFluid.isEmpty())
            {
                // Try and keep as much of the original input as possible
                final int retainAmount = fluid.getAmount() - (multiplier * recipe.getInputFluid().amount());
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
                outputFluid.setAmount(Math.min(KegBlockEntity.CAPACITY, amount));
                inventory.fill(outputFluid, IFluidHandler.FluidAction.EXECUTE);
            }
        });
    }

    public static void onSealed(SealedBarrelRecipe recipe, BarrelInventory inventory)
    {
        onProvider(recipe, inventory, recipe.onSeal());
    }

    public static void onUnsealed(SealedBarrelRecipe recipe, BarrelInventory inventory)
    {
        onProvider(recipe, inventory, recipe.onUnseal());
    }

    private static void onProvider(SealedBarrelRecipe recipe, BarrelInventory inventory, @Nullable ItemStackProvider onUnseal)
    {
        if (onUnseal == null)
            return;
        inventory.whileMutable(() -> {
            for (int i = KegBlockEntity.SLOT_INPUT_START; i < KegBlockEntity.SLOT_INPUT_END; i++)
            {
                if (recipe.getInputItem().test(inventory.getStackInSlot(i)))
                {
                    final ItemStack stack = Helpers.removeStack(inventory, i);
                    inventory.insertItem(i, onUnseal.getStack(stack), false);
                }
            }
        });
    }

}
