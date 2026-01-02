package com.eerussianguy.firmalife.client.model.greenhouse;

import com.eerussianguy.firmalife.client.model.GreenhouseBlockModel;
import com.eerussianguy.firmalife.common.blocks.greenhouse.GreenhousePanelRoofBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;

import net.dries007.tfc.client.RenderHelpers;

public class GreenhousePanelRoofBlockModel extends GreenhouseBlockModel.Baked
{
    private static final float WIDTH = (float) Math.sqrt(2);
    private static final float W2 = WIDTH / 2;
    private final Material materialTexture;
    private final Material glassThinTexture;
    private final Material glassThinBothTexture;
    private final Material glassThinUpTexture;
    private final Material glassThinDownTexture;
    private final Material glassThickTexture;
    private final Material glassThickBothTexture;
    private final Material glassThickUpTexture;
    private final Material glassThickDownTexture;

    public GreenhousePanelRoofBlockModel(IGeometryBakingContext context, boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides)
    {
        super(isAmbientOcclusion, isGui3d, isSideLit, overrides);
        materialTexture = context.getMaterial("material");
        glassThinTexture = context.getMaterial("glass_thin");
        glassThinBothTexture = context.getMaterial("glass_thin_both");
        glassThinUpTexture = context.getMaterial("glass_thin_up");
        glassThinDownTexture = context.getMaterial("glass_thin_down");
        glassThickTexture = context.getMaterial("glass_thick");
        glassThickBothTexture = context.getMaterial("glass_thick_both");
        glassThickUpTexture = context.getMaterial("glass_thick_up");
        glassThickDownTexture = context.getMaterial("glass_thick_down");
    }

    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return materialTexture.sprite();
    }

    @Override
    protected void render(BlockState state, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        Direction facing = state.getValue(GreenhousePanelRoofBlock.FACING);
        boolean bottom = state.getValue(GreenhousePanelRoofBlock.DOWN);
        boolean top = state.getValue(GreenhousePanelRoofBlock.UP);
        boolean right = state.getValue(GreenhousePanelRoofBlock.RIGHT);
        boolean left = state.getValue(GreenhousePanelRoofBlock.LEFT);
        float rightOffset = (right ? 1 : 2) / 16f;
        float leftOffset = (left ? 1 : 2) / 16f;
        float angle = switch (facing)
        {
            case SOUTH -> 180;
            case EAST -> 270;
            case WEST -> 90;
            default -> 0;
        };
        Vec3i normal = switch (facing)
        {
            case NORTH, SOUTH -> new Vec3i(1, 0, 0);
            case EAST, WEST -> new Vec3i(0, 0, 1);
            default -> new Vec3i(0, 0, 0);
        };

        poseStack.pushPose();
        poseStack.translate(0.5f, 0, 0.5f);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.translate(-0.5f, 0, -0.5f);

        RenderHelpers.renderTexturedQuads(poseStack, buffer, materialTexture.sprite(), packedLight, packedOverlay, getQuads(0f, 0f, 0f, rightOffset, 1f, 1f), 16, 16, 0, 0, 0, true);
        RenderHelpers.renderTexturedQuads(poseStack, buffer, materialTexture.sprite(), packedLight, packedOverlay, getQuads(1 - leftOffset, 0f, 0f, 1f, 1f, 1f), 16, 16, 0, 0, 0, true);

        if (bottom)
        {
            RenderHelpers.renderTexturedQuads(poseStack, buffer, materialTexture.sprite(), packedLight, packedOverlay, getQuads(rightOffset, 0f, 0f, 1 - leftOffset, 2 / 16f, 2 / 16f), 16, 16, 0, 0, 0, true);
        }
        if (top)
        {
            RenderHelpers.renderTexturedQuads(poseStack, buffer, materialTexture.sprite(), packedLight, packedOverlay, getQuads(rightOffset, 14 / 16f, 14 / 16f, 1 - leftOffset, 1f, 1f), 16, 16, 0, 0, 0, true);
        }

        RenderHelpers.renderTexturedQuads(poseStack, buffer, materialTexture.sprite(), packedLight, packedOverlay, getPlaneVertices(rightOffset, -WIDTH / 16f, 0, 1 - leftOffset, 1 - (WIDTH / 16f), 1), 16, 16, 0, 0, 0, true);

        poseStack.popPose();
        poseStack.popPose();
    }

    private float[][] getQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        float width = WIDTH;
        float p2minY = minY - (width / 16f);
        float p2maxY = maxY - (width / 16f);
        float p2minZ = minY + (width / 16f);
        float p2maxZ = maxY + (width / 16f);
        return new float[][] {
            // Front face
            {maxX, maxY, maxZ, 0, 0, 0},
            {maxX, minY, minZ, 0, 1, 0},
            {minX, minY, minZ, 1, 1, 0},
            {minX, maxY, maxZ, 1, 0, 0},

            // Back face
            {minX, p2maxY, p2maxZ, 1, 0, 0},
            {minX, p2minY, p2minZ, 1, 1, 0},
            {maxX, p2minY, p2minZ, 0, 1, 0},
            {maxX, p2maxY, p2maxZ, 0, 0, 0},

            // Bottom ending face
            {maxX, minY, minZ, 0, 0, 0},
            {maxX, p2minY, p2minZ, 0, 1, 0},
            {minX, p2minY, p2minZ, 1, 1, 0},
            {minX, minY, minZ, 0, 0, 0},

            // Top ending face
            {minX, maxY, maxZ, 0, 0, 0},
            {minX, p2maxY, p2maxZ, 0, 1, 0},
            {maxX, p2maxY, p2maxZ, 1, 1, 0},
            {maxX, maxY, maxZ, 1, 0, 0},

            // Left face
            {maxX, maxY, maxZ, 0, 0, 0},
            {maxX, p2maxY, p2maxZ, 1, 0, 0},
            {maxX, p2minY, p2minZ, 1, 1, 0},
            {maxX, minY, minZ, 0, 1, 0},

            // Right face
            {minX, minY, minZ, 0, 1, 0},
            {minX, p2minY, p2minZ, 1, 1, 0},
            {minX, p2maxY, p2maxZ, 1, 0, 0},
            {minX, maxY, maxZ, 0, 0, 0},
        };
    }

    private static float[][] getPlaneVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new float[][] {
            {maxX, maxY, maxZ, 0, 1, 1.0F},
            {maxX, minY, minZ, 1, 1, 1.0F},
            {minX, minY, minZ, 1, 0, 1.0F},
            {minX, maxY, maxZ, 0, 0, 1.0F},

            {minX, maxY, maxZ, 0, 1, 1.0F},
            {minX, minY, minZ, 1, 1, 1.0F},
            {maxX, minY, minZ, 1, 0, 1.0F},
            {maxX, maxY, maxZ, 0, 0, 1.0F},

        };
    }

}
