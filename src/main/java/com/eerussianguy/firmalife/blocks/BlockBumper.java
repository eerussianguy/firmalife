package com.eerussianguy.firmalife.blocks;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockBumper extends BlockNonCube
{
    public BlockBumper()
    {
        super(Material.WOOD);
        setHardness(1.0f);
        setResistance(1.0f);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote && hand == EnumHand.MAIN_HAND)
        {
            playerIn.sendMessage(new TextComponentTranslation("tooltip.firmalife.bumper"));
        }
        return false;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {
        boolean flag = playerIn.getHeldItemMainhand().getItem() == Item.getItemFromBlock(this);
        playerIn.knockBack(playerIn, flag ? 3.0f : 0.2f, 0.2f, 0.2f);
        if (!worldIn.isRemote)
            playerIn.sendMessage(new TextComponentTranslation("tooltip.firmalife.bumper"));
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (entityIn instanceof EntityPlayer)
        {
            if (((EntityPlayer) entityIn).getHeldItemMainhand().getItem() == Item.getItemFromBlock(this))
            {
                entityIn.motionY = 3.0D;
                entityIn.motionX *= -3.0D;
                entityIn.motionZ *= -3.0D;
            }
        }
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        worldIn.playSound(null, pos, SoundEvents.BLOCK_CLOTH_FALL, SoundCategory.BLOCKS, 3.0F, 1.0F);
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
    {
        return false;
    }

    /**
     * Half slab collision while being in a full block causes the game to slide the player off.
     */
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return BlockQuadPlanter.QUAD_SHAPE;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return (face == EnumFacing.DOWN) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return BlockQuadPlanter.QUAD_SHAPE;
    }
}
