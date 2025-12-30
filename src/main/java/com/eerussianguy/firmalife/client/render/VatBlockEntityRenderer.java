package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.VatBlockEntity;
import com.eerussianguy.firmalife.common.blocks.oven.VatBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.client.RenderHelpers;

public class VatBlockEntityRenderer implements BlockEntityRenderer<VatBlockEntity>
{
    @Override
    public void render(VatBlockEntity vat, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        if (!(vat.getBlockState().getBlock() instanceof VatBlock))
            return;
        if (vat.getBlockState().getValue(VatBlock.SEALED))
            return;

        final ResourceLocation jarTexture = vat.hasOutput() ? vat.getJarTexture() : null;
        if (jarTexture != null)
        {
            final float y = Mth.clampedMap(vat.getOutput().getCount(), 1, 8,  2f / 16, 12f / 16);
            RenderHelpers.renderTexturedFace(poseStack, buffers, 0xFFFFFF, 1f / 8, 1f / 8, 7f / 8, 7f / 8, y, combinedOverlay, combinedLight, jarTexture);
        }
        else
        {
            final FluidStack fluid = FLHelpers.getFluidInTank(vat);
            if (!fluid.isEmpty())
            {
                final float y = Mth.map(fluid.getAmount(), 0f, 10000f, 1f / 16f, 0.5f);
                RenderHelpers.renderFluidFace(poseStack, fluid, buffers, 1f / 8, 1f / 8, 7f / 8, 7f / 8, y, combinedOverlay, combinedLight);
            }
        }

        final var inv = vat.getInventory();
        final ItemStack itemStack = inv.getStackInSlot(0);
        if (!itemStack.isEmpty())
        {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.15625, 0.5);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            Minecraft.getInstance().getItemRenderer().renderStatic(itemStack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffers, vat.getLevel(), 0);
            poseStack.popPose();
        }
    }
}
