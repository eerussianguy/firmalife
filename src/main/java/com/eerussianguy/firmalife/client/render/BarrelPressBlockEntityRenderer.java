package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.BarrelPressBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;

import net.dries007.tfc.util.Helpers;

public class BarrelPressBlockEntityRenderer implements BlockEntityRenderer<BarrelPressBlockEntity>
{
    public static final ModelResourceLocation PRESS = ModelResourceLocation.standalone(FLHelpers.identifier("block/barrel_press_piston"));

    @Override
    public void render(BarrelPressBlockEntity press, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        if (press.getLevel() == null)
            return;
        poseStack.pushPose();

        float passed = Helpers.easeInOutCubic(Mth.clamp(press.sinceWeLastTouched(partialTick) / BarrelPressBlockEntity.TIME, 0f, 1f));
        if (passed > 0.5f)
            passed = 1f - passed;
        poseStack.translate(0f, -passed, 0f);

        final BakedModel model = Minecraft.getInstance().getModelManager().getModel(PRESS);
        final ModelBlockRenderer mr = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        mr.tesselateWithAO(press.getLevel(), model, press.getBlockState(), press.getBlockPos(), poseStack, buffers.getBuffer(RenderType.cutout()), false, RandomSource.create(), 4L, combinedOverlay, ModelData.EMPTY, RenderType.cutout());

        poseStack.popPose();
    }
}
