package com.eerussianguy.firmalife.items;

import java.util.HashMap;
import java.util.Map;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.objects.items.ItemMisc;

public class ItemFruitPole extends ItemMisc
{
    private static final Map<IFruitTree, ItemFruitPole> MAP = new HashMap<>();

    public static ItemFruitPole get(IFruitTree tree)
    {
        return MAP.get(tree);
    }

    public ItemFruitPole(IFruitTree tree)
    {
        super(Size.SMALL, Weight.MEDIUM);
        if (MAP.put(tree, this) != null)
        {
            throw new IllegalStateException("There can only be one.");
        }
    }
}
