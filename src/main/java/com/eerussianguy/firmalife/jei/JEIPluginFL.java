package com.eerussianguy.firmalife.jei;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import com.eerussianguy.firmalife.init.KnappingFL;
import com.eerussianguy.firmalife.init.RegistriesFL;
import com.eerussianguy.firmalife.jei.category.DryingRecipeCategory;
import com.eerussianguy.firmalife.jei.category.OvenRecipeCategory;
import com.eerussianguy.firmalife.jei.wrapper.DryingRecipeWrapper;
import com.eerussianguy.firmalife.jei.wrapper.KnappingRecipeWrapperFL;
import com.eerussianguy.firmalife.jei.wrapper.OvenRecipeWrapper;
import com.eerussianguy.firmalife.registry.BlocksFL;
import com.eerussianguy.firmalife.registry.ItemsFL;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.compat.jei.categories.KnappingCategory;
import net.dries007.tfc.compat.jei.wrappers.KnappingRecipeWrapper;
import net.dries007.tfc.compat.jei.wrappers.SimpleRecipeWrapper;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@JEIPlugin
public class JEIPluginFL implements IModPlugin
{
    public static final String OVEN_ID = MOD_ID + ".oven";
    public static final String DRY_ID = MOD_ID + ".drying";
    public static final String KNAP_PUMPKIN_UID = MOD_ID + ".knap.pumpkin";


    private static IModRegistry REGISTRY;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        registry.addRecipeCategories(new OvenRecipeCategory(registry.getJeiHelpers().getGuiHelper(), OVEN_ID));
        registry.addRecipeCategories(new DryingRecipeCategory(registry.getJeiHelpers().getGuiHelper(), DRY_ID));
        registry.addRecipeCategories(new KnappingCategory(registry.getJeiHelpers().getGuiHelper(), KNAP_PUMPKIN_UID));
    }

    @Override
    public void register(IModRegistry registry)
    {
        REGISTRY = registry;

        List<SimpleRecipeWrapper> ovenList = RegistriesFL.OVEN.getValuesCollection().stream().map(OvenRecipeWrapper::new).collect(Collectors.toList());
        registry.addRecipes(ovenList, OVEN_ID);
        registry.addRecipeCatalyst(new ItemStack(BlocksFL.OVEN), OVEN_ID);

        List<SimpleRecipeWrapper> dryList = RegistriesFL.DRYING.getValuesCollection().stream().map(DryingRecipeWrapper::new).collect(Collectors.toList());
        registry.addRecipes(dryList, DRY_ID);
        registry.addRecipeCatalyst(new ItemStack(BlocksFL.LEAF_MAT, 1), DRY_ID);

        registry.addIngredientInfo(new ItemStack(ItemsFL.FRUIT_LEAF, 1), VanillaTypes.ITEM, new TextComponentTranslation("jei.tooltip.firmalife.fruit_leaf").getFormattedText());
        registry.addIngredientInfo(new ItemStack(ItemsFL.COCOA_POWDER, 1), VanillaTypes.ITEM, new TextComponentTranslation("jei.tooltip.firmalife.cocoa_powder").getFormattedText());

        // Pumpkin Knapping
        List<KnappingRecipeWrapper> pumpkinknapRecipes = TFCRegistries.KNAPPING.getValuesCollection().stream()
            .filter(recipe -> recipe.getType() == KnappingFL.PUMPKIN)
            .map(recipe -> new KnappingRecipeWrapperFL(recipe, registry.getJeiHelpers().getGuiHelper()))
            .collect(Collectors.toList());

        registry.addRecipes(pumpkinknapRecipes, KNAP_PUMPKIN_UID);
        registry.addRecipeCatalyst(new ItemStack(Item.getItemFromBlock(BlocksFL.PUMPKIN_FRUIT)), KNAP_PUMPKIN_UID);
    }
}
