package com.eerussianguy.firmalife.blocks;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.items.ItemFruitDoor;
import com.eerussianguy.firmalife.registry.BlocksFL;
import com.eerussianguy.firmalife.registry.ItemsFL;
import mcp.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockFruitDoor extends BlockDoor
{

    public BlockFruitDoor()
    {
        super(Material.WOOD);
        setHardness(3.0F);
        disableStats();
    }

    @Nullable
    public Item getItem() //From the way we build the ImmutableLists these two should always be sorted
    {
        Iterator<ItemFruitDoor> ifd = ItemsFL.getAllFruitDoors().iterator();
        Iterator<BlockFruitDoor> bfd = BlocksFL.getAllFruitDoors().iterator();
        while (ifd.hasNext() && bfd.hasNext())
        {
            ItemFruitDoor i = ifd.next();
            BlockFruitDoor b = bfd.next();
            if (this == b)
                return i;
        }
        return null;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(HALF) == EnumDoorHalf.UPPER ? Items.AIR : getItem();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(getItem());
    }
}
