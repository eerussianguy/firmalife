package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.container.StovetopPotContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.IPotInventory;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.capabilities.InventoryFluidTank;
import net.dries007.tfc.common.capabilities.PartialFluidHandler;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.outputs.PotOutput;
import net.dries007.tfc.util.Helpers;

public class StovetopPotBlockEntity extends BoilingBlockEntity<StovetopPotBlockEntity.StovetopPotInventory>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, StovetopPotBlockEntity pot)
    {
        pot.checkForLastTickSync();
        pot.checkForCalendarUpdate();

        if (pot.needsRecipeUpdate)
        {
            pot.updateCachedRecipe();
        }

        pot.tickTemperature();
        pot.handleCooking();
    }

    public static final int SLOTS = 5;
    private static final int DURATION = 1000;

    @Nullable private PotOutput output = null;
    @Nullable private PotRecipe cachedRecipe = null;
    private int preBoilingTicks = 0;
    private final SidedHandler<IFluidHandler> sidedFluidInventory;

    public StovetopPotBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.STOVETOP_POT.get(), pos, state, StovetopPotInventory::new, FLHelpers.blockEntityName("stovetop_pot"));
        sidedInventory.on(new PartialItemHandler(inventory).insert(), Direction.Plane.HORIZONTAL);
        sidedFluidInventory = new SidedHandler<IFluidHandler>(inventory)
            .on(PartialFluidHandler::insertOnly, Direction.UP)
            .on(PartialFluidHandler::extractOnly, Direction.Plane.HORIZONTAL);
    }

    @Nullable
    public IFluidHandler getSidedFluidInventory(@Nullable Direction dir)
    {
        return sidedFluidInventory.get(dir);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return StovetopPotContainer.create(this, inventory, containerId);
    }

    public void updateCachedRecipe()
    {
        assert level != null;
        cachedRecipe = level.getRecipeManager()
            .getRecipeFor(TFCRecipeTypes.POT.get(), inventory, level)
            .map(RecipeHolder::value)
            .orElse(null);
    }

    @Override
    public void advanceForCalendar(long ticks)
    {
        if (isBoiling())
        {
            assert cachedRecipe != null;
            if (ticks > cachedRecipe.getDuration() - boilingTicks)
            {
                boilingTicks = cachedRecipe.getDuration();
                handleCooking();
            }
            else
            {
                boilingTicks += (int) ticks;
            }
        }
    }

    @Override
    public boolean isBoiling()
    {
        // if we have a recipe, there is no output, and we're hot enough, we boil
        return cachedRecipe != null && output == null && cachedRecipe.isHotEnough(temperature);
    }

    public boolean hasRecipeStarted()
    {
        return isBoiling() && preBoilingTicks >= PotBlockEntity.PRE_BOIL_TIME;
    }

    public boolean shouldRenderAsBoiling()
    {
        return boilingTicks > 0;
    }

    public int getBoilingTicks()
    {
        return boilingTicks;
    }

    public ItemInteractionResult interactWithOutput(Player player, ItemStack stack)
    {
        if (output != null)
        {
            final ItemInteractionResult result = output.onInteract(getInventory(), player, stack);
            if (output.isEmpty())
            {
                output = null;
            }
            markForSync();
            return result;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    public PotOutput getOutput()
    {
        return output;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    public void handleCooking()
    {
        if (isBoiling())
        {
            if (preBoilingTicks < PotBlockEntity.PRE_BOIL_TIME)
            {
                preBoilingTicks++;
                return;
            }
            assert cachedRecipe != null;
            if (boilingTicks < cachedRecipe.getDuration())
            {
                boilingTicks++;
                if (boilingTicks == 1)
                {
                    updateCachedRecipe();
                    markForSync();
                }
            }
            else
            {
                // Create output
                // Set the crafting input, so providers can access all pot recipe inputs
                RecipeHelpers.setCraftingInput(inventory, inventory.inputStart(), inventory.inputEnd() + 1);

                // Save the recipe here, as setting inventory will call setAndUpdateSlots, which will clear the cached recipe before output is created
                final PotRecipe recipe = cachedRecipe;
                final PotOutput output = recipe.getOutput(inventory);

                RecipeHelpers.clearCraftingInput();

                // Clear inputs
                for (int slot = inventory.inputStart(); slot <= inventory.inputEnd(); slot++)
                {
                    // Consume items, but set container items if they exist
                    inventory.setStackInSlot(slot, inventory.getStackInSlot(slot).getCraftingRemainingItem());
                }

                output.onFinish(inventory); // Let the output handle filling into the empty pot
                if (!output.isEmpty()) // Then, if we still have contents, save the output
                {
                    this.output = output;
                }

                // Reset recipe progress
                cachedRecipe = null;
                boilingTicks = 0;
                preBoilingTicks = 0;
                updateCachedRecipe();
                markForSync();
            }
        }
        else if (boilingTicks > 0) // catch accidentally not syncing when it dips below temperature
        {
            boilingTicks = 0;
            preBoilingTicks = 0;
            markForSync();
        }
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        if (nbt.contains("output"))
        {
            output = PotOutput.read(provider, nbt.getCompound("output"));
        }
        boilingTicks = nbt.getInt("boilingTicks");
        preBoilingTicks = nbt.getInt("preBoilingTicks");
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        if (output != null)
        {
            nbt.put("output", PotOutput.write(provider, output));
        }
        nbt.putInt("boilingTicks", boilingTicks);
        nbt.putInt("preBoilingTicks", preBoilingTicks);
        super.saveAdditional(nbt, provider);
    }

    @Override
    public void ranOutDueToCalendar()
    {
        coolInstantly();
    }

    public void coolInstantly()
    {
        boilingTicks = 0;
        preBoilingTicks = 0;
        markForSync();
    }

    public static class StovetopPotInventory extends BoilingInventory implements IPotInventory
    {
        private final StovetopPotBlockEntity pot;

        public StovetopPotInventory(InventoryBlockEntity<?> pot)
        {
            super(pot, SLOTS, new InventoryFluidTank(FluidHelpers.BUCKET_VOLUME, f -> ((StovetopPotBlockEntity) pot).output == null && Helpers.isFluid(f.getFluid(), TFCTags.Fluids.USABLE_IN_POT), (StovetopPotBlockEntity) pot));
            this.pot = (StovetopPotBlockEntity) pot;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return pot.hasRecipeStarted() && slot >= inputStart() ? ItemStack.EMPTY : inventory.extractItem(slot, amount, simulate);
        }

        @Override
        public void clearFluid()
        {
            tank.setFluid(FluidStack.EMPTY);
        }

        @Override
        public int inputStart()
        {
            return 0;
        }

        @Override
        public int inputEnd()
        {
            return 4;
        }

    }
}
