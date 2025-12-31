package com.eerussianguy.firmalife.common.items;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.oven.OvenType;
import com.eerussianguy.firmalife.common.capabilities.wine.WineType;
import com.eerussianguy.firmalife.common.util.FLArmorMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;


import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.FLFruit;
import com.eerussianguy.firmalife.common.util.FLMetal;
import com.eerussianguy.firmalife.config.FLConfig;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.Lore;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.items.*;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.common.items.TFCItems.ItemId;

@SuppressWarnings("unused")
public class FLItems
{
    public static final DeferredRegister<Item> ITEM = DeferredRegister.create(Registries.ITEM, FirmaLife.MOD_ID);

    public static final EnumSet<Food> TFC_FRUITS = EnumSet.of(Food.BANANA, Food.BLACKBERRY, Food.BLUEBERRY, Food.BUNCHBERRY, Food.CHERRY, Food.CLOUDBERRY, Food.CRANBERRY, Food.ELDERBERRY, Food.GOOSEBERRY, Food.GREEN_APPLE, Food.LEMON, Food.OLIVE, Food.ORANGE, Food.PEACH, Food.PLUM, Food.RASPBERRY, Food.RED_APPLE, Food.SNOWBERRY, Food.STRAWBERRY, Food.WINTERGREEN_BERRY);

    public static final Map<FLFood, ItemId> FOODS = Helpers.mapOf(FLFood.class, food -> register("food/" + food.name(), () -> new Item(new Item.Properties().food(food.getFoodProperties()))));
    public static final Map<FLFruit, ItemId> FRUITS = Helpers.mapOf(FLFruit.class, food -> register("food/" + food.name(), () -> new Item(new Item.Properties().food(food.getFoodProperties()))));

    public static final ItemId BEESWAX = register("beeswax", () -> new HoneycombItem(prop()));
    public static final ItemId WILD_HONEYCOMB = register("wild_honeycomb", () -> new HoneycombItem(prop()));
    public static final ItemId AROMATIC_HONEYCOMB = register("aromatic_honeycomb", () -> new HoneycombItem(prop()));
    public static final ItemId BEEHIVE_FRAME = register("beehive_frame");
    public static final ItemId INSULATING_BEEHIVE_FRAME = register("insulating_beehive_frame", () -> new BeehiveFrameItem(prop().craftRemainder(BEEHIVE_FRAME.get()), TFCItems.WOOL));
    public static final ItemId SCRAPED_BEEHIVE_FRAME = register("scraped_beehive_frame", () -> new Item(prop().craftRemainder(BEEHIVE_FRAME.get())));
    public static final ItemId FILLED_BEEHIVE_FRAME = register("filled_beehive_frame", () -> new BeehiveFrameItem(prop().craftRemainder(SCRAPED_BEEHIVE_FRAME.get()), BEESWAX));
    public static final ItemId HONEYED_BEEHIVE_FRAME = register("honeyed_beehive_frame", () -> new BeehiveFrameItem(prop().craftRemainder(BEEHIVE_FRAME.get()), FLItems.FOODS.get(FLFood.RAW_HONEY)));
    public static final ItemId SUGARED_BEEHIVE_FRAME = register("sugared_beehive_frame", () -> new BeehiveFrameItem(prop().craftRemainder(BEEHIVE_FRAME.get()), () -> Items.SUGAR));
    public static final ItemId CHEESECLOTH = register("cheesecloth");
    public static final ItemId FRUIT_LEAF = register("fruit_leaf");
    public static final ItemId HOLLOW_SHELL = register("hollow_shell",  () -> new HollowShellItem(prop(), FLConfig.SERVER.hollowShellCapacity, FLTags.Fluids.USABLE_IN_HOLLOW_SHELL, false, false));
    public static final ItemId WINE_GLASS = register("wine_glass",  () -> new WineGlassItem(prop(), FLConfig.SERVER.wineGlassCapacity, FLTags.Fluids.USABLE_IN_WINE_GLASS));
    public static final ItemId ICE_SHAVINGS = register("ice_shavings");
    public static final ItemId OVEN_INSULATION = register("oven_insulation", () -> new PeelItem(prop()));
    public static final ItemId PEEL = register("peel", () -> new PeelItem(prop()));
    public static final ItemId PIE_PAN = register("pie_pan");
    public static final ItemId PINEAPPLE_FIBER = register("pineapple_fiber");
    public static final ItemId PINEAPPLE_LEATHER = register("pineapple_leather");
    public static final ItemId PINEAPPLE_YARN = register("pineapple_yarn");
    public static final ItemId POTTERY_SHERD = register("pottery_sherd");
    public static final ItemId REINFORCED_GLASS = register("reinforced_glass");
    public static final ItemId RENNET = register("rennet");
    public static final ItemId SPOON = register("spoon");
    public static final ItemId SPRINKLER = register("sprinkler", () -> new SprinklerItem(FLBlocks.SPRINKLER.get(), FLBlocks.FLOOR_SPRINKLER.get(), prop()));
    public static final ItemId STAINLESS_STEEL_JAR_LID = register("stainless_steel_jar_lid");
    public static final ItemId EMPTY_JAR_WITH_STAINLESS_STEEL_LID = register("empty_jar_with_stainless_steel_lid");
    public static final ItemId TREATED_LUMBER = register("treated_lumber");
    public static final ItemId WATERING_CAN = register("watering_can", () -> new WateringCanItem(prop().durability(20)));
    public static final ItemId HEMATITIC_WINE_BOTTLE = register("hematitic_wine_bottle", () -> new FilledWineBottleItem(prop(), FLHelpers.identifier("block/hematitic_wine_bottle")));
    public static final ItemId VOLCANIC_WINE_BOTTLE = register("volcanic_wine_bottle", () -> new FilledWineBottleItem(prop(), FLHelpers.identifier("block/volcanic_wine_bottle")));
    public static final ItemId OLIVINE_WINE_BOTTLE = register("olivine_wine_bottle", () -> new FilledWineBottleItem(prop(), FLHelpers.identifier("block/olivine_wine_bottle")));
    public static final ItemId EMPTY_HEMATITIC_WINE_BOTTLE = register("empty_hematitic_wine_bottle", () -> new WineBottleItem(prop(), FLHelpers.identifier("block/empty_hematitic_wine_bottle")));
    public static final ItemId EMPTY_VOLCANIC_WINE_BOTTLE = register("empty_volcanic_wine_bottle", () -> new WineBottleItem(prop(), FLHelpers.identifier("block/empty_volcanic_wine_bottle")));
    public static final ItemId EMPTY_OLIVINE_WINE_BOTTLE = register("empty_olivine_wine_bottle", () -> new WineBottleItem(prop(), FLHelpers.identifier("block/empty_olivine_wine_bottle")));
    public static final ItemId CORK = register("cork");
    public static final ItemId TIRAGE_MIXTURE = register("tirage_mixture");
    public static final ItemId BARREL_STAVE = register("barrel_stave");
    public static final ItemId BOTTLE_LABEL = register("bottle_label");
    public static final ItemId RED_GRAPE_SEEDS = register("seeds/red_grape", () -> new GrapeSeedItem(new Item.Properties(), FLBlocks.GRAPE_STRING_PLANT_RED));
    public static final ItemId WHITE_GRAPE_SEEDS = register("seeds/white_grape", () -> new GrapeSeedItem(new Item.Properties(), FLBlocks.GRAPE_STRING_PLANT_WHITE));

    public static final ItemId BEEKEEPER_HELMET = register("beekeeper_helmet", () -> new ArmorItem(FLArmorMaterials.BEEKEEPER.holder(), ArmorItem.Type.HELMET, new Item.Properties()));
    public static final ItemId BEEKEEPER_CHESTPLATE = register("beekeeper_chestplate", () -> new ArmorItem(FLArmorMaterials.BEEKEEPER.holder(), ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final ItemId BEEKEEPER_LEGGINGS = register("beekeeper_leggings", () -> new ArmorItem(FLArmorMaterials.BEEKEEPER.holder(), ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final ItemId BEEKEEPER_BOOTS = register("beekeeper_boots", () -> new ArmorItem(FLArmorMaterials.BEEKEEPER.holder(), ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final Map<Spice, ItemId> SPICES = Helpers.mapOf(Spice.class, spice -> register("spice/" + spice.name()));
    public static final Map<OvenType, ItemId> FINISHES = Helpers.mapOf(OvenType.class, type -> type != OvenType.BRICK, type -> register(type.getSerializedName() + "_finish", () -> new FinishItem(prop(), type)));

    public static final ItemId HONEY_JAR = register("jar/honey", () -> new Item(new Item.Properties().component(Lore.TYPE, Lore.UNSEALED).craftRemainder(TFCItems.EMPTY_JAR.asItem())));
    public static final ItemId COMPOST_JAR = register("jar/compost", () -> new Item(new Item.Properties().component(Lore.TYPE, Lore.UNSEALED).craftRemainder(TFCItems.EMPTY_JAR.asItem())));
    public static final ItemId ROTTEN_COMPOST_JAR = register("jar/rotten_compost", () -> new Item(new Item.Properties().component(Lore.TYPE, Lore.UNSEALED).craftRemainder(TFCItems.EMPTY_JAR.asItem())));
    public static final ItemId GUANO_JAR = register("jar/guano", () -> new Item(new Item.Properties().component(Lore.TYPE, Lore.UNSEALED).craftRemainder(TFCItems.EMPTY_JAR.asItem())));
    public static final Map<FLFruit, ItemId> FRUIT_PRESERVES = Helpers.mapOf(FLFruit.class, food -> register("jar/" + food.getSerializedName() , () -> new Item(new Item.Properties().component(Lore.TYPE, Lore.UNSEALED).craftRemainder(TFCItems.EMPTY_JAR.asItem()))));
    public static final Map<FLFruit, ItemId> UNSEALED_FRUIT_PRESERVES = Helpers.mapOf(FLFruit.class, food -> register("jar/" + food.getSerializedName() + "_unsealed" , () -> new Item(new Item.Properties().component(Lore.TYPE, Lore.UNSEALED).craftRemainder(TFCItems.EMPTY_JAR.asItem()))));
    public static final Map<FLFruit, ItemId> JAM = Helpers.mapOf(FLFruit.class, food -> register("food/" + food.getSerializedName() + "_jam", () -> new Item(new Item.Properties())));

    public static final ItemId FILLED_PIE = register("food/filled_pie"); // inedible
    public static final ItemId RAW_PUMPKIN_PIE = register("food/raw_pumpkin_pie"); // inedible
    public static final ItemId RAW_PIZZA = register("food/raw_pizza"); // inedible
    public static final ItemId NIGHTSHADE_BERRY = register("food/nightshade_berry", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3f).effect(() -> new MobEffectInstance(MobEffects.HARM, 1, 10), 0.5f).build())));
    public static final ItemId STINKY_SOUP = register("food/stinky_soup", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3f).effect(() -> new MobEffectInstance(MobEffects.HARM, 1, 10), 0.5f).build())));

    public static final Map<Ore.Grade, ItemId> CHROMIUM_ORES = Helpers.mapOf(Ore.Grade.class, grade -> register("ore/" + grade.name() + "_chromite"));

    public static final Map<FLMetal, Map<FLMetal.ItemType, ItemId>> METAL_ITEMS = Helpers.mapOf(FLMetal.class, metal ->
        Helpers.mapOf(FLMetal.ItemType.class, type ->
            register("metal/" + type.name() + "/" + metal.name(), () -> type.create(metal))
        )
    );

    public static final Map<FLMetal, ItemId> METAL_FLUID_BUCKETS = Helpers.mapOf(FLMetal.class, metal ->
        register("bucket/metal/" + metal.name(), () -> new BucketItem(FLFluids.METALS.get(metal).getSource(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)))
    );

    public static final Map<ExtraFluid, ItemId> EXTRA_FLUID_BUCKETS = Helpers.mapOf(ExtraFluid.class, fluid ->
        register("bucket/" + fluid.getSerializedName(), () -> new BucketItem(FLFluids.EXTRA_FLUIDS.get(fluid).getSource(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)))
    );

    public static final Map<WineType, ItemId> WINE_FLUID_BUCKETS = Helpers.mapOf(WineType.class, fluid ->
        register("bucket/" + fluid.getSerializedName(), () -> new BucketItem(FLFluids.WINE_FLUIDS.get(fluid).getSource(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)))
    );


    private static Item.Properties prop()
    {
        return new Item.Properties();
    }

    private static Item.Properties foodProperties()
    {
        return new Item.Properties().food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.3f).build());
    }

    private static ItemId register(String name)
    {
        return register(name, () -> new Item(new Item.Properties()));
    }

    private static ItemId register(String name, Item.Properties properties)
    {
        return new ItemId(ITEM.register(name.toLowerCase(Locale.ROOT), () -> new Item(properties)));
    }

    private static ItemId register(String name, Supplier<Item> item)
    {
        return new ItemId(ITEM.register(name.toLowerCase(Locale.ROOT), item));
    }

}
