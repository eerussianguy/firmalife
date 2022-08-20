package com.eerussianguy.firmalife.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.MutatingPlantBlock;
import com.eerussianguy.firmalife.common.items.FLItems;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;

public class SeedBall extends ThrowableItemProjectile
{
    public SeedBall(EntityType<? extends ThrowableItemProjectile> type, Level level)
    {
        super(type, level);
    }

    public SeedBall(double x, double y, double z, Level level)
    {
        super(FLEntities.SEED_BALL.get(), x, y, z, level);
    }

    public SeedBall(LivingEntity entity, Level level)
    {
        super(FLEntities.SEED_BALL.get(), entity, level);
    }

    @Override
    protected Item getDefaultItem()
    {
        return FLItems.SEED_BALL.get();
    }

    @Override
    public void handleEntityEvent(byte value)
    {
        if (value == 3)
        {
            ParticleOptions itemParticle = new ItemParticleOption(ParticleTypes.ITEM, getDefaultItem().getDefaultInstance());

            for(int i = 0; i < 8; ++i)
            {
                this.level.addParticle(itemParticle, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
                this.level.addParticle(TFCParticles.STEAM.get(), getX(), getY(), getZ(), Helpers.triangle(level.random), 1, Helpers.triangle(level.random));
            }
        }
    }


    @Override
    protected void onHit(HitResult hit)
    {
        super.onHit(hit);
        if (!this.level.isClientSide)
        {
            spread(level, blockPosition());
            this.level.broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    private void spread(Level level, BlockPos origin)
    {
        for (BlockPos pos : FLHelpers.allPositionsCentered(origin, 3, 2))
        {
            if (random.nextBoolean() && EnvironmentHelpers.isWorldgenReplaceable(level.getBlockState(pos)) && Helpers.isBlock(level.getBlockState(pos.below()), TFCTags.Blocks.GRASS_PLANTABLE_ON))
            {
                level.setBlockAndUpdate(pos, FLBlocks.BUTTERFLY_GRASS.get().defaultBlockState().setValue(MutatingPlantBlock.MATURE, false));
            }
        }
    }
}
