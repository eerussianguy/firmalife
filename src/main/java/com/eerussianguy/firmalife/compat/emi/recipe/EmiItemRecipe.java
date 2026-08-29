package com.eerussianguy.firmalife.compat.emi.recipe;

import java.util.List;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.FillingArrowWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

import net.dries007.tfc.common.recipes.ItemRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.recipe.AutoLayoutRecipe;
import net.dries007.tfc.compat.emi.widgets.CyclingSlotWidget;
import net.dries007.tfc.compat.emi.widgets.ItemStackProviderWidget;

/**
 * A simple {@code Ingredient -> ItemStackProvider} recipe, displayed as {@code input -> middle -> output}.
 * If the output depends on the input, the two slots cycle together, as TFC does for barrel recipes.
 */
public class EmiItemRecipe<T extends Recipe<?>> extends AutoLayoutRecipe<T>
{
    private static final int SEED_UNIQUE = 67108864;

    public static <T extends ItemRecipe> EmiItemRecipe<T> of(EmiRecipeCategory category, ResourceLocation id, T recipe)
    {
        return new EmiItemRecipe<>(category, id, recipe, recipe.getIngredient(), recipe.getResult());
    }

    protected final Ingredient ingredient;
    protected final ItemStackProvider provider;
    protected final boolean isStatic;
    private @Nullable SlotWidget inputSlot;

    public EmiItemRecipe(EmiRecipeCategory category, ResourceLocation id, T recipe, Ingredient ingredient, ItemStackProvider provider)
    {
        super(category, id, recipe);
        this.ingredient = ingredient;
        this.provider = provider;
        this.isStatic = !provider.dependsOnInput();
        init(recipe);
    }

    @Override
    protected void processRecipe(T recipe)
    {
        inputs.add(EmiIngredient.of(ingredient));
        outputs.add(EmiHelpers.nonDecayStack(provider.getEmptyStack()));
    }

    @Override
    protected List<Widget> generateWidgets(T recipe)
    {
        final WidgetLayout widgets = new WidgetLayout(new Bounds(getMargin() + getPaddingLeft(), getMargin() + getPaddingTop(), 0, 0));
        final int y = getMargin() + getPaddingTop();

        widgets.add(generateInputSlot(inputs.getFirst(), widgets.last(Position.RIGHT), y, 0));
        generateMiddleWidgets(widgets, y);
        widgets.add(generateOutputSlot(outputs.getFirst(), widgets.last(Position.RIGHT, 4), y, 0));

        return widgets;
    }

    protected void generateMiddleWidgets(WidgetLayout widgets, int y)
    {
        widgets.add(new FillingArrowWidget(widgets.last(Position.RIGHT, 4), y, 3000));
    }

    @Override
    protected SlotWidget generateInputSlot(EmiIngredient ingredient, int x, int y, int index)
    {
        if (!isStatic)
        {
            inputSlot = new CyclingSlotWidget(ingredient, SEED_UNIQUE, x, y);
            return inputSlot;
        }
        return super.generateInputSlot(ingredient, x, y, index);
    }

    @Override
    protected SlotWidget generateOutputSlot(EmiStack stack, int x, int y, int index)
    {
        if (!isStatic && inputSlot != null)
        {
            return new ItemStackProviderWidget(inputSlot, provider, SEED_UNIQUE, x, y).recipeContext(this);
        }
        return super.generateOutputSlot(stack, x, y, index);
    }

    @Override
    public boolean supportsRecipeTree()
    {
        return isStatic;
    }
}
