package com.eerussianguy.firmalife.common.blocks.greenhouse;

import com.eerussianguy.firmalife.common.blockentities.HydroponicPlanterBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class HydroponicPlanterBlock extends QuadPlanterBlock
{
    public HydroponicPlanterBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public PlanterType getPlanterType()
    {
        return PlanterType.HYDROPONIC;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (random.nextFloat() < 0.2f && level.getBlockEntity(pos) instanceof HydroponicPlanterBlockEntity planter && planter.hasPipe())
        {
            level.addParticle(TFCParticles.BUBBLE.get(), pos.getX() + 0.5f + Helpers.triangle(random, 0.25f), pos.getY() + (5f / 16f), pos.getZ() + 0.5f + Helpers.triangle(random, 0.25f), Helpers.triangle(random, 0.1f), 0.1f, Helpers.triangle(random, 0.1f));
        }
    }
}
