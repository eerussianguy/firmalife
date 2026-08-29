package com.eerussianguy.firmalife.compat.emi.recipe;

import java.util.List;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.FillingArrowWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import com.eerussianguy.firmalife.common.recipes.VatRecipe;
import com.eerussianguy.firmalife.compat.emi.FLEmiPlugin;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.recipe.AutoLayoutRecipe;
import net.dries007.tfc.compat.emi.stack.EmiSizedIngredient;
import net.dries007.tfc.compat.emi.widgets.CyclingSlotWidget;
import net.dries007.tfc.compat.emi.widgets.ItemStackProviderWidget;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendars;

public class EmiVatRecipe extends AutoLayoutRecipe<VatRecipe>
{
    private static final int SEED_UNIQUE = 33554432;

    private final float temperature;
    private final int duration;

    private boolean hasItemInput;
    private @Nullable ItemStackProvider outputProvider;

    public EmiVatRecipe(ResourceLocation id, VatRecipe recipe)
    {
        super(FLEmiPlugin.VAT, id, recipe);
        temperature = recipe.getTemperature();
        duration = recipe.getDuration();
        init(recipe);
    }

    @Override
    protected void processRecipe(VatRecipe recipe)
    {
        final SizedIngredient inputItem = recipe.getInputItem();
        final ItemStackProvider output = recipe.getOutputItem().orElse(null);

        hasItemInput = !inputItem.ingredient().isEmpty();
        if (hasItemInput)
        {
            inputs.add(new EmiSizedIngredient(inputItem));
        }
        if (recipe.getInputFluid().amount() > 0)
        {
            inputs.add(EmiHelpers.toIngredient(recipe.getInputFluid()));
        }

        if (output != null)
        {
            if (output.dependsOnInput() && hasItemInput)
            {
                outputProvider = output;
            }
            else
            {
                final ItemStack stack = output.getEmptyStack();
                if (!stack.isEmpty())
                {
                    outputs.add(EmiHelpers.nonDecayStack(stack));
                }
            }
        }

        recipe.getOutputFluid()
            .filter(fluid -> !fluid.isEmpty())
            .ifPresent(fluid -> outputs.add(EmiStack.of(fluid.getFluid(), fluid.getAmount())));

        recipe.getJarOutput()
            .filter(stack -> !stack.isEmpty())
            .ifPresent(stack -> outputs.add(EmiHelpers.nonDecayStack(stack)));
    }

    @Override
    protected List<Widget> generateWidgets(VatRecipe recipe)
    {
        final WidgetLayout widgets = new WidgetLayout(new Bounds(getMargin() + getPaddingLeft(), getMargin() + getPaddingTop(), 0, 0));
        final int y = getMargin() + getPaddingTop();

        @Nullable SlotWidget itemInputSlot = null;
        int index = 0;
        for (EmiIngredient input : inputs)
        {
            final int x = widgets.last(Position.RIGHT, index == 0 ? 0 : 3);
            final SlotWidget slot = index == 0 && hasItemInput && outputProvider != null
                ? new CyclingSlotWidget(input, SEED_UNIQUE, x, y)
                : new SlotWidget(input, x, y);
            if (index == 0 && hasItemInput)
            {
                itemInputSlot = slot;
            }
            widgets.add(slot);
            index++;
        }

        widgets.add(new FillingArrowWidget(widgets.last(Position.RIGHT, 3), y, 3000));

        if (outputProvider != null && itemInputSlot != null)
        {
            widgets.add(new ItemStackProviderWidget(itemInputSlot, outputProvider, SEED_UNIQUE, widgets.last(Position.RIGHT, 3), y).recipeContext(this));
        }
        for (EmiStack output : outputs)
        {
            widgets.add(new SlotWidget(output, widgets.last(Position.RIGHT, 3), y).recipeContext(this));
        }

        return widgets;
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        super.addWidgets(widgets);

        final @Nullable Component heat = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(temperature);
        if (heat != null)
        {
            widgets.addText(heat, getDisplayWidth() / 2, getMargin() + 2, 0xffffffff, true)
                .horizontalAlign(TextWidget.Alignment.CENTER)
                .verticalAlign(TextWidget.Alignment.CENTER);
        }
        widgets.addText(Calendars.CLIENT.getTimeDelta(duration), getDisplayWidth() / 2, getDisplayHeight() - 2, 0xffffffff, true)
            .horizontalAlign(TextWidget.Alignment.CENTER)
            .verticalAlign(TextWidget.Alignment.END);
    }

    @Override
    public boolean supportsRecipeTree()
    {
        return outputProvider == null;
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiVatRecipe vat && duration != vat.duration)
        {
            return duration - vat.duration;
        }
        return super.compareTo(other);
    }

    @Override
    protected int getPaddingTop()
    {
        return 10;
    }

    @Override
    protected int getPaddingBottom()
    {
        return 10;
    }
}
