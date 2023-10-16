package com.eerussianguy.firmalife.client.model;

import java.util.function.Function;
import com.eerussianguy.firmalife.common.blockentities.BonsaiPlanterBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
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

import net.dries007.tfc.client.RenderHelpers;

public class BonsaiPlanterBlockModel extends SimpleDynamicBlockModel<BonsaiPlanterBlockEntity>
{
    private static final int FRUITING_ID = 0;
    private static final int DRY_ID = 1;
    private static final int FLOWERING_ID = 2;
    private static final int BRANCH_ID = 3;
    private static final int LEAVES_ID = 4;

    public BonsaiPlanterBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    public void render(BonsaiPlanterBlockEntity planter, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        final Plantable plant = planter.getPlantable(0);
        if (plant == null) return;

        final boolean water = planter.getWater() > 0;
        final float growth = planter.getGrowth(0);

        final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
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

        final TextureAtlasSprite branch = atlas.apply(plant.getTexture(BRANCH_ID));
        final TextureAtlasSprite leaves = atlas.apply(plant.getTexture(id));

        poseStack.pushPose();

        // branches
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, branch, packedLight, packedOverlay, 7 / 16f, 7 / 16f, 7 / 16f, 9 / 16f, 18 / 16f, 9 / 16f);
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, branch, packedLight, packedOverlay, 7 / 16f, 12 / 16f, 4 / 16f, 9 / 16f, 14 / 16f, 7 / 16f);
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, branch, packedLight, packedOverlay, 5 / 16f, 14 / 16f, 8 / 16f, 7 / 16f, 16 / 16f, 12 / 16f);

        // leaves
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, leaves, packedLight, packedOverlay, 4 / 16f, 12 / 16f, 10 / 16f, 10 / 16f, 19f / 16f, 16 / 16f);
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, leaves, packedLight, packedOverlay, 3 / 16f, 11 / 16f, 5 / 16f, 11 / 16f, 21 / 16f, 11 / 16f);
        RenderHelpers.renderTexturedCuboid(poseStack, buffer, leaves, packedLight, packedOverlay, 4 / 16f, 10 / 16f, 1 / 16f, 12 / 16f, 17 / 16f, 7 / 16f);

        poseStack.popPose();
    }

    @Override
    public BlockEntityType<BonsaiPlanterBlockEntity> type()
    {
        return FLBlockEntities.BONSAI_PLANTER.get();
    }
}
