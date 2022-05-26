package com.eerussianguy.firmalife.common.blocks.greenhouse;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.IHighlightHandler;
import net.dries007.tfc.common.blocks.ExtendedProperties;

public class QuadPlanterBlock extends LargePlanterBlock implements IHighlightHandler
{
    private static final VoxelShape SMALL_SHAPE = box(0, 0, 0, 16, 6, 16);
    private static final VoxelShape[] HITBOXES = new VoxelShape[] {
        box(0, 0, 0, 8, 6, 8),// < <
        box(8, 0, 8, 16, 6, 16),// > >
        box(8, 0, 0, 16, 6, 8),// > <
        box(0, 0, 8, 8, 6, 16) // < >
    };

    public QuadPlanterBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public PlanterType getPlanterType()
    {
        return PlanterType.QUAD;
    }

    @Override
    protected int getUseSlot(BlockHitResult hit, BlockPos pos)
    {
        final Vec3 trace = hit.getLocation().add(-pos.getX(), -pos.getY(), -pos.getZ());
        return getSlotForHit(trace.x, trace.z);
    }

    @Override
    public boolean drawHighlight(Level level, BlockPos pos, Player player, BlockHitResult hit, PoseStack poseStack, MultiBufferSource buffer, Vec3 renderPos)
    {
        Vec3 trace = hit.getLocation().add(-pos.getX(), -pos.getY(), -pos.getZ());
        VoxelShape shape = HITBOXES[getSlotForHit(trace.x, trace.z)];
        IHighlightHandler.drawBox(poseStack, shape, buffer, pos, renderPos, 1f, 0f, 0f, 0.4f);
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SMALL_SHAPE;
    }

    private int getSlotForHit(double hitX, double hitZ)
    {
        if (hitX > 0.5 && hitZ > 0.5)
        {
            return 1;
        }
        else if (hitX > 0.5 && hitZ < 0.5)
        {
            return 2;
        }
        else if (hitX < 0.5 && hitZ > 0.5)
        {
            return 3;
        }
        else
        {
            return 0;
        }
    }
}
