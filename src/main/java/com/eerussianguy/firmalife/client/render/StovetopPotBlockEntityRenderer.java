package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.blockentities.StovetopPotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.fluids.TFCFluids;

public class StovetopPotBlockEntityRenderer implements BlockEntityRenderer<StovetopPotBlockEntity>
{
    @Override
    public void render(StovetopPotBlockEntity pot, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (pot.getLevel() == null) return;

        FluidStack fluidStack = pot.getCapability(Capabilities.FLUID).map(cap -> cap.getFluidInTank(0)).orElse(FluidStack.EMPTY);
        if (pot.hasOutput())
        {
            fluidStack = new FluidStack(Fluids.WATER, 1000);
        }
        if (!fluidStack.isEmpty())
        {
            final int color = pot.hasOutput() ? (TFCFluids.ALPHA_MASK | 0xA64214) : RenderHelpers.getFluidColor(fluidStack);
            RenderHelpers.renderFluidFace(poseStack, fluidStack, buffer, color, 0.3125F, 0.3125F, 0.6875F, 0.6875F, 5f / 16, combinedOverlay, combinedLight);
        }

        pot.getCapability(Capabilities.ITEM).ifPresent(cap -> {
            int ordinal = 0;
            for (int slot = 0; slot < StovetopPotBlockEntity.SLOTS; slot++)
            {
                ItemStack item = cap.getStackInSlot(slot);
                if (!item.isEmpty())
                {
                    float yOffset = 1f / 16;
                    poseStack.pushPose();
                    poseStack.translate(0.5, 0.003125D + yOffset, 0.5);
                    poseStack.scale(0.3f, 0.3f, 0.3f);
                    poseStack.mulPose(RenderHelpers.rotateDegreesX(90F));
                    poseStack.mulPose(RenderHelpers.rotateDegreesZ(180F));

                    ordinal++;
                    poseStack.translate(0, 0, -0.12F * ordinal);

                    Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, 0);
                    poseStack.popPose();
                }
            }
        });
    }
}
