package com.eerussianguy.firmalife.common.blockentities;

import java.util.Set;
import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;

public class FoodShelfBlockEntity extends InventoryBlockEntity<ItemStackHandler> implements ClimateReceiver
{
    private static final Set<DeferredHolder<FoodTrait, FoodTrait>> POSSIBLE = Set.of(FLFoodTraits.SHELVED, FLFoodTraits.SHELVED_2, FLFoodTraits.SHELVED_3);

    private boolean climateValid = false;

    public FoodShelfBlockEntity(BlockPos pos, BlockState state)
    {
        this(FLBlockEntities.FOOD_SHELF.get(), pos, state);
    }

    public FoodShelfBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(1), FLHelpers.blockEntityName("food_shelf"));
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        markForSync();
    }

    @Override
    public void markForSync()
    {
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        super.markForSync();
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        climateValid = nbt.getBoolean("climateValid");
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putBoolean("climateValid", climateValid);
    }

    public boolean isClimateValid()
    {
        return climateValid;
    }

    @Override
    public void onLoadAdditional()
    {
        updatePreservation(climateValid);
    }

    @Override
    public void ejectInventory()
    {
        updatePreservation(false);
        super.ejectInventory();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        final ItemStack current = inventory.getStackInSlot(slot);
        if (!current.isEmpty())
        {
            ItemStack currentWithoutTrait = current.copy();
            FoodCapability.removeTrait(currentWithoutTrait, getFoodTrait());
            return FoodCapability.areStacksStackableExceptCreationDate(stack, currentWithoutTrait);
        }
        return stack.getCapability(FoodCapability.CAPABILITY).isPresent();
    }

    public FoodTrait getFoodTrait()
    {
        if (level != null)
        {
            final float temp = Climate.getAverageTemperature(level, getBlockPos());
            if (temp < FLConfig.SERVER.cellarLevel3Temperature.get())
            {
                return FLFoodTraits.SHELVED_3.get();
            }
            if (temp < FLConfig.SERVER.cellarLevel2Temperature.get())
            {
                return FLFoodTraits.SHELVED_2.get();
            }
        }
        return FLFoodTraits.SHELVED.get();
    }

    public Set<DeferredHolder<FoodTrait, FoodTrait>> getPossibleTraits()
    {
        return POSSIBLE;
    }

    public ItemInteractionResult use(ItemStack held)
    {
        assert level != null;
        var res = InteractionResult.PASS;
        if (!held.isEmpty() && isItemValid(0, held))
        {
            if (climateValid)
            {
                FoodCapability.applyTrait(held, getFoodTrait());
            }
            player.setItemInHand(hand, Helpers.mergeInsertStack(inventory, 0, held));

            FoodCapability.removeTrait(player.getItemInHand(hand), getFoodTrait());
            res = InteractionResult.sidedSuccess(level.isClientSide);
        }
        else if (held.isEmpty())
        {
            ItemStack stack = inventory.extractItem(0, player.isShiftKeyDown() ? Integer.MAX_VALUE : 1, false);
            if (stack.isEmpty()) return InteractionResult.PASS;
            FoodCapability.removeTrait(stack, getFoodTrait());
            ItemHandlerHelper.giveItemToPlayer(player, stack);
            res = InteractionResult.sidedSuccess(level.isClientSide);
        }
        updatePreservation(climateValid);
        markForSync();
        return res;
    }

    public void updatePreservation(boolean preserved)
    {
        if (preserved)
        {
            FoodCapability.applyTrait(inventory.getStackInSlot(0), getFoodTrait());
        }
        else
        {
            final ItemStack stack = inventory.getStackInSlot(0);
            for (FoodTrait trait : getPossibleTraits())
            {
                FoodCapability.removeTrait(stack, trait);
            }
        }
    }

    @Override
    public void setValid(Level level, BlockPos pos, boolean valid, int tier, ClimateType climate)
    {
        if (climate == ClimateType.CELLAR)
        {
            climateValid = valid;
            updatePreservation(valid);
        }
        markForSync();
    }

}
