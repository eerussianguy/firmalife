package com.eerussianguy.firmalife.common.blocks;

import java.util.Random;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.StovetopGrillBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCDamageSources;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.util.Helpers;

public class StovetopGrillBlock extends BottomSupportedDeviceBlock
{
    public static final VoxelShape SHAPE = box(1, 0, 1, 15, 1, 15);

    public StovetopGrillBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP, SHAPE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (level.getBlockEntity(pos) instanceof StovetopGrillBlockEntity grill && player instanceof ServerPlayer serverPlayer)
        {
            Helpers.openScreen(serverPlayer, grill, pos);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity)
    {
        if (!entity.fireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity) && level.getBlockEntity(pos) instanceof StovetopGrillBlockEntity grill && grill.getTemperature() > 0)
        {
            entity.hurt(TFCDamageSources.GRILL, 1.0F);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random rand)
    {
        FLHelpers.readInventory(level, pos, FLBlockEntities.STOVETOP_GRILL, (grill, cap) -> {
            for (int i = 0; i < StovetopGrillBlockEntity.SLOTS; i++)
            {
                if (!cap.getStackInSlot(i).isEmpty())
                {
                    double x = pos.getX() + 0.5D;
                    double y = pos.getY() + (1f / 16);
                    double z = pos.getZ() + 0.5D;
                    level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.25F, rand.nextFloat() * 0.7F + 0.4F, false);
                    level.addParticle(ParticleTypes.SMOKE, x + rand.nextFloat() / 2 - 0.25, y, z + rand.nextFloat() / 2 - 0.25, 0.0D, 0.1D, 0.0D);
                    break;
                }
            }
        });
    }
}
