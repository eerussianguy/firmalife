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
    protected void render(BlockState state, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        TextureAtlasSprite postTexture = materialTexture.sprite();
        Direction facing = state.getValue(GreenhousePanelWallBlock.FACING);
        boolean left = state.getValue(GreenhousePanelWallBlock.LEFT);
        boolean right = state.getValue(GreenhousePanelWallBlock.RIGHT);
        boolean down = state.getValue(GreenhousePanelWallBlock.DOWN);
        GreenhouseConnectable.PostType up = state.getValue(GreenhousePanelWallBlock.UP);
        GreenhouseConnectable.SideType side = state.getValue(GreenhousePanelWallBlock.EXTRA_WALL);

        float angle = switch (facing)
        {
            case NORTH -> 0;
            case SOUTH -> 180;
            case EAST -> 270;
            case WEST -> 90;
            default -> throw new IllegalStateException("Unexpected value: " + facing);
        };
        //TODO normals are incorrect for RenderHelpers#renderTexturedCuboid calls for rotated models
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

        drawLeft(poseStack, buffer, packedLight, packedOverlay, left, up, down, side == GreenhouseConnectable.SideType.LEFT, normal);
        drawRight(poseStack, buffer, packedLight, packedOverlay, right, up, down, side == GreenhouseConnectable.SideType.RIGHT, normal);

        // Top
        if (up == GreenhouseConnectable.PostType.BOTH || up == side.toPost().opposite())
        {
            drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, 0, 14 / 16f, 0f, 1, 1f, 2 / 16f, 0, 0, 1, 1, normal);
        }
        // Bottom
        if (!down)
        {
            drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, 0, 0, 0f, 1, 2 / 16f, 2 / 16f, 0, 0, 1, 1, normal);
        }

        poseStack.popPose();
        poseStack.popPose();
    }

    private void drawLeft(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, boolean side, GreenhouseConnectable.PostType top, boolean bottom, boolean corner, Vec3i normal)
    {
        TextureAtlasSprite postTexture = materialTexture.sprite();
        float width = 1f - (side ? 1 : 2) / 16f;
        float postStart = bottom ? 0 : (2 / 16f);
        boolean topConnection = !top.contains(GreenhouseConnectable.PostType.LEFT);
        float postEnd = topConnection ? 1 : (14 / 16f);
        float topOffset = 1 - (true ? 0 : 2) / 16f;
        float downOffset = (true ? 0 : 2) / 16f;

        if (corner)
        {
            drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, 14 / 16f, postStart, 0f, 1f, postEnd, 2 / 16f, 0, 0, 1, 1, normal);
            drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, 14 / 16f, postStart, width, 1f, postEnd, 1, 0, 0, 1, 1, normal);
            // Bottom post for the extra wall
            if (!bottom)
            {
                drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, 14 / 16f, 0, 2 / 16f, 1f, 2 / 16f, 1, 0, 0, 1, 1, normal);
            }
            // Top Post for the extra wall
            if (!topConnection)
            {
                drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, 14 / 16f, 14 / 16f, 0, 1f, 1, 1, 0, 0, 1, 1, normal);
            }

            // Glass panels connected to the corner post, and cannot have side connections
            drawGlass(poseStack, buffer, packedLight, packedOverlay, false, GreenhouseConnectable.PostType.LEFT.contains(top), bottom, getPlaneVertices(0.5f, topOffset, 1 / 16f, 14 / 16f, downOffset, 1 / 16f, 2 / 16f, topOffset, 0.5f, downOffset), normal);
            drawGlass(poseStack, buffer, packedLight, packedOverlay, false, topConnection, bottom, getPlaneVertices(15 / 16f, 0, 0, 15 / 16f, 1, 0.5f, 0.5f, 0, 1, 1), normal);

            // Glass panel in the extra wall not connected to the corner post
            drawGlass(poseStack, buffer, packedLight, packedOverlay, side, topConnection, bottom, getPlaneVertices(15 / 16f, 0, 0.5f, 15 / 16f, 1, 1, 0, 0, 0.5f, 1), normal);
        }
        else
        {
            drawGlass(poseStack, buffer, packedLight, packedOverlay, side, topConnection, bottom, getPlaneVertices(0.5f, topOffset, 1 / 16f, width, downOffset, 1 / 16f, 1 - width, topOffset, 0.5f, downOffset), normal);
            drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, width, postStart, 0f, 1f, postEnd, 2 / 16f, 0, 0, 1, 1, normal);
        }
    }

    private void drawRight(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, boolean side, GreenhouseConnectable.PostType top, boolean bottom, boolean corner, Vec3i normal)
    {
        TextureAtlasSprite postTexture = materialTexture.sprite();
        float width = (side ? 1 : 2) / 16f;
        float postStart = bottom ? 0 : (2 / 16f);
        boolean topConnection = top == GreenhouseConnectable.PostType.LEFT || top == GreenhouseConnectable.PostType.NONE;
        float postEnd = topConnection ? 1 : (14 / 16f);
        float topOffset = 1 - (true ? 0 : 2) / 16f;
        float downOffset = (true ? 0 : 2) / 16f;
        if (corner)
        {
            drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, 0f, postStart, 0f, 2 / 16f, postEnd, 2 / 16f, 0, 0, 1, 1, normal);
            drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, 0f, postStart, 1 - width, 2 / 16f, postEnd, 1, 0, 0, 1, 1, normal);
        }
        else
        {
            drawCube(poseStack, buffer, postTexture, packedLight, packedOverlay, 0f, postStart, 0f, width, postEnd, 2 / 16f, 0, 0, 1, 1, normal);
            drawGlass(poseStack, buffer, packedLight, packedOverlay, side, topConnection, bottom, getPlaneVertices(width, topOffset, 1 / 16f, 0.5f, downOffset, 1 / 16f, 0.5f, topOffset, 1f - width, downOffset), normal);
        }
    }

    private void drawCube(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite texture, int packedLight, int packedOverlay, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float u0, float v0, float u1, float v1, Vec3i normal)
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
     * Modified version of {@link RenderHelpers#getDiagonalPlaneVertices(float, float, float, float, float, float, float, float, float, float)} that includes data for the normal
     */
    private static float[][] getPlaneVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float u0, float v0, float u1, float v1)
    {
        return new float[][] {
            {minX, minY, minZ, u1, v1, 1.0F},
            {maxX, minY, maxZ, u0, v1, 1.0F},
            {maxX, maxY, maxZ, u0, v0, 1.0F},
            {minX, maxY, minZ, u1, v0, 1.0F},

            {maxX, minY, maxZ, u0, v1, -1.0F},
            {minX, minY, minZ, u1, v1, -1.0F},
            {minX, maxY, minZ, u1, v0, -1.0F},
            {maxX, maxY, maxZ, u0, v0, -1.0F}
        };
    }

    /**
     * Modified version of {@link RenderHelpers#getXVertices(float, float, float, float, float, float)} with custom UV mapping
     */
    public static float[][] getXVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        float u0 = 1 - minZ;
        float u1 = 1 - maxZ;
        float v0 = 1 - minY;
        float v1 = 1 - maxY;
        return new float[][] {
            {minX, minY, minZ, u0, v1, 1.0F},
            {minX, minY, maxZ, u1, v1, 1.0F},
            {minX, maxY, maxZ, u1, v0, 1.0F},
            {minX, maxY, minZ, u0, v0, 1.0F},
            {maxX, minY, maxZ, u1, v0, -1.0F},
            {maxX, minY, minZ, u0, v0, -1.0F},
            {maxX, maxY, minZ, u0, v1, -1.0F},
            {maxX, maxY, maxZ, u1, v1, -1.0F}
        };
    }

    /**
     * Modified version of {@link RenderHelpers#getYVertices(float, float, float, float, float, float)} with custom UV mapping
     */
    public static float[][] getYVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        float u0 = 1 - minZ;
        float u1 = 1 - maxZ;
        float v0 = 1 - minX;
        float v1 = 1 - maxX;
        return new float[][] {
            {minX, maxY, minZ, u0, v1, 1.0F},
            {minX, maxY, maxZ, u1, v1, 1.0F},
            {maxX, maxY, maxZ, u1, v0, 1.0F},
            {maxX, maxY, minZ, u0, v0, 1.0F},
            {minX, minY, maxZ, u1, v0, -1.0F},
            {minX, minY, minZ, u0, v0, -1.0F},
            {maxX, minY, minZ, u0, v1, -1.0F},
            {maxX, minY, maxZ, u1, v1, -1.0F}
        };
    }

    /**
     * Modified version of {@link RenderHelpers#getZVertices(float, float, float, float, float, float)} with custom UV mapping
     */
    public static float[][] getZVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        float u0 = 1 - maxX;
        float u1 = 1 - minX;
        float v0 = 1 - maxY;
        float v1 = 1 - minY;
        return new float[][] {
            {maxX, minY, minZ, u0, v1, 1.0F},
            {minX, minY, minZ, u1, v1, 1.0F},
            {minX, maxY, minZ, u1, v0, 1.0F},
            {maxX, maxY, minZ, u0, v0, 1.0F},
            {minX, minY, maxZ, u1, v0, -1.0F},
            {maxX, minY, maxZ, u0, v0, -1.0F},
            {maxX, maxY, maxZ, u0, v1, -1.0F},
            {minX, maxY, maxZ, u1, v1, -1.0F}
        };
    }

}
