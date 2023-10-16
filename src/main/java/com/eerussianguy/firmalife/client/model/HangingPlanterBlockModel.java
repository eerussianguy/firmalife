package com.eerussianguy.firmalife.client.model;

import java.util.function.Function;
import com.eerussianguy.firmalife.client.render.RenderUtils;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.HangingPlanterBlockEntity;
import com.eerussianguy.firmalife.common.util.Plantable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.dries007.tfc.client.RenderHelpers;

public class HangingPlanterBlockModel extends SimpleDynamicBlockModel<HangingPlanterBlockEntity>
{
    private static final int FRUIT_ID = 0;

    public HangingPlanterBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    public void render(HangingPlanterBlockEntity planter, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        final Plantable plant = planter.getPlantable(0);
        if (plant == null) return;

        poseStack.pushPose();
        final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        final TextureAtlasSprite growth = atlas.apply(plant.getTexture(planter.getGrowth(0)));

        // foliage
        RenderUtils.renderCross(1 / 16f, 15 / 16f, 0 / 16f, 13 / 16f, poseStack, buffer, packedLight, packedOverlay, growth, 1f / 16f, 0f, 15f / 16f, 13f / 16f);

        // fruits
        if (planter.getGrowth(0) >= 1f)
        {
            TextureAtlasSprite fruit = atlas.apply(plant.getSpecialTexture(FRUIT_ID));
            RenderHelpers.renderTexturedCuboid(poseStack, buffer, fruit, packedLight, packedOverlay, 4 / 16f, 3 / 16f, 4 / 16f, 7 / 16f, 6 / 16f, 7 / 16f);
            RenderHelpers.renderTexturedCuboid(poseStack, buffer, fruit, packedLight, packedOverlay, 4 / 16f, (float) 0, 10 / 16f, 7 / 16f, 3 / 16f, 13 / 16f);
            RenderHelpers.renderTexturedCuboid(poseStack, buffer, fruit, packedLight, packedOverlay, 9 / 16f, 7 / 16f, 5 / 16f, 12 / 16f, 10 / 16f, 8 / 16f);
        }

        poseStack.popPose();
    }

    @Override
    public BlockEntityType<HangingPlanterBlockEntity> type()
    {
        return FLBlockEntities.HANGING_PLANTER.get();
    }
}
