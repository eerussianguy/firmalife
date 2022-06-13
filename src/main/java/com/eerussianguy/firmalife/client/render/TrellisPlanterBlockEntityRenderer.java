package com.eerussianguy.firmalife.client.render;

import java.nio.file.Path;
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

public class TrellisPlanterBlockEntityRenderer implements BlockEntityRenderer<TrellisPlanterBlockEntity>
{
    @Override
    public void render(TrellisPlanterBlockEntity planter, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        Plantable plant = planter.getPlantable(0);
        if (plant == null) return;

        final boolean water = planter.getWater() > 0;
        final float growth = planter.getGrowth(0);

        final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        final String baseTex = plant.getTextureLocation();
        String prefix = "";
        if (growth >= 1)
        {
            prefix = "fruiting_";
        }
        else if (!water)
        {
            prefix = "dry_";
        }
        else if (growth > 0.66f)
        {
            prefix = "flowering_";
        }

        final String[] splits = baseTex.split("block/berry_bush/", 2);
        final ResourceLocation actualTexture = new ResourceLocation(splits[0] + "block/berry_bush/" + prefix + splits[1]);

        poseStack.pushPose();
        final VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());


        TextureAtlasSprite sprite = atlas.apply(actualTexture);
        RenderUtils.renderTexturedCuboid(poseStack, buffer, sprite, combinedLight, combinedOverlay, 0, 0, 0, 1, 1.01f, 1);

        poseStack.popPose();
    }
}
