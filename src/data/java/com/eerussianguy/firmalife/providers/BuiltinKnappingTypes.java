package com.eerussianguy.firmalife.providers;

import java.util.concurrent.CompletableFuture;
import com.eerussianguy.firmalife.DataEntryPoint;
import com.eerussianguy.firmalife.common.FLTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.recipes.ingredients.AndIngredient;
import net.dries007.tfc.common.recipes.ingredients.NotRottenIngredient;
import net.dries007.tfc.util.data.KnappingType;

public class BuiltinKnappingTypes extends DataManagerProvider<KnappingType>
{
    public BuiltinKnappingTypes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(KnappingType.MANAGER, output, lookup, TerraFirmaCraft.MOD_ID);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add(DataEntryPoint.PUMPKIN, new KnappingType(
            new SizedIngredient(AndIngredient.of(Ingredient.of(FLTags.Items.PUMPKIN_KNAPPING), NotRottenIngredient.INSTANCE), 1),
            1, TFCSounds.KNAP_LEATHER.holder(), false, false, false, TFCBlocks.PUMPKIN.asItem().getDefaultInstance()
        ));
    }
}
