package com.eerussianguy.firmalife.client.model;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.FoodShelfBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.dries007.tfc.common.capabilities.Capabilities;

public class FoodShelfBlockModel extends SimpleDynamicBlockModel<FoodShelfBlockEntity>
{
    public FoodShelfBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    protected void render(FoodShelfBlockEntity shelf, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        final ItemStack stack = shelf.getCapability(Capabilities.ITEM).map(cap -> cap.getStackInSlot(0)).orElse(ItemStack.EMPTY);
        if (stack.isEmpty() || shelf.getLevel() == null) return;

        int totalDraws = 16;
        int maxStackSize = Mth.clamp(stack.getItem().getMaxStackSize(stack), 1, 64);
        float filled = (float) stack.getCount() / (float) maxStackSize;

        final Direction facing = shelf.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        final int angle = switch (facing)
            {
                case SOUTH -> 0;
                case EAST -> 90;
                case WEST, DOWN, UP -> 270;
                case NORTH -> 180;
            };

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(0.45f, 0.45f, 0.45f);
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        var currentDraws = 0;
        for (int i = 0; i < 4; i++)
        {
            if (currentDraws >= filled * totalDraws)
            {
                break;
            }
            poseStack.pushPose();
            poseStack.translate(-0.5f, -0.35f, -1.1f);
            poseStack.translate((i % 2 == 0) ? 0.45f : -0.45f, (i < 2) ? 0.45f : -0.45f, 0);
            for (int j = 0; j < 4; j++)
            {
                if (currentDraws >= filled * totalDraws)
                {
                    break;
                }
                else
                {
                    currentDraws += 1;
                }
                poseStack.translate(0, 0, 0.175f);

                final BakedModel model = itemRenderer.getModel(stack, shelf.getLevel(), null, 42);
                poseStack.pushPose();
                poseStack.scale(0.9f, 0.9f, 0.9f);
                itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, buffer);
                poseStack.popPose();
            }
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    protected BlockEntityType<FoodShelfBlockEntity> type()
    {
        return FLBlockEntities.FOOD_SHELF.get();
    }
}
