package com.eerussianguy.firmalife.common.blocks.bee;

import java.util.List;
import java.util.function.Consumer;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBeehiveBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeAbility;
import com.eerussianguy.firmalife.common.misc.FLPOIs;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;

public class WildBeehiveBlock extends HorizontalDirectionalBlock implements IForgeBlockExtension, HoeOverlayBlock
{
    public static void honeyDripParticle(Level level, BlockPos pos, BlockState state)
    {
        if (state.getFluidState().isEmpty() && !(level.random.nextFloat() < 0.3F))
        {
            final VoxelShape shape = state.getCollisionShape(level, pos);
            if (!Helpers.isBlock(state, BlockTags.IMPERMEABLE))
            {
                double minY = shape.min(Direction.Axis.Y);
                if (minY > 0.0F)
                {
                    double y1 = pos.getY() + minY - 0.05;
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
                        spawnFluidParticle(level, pos.getX() + shape.min(Direction.Axis.X), pos.getX() + shape.max(Direction.Axis.X), pos.getZ() + shape.min(Direction.Axis.Z), pos.getZ() + shape.max(Direction.Axis.Z), pos.getY() - 0.05);
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

    public static boolean isWarmEnough(Level level, BlockPos pos)
    {
        return Climate.getInstantTemperature(level, pos) > BeeAbility.getMinTemperature(0);
    }

    public static void angerAllNearby(BlockState state, LevelAccessor level, BlockPos pos)
    {
        List<Player> list = level.getNearbyPlayers(TARGETING, null, new AABB(pos).inflate(15f));
        if (state.getValue(BEES))
            list.forEach(BaseBeehiveBlock::attack);
    }

    public static final VoxelShape SHAPE = Shapes.or(
        box(0, 0, 0, 16, 11, 16),
        box(3, 12, 3, 13, 15, 13)
    );

    public static final BooleanProperty HONEY = FLStateProperties.HONEY;
    public static final BooleanProperty BEES = FLStateProperties.BEES;

    private static final TargetingConditions TARGETING = TargetingConditions.forNonCombat().range(15f).ignoreLineOfSight();

    private final ExtendedProperties properties;

    public WildBeehiveBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        registerDefaultState(getStateDefinition().any().setValue(HONEY, false).setValue(BEES, true));
        this.properties = properties;
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos blockPos, BlockState blockState, Consumer<Component> tooltip, boolean b)
    {
        tooltip.accept(Component.translatable("firmalife.beehive." + (blockState.getValue(BEES) ? "has_queen": "no_queen")));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (state.getValue(BEES))
        {
            final boolean honeyed = state.getValue(HONEY);
            final boolean warm = isWarmEnough(level, pos);
            if (warm != honeyed)
            {
                level.setBlockAndUpdate(pos, state.setValue(HONEY, warm));
            }
            if (!warm)
                return;
            final BlockPos hivePos = FLHelpers.getPoint(level, pos, 15, FLPOIs.BEEHIVES, (l, p) -> l.getBlockEntity(p) instanceof FLBeehiveBlockEntity hive && !hive.getBee().hasQueen());
            if (hivePos != null && level.getBlockEntity(hivePos) instanceof FLBeehiveBlockEntity hive)
            {
                hive.linkSwarmFrom(pos);
            }
        }

    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        if (direction == Direction.UP && !canHangOn(neighborState))
        {
            angerAllNearby(state, level, pos);
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return canHangOn(level.getBlockState(pos.above()));
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity entity, ItemStack tool)
    {
        if (state.getValue(BEES))
        {
            BaseBeehiveBlock.attack(player);
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
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile)
    {
        final BlockPos pos = hit.getBlockPos();
        if (!level.isClientSide && projectile.mayInteract(level, pos) && projectile.mayBreak(level))
        {
            level.destroyBlock(pos, true, projectile);
            angerAllNearby(state, level, pos);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(FACING, HONEY, BEES));
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
