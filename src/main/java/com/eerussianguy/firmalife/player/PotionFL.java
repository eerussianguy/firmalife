package com.eerussianguy.firmalife.player;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public abstract class PotionFL extends Potion
{
    private static final ResourceLocation POTION_ICONS = new ResourceLocation(MOD_ID, "textures/gui/icons/potion.png");

    protected PotionFL(boolean isBadEffectIn, int liquidColorIn)
    {
        super(isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean isReady(int duration, int amplifier)
    {
        return true;
    }

    @Override
    public boolean isInstant()
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex()
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(POTION_ICONS);

        return super.getStatusIconIndex();
    }
}
