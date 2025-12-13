package com.eerussianguy.firmalife.client.model;

import java.util.ArrayList;
import java.util.List;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

/**
 * Convenience impl of the dynamic block model for a known block entity and rendering method... basically a drop in replacement for a BER
 */
public abstract class SimpleDynamicBlockModel<T extends BlockEntity> extends DynamicBlockModel.Baked
{
    public SimpleDynamicBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData)
    {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null && blockEntity.getType() == type())
        {
            return modelData.derive()
                .with(DynamicBlockModel.StaticModelData.PROPERTY, render(level, pos, (T) blockEntity))
                .build();
        }
        return modelData;
    }

    public DynamicBlockModel.StaticModelData render(BlockAndTintGetter level, BlockPos pos, T blockEntity)
    {
        final int packedLight = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));
        final int packedOverlay = OverlayTexture.NO_OVERLAY;
        final List<BakedQuad> quads = new ArrayList<>(24);

        // Copied from TFC, needs to bake every quad manually or rendering will break.
        // See TFC's SimpleStaticBlockEntityModel#render for a better explanation
        class Baker extends QuadBakingVertexConsumer
        {
            int count = 0;

            @Override
            public VertexConsumer addVertex(float x, float y, float z)
            {
                if (count == 4)
                {
                    count = 0;
                    quads.add(bakeQuad());
                }
                count++;
                return super.addVertex(x, y, z);
            }
        }
        final Baker baker = new Baker();
        final PoseStack poseStack = new PoseStack();

        render(blockEntity, poseStack, baker, packedLight, packedOverlay);
        if (baker.count > 0) // We baked at least one quad, so get the last one
        {
            quads.add(baker.bakeQuad());
        }
        return quads.isEmpty() ? DynamicBlockModel.StaticModelData.EMPTY : new DynamicBlockModel.StaticModelData(quads);
    }

    protected abstract void render(T blockEntity, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay);

    protected abstract BlockEntityType<T> type();
}
