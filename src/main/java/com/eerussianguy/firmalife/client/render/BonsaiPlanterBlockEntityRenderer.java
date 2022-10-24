package com.eerussianguy.firmalife.client.render;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import com.eerussianguy.firmalife.common.blockentities.LargePlanterBlockEntity;
import com.eerussianguy.firmalife.common.util.Plantable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dries007.tfc.client.RenderHelpers;

public class BonsaiPlanterBlockEntityRenderer implements BlockEntityRenderer<LargePlanterBlockEntity>
{
    @Override
    public void render(LargePlanterBlockEntity planter, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        Plantable plant = planter.getPlantable(0);
        if (plant == null) return;

        final boolean water = planter.getWater() > 0;
        final float growth = planter.getGrowth(0);

        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        String baseTex = plant.getTextureLocation();
        String leafTex = "_leaves";
        if (growth >= 1)
        {
            leafTex = "_fruiting_leaves";
        }
        else if (!water)
        {
            leafTex = "_dry_leaves";
        }
        else if (growth > 0.66f)
        {
            leafTex = "_flowering_leaves";
        }

        TextureAtlasSprite branch = atlas.apply(new ResourceLocation(baseTex + "_branch"));
        TextureAtlasSprite leaves = atlas.apply(new ResourceLocation(baseTex + leafTex));

        poseStack.pushPose();
        VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());

        // branches
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, branch, combinedLight, combinedOverlay, 7 / 16f, 7 / 16f, 7 / 16f, 9 / 16f, 18 / 16f, 9 / 16f);
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, branch, combinedLight, combinedOverlay, 7 / 16f, 12 / 16f, 4 / 16f, 9 / 16f, 14 / 16f, 7 / 16f);
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, branch, combinedLight, combinedOverlay, 5 / 16f, 14 / 16f, 8 / 16f, 7 / 16f, 16 / 16f, 12 / 16f);

        // leaves
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, leaves, combinedLight, combinedOverlay, 4 / 16f, 12 / 16f, 10 / 16f, 10 / 16f, 19f / 16f, 16 / 16f);
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, leaves, combinedLight, combinedOverlay, 3 / 16f, 11 / 16f, 5 / 16f, 11 / 16f, 21 / 16f, 11 / 16f);
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, leaves, combinedLight, combinedOverlay, 4 / 16f, 10 / 16f, 1 / 16f, 12 / 16f, 17 / 16f, 7 / 16f);

        poseStack.popPose();
    }
}
