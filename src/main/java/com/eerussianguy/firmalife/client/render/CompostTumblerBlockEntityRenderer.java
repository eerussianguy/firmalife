package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.CompostTumblerBlockEntity;
import com.eerussianguy.firmalife.common.blocks.CompostTumblerBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;

public class CompostTumblerBlockEntityRenderer implements BlockEntityRenderer<CompostTumblerBlockEntity>
{
    public static final ResourceLocation CLOSED_MODEL = FLHelpers.identifier("block/compost_tumbler_closed");
    public static final ResourceLocation OPEN_MODEL = FLHelpers.identifier("block/compost_tumbler_open");

    private static final ResourceLocation NORMAL_TEXTURE = Helpers.identifier("block/devices/composter/normal");
    private static final ResourceLocation READY_TEXTURE = Helpers.identifier("block/devices/composter/ready");
    private static final ResourceLocation ROTTEN_TEXTURE = Helpers.identifier("block/devices/composter/rotten");

    @Override
    public void render(CompostTumblerBlockEntity composter, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        final Level level = composter.getLevel();
        final BlockPos pos = composter.getBlockPos();
        final BlockState state = composter.getBlockState();

        if (level == null || !(state.getBlock() instanceof CompostTumblerBlock))
        {
            return;
        }

        final Direction facing = state.getValue(CompostTumblerBlock.FACING);

        final boolean isStill = composter.getRotationNode().rotation() == null;
        final float angle = !isStill ? composter.getRotationNode().rotation().angle(partialTicks) : 0f;

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - 90f * facing.get2DDataValue()));
        poseStack.translate(-0.5f, -0.5f, -0.5f);

        if (!isStill)
        {
            poseStack.translate(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(Axis.ZP.rotation(-angle));
            poseStack.translate(-0.5f, -0.5f, -0.5f);
        }

        final ModelBlockRenderer modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        final BakedModel baked = Minecraft.getInstance().getModelManager().getModel(isStill ? OPEN_MODEL : CLOSED_MODEL);
        final VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());

        if (isStill)
        {
            modelRenderer.tesselateWithAO(level, baked, state, pos, poseStack, buffer, true, level.getRandom(), combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.cutout());

            ResourceLocation texture = NORMAL_TEXTURE;
            if (composter.isRotten())
            {
                texture = ROTTEN_TEXTURE;
            }
            else if (composter.isReady())
            {
                texture = READY_TEXTURE;
            }

            final TextureAtlasSprite sprite = RenderHelpers.blockTexture(texture);
            final int total = composter.getTotal();

            float height = 0f;
            if (total > 0)
            {
                height = 8f * total / CompostTumblerBlockEntity.MAX_COMPOST;
            }
            else
            {
                final IItemHandler inv = Helpers.getCapability(composter, Capabilities.ITEM);
                if (inv != null)
                {
                    final ItemStack compost = inv.getStackInSlot(CompostTumblerBlockEntity.SLOT_COMPOST);
                    if (!compost.isEmpty())
                    {
                        height = 8f * compost.getCount() / 3f;
                    }
                }
            }
            if (height > 0f)
            {
                RenderHelpers.renderTexturedCuboid(poseStack, buffer, sprite, combinedLight, combinedOverlay, 4 / 16f, 3 / 16f, 4 / 16f, 12 / 16f, (height + 3)  / 16f, 15 / 16f);
            }

        }
        else
        {
            modelRenderer.renderModel(poseStack.last(), buffer, state, baked, 1f, 1f, 1f, combinedLight, combinedOverlay, ModelData.EMPTY, RenderType.cutout());
        }

        poseStack.popPose();

    }
}
