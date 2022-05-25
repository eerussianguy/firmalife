package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.greenhouse.LargePlanterBlock;
import com.eerussianguy.firmalife.common.util.Plantable;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import org.jetbrains.annotations.Nullable;

public class LargePlanterBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements ICalendarTickable
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, LargePlanterBlockEntity planter)
    {
        planter.checkForCalendarUpdate();
    }

    public static final Component NAME = FLHelpers.blockEntityName("large_planter");

    @Nullable
    private Plantable cachedPlant;
    private long lastPlayerTick;
    private boolean climateValid;

    public LargePlanterBlockEntity(BlockPos pos, BlockState state)
    {
        this(FLBlockEntities.LARGE_PLANTER.get(), pos, state, defaultInventory(1), NAME);
    }

    public LargePlanterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<ItemStackHandler> inventoryFactory, Component defaultName)
    {
        super(type, pos, state, inventoryFactory, defaultName);
        cachedPlant = null;
        lastPlayerTick = Calendars.SERVER.getTicks();
        climateValid = false;
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        BlockState state = level.getBlockState(worldPosition);
        if (state.getBlock() instanceof LargePlanterBlock)
        {
            if (!level.isClientSide)
            {

            }
        }

    }



    protected void postGrowthTick(Level level, BlockPos pos, BlockState state, Plantable plantable, int slot)
    {

    }

    @Override
    public long getLastUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    public void setLastUpdateTick(long ticks)
    {
        lastPlayerTick = ticks;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        lastPlayerTick = nbt.getLong("lastPlayerTick");
        climateValid = nbt.getBoolean("climateValid");

        updateCache();
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putLong("lastPlayerTick", lastPlayerTick);
        nbt.putBoolean("climateValid", climateValid);
    }

    protected void updateCache()
    {
        cachedPlant = Plantable.get(inventory.getStackInSlot(0));
    }

    protected boolean checkValid()
    {
        assert level != null;
        return climateValid && level.getBrightness(LightLayer.SKY, worldPosition.above()) >= level.getMaxLightLevel() - 5;
    }

}
