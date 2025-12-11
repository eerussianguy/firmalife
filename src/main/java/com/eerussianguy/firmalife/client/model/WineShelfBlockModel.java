package com.eerussianguy.firmalife.client.model;

import java.util.List;
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
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class WineShelfBlockModel extends InventoryBlockModel.Baked
{
    public WineShelfBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    protected void render(List<ItemStack> inventory, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        final Level level = Minecraft.getInstance().level;
        if (!(state.getBlock() instanceof WineShelfBlock && level != null))
            return;

        final Minecraft mc = Minecraft.getInstance();
        final ModelBlockRenderer renderer = mc.getBlockRenderer().getModelRenderer();
        final Direction facing = state.getValue(WineShelfBlock.FACING);
        final int angle = switch (facing)
        {
            case SOUTH -> 180;
            case EAST -> 270;
            case WEST, DOWN, UP -> 90;
            case NORTH -> 0;
        };

        for (int i = 0; i < inventory.size(); i++)
        {
            final ItemStack stack = inventory.get(i);
            if (stack.getItem() instanceof WineBottleItem wine)
            {
                poseStack.pushPose();
                poseStack.translate(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
                poseStack.translate(-0.5f, -0.5f, -0.5f);

                poseStack.translate(i < 2 ? 0.25f : -0.25f, 1f / 16f, i % 2 == 0 ? 0.25f : -0.25f);

                final BakedModel baked = mc.getModelManager().getModel(ModelResourceLocation.standalone(wine.getModelLocation()));
                renderer.tesselateWithAO(level, baked, state, pos, poseStack, buffer, false, RandomSource.create(), 4L, packedOverlay, ModelData.EMPTY, RenderType.cutout());

                poseStack.popPose();
            }
        }
    }
}
