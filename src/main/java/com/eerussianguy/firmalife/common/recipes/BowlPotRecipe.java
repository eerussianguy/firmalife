package com.eerussianguy.firmalife.common.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.outputs.PotOutput;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tooltip.BlockEntityTooltip;
import net.dries007.tfc.util.tooltip.BlockEntityTooltips;

public class BowlPotRecipe extends PotRecipe
{
    public static final MapCodec<BowlPotRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        PotRecipe.CODEC.forGetter(c -> c),
        ItemStack.CODEC.fieldOf("item_output").forGetter(c -> c.itemOutput),
        FoodData.CODEC.fieldOf("food").forGetter(c -> c.food)
    ).apply(i, BowlPotRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BowlPotRecipe> STREAM_CODEC = StreamCodec.composite(
        PotRecipe.STREAM_CODEC, c -> c,
        ItemStack.STREAM_CODEC, c -> c.itemOutput,
        FoodData.STREAM_CODEC, c -> c.food,
        BowlPotRecipe::new
    );

    public static final PotOutput.OutputType OUTPUT_TYPE = (provider, nbt) -> {
        ItemStack stack = ItemStack.parseOptional(provider, nbt.getCompound("item"));
        return new BowlOutput(stack);
    };
    private final ItemStack itemOutput;
    private final FoodData food;

    public BowlPotRecipe(PotRecipe base, ItemStack itemOutput, FoodData food)
    {
        super(base);
        this.itemOutput = FoodCapability.setNonDecaying(itemOutput);
        this.food = food;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider access)
    {
        return itemOutput;
    }

    @Override
    public PotOutput getOutput(PotBlockEntity.PotInventory inv)
    {
        final ItemStack item = itemOutput.copy();
        FoodCapability.setFoodForDynamicItemOnCreate(item, food);
        return new BowlOutput(item);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return FLRecipeSerializers.BOWL_POT.get();
    }

    public record BowlOutput(ItemStack stack) implements PotOutput
    {
        @Override
        public boolean isEmpty()
        {
            return stack.isEmpty();
        }

        @Override
        public int getFluidColor()
        {
            return TFCFluids.ALPHA_MASK | 0x24b1d1;
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
            return BowlPotRecipe.OUTPUT_TYPE;
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

}
