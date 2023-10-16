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
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData)
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
        final VertexConsumer buffer = new QuadBakingVertexConsumer(quads::add);
        final PoseStack poseStack = new PoseStack();

        render(blockEntity, poseStack, buffer, packedLight, packedOverlay);
        return quads.isEmpty() ? DynamicBlockModel.StaticModelData.EMPTY : new DynamicBlockModel.StaticModelData(quads);
    }

    abstract void render(T blockEntity, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay);

    abstract BlockEntityType<T> type();
}
