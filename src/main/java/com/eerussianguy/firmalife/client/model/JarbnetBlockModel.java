package com.eerussianguy.firmalife.client.model;

import java.util.List;
import com.eerussianguy.firmalife.common.blocks.JarbnetBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import net.dries007.tfc.client.render.blockentity.PlacedItemBlockEntityRenderer;
import net.dries007.tfc.common.items.CandleBlockItem;

public class JarbnetBlockModel extends InventoryBlockModel.Baked
{
    private static final int[] CANDLE_AMOUNTS = {3, 1, 2, 2, 3, 1};

    public JarbnetBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    public void render(List<ItemStack> inventory, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        final Level level = Minecraft.getInstance().level;
        if (state.getBlock() instanceof JarbnetBlock && level != null)
        {
            final Minecraft mc = Minecraft.getInstance();
            final Direction facing = state.getValue(JarbnetBlock.FACING);
            final int angle = switch (facing)
            {
                case SOUTH -> 0;
                case EAST -> 90;
                case WEST, DOWN, UP -> 270;
                case NORTH -> 180;
            };

            if (state.getValue(JarbnetBlock.OPEN))
            {
                poseStack.pushPose();
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.8f, 0.8f, 0.8f);
                poseStack.mulPose(Axis.YP.rotationDegrees(angle));

                final ModelBlockRenderer modelRenderer = mc.getBlockRenderer().getModelRenderer();

                for (int i = 0; i < inventory.size(); i++)
                {
                    final Item item = inventory.get(i).getItem();
                    final boolean isCandle = item instanceof CandleBlockItem;
                    poseStack.pushPose();
                    final int dx = i > 2 ? i - 3 : i;
                    poseStack.translate(dx * -0.35f + 0.135f, i > 2 ? -0.53f : 0.02f, -0.5);
                    if (isCandle)
                    {
                        poseStack.translate(-0.3f, 0f, 0.05f);
                    }
                    if (PlacedItemBlockEntityRenderer.MODELS.containsKey(item))
                    {
                        final var provider = PlacedItemBlockEntityRenderer.MODELS.get(item);
                        final BakedModel model = mc.getModelManager().getModel(provider.model());
                        modelRenderer.tesselateWithAO(level, model, state, pos, poseStack, buffer, false, RandomSource.create(), 4L, packedOverlay, ModelData.EMPTY, provider.renderType());
                    }
                    if (item instanceof BlockItem bi)
                    {
                        BlockState candleState = bi.getBlock().defaultBlockState();
                        if (isCandle)
                        {
                            candleState = candleState.setValue(CandleBlock.CANDLES, CANDLE_AMOUNTS[i]);
                            if (state.getValue(JarbnetBlock.LIT))
                            {
                                candleState = candleState.setValue(CandleBlock.LIT, true);
                            }
                        }
                        modelRenderer.tesselateWithAO(level, mc.getBlockRenderer().getBlockModel(candleState), state, pos, poseStack, buffer, false, RandomSource.create(), 4L, packedOverlay, ModelData.EMPTY, RenderType.cutout());
                    }
                    poseStack.popPose();
                }

                poseStack.popPose();
            }
        }
    }

}
