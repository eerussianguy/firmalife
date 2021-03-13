package com.eerussianguy.firmalife.compat.crafttweaker;

import com.blamejared.mtlib.helpers.InputHelper;
import com.eerussianguy.firmalife.init.RegistriesFL;
import com.eerussianguy.firmalife.recipe.DryingRecipe;
import com.eerussianguy.firmalife.recipe.NutRecipe;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;

@ZenClass("mods.firmalife.Nut")
@ZenRegister
@SuppressWarnings("unused")
public class CTNut {

    @ZenMethod
    public static void addRecipe(String recipe_name, IItemStack log, IItemStack leave, IItemStack output) {

        if (!(InputHelper.isABlock(log) && InputHelper.isABlock(leave))) throw new IllegalArgumentException("Input is not a block!");

        ItemStack log_stack = InputHelper.toStack(log);
        ItemStack leave_stack = InputHelper.toStack(leave);

        if(log_stack.getItem() instanceof ItemBlock && leave_stack.getItem() instanceof ItemBlock) {
            Block log_block = ((ItemBlock) log_stack.getItem()).getBlock();
            Block leave_block = ((ItemBlock) leave_stack.getItem()).getBlock();

            NutRecipe recipe = new NutRecipe(log_block, leave_block, InputHelper.toStack(output)).setRegistryName(recipe_name);
            CraftTweakerAPI.apply(new IAction() {
                @Override
                public void apply() {
                    RegistriesFL.NUT_TREES.register(recipe);
                }

                @Override
                public String describe() {
                    return "Adding Nut recipe " +recipe.getRegistryName().toString();
                }
            });
        }
    }

    @ZenMethod
    public static void removeRecipe(String recipe_name) {

        NutRecipe recipe = RegistriesFL.NUT_TREES.getValue(new ResourceLocation(recipe_name));

        if(recipe != null) {
            CraftTweakerAPI.apply(new IAction() {
                @Override
                public void apply() {
                    IForgeRegistryModifiable<NutRecipe> NUT = (IForgeRegistryModifiable<NutRecipe>) RegistriesFL.NUT_TREES;
                    NUT.remove(recipe.getRegistryName());
                }

                @Override
                public String describe() {
                    return "Removing Nut recipe " + recipe_name;
                }
            });
        }
    }

    @ZenMethod
    public static void removeRecipe(IItemStack output) {
        if (output == null) throw new IllegalArgumentException("Output not allowed to be empty");
        ArrayList<NutRecipe> removeList = new ArrayList<>();

        RegistriesFL.NUT_TREES.getValuesCollection()
                .stream()
                .filter(x -> x.getNut().isItemEqual(InputHelper.toStack(output)))
                .forEach(removeList::add);

        for(NutRecipe recipe : removeList) {
            CraftTweakerAPI.apply(new IAction() {
                @Override
                public void apply() {
                    IForgeRegistryModifiable<NutRecipe> NUT = (IForgeRegistryModifiable<NutRecipe>) RegistriesFL.NUT_TREES;
                    NUT.remove(recipe.getRegistryName());
                }

                @Override
                public String describe() {
                    return "Removing Drying recipe for output " + output.getDisplayName();
                }
            });
        }
    }
}
