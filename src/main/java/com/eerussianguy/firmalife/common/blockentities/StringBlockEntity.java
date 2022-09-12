package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.items.CapabilityItemHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.StringBlock;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.recipes.SmokingRecipe;
import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.common.blockentities.FirepitBlockEntity;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;

public class StringBlockEntity extends SimpleItemRecipeBlockEntity<SmokingRecipe>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, StringBlockEntity string)
    {
        if (level.getGameTime() % 40 == 0)
        {
            if (string.cachedRecipe == null)
            {
                string.resetCounter();
                return;
            }
            FirepitBlockEntity firepit = StringBlock.findFirepit(level, pos);
            if (firepit == null)
            {
                string.resetCounter();
            }
            else
            {
                BlockState pitState = level.getBlockState(pos);
                if (pitState.hasProperty(FirepitBlock.LIT) && !pitState.getValue(FirepitBlock.LIT))
                {
                    string.resetCounter();
                }
                else
                {
                    // check for correct fuel
                    firepit.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                        for (int i = FirepitBlockEntity.SLOT_FUEL_CONSUME; i < FirepitBlockEntity.SLOT_FUEL_INPUT; i++)
                        {
                            ItemStack item = inv.getStackInSlot(i);
                            if (!item.isEmpty() && !Helpers.isItem(item, FLTags.Items.SMOKING_FUEL))
                            {
                                FoodCapability.applyTrait(item, FLFoodTraits.RANCID_SMOKED);
                                string.resetCounter();
                            }
                        }
                    });
                }
            }
        }

        if (string.getTicksLeft() <= 0)
        {
            string.finish();
        }
    }

    public StringBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.STRING.get(), pos, state, FLHelpers.blockEntityName("string"), FLConfig.SERVER.smokingTicks);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        assert level != null;
        return SmokingRecipe.getRecipe(level, new ItemStackInventory(stack)) != null;
    }

    @Override
    public void updateCache()
    {
        assert level != null;
        cachedRecipe = SmokingRecipe.getRecipe(level, new ItemStackInventory(readStack()));
    }
}
