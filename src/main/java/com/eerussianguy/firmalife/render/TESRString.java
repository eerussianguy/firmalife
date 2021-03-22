package com.eerussianguy.firmalife.render;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import com.eerussianguy.firmalife.blocks.BlockString;
import com.eerussianguy.firmalife.te.TEString;

@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class TESRString extends TileEntitySpecialRenderer<TEString>
{
    @Override
    public void render(TEString te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        if (te.hasWorld())
        {
            IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (cap != null)
            {
                IBlockState state = te.getWorld().getBlockState(te.getPos());
                if (!(state.getBlock() instanceof BlockString)) return;
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 0.5D, y + 0.38D, z + 0.5D);
                GlStateManager.scale(0.5f, 0.5f, 0.5f);
                if (state.getValue(BlockString.AXIS) == EnumFacing.Axis.Z)
                    GlStateManager.rotate(90f, 0f, 1f, 0f);
                ItemStack item = cap.getStackInSlot(0);
                if (!item.isEmpty())
                    Minecraft.getMinecraft().getRenderItem().renderItem(item, ItemCameraTransforms.TransformType.FIXED);
                GlStateManager.popMatrix();
            }
        }
    }
}
