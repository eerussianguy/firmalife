package com.eerussianguy.firmalife.items;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.blocks.BlockJars;
import com.eerussianguy.firmalife.init.FoodFL;
import com.eerussianguy.firmalife.registry.ItemsFL;
import mcp.MethodsReturnNonnullByDefault;

import net.dries007.tfc.objects.items.itemblock.ItemBlockTFC;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemBlockJar extends ItemBlockTFC
{
    public ItemBlockJar(Block block)
    {
        super(block);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getBlock() == block)
        {
            if (!worldIn.isRemote && hand == EnumHand.MAIN_HAND)
            {
                int jars = state.getValue(BlockJars.JARS);
                if (jars < 4)
                {
                    worldIn.setBlockState(pos, state.withProperty(BlockJars.JARS, jars + 1));
                    player.getHeldItem(hand).shrink(1);
                    return EnumActionResult.SUCCESS;
                }
            }
            return EnumActionResult.FAIL;
        }
        if (worldIn.getBlockState(pos).isSideSolid(worldIn, pos, EnumFacing.UP))
        {
            return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack)
    {
        return new ItemStack(ItemsFL.JAR);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack)
    {
        return true;
    }
}
