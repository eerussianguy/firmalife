package com.eerussianguy.firmalife.client.render;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import com.eerussianguy.firmalife.common.blockentities.DryingMatBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;

public record DryingMatBlockEntityRenderer(float offset) implements BlockEntityRenderer<DryingMatBlockEntity>
{

    @Override
    public void render(DryingMatBlockEntity mat, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (mat.getLevel() == null)
            return;
        final double magic = 0.003125D;
        poseStack.pushPose();
        poseStack.translate(0.5f, offset + magic, 0.5f);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));

        ItemStack stack = mat.viewStack();
        if (!stack.isEmpty())
        {
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffer, mat.getLevel(), 0);
        }
        poseStack.popPose();
    }

    @Override
    public int getViewDistance()
    {
        return 16;
    }
}
