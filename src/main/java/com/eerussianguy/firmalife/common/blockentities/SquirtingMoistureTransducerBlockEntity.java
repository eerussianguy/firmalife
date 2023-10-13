package com.eerussianguy.firmalife.common.blockentities;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.greenhouse.SquirtingMoistureTransducerBlock;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;

public class SquirtingMoistureTransducerBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements ClimateReceiver
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, SquirtingMoistureTransducerBlockEntity transducer)
    {
        transducer.checkForLastTickSync();
        if (level.getGameTime() % 20 == 0 || transducer.cachedMoisture == -1)
        {
            transducer.cachedMoisture = updateMoisture(level, pos, state, transducer);
        }
        if (transducer.climateValid)
        {
            if (level.getGameTime() % WATER_UPDATE_INTERVAL == 0 && level instanceof ServerLevel serverLevel && updateMoisture(level, pos, state, transducer) > getMinPipes(level, pos))
            {
                final double x = pos.getX() + 0.5;
                final double y = pos.getY() + 1.1;
                final double z = pos.getZ() + 0.5;
                final RandomSource random = level.random;
                serverLevel.sendParticles(ParticleTypes.BUBBLE, x + Helpers.triangle(random, 2), y + Helpers.triangle(random, 2) + 2, z + Helpers.triangle(random, 2), 15, 0.0, 0.0, 0.0, 1);
                serverLevel.sendParticles(TFCParticles.STEAM.get(), x, y, z, 15, 0.0, 0.0, 0.0, 1);
                FLHelpers.allPositionsCentered(pos, 4, 4).forEach(checkPos -> {
                    final ClimateReceiver receiver = ClimateReceiver.get(level, pos);
                    if (receiver != null)
                    {
                        receiver.addWater(0.1f);
                    }
                });
                Helpers.playSound(level, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE);
            }
        }
    }

    public static int getMinPipes(Level level, BlockPos pos)
    {
        return Mth.floor(Mth.map(Climate.getRainfall(level, pos), 0f, 500f, 31, 13));
    }

    private static int updateMoisture(Level level, BlockPos pos, BlockState state, SquirtingMoistureTransducerBlockEntity transducer)
    {
        final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        final Block pipe = FLBlocks.EMBEDDED_PIPE.get();
        final BlockState pipeState = pipe.defaultBlockState();
        return transducer.getCapability(Capabilities.ITEM).map(inv -> {
            int found = 0;
            for (int y = -1; y >= -32; y--)
            {
                mutable.setWithOffset(pos, 0, y, 0);
                final BlockState stateAt = level.getBlockState(mutable);
                if (!Helpers.isBlock(stateAt, pipe))
                {
                    if (!level.isClientSide)
                    {
                        ItemStack stack = inv.getStackInSlot(0);
                        if (!stack.isEmpty() && (Helpers.isBlock(stateAt, FLTags.Blocks.PIPE_REPLACEABLE) || stateAt.isAir()))
                        {
                            found++;
                            level.setBlockAndUpdate(mutable, pipeState);
                            inv.extractItem(0, 1, false);
                            transducer.markForSync();
                            Helpers.playSound(level, pos, SoundEvents.METAL_PLACE);
                            return found; // we only will update one
                        }
                    }
                    else
                    {
                        return found;
                    }
                }
                else
                {
                    found++;
                }
            }
            return found;
        }).orElse(0);
    }

    private static final int WATER_UPDATE_INTERVAL = 20 * 60;

    private boolean climateValid = false;
    private int cachedMoisture = -1;

    public SquirtingMoistureTransducerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.SQUIRTING_MOISTURE_TRANSDUCER.get(), pos, state, defaultInventory(1), FLHelpers.blockEntityName("squirting_moisture_transducer"));
    }

    public boolean isClimateValid()
    {
        return climateValid;
    }

    /**
     * for visualization only
     */
    public int getCachedMoisture()
    {
        assert level != null;
        if (cachedMoisture == -1 || level.getGameTime() % 20 == 0)
        {
            cachedMoisture = updateMoisture(level, getBlockPos(), level.getBlockState(getBlockPos()), this);
        }
        return cachedMoisture;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 32;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return stack.getItem() == FLBlocks.EMBEDDED_PIPE.get().asItem();
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        climateValid = nbt.getBoolean("valid");
        cachedMoisture = -1;
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putBoolean("valid", climateValid);
    }

    @Override
    public void setValid(Level level, BlockPos pos, boolean valid, int tier, boolean cellar)
    {
        if (!cellar)
        {
            this.climateValid = valid;
            markForSync();
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof SquirtingMoistureTransducerBlock)
            {
                boolean stasis = state.getValue(SquirtingMoistureTransducerBlock.STASIS);
                if (stasis != valid)
                {
                    level.setBlockAndUpdate(pos, state.setValue(SquirtingMoistureTransducerBlock.STASIS, valid));
                }
            }
        }
    }
}
