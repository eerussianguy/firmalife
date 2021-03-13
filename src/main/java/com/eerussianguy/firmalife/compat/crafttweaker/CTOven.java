package com.eerussianguy.firmalife.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.eerussianguy.firmalife.init.RegistriesFL;
import com.eerussianguy.firmalife.recipe.CrackingRecipe;
import com.eerussianguy.firmalife.recipe.DryingRecipe;
import com.eerussianguy.firmalife.recipe.OvenRecipe;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import net.dries007.tfc.compat.crafttweaker.CTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;

@ZenClass("mods.firmalife.Oven")
@ZenRegister
@SuppressWarnings("unused")
public class CTOven {

    @ZenMethod
    public static void addRecipe(String recipe_name,IIngredient input, IItemStack output, int duration) {
        OvenRecipe recipe = new OvenRecipe(CTHelper.getInternalIngredient(input), InputHelper.toStack(output), duration).setRegistryName(recipe_name);
        CraftTweakerAPI.apply(new IAction() {
            @Override
            public void apply() {
                RegistriesFL.OVEN.register(recipe);
            }

            @Override
            public String describe() {
                return "Adding Oven recipe " + recipe_name;
            }
        });
    }

    @ZenMethod
    public static void removeRecipe(String recipe_name) {

        OvenRecipe recipe = RegistriesFL.OVEN.getValue(new ResourceLocation(recipe_name));

        if(recipe != null) {
            CraftTweakerAPI.apply(new IAction() {
                @Override
                public void apply() {
                    IForgeRegistryModifiable<OvenRecipe> Oven = (IForgeRegistryModifiable<OvenRecipe>) RegistriesFL.OVEN;
                    Oven.remove(recipe.getRegistryName());
                }

                @Override
                public String describe() {
                    return "Removing Oven recipe " + recipe_name;
                }
            });
        }
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        ArrayList<OvenRecipe> removeList = new ArrayList<>();

        RegistriesFL.OVEN.getValuesCollection()
                .stream()
                .filter(x -> x.getOutputItem(ItemStack.EMPTY).isItemEqual(InputHelper.toStack(output)))
                .forEach(removeList::add);

        for(OvenRecipe recipe : removeList) {
            CraftTweakerAPI.apply(new IAction() {
                @Override
                public void apply() {
                    IForgeRegistryModifiable<OvenRecipe> Oven = (IForgeRegistryModifiable<OvenRecipe>) RegistriesFL.OVEN;
                    Oven.remove(recipe.getRegistryName());
                }

                @Override
                public String describe() {
                    return "Removing Oven recipe for output " + output.getDisplayName();
                }
            });
        }
    }
}
