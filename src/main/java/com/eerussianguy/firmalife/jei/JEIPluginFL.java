package com.eerussianguy.firmalife.jei;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.soap.Text;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

import com.eerussianguy.firmalife.init.RegistriesFL;
import com.eerussianguy.firmalife.registry.ModRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.dries007.tfc.compat.jei.wrappers.SimpleRecipeWrapper;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@JEIPlugin
public class JEIPluginFL implements IModPlugin
{
    public static final String OVEN_ID = MOD_ID + ".oven";
    public static final String DRY_ID = MOD_ID + ".drying";

    private static IModRegistry REGISTRY;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        registry.addRecipeCategories(new OvenRecipeCategory(registry.getJeiHelpers().getGuiHelper(), OVEN_ID));
        registry.addRecipeCategories(new DryingRecipeCategory(registry.getJeiHelpers().getGuiHelper(), DRY_ID));
    }

    @Override
    public void register(IModRegistry registry)
    {
        REGISTRY = registry;

        List<SimpleRecipeWrapper> ovenList = RegistriesFL.OVEN.getValuesCollection().stream().map(OvenRecipeWrapper::new).collect(Collectors.toList());
        registry.addRecipes(ovenList, OVEN_ID);
        registry.addRecipeCatalyst(new ItemStack(ModRegistry.OVEN), OVEN_ID);

        List<SimpleRecipeWrapper> dryList = RegistriesFL.DRYING.getValuesCollection().stream().map(DryingRecipeWrapper::new).collect(Collectors.toList());
        registry.addRecipes(dryList, DRY_ID);
        registry.addRecipeCatalyst(new ItemStack(ModRegistry.LEAF_MAT, 1), DRY_ID);

        registry.addIngredientInfo(new ItemStack(ModRegistry.FRUIT_LEAF, 1), VanillaTypes.ITEM, new TextComponentTranslation("jei.tooltip.firmalife.fruit_leaf").getFormattedText());
        registry.addIngredientInfo(new ItemStack(ModRegistry.COCOA_POWDER, 1), VanillaTypes.ITEM, new TextComponentTranslation("jei.tooltip.firmalife.cocoa_powder").getFormattedText());
    }
}
