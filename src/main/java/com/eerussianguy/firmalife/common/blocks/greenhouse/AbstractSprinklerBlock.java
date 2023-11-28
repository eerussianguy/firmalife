package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.common.misc.FLParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.client.particle.FluidParticleOption;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;

public class AbstractSprinklerBlock extends DeviceBlock implements HoeOverlayBlock
{
    public static final BooleanProperty STASIS = FLStateProperties.STASIS;



    private final Predicate<Direction> pipeConnection;
    private final Function<BlockPos, Iterable<BlockPos>> pathMaker;
    private final double dh;
    private final double dy;
    private final Vec3 particleOffset;

    public AbstractSprinklerBlock(ExtendedProperties properties, Predicate<Direction> pipeConnection, Function<BlockPos, Iterable<BlockPos>> pathMaker, double dh, double dy, Vec3 particleOffset)
    {
        super(properties, InventoryRemoveBehavior.NOOP);
        this.pipeConnection = pipeConnection;
        this.pathMaker = pathMaker;
        this.dh = dh;
        this.dy = dy;
        this.particleOffset = particleOffset;
        registerDefaultState(getStateDefinition().any().setValue(STASIS, false));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (state.getValue(STASIS))
        {
            final Vec3 part = particleOffset(state);
            level.addParticle(new FluidParticleOption(FLParticles.SPRINKLER.get(), Fluids.WATER), pos.getX() + part.x, pos.getY() + part.y, pos.getZ() + part.z, Mth.nextDouble(random, -dh, dh) * 0.1, dy * 0.33, Mth.nextDouble(random, -dh, dh) * 0.1);
            if (random.nextFloat() < 0.1f)
            {
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.POINTED_DRIPSTONE_DRIP_WATER, SoundSource.BLOCKS, 0.5f + random.nextFloat(), 1f, false);
            }
        }
    }

    protected Vec3 particleOffset(BlockState state)
    {
        return particleOffset;
    }

    public Predicate<Direction> getPipeConnection()
    {
        return pipeConnection;
    }

    public Function<BlockPos, Iterable<BlockPos>> getPathMaker()
    {
        return pathMaker;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(STASIS));
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, List<Component> text, boolean debug)
    {
        text.add(Component.translatable(state.getValue(STASIS) ? "firmalife.greenhouse.valid_sprinkler" : "firmalife.greenhouse.invalid_sprinkler"));
    }
}
