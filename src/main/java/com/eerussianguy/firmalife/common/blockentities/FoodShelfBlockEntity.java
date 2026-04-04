package com.eerussianguy.firmalife.common.blockentities;

import java.util.Set;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.client.model.InventoryBlockModel;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
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
        super(type, pos, state, defaultInventory(1), FirmaLife.MOD_ID);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        requestModelDataUpdate();
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
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        super.loadAdditional(nbt, access);
        climateValid = nbt.getBoolean("climateValid");
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        super.saveAdditional(nbt, access);
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
        return FoodCapability.get(stack) != null;
    }

    public Holder<FoodTrait> getFoodTrait()
    {
        if (level != null)
        {
            final float temp = Climate.getAverageTemperature(level, getBlockPos());
            if (temp < FLConfig.SERVER.cellarLevel3Temperature.get())
            {
                return FLFoodTraits.SHELVED_3;
            }
            if (temp < FLConfig.SERVER.cellarLevel2Temperature.get())
            {
                return FLFoodTraits.SHELVED_2;
            }
        }
        return FLFoodTraits.SHELVED;
    }

    public Set<DeferredHolder<FoodTrait, FoodTrait>> getPossibleTraits()
    {
        return POSSIBLE;
    }

    public ItemInteractionResult use(ItemStack held, Player player)
    {
        assert level != null;
        var res = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!held.isEmpty() && isItemValid(0, held))
        {
            if (climateValid)
            {
                FoodCapability.applyTrait(held, getFoodTrait());
            }

            Helpers.playPlaceSound(player, level, player.blockPosition(), Blocks.CAKE.defaultBlockState());
            ItemStack remainder = Helpers.mergeInsertStack(inventory, 0, held);
            held.setCount(remainder.getCount());

            FoodCapability.removeTrait(held, getFoodTrait());

            res = ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        else if (held.isEmpty())
        {
            ItemStack stack = inventory.extractItem(0, player.isShiftKeyDown() ? Integer.MAX_VALUE : 1, false);
            if (stack.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            FoodCapability.removeTrait(stack, getFoodTrait());
            ItemHandlerHelper.giveItemToPlayer(player, stack);
            res = ItemInteractionResult.sidedSuccess(level.isClientSide);
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
            for (Holder<FoodTrait> trait : getPossibleTraits())
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

    @Override
    public ModelData getModelData()
    {
        assert level != null;
        return InventoryBlockModel.InventoryModelData.of(level, this);
    }
}
