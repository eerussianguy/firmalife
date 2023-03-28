package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.container.StovetopPotContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.InventoryFluidTank;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.items.DynamicBowlFood;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.SoupPotRecipe;
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

    public static final int SLOTS = 4;
    private static final int DURATION = 1000;
    private static final float MIN_TEMP = 500f;

    private boolean hasRecipe = false;
    private ItemStack soupStack = ItemStack.EMPTY;

    public StovetopPotBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.STOVETOP_POT.get(), pos, state, StovetopPotInventory::new, FLHelpers.blockEntityName("stovetop_pot"));
        sidedInventory.on(new PartialItemHandler(inventory).insert(), Direction.Plane.HORIZONTAL);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return StovetopPotContainer.create(this, inventory, containerId);
    }

    public boolean hasOutput()
    {
        return !soupStack.isEmpty();
    }

    public void updateCachedRecipe()
    {
        if (inventory.getFluidInTank(0).getAmount() >= 100 && temperature > MIN_TEMP)
        {
            int found = 0;
            for (ItemStack stack : Helpers.iterate(inventory))
            {
                if (!stack.isEmpty())
                {
                    found++;
                }
            }
            if (found >= 3)
            {
                hasRecipe = true;
                return;
            }
        }
        hasRecipe = false;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, FLTags.Items.USABLE_IN_STOVETOP_SOUP);
    }

    public void handleCooking()
    {
        assert level != null;
        if (isBoiling())
        {
            if (boilingTicks < DURATION)
            {
                boilingTicks++;
                if (boilingTicks == 1) markForSync();
            }
            else
            {
                assembleSoup();
                boilingTicks = 0;
                updateCachedRecipe();
                for (int i = 0; i < SLOTS; i++)
                {
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                    inventory.getFluidHandler().drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                }
                markForSync();
            }
        }
        else if (boilingTicks > 0)
        {
            boilingTicks = 0;
            markForSync();
        }
    }

    public void assembleSoup()
    {
        int ingredientCount = 0;
        float water = 20, saturation = 2;
        float[] nutrition = new float[Nutrient.TOTAL];
        ItemStack soupStack = ItemStack.EMPTY;
        for (int i = 0; i < SLOTS; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            IFood food = stack.getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);
            if (food != null)
            {
                if (food.isRotten()) // this should mostly not happen since the ingredients are not rotten to start, but worth checking
                {
                    ingredientCount = 0;
                    break;
                }
                final FoodData data = food.getData();
                water += data.water();
                saturation += data.saturation();
                for (Nutrient nutrient : Nutrient.VALUES)
                {
                    nutrition[nutrient.ordinal()] += data.nutrient(nutrient);
                }
                ingredientCount++;
            }
        }
        if (ingredientCount > 0)
        {
            float multiplier = 1 - (0.05f * ingredientCount); // per-serving multiplier of nutrition
            water *= multiplier; saturation *= multiplier;
            Nutrient maxNutrient = Nutrient.GRAIN; // determines what item you get. this is a default
            float maxNutrientValue = 0;
            for (Nutrient nutrient : Nutrient.VALUES)
            {
                final int idx = nutrient.ordinal();
                nutrition[idx] *= multiplier;
                if (nutrition[idx] > maxNutrientValue)
                {
                    maxNutrientValue = nutrition[idx];
                    maxNutrient = nutrient;
                }
            }
            FoodData data = FoodData.create(SoupPotRecipe.SOUP_HUNGER_VALUE, water, saturation, nutrition, SoupPotRecipe.SOUP_DECAY_MODIFIER);
            int servings = (int) (ingredientCount / 2f) + 1;
            long created = FoodCapability.getRoundedCreationDate();

            soupStack = new ItemStack(TFCItems.SOUPS.get(maxNutrient).get(), servings);
            soupStack.getCapability(FoodCapability.CAPABILITY)
                .filter(food -> food instanceof DynamicBowlFood.DynamicBowlHandler)
                .ifPresent(food -> {
                    DynamicBowlFood.DynamicBowlHandler handler = (DynamicBowlFood.DynamicBowlHandler) food;
                    handler.setCreationDate(created);
                    handler.setFood(data);
                });
        }

        if (!soupStack.isEmpty())
        {
            this.soupStack = soupStack;
        }
    }

    public InteractionResult interactWithOutput(Player player, ItemStack clickedWith)
    {
        if (Helpers.isItem(clickedWith.getItem(), TFCTags.Items.SOUP_BOWLS) && !soupStack.isEmpty())
        {
            // set the internal bowl to the one we clicked with
            soupStack.getCapability(FoodCapability.CAPABILITY)
                .filter(food -> food instanceof DynamicBowlFood.DynamicBowlHandler)
                .ifPresent(food -> ((DynamicBowlFood.DynamicBowlHandler) food).setBowl(clickedWith));

            // take the player's bowl, give a soup
            clickedWith.shrink(1);
            ItemHandlerHelper.giveItemToPlayer(player, soupStack.split(1));
            markForSync();
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        if (nbt.contains("soup"))
        {
            soupStack = ItemStack.of(nbt.getCompound("soup"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        if (!soupStack.isEmpty())
        {
            nbt.put("soup", soupStack.save(new CompoundTag()));
        }
    }

    @Override
    public boolean isBoiling()
    {
        assert level != null;
        if (level.isClientSide)
        {
            return boilingTicks > 0;
        }
        return hasRecipe && temperature > MIN_TEMP;
    }

    public static class StovetopPotInventory extends BoilingInventory
    {
        public StovetopPotInventory(InventoryBlockEntity<?> entity)
        {
            super(entity, SLOTS, new InventoryFluidTank(1000, fluid -> fluid.getFluid().isSame(Fluids.WATER), (StovetopPotBlockEntity) entity));
        }
    }
}
