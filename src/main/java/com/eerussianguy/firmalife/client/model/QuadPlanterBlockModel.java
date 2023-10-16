package com.eerussianguy.firmalife.client.model;

import com.eerussianguy.firmalife.client.render.RenderUtils;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.QuadPlanterBlockEntity;
import com.eerussianguy.firmalife.common.util.Plantable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class QuadPlanterBlockModel<T extends QuadPlanterBlockEntity> extends SimpleDynamicBlockModel<T>
{
    public QuadPlanterBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    public void render(QuadPlanterBlockEntity planter, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        for (int i = 0; i < 4; i++)
        {
            Plantable plant = planter.getPlantable(i);
            if (plant == null) continue;

            poseStack.pushPose();
            TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(plant.getTexture(planter.getGrowth(i)));

            if (i == 1 || i == 2)
            {
                poseStack.translate(0.5f, 0f, 0f);
            }
            if (i == 1 || i == 3)
            {
                poseStack.translate(0f, 0f, 0.5f);
            }

            RenderUtils.renderCross(0.125f, 0.4375f, 0.3125f, 0.7291f, poseStack, buffer, packedLight, packedOverlay, sprite, 0.125f, 0f, 0.875f, 1f);

            poseStack.popPose();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public BlockEntityType<T> type()
    {
        return (BlockEntityType<T>) FLBlockEntities.QUAD_PLANTER.get();
    }
}
