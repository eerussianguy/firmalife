package com.eerussianguy.firmalife.compat.emi.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;

public class EmiBlockWidget extends Widget
{
    private final int x;
    private final int y;
    private final int z;
    private final BlockState blockState;
    private final Bounds bounds;
    private final float scale;
    private final float rotX;
    private final float rotY;
    private final int light;

    /**
     * Renders a block in EMI using a given blockstate.
     *
     * @param blockState The blockstate to render.
     * @param x          X-position on screen.
     * @param y          Y-position on screen.
     * @param z          Z-position on screen. (Brings the display forward and backward in relation to other widgets)
     * @param scale      The scale of the block render.
     * @param rotX       The rotation of the block render on the X-axis.
     * @param rotY       The rotation of the block render on the Y-axis.
     * @param light      The light value of the block render. (Use {@link LightTexture} for premade values.)
     */
    public EmiBlockWidget(BlockState blockState, int x, int y, int z, float scale, float rotX, float rotY, int light)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockState = blockState != null ? blockState : Blocks.AIR.defaultBlockState();
        this.scale = scale;
        this.rotX = rotX;
        this.rotY = rotY;
        this.bounds = new Bounds(x, y, (int) scale, (int) scale);
        this.light = light;
    }

    @Override
    public Bounds getBounds()
    {
        return this.bounds;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        if (this.blockState.isAir())
            return;

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(this.x + 9, this.y + 9, z);
        poseStack.mulPose(Axis.XP.rotationDegrees(rotX));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
        poseStack.scale(scale, -scale, scale);
        poseStack.translate(-0.5, -0.5, -0.5);

        RenderType renderType = ItemBlockRenderTypes.getRenderType(this.blockState, false);
        if (this.blockState.getRenderShape() != RenderShape.MODEL)
        {
            renderType = RenderType.solid();
        }

        MultiBufferSource.BufferSource bufferSource = graphics.bufferSource();

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
            this.blockState,
            poseStack,
            bufferSource,
            light,
            OverlayTexture.NO_OVERLAY,
            ModelData.EMPTY,
            renderType);

        bufferSource.endBatch(renderType);

        poseStack.popPose();
    }
}
