package com.eerussianguy.firmalife.common.blocks.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.ISlowEntities;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.util.Helpers;

public class GrapeFluffBlock extends GroundcoverBlock implements ISlowEntities
{
    public static final EnumProperty<Lifecycle> LIFECYCLE = TFCBlockStateProperties.LIFECYCLE;

    public GrapeFluffBlock(ExtendedProperties properties)
    {
        super(properties, GroundcoverBlock.PIXEL_HIGH);
        registerDefaultState(getStateDefinition().any().setValue(LIFECYCLE, Lifecycle.HEALTHY).setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    public float slowEntityFactor(BlockState blockState)
    {
        return 0.5f;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(LIFECYCLE));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        final BlockState superState = super.updateShape(state, facing, facingState, level, pos, facingPos);
        if (facing == Direction.DOWN && superState.getBlock() instanceof GrapeFluffBlock && facingState.hasProperty(LIFECYCLE))
        {
            return Helpers.copyProperty(superState, facingState, LIFECYCLE);
        }
        return superState;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final Block block = level.getBlockState(pos.below()).getBlock();
        return block instanceof GrapeTrellisPostWithPlantBlock || block instanceof GrapeStringWithPlantBlock;
    }
}
