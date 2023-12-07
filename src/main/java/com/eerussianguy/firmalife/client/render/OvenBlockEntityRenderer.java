package com.eerussianguy.firmalife.client.render;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import com.eerussianguy.firmalife.common.blockentities.OvenTopBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.capabilities.Capabilities;

public class OvenBlockEntityRenderer implements BlockEntityRenderer<OvenTopBlockEntity>
{
    @Override
    public void render(OvenTopBlockEntity oven, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        if (oven.getLevel() == null)
            return;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        oven.getCapability(Capabilities.ITEM).ifPresent(cap -> {
            float timeD = RenderHelpers.itemTimeRotation();
            poseStack.translate(0.25D, 0.25D, 0.25D);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            for (int i = 0; i < cap.getSlots(); i++)
            {
                ItemStack stack = cap.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                poseStack.pushPose();
                poseStack.translate((i % 2 == 0 ? 1 : 0), 0, (i < 2 ? 1 : 0));
                poseStack.mulPose(Axis.YP.rotationDegrees(timeD));
                itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, poseStack, buffer, oven.getLevel(), 0);
                poseStack.popPose();
            }
        });
    }

    @Override
    public int getViewDistance()
    {
        return 16;
    }
}
