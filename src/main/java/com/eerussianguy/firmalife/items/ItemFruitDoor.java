package com.eerussianguy.firmalife.items;

import java.util.HashMap;
import java.util.Map;

import com.eerussianguy.firmalife.blocks.BlockFruitDoor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.IFruitTree;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;



@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemFruitDoor extends ItemDoor implements IItemSize {

    private static final Map<IFruitTree, ItemFruitDoor> MAP = new HashMap<>();

    public static ItemFruitDoor get(IFruitTree tree) { return MAP.get(tree); }

    public final IFruitTree tree;

    public ItemFruitDoor(BlockFruitDoor block)
    {
        super(block);
        if (MAP.put(block.tree, this) != null) throw new IllegalStateException("There can only be one.");
        tree = block.tree;
    }

    @Nonnull
    @Override
    public Size getSize(ItemStack stack) { return Size.VERY_LARGE; }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack) { return Weight.HEAVY; }

    @Override
    public int getItemStackLimit(ItemStack stack) { return getStackSize(stack); }
}
