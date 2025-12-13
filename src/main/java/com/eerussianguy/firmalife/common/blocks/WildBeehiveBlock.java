package com.eerussianguy.firmalife.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;

public class WildBeehiveBlock extends HorizontalDirectionalBlock implements IForgeBlockExtension
{
    public static void honeyDripParticle(Level level, BlockPos pos, BlockState state)
    {
        if (state.getFluidState().isEmpty() && !(level.random.nextFloat() < 0.3F))
        {
            final VoxelShape shape = state.getCollisionShape(level, pos);
            final double y = shape.max(Direction.Axis.Y);
            if (y >= 1.0F && !Helpers.isBlock(state, BlockTags.IMPERMEABLE))
            {
                double d1 = shape.min(Direction.Axis.Y);
                if (d1 >0.0F)
                {
                    double y1 = pos.getY() + d1 - 0.05;
                    spawnFluidParticle(level, pos.getX() + shape.min(Direction.Axis.X), pos.getX() + shape.max(Direction.Axis.X), pos.getZ() + shape.min(Direction.Axis.Z), pos.getZ() + shape.max(Direction.Axis.Z), y1);
                }
                else
                {
                    final BlockPos below = pos.below();
                    final BlockState belowState = level.getBlockState(below);
                    final VoxelShape belowShape = belowState.getCollisionShape(level, below);
                    final double belowMaxY = belowShape.max(Direction.Axis.Y);
                    if ((belowMaxY < 1.0F || !belowState.isCollisionShapeFullBlock(level, below)) && belowState.getFluidState().isEmpty())
                    {
                        double y1 = pos.getY() - 0.05;
                        spawnFluidParticle(level, pos.getX() + shape.min(Direction.Axis.X), pos.getX() + shape.max(Direction.Axis.X), pos.getZ() + shape.min(Direction.Axis.Z), pos.getZ() + shape.max(Direction.Axis.Z), y1);
                    }
                }
            }
        }

    }

    private static void spawnFluidParticle(Level particleData, double x1, double x2, double z1, double z2, double y)
    {
        particleData.addParticle(ParticleTypes.DRIPPING_HONEY, Mth.lerp(particleData.random.nextDouble(), x1, x2), y, Mth.lerp(particleData.random.nextDouble(), z1, z2), 0.0F, 0.0F, 0.0F);
    }

    public static boolean canHangOn(BlockState state)
    {
        return Helpers.isBlock(state, BlockTags.LEAVES) || Helpers.isBlock(state, BlockTags.LOGS);
    }

    public static final BooleanProperty HONEY = FLStateProperties.HONEY;

    private static final TargetingConditions TARGETING = TargetingConditions.forNonCombat().range(15f).ignoreLineOfSight();

    private final ExtendedProperties properties;

    public WildBeehiveBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        registerDefaultState(getStateDefinition().any().setValue(HONEY, false));
        this.properties = properties;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        if (direction == Direction.UP && !canHangOn(neighborState))
        {
            level.getNearbyPlayers(TARGETING, null, new AABB(pos).inflate(15f)).forEach(FLBeehiveBlock::attack);
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return canHangOn(level.getBlockState(pos.above()));
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity entity, ItemStack tool)
    {
        if (!FLBeehiveBlock.hasFirepit(level, pos))
        {
            FLBeehiveBlock.attack(player);
        }
        super.playerDestroy(level, player, pos, state, entity, tool);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (state.getValue(HONEY))
        {
            for(int i = 0; i < random.nextInt(1) + 1; ++i)
            {
                WildBeehiveBlock.honeyDripParticle(level, pos, state);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING, HONEY));
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec()
    {
        return IForgeBlockExtension.getFakeBlockCodec();
    }
}
