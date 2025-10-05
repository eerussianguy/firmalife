package com.eerussianguy.firmalife.common.recipes;

import java.util.function.Function;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.outputs.PotOutput;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tooltip.BlockEntityTooltip;
import net.dries007.tfc.util.tooltip.BlockEntityTooltips;

import static net.dries007.tfc.common.recipes.SoupPotRecipe.*;

public class StinkySoupRecipe extends PotRecipe
{
    public static final PotOutput.OutputType OUTPUT_TYPE = (provider, nbt) -> {
        ItemStack stack = ItemStack.parseOptional(provider, nbt.getCompound("item"));
        return new StinkOutput(stack);
    };

    public static final MapCodec<StinkySoupRecipe> CODEC = PotRecipe.CODEC.xmap(StinkySoupRecipe::new, Function.identity());
    public static final StreamCodec<RegistryFriendlyByteBuf, StinkySoupRecipe> STREAM_CODEC = PotRecipe.STREAM_CODEC.map(StinkySoupRecipe::new, Function.identity());

    public StinkySoupRecipe(PotRecipe base)
    {
        super(base);
    }

    public record StinkOutput(ItemStack stack) implements PotOutput
    {
        @Override
        public boolean isEmpty()
        {
            return stack.isEmpty();
        }

        @Override
        public int getFluidColor()
        {
            return TFCFluids.ALPHA_MASK | 0x6666ff;
        }

        @Override
        public ItemInteractionResult onInteract(PotBlockEntity entity, Player player, ItemStack clickedWith)
        {
            if (Helpers.isItem(clickedWith.getItem(), TFCTags.Items.SOUP_BOWLS) && !stack.isEmpty())
            {
                // set the internal bowl to the one we clicked with
                stack.set(TFCComponents.BOWL, new ItemComponent(clickedWith.copyWithCount(1)));
                // take the player's bowl, give a soup
                clickedWith.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, stack.split(1));
                return ItemInteractionResult.sidedSuccess(player.level().isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        @Override
        public void write(HolderLookup.Provider provider, CompoundTag nbt)
        {
            nbt.put("item", stack.save(provider, new CompoundTag()));
        }

        @Override
        public OutputType getType()
        {
            return StinkySoupRecipe.OUTPUT_TYPE;
        }

        @Override
        public BlockEntityTooltip getTooltip()
        {
            return ((level, blockState, blockPos, blockEntity, tooltip) -> {
                BlockEntityTooltips.itemWithCount(tooltip, this.stack);
                FoodCapability.addTooltipInfo(this.stack, tooltip);
            });
        }
    }

    @Override
    public PotOutput getOutput(PotBlockEntity.PotInventory inventory)
    {
        int ingredientCount = 0;
        float water = 20, saturation = 2;
        float[] nutrition = new float[Nutrient.TOTAL];
        ItemStack soupStack = ItemStack.EMPTY;
        for (int i = PotBlockEntity.SLOT_EXTRA_INPUT_START; i <= PotBlockEntity.SLOT_EXTRA_INPUT_END; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            IFood food = FoodCapability.get(stack);
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
            for (Nutrient nutrient : Nutrient.VALUES)
            {
                final int idx = nutrient.ordinal();
                nutrition[idx] *= multiplier;
            }
            final FoodData data = new FoodData(SOUP_HUNGER_VALUE, water, saturation, 0, nutrition, SOUP_DECAY_MODIFIER);
            int servings = (int) (ingredientCount / 2f) + 1;

            soupStack = new ItemStack(FLItems.STINKY_SOUP.get(), servings);

            FoodCapability.setFoodForDynamicItemOnCreate(soupStack, data);
        }

        return new StinkOutput(soupStack);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.STINKY_SOUP.get();
    }

}
