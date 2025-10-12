package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.blockentities.StompingBarrelBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.client.RenderHelpers;

public class StompingBarrelBlockEntityRenderer implements BlockEntityRenderer<StompingBarrelBlockEntity>
{

    @Override
    public void render(StompingBarrelBlockEntity barrel, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        if (barrel.getLevel() == null)
            return;

        final ItemStack stack = barrel.readStack();
        if (stack.isEmpty())
            return;

        final ResourceLocation texture = barrel.getTexture();
        if (texture == null)
            return;

        poseStack.pushPose();

        final boolean smashed = barrel.isOutputMode();
        float y = (1f / 16) + (7f / 16f * stack.getCount() / StompingBarrelBlockEntity.MAX_GRAPES);
        if (smashed)
        {
            y *= 0.5f;
        }
        else
        {
            y *= Mth.lerp(barrel.getStomps() / 16f, 1f, 0.5f);
        }
        RenderHelpers.renderTexturedFace(poseStack, buffer, 0xffffff, 2f / 16, 2f / 16, 14f / 16, 14f / 16, y, overlay, light, texture);

        poseStack.popPose();
    }
}
