package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.blockentities.PlateBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.client.RenderHelpers;

public class PlateBlockEntityRenderer implements BlockEntityRenderer<PlateBlockEntity>
{
    @Override
    public void render(PlateBlockEntity plate, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        final double magic = 0.003125D;
        poseStack.pushPose();
        poseStack.translate(0.5f, 1f / 16f + magic, 0.5f);
        poseStack.scale(0.6f, 0.6f, 0.6f);
        poseStack.mulPose(Axis.XP.rotationDegrees(90f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(plate.getRotation()));

        ItemStack stack = plate.viewStack();
        if (!stack.isEmpty())
        {
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffer, plate.getLevel(), 0);
        }
        poseStack.popPose();
    }

    @Override
    public int getViewDistance()
    {
        return 12;
    }
}
