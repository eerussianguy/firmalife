package com.eerussianguy.firmalife.client.model;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.HangerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.dries007.tfc.common.capabilities.Capabilities;

public class HangerBlockModel extends SimpleDynamicBlockModel<HangerBlockEntity>
{
    public HangerBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    protected void render(HangerBlockEntity hanger, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        final ItemStack stack = hanger.getCapability(Capabilities.ITEM).map(cap -> cap.getStackInSlot(0)).orElse(ItemStack.EMPTY);
        if (stack.isEmpty() || hanger.getLevel() == null) return;

        int maxStackSize = Mth.clamp(stack.getItem().getMaxStackSize(stack), 1, 64);
        final int draws = Mth.ceil(stack.getCount() / (float) maxStackSize * 4);

        for (int i = 0; i < draws; i++)
        {
            poseStack.pushPose();

            final float center = -0.5f;
            poseStack.translate(-center, -center, -center);
            poseStack.mulPose(Axis.YP.rotationDegrees(30f * i + 30f));
            poseStack.translate(center, center, center);

            poseStack.translate(0.26f, 0.26f, 0.26f);
            poseStack.scale(0.45f, 0.45f, 0.45f);
            poseStack.translate(0, -i, 0);
            final BakedModel model = itemRenderer.getModel(stack, hanger.getLevel(), null, 42);
            itemRenderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, buffer);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    @Override
    protected BlockEntityType<HangerBlockEntity> type()
    {
        return FLBlockEntities.HANGER.get();
    }
}
