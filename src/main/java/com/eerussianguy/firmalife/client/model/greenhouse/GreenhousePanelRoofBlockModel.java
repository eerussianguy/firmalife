package com.eerussianguy.firmalife.client.model.greenhouse;

import com.eerussianguy.firmalife.client.model.GreenhouseBlockModel;
import com.eerussianguy.firmalife.common.blocks.greenhouse.GreenhouseConnectable;
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
    // The width of the panel on the diagonal
    private static final float WIDTH = ((float) Math.sqrt(2) / 16f);
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
    private final Material glassCorner;

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
        glassCorner = context.getMaterial("glass_corner");
    }

    @Override
    public TextureAtlasSprite getParticleIcon()
    {
        return materialTexture.sprite();
    }

    @Override
    protected void render(BlockState state, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay)
    {
        //TODO Z & Y faces should have similar shading from normals
        final Direction facing = state.getValue(GreenhousePanelRoofBlock.FACING);
        final GreenhouseConnectable.PostType diagonal = state.getValue(GreenhousePanelRoofBlock.DIAGONAL);
        final GreenhouseConnectable.PostType sides = state.getValue(GreenhousePanelRoofBlock.SIDES);
        final GreenhouseConnectable.PostSize cw = state.getValue(GreenhousePanelRoofBlock.CW);
        final GreenhouseConnectable.PostSize ccw = state.getValue(GreenhousePanelRoofBlock.CCW);
        final boolean right = sides.contains(GreenhouseConnectable.PostType.RIGHT);
        final boolean left = sides.contains(GreenhouseConnectable.PostType.LEFT);
        final boolean down = diagonal.contains(GreenhouseConnectable.PostType.RIGHT);
        final boolean up = diagonal.contains(GreenhouseConnectable.PostType.LEFT);
        final boolean bottom = state.getValue(GreenhousePanelRoofBlock.BOTTOM);
        final boolean back = state.getValue(GreenhousePanelRoofBlock.BACK);
        final float rightOffset = (right ? 1 : 2) / 16f;
        final float leftOffset = (left ? 1 : 2) / 16f;
        final float downOffset = (down ? 0 : 2) / 16f;
        final float upOffset = (up ? 0 : 2) / 16f;
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

        drawPanelCube(poseStack, buffer, materialTexture.sprite(), packedLight, packedOverlay, 0f, 0f, 0f, rightOffset, 1f, 1f, normal, back, bottom);
        drawPanelCube(poseStack, buffer, materialTexture.sprite(), packedLight, packedOverlay, 1 - leftOffset, 0f, 0f, 1f, 1f, 1f, normal, back, bottom);

        if (!down)
        {
            drawPanelCube(poseStack, buffer, materialTexture.sprite(), packedLight, packedOverlay, rightOffset, 0f, 0f, 1 - leftOffset, PIXEL_WIDTH * 2, PIXEL_WIDTH * 2, normal, false, bottom);
        }
        if (!up)
        {
            drawPanelCube(poseStack, buffer, materialTexture.sprite(), packedLight, packedOverlay, rightOffset, PIXEL_WIDTH * 14, PIXEL_WIDTH * 14, 1 - leftOffset, 1f, 1f, normal, back, false);
        }
        if (cw != GreenhouseConnectable.PostSize.NONE)
        {
            drawSideWall(poseStack, buffer, materialTexture.sprite(), glassCorner.sprite(), packedLight, packedOverlay, PIXEL_WIDTH * 14, 0, PIXEL_WIDTH * (cw == GreenhouseConnectable.PostSize.THIN ? 15 : 14), 1f, 1 - 2 * WIDTH, 1f, normal);
        }
        if (ccw != GreenhouseConnectable.PostSize.NONE)
        {
            drawSideWall(poseStack, buffer, materialTexture.sprite(), glassCorner.sprite(), packedLight, packedOverlay, 0, 0, PIXEL_WIDTH * (ccw == GreenhouseConnectable.PostSize.THIN ? 15 : 14), PIXEL_WIDTH * 2, 1 - 2 * WIDTH, 1f, normal);

        }

        TextureAtlasSprite leftGlass = selectGlassTexture(left, down, up);
        RenderHelpers.renderTexturedQuads(poseStack, buffer, leftGlass, packedLight, packedOverlay, getGlassVertices(0.5f, 0 - WIDTH / 2 + downOffset, WIDTH / 2 + downOffset, 1 - leftOffset, 1 - WIDTH / 2 - upOffset, 1 + WIDTH / 2 - upOffset, upOffset, 1 - downOffset), 16, 16, 0, 0, 0, false);

        TextureAtlasSprite rightGlass = selectGlassTexture(right, down, up);
        RenderHelpers.renderTexturedQuads(poseStack, buffer, rightGlass, packedLight, packedOverlay, getGlassVertices(rightOffset, 0 - WIDTH / 2 + downOffset, WIDTH / 2 + downOffset, 0.5f, 1 - WIDTH / 2 - upOffset, 1 + WIDTH / 2 - upOffset, upOffset, 1 - downOffset), 16, 16, 0, 0, 0, false);

        poseStack.popPose();
        poseStack.popPose();
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

    private static void drawPanelCube(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite texture, int packedLight, int packedOverlay, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Vec3i normal, boolean back, boolean bottom)
    {
        RenderHelpers.renderTexturedQuads(poseStack, buffer, texture, packedLight, packedOverlay, getPanelSideVertices(minX, minY, minZ, maxX, maxY, maxZ, back, bottom), 16, 16, normal.getX(), 0, normal.getZ(), true);
        RenderHelpers.renderTexturedQuads(poseStack, buffer, texture, packedLight, packedOverlay, getPanelTopVertices(minX, minY, minZ, maxX, maxY, maxZ, back, bottom), 16, 16, 0, 1, 0, true);
        RenderHelpers.renderTexturedQuads(poseStack, buffer, texture, packedLight, packedOverlay, getPanelEndVertices(minX, minY, minZ, maxX, maxY, maxZ, back, bottom), 16, 16, normal.getZ(), 0, normal.getX(), true);
    }

    private static float[][] getPanelSideVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean back, boolean bottom)
    {
        float p2minY = minY - (bottom ? 0 : WIDTH);
        float p2maxY = maxY - (back ? WIDTH * 2 : WIDTH);
        float p2minZ = minY + (bottom ? WIDTH * 2 : WIDTH);
        float p2maxZ = maxY + (back ? 0 : WIDTH);
        float u0 = 1 - minX;
        float u1 = 1 - maxX;
        float v0 = 1 - maxY;
        float v1 = 1 - minY;
        float backVOffset = back ? WIDTH : 0;
        float bottomVOffset = bottom ? WIDTH : 0;
        return new float[][] {
            // Left face
            {maxX, maxY, maxZ, u0, v0, 1.0F},
            {maxX, p2maxY, p2maxZ, u1, v0 + backVOffset, 1.0F},
            {maxX, p2minY, p2minZ, u1, v1 - bottomVOffset, 1.0F},
            {maxX, minY, minZ, u0, v1, 1.0F},

            // Right face
            {minX, minY, minZ, u0, v1, -1.0F},
            {minX, p2minY, p2minZ, u1, v1 - bottomVOffset, -1.0F},
            {minX, p2maxY, p2maxZ, u1, v0 + backVOffset, -1.0F},
            {minX, maxY, maxZ, u0, v0, -1.0F}
        };
    }

    private static float[][] getPanelTopVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean back, boolean bottom)
    {
        float p2minY = minY - (bottom ? 0 : WIDTH);
        float p2maxY = maxY - (back ? WIDTH * 2 : WIDTH);
        float p2minZ = minY + (bottom ? WIDTH * 2 : WIDTH);
        float p2maxZ = maxY + (back ? 0 : WIDTH);
        float u0 = 1 - minX;
        float u1 = 1 - maxX;
        float v0 = 1 - maxY;
        float v1 = 1 - minY;
        float backVOffset = back ? WIDTH : 0;
        float bottomVOffset = bottom ? WIDTH : 0;
        return new float[][] {
            // Front face
            {maxX, maxY, maxZ, u1, v0, 1.0F},
            {maxX, minY, minZ, u1, v1, 1.0F},
            {minX, minY, minZ, u0, v1, 1.0F},
            {minX, maxY, maxZ, u0, v0, 1.0F},

            // Back face
            {minX, p2maxY, p2maxZ, u0, v0 + backVOffset, -1.0F},
            {minX, p2minY, p2minZ, u0, v1 - bottomVOffset, -1.0F},
            {maxX, p2minY, p2minZ, u1, v1 - bottomVOffset, -1.0F},
            {maxX, p2maxY, p2maxZ, u1, v0 + backVOffset, -1.0F}
        };
    }

    private static float[][] getPanelEndVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, boolean back, boolean bottom)
    {
        float p2minY = minY - (bottom ? 0 : WIDTH);
        float p2maxY = maxY - (back ? WIDTH * 2 : WIDTH);
        float p2minZ = minY + (bottom ? WIDTH * 2 : WIDTH);
        float p2maxZ = maxY + (back ? 0 : WIDTH);
        float u0 = 1 - minX;
        float u1 = 1 - maxX;
        float v0 = 0;
        float v1 = PIXEL_WIDTH * 2;
        return new float[][] {
            // Bottom ending face
            {maxX, minY, minZ, u1, v0, -1.0F},
            {maxX, p2minY, p2minZ, u1, v1, -1.0F},
            {minX, p2minY, p2minZ, u0, v1, -1.0F},
            {minX, minY, minZ, u0, v0, -1.0F},

            // Top ending face
            {minX, maxY, maxZ, u0, 1 - v0, 1.0F},
            {minX, p2maxY, p2maxZ, u0, 1 - v1, 1.0F},
            {maxX, p2maxY, p2maxZ, u1, 1 - v1, 1.0F},
            {maxX, maxY, maxZ, u1, 1 - v0, 1.0F}
        };
    }

    private static float[][] getGlassVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float v0, float v1)
    {
        float u0 = 1 - minX;
        float u1 = 1 - maxX;
        return new float[][] {
            {maxX, maxY, maxZ, u1, v0, 1.0F},
            {maxX, minY, minZ, u1, v1, 1.0F},
            {minX, minY, minZ, u0, v1, 1.0F},
            {minX, maxY, maxZ, u0, v0, 1.0F},

            {minX, maxY, maxZ, u0, v0, 1.0F},
            {minX, minY, minZ, u0, v1, 1.0F},
            {maxX, minY, minZ, u1, v1, 1.0F},
            {maxX, maxY, maxZ, u1, v0, 1.0F}
        };
    }

    private static void drawSideWall(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite texture, TextureAtlasSprite glass, int packedLight, int packedOverlay, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, Vec3i normal)
    {
        RenderHelpers.renderTexturedQuads(poseStack, buffer, texture, packedLight, packedOverlay, getSideWallXVertices(minX, minY, minZ, maxX, maxY, maxZ), 16, 16, normal.getX(), 0, normal.getZ(), true);
        RenderHelpers.renderTexturedQuads(poseStack, buffer, texture, packedLight, packedOverlay, getSideWallZVertices(minX, minY, minZ, maxX, maxY, maxZ), 16, 16, normal.getZ(), 0, normal.getX(), true);
        float postWidth = maxZ - minZ;
        RenderHelpers.renderTexturedQuads(poseStack, buffer, glass, packedLight, packedOverlay, getGlassSideVertices(minX + PIXEL_WIDTH, minY, minZ - PIXEL_WIDTH, maxY - postWidth, minZ), 16, 16, 0, 0, 0, false);
    }

    public static float[][] getSideWallXVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        float u0 = minZ;
        float u1 = maxZ;
        float v0 = 1 - minY;
        float v1 = 1 - maxY;
        float slopeOffset = maxZ - minZ;
        return new float[][] {
            {minX, minY, minZ, u0, v0, 1.0F},
            {minX, minY, maxZ, u1, v0, 1.0F},
            {minX, maxY, maxZ, u1, v1, 1.0F},
            {minX, maxY - slopeOffset, minZ, u0, v1 + slopeOffset, 1.0F},

            {maxX, minY, maxZ, 1 - u1, v0, -1.0F},
            {maxX, minY, minZ, 1 - u0, v0, -1.0F},
            {maxX, maxY - slopeOffset, minZ, 1 - u0, v1 + slopeOffset, -1.0F},
            {maxX, maxY, maxZ, 1 - u1, v1, -1.0F}
        };
    }

    public static float[][] getSideWallZVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        float u0 = 1 - maxX;
        float u1 = 1 - minX;
        float v0 = 1 - maxY;
        float v1 = 1 - minY;
        float slopeOffset = maxZ - minZ;
        return new float[][] {
            {maxX, minY, minZ, u0, v1, 1.0F},
            {minX, minY, minZ, u1, v1, 1.0F},
            {minX, maxY - slopeOffset, minZ, u1, v0 + slopeOffset, 1.0F},
            {maxX, maxY - slopeOffset, minZ, u0, v0 + slopeOffset, 1.0F},
            {minX, minY, maxZ, u1, v1, -1.0F},
            {maxX, minY, maxZ, u0, v1, -1.0F},
            {maxX, maxY, maxZ, u0, v0, -1.0F},
            {minX, maxY, maxZ, u1, v0, -1.0F}
        };
    }

    public static float[][] getGlassSideVertices(float xPos, float minY, float minZ, float maxY, float maxZ)
    {
        float u0 = 0;
        float u1 = PIXEL_WIDTH;
        float v0 = 1 - minY;
        float v1 = 1 - maxY;
        return new float[][] {
            // Vertical edge
            {xPos, minY, minZ, u0, v0, 1.0F},
            {xPos, minY, maxZ, u1, v0, 1.0F},
            {xPos, maxY, maxZ, u1, v1, 1.0F},
            {xPos, maxY - PIXEL_WIDTH, minZ, u0, v1 + PIXEL_WIDTH, 1.0F},

            {xPos, minY, maxZ, 1 - u1, v0, -1.0F},
            {xPos, minY, minZ, 1 - u0, v0, -1.0F},
            {xPos, maxY - PIXEL_WIDTH, minZ, 1 - u0, v1 + PIXEL_WIDTH, -1.0F},
            {xPos, maxY, maxZ, 1 - u1, v1, -1.0F},

            // Diagonal edge
            {xPos, -WIDTH, WIDTH, 0, 0, -1.0F},
            {xPos, -WIDTH - PIXEL_WIDTH, WIDTH, 0, PIXEL_WIDTH, -1.0F},
            {xPos, maxY - PIXEL_WIDTH * 2, maxZ - PIXEL_WIDTH, 1, PIXEL_WIDTH, -1.0F},
            {xPos, maxY - PIXEL_WIDTH, maxZ - PIXEL_WIDTH, 1, 0, -1.0F},

            {xPos, maxY - PIXEL_WIDTH, maxZ - PIXEL_WIDTH, 1, 0, -1.0F},
            {xPos, maxY - PIXEL_WIDTH * 2, maxZ - PIXEL_WIDTH, 1, PIXEL_WIDTH, -1.0F},
            {xPos, -WIDTH - PIXEL_WIDTH, WIDTH, 0, PIXEL_WIDTH, -1.0F},
            {xPos, -WIDTH, WIDTH, 0, 0, -1.0F},
        };
    }
}
