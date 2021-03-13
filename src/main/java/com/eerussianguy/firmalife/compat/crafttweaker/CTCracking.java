package com.eerussianguy.firmalife.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.eerussianguy.firmalife.init.RegistriesFL;
import com.eerussianguy.firmalife.recipe.CrackingRecipe;
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

@ZenClass("mods.firmalife.Cracking")
@ZenRegister
@SuppressWarnings("unused")
public class CTCracking {

    @ZenMethod
    public static void addRecipe(String recipe_name, IIngredient input, IItemStack output, float chance) {
        CrackingRecipe recipe = new CrackingRecipe(CTHelper.getInternalIngredient(input), InputHelper.toStack(output), chance).setRegistryName(recipe_name);
        CraftTweakerAPI.apply(new IAction() {
            @Override
            public void apply() {
                RegistriesFL.CRACKING.register(recipe);
            }

            @Override
            public String describe() {
                return "Adding Cracking recipe " +recipe.getRegistryName().toString();
            }
        });
    }

    @ZenMethod
    public static void removeRecipe(String recipe_name) {

        CrackingRecipe recipe = RegistriesFL.CRACKING.getValue(new ResourceLocation(recipe_name));

        if(recipe != null) {
            CraftTweakerAPI.apply(new IAction() {
                @Override
                public void apply() {
                    IForgeRegistryModifiable<CrackingRecipe> CRACKING = (IForgeRegistryModifiable<CrackingRecipe>) RegistriesFL.CRACKING;
                    CRACKING.remove(recipe.getRegistryName());
                }

                @Override
                public String describe() {
                    return "Removing Cracking recipe " + recipe_name;
                }
            });
        }
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        ArrayList<CrackingRecipe> removeList = new ArrayList<>();

        RegistriesFL.CRACKING.getValuesCollection()
                .stream()
                .filter(x -> x.getOutputItem(ItemStack.EMPTY).isItemEqual(InputHelper.toStack(output)))
                .forEach(removeList::add);

        for(CrackingRecipe recipe : removeList) {
            CraftTweakerAPI.apply(new IAction() {
                @Override
                public void apply() {
                    IForgeRegistryModifiable<CrackingRecipe> CRACKING = (IForgeRegistryModifiable<CrackingRecipe>) RegistriesFL.CRACKING;
                    CRACKING.remove(recipe.getRegistryName());
                }

                @Override
                public String describe() {
                    return "Removing Cracking recipe for output " + output.getDisplayName();
                }
            });
        }
    }
}
