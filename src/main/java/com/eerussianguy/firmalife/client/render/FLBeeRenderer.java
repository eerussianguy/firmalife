package com.eerussianguy.firmalife.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.animal.Bee;

public class FLBeeRenderer extends BeeRenderer
{
    public FLBeeRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.shadowRadius = 0.15f;
        this.shadowStrength = 0.5f;
    }

    @Override
    protected void scale(Bee entity, PoseStack poseStack, float ticks)
    {
        poseStack.scale(0.2f, 0.2f, 0.2f);
    }
}
