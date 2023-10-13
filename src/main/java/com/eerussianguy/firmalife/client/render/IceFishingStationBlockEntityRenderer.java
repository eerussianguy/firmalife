package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.IceFishingStationBlockEntity;
import com.eerussianguy.firmalife.common.blocks.IceFishingStationBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class IceFishingStationBlockEntityRenderer implements BlockEntityRenderer<IceFishingStationBlockEntity>
{
    private static final float RED = 0.1f;
    private static final float GREEN = 0.07f;
    private static final float BLUE = 0.02f;

    @Override
    public void render(IceFishingStationBlockEntity station, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        final ItemStack rod = station.getRod();
        final BlockState state = station.getBlockState();
        if (!rod.isEmpty() && state.getBlock() instanceof IceFishingStationBlock && station.getLevel() != null)
        {
            final Direction facing = state.getValue(IceFishingStationBlock.FACING);
            final int angle = 90 + switch (facing)
                {
                    case SOUTH -> 0;
                    case EAST -> 90;
                    case WEST, DOWN, UP -> 270;
                    case NORTH -> 180;
                };
            poseStack.pushPose();
            poseStack.translate(0.5, 0.4, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            poseStack.scale(1.2f, 1.2f, 1.2f);
            Minecraft.getInstance().getItemRenderer().renderStatic(rod, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffers, station.getLevel(), 0);
            poseStack.popPose();

            if (state.getValue(IceFishingStationBlock.CAST))
            {
                renderFishingLine(station, station.getHooked(), partialTicks, poseStack, buffers, facing);
            }
        }
    }

    private void renderFishingLine(IceFishingStationBlockEntity station, @Nullable AbstractFish fish, float partialTick, PoseStack poseStack, MultiBufferSource buffers, Direction dir)
    {
        poseStack.pushPose();

        final BlockPos hookPos = station.getBlockPos();
        final Vec3 ropePos = FLHelpers.vec3(hookPos).add(0.5, 1.8, 0.5).add(dir.getStepX() * 2, 0, dir.getStepZ() * 2);
        poseStack.translate(ropePos.x, ropePos.y, ropePos.z);
        float dx, dy, dz;
        final VertexConsumer buffer = buffers.getBuffer(RenderType.lineStrip());
        final PoseStack.Pose pose = poseStack.last();
        if (fish != null)
        {
            final Vec3 off = fish.getLeashOffset(partialTick);
            dx = (float) (fish.xo - ropePos.x + off.x);
            dy = (float) (fish.yo - ropePos.y + off.y);
            dz = (float) (fish.zo - ropePos.z + off.z);
        }
        else
        {
            final BlockPos lowerPos = station.getBaitPos(station.getBlockState());
            dx = (float) (lowerPos.getX() + 0.5f - ropePos.x);
            dy = (float) (lowerPos.getY() + 0.5f - ropePos.y);
            dz = (float) (lowerPos.getZ() + 0.5f - ropePos.z);
        }

        for(int k = 0; k <= 16; ++k)
        {
            stringVertex(dx, dy, dz, buffer, pose, fraction(k, 16), fraction(k + 1, 16));
        }

        poseStack.popPose();
    }

    private static float fraction(int num, int denominator)
    {
        return (float)num / (float)denominator;
    }


    private static void stringVertex(float dx, float dy, float dz, VertexConsumer buffer, PoseStack.Pose poseStack, float v1, float v2)
    {
        float x = dx * v1;
        float y = dy * (v1 * v1 + v1) * 0.5F + 0.25F;
        float z = dz * v1;
        float n1 = dx * v2 - x;
        float n2 = dy * (v2 * v2 + v2) * 0.5F + 0.25F - y;
        float n3 = dz * v2 - z;
        float size = Mth.sqrt(n1 * n1 + n2 * n2 + n3 * n3);
        n1 /= size;
        n2 /= size;
        n3 /= size;
        buffer.vertex(poseStack.pose(), x, y, z).color(0, 0, 0, 255).normal(poseStack.normal(), n1, n2, n3).endVertex();
    }

}
