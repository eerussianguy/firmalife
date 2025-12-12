package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.client.FLClientHelpers;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.PickerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;

import net.dries007.tfc.client.RenderHelpers;

public class PickerBlockEntityRenderer implements BlockEntityRenderer<PickerBlockEntity>
{
    public static final ModelResourceLocation ARMS = FLClientHelpers.mrl("block/picker_arms");
    public static final ResourceLocation STEEL = FLHelpers.identifier("block/vat");

    @Override
    public void render(PickerBlockEntity picker, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        if (picker.getLevel() == null)
            return;
        final Minecraft mc = Minecraft.getInstance();
        final ModelBlockRenderer modelRenderer = mc.getBlockRenderer().getModelRenderer();
        final BakedModel baked = mc.getModelManager().getModel(ARMS);
        final float ext = picker.getExtensionLength();
        final VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());

        poseStack.pushPose();
        poseStack.translate(0f, -ext, 0f);
        modelRenderer.tesselateWithAO(picker.getLevel(), baked, picker.getBlockState(), picker.getBlockPos(), poseStack, buffer, false, RandomSource.create(), 4L, combinedOverlay, ModelData.EMPTY, RenderType.cutout());
        poseStack.popPose();

        if (ext > 0)
        {
            final TextureAtlasSprite sprite = mc.getTextureAtlas(RenderHelpers.BLOCKS_ATLAS).apply(STEEL);

            for (int x = 0; x <= 1; x++)
            {
                for (int z = 0; z <= 1; z++)
                {
                    poseStack.pushPose();
                    poseStack.translate(0f, 0.99f - ext, 0f);
                    RenderHelpers.renderTexturedCuboid(poseStack, buffer, sprite, combinedLight, combinedOverlay, (2.5f + x * 9) / 16f, 0f, (2.5f + z * 9) / 16f, (4.5f + x * 9) / 16f, ext, (4.5f + z * 9) / 16f);
                    poseStack.popPose();
                }
            }
        }

    }

}
