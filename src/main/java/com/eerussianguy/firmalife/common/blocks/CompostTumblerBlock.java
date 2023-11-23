package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.blockentities.CompostTumblerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;


public class CompostTumblerBlock extends FourWayDeviceBlock
{
    public static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(dir -> Shapes.or(
        Helpers.rotateShape(dir, 0, 0, 0, 16, 2, 2),
        Helpers.rotateShape(dir, 0, 0, 2, 2, 1, 16),
        Helpers.rotateShape(dir, 14, 0, 2, 16, 1, 16),
        Helpers.rotateShape(dir, 7, 7, 0, 9, 9, 3),
        Helpers.rotateShape(dir, 7, 2, 0, 9, 7, 2),
        Helpers.rotateShape(dir, 2, 2, 2, 14, 13, 16)
    ));

    public CompostTumblerBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (level.getBlockEntity(pos) instanceof CompostTumblerBlockEntity tumbler && tumbler.isReady())
        {
            final SimpleParticleType particle = !tumbler.isRotten() ? TFCParticles.COMPOST_READY.get().getType() : TFCParticles.COMPOST_ROTTEN.get().getType();

            final double x = pos.getX() + random.nextDouble();
            final double y = pos.getY() + 1 + random.nextDouble() / 5D;
            final double z = pos.getZ() + random.nextDouble();
            final int count = Mth.nextInt(random, 0, 4);
            for (int i = 0; i < count; i++)
            {
                level.addParticle(particle, x, y, z, 0D, 0D, 0D);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (level.getBlockEntity(pos) instanceof CompostTumblerBlockEntity composter && composter.getRotationNode().rotation() == null)
        {
            return composter.use(player.getItemInHand(hand), player, level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (level.getBlockEntity(pos) instanceof CompostTumblerBlockEntity composter)
        {
            composter.randomTick();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        final int id = state.getValue(FACING).get2DDataValue();
        return SHAPES[id];
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (level.getBlockEntity(pos) instanceof CompostTumblerBlockEntity tumbler)
        {
            tumbler.resetCounter();
        }
    }
}
