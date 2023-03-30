package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.JarbnetBlockEntity;
import com.eerussianguy.firmalife.common.blocks.JarbnetBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.items.CandleBlockItem;
import net.dries007.tfc.common.items.JugItem;

public class JarbnetBlockEntityRenderer implements BlockEntityRenderer<JarbnetBlockEntity>
{
    public static final ResourceLocation JUG_LOCATION = FLHelpers.identifier("block/jar_jug");

    public static final int[] CANDLE_AMOUNTS = {3, 1, 2, 2, 3, 1};

    @Override
    public void render(JarbnetBlockEntity jarbnet, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        if (jarbnet.getBlockState().getBlock() instanceof JarbnetBlock)
        {
            final Direction facing = jarbnet.getBlockState().getValue(JarbnetBlock.FACING);
            final int angle = switch (facing)
                {
                    case SOUTH -> 0;
                    case EAST -> 90;
                    case WEST, DOWN, UP -> 270;
                    case NORTH -> 180;
                };

            if (jarbnet.getBlockState().getValue(JarbnetBlock.OPEN))
            {
                jarbnet.getCapability(Capabilities.ITEM).ifPresent(inv -> {
                    poseStack.pushPose();
                    poseStack.translate(0.5, 0.5, 0.5);
                    poseStack.scale(0.8f, 0.8f, 0.8f);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(angle));

                    final var renderer = Minecraft.getInstance().getBlockRenderer();

                    for (int i = 0; i < JarbnetBlockEntity.SLOTS; i++)
                    {
                        final Item item = inv.getStackInSlot(i).getItem();
                        final boolean isJug = item instanceof JugItem;
                        final boolean isCandle = item instanceof CandleBlockItem;
                        //final boolean isJar = item instanceof JarsBlockItem;
                        poseStack.pushPose();
                        final int dx = i > 2 ? i - 3 : i;
                        poseStack.translate(dx * -0.35f - 0.4f, i > 2 ? -0.53f : 0.02f, -0.65f);
                        if (isCandle)
                        {
                            poseStack.translate(0.3f, 0f, 0f);
                        }
                        if (item instanceof BlockItem bi)
                        {
                            BlockState state = bi.getBlock().defaultBlockState();
                            if (isCandle)
                            {
                                state = state.setValue(CandleBlock.CANDLES, CANDLE_AMOUNTS[i]);
                                if (jarbnet.getBlockState().getValue(JarbnetBlock.LIT))
                                {
                                    state = state.setValue(CandleBlock.LIT, true);
                                }
                            }
                            renderer.renderSingleBlock(state, poseStack, buffers, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
                        }
                        else if (isJug)
                        {
                            final Minecraft mc = Minecraft.getInstance();
                            final BakedModel baked = mc.getModelManager().getModel(JUG_LOCATION);
                            final VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());
                            mc.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffer, null, baked, 1f, 1f, 1f, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
                        }
                        poseStack.popPose();
                    }

                    poseStack.popPose();
                });
            }
        }

    }
}
