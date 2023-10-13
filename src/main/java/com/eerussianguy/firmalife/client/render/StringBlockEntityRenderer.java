package com.eerussianguy.firmalife.client.render;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.blockentities.StringBlockEntity;
import com.eerussianguy.firmalife.common.blocks.StringBlock;
import com.mojang.blaze3d.vertex.PoseStack;

public class StringBlockEntityRenderer implements BlockEntityRenderer<StringBlockEntity>
{
    @Override
    public void render(StringBlockEntity string, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        Level level = string.getLevel();
        if (level == null) return;
        BlockState state = level.getBlockState(string.getBlockPos());
        if (!(state.getBlock() instanceof StringBlock)) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.38D, 0.5D);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        if (state.getValue(StringBlock.AXIS) == Direction.Axis.Z)
        {
            poseStack.mulPose(Axis.YP.rotationDegrees(90f));
        }
        ItemStack item = string.viewStack();
        if (!item.isEmpty())
        {
            Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffers, string.getLevel(), 0);
        }
        poseStack.popPose();
    }
}
