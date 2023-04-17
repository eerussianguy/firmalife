package com.eerussianguy.firmalife.common.blockentities;

import java.util.ArrayList;
import java.util.List;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.VatBlock;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.FLRecipeTypes;
import com.eerussianguy.firmalife.common.recipes.VatRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.InventoryFluidTank;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.util.Helpers;

public class VatBlockEntity extends BoilingBlockEntity<VatBlockEntity.VatInventory>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, VatBlockEntity vat)
    {
        vat.checkForLastTickSync();
        vat.checkForCalendarUpdate();

        if (vat.needsRecipeUpdate)
        {
            vat.updateCachedRecipe();
        }

        final List<ItemStack> excess = vat.inventory.excess;
        if (!excess.isEmpty() && vat.inventory.getStackInSlot(0).isEmpty())
        {
            vat.inventory.setStackInSlot(0, excess.remove(0));
        }
        vat.handleJarring();
        vat.tickTemperature();
        vat.handleCooking();
    }

    public static final int CAPACITY = 10_000;

    @Nullable private VatRecipe cachedRecipe = null;

    public VatBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.VAT.get(), pos, state, VatInventory::new, FLHelpers.blockEntityName("vat"));

        sidedInventory.on(new PartialItemHandler(inventory).insert(), Direction.Plane.HORIZONTAL);
    }

    public void handleJarring()
    {
        final FluidStack fluid = inventory.getFluidInTank(0);
        final ItemStack stack = inventory.getStackInSlot(0);
        if (fluid.hasTag() && fluid.getTag().contains("fruit", Tag.TAG_COMPOUND) && Helpers.isItem(stack, FLItems.EMPTY_JAR.get()))
        {
            final int jars = stack.getCount();
            final int maxFill = fluid.getAmount() / 500;
            if (jars <= maxFill && maxFill > 0)
            {
                final ItemStack newStack = ItemStack.of(fluid.getTag().getCompound("fruit"));
                newStack.setCount(jars);
                inventory.setStackInSlot(0, newStack);
                inventory.drain(500 * jars, IFluidHandler.FluidAction.EXECUTE);
                markForSync();
            }
        }
    }

    public void updateCachedRecipe()
    {
        assert level != null;
        if (inventory.excess.isEmpty())
        {
            cachedRecipe = level.getRecipeManager().getRecipeFor(FLRecipeTypes.VAT.get(), inventory, level).orElse(null);
        }
        else
        {
            cachedRecipe = null;
        }
        needsRecipeUpdate = false;
    }

    public void handleCooking()
    {
        assert level != null;
        if (isBoiling())
        {
            assert cachedRecipe != null;
            if (boilingTicks < cachedRecipe.getDuration())
            {
                boilingTicks++;
                if (boilingTicks == 1) markForSync();
            }
            else
            {
                final VatRecipe recipe = cachedRecipe;
                cachedRecipe = null;
                recipe.assembleOutputs(inventory);
                boilingTicks = 0;
                updateCachedRecipe();
                markForSync();
                if (getBlockState().hasProperty(VatBlock.SEALED))
                {
                    level.setBlockAndUpdate(getBlockPos(), getBlockState().setValue(VatBlock.SEALED, false));
                }
            }
        }
        else if (boilingTicks > 0)
        {
            boilingTicks = 0;
            markForSync();
        }
    }

    @Override
    public boolean isBoiling()
    {
        assert level != null;
        if (getBlockState().hasProperty(VatBlock.SEALED) && !getBlockState().getValue(VatBlock.SEALED))
        {
            return false;
        }
        if (level.isClientSide)
        {
            return boilingTicks > 0;
        }
        return cachedRecipe != null && temperature > cachedRecipe.getTemperature();
    }

    public static class VatInventory extends BoilingInventory
    {
        private final List<ItemStack> excess;

        public VatInventory(InventoryBlockEntity<?> entity)
        {
            super(entity, 1, new InventoryFluidTank(CAPACITY, fluid -> Helpers.isFluid(fluid.getFluid(), TFCTags.Fluids.USABLE_IN_POT), (VatBlockEntity) entity));
            this.excess = new ArrayList<>();
        }

        public void insertItemWithOverflow(ItemStack stack)
        {
            final ItemStack remainder = inventory.insertItem(0, stack, false);
            if (!remainder.isEmpty())
            {
                excess.add(remainder);
            }
        }

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag nbt = super.serializeNBT();
            FLHelpers.writeItemStackList(excess, nbt, "excess");
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            super.deserializeNBT(nbt);
            FLHelpers.readItemStackList(excess, nbt, "excess");
        }
    }
}
