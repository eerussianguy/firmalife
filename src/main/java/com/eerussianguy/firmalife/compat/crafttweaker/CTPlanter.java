package com.eerussianguy.firmalife.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.eerussianguy.firmalife.init.RegistriesFL;
import com.eerussianguy.firmalife.recipe.PlanterRecipe;
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

@ZenClass("mods.firmalife.Planter")
@ZenRegister
@SuppressWarnings("unused")
public class CTPlanter {

    @ZenMethod
    public static void addRecipe(String recipe_name,IIngredient input, IItemStack output, int stage, boolean large) {
        addRecipe(recipe_name, input, output, stage, large, 0);
    }

    @ZenMethod
    public static void addRecipe(String recipe_name,IIngredient input, IItemStack output, int stage, boolean large, int tier) {
        PlanterRecipe recipe = new PlanterRecipe(CTHelper.getInternalIngredient(input), InputHelper.toStack(output), stage, large, tier).setRegistryName(recipe_name);
        CraftTweakerAPI.apply(new IAction() {
            @Override
            public void apply() {
                RegistriesFL.PLANTER_QUAD.register(recipe);
            }

            @Override
            public String describe() {
                return "Adding Planter recipe " + recipe_name;
            }
        });
    }

    @ZenMethod
    public static void removeRecipe(String recipe_name) {

        PlanterRecipe recipe = RegistriesFL.PLANTER_QUAD.getValue(new ResourceLocation(recipe_name));

        if(recipe != null) {
            CraftTweakerAPI.apply(new IAction() {
                @Override
                public void apply() {
                    IForgeRegistryModifiable<PlanterRecipe> Planter = (IForgeRegistryModifiable<PlanterRecipe>) RegistriesFL.PLANTER_QUAD;
                    Planter.remove(recipe.getRegistryName());
                }

                @Override
                public String describe() {
                    return "Removing Planter recipe " + recipe_name;
                }
            });
        }
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        ArrayList<PlanterRecipe> removeList = new ArrayList<>();

        RegistriesFL.PLANTER_QUAD.getValuesCollection()
                .stream()
                .filter(x -> x.getOutputItem(ItemStack.EMPTY).isItemEqual(InputHelper.toStack(output)))
                .forEach(removeList::add);

        for(PlanterRecipe recipe : removeList) {
            CraftTweakerAPI.apply(new IAction() {
                @Override
                public void apply() {
                    IForgeRegistryModifiable<PlanterRecipe> Planter = (IForgeRegistryModifiable<PlanterRecipe>) RegistriesFL.PLANTER_QUAD;
                    Planter.remove(recipe.getRegistryName());
                }

                @Override
                public String describe() {
                    return "Removing Planter recipe for output " + output.getDisplayName();
                }
            });
        }
    }
}
