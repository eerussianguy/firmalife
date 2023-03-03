package com.eerussianguy.firmalife.client.render;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import com.eerussianguy.firmalife.common.blockentities.TrellisPlanterBlockEntity;
import com.eerussianguy.firmalife.common.util.Plantable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dries007.tfc.client.RenderHelpers;

public class TrellisPlanterBlockEntityRenderer implements BlockEntityRenderer<TrellisPlanterBlockEntity>
{
    private static final int GROWING_ID = 0;
    private static final int DRY_ID = 1;
    private static final int FLOWERING_ID = 2;
    private static final int FRUITING_ID = 3;

    @Override
    public void render(TrellisPlanterBlockEntity planter, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        Plantable plant = planter.getPlantable(0);
        if (plant == null) return;

        final boolean water = planter.getWater() > 0;
        final float growth = planter.getGrowth(0);

        final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        int id = GROWING_ID;
        if (growth >= 1)
        {
            id = FRUITING_ID;
        }
        else if (!water)
        {
            id = DRY_ID;
        }
        else if (growth > 0.66f)
        {
            id = FLOWERING_ID;
        }

        poseStack.pushPose();
        final VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());

        TextureAtlasSprite sprite = atlas.apply(plant.getTexture(id));
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, sprite, combinedLight, combinedOverlay, 0f, 0f, 0f, 1f, 1.01f, 1f);

        poseStack.popPose();
    }
}
