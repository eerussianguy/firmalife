package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.blocks.greenhouse.PumpingStationBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.RotationSinkBlockEntity;
import net.dries007.tfc.common.blocks.rotation.CrankshaftBlock;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.SinkNode;

public class PumpingStationBlockEntity extends TFCBlockEntity implements RotationSinkBlockEntity
{
    private final Node node;

    public PumpingStationBlockEntity(BlockPos pos, BlockState state)
    {
        this(FLBlockEntities.PUMPING_STATION.get(), pos, state);
    }

    public PumpingStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        final Direction dir = state.getValue(PumpingStationBlock.FACING);

        this.node = new SinkNode(pos, dir) {
            @Override
            public String toString()
            {
                return "SinkNode[pos=%s]".formatted(pos);
            }
        };
    }

    public boolean isPumping()
    {
        assert level != null;
        return node.rotation() != null && node.rotation().speed() > 0f && level.getFluidState(worldPosition.below()).getType() == Fluids.WATER;
    }

    @Override
    protected void onLoadAdditional()
    {
        this.performNetworkAction(NetworkAction.ADD);
    }

    @Override
    protected void onUnloadAdditional()
    {
        this.performNetworkAction(NetworkAction.REMOVE);
    }

    @Override
    public Node getRotationNode()
    {
        return node;
    }
}
