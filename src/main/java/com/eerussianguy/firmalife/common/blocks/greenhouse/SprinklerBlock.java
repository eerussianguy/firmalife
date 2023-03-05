package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.List;
import java.util.Random;
import com.eerussianguy.firmalife.common.blockentities.ClimateReceiver;
import com.eerussianguy.firmalife.common.blockentities.SprinklerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;

public class SprinklerBlock extends DeviceBlock implements HoeOverlayBlock
{
    public static final VoxelShape SHAPE = Shapes.or(
        box(5, 13, 5, 11, 16, 11),
        box(6, 10, 6, 10, 13, 10),
        box(7, 7, 7, 9, 10, 9),
        box(10, 10, 7, 13, 12, 9),
        box(7, 10, 10, 9, 12, 13),
        box(3, 10, 7, 6, 12, 9),
        box(7, 10, 3, 9, 12, 6)
    );

    public SprinklerBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.NOOP);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random)
    {
        if (random.nextFloat() < 0.3f)
        {
            if (level.getBlockEntity(pos) instanceof SprinklerBlockEntity sprinkler && sprinkler.isActive())
            {
                level.addParticle(ParticleTypes.DRIPPING_DRIPSTONE_WATER, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 0, 0, 0);
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.POINTED_DRIPSTONE_DRIP_WATER, SoundSource.BLOCKS, 0.5f + random.nextFloat(), 1f, false);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos origin, Random rand)
    {
        if (level.getBlockEntity(origin) instanceof SprinklerBlockEntity sprinkler && sprinkler.isValid())
        {
            sprinkler.getCapability(Capabilities.FLUID, Direction.UP).ifPresent(cap -> {
                for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-2, -6, -2), origin.offset(2, -1, 2)))
                {
                    final ClimateReceiver receiver = ClimateReceiver.get(level, pos);
                    if (receiver != null)
                    {
                        if (cap.getFluidInTank(0).getAmount() > 10)
                        {
                            if (receiver.addWater(0.1f))
                            {
                                cap.drain(10, IFluidHandler.FluidAction.EXECUTE);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, List<Component> text, boolean debug)
    {
        if (level.getBlockEntity(pos) instanceof SprinklerBlockEntity sprinkler)
        {
            text.add(Helpers.translatable(sprinkler.isValid() ? "firmalife.greenhouse.valid_block" : "firmalife.greenhouse.invalid_block"));
        }
    }
}
