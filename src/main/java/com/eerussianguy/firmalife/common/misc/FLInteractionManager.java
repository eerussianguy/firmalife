package com.eerussianguy.firmalife.common.misc;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.BlockItemPlacement;
import net.dries007.tfc.util.InteractionManager;

public class FLInteractionManager
{
    public static void init()
    {
        InteractionManager.register(new BlockItemPlacement(TFCItems.WOOL_YARN, FLBlocks.WOOL_STRING));
    }
}
