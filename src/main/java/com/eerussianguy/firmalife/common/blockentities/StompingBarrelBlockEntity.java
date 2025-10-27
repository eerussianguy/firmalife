package com.eerussianguy.firmalife.common.blockentities;

import java.util.Collections;
import java.util.List;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.recipes.StompingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.util.Helpers;

public class StompingBarrelBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int MAX_GRAPES = 16;

    private int stomps = 0;

    @Nullable
    private ResourceLocation texture;
    private boolean isOutputMode = false;

    public StompingBarrelBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.STOMPING_BARREL.get(), pos, state, defaultInventory(1), FirmaLife.MOD_ID);
    }

    public void stomp(Entity entity)
    {
        assert level != null;
        if (entity instanceof LivingEntity)
        {
            final ItemStack current = inventory.getStackInSlot(0);
            final StompingRecipe recipe = StompingRecipe.getRecipe(current);
            if (recipe == null)
                return;
            stomps += 1;
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            Helpers.playSound(level, worldPosition, recipe.getSound());

            if (stomps > 16)
            {
                final IFood food = FoodCapability.get(current);
                final List<FoodTrait> traits = food != null ? food.getTraits() : Collections.emptyList();
                final ItemStack newStack = recipe.assemble(current);
                newStack.setCount(newStack.getCount() * current.getCount());
                for (FoodTrait trait : traits)
                    FoodCapability.applyTrait(newStack, FoodTraits.REGISTRY.createIntrusiveHolder(trait));
                if (newStack.getCount() > MAX_GRAPES)
                {
                    Helpers.spawnItem(level, worldPosition, newStack.split(newStack.getCount() - MAX_GRAPES));
                }
                inventory.setStackInSlot(0, newStack);
                Helpers.playSound(level, worldPosition, recipe.getSound());
                stomps = 0;
                isOutputMode = true;
                texture = recipe.getOutputTexture();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
                markForSync();
            }
        }
    }

    @Nullable
    public ResourceLocation getTexture()
    {
        return texture;
    }

    public boolean isOutputMode()
    {
        return isOutputMode;
    }

    public int getStomps()
    {
        return stomps;
    }

    public ItemStack readStack()
    {
        return inventory.getStackInSlot(0);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        super.loadAdditional(nbt, access);
        stomps = nbt.getInt("stomps");
        isOutputMode = nbt.getBoolean("isOutput");
        if (nbt.contains("texture", Tag.TAG_STRING))
            texture = FLHelpers.res(nbt.getString("texture"));
        causeTextureUpdate();
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        super.saveAdditional(nbt, access);
        nbt.putInt("stomps", stomps);
        nbt.putBoolean("isOutput", isOutputMode);
        if (texture != null)
            nbt.putString("texture", texture.toString());
    }

    public void causeTextureUpdate()
    {
        if (level == null)
            return;
        // the case where nothing is in it, we can show nothing
        final ItemStack current = inventory.getStackInSlot(0);
        if (current.isEmpty())
        {
            texture = null;
            isOutputMode = false;
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            markForSync();
            return;
        }
        // only if there is no stomps and a valid recipe do we switch to a new recipe
        final StompingRecipe recipe = StompingRecipe.getRecipe(current);
        if (stomps == 0 && recipe != null)
        {
            texture = recipe.getInputTexture();
            isOutputMode = false;
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            markForSync();
        }
        // switching to the output texture is handled by the recipe assembly
        // we fall out here as a means of respecting that the output texture should stick until the device is emptied.
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        markForSync();
        stomps = 0;
        causeTextureUpdate();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        assert level != null;
        return StompingRecipe.getRecipe(stack) != null;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return MAX_GRAPES;
    }
}
