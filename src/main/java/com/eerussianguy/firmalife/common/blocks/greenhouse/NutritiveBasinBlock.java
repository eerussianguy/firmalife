package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.Random;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;

public class NutritiveBasinBlock extends ExtendedBlock implements EntityBlockExtension
{
    public static final VoxelShape SHAPE = Shapes.join(
        Shapes.block(),
        box(1, 1, 1, 15, 16, 15),
        BooleanOp.ONLY_FIRST
    );

    public static final BooleanProperty WATERED = FLStateProperties.WATERED;

    public NutritiveBasinBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(WATERED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        final ItemStack held = player.getItemInHand(hand);
        if (!state.getValue(WATERED))
        {
            return held.getCapability(Capabilities.FLUID_ITEM).map(cap -> {
                final FluidStack fluid = cap.getFluidInTank(0);
                if (fluid.getFluid().isSame(Fluids.WATER) && fluid.getAmount() >= 100)
                {
                    cap.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                    Helpers.playSound(level, pos, SoundEvents.BUCKET_EMPTY);
                    level.setBlockAndUpdate(pos, state.setValue(WATERED, true));
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.FAIL;
            }).orElse(InteractionResult.PASS);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (random.nextFloat() < 0.2f)
        {
            final double x = pos.getX() + random.nextFloat();
            final double y = pos.getY() + (1f / 16);
            final double z = pos.getX() + random.nextFloat();
            level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, x, y, z, 0, 0, 0);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
    {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(WATERED));
    }
}
