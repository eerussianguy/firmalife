package com.eerussianguy.firmalife.compat.jei;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.compat.jei.category.KnappingRecipeCategory;
import net.dries007.tfc.util.Helpers;

@JeiPlugin
public class FLJEIPlugin implements IModPlugin
{
    private static <C extends Container, T extends Recipe<C>> List<T> getRecipes(net.minecraft.world.item.crafting.RecipeType<T> type)
    {
        ClientLevel level = Minecraft.getInstance().level;
        assert level != null;
        return level.getRecipeManager().getAllRecipesFor(type);
    }

    private static <C extends Container, T extends Recipe<C>> List<T> getRecipes(net.minecraft.world.item.crafting.RecipeType<T> type, Predicate<T> filter)
    {
        return getRecipes(type).stream().filter(filter).collect(Collectors.toList());
    }

    private static void addCatalystTag(IRecipeCatalystRegistration r, TagKey<Item> tag, RecipeType<?> recipeType)
    {
        Helpers.getAllTagValues(tag, ForgeRegistries.ITEMS).forEach(item -> r.addRecipeCatalyst(new ItemStack(item), recipeType));
    }

    private static <T> RecipeType<T> type(String name, Class<T> tClass)
    {
        return RecipeType.create(FirmaLife.MOD_ID, name, tClass);
    }

    private static final ResourceLocation PUMPKIN_TEXTURE = Helpers.identifier("textures/gui/knapping/pumpkin.png");

    public static final RecipeType<DryingRecipe> DRYING = type("drying", DryingRecipe.class);
    public static final RecipeType<SmokingRecipe> SMOKING = type("smoking", SmokingRecipe.class);
    public static final RecipeType<MixingBowlRecipe> MIXING_BOWL = type("mixing_bowl", MixingBowlRecipe.class);
    public static final RecipeType<KnappingRecipe> PUMPKIN_KNAPPING = type("pumpkin_knapping", KnappingRecipe.class);
    public static final RecipeType<OvenRecipe> OVEN = type("oven", OvenRecipe.class);

    @Override
    public ResourceLocation getPluginUid()
    {
        return FLHelpers.identifier("jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration r)
    {
        IGuiHelper gui = r.getJeiHelpers().getGuiHelper();
        r.addRecipeCategories(new DryingCategory(DRYING, gui));
        r.addRecipeCategories(new SmokingCategory(SMOKING, gui));
        r.addRecipeCategories(new MixingCategory(MIXING_BOWL, gui));
        r.addRecipeCategories(new OvenCategory(OVEN, gui));
        r.addRecipeCategories(new KnappingRecipeCategory<>(PUMPKIN_KNAPPING, gui, new ItemStack(TFCBlocks.PUMPKIN.get()), PUMPKIN_TEXTURE, null));
    }

    @Override
    public void registerRecipes(IRecipeRegistration r)
    {
        r.addRecipes(DRYING, getRecipes(FLRecipeTypes.DRYING.get()));
        r.addRecipes(SMOKING, getRecipes(FLRecipeTypes.SMOKING.get()));
        r.addRecipes(MIXING_BOWL, getRecipes(FLRecipeTypes.MIXING_BOWL.get()));
        r.addRecipes(OVEN, getRecipes(FLRecipeTypes.OVEN.get()));
        r.addRecipes(PUMPKIN_KNAPPING, getRecipes(FLRecipeTypes.PUMPKIN_KNAPPING.get(), recipe -> recipe.getSerializer() == FLRecipeSerializers.PUMPKIN_KNAPPING.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration r)
    {
        cat(r, FLBlocks.DRYING_MAT, DRYING);
        cat(r, FLBlocks.SOLAR_DRIER, DRYING);
        cat(r, TFCItems.WOOL_YARN.get(), SMOKING);
        cat(r, FLBlocks.MIXING_BOWL, MIXING_BOWL);
        cat(r, FLItems.SPOON.get(), MIXING_BOWL);
        cat(r, TFCBlocks.PUMPKIN, PUMPKIN_KNAPPING);
        cat(r, FLBlocks.CURED_OVEN_BOTTOM, OVEN);
        cat(r, FLBlocks.CURED_OVEN_CHIMNEY, OVEN);
        cat(r, FLBlocks.CURED_OVEN_TOP, OVEN);
    }

    private static void cat(IRecipeCatalystRegistration r, Supplier<? extends Block> supplier, RecipeType<?> type)
    {
        r.addRecipeCatalyst(new ItemStack(supplier.get()), type);
    }

    private static void cat(IRecipeCatalystRegistration r, Item item, RecipeType<?> type)
    {
        r.addRecipeCatalyst(new ItemStack(item), type);
    }
}
