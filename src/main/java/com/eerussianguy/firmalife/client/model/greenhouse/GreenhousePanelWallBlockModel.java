package com.eerussianguy.firmalife.client.model.greenhouse;

import com.eerussianguy.firmalife.client.model.GreenhouseBlockModel;
import com.eerussianguy.firmalife.common.blocks.greenhouse.GreenhouseConnectable;
import com.eerussianguy.firmalife.common.blocks.greenhouse.GreenhousePanelWallBlock;
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

public class GreenhousePanelWallBlockModel extends GreenhouseBlockModel.Baked
{
    // UV width of one pixel, for a 16x16 texture
    private static final float PIXEL_WIDTH = 1 / 16f;
    private final Material materialTexture;
    private final Material glassThinTexture;
    private final Material glassThinBothTexture;
    private final Material glassThinUpTexture;
    private final Material glassThinDownTexture;
    private final Material glassThickTexture;
    private final Material glassThickBothTexture;
    private final Material glassThickUpTexture;
    private final Material glassThickDownTexture;

    public GreenhousePanelWallBlockModel(IGeometryBakingContext context, boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides)
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
        final TextureAtlasSprite postTexture = materialTexture.sprite();
        final Direction facing = state.getValue(GreenhousePanelWallBlock.FACING);
        final boolean left = state.getValue(GreenhousePanelWallBlock.LEFT);
        final boolean right = state.getValue(GreenhousePanelWallBlock.RIGHT);
        final boolean down = state.getValue(GreenhousePanelWallBlock.DOWN);
        final GreenhouseConnectable.DualSide up = state.getValue(GreenhousePanelWallBlock.UP);
        final GreenhouseConnectable.Side side = state.getValue(GreenhousePanelWallBlock.EXTRA_WALL);

        //TODO issues with corner walls when the main face has an upwards connection
        //TODO breaking animation is incorrect
        final float angle = switch (facing)
        {
            case SOUTH -> 180;
            case EAST -> 270;
            case WEST -> 90;
            default -> 0;
        };
        final Vec3i normal = switch (facing)
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

        drawSide(poseStack, buffer, packedLight, packedOverlay, GreenhouseConnectable.DualSide.LEFT, left, up, down, side == GreenhouseConnectable.Side.LEFT, false, normal);
        drawSide(poseStack, buffer, packedLight, packedOverlay, GreenhouseConnectable.DualSide.RIGHT, right, up, down, side == GreenhouseConnectable.Side.RIGHT, true, normal);

        // Top
        if (up == GreenhouseConnectable.DualSide.NONE || up == side.dual())
        {
            drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, 0, PIXEL_WIDTH * 14, 0f, 1, 1f, PIXEL_WIDTH * 2, normal);
        }
        // Bottom
        if (!down)
        {
            drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, 0, 0, 0f, 1, PIXEL_WIDTH * 2, PIXEL_WIDTH * 2, normal);
        }

        poseStack.popPose();
        poseStack.popPose();
    }

    private void drawSide(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, GreenhouseConnectable.DualSide sideType, boolean side, GreenhouseConnectable.DualSide top, boolean bottom, boolean corner, boolean mirror, Vec3i normal)
    {
        final TextureAtlasSprite postTexture = materialTexture.sprite();
        final float width = side ? PIXEL_WIDTH : 0;
        final float postStart = bottom ? 0 : (PIXEL_WIDTH * 2);
        final boolean topConnection = top.contains(sideType.opposite());
        final boolean topSideConnection = top.contains(sideType);
        final float postEnd = topConnection ? 1 : (PIXEL_WIDTH * 14);
        final float sidePostEnd = topSideConnection ? 1 : (PIXEL_WIDTH * 14);
        final float topOffset = 1 - (topConnection ? 0 : 2 / 16f);
        final float topSideOffset = 1 - (topSideConnection ? 0 : 2) / 16f;
        final float downOffset = (bottom ? 0 : 2) / 16f;

        float postWidth = PIXEL_WIDTH * 2;
        final float widthStartOffset = side && !mirror ? PIXEL_WIDTH : 0;
        final float widthEndOffset = side && mirror ? PIXEL_WIDTH : 0;

        float glassStart = mirror ? PIXEL_WIDTH * 2 : 0.5f;
        float glassEnd = mirror ? 0.5f : PIXEL_WIDTH * 14;
        float cornerPostStart = mirror ? 0 : PIXEL_WIDTH * 14;
        float cornerPostEnd = mirror ? PIXEL_WIDTH * 2 : 1f;

        if (corner)
        {
            // Corner post
            drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, cornerPostStart, postStart, 0f, cornerPostEnd, 1f, PIXEL_WIDTH * 2, normal);
            // Extra wall post
            drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, cornerPostStart, postStart, PIXEL_WIDTH * 14 + width, cornerPostEnd, sidePostEnd, 1, normal);
            // Bottom post for the extra wall
            if (!bottom)
            {
                drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, cornerPostStart, 0f, PIXEL_WIDTH * 2, cornerPostEnd, PIXEL_WIDTH * 2, 1f, normal);
            }
            // Top Post for the extra wall
            if (!topSideConnection)
            {
                drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, cornerPostStart, PIXEL_WIDTH * 14, PIXEL_WIDTH * 2, cornerPostEnd, 1f, 1f, normal);
            }

            final float glassSidePos = PIXEL_WIDTH * (mirror ? 1 : 15);

            // Glass panels connected to the corner post, and cannot have side connections
            drawGlass(poseStack, buffer, packedLight, packedOverlay, false, topConnection, bottom, getPlaneVertices(glassStart, downOffset, PIXEL_WIDTH, glassEnd, topOffset, PIXEL_WIDTH), normal);
            drawGlass(poseStack, buffer, packedLight, packedOverlay, false, topSideConnection, bottom, getPlaneVertices(glassSidePos, downOffset, PIXEL_WIDTH * 2, glassSidePos, topSideOffset, 0.5f), normal);

            // Glass panel in the extra wall not connected to the corner post
            drawGlass(poseStack, buffer, packedLight, packedOverlay, side, topSideConnection, bottom, getPlaneVertices(glassSidePos, downOffset, 0.5f, glassSidePos, topSideOffset, PIXEL_WIDTH * 14 + width), normal);
        }
        else
        {
            drawGlass(poseStack, buffer, packedLight, packedOverlay, side, topConnection, bottom, getPlaneVertices(glassStart - widthEndOffset, downOffset, PIXEL_WIDTH, glassEnd + widthStartOffset, topOffset, PIXEL_WIDTH), normal);
            drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, cornerPostStart + widthStartOffset, postStart, 0f, cornerPostEnd - widthEndOffset, postEnd, postWidth, normal);
        }
    }

    private void drawCube(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite texture, int packedLight, int packedOverlay, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Vec3i normal)
    {
        RenderHelpers.renderTexturedQuads(poseStack, buffer, texture, packedLight, packedOverlay, getXVertices(minX, minY, minZ, maxX, maxY, maxZ), 16, 16, normal.getX(), 0, normal.getZ(), true);
        RenderHelpers.renderTexturedQuads(poseStack, buffer, texture, packedLight, packedOverlay, getYVertices(minX, minY, minZ, maxX, maxY, maxZ), 16, 16, 0, 1, 0, true);
        RenderHelpers.renderTexturedQuads(poseStack, buffer, texture, packedLight, packedOverlay, getZVertices(minX, minY, minZ, maxX, maxY, maxZ), 16, 16, normal.getZ(), 0, normal.getX(), true);
    }

    private void drawGlass(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, boolean side, boolean top, boolean bottom, float[][] vertices, Vec3i normal)
    {
        TextureAtlasSprite glass = selectGlassTexture(side, bottom, top);
        RenderHelpers.renderTexturedQuads(poseStack, buffer, glass, packedLight, packedOverlay, vertices, 16, 16, normal.getX(), normal.getY(), normal.getZ(), false);
    }

    private TextureAtlasSprite selectGlassTexture(boolean side, boolean bottom, boolean top)
    {
        Material material;
        if (side)
        {
            if (bottom && top)
            {
                material = glassThinBothTexture;
            }
            else if (bottom)
            {
                material = glassThinUpTexture;
            }
            else if (top)
            {
                material = glassThinDownTexture;
            }
            else
            {
                material = glassThinTexture;
            }
        }
        else
        {
            if (bottom && top)
            {
                material = glassThickBothTexture;
            }
            else if (bottom)
            {
                material = glassThickUpTexture;
            }
            else if (top)
            {
                material = glassThickDownTexture;
            }
            else
            {
                material = glassThickTexture;
            }
        }
        return material.sprite();
    }

    /**
     * Modified version of {@link RenderHelpers#getDiagonalPlaneVertices(float, float, float, float, float, float, float, float, float, float)}
     * that includes data for the normal, and calculates UV based off of the position
     */
    private static float[][] getPlaneVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        assert minX <= maxX : "x " + minX + " " + maxX;
        assert minY <= maxY : "y " + minY + " " + maxY;
        assert minZ <= maxZ : "z " + minZ + " " + maxZ;
        final float v0 = 1 - maxY;
        final float v1 = 1 - minY;
        final float u0 = maxX - minX > maxZ - minZ ? minX : minZ;
        final float u1 = u0 + Math.max(maxX - minX, maxZ - minZ);
        return new float[][] {
            {minX, minY, minZ, u0, v1, 1.0F},
            {maxX, minY, maxZ, u1, v1, 1.0F},
            {maxX, maxY, maxZ, u1, v0, 1.0F},
            {minX, maxY, minZ, u0, v0, 1.0F},

            {maxX, minY, maxZ, 1 - u1, v1, -1.0F},
            {minX, minY, minZ, 1 - u0, v1, -1.0F},
            {minX, maxY, minZ, 1 - u0, v0, -1.0F},
            {maxX, maxY, maxZ, 1 - u1, v0, -1.0F}
        };
    }

    /**
     * Modified version of {@link RenderHelpers#getXVertices(float, float, float, float, float, float)}
     * with UV mapping based off of position
     */
    public static float[][] getXVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        assert minX < maxX : "x";
        assert minY < maxY : "y";
        assert minZ < maxZ : "z";
        final float u0 = minZ;
        final float u1 = maxZ;
        final float v0 = 1 - minY;
        final float v1 = 1 - maxY;
        return new float[][] {
            {minX, minY, minZ, u0, v0, 1.0F},
            {minX, minY, maxZ, u1, v0, 1.0F},
            {minX, maxY, maxZ, u1, v1, 1.0F},
            {minX, maxY, minZ, u0, v1, 1.0F},
            {maxX, minY, maxZ, 1 - u1, v0, -1.0F},
            {maxX, minY, minZ, 1 - u0, v0, -1.0F},
            {maxX, maxY, minZ, 1 - u0, v1, -1.0F},
            {maxX, maxY, maxZ, 1 - u1, v1, -1.0F}
        };
    }

    /**
     * Modified version of {@link RenderHelpers#getYVertices(float, float, float, float, float, float)}
     * with UV mapping based off of position
     */
    public static float[][] getYVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        assert minX < maxX : "x";
        assert minY < maxY : "y";
        assert minZ < maxZ : "z";
        final float u0 = 1 - minZ;
        final float u1 = 1 - maxZ;
        final float v0 = 1 - minX;
        final float v1 = 1 - maxX;
        return new float[][] {
            {minX, maxY, minZ, u0, v0, 1.0F},
            {minX, maxY, maxZ, u1, v0, 1.0F},
            {maxX, maxY, maxZ, u1, v1, 1.0F},
            {maxX, maxY, minZ, u0, v1, 1.0F},
            {minX, minY, maxZ, u1, v0, -1.0F},
            {minX, minY, minZ, u0, v0, -1.0F},
            {maxX, minY, minZ, u0, v1, -1.0F},
            {maxX, minY, maxZ, u1, v1, -1.0F}
        };
    }

    /**
     * Modified version of {@link RenderHelpers#getZVertices(float, float, float, float, float, float)}
     * with UV mapping based off of position
     */
    public static float[][] getZVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        assert minX < maxX : "x";
        assert minY < maxY : "y";
        assert minZ < maxZ : "z";
        final float u0 = 1 - maxX;
        final float u1 = 1 - minX;
        final float v0 = 1 - maxY;
        final float v1 = 1 - minY;
        return new float[][] {
            {maxX, minY, minZ, u0, v1, 1.0F},
            {minX, minY, minZ, u1, v1, 1.0F},
            {minX, maxY, minZ, u1, v0, 1.0F},
            {maxX, maxY, minZ, u0, v0, 1.0F},
            {minX, minY, maxZ, u1, v1, -1.0F},
            {maxX, minY, maxZ, u0, v1, -1.0F},
            {maxX, maxY, maxZ, u0, v0, -1.0F},
            {minX, maxY, maxZ, u1, v0, -1.0F}
        };
    }

}
