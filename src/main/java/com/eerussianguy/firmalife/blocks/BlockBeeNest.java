package com.eerussianguy.firmalife.blocks;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.eerussianguy.firmalife.init.FoodFL;
import com.eerussianguy.firmalife.registry.EffectsFL;
import com.eerussianguy.firmalife.registry.ItemsFL;
import net.dries007.tfc.Constants;

@ParametersAreNonnullByDefault
public class BlockBeeNest extends BlockNonCube
{
    public static final AxisAlignedBB SHAPE = new AxisAlignedBB(2.0D / 16, 4.0D / 16, 2.0D / 16, 14.0D / 16, 16.0D / 16, 14.0D / 16);

    public BlockBeeNest()
    {
        super(Material.WOOD);
        setHardness(2.0f);
        setResistance(2.0f);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        BlockPos up = pos.up();
        if (worldIn.getBlockState(up).getMaterial() != Material.LEAVES)
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        int count1 = Constants.RNG.nextInt(3);
        drops.add(new ItemStack(ItemsFL.HONEYCOMB, count1));
        int count2 = Constants.RNG.nextInt(3);
        drops.add(new ItemStack(ItemsFL.getFood(FoodFL.RAW_HONEY), count2));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
    {
        if (world.isDaytime())
        {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            for (int i = 0; i < 3 + rand.nextInt(4); i++)
            {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + rand.nextFloat() - rand.nextFloat(), y + rand.nextFloat(), z + rand.nextFloat() - rand.nextFloat(),
                    0.5 * (rand.nextFloat() - rand.nextFloat()), 0.5 * (rand.nextFloat() - rand.nextFloat()), 0.5 * (rand.nextFloat() - rand.nextFloat()));
            }
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        if (world.isDaytime())
        {
            EntityPlayer player = world.getNearestAttackablePlayer(pos, 10, 10);
            if (player != null)
            {
                player.addPotionEffect(new PotionEffect(EffectsFL.SWARM, 30 * 20));
            }
        }
        super.breakBlock(world, pos, state);
    }
}
