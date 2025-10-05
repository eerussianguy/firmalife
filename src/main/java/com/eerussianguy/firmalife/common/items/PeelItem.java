package com.eerussianguy.firmalife.common.items;

import com.eerussianguy.firmalife.client.render.PeelRenderer;
import cpw.mods.util.Lazy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class PeelItem extends Item
{
    public PeelItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.SPEAR;
    }

    public enum Extension implements IClientItemExtensions
    {
        INSTANCE;

        private final Lazy<PeelRenderer> renderer = Lazy.of(() -> new PeelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels()));

        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer()
        {
            return renderer.get();
        }
    }
}
