package com.eerussianguy.firmalife.blocks;

import java.util.Random;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.registry.ItemsFL;
import mcp.MethodsReturnNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockGreenhouseDoor extends BlockDoor
{
    public BlockGreenhouseDoor()
    {
        super(Material.WOOD);
        this.setSoundType(SoundType.METAL);
        this.setHardness(3.0F);
        this.disableStats();
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    public Item getItem()
    {
        return ItemsFL.ITEM_GREENHOUSE_DOOR;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(HALF) == EnumDoorHalf.UPPER ? Items.AIR : this.getItem();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(this.getItem());
    }
}
