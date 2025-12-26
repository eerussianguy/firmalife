package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.CentrifugeBlockEntity;
import com.eerussianguy.firmalife.common.blocks.bee.CentrifugeBlock;
import com.eerussianguy.firmalife.common.blocks.bee.WoodenBeehiveBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import net.dries007.tfc.client.render.blockentity.PlacedItemBlockEntityRenderer;

public class CentrifugeBlockEntityRenderer implements BlockEntityRenderer<CentrifugeBlockEntity>
{
    public static final ModelResourceLocation BASE_LOCATION = ModelResourceLocation.standalone(FLHelpers.identifier("block/centrifuge"));

    private final float[] FRAME_POINTS = new float[] {
        1.6f / 16f,
        5.2f / 16f,
        8.8f / 16f,
        12.4f / 16f
    };

    @Override
    public void render(CentrifugeBlockEntity cent, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        if (cent.getLevel() == null)
            return;

        final BlockState state = cent.getBlockState();

        if (state.getBlock() instanceof CentrifugeBlock)
        {
            final Minecraft mc = Minecraft.getInstance();
            final Direction facing = state.getValue(WoodenBeehiveBlock.FACING);
            final ModelBlockRenderer modelRenderer = mc.getBlockRenderer().getModelRenderer();
            final VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());
            final float rotationAngle = cent.getRotationAngle(partialTick);

            final int angle = switch (facing)
            {
                case SOUTH -> 180;
                case EAST -> 270;
                case NORTH -> 0;
                default -> 90;
            };

            poseStack.pushPose();
            poseStack.translate(0.5f, 0, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            poseStack.mulPose(Axis.YP.rotation(-rotationAngle));
            poseStack.translate(-0.5f, 0f, -0.5f);

            final BakedModel base = mc.getModelManager().getModel(BASE_LOCATION);
            modelRenderer.tesselateWithAO(cent.getLevel(), base, state, cent.getBlockPos(), poseStack, buffer, false, RandomSource.create(), 4L, combinedOverlay, ModelData.EMPTY, RenderType.cutout());

            for (int i = 0; i < CentrifugeBlockEntity.SLOTS; i++)
            {
                final Item item = cent.getInventory().getStackInSlot(i).getItem();
                poseStack.pushPose();
                poseStack.translate(1 / 16f, 0.0625, FRAME_POINTS[i]);
                if (PlacedItemBlockEntityRenderer.MODELS.containsKey(item))
                {
                    final var provider = PlacedItemBlockEntityRenderer.MODELS.get(item);
                    final BakedModel model = mc.getModelManager().getModel(provider.model());
                    modelRenderer.tesselateWithAO(cent.getLevel(), model, state, cent.getBlockPos(), poseStack, buffer, false, RandomSource.create(), 4L, combinedOverlay, ModelData.EMPTY, provider.renderType());
                }
                poseStack.popPose();
            }

            poseStack.translate(-0.5f, 0, -0.5f);
            poseStack.popPose();
        }
    }
}
