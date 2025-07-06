package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.blocks.greenhouse.LargePlanterBlock;
import com.eerussianguy.firmalife.config.FLConfig;
import com.mojang.math.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TickableBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.RotationSinkBlockEntity;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.SinkNode;

public class SweeperBlockEntity extends TickableBlockEntity implements RotationSinkBlockEntity
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, SweeperBlockEntity sweeper)
    {
        sweeper.checkForLastTickSync();

        float angle = sweeper.getRotationAngle(0f) * Constants.RAD_TO_DEG - 90f;

        if (!sweeper.getRotationNode().isConnectedToNetwork())
        {
            if (FLConfig.SERVER.mechanicalPowerCheatMode.get() && level.hasNeighborSignal(pos))
            {
                angle = 360f - ((level.getGameTime() % 80) / 80f * 360f) - 90f;
            }
            else
            {
                return;
            }
        }
        if (angle < 0)
            angle += 360f;

        int x = 0;
        int z = 0;
        if (angle > 0 && angle < 5)
            x = 1;
        else if (angle > 40 && angle < 45)
        {
            x = 1;
            z = -1;
        }
        if (angle > 85 && angle < 90)
            z = -1;
        if (angle > 130 && angle < 135)
        {
            x = -1;
            z = -1;
        }
        if (angle > 175 && angle < 180)
            x = -1;
        if (angle > 220 && angle < 225)
        {
            x = -1;
            z = 1;
        }
        if (angle > 265 && angle < 270)
            z = 1;
        if (angle > 310 && angle < 315)
        {
            x = 1;
            z = 1;
        }

        if (level.getBlockEntity(pos.offset(x, 0, z)) instanceof LargePlanterBlockEntity planter)
        {
            if (planter instanceof QuadPlanterBlockEntity)
                return;
            for (int i = 0; i < planter.slots(); i++)
            {
                LargePlanterBlock.takeSlot(level, planter, i, item -> Helpers.spawnItem(level, pos, item));
            }
        }

    }

    private final Node node;

    public SweeperBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.SWEEPER.get(), pos, state);
        this.node = new SinkNode(pos, Direction.DOWN)
        {
            @Override
            public String toString()
            {
                return "SinkNode[pos=%s]".formatted(pos);
            }
        };
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
