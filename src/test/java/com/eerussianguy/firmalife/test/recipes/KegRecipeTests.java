package com.eerussianguy.firmalife.test.recipes;

import java.util.function.Consumer;
import com.eerussianguy.firmalife.common.blockentities.KegBlockEntity;
import com.eerussianguy.firmalife.common.recipes.KegRecipe;
import com.eerussianguy.firmalife.test.TestSetup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.blockentities.BarrelInventoryCallback;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.InstantBarrelRecipe;
import net.dries007.tfc.common.recipes.SealedBarrelRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

import static com.eerussianguy.firmalife.test.TestAssertions.*;

public class KegRecipeTests implements TestSetup
{
    private static final int FIRST = KegBlockEntity.SLOT_INPUT_START;

    @Test
    public void testSealedFluidOnly()
    {
        // i.e. milk -> curdled milk, which has no item input, and runs with the input slots empty
        final SealedBarrelRecipe recipe = sealed(b -> b.input(Fluids.WATER, 10).output(brine(), 10), 1000);
        final KegBlockEntity.KegInventory keg = keg(new FluidStack(Fluids.WATER, 1000));

        assertTrue(KegRecipe.matches(recipe, keg));
        KegRecipe.assemble(recipe, keg);

        assertEquals(new FluidStack(brine(), 1000), keg.getFluidInTank(0));
    }

    @Test
    public void testSealedFluidOnlyLeavesUnrelatedItems()
    {
        final SealedBarrelRecipe recipe = sealed(b -> b.input(Fluids.WATER, 10).output(brine(), 10), 1000);
        final KegBlockEntity.KegInventory keg = keg(new FluidStack(Fluids.WATER, 1000), new ItemStack(Items.COAL, 5));

        KegRecipe.assemble(recipe, keg);

        assertEquals(new FluidStack(brine(), 1000), keg.getFluidInTank(0));
        assertEquals(5, count(keg, Items.COAL));
    }

    @Test
    public void testSealedWithItemRetainsExcessFluid()
    {
        final SealedBarrelRecipe recipe = sealed(b -> b.input(Items.COAL).input(Fluids.WATER, 100).output(Items.CHARCOAL), 1000);
        final KegBlockEntity.KegInventory keg = keg(new FluidStack(Fluids.WATER, 1000), new ItemStack(Items.COAL, 4));

        assertTrue(KegRecipe.matches(recipe, keg));
        KegRecipe.assemble(recipe, keg);

        assertEquals(4, count(keg, Items.CHARCOAL));
        assertEquals(0, count(keg, Items.COAL));
        assertEquals(new FluidStack(Fluids.WATER, 600), keg.getFluidInTank(0));
    }

    @Test
    public void testSealedWithItemRetainsExcessItems()
    {
        final SealedBarrelRecipe recipe = sealed(b -> b.input(Items.COAL).input(Fluids.WATER, 100).output(Items.CHARCOAL), 1000);
        final KegBlockEntity.KegInventory keg = keg(new FluidStack(Fluids.WATER, 300), new ItemStack(Items.COAL, 5));

        KegRecipe.assemble(recipe, keg);

        assertEquals(3, count(keg, Items.CHARCOAL));
        assertEquals(2, count(keg, Items.COAL));
        assertTrue(keg.getFluidInTank(0).isEmpty());
    }

    @Test
    public void testSealedWithItemsAcrossSlotsConservesItems()
    {
        final SealedBarrelRecipe recipe = sealed(b -> b.input(new SizedIngredient(Ingredient.of(Items.COAL), 2)).input(Fluids.WATER, 100).output(Items.CHARCOAL), 1000);
        final KegBlockEntity.KegInventory keg = keg(new FluidStack(Fluids.WATER, 1000), new ItemStack(Items.COAL, 3), new ItemStack(Items.COAL, 3));

        KegRecipe.assemble(recipe, keg);

        // Each stack converts on its own, so 2 + 2 are used, and 1 + 1 are left over
        assertEquals(2, count(keg, Items.CHARCOAL));
        assertEquals(2, count(keg, Items.COAL));
        assertEquals(new FluidStack(Fluids.WATER, 800), keg.getFluidInTank(0));
    }

    @Test
    public void testSealedOutputFluidIsTrimmedToCapacity()
    {
        final SealedBarrelRecipe recipe = sealed(b -> b.input(Items.COAL).input(Fluids.WATER, 1).output(brine(), 1000), 1000);
        final KegBlockEntity.KegInventory keg = keg(new FluidStack(Fluids.WATER, 1000), new ItemStack(Items.COAL, 64), new ItemStack(Items.COAL, 64));

        KegRecipe.assemble(recipe, keg);

        final int converted = KegBlockEntity.CAPACITY / 1000;
        assertEquals(new FluidStack(brine(), KegBlockEntity.CAPACITY), keg.getFluidInTank(0));
        assertEquals(128 - converted, count(keg, Items.COAL));
    }

    @Test
    public void testInstantMatchesWhenBalanced()
    {
        final InstantBarrelRecipe recipe = instant(b -> b.input(Items.COAL).input(Fluids.WATER, 500).output(limewater(), 500));

        assertTrue(KegRecipe.matches(recipe, keg(new FluidStack(Fluids.WATER, 500), new ItemStack(Items.COAL, 1))));
        assertTrue(KegRecipe.matches(recipe, keg(new FluidStack(Fluids.WATER, 500), new ItemStack(Items.COAL, 2))));
        assertFalse(KegRecipe.matches(recipe, keg(new FluidStack(Fluids.WATER, 1000), new ItemStack(Items.COAL, 1))));
    }

    @Test
    public void testInstantCountsItemsAcrossSlots()
    {
        final InstantBarrelRecipe recipe = instant(b -> b.input(Items.COAL).input(Fluids.WATER, 500).output(limewater(), 500));

        assertTrue(KegRecipe.matches(recipe, keg(new FluidStack(Fluids.WATER, 1000), new ItemStack(Items.COAL, 1), new ItemStack(Items.COAL, 1))));
    }

    @Test
    public void testInstantWithOutputFluid()
    {
        final InstantBarrelRecipe recipe = instant(b -> b.input(Items.COAL).input(Fluids.WATER, 500).output(limewater(), 500));
        final KegBlockEntity.KegInventory keg = keg(new FluidStack(Fluids.WATER, 1000), new ItemStack(Items.COAL, 3));

        KegRecipe.assemble(recipe, keg);

        assertEquals(new FluidStack(limewater(), 1000), keg.getFluidInTank(0));
        assertEquals(1, count(keg, Items.COAL));
    }

    @Test
    public void testInstantWithNoOutputFluidAlwaysMatches()
    {
        // i.e. washing, where the fluid is used up but not converted
        final InstantBarrelRecipe recipe = instant(b -> b.input(Items.COAL).input(Fluids.WATER, 100).output(Items.CHARCOAL));
        final KegBlockEntity.KegInventory keg = keg(new FluidStack(Fluids.WATER, 1000), new ItemStack(Items.COAL, 3));

        assertTrue(KegRecipe.matches(recipe, keg));
        KegRecipe.assemble(recipe, keg);

        assertEquals(3, count(keg, Items.CHARCOAL));
        assertEquals(new FluidStack(Fluids.WATER, 700), keg.getFluidInTank(0));
    }

    @Test
    public void testDoesNotMatchWithoutItem()
    {
        final InstantBarrelRecipe recipe = instant(b -> b.input(Items.COAL).input(Fluids.WATER, 100).output(Items.CHARCOAL));

        assertFalse(KegRecipe.matches(recipe, keg(new FluidStack(Fluids.WATER, 1000))));
        assertFalse(KegRecipe.matches(recipe, keg(new FluidStack(Fluids.WATER, 1000), new ItemStack(Items.STICK))));
    }

    @Test
    public void testDoesNotMatchWithWrongFluid()
    {
        final InstantBarrelRecipe recipe = instant(b -> b.input(Items.COAL).input(Fluids.WATER, 100).output(Items.CHARCOAL));

        assertFalse(KegRecipe.matches(recipe, keg(new FluidStack(brine(), 1000), new ItemStack(Items.COAL))));
        assertFalse(KegRecipe.matches(recipe, keg(new FluidStack(Fluids.WATER, 50), new ItemStack(Items.COAL))));
    }

    @Test
    public void testInfiniteMatchesOnlyWithEnoughFluid()
    {
        final SealedBarrelRecipe recipe = infinite(b -> b.input(Items.COAL).input(Fluids.WATER, 100), Items.CHARCOAL, Items.COAL);

        assertTrue(KegRecipe.matches(recipe, keg(new FluidStack(Fluids.WATER, 200), new ItemStack(Items.COAL, 2))));
        assertFalse(KegRecipe.matches(recipe, keg(new FluidStack(Fluids.WATER, 100), new ItemStack(Items.COAL, 2))));
    }

    @Test
    public void testInfiniteSealAndUnseal()
    {
        final SealedBarrelRecipe recipe = infinite(b -> b.input(Ingredient.of(Items.COAL, Items.CHARCOAL)).input(Fluids.WATER, 100), Items.CHARCOAL, Items.COAL);
        final KegBlockEntity.KegInventory keg = keg(new FluidStack(Fluids.WATER, 1000), new ItemStack(Items.COAL, 2), new ItemStack(Items.STICK), new ItemStack(Items.COAL, 3));

        KegRecipe.onSealed(recipe, keg);

        assertEquals(5, count(keg, Items.CHARCOAL));
        assertEquals(0, count(keg, Items.COAL));
        assertEquals(new ItemStack(Items.STICK), keg.getStackInSlot(FIRST + 1));
        assertEquals(new FluidStack(Fluids.WATER, 1000), keg.getFluidInTank(0));

        KegRecipe.onUnsealed(recipe, keg);

        assertEquals(0, count(keg, Items.CHARCOAL));
        assertEquals(5, count(keg, Items.COAL));
        assertEquals(new ItemStack(Items.STICK), keg.getStackInSlot(FIRST + 1));
    }

    @Test
    public void testSealDoesNotCreateItemsInEmptySlots()
    {
        final SealedBarrelRecipe recipe = infinite(b -> b.input(Fluids.WATER, 100), Items.CHARCOAL, Items.COAL);
        final KegBlockEntity.KegInventory keg = keg(new FluidStack(Fluids.WATER, 1000));

        KegRecipe.onSealed(recipe, keg);
        KegRecipe.onUnsealed(recipe, keg);

        assertEquals(0, count(keg, Items.CHARCOAL));
        assertEquals(0, count(keg, Items.COAL));
    }

    private KegBlockEntity.KegInventory keg(FluidStack fluid, ItemStack... stacks)
    {
        // A sealed keg, as recipes must be able to modify the inventory regardless
        final KegBlockEntity.KegInventory keg = new KegBlockEntity.KegInventory((BarrelInventoryCallback) () -> false);
        keg.whileMutable(() -> {
            assertEquals(fluid.getAmount(), keg.fill(fluid.copy(), IFluidHandler.FluidAction.EXECUTE), "Fluid not accepted by keg: " + fluid);
            for (int i = 0; i < stacks.length; i++)
            {
                keg.setStackInSlot(FIRST + i, stacks[i].copy());
            }
        });
        return keg;
    }

    private int count(KegBlockEntity.KegInventory keg, Item item)
    {
        int count = 0;
        for (int i = KegBlockEntity.SLOT_INPUT_START; i <= KegBlockEntity.SLOT_INPUT_END; i++)
        {
            final ItemStack stack = keg.getStackInSlot(i);
            if (stack.is(item)) count += stack.getCount();
        }
        return count;
    }

    private SealedBarrelRecipe sealed(Consumer<BarrelRecipe.Builder> builder, int duration)
    {
        return build(builder, b -> b.sealed(duration));
    }

    private SealedBarrelRecipe infinite(Consumer<BarrelRecipe.Builder> builder, Item onSeal, Item onUnseal)
    {
        return build(builder, b -> b.sealed(ItemStackProvider.of(onSeal), ItemStackProvider.of(onUnseal)));
    }

    private InstantBarrelRecipe instant(Consumer<BarrelRecipe.Builder> builder)
    {
        return build(builder, BarrelRecipe.Builder::instant);
    }

    @SuppressWarnings("unchecked")
    private <R extends BarrelRecipe> R build(Consumer<BarrelRecipe.Builder> builder, Consumer<BarrelRecipe.Builder> finish)
    {
        final BarrelRecipe[] recipe = new BarrelRecipe[1];
        final BarrelRecipe.Builder b = new BarrelRecipe.Builder(r -> recipe[0] = r);
        builder.accept(b);
        finish.accept(b);
        return (R) recipe[0];
    }

    private Fluid brine()
    {
        return TFCFluids.SIMPLE_FLUIDS.get(SimpleFluid.BRINE).getSource();
    }

    private Fluid limewater()
    {
        return TFCFluids.SIMPLE_FLUIDS.get(SimpleFluid.LIMEWATER).getSource();
    }
}
