package com.eerussianguy.firmalife.client.model;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.WineShelfBlockEntity;
import com.eerussianguy.firmalife.common.blocks.WineShelfBlock;
import com.eerussianguy.firmalife.common.items.WineBottleItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.IItemHandler;

import net.dries007.tfc.common.capabilities.BlockCapabilities;
import net.dries007.tfc.util.Helpers;

public class WineShelfBlockModel extends SimpleDynamicBlockModel<WineShelfBlockEntity>
{
    public WineShelfBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    public void render(WineShelfBlockEntity shelf, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        if (!(shelf.getBlockState().getBlock() instanceof WineShelfBlock && shelf.getLevel() != null))
            return;

        final Minecraft mc = Minecraft.getInstance();
        final ModelBlockRenderer renderer = mc.getBlockRenderer().getModelRenderer();
        final Direction facing = shelf.getBlockState().getValue(WineShelfBlock.FACING);
        final int angle = switch (facing)
            {
                case SOUTH -> 180;
                case EAST -> 270;
                case WEST, DOWN, UP -> 90;
                case NORTH -> 0;
            };

        final IItemHandler inv = Helpers.getCapability(BlockCapabilities.ITEM, shelf);
        if (inv == null)
            return;
        for (int i = 0; i < 4; i++)
        {
            final ItemStack stack = inv.getStackInSlot(i);
            if (stack.getItem() instanceof WineBottleItem wine)
            {
                poseStack.pushPose();
                poseStack.translate(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
                poseStack.translate(-0.5f, -0.5f, -0.5f);

                poseStack.translate(i < 2 ? 0.25f : -0.25f, 1f / 16f, i % 2 == 0 ? 0.25f : -0.25f);

                final BakedModel baked = mc.getModelManager().getModel(wine.getModelLocation());
                renderer.tesselateWithAO(shelf.getLevel(), baked, shelf.getBlockState(), shelf.getBlockPos(), poseStack, buffer, false, RandomSource.create(), 4L, packedOverlay, ModelData.EMPTY, RenderType.cutout());

                poseStack.popPose();
            }
        }

    }

    @Override
    public BlockEntityType<WineShelfBlockEntity> type()
    {
        return FLBlockEntities.WINE_SHELF.get();
    }
}
