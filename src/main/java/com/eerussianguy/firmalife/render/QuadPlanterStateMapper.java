package com.eerussianguy.firmalife.render;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class QuadPlanterStateMapper extends StateMapperBase
{
    @Override
    @Nonnull
    protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state)
    {
        return new ModelResourceLocation(MOD_ID + ":quad_planter");
    }
}
