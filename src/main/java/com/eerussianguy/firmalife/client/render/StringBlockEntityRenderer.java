package com.eerussianguy.firmalife.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.blockentities.StringBlockEntity;
import com.eerussianguy.firmalife.common.blocks.StringBlock;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.dries007.tfc.common.capabilities.food.FoodCapability;

public class StringBlockEntityRenderer implements BlockEntityRenderer<StringBlockEntity>
{
    @Override
    public void render(StringBlockEntity string, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        Level level = string.getLevel();
        if (level == null) return;
        BlockState state = level.getBlockState(string.getBlockPos());
        if (!(state.getBlock() instanceof StringBlock)) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.38D, 0.5D);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        if (state.getValue(StringBlock.AXIS) == Direction.Axis.Z)
        {
            poseStack.mulPose(Vector3f.YP.rotationDegrees(90f));
        }
        ItemStack item = string.readStack();
        if (!item.isEmpty())
        {
            item.getCapability(FoodCapability.CAPABILITY).ifPresent(cap -> {
                if (cap.getTraits().contains(FLFoodTraits.SMOKED) || cap.getTraits().contains(FLFoodTraits.RANCID_SMOKED))
                {
                    RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1f);
                }
            });
            Minecraft.getInstance().getItemRenderer().renderStatic(item, ItemTransforms.TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffers, 0);
        }
        poseStack.popPose();
    }
}
