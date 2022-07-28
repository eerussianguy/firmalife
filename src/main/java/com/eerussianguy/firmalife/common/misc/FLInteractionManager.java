package com.eerussianguy.firmalife.common.misc;

import net.minecraft.world.item.crafting.Ingredient;

import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.container.FLContainerProviders;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.container.TFCContainerProviders;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.BlockItemPlacement;
import net.dries007.tfc.util.InteractionManager;

public class FLInteractionManager
{
    public static void init()
    {
        InteractionManager.register(new BlockItemPlacement(TFCItems.WOOL_YARN, FLBlocks.WOOL_STRING));

        InteractionManager.register(Ingredient.of(FLTags.Items.PUMPKIN_KNAPPING), false, true, InteractionManager.createKnappingInteraction((s, p) -> p.getInventory().contains(TFCTags.Items.KNIVES), FLContainerProviders.PUMPKIN_KNAPPING));
    }
}
