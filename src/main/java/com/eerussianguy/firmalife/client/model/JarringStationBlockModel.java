package com.eerussianguy.firmalife.client.model;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.JarringStationBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.model.data.ModelData;

import net.dries007.tfc.client.render.blockentity.PlacedItemBlockEntityRenderer;

public class JarringStationBlockModel extends SimpleDynamicBlockModel<JarringStationBlockEntity>
{
    private static final int[][] OFFSETS = {
        {1, 1},
        {1, 0},
        {0, 1},
        {-1, 0},
        {0, 0},
        {0, -1},
        {-1, -1},
        {-1, 1},
        {1, -1}
    };

    public JarringStationBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    protected void render(JarringStationBlockEntity station, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        if (station.getLevel() == null)
            return;
        final var inv = station.getInventory();
        for (int i = 0; i < JarringStationBlockEntity.SLOTS; i++)
        {
            final int[] offset = OFFSETS[i];
            poseStack.pushPose();
            poseStack.translate(0.33f, 1f / 16, 0.33f);
            final float translate = 0.28f;
            poseStack.translate(offset[0] * translate, 0, offset[1] * translate);
            poseStack.scale(0.8f, 0.8f, 0.8f);
            final ItemStack item = inv.getStackInSlot(i);
            final Minecraft mc = Minecraft.getInstance();
            if (PlacedItemBlockEntityRenderer.MODELS.containsKey(item.getItem()))
            {
                final var provider = PlacedItemBlockEntityRenderer.MODELS.get(item.getItem());
                final BakedModel baked = mc.getModelManager().getModel(provider.model());
                final ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
                modelRenderer.tesselateWithAO(station.getLevel(), baked, station.getBlockState(), station.getBlockPos(), poseStack, buffer, false, RandomSource.create(), 4L, packedOverlay, ModelData.EMPTY, provider.renderType());
            }
            poseStack.popPose();
        }
    }

    @Override
    protected BlockEntityType<JarringStationBlockEntity> type()
    {
        return FLBlockEntities.JARRING_STATION.get();
    }
}
