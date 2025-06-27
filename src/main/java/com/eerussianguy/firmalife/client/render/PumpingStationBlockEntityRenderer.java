package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.blockentities.PumpingStationBlockEntity;
import com.eerussianguy.firmalife.common.blocks.greenhouse.PumpingStationBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.render.blockentity.AxleBlockEntityRenderer;
import net.dries007.tfc.common.blocks.rotation.ConnectedAxleBlock;

public class PumpingStationBlockEntityRenderer implements BlockEntityRenderer<PumpingStationBlockEntity>
{
    @Override
    public void render(PumpingStationBlockEntity station, float partialTicks, PoseStack stack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        final Level level = station.getLevel();
        if (level != null)
        {
            final Direction facing = station.getBlockState().getValue(PumpingStationBlock.FACING);
            final BlockState axleState = level.getBlockState(station.getBlockPos().relative(facing));
            if (axleState.getBlock() instanceof ConnectedAxleBlock connected && station.getRotationNode().isConnectedToNetwork())
            {
                TextureAtlasSprite sprite = RenderHelpers.blockTexture(connected.getAxleTextureLocation());
                VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());
                stack.pushPose();
                float angle = -station.getRotationAngle(partialTicks);
                if (facing == Direction.NORTH || facing == Direction.WEST)
                {
                    stack.translate(0.5F, 0.5F, 0.5F);
                    stack.mulPose(Axis.YP.rotationDegrees(180.0F));
                    stack.translate(-0.5F, -0.5F, -0.5F);
                    angle = -angle;
                }
                AxleBlockEntityRenderer.applyRotation(stack, facing.getAxis(), angle);
                RenderHelpers.renderTexturedCuboid(stack, buffer, sprite, combinedLight, combinedOverlay, 0.375F, 0.375F, 0.5F, 0.625F, 0.625F, 1.0f, false);
                stack.popPose();
            }
        }
    }
}
