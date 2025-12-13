package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.blockentities.StovetopPotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.outputs.PotOutput;

public class StovetopPotBlockEntityRenderer implements BlockEntityRenderer<StovetopPotBlockEntity>
{
    @Override
    public void render(StovetopPotBlockEntity pot, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        if (pot.getLevel() == null) return;

        final float fOffset = 5f / 16;
        final PotOutput output = pot.getOutput();
        if (output != null && output.getRenderTexture() != null)
        {
            RenderHelpers.renderTexturedFace(poseStack, buffer, 0xFFFFFF, 0.3125F, 0.3125F, 0.6875F, 0.6875F, output.getFluidYLevel() - fOffset, combinedOverlay, combinedLight, output.getRenderTexture());
        }
        else
        {
            final boolean useDefaultFluid = output != null && output.getFluidColor() != -1;
            FluidStack fluidStack = pot.getInventory().getFluidInTank(0);
            if (fluidStack.isEmpty() && useDefaultFluid)
            {
                fluidStack = new FluidStack(Fluids.WATER, FluidHelpers.BUCKET_VOLUME);
            }
            if (!fluidStack.isEmpty())
            {
                final int color = useDefaultFluid ? output.getFluidColor() : RenderHelpers.getFluidColor(fluidStack);
                RenderHelpers.renderFluidFace(poseStack, fluidStack, buffer, color, 0.3125F, 0.3125F, 0.6875F, 0.6875F, output == null ? 0.625F - fOffset : output.getFluidYLevel() - fOffset, combinedOverlay, combinedLight);
            }
        }

        final var cap = pot.getInventory();
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
                poseStack.mulPose(Axis.XP.rotationDegrees(90F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(180F));

                ordinal++;
                poseStack.translate(0, 0, -0.12F * ordinal);

                Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffer, pot.getLevel(), 0);
                poseStack.popPose();
            }
        }
    }
}
