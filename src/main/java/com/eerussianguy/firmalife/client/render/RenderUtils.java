package com.eerussianguy.firmalife.client.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

public final class RenderUtils
{
    // todo: renderhelpers
    public static void renderTexturedCuboid(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, int packedLight, int packedOverlay, float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        renderTexturedCuboid(poseStack, buffer, sprite, packedLight, packedOverlay, minX, minY, minZ, maxX, maxY, maxZ, 16f * (maxX - minX), 16f * (maxY - minY), 16f * (maxZ - minZ));
    }

    // todo: renderhelpers
    public static void renderTexturedCuboid(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, int packedLight, int packedOverlay, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float xPixels, float yPixels, float zPixels)
    {
        renderTexturedQuads(poseStack, buffer, sprite, packedLight, packedOverlay, getXVertices(minX, minY, minZ, maxX, maxY, maxZ), zPixels, yPixels, 1, 0, 0);
        renderTexturedQuads(poseStack, buffer, sprite, packedLight, packedOverlay, getYVertices(minX, minY, minZ, maxX, maxY, maxZ), zPixels, xPixels, 0, 1, 0);
        renderTexturedQuads(poseStack, buffer, sprite, packedLight, packedOverlay, getZVertices(minX, minY, minZ, maxX, maxY, maxZ), xPixels, yPixels, 0, 0, 1);
    }

    // todo: renderhelpers
    public static void renderTexturedQuads(PoseStack poseStack, VertexConsumer buffer, TextureAtlasSprite sprite, int packedLight, int packedOverlay, float[][] vertices, float uSize, float vSize, float normalX, float normalY, float normalZ)
    {
        for (float[] v : vertices)
        {
            renderTexturedVertex(poseStack, buffer, packedLight, packedOverlay, v[0], v[1], v[2], sprite.getU(v[3] * uSize), sprite.getV(v[4] * vSize), v[5] * normalX, v[5] * normalY, v[5] * normalZ);
        }
    }

    // todo: renderhelpers
    public static float[][] getXVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new float[][] {
            {minX, minY, minZ, 0, 1, 1}, // +X
            {minX, minY, maxZ, 1, 1, 1},
            {minX, maxY, maxZ, 1, 0, 1},
            {minX, maxY, minZ, 0, 0, 1},

            {maxX, minY, maxZ, 1, 0, -1}, // -X
            {maxX, minY, minZ, 0, 0, -1},
            {maxX, maxY, minZ, 0, 1, -1},
            {maxX, maxY, maxZ, 1, 1, -1}
        };
    }

    // todo: renderhelpers
    public static float[][] getYVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new float[][] {
            {minX, maxY, minZ, 0, 1, 1}, // +Y
            {minX, maxY, maxZ, 1, 1, 1},
            {maxX, maxY, maxZ, 1, 0, 1},
            {maxX, maxY, minZ, 0, 0, 1},

            {minX, minY, maxZ, 1, 0, -1}, // -Y
            {minX, minY, minZ, 0, 0, -1},
            {maxX, minY, minZ, 0, 1, -1},
            {maxX, minY, maxZ, 1, 1, -1}
        };
    }

    // todo: renderhelpers
    public static float[][] getZVertices(float minX, float minY, float minZ, float maxX, float maxY, float maxZ)
    {
        return new float[][] {
            {maxX, minY, minZ, 0, 1, 1}, // +Z
            {minX, minY, minZ, 1, 1, 1},
            {minX, maxY, minZ, 1, 0, 1},
            {maxX, maxY, minZ, 0, 0, 1},

            {minX, minY, maxZ, 1, 0, -1}, // -Z
            {maxX, minY, maxZ, 0, 0, -1},
            {maxX, maxY, maxZ, 0, 1, -1},
            {minX, maxY, maxZ, 1, 1, -1}
        };
    }

    public static void renderCross(float x, float z, float y1, float y2, PoseStack poseStack, VertexConsumer buffer, int combinedLight, int combinedOverlay, TextureAtlasSprite sprite, float u1, float v1, float u2, float v2)
    {
        for (float[] ver : getCrossHalfVertices(x, y1, x, z, y2, z, u1, v1, u2, v2))
        {
            renderTexturedVertex(poseStack, buffer, combinedLight, combinedOverlay, ver[0], ver[1], ver[2], sprite.getU(ver[3] * 16), sprite.getV(16 - (ver[4] * 16)), -1.414f, 0f, -1.414f);
        }

        for (float[] ver : getCrossHalfVertices(x, y1, z, z, y2, x, u1, v1, u2, v2))
        {
            renderTexturedVertex(poseStack, buffer, combinedLight, combinedOverlay, ver[0], ver[1], ver[2], sprite.getU(ver[3] * 16), sprite.getV(16 - (ver[4] * 16)), 1.414f, 0f, 1.414f);
        }
    }

    public static float[][] getCrossHalfVertices(float x1, float y1, float z1, float x2, float y2, float z2, float u1, float v1, float u2, float v2)
    {
        return new float[][] {
            {x1, y1, z1, u1, v1},
            {x2, y1, z2, u2, v1},
            {x2, y2, z2, u2, v2},
            {x1, y2, z1, u1, v2},

            {x2, y1, z2, u2, v1},
            {x1, y1, z1, u1, v1},
            {x1, y2, z1, u1, v2},
            {x2, y2, z2, u2, v2}
        };
    }

    // todo: renderhelpers
    public static void renderTexturedVertex(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float x, float y, float z, float u, float v, float normalX, float normalY, float normalZ)
    {
        buffer.vertex(poseStack.last().pose(), x, y, z)
            .color(1f, 1f, 1f, 1f)
            .uv(u, v)
            .uv2(packedLight)
            .overlayCoords(packedOverlay)
            .normal(poseStack.last().normal(), normalX, normalY, normalZ)
            .endVertex();
    }
}
