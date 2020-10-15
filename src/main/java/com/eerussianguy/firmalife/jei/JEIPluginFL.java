package com.eerussianguy.firmalife.jei;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.init.Blocks;
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
import net.dries007.tfc.objects.blocks.BlocksTFC;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@JEIPlugin
public class JEIPluginFL implements IModPlugin
{
    public static final String OVEN_ID = MOD_ID + ".oven";

    private static IModRegistry REGISTRY;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        registry.addRecipeCategories(new OvenRecipeCategory(registry.getJeiHelpers().getGuiHelper(), OVEN_ID));
    }

    @Override
    public void register(IModRegistry registry)
    {
        REGISTRY = registry;

        List<SimpleRecipeWrapper> ovenList = RegistriesFL.OVEN.getValuesCollection().stream().map(OvenRecipeWrapper::new).collect(Collectors.toList());
        registry.addRecipes(ovenList, OVEN_ID);
        registry.addRecipeCatalyst(new ItemStack(ModRegistry.OVEN), OVEN_ID);

        registry.addIngredientInfo(new ItemStack(ModRegistry.FRUIT_LEAF, 1), VanillaTypes.ITEM, new TextComponentTranslation("jei.tooltip.firmalife.fruit_leaf").getFormattedText());
    }
}
