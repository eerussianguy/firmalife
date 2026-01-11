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
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.common.blocks.plant.fruit.SeasonalPlantBlock;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;

import static com.eerussianguy.firmalife.common.blocks.greenhouse.PlanterType.*;

public class BuiltinPlantables extends DataManagerProvider<Plantable>
{
    public BuiltinPlantables(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(Plantable.MANAGER, output, lookup, FirmaLife.MOD_ID);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        simple(Crop.BEET, QUAD, food(Food.BEET), 5);
        simple(Crop.CABBAGE, QUAD, food(Food.CABBAGE), 5);
        simple(Crop.CARROT, QUAD, food(Food.CARROT), 4);
        simple(Crop.GARLIC, QUAD, food(Food.GARLIC), 4);
        simple(Crop.POTATO, QUAD, food(Food.POTATO), 6);
        simple(Crop.ONION, QUAD, food(Food.ONION), 6);
        simple(Crop.SOYBEAN, QUAD, food(Food.SOYBEAN), 6);
        simple(Crop.CASSAVA, QUAD, food(Food.CASSAVA), 5);
        simple(Crop.LENTIL, QUAD, food(Food.LENTIL), 5);
        simple(Crop.PEANUT, QUAD, food(Food.PEANUT), 5);
        simple(Crop.CANOLA, QUAD, TFCItems.CANOLA, 5);
        simple(Crop.RADISH, QUAD, food(Food.RADISH), 5);
        simple(Crop.ALFALFA, QUAD, TFCItems.ALFALFA, 5);

        simple(Crop.MAIZE, LARGE, food(Food.MAIZE), 4, 10, true);
        simple(Crop.BARLEY, LARGE, food(Food.BARLEY), 7, 10, false);
        simple(Crop.OAT, LARGE, food(Food.OAT), 7, 10, false);
        simple(Crop.RYE, LARGE, food(Food.RYE), 7, 10, false);
        simple(Crop.WHEAT, LARGE, food(Food.WHEAT), 7, 10, false);
        simple(Crop.RICE, HYDROPONIC, food(Food.RICE), 7, 10, false);

        simple(Crop.JUTE, LARGE, TFCItems.JUTE, 4, 10, true);
        simple(Crop.PAPYRUS, LARGE, TFCItems.PAPYRUS, 5, 10, true);
        simple(Crop.GREEN_BEAN, LARGE, food(Food.GREEN_BEAN), 4, 10, true);
        simple(Crop.TOMATO, LARGE, food(Food.TOMATO), 4, 10, true);
        simple(Crop.SUGARCANE, LARGE, food(Food.SUGARCANE), 4, 10, true);
        simple(Crop.RED_BELL_PEPPER, LARGE, food(Food.RED_BELL_PEPPER), 6, 10, false);
        simple(Crop.YELLOW_BELL_PEPPER, LARGE, food(Food.YELLOW_BELL_PEPPER), 6, 10, false);

        plantable("cranberry", TFCBlocks.CRANBERRY_BUSH.asItem(), HYDROPONIC, 10, 3, 0.5f, food(Food.CRANBERRY), of(0.2f, 0.25f, 0.25f), cropTextures(FirmaLife.MOD_ID, "cranberry", 4), List.of());

        plantable("red_grapes", FLItems.RED_GRAPE_SEEDS, TRELLIS, 15, 0, 0.5f, food(FLFruit.RED_GRAPES), of(0.25f, 0.25f, 0.25f), forEach(FirmaLife.MOD_ID, "block/crop/grape", "_leaves", "_leaves_dead", "_leaves_flowering", "_leaves_red"), List.of());
        plantable("white_grapes", FLItems.WHITE_GRAPE_SEEDS, TRELLIS, 15, 0, 0.5f, food(FLFruit.WHITE_GRAPES), of(0.25f, 0.25f, 0.25f), forEach(FirmaLife.MOD_ID, "block/crop/grape", "_leaves", "_leaves_dead", "_leaves_flowering", "_leaves_white"), List.of());

        for (FruitBlocks.Tree tree : FruitBlocks.Tree.values())
        {
            final SeasonalPlantBlock block = (SeasonalPlantBlock) TFCBlocks.FRUIT_TREE_LEAVES.get(tree).get();
            final ItemStack fruit = block.getProductItem(RandomSource.create());
            plantable(tree.getSerializedName(), TFCBlocks.FRUIT_TREE_SAPLINGS.get(tree).asItem(), BONSAI, 15, 0, 0.08f, fruit.getItem(), getTreeNutrients(tree), forEach(TerraFirmaCraft.MOD_ID, "block/fruit_tree/" + tree.getSerializedName(), "_fruiting_leaves", "_dry_leaves", "_flowering_leaves", "_branch", "_leaves"), List.of());
        }
        for (FLFruitBlocks.Tree tree : FLFruitBlocks.Tree.values())
        {
            final SeasonalPlantBlock block = (SeasonalPlantBlock) FLBlocks.FRUIT_TREE_LEAVES.get(tree).get();
            final ItemStack fruit = block.getProductItem(RandomSource.create());
            plantable(tree.name().toLowerCase(Locale.ROOT), FLBlocks.FRUIT_TREE_SAPLINGS.get(tree).asItem(), BONSAI, 15, 0, 0.08f, fruit.getItem(), getFLTreeNutrients(tree), forEach(FirmaLife.MOD_ID, "block/fruit_tree/" + tree.name().toLowerCase(Locale.ROOT), "_fruiting_leaves", "_dry_leaves", "_flowering_leaves", "_branch", "_leaves"), List.of());
        }
        for (FruitBlocks.StationaryBush bush : FruitBlocks.StationaryBush.values())
        {
            final SeasonalPlantBlock block = (SeasonalPlantBlock) TFCBlocks.STATIONARY_BUSHES.get(bush).get();
            final ItemStack fruit = block.getProductItem(RandomSource.create());
            final String name = bush.name().toLowerCase(Locale.ROOT);
            final String p = "block/berry_bush/";
            plantable(name, TFCBlocks.STATIONARY_BUSHES.get(bush).asItem(), TRELLIS, 15, 0, 0.08f, fruit.getItem(), getStationaryNutrients(bush), forEachPrefix(TerraFirmaCraft.MOD_ID, "_bush", p + name, p + "dry_" + name, p + "flowering_" + name, p + "fruiting_" + name), List.of());
        }
        for (FLFruitBlocks.StationaryBush bush : FLFruitBlocks.StationaryBush.values())
        {
            final SeasonalPlantBlock block = (SeasonalPlantBlock) FLBlocks.STATIONARY_BUSHES.get(bush).get();
            final ItemStack fruit = block.getProductItem(RandomSource.create());
            final String name = bush.name().toLowerCase(Locale.ROOT);
            final String p = "block/berry_bush/";
            plantable(name, FLBlocks.STATIONARY_BUSHES.get(bush).asItem(), TRELLIS, 15, 0, 0.08f, fruit.getItem(), getFLStationaryNutrients(bush), forEachPrefix(FirmaLife.MOD_ID, "_bush", p + name, p + "dry_" + name, p + "flowering_" + name, p + "fruiting_" + name), List.of());
        }
        for (FruitBlocks.SpreadingBush bush : FruitBlocks.SpreadingBush.values())
        {
            final SeasonalPlantBlock block = (SeasonalPlantBlock) TFCBlocks.SPREADING_BUSHES.get(bush).get();
            final ItemStack fruit = block.getProductItem(RandomSource.create());
            final String name = bush.name().toLowerCase(Locale.ROOT);
            final String p = "block/berry_bush/";
            plantable(name, TFCBlocks.SPREADING_BUSHES.get(bush).asItem(), TRELLIS, 15, 0, 0.08f, fruit.getItem(), getSpreadingNutrients(bush), forEachPrefix(TerraFirmaCraft.MOD_ID, "_bush", p + name, p + "dry_" + name, p + "flowering_" + name, p + "fruiting_" + name), List.of());
        }

        for (Herb herb : Herb.values())
        {
            final String name = herb.name().toLowerCase(Locale.ROOT);
            final List<ResourceLocation> textures = new ArrayList<>();
            for (int i = 0; i <= 1; i++)
            {
                textures.add(ResourceLocation.fromNamespaceAndPath(FirmaLife.MOD_ID, "block/plant/" + name + "/" + i));
            }
            plantable(name, FLBlocks.HERBS.get(herb).asItem(), QUAD, 0, 1, 0.8f, FLBlocks.HERBS.get(herb).asItem(), of(0.2f, 0.2f, 0.2f), textures, List.of());
        }

        hanging("squash", TFCItems.CROP_SEEDS.get(Crop.SQUASH), food(Food.SQUASH), 15, of(Crop.SQUASH), 0.5f);
        hanging("pumpkin", TFCItems.CROP_SEEDS.get(Crop.PUMPKIN), TFCBlocks.PUMPKIN, 15, of(Crop.PUMPKIN), 0.5f);
        hanging("melon", TFCItems.CROP_SEEDS.get(Crop.MELON), TFCBlocks.MELON, 15, of(Crop.MELON), 0.5f);
        hanging("banana", TFCBlocks.BANANA_SAPLING, food(Food.BANANA), 15, of(0.25f, 0.3f, 0.3f), 0.08f);
    }

    private Plantable.NutrientList getTreeNutrients(FruitBlocks.Tree tree)
    {
        return switch (tree)
        {
            case CHERRY -> of(0.15f, 0.3f, 0.3f);
            case GREEN_APPLE, RED_APPLE, OLIVE -> of(0.3f, 0.3f, 0.3f);
            case LEMON -> of(0.4f, 0.2f, 0.2f);
            case ORANGE -> of(0.3f, 0.2f, 0.3f);
            case PEACH -> of(0.2f, 0.3f, 0.3f);
            case PLUM -> of(0.25f, 0.25f, 0.25f);
        };
    }

    private Plantable.NutrientList getFLTreeNutrients(FLFruitBlocks.Tree tree)
    {
        return switch (tree)
        {
            case COCOA -> of(0.3f, 0.2f, 0.45f);
            case FIG -> of(0.25f, 0.25f, 0.25f);
        };
    }

    private Plantable.NutrientList getStationaryNutrients(FruitBlocks.StationaryBush bush)
    {
        return switch (bush)
        {
            case SNOWBERRY -> of(0.3f, 0.3f, 0.25f);
            case BUNCHBERRY, GOOSEBERRY, WINTERGREEN_BERRY -> of(0.25f, 0.25f, 0.25f);
            case CLOUDBERRY -> of(0.2f, 0.3f, 0.3f);
            case STRAWBERRY -> of(0.3f, 0.2f, 0.3f);
        };
    }

    private Plantable.NutrientList getFLStationaryNutrients(FLFruitBlocks.StationaryBush bush)
    {
        return switch (bush)
        {
            case PINEAPPLE -> of(0.3f, 0.3f, 0.3f);
            case NIGHTSHADE -> of(0.1f, 0.2f, 0.2f);
        };
    }

    private Plantable.NutrientList getSpreadingNutrients(FruitBlocks.SpreadingBush bush)
    {
        return switch (bush)
        {
            case BLACKBERRY, RASPBERRY, ELDERBERRY -> of(0.25f, 0.25f, 0.25f);
            case BLUEBERRY -> of(0.25f, 0.2f, 0.25f);
        };
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

    private void simple(Crop crop, PlanterType planter, ItemLike output, int stages)
    {
        simple(crop, planter, output, stages, 0, false);
    }

    private void simple(Crop crop, PlanterType planter, ItemLike output, int stages, int tier, boolean firmalife)
    {
        final String name = crop.name().toLowerCase(Locale.ROOT);
        final List<ResourceLocation> textures = cropTextures(firmalife ? FirmaLife.MOD_ID : TerraFirmaCraft.MOD_ID, name, stages);
        add(name, new Plantable(Ingredient.of(TFCItems.CROP_SEEDS.get(crop)), planter, tier, stages, 0.5f, TFCItems.CROP_SEEDS.get(crop).get().getDefaultInstance(), output.asItem().getDefaultInstance(), of(crop), textures, List.of()));
    }

    private void hanging(String name, ItemLike seed, ItemLike crop, int tier, Plantable.NutrientList nut, float seedChance)
    {
        plantable(name, seed, HANGING, tier, 4, seedChance, seed, nut, forEach(FirmaLife.MOD_ID, "block/crop/" + name, "_0", "_1", "_2", "_3", "_4"), List.of(FLHelpers.identifier("block/crop/" + name + "_fruit")));
    }

    private void plantable(String name, ItemLike seed, PlanterType planter, int tier, int stages, float extraSeedChance, ItemLike output, Plantable.NutrientList nut, List<ResourceLocation> textures, List<ResourceLocation> specials)
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

    private Plantable.NutrientList of(Crop crop)
    {
        return new Plantable.NutrientList(crop.getNitrogen(), crop.getPhosphorous(), crop.getPotassium());
    }

    private Plantable.NutrientList of(float n, float p, float k)
    {
        return new Plantable.NutrientList(n, p, k);
    }
}
