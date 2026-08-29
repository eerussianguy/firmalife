package com.eerussianguy.firmalife.test.greenhouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.greenhouse.PlanterType;
import com.eerussianguy.firmalife.common.util.GreenhouseType;
import com.eerussianguy.firmalife.common.util.Plantable;
import com.eerussianguy.firmalife.test.TestSetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.util.Helpers;

import static org.junit.jupiter.api.Assertions.*;

public class GreenhouseTests implements TestSetup
{
    @Test
    public void testPlantableTexturesCoverEveryStage()
    {
        // getTexture() indexes textures by [0, stages], so a plantable with too few of them silently repeats a stage
        assertPlantables(
            (id, plant) -> plant.textures().size() == plant.stages() + 1 || plant.planter() == PlanterType.TRELLIS || plant.planter() == PlanterType.BONSAI,
            (id, plant) -> id + " has " + plant.textures().size() + " textures for " + plant.stages() + " stages",
            "Plantables with the wrong number of stage textures:"
        );
    }

    @Test
    public void testHangingPlantablesHaveSpecialTextures()
    {
        // Hanging planters render their fruit from the first special texture
        assertPlantables(
            (id, plant) -> plant.planter() != PlanterType.HANGING || !plant.specials().isEmpty(),
            (id, plant) -> id.toString(),
            "Hanging plantables with no special textures:"
        );
    }

    @Test
    public void testPlantableTiersAreReachable()
    {
        final int maxTier = GreenhouseType.MANAGER.getValues()
            .stream()
            .mapToInt(GreenhouseType::tier)
            .max()
            .orElseThrow();

        assertPlantables(
            (id, plant) -> plant.tier() <= maxTier,
            (id, plant) -> id + " requires tier " + plant.tier() + ", but the highest greenhouse tier is " + maxTier,
            "Plantables that no greenhouse can grow:"
        );
    }

    @Test
    public void testPlantableSeedsAndCropsAreValid()
    {
        // The seed is what a harvest returns, so it has to be plantable again itself
        assertPlantables(
            (id, plant) -> !plant.seed().isEmpty() && !plant.crop().isEmpty() && plant.ingredient().test(plant.seed()),
            (id, plant) -> id + " has seed " + plant.seed() + " and crop " + plant.crop(),
            "Plantables with an invalid seed or crop:"
        );
    }

    @Test
    public void testPlantableCropFoodsHaveFoodDefinitions()
    {
        // getCrop() stamps a creation date on the crop, which does nothing at all without a food definition
        assertPlantables(
            (id, plant) -> !Helpers.isItem(plant.crop(), TFCTags.Items.FOODS) || FoodCapability.getDefinition(plant.crop()) != null,
            (id, plant) -> id + " produces " + plant.crop(),
            "Plantables producing a food with no food definition:"
        );
    }

    @Test
    public void testPlantablesDoNotOverlap()
    {
        // Plantable#get returns the first match out of the cache, so an item accepted by two of them silently shadows one
        final Map<ResourceLocation, Plantable> plantables = Plantable.MANAGER.getElements();
        final List<String> errors = new ArrayList<>();

        for (Item item : plantables.values()
            .stream()
            .flatMap(plant -> Arrays.stream(plant.ingredient().getItems()))
            .map(ItemStack::getItem)
            .distinct()
            .toList())
        {
            final ItemStack stack = item.getDefaultInstance();
            final List<ResourceLocation> matches = plantables.entrySet()
                .stream()
                .filter(entry -> entry.getValue().ingredient().test(stack))
                .map(Map.Entry::getKey)
                .toList();

            if (matches.size() > 1)
            {
                errors.add(stack + " is accepted by " + matches.stream().map(Object::toString).collect(Collectors.joining(", ")));
            }
        }

        assertTrue(errors.isEmpty(), "Plantables accepting the same item:\n" + String.join("\n", errors));
    }

    @Test
    public void testGreenhouseBlocksHaveAGreenhouseType()
    {
        // A greenhouse block with no type fails the wall test in Mechanics#getGreenhouse, and the greenhouse never forms
        final List<String> errors = new ArrayList<>();

        FLBlocks.GREENHOUSE_BLOCKS.forEach((greenhouse, blocks) -> blocks.forEach((type, block) -> {
            if (GreenhouseType.get(block.get().defaultBlockState()) == null)
            {
                errors.add(greenhouse + " " + type);
            }
        }));

        assertTrue(errors.isEmpty(), "Greenhouse blocks with no greenhouse type:\n" + String.join("\n", errors));
    }

    private void assertPlantables(BiPredicate<ResourceLocation, Plantable> valid, PlantableError error, String message)
    {
        final List<String> errors = Plantable.MANAGER.getElements()
            .entrySet()
            .stream()
            .filter(entry -> !valid.test(entry.getKey(), entry.getValue()))
            .map(entry -> error.apply(entry.getKey(), entry.getValue()))
            .toList();

        assertTrue(errors.isEmpty(), message + "\n" + String.join("\n", errors));
    }

    @FunctionalInterface
    interface PlantableError
    {
        String apply(ResourceLocation id, Plantable plant);
    }
}
