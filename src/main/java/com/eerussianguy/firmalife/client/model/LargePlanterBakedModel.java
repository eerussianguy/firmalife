package com.eerussianguy.firmalife.client.model;

import java.util.function.Function;
import com.eerussianguy.firmalife.client.render.RenderUtils;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.LargePlanterBlockEntity;
import com.eerussianguy.firmalife.common.util.Plantable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;


public class LargePlanterBakedModel extends SimpleDynamicBlockModel<LargePlanterBlockEntity>
{
    public LargePlanterBakedModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    public void render(LargePlanterBlockEntity planter, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        final Plantable plant = planter.getPlantable(0);
        if (plant == null)
            return;

        final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        final TextureAtlasSprite sprite = atlas.apply(plant.getTexture(planter.getGrowth(0)));

        poseStack.pushPose();
        RenderUtils.renderCross(0.125f, 0.875f, 0.3125f, 1.0625f, poseStack, buffer, packedLight, packedOverlay, sprite, 0f, 0f, 1f, 1f);
        poseStack.popPose();
    }

    @Override
    public BlockEntityType<LargePlanterBlockEntity> type()
    {
        return FLBlockEntities.LARGE_PLANTER.get();
    }

}
