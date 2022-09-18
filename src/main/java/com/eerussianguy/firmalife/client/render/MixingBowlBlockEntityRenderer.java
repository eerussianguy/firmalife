package com.eerussianguy.firmalife.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fluids.FluidStack;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.MixingBowlBlockEntity;
import com.eerussianguy.firmalife.common.blocks.MixingBowlBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.capabilities.Capabilities;

public class MixingBowlBlockEntityRenderer implements BlockEntityRenderer<MixingBowlBlockEntity>
{
    public static final ResourceLocation SPOON_LOCATION = FLHelpers.identifier("block/spoon");

    @Override
    public void render(MixingBowlBlockEntity bowl, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        if (bowl.getLevel() != null)
        {
            final BlockState state = bowl.getLevel().getBlockState(bowl.getBlockPos());
            if (state.getBlock() instanceof MixingBowlBlock)
            {
                if (state.getValue(MixingBowlBlock.SPOON))
                {
                    poseStack.pushPose();
                    final Minecraft mc = Minecraft.getInstance();
                    final BakedModel baked = mc.getModelManager().getModel(SPOON_LOCATION);
                    final VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());

                    int rot = bowl.getRotationTimer();
                    if (!bowl.isMixing())
                    {
                        partialTicks = 0;
                        rot = 120;
                    }
                    poseStack.translate(0.5f, 0f, 0.5f);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees((rot - partialTicks) * 4f));
                    poseStack.translate(-0.5f, 0f, -0.5f);
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(22.5f));
                    poseStack.translate(0f, 0.4f, 0f);

                    mc.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffer, null, baked, 1f, 1f, 1f, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);

                    poseStack.popPose();
                }
            }
            poseStack.pushPose();
            bowl.getCapability(Capabilities.FLUID).ifPresent(cap -> {
                FluidStack fluid = cap.getFluidInTank(0);
                if (!fluid.isEmpty())
                {
                    RenderHelpers.renderFluidFace(poseStack, fluid, buffers, RenderHelpers.getFluidColor(fluid), 3f / 16, 3f / 16, 13f / 16, 13f / 16, 5f / 16, combinedOverlay, combinedLight);
                }
            });
            poseStack.popPose();

            poseStack.pushPose();
            bowl.getCapability(Capabilities.ITEM).ifPresent(cap -> {
                int ordinal = 0;
                ItemRenderer render = Minecraft.getInstance().getItemRenderer();
                for (int slot = 0; slot < MixingBowlBlockEntity.SLOTS; slot++)
                {
                    ItemStack item = cap.getStackInSlot(slot);
                    if (!item.isEmpty())
                    {
                        float yOffset = 0.125f;
                        poseStack.pushPose();
                        poseStack.translate(0.5, 0.003125D + yOffset, 0.5);
                        poseStack.scale(0.3f, 0.3f, 0.3f);
                        poseStack.mulPose(Vector3f.XP.rotationDegrees(90F));
                        poseStack.mulPose(Vector3f.ZP.rotationDegrees(180F));

                        ordinal++;
                        poseStack.translate(0, 0, -0.12F * ordinal);

                        render.renderStatic(item, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffers, 0);
                        poseStack.popPose();
                    }
                }
            });
            poseStack.popPose();
        }
    }
}
