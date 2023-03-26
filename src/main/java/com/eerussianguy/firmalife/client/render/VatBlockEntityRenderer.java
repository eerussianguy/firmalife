package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.blockentities.VatBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.capabilities.Capabilities;

public class VatBlockEntityRenderer implements BlockEntityRenderer<VatBlockEntity>
{
    @Override
    public void render(VatBlockEntity vat, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        vat.getCapability(Capabilities.FLUID).ifPresent(cap -> {
            final FluidStack fluid = cap.getFluidInTank(0);
            if (!fluid.isEmpty())
            {
                final float y = Mth.map(fluid.getAmount(), 0f, 10000f, 1f / 16f, 0.5f);
                RenderHelpers.renderFluidFace(poseStack, fluid, buffers, 1f / 8, 1f / 8, 7f / 8, 7f / 8, y, combinedOverlay, combinedLight);
            }
        });

        vat.getCapability(Capabilities.ITEM).ifPresent(inv -> {
            final ItemStack itemStack = inv.getStackInSlot(0);
            if (!itemStack.isEmpty())
            {
                poseStack.pushPose();
                poseStack.translate(0.5, 0.15625, 0.5);
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.mulPose(RenderHelpers.rotateDegreesX(90.0F));
                Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffers, 0);
                poseStack.popPose();
            }
        });
    }
}
