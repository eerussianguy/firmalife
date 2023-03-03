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
    private static final int FRUITING_ID = 0;
    private static final int DRY_ID = 1;
    private static final int FLOWERING_ID = 2;
    private static final int BRANCH_ID = 3;
    private static final int LEAVES_ID = 4;

    @Override
    public void render(LargePlanterBlockEntity planter, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        Plantable plant = planter.getPlantable(0);
        if (plant == null) return;

        final boolean water = planter.getWater() > 0;
        final float growth = planter.getGrowth(0);

        Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        int id = LEAVES_ID;
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

        TextureAtlasSprite branch = atlas.apply(plant.getTexture(BRANCH_ID));
        TextureAtlasSprite leaves = atlas.apply(plant.getTexture(id));

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
