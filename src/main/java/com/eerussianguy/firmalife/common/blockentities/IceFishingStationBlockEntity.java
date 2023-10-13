package com.eerussianguy.firmalife.common.blockentities;

import java.util.Random;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.IceFishingStationBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.items.TFCFishingRodItem;
import net.dries007.tfc.util.Helpers;

public class IceFishingStationBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, IceFishingStationBlockEntity station)
    {
        station.checkForLastTickSync();

        if (level.getGameTime() % 40 == 0 && state.getBlock() instanceof IceFishingStationBlock)
        {
            final ItemStack bait = TFCFishingRodItem.getBaitItem(station.getRod());
            final TFCFishingRodItem.BaitType type = TFCFishingRodItem.getBaitType(bait);
            if (state.getValue(IceFishingStationBlock.CAST))
            {
                if (type != TFCFishingRodItem.BaitType.SMALL)
                {
                    station.withdraw();
                    return;
                }
                if (station.hooked == null)
                {
                    for (AbstractFish fish : level.getEntitiesOfClass(AbstractFish.class, new AABB(station.getBaitPos(state)).inflate(3)))
                    {
                        if (!Helpers.isEntity(fish, TFCTags.Entities.NEEDS_LARGE_FISHING_BAIT))
                        {
                            station.hooked = fish;
                            station.markForSync();
                            break;
                        }
                    }
                }
            }
        }
        if (station.hooked != null)
        {
            if (station.hooked.isRemoved() || station.hooked.isDeadOrDying())
            {
                station.hooked = null;
                station.markForSync();
            }
            else
            {
                station.hooked.restrictTo(station.getBaitPos(state), 3);
                if (level.getGameTime() % 30 == 0)
                {
                    station.hooked.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1f);
                }
                if (level.random.nextFloat() < 0.05f && level instanceof ServerLevel server)
                {
                    final RandomSource random = level.random;
                    final BlockPos particlePos = pos.relative(state.getValue(IceFishingStationBlock.FACING));
                    Helpers.playSound(level, particlePos, SoundEvents.FISHING_BOBBER_SPLASH);
                    server.sendParticles(ParticleTypes.SPLASH, particlePos.getX() + random.nextFloat(), particlePos.getY() + (random.nextFloat() * 0.1f), particlePos.getZ() + random.nextFloat(), 3, 0, 0, 0, 1);
                }
            }
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, IceFishingStationBlockEntity station)
    {
        if (station.hooked == null)
        {
            if (station.entityId != -1)
            {
                Entity entity = level.getEntity(station.entityId);
                if (entity instanceof AbstractFish fish)
                {
                    station.hooked = fish;
                }
                station.entityId = -1;
            }
        }
        else if (station.hooked.isRemoved() || station.hooked.isDeadOrDying())
        {
            station.hooked = null;
        }
    }

    @Nullable
    private AbstractFish hooked = null;
    private int entityId = -1;

    public IceFishingStationBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.ICE_FISHING_STATION.get(), pos, state, defaultInventory(1), FLHelpers.blockEntityName("ice_fishing_station"));
    }

    public void withdraw()
    {
        assert level != null;
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(IceFishingStationBlock.CAST, false));
        hooked = null;
        markForSync();
    }

    public boolean hasHooked()
    {
        return hooked != null;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        if (nbt.contains("entityId", Tag.TAG_INT))
        {
            entityId = nbt.getInt("entityId");
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        if (hooked != null)
        {
            nbt.putInt("entityId", hooked.getId());
        }
    }

    @Nullable
    public AbstractFish getHooked()
    {
        return hooked;
    }

    public void pullHooked()
    {
        if (hooked != null)
        {
            final BlockPos pullPos = worldPosition.relative(getBlockState().getValue(IceFishingStationBlock.FACING));
            hooked.teleportTo(pullPos.getX() + 0.5, pullPos.getY() + 0.5, pullPos.getZ() + 0.5);
            hooked.kill();
            markForSync();
        }
    }

    public boolean hasBait()
    {
        return TFCFishingRodItem.getBaitType(TFCFishingRodItem.getBaitItem(getRod())) == TFCFishingRodItem.BaitType.SMALL;
    }

    public ItemStack getRod()
    {
        return inventory.getStackInSlot(0);
    }

    public BlockPos getBaitPos(BlockState state)
    {
        return worldPosition.relative(state.getValue(IceFishingStationBlock.FACING)).below(5);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return stack.getItem() instanceof TFCFishingRodItem;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

}
