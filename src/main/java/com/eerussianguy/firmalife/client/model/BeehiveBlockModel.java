package com.eerussianguy.firmalife.client.model;

import java.util.List;
import com.eerussianguy.firmalife.common.blockentities.FLBeehiveBlockEntity;
import com.eerussianguy.firmalife.common.blocks.WoodenBeehiveBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import net.dries007.tfc.client.render.blockentity.PlacedItemBlockEntityRenderer;

public class BeehiveBlockModel extends InventoryBlockModel.Baked
{
    public BeehiveBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    private final float[] FRAME_POINTS = new float[] {
        1.6f / 16f,
        5.2f / 16f,
        8.8f / 16f,
        12.4f / 16f
    };

    @Override
    public void render(List<ItemStack> inventory, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        final Level level = Minecraft.getInstance().level;
        if (state.getBlock() instanceof WoodenBeehiveBlock && level != null)
        {
            final Minecraft mc = Minecraft.getInstance();
            final Direction facing = state.getValue(WoodenBeehiveBlock.FACING);
            final int angle = switch (facing)
            {
                case SOUTH -> 270;
                case EAST -> 0;
                case WEST, DOWN, UP -> 180;
                case NORTH -> 90;
            };

            if (state.getValue(WoodenBeehiveBlock.OPEN))
            {
                poseStack.pushPose();
                poseStack.translate(0.5, 0, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(angle));

                final ModelBlockRenderer modelRenderer = mc.getBlockRenderer().getModelRenderer();

                for (int i = 0; i < FLBeehiveBlockEntity.FRAME_SLOTS; i++)
                {
                    final Item item = inventory.get(i).getItem();
                    poseStack.pushPose();
                    poseStack.translate(-0.5 + (1 / 16f), 0.0625, -0.5 + FRAME_POINTS[i]);
                    if (PlacedItemBlockEntityRenderer.MODELS.containsKey(item))
                    {
                        final var provider = PlacedItemBlockEntityRenderer.MODELS.get(item);
                        final BakedModel model = mc.getModelManager().getModel(provider.model());
                        modelRenderer.tesselateWithAO(level, model, state, pos, poseStack, buffer, false, RandomSource.create(), 4L, packedOverlay, ModelData.EMPTY, provider.renderType());
                    }
                    poseStack.popPose();
                }

                poseStack.popPose();
            }
        }
    }

}
