package com.eerussianguy.firmalife.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;

import com.eerussianguy.firmalife.common.blockentities.QuadPlanterBlockEntity;
import com.eerussianguy.firmalife.common.util.Plantable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class QuadPlanterBlockEntityRenderer implements BlockEntityRenderer<QuadPlanterBlockEntity>
{
    @Override
    public void render(QuadPlanterBlockEntity planter, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        for (int i = 0; i < 4; i++)
        {
            Plantable plant = planter.getPlantable(i);
            if (plant == null) continue;

            poseStack.pushPose();
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(plant.getTexture(planter.getGrowth(0)));
            VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());

            if (i == 1 || i == 2)
            {
                poseStack.translate(0.5f, 0f, 0f);
            }
            if (i == 1 || i == 3)
            {
                poseStack.translate(0f, 0f, 0.5f);
            }

            LargePlanterBlockEntityRenderer.renderCross(0.125f, 0.4375f, 0.3125f, 0.7291f, poseStack, buffer, combinedLight, combinedOverlay, sprite, 0.125f, 0f, 0.875f, 1f);

            poseStack.popPose();
        }

    }
}
