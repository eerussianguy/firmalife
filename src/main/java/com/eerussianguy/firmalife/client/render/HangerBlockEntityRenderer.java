package com.eerussianguy.firmalife.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import com.eerussianguy.firmalife.common.blockentities.HangerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.capabilities.Capabilities;

public class HangerBlockEntityRenderer implements BlockEntityRenderer<HangerBlockEntity>
{
    @Override
    public void render(HangerBlockEntity hanger, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        final ItemStack stack = hanger.getCapability(Capabilities.ITEM).map(cap -> cap.getStackInSlot(0)).orElse(ItemStack.EMPTY);
        if (stack.isEmpty()) return;

        float timeD = RenderHelpers.itemTimeRotation();

        int totalDraws = 4;
        int maxStackSize = Mth.clamp(stack.getItem().getItemStackLimit(stack), 1, 64);
        float filled = (float) stack.getCount() / (float) maxStackSize;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(0.45f, 0.45f, 0.45f);
        int currentDraws = 0;
        poseStack.mulPose(Vector3f.YP.rotationDegrees(timeD));
        poseStack.translate(0, 1f, 0);
        for (int i = 0; i < 4; i++)
        {
            poseStack.translate(0, -1f, 0);
            poseStack.mulPose(Vector3f.YP.rotationDegrees((timeD + 22.5f) % 360));
            if (currentDraws >= filled * totalDraws)
            {
                break;
            }
            else
            {
                currentDraws += 1;
            }
            itemRenderer.renderStatic(stack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
        }

        poseStack.popPose();
    }
}
