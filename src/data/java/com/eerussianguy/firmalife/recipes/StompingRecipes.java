package com.eerussianguy.firmalife.recipes;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.recipes.StompingRecipe;
import com.eerussianguy.firmalife.common.util.FLFruit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Helpers;

public interface StompingRecipes extends Recipes
{
    default void stompingRecipes()
    {
        stomp(
            notRotten(lacksTrait(itemOf(FLFruit.RED_GRAPES), FLFoodTraits.DRIED)),
            ItemStackProvider.of(itemOf(FLFood.SMASHED_RED_GRAPES)),
            FLHelpers.identifier("block/red_unsmashed_grapes"),
            FLHelpers.identifier("block/red_smashed_grapes"),
            SoundEvents.SLIME_SQUISH
        );
        stomp(
            notRotten(lacksTrait(itemOf(FLFruit.WHITE_GRAPES), FLFoodTraits.DRIED)),
            ItemStackProvider.of(itemOf(FLFood.SMASHED_WHITE_GRAPES)),
            FLHelpers.identifier("block/white_unsmashed_grapes"),
            FLHelpers.identifier("block/white_smashed_grapes"),
            SoundEvents.SLIME_SQUISH
        );
        stomp(
            Items.CHARCOAL,
            ItemStackProvider.of(itemOf(Powder.CHARCOAL), 4),
            Helpers.identifier("block/charcoal_pile"),
            Helpers.identifier("block/powder/charcoal"),
            TFCSounds.CHARCOAL.getFallSound()
        );
        stomp(
            notRotten(itemOf(FLFood.DEHYDRATED_SOYBEANS)),
            ItemStackProvider.of(itemOf(FLFood.SOYBEAN_PASTE)),
            FLHelpers.identifier("block/dehydrated_soybeans"),
            FLHelpers.identifier("block/soybean_paste"),
            SoundEvents.SLIME_SQUISH
        );

    }
    private void stomp(ItemLike input, ItemStackProvider output, ResourceLocation inTex, ResourceLocation outTex, SoundEvent sound) {
        stomp(Ingredient.of(input), output, inTex, outTex, sound);
    }

    private void stomp(Ingredient input, ItemStackProvider output, ResourceLocation inTex, ResourceLocation outTex, SoundEvent sound) {
        add(new StompingRecipe(input, output, inTex, outTex, sound));
    }
}
