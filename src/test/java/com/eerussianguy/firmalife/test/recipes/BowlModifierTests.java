package com.eerussianguy.firmalife.test.recipes;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.recipes.OvenRecipe;
import com.eerussianguy.firmalife.common.recipes.data.AddPiePanModifier;
import com.eerussianguy.firmalife.common.recipes.data.CopyBowlModifier;
import com.eerussianguy.firmalife.common.recipes.data.CopyDynamicFoodModifier;
import com.eerussianguy.firmalife.common.recipes.data.EmptyPanModifier;
import com.eerussianguy.firmalife.test.TestSetup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.component.Bowl;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;
import net.dries007.tfc.util.Helpers;

import static com.eerussianguy.firmalife.test.TestAssertions.*;

public class BowlModifierTests implements TestSetup
{
    @Test
    public void testAddPiePan()
    {
        final ItemStack output = AddPiePanModifier.INSTANCE.apply(new ItemStack(FLItems.FILLED_PIE.get()), ItemStack.EMPTY, ItemStackModifier.Context.DEFAULT);

        assertEquals(new ItemStack(FLItems.PIE_PAN.get()), bowlOf(output));
    }

    @Test
    public void testCopyBowl()
    {
        final ItemStack bowl = new ItemStack(Items.BOWL);
        final ItemStack output = CopyBowlModifier.INSTANCE.apply(new ItemStack(Items.STICK), withBowl(new ItemStack(Items.STICK), bowl), ItemStackModifier.Context.DEFAULT);

        assertEquals(bowl, bowlOf(output));
    }

    @Test
    public void testCopyBowlWithEmptyBowlDefaultsToBowl()
    {
        final ItemStack input = new ItemStack(Items.STICK);
        input.set(TFCComponents.BOWL, new ItemComponent(ItemStack.EMPTY));

        final ItemStack output = CopyBowlModifier.INSTANCE.apply(new ItemStack(Items.STICK), input, ItemStackModifier.Context.DEFAULT);

        assertEquals(new ItemStack(Items.BOWL), bowlOf(output));
    }

    @Test
    public void testCopyBowlWithNoBowl()
    {
        final ItemStack output = CopyBowlModifier.INSTANCE.apply(new ItemStack(Items.STICK), new ItemStack(Items.STICK), ItemStackModifier.Context.DEFAULT);

        assertNull(output.get(TFCComponents.BOWL));
    }

    @Test
    public void testCopyDynamicFoodCopiesBowl()
    {
        final ItemStack pan = new ItemStack(FLItems.PIE_PAN.get());
        final ItemStack output = CopyDynamicFoodModifier.INSTANCE.apply(new ItemStack(cookedPie()), withBowl(new ItemStack(FLItems.FILLED_PIE.get()), pan), ItemStackModifier.Context.DEFAULT);

        assertEquals(pan, bowlOf(output));
    }

    @Test
    public void testEmptyPanReturnsInputPan()
    {
        final ItemStack pan = new ItemStack(FLItems.PIE_PAN.get());

        assertEquals(pan, emptyPan(withBowl(new ItemStack(cookedPie()), pan)));
    }

    @Test
    public void testEmptyPanReturnsInputBowl()
    {
        // Stinky soup is washable, and is served in a bowl rather than a pan
        final ItemStack bowl = new ItemStack(Items.BOWL);

        assertEquals(bowl, emptyPan(withBowl(new ItemStack(FLItems.STINKY_SOUP.get()), bowl)));
    }

    @Test
    public void testEmptyPanWithNoBowlDefaultsToPan()
    {
        assertEquals(new ItemStack(FLItems.PIE_PAN.get()), emptyPan(new ItemStack(cookedPie())));
    }

    @Test
    public void testEmptyPanDoesNotShareInputBowl()
    {
        final ItemStack input = withBowl(new ItemStack(cookedPie()), new ItemStack(FLItems.PIE_PAN.get()));
        final ItemStack output = emptyPan(input);

        output.grow(1);

        assertEquals(new ItemStack(FLItems.PIE_PAN.get()), bowlOf(input));
    }

    @Test
    public void testPieKeepsItsPanFromOvenToWashing()
    {
        final OvenRecipe cook = recipe("oven/cook_pie", OvenRecipe.class);
        final BarrelRecipe wash = recipe("barrel_instant/wash_foods", BarrelRecipe.class);
        final ItemStack pan = new ItemStack(FLItems.PIE_PAN.get());

        final ItemStack cooked = cook.assembleItem(withBowl(new ItemStack(FLItems.FILLED_PIE.get()), pan));

        assertEquals(pan, bowlOf(cooked));
        assertEquals(pan, wash.getOutputItem().getSingleStack(cooked));
    }

    private ItemStack emptyPan(ItemStack input)
    {
        return EmptyPanModifier.INSTANCE.apply(ItemStack.EMPTY, input, ItemStackModifier.Context.DEFAULT);
    }

    private ItemStack withBowl(ItemStack stack, ItemStack bowl)
    {
        stack.set(TFCComponents.BOWL, Bowl.of(bowl));
        return stack;
    }

    private ItemStack bowlOf(ItemStack stack)
    {
        final ItemComponent bowl = stack.get(TFCComponents.BOWL);
        assertNotNull(bowl, "No bowl on " + stack);
        return bowl.stack();
    }

    private Item cookedPie()
    {
        return FLItems.FOODS.get(FLFood.COOKED_PIE).get();
    }

    private <R extends Recipe<?>> R recipe(String path, Class<R> type)
    {
        final Recipe<?> recipe = Helpers.getUnsafeRecipeManager()
            .byKey(FLHelpers.identifier(path))
            .orElseThrow(() -> new AssertionError("No recipe " + path))
            .value();
        assertInstanceOf(type, recipe);
        return type.cast(recipe);
    }
}
