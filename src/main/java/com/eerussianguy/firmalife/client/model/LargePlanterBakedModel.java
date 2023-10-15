package com.eerussianguy.firmalife.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import com.eerussianguy.firmalife.client.render.RenderUtils;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.LargePlanterBlockEntity;
import com.eerussianguy.firmalife.common.util.Plantable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jetbrains.annotations.NotNull;

public class LargePlanterBakedModel extends DynamicBlockModel.Baked
{
    public LargePlanterBakedModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData)
    {
        final BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null && blockEntity.getType() == FLBlockEntities.LARGE_PLANTER.get())
        {
            return modelData.derive()
                .with(DynamicBlockModel.StaticModelData.PROPERTY, render(level, pos, (LargePlanterBlockEntity) blockEntity))
                .build();
        }
        return modelData;
    }

    private DynamicBlockModel.StaticModelData render(BlockAndTintGetter level, BlockPos pos, LargePlanterBlockEntity planter)
    {
        final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
        final Plantable plant = planter.getPlantable(0);
        if (plant == null)
            return DynamicBlockModel.StaticModelData.EMPTY;

        final TextureAtlasSprite sprite = atlas.apply(plant.getTexture(planter.getGrowth(0)));
        final int packedLight = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));
        final int packedOverlay = OverlayTexture.NO_OVERLAY;
        final List<BakedQuad> quads = new ArrayList<>(24);
        final VertexConsumer buffer = new QuadBakingVertexConsumer(quads::add);
        final PoseStack poseStack = new PoseStack();

        poseStack.pushPose();

        RenderUtils.renderCross(0.125f, 0.875f, 0.3125f, 1.0625f, poseStack, buffer, packedLight, packedOverlay, sprite, 0f, 0f, 1f, 1f);

        poseStack.popPose();

        return new DynamicBlockModel.StaticModelData(quads);
    }

}
