package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.blocks.greenhouse.SprinklerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.FluidTankCallback;
import net.dries007.tfc.common.capabilities.InventoryFluidTank;

public class SprinklerBlockEntity extends TFCBlockEntity implements FluidTankCallback, ClimateReceiver
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, SprinklerBlockEntity sprinkler)
    {
        if (level.getGameTime() % 20 == 0 && sprinkler.valid)
        {
            if (sprinkler.tank.getFluidInTank(0).getAmount() < TANK_CAPACITY)
            {
                final BlockEntity above = level.getBlockEntity(pos.above());
                if (above != null)
                {
                    above.getCapability(Capabilities.FLUID, Direction.DOWN).ifPresent(aboveCap -> {
                        final int amount = TANK_CAPACITY - sprinkler.tank.getFluidInTank(0).getAmount();
                        if (amount > 0)
                        {
                            sprinkler.tank.fill(aboveCap.drain(amount, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                        }
                    });
                }
            }

            if (level.getGameTime() % 40 == 0 && state.getBlock() instanceof SprinklerBlock block)
            {
                sprinkler.getCapability(Capabilities.FLUID, Direction.UP).ifPresent(cap -> {
                    for (BlockPos testPos : block.getPathMaker().apply(pos))
                    {
                        final ClimateReceiver receiver = ClimateReceiver.get(level, testPos);
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
    }

    public static final int TANK_CAPACITY = 1000;

    private FluidTank tank;
    private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> this.tank);
    private boolean valid = false;

    public SprinklerBlockEntity(BlockPos pos, BlockState state)
    {
        this(FLBlockEntities.SPRINKLER.get(), pos, state);
    }

    public SprinklerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.tank = new InventoryFluidTank(TANK_CAPACITY, f -> Fluids.WATER.getSource().isSame(f.getFluid()), this);
    }

    public boolean isActive()
    {
        return valid && tank.getFluidInTank(0).getAmount() > 0;
    }

    @Override
    public void fluidTankChanged()
    {
        markForSync();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
    {
        if ((side == Direction.UP || side == null) && cap == Capabilities.FLUID)
        {
            return holder.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void loadAdditional(CompoundTag tag)
    {
        this.tank.readFromNBT(tag.getCompound("tank"));
        this.valid = tag.getBoolean("valid");
        super.loadAdditional(tag);
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        tag.put("tank", this.tank.writeToNBT(new CompoundTag()));
        tag.putBoolean("valid", valid);
        super.saveAdditional(tag);
    }

    @Override
    public void setValid(Level level, BlockPos pos, boolean valid, int tier, boolean cellar)
    {
        if (valid != this.valid)
        {
            markForSync();
        }
        this.valid = valid;
    }

    public boolean isValid()
    {
        return valid;
    }
}
