package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class OvenHopperBlock extends FourWayDeviceBlock implements ICure
{
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    @Nullable
    private final Supplier<? extends Block> cured;

    public OvenHopperBlock(ExtendedProperties properties, @Nullable Supplier<? extends Block> cured)
    {
        super(properties, InventoryRemoveBehavior.DROP);

        registerDefaultState(getStateDefinition().any().setValue(ENABLED, true));
        this.cured = cured;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(ENABLED));
    }

    private void checkPoweredState(Level level, BlockPos pos, BlockState state, int flags)
    {
        final boolean noSignal = !level.hasNeighborSignal(pos);
        if (state.getValue(ENABLED) != noSignal)
        {
            level.setBlock(pos, state.setValue(ENABLED, noSignal), flags);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (!oldState.is(state.getBlock()))
        {
            this.checkPoweredState(level, pos, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
        this.checkPoweredState(level, pos, state, Block.UPDATE_INVISIBLE);
    }

    @Override
    public void cure(Level level, BlockState state, BlockPos pos)
    {
        if (getCured() != null)
        {
            BlockState cured = getCured().defaultBlockState();
            cured = Helpers.setProperty(cured, FACING, state.getValue(FACING));
            level.setBlockAndUpdate(pos, cured);
        }
    }

    @Override
    public @Nullable Block getCured()
    {
        return cured == null ? null : cured.get();
    }
}
