package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.blockentities.StovetopGrillBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.capabilities.Capabilities;

public class StovetopGrillBlockEntityRenderer implements BlockEntityRenderer<StovetopGrillBlockEntity>
{
    @Override
    public void render(StovetopGrillBlockEntity grill, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        grill.getCapability(Capabilities.ITEM).ifPresent(cap -> {
            for (int i = 0; i < StovetopGrillBlockEntity.SLOTS; i++)
            {
                ItemStack item = cap.getStackInSlot(i);
                if (!item.isEmpty())
                {
                    float yOffset = 1f / 16;
                    poseStack.pushPose();
                    poseStack.translate(0.3, 0.003125D + yOffset, 0.28);
                    poseStack.scale(0.3f, 0.3f, 0.3f);
                    poseStack.mulPose(RenderHelpers.rotateDegreesX(90F));
                    poseStack.mulPose(RenderHelpers.rotateDegreesZ(180F));

                    float translateAmount = -1.4F;
                    if (i == 1 || i == 3)
                    {
                        poseStack.translate(translateAmount, 0, 0);
                    }
                    if (i == 2 || i == 3)
                    {
                        poseStack.translate(0, translateAmount, 0);
                    }

                    Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
                    poseStack.popPose();
                }
            }
        });
    }
}
