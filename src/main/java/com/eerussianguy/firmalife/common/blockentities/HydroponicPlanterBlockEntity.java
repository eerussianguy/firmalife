package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class HydroponicPlanterBlockEntity extends QuadPlanterBlockEntity
{
    public static void hydroponicServerTick(Level level, BlockPos pos, BlockState state, HydroponicPlanterBlockEntity planter)
    {
        planter.checkForCalendarUpdate();
        planter.checkForLastTickSync();
        if (level.getGameTime() % (80 + (pos.getZ() % 4)) == 0 && planter.isClimateValid())
        {
            final Fluid fluid = SprinklerBlockEntity.searchForFluid(level, pos, Direction.DOWN);
            final boolean valid = fluid != null;
            if (valid != planter.hasPipe)
            {
                planter.hasPipe = valid;
                planter.markForSync();
            }
        }
    }

    private boolean hasPipe = false;

    public HydroponicPlanterBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.HYDROPONIC_PLANTER.get(), pos, state, FLHelpers.blockEntityName("hydroponic_planter"));
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        hasPipe = nbt.getBoolean("hasPipe");
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putBoolean("hasPipe", hasPipe);
    }

    @Override
    @Nullable
    public Component getInvalidReason()
    {
        if (!hasPipe())
        {
            return Component.translatable("firmalife.greenhouse.needs_pipe");
        }
        return super.getInvalidReason();
    }

    @Override
    public float getWater()
    {
        assert level != null;
        return hasPipe() ? 1f : 0f;
    }

    public boolean hasPipe()
    {
        assert level != null;
        return hasPipe;
    }

    @Override
    public boolean addWater(float amount, @Nullable Direction direction)
    {
        return false;
    }

    @Override
    public void drainWater(float amount) { }
}
