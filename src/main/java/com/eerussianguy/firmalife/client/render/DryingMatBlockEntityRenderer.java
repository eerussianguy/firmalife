package com.eerussianguy.firmalife.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.items.CapabilityItemHandler;

import com.eerussianguy.firmalife.common.blockentities.DryingMatBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

public record DryingMatBlockEntityRenderer(float offset) implements BlockEntityRenderer<DryingMatBlockEntity>
{

    @Override
    public void render(DryingMatBlockEntity mat, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        mat.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
            final double magic = 0.003125D;
            poseStack.pushPose();
            poseStack.translate(0.5f, offset + magic, 0.5f);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(Vector3f.XP.rotationDegrees(90f));

            ItemStack stack = cap.getStackInSlot(0);
            if (!stack.isEmpty())
            {
                Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
            }
            poseStack.popPose();
        });
    }
}
