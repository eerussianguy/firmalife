package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.SweeperBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.client.model.data.ModelData;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.render.blockentity.AxleBlockEntityRenderer;

public class SweeperBlockEntityRenderer implements BlockEntityRenderer<SweeperBlockEntity>
{
    public static final ResourceLocation ARM = FLHelpers.identifier("block/sweeper_arm");
    public static final ResourceLocation STEEL = FLHelpers.identifier("block/metal/smooth/stainless_steel");

    @Override
    public void render(SweeperBlockEntity sweeper, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        if (sweeper.getLevel() == null)
            return;
        final Minecraft mc = Minecraft.getInstance();
        if (mc == null)
            return;
        final ModelBlockRenderer modelRenderer = mc.getBlockRenderer().getModelRenderer();
        final BakedModel baked = mc.getModelManager().getModel(ARM);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotation(sweeper.getRotationAngle(partialTicks)));

        modelRenderer.tesselateWithAO(sweeper.getLevel(), baked, sweeper.getBlockState(), sweeper.getBlockPos(), poseStack, buffers.getBuffer(RenderType.cutout()), false, RandomSource.create(), 4L, combinedOverlay, ModelData.EMPTY, RenderType.cutout());

        poseStack.translate(-0.5f, -0.5f, -0.5f);
        poseStack.popPose();

        poseStack.pushPose();
        AxleBlockEntityRenderer.applyRotation(poseStack, Direction.Axis.Y, -sweeper.getRotationAngle(partialTicks));
        RenderHelpers.renderTexturedCuboid(poseStack, buffers.getBuffer(RenderType.cutout()), mc.getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(STEEL), combinedLight, combinedOverlay, 0.375F, 0.375F, 0.0F, 0.625F, 0.625F, 1.0F);
        poseStack.popPose();
    }
}
