package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.blockentities.NutritiveBasinBlockEntity;
import com.eerussianguy.firmalife.common.blocks.greenhouse.NutritiveBasinBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.model.data.EmptyModelData;

import net.dries007.tfc.client.RenderHelpers;

import static net.dries007.tfc.client.RenderHelpers.*;

public class NutritiveBasinBlockEntityRenderer implements BlockEntityRenderer<NutritiveBasinBlockEntity>
{
    @Override
    public void render(NutritiveBasinBlockEntity basin, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        final BlockState state = basin.getBlockState();
        if (state.getValue(NutritiveBasinBlock.WATERED) && basin.getLevel() != null)
        {
            poseStack.pushPose();
            poseStack.scale(0.9f, 0.95f, 0.9f);
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(Blocks.WATER.defaultBlockState(), poseStack, buffers, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
            poseStack.popPose();

//            final ResourceLocation texture = Fluids.WATER.getAttributes().getStillTexture();
//            final TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(BLOCKS_ATLAS).apply(texture);
//            final VertexConsumer builder = buffers.getBuffer(RenderType.entityTranslucentCull(BLOCKS_ATLAS));
//            RenderHelpers.renderTexturedCuboid(poseStack, builder, sprite, combinedLight, combinedOverlay, 2f / 16, 2f / 16, 2f / 16, 14f / 16, 1, 14f / 16);
        }
    }
}
