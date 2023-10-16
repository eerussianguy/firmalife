package com.eerussianguy.firmalife.client.model;

import java.util.function.Function;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.TrellisPlanterBlockEntity;
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

public class TrellisPlanterBlockModel extends SimpleDynamicBlockModel<TrellisPlanterBlockEntity>
{
    private static final int GROWING_ID = 0;
    private static final int DRY_ID = 1;
    private static final int FLOWERING_ID = 2;
    private static final int FRUITING_ID = 3;

    public TrellisPlanterBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    public void render(TrellisPlanterBlockEntity planter, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
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

        TextureAtlasSprite sprite = atlas.apply(plant.getTexture(id));
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, sprite, packedLight, packedOverlay, 0f, 0f, 0f, 1f, 1.01f, 1f);

        poseStack.popPose();
    }

    @Override
    public BlockEntityType<TrellisPlanterBlockEntity> type()
    {
        return FLBlockEntities.TRELLIS_PLANTER.get();
    }
}
