package com.eerussianguy.firmalife.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.Herb;
import com.eerussianguy.firmalife.common.blocks.greenhouse.PlanterType;
import com.eerussianguy.firmalife.common.blocks.plant.FLFruitBlocks;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLFruit;
import com.eerussianguy.firmalife.common.util.Plantable;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.common.blocks.plant.fruit.SeasonalPlantBlock;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;

import static com.eerussianguy.firmalife.common.blocks.greenhouse.PlanterType.*;
import static net.dries007.tfc.common.blockentities.FarmlandBlockEntity.NutrientType.*;

public class BuiltinPlantables extends DataManagerProvider<Plantable>
{
    public BuiltinPlantables(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(Plantable.MANAGER, output, lookup, FirmaLife.MOD_ID);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        simple(Crop.BEET, POTASSIUM, QUAD, food(Food.BEET), 5);
        simple(Crop.CABBAGE, NITROGEN, QUAD, food(Food.CABBAGE), 5);
        simple(Crop.CARROT, POTASSIUM, QUAD, food(Food.CARROT), 4);
        simple(Crop.GARLIC, NITROGEN, QUAD, food(Food.GARLIC), 4);
        simple(Crop.POTATO, POTASSIUM, QUAD, food(Food.POTATO), 6);
        simple(Crop.ONION, NITROGEN, QUAD, food(Food.ONION), 6);
        simple(Crop.SOYBEAN, NITROGEN, QUAD, food(Food.SOYBEAN), 6);

        simple(Crop.MAIZE, PHOSPHOROUS, LARGE, food(Food.MAIZE), 4, 10, true);
        simple(Crop.BARLEY, NITROGEN, LARGE, food(Food.BARLEY), 7, 10, false);
        simple(Crop.OAT, PHOSPHOROUS, LARGE, food(Food.OAT), 7, 10, false);
        simple(Crop.RYE, PHOSPHOROUS, LARGE, food(Food.RYE), 7, 10, false);
        simple(Crop.WHEAT, PHOSPHOROUS, LARGE, food(Food.WHEAT), 7, 10, false);
        simple(Crop.RICE, PHOSPHOROUS, HYDROPONIC, food(Food.RICE), 7, 10, false);

        simple(Crop.JUTE, POTASSIUM, LARGE, TFCItems.JUTE, 4, 10, true);
        simple(Crop.PAPYRUS, POTASSIUM, LARGE, TFCItems.PAPYRUS, 5, 10, true);
        simple(Crop.GREEN_BEAN, NITROGEN, LARGE, food(Food.GREEN_BEAN), 4, 10, true);
        simple(Crop.TOMATO, POTASSIUM, LARGE, food(Food.TOMATO), 4, 10, true);
        simple(Crop.SUGARCANE, POTASSIUM, LARGE, food(Food.SUGARCANE), 4, 10, true);
        simple(Crop.RED_BELL_PEPPER, POTASSIUM, LARGE, food(Food.RED_BELL_PEPPER), 6, 10, false);
        simple(Crop.YELLOW_BELL_PEPPER, POTASSIUM, LARGE, food(Food.YELLOW_BELL_PEPPER), 6, 10, false);

        plantable("cranberry", TFCBlocks.CRANBERRY_BUSH.asItem(), HYDROPONIC, 10, 3, 0.5f, food(Food.CRANBERRY), PHOSPHOROUS, cropTextures(FirmaLife.MOD_ID, "cranberry", 4), List.of());

        plantable("red_grapes", FLItems.RED_GRAPE_SEEDS, TRELLIS, 15, 0, 0.5f, food(FLFruit.RED_GRAPES), NITROGEN, forEach(FirmaLife.MOD_ID, "block/crop/grape_", "leaves", "dead", "flowering", "red"), List.of());
        plantable("white_grapes", FLItems.WHITE_GRAPE_SEEDS, TRELLIS, 15, 0, 0.5f, food(FLFruit.WHITE_GRAPES), NITROGEN, forEach(FirmaLife.MOD_ID, "block/crop/grape_", "leaves", "dead", "flowering", "red"), List.of());

        for (FruitBlocks.Tree tree : FruitBlocks.Tree.values())
        {
            final SeasonalPlantBlock block = (SeasonalPlantBlock) TFCBlocks.FRUIT_TREE_LEAVES.get(tree).get();
            final ItemStack fruit = block.getProductItem(RandomSource.create());
            plantable(tree.getSerializedName(), TFCBlocks.FRUIT_TREE_SAPLINGS.get(tree).asItem(), BONSAI, 15, 0, 0.08f, fruit.getItem(), NITROGEN, forEach(TerraFirmaCraft.MOD_ID, "block/fruit_tree/" + tree.getSerializedName(), "_fruiting_leaves", "_dry_leaves", "_flowering_leaves", "_branch", "_leaves"), List.of());
        }
        for (FLFruitBlocks.Tree tree : FLFruitBlocks.Tree.values())
        {
            final SeasonalPlantBlock block = (SeasonalPlantBlock) FLBlocks.FRUIT_TREE_LEAVES.get(tree).get();
            final ItemStack fruit = block.getProductItem(RandomSource.create());
            plantable(tree.name().toLowerCase(Locale.ROOT), FLBlocks.FRUIT_TREE_SAPLINGS.get(tree).asItem(), BONSAI, 15, 0, 0.08f, fruit.getItem(), NITROGEN, forEach(FirmaLife.MOD_ID, "block/fruit_tree/" + tree.name().toLowerCase(Locale.ROOT), "_fruiting_leaves", "_dry_leaves", "_flowering_leaves", "_branch", "_leaves"), List.of());
        }
        for (FruitBlocks.StationaryBush bush : FruitBlocks.StationaryBush.values())
        {
            final SeasonalPlantBlock block = (SeasonalPlantBlock) TFCBlocks.STATIONARY_BUSHES.get(bush).get();
            final ItemStack fruit = block.getProductItem(RandomSource.create());
            final String name = bush.name().toLowerCase(Locale.ROOT);
            final String p = "block/berry_bush/" + name;
            plantable(name, TFCBlocks.STATIONARY_BUSHES.get(bush).asItem(), TRELLIS, 15, 0, 0.08f, fruit.getItem(), NITROGEN, forEachPrefix(TerraFirmaCraft.MOD_ID, "_bush", p + name, p + "dry_" + name, p + "flowering_" + name, p + "fruiting_" + name), List.of());
        }
        for (FLFruitBlocks.StationaryBush bush : FLFruitBlocks.StationaryBush.values())
        {
            final SeasonalPlantBlock block = (SeasonalPlantBlock) FLBlocks.STATIONARY_BUSHES.get(bush).get();
            final ItemStack fruit = block.getProductItem(RandomSource.create());
            final String name = bush.name().toLowerCase(Locale.ROOT);
            final String p = "block/berry_bush/" + name;
            plantable(name, FLBlocks.STATIONARY_BUSHES.get(bush).asItem(), TRELLIS, 15, 0, 0.08f, fruit.getItem(), NITROGEN, forEachPrefix(FirmaLife.MOD_ID, "_bush", p + name, p + "dry_" + name, p + "flowering_" + name, p + "fruiting_" + name), List.of());
        }

        for (Herb herb : Herb.values())
        {
            final String name = herb.name().toLowerCase(Locale.ROOT);
            final List<ResourceLocation> textures = new ArrayList<>();
            for (int i = 0; i <= 1; i++)
            {
                textures.add(ResourceLocation.fromNamespaceAndPath(FirmaLife.MOD_ID, "block/plant/" + name + "/" + i));
            }
            plantable(name, FLBlocks.HERBS.get(herb).asItem(), QUAD, 0, 1, 0.8f, FLBlocks.HERBS.get(herb).asItem(), NITROGEN, textures, List.of());
        }

        hanging("squash", TFCItems.CROP_SEEDS.get(Crop.SQUASH), food(Food.SQUASH), 0, POTASSIUM, 0.5f);
        hanging("pumpkin", TFCItems.CROP_SEEDS.get(Crop.PUMPKIN), TFCBlocks.PUMPKIN, 15, PHOSPHOROUS, 0.5f);
        hanging("melon", TFCItems.CROP_SEEDS.get(Crop.MELON), TFCBlocks.MELON, 15, PHOSPHOROUS, 0.5f);
        hanging("banana", TFCBlocks.BANANA_SAPLING, food(Food.BANANA), 15, NITROGEN, 0.08f);
    }

    private ItemLike food(Food food)
    {
        return TFCItems.FOOD.get(food);
    }

    private ItemLike food(FLFood food)
    {
        return FLItems.FOODS.get(food);
    }

    private ItemLike food(FLFruit food)
    {
        return FLItems.FRUITS.get(food);
    }

    private void simple(Crop crop, FarmlandBlockEntity.NutrientType type, PlanterType planter, ItemLike output, int stages)
    {
        simple(crop, type, planter, output, stages, 0, false);
    }

    private void simple(Crop crop, FarmlandBlockEntity.NutrientType type, PlanterType planter, ItemLike output, int stages, int tier, boolean firmalife)
    {
        final String name = crop.name().toLowerCase(Locale.ROOT);
        final List<ResourceLocation> textures = cropTextures(firmalife ? FirmaLife.MOD_ID : TerraFirmaCraft.MOD_ID, name, stages);
        add(name, new Plantable(Ingredient.of(TFCItems.CROP_SEEDS.get(crop)), planter, tier, stages, 0.5f, TFCItems.CROP_SEEDS.get(crop).get().getDefaultInstance(), output.asItem().getDefaultInstance(), type, textures, List.of()));
    }

    private void hanging(String name, ItemLike seed, ItemLike crop, int tier, FarmlandBlockEntity.NutrientType nut, float seedChance)
    {
        plantable(name, seed, HANGING, tier, 4, seedChance, seed, nut, forEach(FirmaLife.MOD_ID, "block/crop/" + name, "0", "1", "2", "3", "4"), List.of(FLHelpers.identifier(name + "_fruit")));
    }

    private void plantable(String name, ItemLike seed, PlanterType planter, int tier, int stages, float extraSeedChance, ItemLike output, FarmlandBlockEntity.NutrientType nut, List<ResourceLocation> textures, List<ResourceLocation> specials)
    {
        add(name, new Plantable(Ingredient.of(seed), planter, tier, stages, extraSeedChance, seed.asItem().getDefaultInstance(), output.asItem().getDefaultInstance(), nut, textures, specials));
    }

    private List<ResourceLocation> cropTextures(String modId, String name, int stages)
    {
        return cropTextures(modId, "crop", name, stages);
    }

    private List<ResourceLocation> cropTextures(String modId, String prefix, String name, int stages)
    {
        final List<ResourceLocation> textures = new ArrayList<>();
        for (int i = 0; i <= stages; i++)
        {
            textures.add(ResourceLocation.fromNamespaceAndPath(modId, "block/" + prefix + "/" + name + "_" + i));
        }
        return textures;
    }

    private List<ResourceLocation> forEach(String modId, String prefix, String... suffixes)
    {
        final List<ResourceLocation> textures = new ArrayList<>();
        for (String suffix : suffixes)
        {
            textures.add(ResourceLocation.fromNamespaceAndPath(modId, prefix + suffix));
        }
        return textures;
    }

    private List<ResourceLocation> forEachPrefix(String modId, String suffix, String... prefixes)
    {
        final List<ResourceLocation> textures = new ArrayList<>();
        for (String prefix : prefixes)
        {
            textures.add(ResourceLocation.fromNamespaceAndPath(modId, prefix + suffix));
        }
        return textures;
    }
}
