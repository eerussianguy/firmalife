package com.eerussianguy.firmalife.client.screen;

import com.eerussianguy.firmalife.common.blockentities.KegBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.screen.BarrelScreen;
import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.network.ScreenButtonPacket;

public class KegSealButton extends Button
{
    private final KegBlockEntity keg;

    public KegSealButton(KegBlockEntity keg, int guiLeft, int guiTop, Component tooltip)
    {
        super(guiLeft + 34, guiTop + 78, 20, 20, tooltip, b -> {}, RenderHelpers.NARRATION);

        setTooltip(Tooltip.create(tooltip));
        this.keg = keg;
    }

    @Override
    public void onPress()
    {
        PacketDistributor.sendToServer(new ScreenButtonPacket(0));
        playDownSound(Minecraft.getInstance().getSoundManager());
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        final int v = keg.getBlockState().getValue(BarrelBlock.SEALED) ? 0 : 20;
        graphics.blit(BarrelScreen.BACKGROUND, getX(), getY(), 236, v, 20, 20, 256, 256);
    }
}
