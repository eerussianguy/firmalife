package com.eerussianguy.firmalife.client.render;

import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

import com.eerussianguy.firmalife.common.blockentities.FoodShelfBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.common.capabilities.Capabilities;

public class FoodShelfBlockEntityRenderer implements BlockEntityRenderer<FoodShelfBlockEntity>
{
    @Override
    public void render(FoodShelfBlockEntity shelf, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        final ItemStack stack = shelf.getCapability(Capabilities.ITEM).map(cap -> cap.getStackInSlot(0)).orElse(ItemStack.EMPTY);
        if (stack.isEmpty()) return;

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
            poseStack.translate(0, 0, -0.9f);
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
                itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffer, shelf.getLevel(), 0);
            }
            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
