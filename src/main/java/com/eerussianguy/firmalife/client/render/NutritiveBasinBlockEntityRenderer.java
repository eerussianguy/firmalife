package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.blockentities.NutritiveBasinBlockEntity;
import com.eerussianguy.firmalife.common.blocks.greenhouse.HydroponicPlanterBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.NutritiveBasinBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.client.RenderHelpers;

public class NutritiveBasinBlockEntityRenderer implements BlockEntityRenderer<NutritiveBasinBlockEntity>
{
    @Override
    public void render(NutritiveBasinBlockEntity basin, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        final BlockState state = basin.getBlockState();
        if (state.getValue(NutritiveBasinBlock.WATERED) && basin.getLevel() != null && !(basin.getLevel().getBlockState(basin.getBlockPos().above()).getBlock() instanceof HydroponicPlanterBlock))
        {
            RenderHelpers.renderFluidFace(poseStack, new FluidStack(Fluids.WATER, 100), buffers, 1f / 16, 1f / 16f, 1f / 16, 1f / 16, 1, combinedOverlay, combinedLight);
        }
    }
}
