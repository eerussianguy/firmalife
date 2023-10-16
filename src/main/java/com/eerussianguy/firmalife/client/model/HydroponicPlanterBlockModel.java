package com.eerussianguy.firmalife.client.model;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.HydroponicPlanterBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.QuadPlanterBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.entity.BlockEntityType;


public class HydroponicPlanterBlockModel extends QuadPlanterBlockModel<HydroponicPlanterBlockEntity>
{
    public HydroponicPlanterBlockModel(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides, baseModel);
    }

    @Override
    public void render(QuadPlanterBlockEntity planter, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        super.render(planter, poseStack, buffer, packedLight, packedOverlay);
        // todo fix
//        RenderHelpers.renderFluidFace(poseStack, new FluidStack(Fluids.WATER, 100), buffer, 1f / 16, 1f / 16, 15f / 16, 15f / 16, 5f / 16, packedOverlay, packedLight);
    }

    @Override
    public BlockEntityType<HydroponicPlanterBlockEntity> type()
    {
        return FLBlockEntities.HYDROPONIC_PLANTER.get();
    }
}
