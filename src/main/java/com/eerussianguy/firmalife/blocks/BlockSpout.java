package com.eerussianguy.firmalife.blocks;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.eerussianguy.firmalife.init.StatePropertiesFL;
import com.eerussianguy.firmalife.particle.ParticlesFL;
import com.eerussianguy.firmalife.util.HelpersFL;
import com.eerussianguy.firmalife.util.IWaterable;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class BlockSpout extends BlockNonCube
{
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(5.0D / 16, 7.0D / 16, 5.0D / 16, 11.0D / 16, 16.0D / 16, 11.0D / 16);

    private final boolean range;

    public BlockSpout(boolean range)
    {
        super(Material.IRON);
        setHardness(2.0f);
        setResistance(3.0f);
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
        this.range = range;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SHAPE;
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        if (!world.isRemote && doWater(world, pos, true))
        {
            if (range)
            {
                for (int x = -2; x <= 2; x++)
                {
                    for (int y = -1; y > -6; y--)
                    {
                        for (int z = -2; z <= 2; z++)
                        {
                            BlockPos checkPos = pos.add(x, y, z);
                            waterPosition(world, checkPos);
                        }
                    }
                }
            }
            else
            {
                for (int i = -1; i > -6; i--)
                {
                    BlockPos checkPos = pos.add(0, i, 0);
                    if (waterPosition(world, checkPos)) return;
                }
            }

        }
    }

    private boolean waterPosition(World world, BlockPos checkPos)
    {
        TileEntity te = Helpers.getTE(world, checkPos, TileEntity.class);
        if (te instanceof IWaterable)
        {
            ((IWaterable) te).setWater(2);
            IBlockState stateAt = world.getBlockState(checkPos);
            world.setBlockState(checkPos, stateAt.withProperty(StatePropertiesFL.WET, true));
            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
    {
        if (doWater(world, pos, false))
        {
            if (range)
            {
                double speed = 0.05D * (Math.sin(world.getWorldTime()) * 2.0D - 1.0D);
                for (int i = 0; i < 5; i++)
                {
                    ParticlesFL.SPRINKLE.spawn(world, pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, speed * HelpersFL.nextSign(rand), 0.0D, speed * HelpersFL.nextSign(rand), 130);
                }
            }
            else
            {
                ParticlesFL.SPRINKLE.spawn(world, pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D, 130);
            }
        }
    }

    private boolean doWater(World world, BlockPos pos, boolean execute)
    {
        TileEntity te = world.getTileEntity(pos.up());
        if (te != null)
        {
            IFluidHandler cap = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);
            if (cap != null)
            {
                return cap.drain(new FluidStack(FluidsTFC.FRESH_WATER.get(), 1), execute) != null;
            }
        }
        return false;
    }
}
