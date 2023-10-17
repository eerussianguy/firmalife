package com.eerussianguy.firmalife.client.model;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.JarbnetBlockEntity;
import com.eerussianguy.firmalife.common.blocks.JarbnetBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.items.CandleBlockItem;
import net.dries007.tfc.common.items.JarItem;
import net.dries007.tfc.common.items.JugItem;

public class JarbnetBlockModel extends SimpleDynamicBlockModel<JarbnetBlockEntity>
{
    public static final ResourceLocation JUG_LOCATION = FLHelpers.identifier("block/jar_jug");

    private static final int[] CANDLE_AMOUNTS = {3, 1, 2, 2, 3, 1};

    public JarbnetBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    public void render(JarbnetBlockEntity jarbnet, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        if (jarbnet.getBlockState().getBlock() instanceof JarbnetBlock && jarbnet.getLevel() != null)
        {
            final Minecraft mc = Minecraft.getInstance();
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
                    poseStack.mulPose(Axis.YP.rotationDegrees(angle));

                    final ModelBlockRenderer modelRenderer = mc.getBlockRenderer().getModelRenderer();

                    for (int i = 0; i < JarbnetBlockEntity.SLOTS; i++)
                    {
                        final Item item = inv.getStackInSlot(i).getItem();
                        final boolean isJug = item instanceof JugItem;
                        final boolean isCandle = item instanceof CandleBlockItem;
                        final boolean isJar = item instanceof JarItem;
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
                            modelRenderer.tesselateWithAO(jarbnet.getLevel(), mc.getBlockRenderer().getBlockModel(state), state, jarbnet.getBlockPos(), poseStack, buffer, false, RandomSource.create(), 4L, packedOverlay, ModelData.EMPTY, RenderType.cutout());
                        }
                        else if (isJug)
                        {
                            final BakedModel baked = mc.getModelManager().getModel(JUG_LOCATION);
                            modelRenderer.tesselateWithAO(jarbnet.getLevel(), baked, jarbnet.getBlockState(), jarbnet.getBlockPos(), poseStack, buffer, false, RandomSource.create(), 4L, packedOverlay, ModelData.EMPTY, RenderType.cutout());
                        }
                        else if (isJar)
                        {
                            poseStack.translate(0.5f, 0f, 0.25f);
                            final BakedModel baked = mc.getModelManager().getModel(((JarItem) item).getModel());
                            modelRenderer.tesselateWithAO(jarbnet.getLevel(), baked, jarbnet.getBlockState(), jarbnet.getBlockPos(), poseStack, buffer, false, RandomSource.create(), 4L, packedOverlay, ModelData.EMPTY, RenderType.cutout());
                        }
                        poseStack.popPose();
                    }

                    poseStack.popPose();
                });
            }
        }
    }

    @Override
    public BlockEntityType<JarbnetBlockEntity> type()
    {
        return FLBlockEntities.JARBNET.get();
    }
}
