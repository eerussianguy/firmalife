package com.eerussianguy.firmalife.items;

import com.eerussianguy.firmalife.FirmaLife;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.CreativeTabsTFC;
import net.dries007.tfc.objects.items.ItemTFC;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemMiscFL extends ItemTFC {
    public ItemMiscFL(String name) {
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabsTFC.CT_MISC);
        this.setRegistryName(FirmaLife.MOD_ID, name);
        this.setTranslationKey(FirmaLife.MOD_ID+"."+name);
    }
    @Nonnull
    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.SMALL;
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.LIGHT;
    }
}
