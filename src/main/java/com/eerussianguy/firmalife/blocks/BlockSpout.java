package com.eerussianguy.firmalife.blocks;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import com.eerussianguy.firmalife.init.StatePropertiesFL;
import com.eerussianguy.firmalife.util.HelpersFL;
import com.eerussianguy.firmalife.util.IWaterable;
import net.dries007.tfc.objects.blocks.wood.BlockBarrel;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.te.TEBarrel;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class BlockSpout extends BlockNonCube
{
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(5.0D / 16, 7.0D / 16, 5.0D / 16, 11.0D / 16, 16.0D / 16, 11.0D / 16);

    public BlockSpout()
    {
        super(Material.IRON);
        setHardness(2.0f);
        setResistance(3.0f);
        setSoundType(SoundType.METAL);
        setTickRandomly(true);
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
        if (!world.isRemote && doWater(world, pos))
        {
            HelpersFL.sendVanillaParticleToClient(EnumParticleTypes.WATER_SPLASH, world, pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            for (int i = -1; i > -6; i--)
            {
                BlockPos checkPos = pos.add(0, i, 0);
                TileEntity te = Helpers.getTE(world, checkPos, TileEntity.class);
                if (te instanceof IWaterable)
                {
                    ((IWaterable) te).setWater(2);
                    IBlockState stateAt = world.getBlockState(checkPos);
                    world.setBlockState(checkPos, stateAt.withProperty(StatePropertiesFL.WET, true));
                    return;
                }
            }
        }
    }

    private boolean doWater(World world, BlockPos pos)
    {
        BlockPos up = pos.up();
        if (world.getBlockState(up).getBlock() instanceof BlockBarrel)
        {
            TEBarrel te = Helpers.getTE(world, up, TEBarrel.class);
            if (te != null)
            {
                IFluidHandler cap = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (cap != null)
                {
                    return cap.drain(new FluidStack(FluidsTFC.FRESH_WATER.get(), 1), true) != null;
                }
            }
        }
        return false;
    }
}
