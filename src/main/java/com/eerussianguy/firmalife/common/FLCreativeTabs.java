package com.eerussianguy.firmalife.common;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.OvenType;
import com.eerussianguy.firmalife.common.blocks.greenhouse.Greenhouse;
import com.eerussianguy.firmalife.common.blocks.plant.FLFruitBlocks;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.Carving;
import com.eerussianguy.firmalife.common.util.FLFruit;
import com.eerussianguy.firmalife.common.util.FLMetal;
import com.eerussianguy.firmalife.common.util.FLSelfTests;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.TFCCreativeTabs;
import net.dries007.tfc.common.blocks.DecorationBlockHolder;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Metal;

@SuppressWarnings("unused")
public final class FLCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FirmaLife.MOD_ID);

    public static final TFCCreativeTabs.Id FIRMALIFE = register("firmalife", () -> new ItemStack(FLBlocks.CURED_OVEN_TOP.get(OvenType.BRICK).get()),  FLCreativeTabs::fillFirmalifeTab);

    public static void fillFirmalifeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output out)
    {
        accept(out, FLItems.HONEY_JAR);
        accept(out, FLItems.COMPOST_JAR);
        accept(out, FLItems.ROTTEN_COMPOST_JAR);
        accept(out, FLItems.GUANO_JAR);
        accept(out, FLItems.CHEESECLOTH);
        accept(out, FLItems.FRUIT_LEAF);
        accept(out, FLItems.ICE_SHAVINGS);
        accept(out, FLItems.OVEN_INSULATION);
        accept(out, FLItems.PEEL);
        accept(out, FLItems.PIE_PAN);
        accept(out, FLItems.PINEAPPLE_YARN);
        accept(out, FLItems.PINEAPPLE_LEATHER);
        accept(out, FLItems.PINEAPPLE_FIBER);
        accept(out, FLItems.POTTERY_SHERD);
        accept(out, FLItems.BEEHIVE_FRAME);
        accept(out, FLItems.FILLED_BEEHIVE_FRAME);
        accept(out, FLItems.QUEEN_BEE);
        accept(out, FLItems.BEESWAX);
        accept(out, FLItems.STAINLESS_STEEL_JAR_LID);
        accept(out, FLItems.EMPTY_JAR_WITH_STAINLESS_STEEL_LID);
        accept(out, FLItems.RENNET);
        accept(out, FLItems.SEED_BALL);
        accept(out, FLItems.WATERING_CAN);
        accept(out, FLItems.BEEKEEPER_HELMET);
        accept(out, FLItems.BEEKEEPER_CHESTPLATE);
        accept(out, FLItems.BEEKEEPER_LEGGINGS);
        accept(out, FLItems.BEEKEEPER_BOOTS);
        accept(out, FLItems.CINNAMON_BARK);
        accept(out, FLItems.OLIVINE_WINE_BOTTLE);
        accept(out, FLItems.HEMATITIC_WINE_BOTTLE);
        accept(out, FLItems.VOLCANIC_WINE_BOTTLE);
        accept(out, FLItems.EMPTY_OLIVINE_WINE_BOTTLE);
        accept(out, FLItems.EMPTY_HEMATITIC_WINE_BOTTLE);
        accept(out, FLItems.EMPTY_VOLCANIC_WINE_BOTTLE);
        accept(out, FLItems.CORK);
        accept(out, FLItems.TIRAGE_MIXTURE);
        accept(out, FLItems.BOTTLE_LABEL);
        accept(out, FLItems.BARREL_STAVE);
        accept(out, FLItems.RED_GRAPE_SEEDS);
        accept(out, FLItems.WHITE_GRAPE_SEEDS);

        FLItems.SPICES.values().forEach(reg -> accept(out, reg));

        for (OvenType type : OvenType.values())
        {
            accept(out, FLBlocks.CURED_OVEN_BOTTOM, type);
            accept(out, FLBlocks.INSULATED_OVEN_BOTTOM, type);
            accept(out, FLBlocks.CURED_OVEN_TOP, type);
            accept(out, FLBlocks.CURED_OVEN_CHIMNEY, type);
            accept(out, FLBlocks.CURED_OVEN_HOPPER, type);
            accept(out, FLBlocks.OVEN_COUNTERTOP, type);
            accept(out, FLItems.FINISHES, type);
            if (type == OvenType.RUSTIC)
            {
                accept(out, FLBlocks.RUSTIC_BRICKS);
                accept(out, FLBlocks.RUSTIC_BRICK_DECOR);
            }
            else if (type == OvenType.TILE)
            {
                accept(out, FLBlocks.TILES);
                accept(out, FLBlocks.TILE_DECOR);
            }
        }
        accept(out, FLBlocks.OVEN_BOTTOM);
        accept(out, FLBlocks.OVEN_TOP);
        accept(out, FLBlocks.OVEN_CHIMNEY);
        accept(out, FLBlocks.OVEN_HOPPER);
        accept(out, FLBlocks.ASHTRAY);
        accept(out, FLBlocks.DRYING_MAT);
        accept(out, FLBlocks.SOLAR_DRIER);
        accept(out, FLBlocks.BEEHIVE);
        accept(out, FLBlocks.SKEP);
        accept(out, FLBlocks.WILD_BEEHIVE);
        accept(out, FLBlocks.COMPOST_TUMBLER);
        accept(out, FLBlocks.MIXING_BOWL);
        accept(out, FLItems.SPOON);
        accept(out, FLBlocks.VAT);
        accept(out, FLBlocks.JARRING_STATION);
        accept(out, FLBlocks.PLATE);
        accept(out, FLBlocks.CLIMATE_STATION);
        accept(out, FLBlocks.LARGE_PLANTER);
        accept(out, FLBlocks.QUAD_PLANTER);
        accept(out, FLBlocks.HYDROPONIC_PLANTER);
        accept(out, FLBlocks.BONSAI_PLANTER);
        accept(out, FLBlocks.HANGING_PLANTER);
        accept(out, FLBlocks.TRELLIS_PLANTER);
        accept(out, FLBlocks.PICKER);
        accept(out, FLBlocks.SWEEPER);
        accept(out, FLBlocks.GRAPE_TRELLIS_POST);
        accept(out, FLBlocks.SEALED_BRICKS);
        accept(out, FLBlocks.CHISELED_SEALED_BRICKS);
        accept(out, FLBlocks.POLISHED_SEALED_BRICKS);
        accept(out, FLBlocks.DARK_LADDER);
        accept(out, FLBlocks.SEALED_DOOR);
        accept(out, FLBlocks.SEALED_WALL);
        accept(out, FLBlocks.SEALED_TRAPDOOR);
        accept(out, FLItems.HOLLOW_SHELL);
        accept(out, FLItems.WINE_GLASS);
        accept(out, FLItems.TREATED_LUMBER);
        accept(out, FLBlocks.TREATED_WOOD);
        for (Wood wood : Wood.values())
        {
            accept(out, FLBlocks.FOOD_SHELVES, wood);
            accept(out, FLBlocks.HANGERS, wood);
            accept(out, FLBlocks.JARBNETS, wood);
            accept(out, FLBlocks.KEGS, wood);
            accept(out, FLBlocks.STOMPING_BARRELS, wood);
            accept(out, FLBlocks.BARREL_PRESSES, wood);
            accept(out, FLBlocks.WINE_SHELVES, wood);
        }

        for (Carving carving : Carving.values())
        {
            accept(out, FLBlocks.CARVED_PUMPKINS, carving);
            accept(out, FLBlocks.JACK_O_LANTERNS, carving);
        }

        accept(out, FLItems.REINFORCED_GLASS);
        accept(out, FLBlocks.COPPER_PIPE);
        accept(out, FLBlocks.OXIDIZED_COPPER_PIPE);
        accept(out, FLBlocks.IRRIGATION_TANK);
        accept(out, FLBlocks.PUMPING_STATION);
        accept(out, FLItems.SPRINKLER);
        for (Greenhouse greenhouse : Greenhouse.values())
        {
            for (Greenhouse.BlockType blockType : Greenhouse.BlockType.values())
            {
                accept(out, FLBlocks.GREENHOUSE_BLOCKS, greenhouse, blockType);
            }
        }
    }

    public static void onTooltip(ItemTooltipEvent event)
    {

    }

    public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent out)
    {
        if (out.getTab() == TFCCreativeTabs.FOOD.tab().get())
        {
            FLItems.FOODS.values().forEach(reg -> accept(out, reg));
            FLItems.FRUITS.values().forEach(reg -> accept(out, reg));
            for (FLFruit fruit : FLFruit.values())
            {
                accept(out, FLItems.FRUIT_PRESERVES.get(fruit));
                accept(out, FLItems.UNSEALED_FRUIT_PRESERVES.get(fruit));
                accept(out, FLItems.JAM.get(fruit));
            }
            accept(out, FLItems.FILLED_PIE);
            accept(out, FLItems.RAW_PIZZA);
            accept(out, FLItems.STINKY_SOUP);
            accept(out, FLItems.NIGHTSHADE_BERRY);
            accept(out, FLBlocks.CHEDDAR_WHEEL);
            accept(out, FLBlocks.CHEVRE_WHEEL);
            accept(out, FLBlocks.RAJYA_METOK_WHEEL);
            accept(out, FLBlocks.GOUDA_WHEEL);
            accept(out, FLBlocks.FETA_WHEEL);
            accept(out, FLBlocks.SHOSHA_WHEEL);
        }
        else if (out.getTab() == TFCCreativeTabs.METAL.tab().get())
        {
            for (FLMetal metal : FLMetal.values())
            {
                for (FLMetal.ItemType type : FLMetal.ItemType.values())
                {
                    accept(out, FLItems.METAL_ITEMS, metal, type);
                }
                for (Metal.BlockType type : Metal.BlockType.values())
                {
                    accept(out, FLBlocks.METALS, metal, type);
                }
            }
        }
        else if (out.getTab() == TFCCreativeTabs.FLORA.tab().get())
        {
            accept(out, FLBlocks.BUTTERFLY_GRASS);
            FLBlocks.HERBS.values().forEach(reg -> accept(out, reg));
            for (FLFruitBlocks.Tree tree : FLFruitBlocks.Tree.values())
            {
                accept(out, FLBlocks.FRUIT_TREE_SAPLINGS, tree);
                accept(out, FLBlocks.FRUIT_TREE_LEAVES, tree);
            }
            FLBlocks.STATIONARY_BUSHES.values().forEach(reg -> accept(out, reg));
        }
        else if (out.getTab() == TFCCreativeTabs.WOOD.tab().get())
        {
            for (Wood wood : Wood.values())
            {
                accept(out, FLBlocks.FOOD_SHELVES, wood);
                accept(out, FLBlocks.HANGERS, wood);
                accept(out, FLBlocks.JARBNETS, wood);
            }
        }
        else if (out.getTab() == TFCCreativeTabs.ORES.tab().get())
        {
            accept(out, FLBlocks.SMALL_CHROMITE);
            FLItems.CHROMIUM_ORES.values().forEach(reg -> accept(out, reg));
            for (Rock rock : Rock.values())
            {
                for (Ore.Grade grade : Ore.Grade.values())
                {
                    accept(out, FLBlocks.CHROMITE_ORES, rock, grade);
                }
            }
        }
        else if (out.getTab() == TFCCreativeTabs.MISC.tab().get())
        {
            FLItems.METAL_FLUID_BUCKETS.values().forEach(reg -> accept(out, reg));
            FLItems.EXTRA_FLUID_BUCKETS.values().forEach(reg -> accept(out, reg));
            FLItems.WINE_FLUID_BUCKETS.values().forEach(reg -> accept(out, reg));
        }
    }

    private static TFCCreativeTabs.Id register(String name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator displayItems)
    {
        final var holder = CREATIVE_TAB.register(name, () -> CreativeModeTab.builder()
            .icon(icon)
            .title(Component.translatable("firmalife.creative_tab." + name))
            .displayItems(displayItems)
            .build());
        return new TFCCreativeTabs.Id(holder, displayItems);
    }

    private static <T extends ItemLike, R extends Supplier<T>, K1, K2> void accept(CreativeModeTab.Output out, Map<K1, Map<K2, R>> map, K1 key1, K2 key2)
    {
        if (map.containsKey(key1) && map.get(key1).containsKey(key2))
        {
            out.accept(map.get(key1).get(key2).get());
        }
    }

    private static <T extends ItemLike, R extends Supplier<T>, K> void accept(CreativeModeTab.Output out, Map<K, R> map, K key)
    {
        if (map.containsKey(key))
        {
            out.accept(map.get(key).get());
        }
    }

    private static <T extends ItemLike, R extends Supplier<T>> void accept(CreativeModeTab.Output out, R reg)
    {
        if (reg.get().asItem() == Items.AIR)
        {
            FirmaLife.LOGGER.error("BlockItem with no Item added to creative tab: " + reg);
            FLSelfTests.reportExternalError();
            return;
        }
        out.accept(reg.get());
    }

    private static void accept(CreativeModeTab.Output out, DecorationBlockHolder decoration)
    {
        out.accept(decoration.stair());
        out.accept(decoration.slab());
        out.accept(decoration.wall());
    }

    private static <T> void consumeOurs(Registry<T> registry, Consumer<T> consumer)
    {
        for (T value : registry)
        {
            if (Objects.requireNonNull(registry.getKey(value)).getNamespace().equals(FirmaLife.MOD_ID))
            {
                consumer.accept(value);
            }
        }
    }

}
