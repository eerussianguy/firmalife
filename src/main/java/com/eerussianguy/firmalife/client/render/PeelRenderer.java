package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.client.FLClientHelpers;
import com.eerussianguy.firmalife.client.model.PeelModel;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.client.RenderHelpers;

public class PeelRenderer extends BlockEntityWithoutLevelRenderer
{
    public static final ResourceLocation TEXTURE = FLHelpers.identifier("textures/entity/peel.png");

    private final EntityModelSet set;
    private PeelModel model;

    public PeelRenderer(BlockEntityRenderDispatcher disp, EntityModelSet set)
    {
        super(disp, set);
        this.set = set;
        this.model = new PeelModel(set.bakeLayer(FLClientHelpers.modelIdentifier("peel")));
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager)
    {
        this.model = new PeelModel(set.bakeLayer(FLClientHelpers.modelIdentifier("peel")));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transform, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        poseStack.pushPose();
        poseStack.translate(0f, -1f, 0f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-45f));
        poseStack.translate(0f, -1f, 0f);
        final VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(buffers, model.renderType(TEXTURE), false, stack.hasFoil());
        this.model.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
