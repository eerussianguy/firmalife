package com.eerussianguy.firmalife.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;

import com.eerussianguy.firmalife.items.ItemMetalMalletMold;
import com.eerussianguy.firmalife.registry.ItemsFL;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.recipes.RecipeUtils;

public class UnmoldRecipeFL extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    private final NonNullList<Ingredient> input;
    private final ResourceLocation group;
    private final String type;
    private final float chance;

    private UnmoldRecipeFL(@Nullable ResourceLocation group, NonNullList<Ingredient> input, @Nonnull String type, float chance)
    {
        this.group = group;
        this.input = input;
        this.type = type;
        this.chance = chance;
    }

    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
    {
        boolean foundMold = false;

        for (int slot = 0; slot < inv.getSizeInventory(); ++slot)
        {
            ItemStack stack = inv.getStackInSlot(slot);
            if (!stack.isEmpty())
            {
                if (!(stack.getItem() instanceof ItemMetalMalletMold))
                {
                    return false;
                }

                ItemMetalMalletMold moldItem = (ItemMetalMalletMold) stack.getItem();
                IFluidHandler cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (!(cap instanceof IMoldHandler))
                {
                    return false;
                }

                IMoldHandler moldHandler = (IMoldHandler) cap;
                if (moldHandler.isMolten())
                {
                    return false;
                }

                Metal metal = moldHandler.getMetal();
                if (metal == null || !moldItem.getToolName().equals(this.type) || foundMold)
                {
                    return false;
                }

                foundMold = true;
            }
        }

        return foundMold;
    }

    @Nonnull
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack moldStack = null;

        for (int slot = 0; slot < inv.getSizeInventory(); ++slot)
        {
            ItemStack stack = inv.getStackInSlot(slot);
            if (!stack.isEmpty())
            {
                if (!(stack.getItem() instanceof ItemMetalMalletMold))
                {
                    return ItemStack.EMPTY;
                }

                ItemMetalMalletMold tmp = (ItemMetalMalletMold) stack.getItem();
                if (!tmp.getToolName().equals(this.type) || moldStack != null)
                {
                    return ItemStack.EMPTY;
                }

                moldStack = stack;
            }
        }

        if (moldStack != null)
        {
            IFluidHandler moldCap = moldStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (moldCap instanceof IMoldHandler)
            {
                IMoldHandler moldHandler = (IMoldHandler) moldCap;
                if (!moldHandler.isMolten() && moldHandler.getAmount() == 100)
                {
                    return this.getOutputItem(moldHandler);
                }
            }
        }

        return ItemStack.EMPTY;
    }

    public boolean canFit(int width, int height)
    {
        return true;
    }

    @Nonnull
    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    @Nonnull
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        for (int slot = 0; slot < inv.getSizeInventory(); ++slot)
        {
            ItemStack stack = inv.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemMetalMalletMold)
            {
                EntityPlayer player = ForgeHooks.getCraftingPlayer();
                if (!player.world.isRemote)
                {
                    stack = this.getMoldResult(stack);
                    if (!stack.isEmpty())
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, stack);
                    }
                    else
                    {
                        player.world.playSound(null, player.getPosition(), TFCSounds.CERAMIC_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }

        return ForgeHooks.defaultRecipeGetRemainingItems(inv);
    }

    @Nonnull
    public NonNullList<Ingredient> getIngredients()
    {
        return this.input;
    }

    public boolean isDynamic()
    {
        return true;
    }

    @Nonnull
    public String getGroup()
    {
        return this.group == null ? "" : this.group.toString();
    }

    public String getType()
    {
        return this.type;
    }

    public float getChance()
    {
        return this.chance;
    }

    public ItemStack getMoldResult(ItemStack moldIn)
    {
        return Constants.RNG.nextFloat() <= this.chance ? new ItemStack(moldIn.getItem()) : ItemStack.EMPTY;
    }

    public ItemStack getOutputItem(IMoldHandler moldHandler)
    {
        Metal m = moldHandler.getMetal();
        if (m != null)
        {
            ItemStack output = new ItemStack(ItemsFL.getMetalMalletHead(m));
            IItemHeat heat = output.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            if (heat != null)
            {
                heat.setTemperature(moldHandler.getTemperature());
            }

            return output;
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    public static class Factory implements IRecipeFactory
    {
        public Factory()
        {
        }

        public IRecipe parse(JsonContext context, JsonObject json)
        {
            NonNullList<Ingredient> ingredients = RecipeUtils.parseShapeless(context, json);
            String result = JsonUtils.getString(json, "result");
            String type = result.toLowerCase().split("_")[0];
            String group = JsonUtils.getString(json, "group", "");
            float chance = 0.0F;
            if (JsonUtils.hasField(json, "chance"))
            {
                chance = JsonUtils.getFloat(json, "chance");
            }
            return new UnmoldRecipeFL(group.isEmpty() ? new ResourceLocation(result) : new ResourceLocation(group), ingredients, type, chance);
        }
    }
}
