package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.container.StovetopGrillContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;

public class StovetopGrillBlockEntity extends ApplianceBlockEntity<StovetopGrillBlockEntity.GrillInventory>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, StovetopGrillBlockEntity grill)
    {
        grill.checkForLastTickSync();
        grill.checkForCalendarUpdate();

        if (grill.needsRecipeUpdate)
        {
            grill.updateCachedRecipe();
        }

        grill.tickTemperature();
        grill.handleCooking();
    }

    public static final int SLOTS = 4;

    private final HeatingRecipe[] cachedRecipes;
    private boolean needsRecipeUpdate = true;

    public StovetopGrillBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.STOVETOP_GRILL.get(), pos, state, GrillInventory::new, FLHelpers.blockEntityName("stovetop_grill"));

        sidedInventory
            .on(new PartialItemHandler(inventory).insert(0, 1, 2, 3), Direction.UP)
            .on(new PartialItemHandler(inventory).extract(0, 1, 2, 3), Direction.Plane.HORIZONTAL);
        cachedRecipes = new HeatingRecipe[SLOTS];
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return StovetopGrillContainer.create(this, inventory, containerId);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.mightHaveCapability(stack, HeatCapability.CAPABILITY);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        needsRecipeUpdate = true;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsRecipeUpdate = true;
    }

    protected void handleCooking()
    {
        assert level != null;
        for (int slot = 0; slot < SLOTS; slot++)
        {
            final ItemStack inputStack = inventory.getStackInSlot(slot);
            final int finalSlot = slot;
            inputStack.getCapability(HeatCapability.CAPABILITY, null).ifPresent(cap -> {
                HeatCapability.addTemp(cap, temperature);
                final HeatingRecipe recipe = cachedRecipes[finalSlot];
                if (recipe != null && recipe.isValidTemperature(cap.getTemperature()))
                {
                    ItemStack output = recipe.assemble(new ItemStackInventory(inputStack), level.registryAccess());
                    FoodCapability.applyTrait(output, FoodTraits.WOOD_GRILLED);
                    inventory.setStackInSlot(finalSlot, output);
                    markForSync();
                }
            });
        }
    }

    protected void updateCachedRecipe()
    {
        assert level != null;
        for (int slot = 0; slot < SLOTS; slot++)
        {
            final ItemStack stack = inventory.getStackInSlot(slot);
            cachedRecipes[slot] = stack.isEmpty() ? null : HeatingRecipe.getRecipe(new ItemStackInventory(stack));
        }
    }

    public static class GrillInventory extends ApplianceInventory
    {
        public GrillInventory(InventoryBlockEntity<?> entity)
        {
            super(entity, SLOTS);
        }
    }
}
