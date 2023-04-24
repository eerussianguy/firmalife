package com.eerussianguy.firmalife.client.render;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.JarringStationBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.items.JarsBlockItem;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;

public class JarringStationBlockEntityRenderer implements BlockEntityRenderer<JarringStationBlockEntity>
{
    public static final ResourceLocation EMPTY_JAR_LOCATION = FLHelpers.identifier("block/jar_1");

    private static final int[][] OFFSETS = {
        {1, 1},
        {1, 0},
        {0, 1},
        {-1, 0},
        {0, 0},
        {0, -1},
        {-1, -1},
        {-1, 1},
        {1, -1}
    };

    @Override
    public void render(JarringStationBlockEntity station, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int combinedLight, int combinedOverlay)
    {
        station.getCapability(Capabilities.ITEM).ifPresent(inv -> {
            for (int i = 0; i < JarringStationBlockEntity.SLOTS; i++)
            {
                final int[] offset = OFFSETS[i];
                poseStack.pushPose();
                poseStack.translate(-0.1f, 1f / 16, -0.1f);
                final float translate = 0.28f;
                poseStack.translate(offset[0] * translate, 0, offset[1] * translate);
                poseStack.scale(0.8f, 0.8f, 0.8f);
                final ItemStack item = inv.getStackInSlot(i);
                if (!item.isEmpty())
                {
                    final Minecraft mc = Minecraft.getInstance();
                    if (Helpers.isItem(item, FLItems.EMPTY_JAR.get()))
                    {
                        final BakedModel baked = mc.getModelManager().getModel(EMPTY_JAR_LOCATION);
                        final VertexConsumer buffer = buffers.getBuffer(RenderType.cutout());
                        mc.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffer, null, baked, 1f, 1f, 1f, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
                    }
                    else if (item.getItem() instanceof JarsBlockItem jars)
                    {
                        mc.getBlockRenderer().renderSingleBlock(jars.getBlock().defaultBlockState(), poseStack, buffers, combinedLight, combinedOverlay, EmptyModelData.INSTANCE);
                    }
                }
                poseStack.popPose();
            }
        });
        if (station.getPourTicks() > 0)
        {
            poseStack.pushPose();
            final FluidStack fs = new FluidStack(FLFluids.EXTRA_FLUIDS.get(ExtraFluid.FRUITY_FLUID).getSource(), 100);

            RenderHelpers.renderFluidFace(poseStack, fs, buffers, 1f / 16, 1f / 16, 15f / 16, 15f / 16, 11.5f / 16, combinedOverlay, combinedLight);
            poseStack.popPose();
        }
    }
}
