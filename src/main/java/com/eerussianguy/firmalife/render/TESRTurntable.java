package com.eerussianguy.firmalife.render;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import com.eerussianguy.firmalife.registry.ItemsFL;
import com.eerussianguy.firmalife.te.TETurntable;

@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class TESRTurntable extends TileEntitySpecialRenderer<TETurntable>
{
    public static final ItemStack[] ITEMS = {
        new ItemStack(ItemsFL.getUnused(0)),
        new ItemStack(ItemsFL.getUnused(1)),
        new ItemStack(ItemsFL.getUnused(2)),
        new ItemStack(ItemsFL.getUnused(3)),
        new ItemStack(ItemsFL.getUnused(4))
    };

    @Override
    public void render(TETurntable te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        if (te.hasWorld())
        {
            IBlockState state = te.getWorld().getBlockState(te.getPos());
            IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
            if (cap != null)
            {
                ItemStack stack = cap.getStackInSlot(0);
                if (!stack.isEmpty())
                {
                    GlStateManager.pushMatrix();
                    RenderHelper.enableStandardItemLighting();

                    GlStateManager.translate(x + 0.5F, y + 1.0F, z + 0.5F);
                    GlStateManager.scale(0.3F, 0.3F, 0.3F);
                    float timeD = (float) (360.0D * (double) (System.currentTimeMillis() & 16383L) / 16383.0D);
                    GlStateManager.rotate(timeD, 0.0F, 1.0F, 0.0F);

                    itemRenderer.renderItem(stack, ItemCameraTransforms.TransformType.FIXED);

                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.popMatrix();
                }
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
            GlStateManager.rotate((10F * te.getInternalProgress() - partialTicks), 0.0F, 1.0F, 0.0F);
            RenderHelper.enableStandardItemLighting();
            itemRenderer.renderItem(ITEMS[te.getBlockMetadata()], ItemCameraTransforms.TransformType.FIXED);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }
}
