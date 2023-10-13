package com.eerussianguy.firmalife.client.screen;

import com.eerussianguy.firmalife.common.blockentities.StovetopPotBlockEntity;
import com.eerussianguy.firmalife.common.container.StovetopPotContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import net.dries007.tfc.client.screen.BlockEntityScreen;
import net.dries007.tfc.client.screen.TFCContainerScreen;
import net.dries007.tfc.common.capabilities.heat.Heat;

public class StovetopPotScreen extends BlockEntityScreen<StovetopPotBlockEntity, StovetopPotContainer>
{
    public StovetopPotScreen(StovetopPotContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, INVENTORY_2x2);
    }

    @Override
    protected void renderLabels(GuiGraphics poseStack, int mouseX, int mouseY)
    {
        super.renderLabels(poseStack, mouseX, mouseY);
        Heat heat = Heat.getHeat(menu.getBlockEntity().getTemperature());
        if (heat != null)
        {
            drawCenteredLine(poseStack, heat.getDisplayName(), 64);
        }

        if (blockEntity.isBoiling())
        {
            drawDisabled(poseStack, 0, StovetopPotBlockEntity.SLOTS - 1);
        }
    }
}
