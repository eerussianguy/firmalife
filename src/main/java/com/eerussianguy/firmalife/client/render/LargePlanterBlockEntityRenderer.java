package com.eerussianguy.firmalife.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;

import com.eerussianguy.firmalife.common.blockentities.LargePlanterBlockEntity;
import com.eerussianguy.firmalife.common.util.Plantable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class LargePlanterBlockEntityRenderer implements BlockEntityRenderer<LargePlanterBlockEntity>
{
    @Override
    public void render(LargePlanterBlockEntity planter, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        Plantable plant = planter.getPlantable(0);
        if (plant == null) return;

        poseStack.pushPose();
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(plant.getTexture(planter.getGrowth(0)));
        VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());

        RenderUtils.renderCross(0.125f, 0.875f, 0.3125f, 1.0625f, poseStack, buffer, combinedLight, combinedOverlay, sprite, 0f, 0f, 1f, 1f);

        poseStack.popPose();
    }

}
