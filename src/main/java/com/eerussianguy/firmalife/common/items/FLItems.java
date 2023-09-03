package com.eerussianguy.firmalife.common.items;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import com.eerussianguy.firmalife.common.blocks.OvenType;
import com.eerussianguy.firmalife.common.util.FLArmorMaterials;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.FLFruit;
import com.eerussianguy.firmalife.common.util.FLMetal;
import com.eerussianguy.firmalife.config.FLConfig;
import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.items.*;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.TFCItemGroup.FOOD;
import static net.dries007.tfc.common.TFCItemGroup.MISC;
import static net.minecraft.world.item.CreativeModeTab.*;

@SuppressWarnings("unused")
public class FLItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FirmaLife.MOD_ID);

    public static final EnumSet<Food> TFC_FRUITS = EnumSet.of(Food.BANANA, Food.BLACKBERRY, Food.BLUEBERRY, Food.BUNCHBERRY, Food.CHERRY, Food.CLOUDBERRY, Food.CRANBERRY, Food.ELDERBERRY, Food.GOOSEBERRY, Food.GREEN_APPLE, Food.LEMON, Food.OLIVE, Food.ORANGE, Food.PEACH, Food.PLUM, Food.RASPBERRY, Food.RED_APPLE, Food.SNOWBERRY, Food.STRAWBERRY, Food.WINTERGREEN_BERRY);

    public static final RegistryObject<Item> BEEHIVE_FRAME = register("beehive_frame", () -> new BeehiveFrameItem(prop()));
    public static final RegistryObject<Item> BEESWAX = register("beeswax", () -> new HoneycombItem(prop()));
    public static final RegistryObject<Item> CINNAMON_BARK = register("cinnamon_bark", MISC);
    public static final RegistryObject<Item> CHEESECLOTH = register("cheesecloth", MISC);
    public static final RegistryObject<Item> FRUIT_LEAF = register("fruit_leaf", MISC);
    public static final RegistryObject<Item> EMPTY_JAR = register("empty_jar", MISC);
    public static final RegistryObject<Item> HOLLOW_SHELL = register("hollow_shell",  () -> new HollowShellItem(prop(), FLConfig.SERVER.hollowShellCapacity, FLTags.Fluids.USABLE_IN_HOLLOW_SHELL, false, false));
    public static final RegistryObject<Item> ICE_SHAVINGS = register("ice_shavings", MISC);
    public static final RegistryObject<Item> OVEN_INSULATION = register("oven_insulation", () -> new PeelItem(prop()));
    public static final RegistryObject<Item> PEEL = register("peel", () -> new PeelItem(prop()));
    public static final RegistryObject<Item> PIE_PAN = register("pie_pan", MISC);
    public static final RegistryObject<Item> PINEAPPLE_FIBER = register("pineapple_fiber", MISC);
    public static final RegistryObject<Item> PINEAPPLE_LEATHER = register("pineapple_leather", MISC);
    public static final RegistryObject<Item> PINEAPPLE_YARN = register("pineapple_yarn", MISC);
    public static final RegistryObject<Item> RAW_HONEY = register("raw_honey", MISC);
    public static final RegistryObject<Item> RENNET = register("rennet", MISC);
    public static final RegistryObject<Item> SEED_BALL = register("seed_ball", () -> new SeedBallItem(prop()));
    public static final RegistryObject<Item> SPOON = register("spoon", MISC);
    public static final RegistryObject<Item> TREATED_LUMBER = register("treated_lumber", MISC);
    public static final RegistryObject<Item> WATERING_CAN = register("watering_can", () -> new WateringCanItem(prop().defaultDurability(20)));

    public static final RegistryObject<Item> BEEKEEPER_HELMET = register("beekeeper_helmet", () -> new ArmorItem(FLArmorMaterials.BEEKEEPER, EquipmentSlot.HEAD, new Item.Properties().tab(TAB_COMBAT)));
    public static final RegistryObject<Item> BEEKEEPER_CHESTPLATE = register("beekeeper_chestplate", () -> new ArmorItem(FLArmorMaterials.BEEKEEPER, EquipmentSlot.CHEST, new Item.Properties().tab(TAB_COMBAT)));
    public static final RegistryObject<Item> BEEKEEPER_LEGGINGS = register("beekeeper_leggings", () -> new ArmorItem(FLArmorMaterials.BEEKEEPER, EquipmentSlot.LEGS, new Item.Properties().tab(TAB_COMBAT)));
    public static final RegistryObject<Item> BEEKEEPER_BOOTS = register("beekeeper_boots", () -> new ArmorItem(FLArmorMaterials.BEEKEEPER, EquipmentSlot.FEET, new Item.Properties().tab(TAB_COMBAT)));

    public static final Map<Spice, RegistryObject<Item>> SPICES = Helpers.mapOfKeys(Spice.class, spice -> register("spice/" + spice.name(), MISC));
    public static final Map<FLFood, RegistryObject<Item>> FOODS = Helpers.mapOfKeys(FLFood.class, food -> register("food/" + food.name(), () -> new DecayingItem(new Item.Properties().food(food.getFoodProperties()).tab(FOOD))));
    public static final Map<FLFruit, RegistryObject<Item>> FRUITS = Helpers.mapOfKeys(FLFruit.class, food -> register("food/" + food.name(), () -> new DecayingItem(new Item.Properties().food(food.getFoodProperties()).tab(FOOD))));
    public static final Map<OvenType, RegistryObject<Item>> FINISHES = Helpers.mapOfKeys(OvenType.class, type -> type != OvenType.BRICK, type -> register(type.getTrueName() + "_finish", () -> new FinishItem(prop(), type)));

    public static final RegistryObject<DynamicBowlFood> FILLED_PIE = registerContainerFood("food/filled_pie", false);
    public static final RegistryObject<DynamicBowlFood> COOKED_PIE = registerContainerFood("food/cooked_pie");
    public static final RegistryObject<DecayingItem> RAW_PIZZA = registerDynamicFood("food/raw_pizza", false);
    public static final RegistryObject<DecayingItem> COOKED_PIZZA = registerDynamicFood("food/cooked_pizza");
    public static final RegistryObject<DecayingItem> BURRITO = registerDynamicFood("food/burrito");
    public static final RegistryObject<DecayingItem> TACO = registerDynamicFood("food/taco");
    public static final RegistryObject<Item> NIGHTSHADE_BERRY = register("food/nightshade_berry", () -> new DecayingItem(new Item.Properties().tab(FOOD).food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).effect(() -> new MobEffectInstance(MobEffects.HARM, 1, 10), 0.5f).build())));
    public static final RegistryObject<DynamicBowlFood> STINKY_SOUP = register("food/stinky_soup", () -> new DynamicBowlFood(new Item.Properties().tab(FOOD).food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).effect(() -> new MobEffectInstance(MobEffects.HARM, 1, 10), 0.5f).build())));

    public static final Map<Ore.Grade, RegistryObject<Item>> CHROMIUM_ORES = Helpers.mapOfKeys(Ore.Grade.class, grade -> register("ore/" + grade.name() + "_chromite", TFCItemGroup.ORES));

    public static final Map<FLMetal, Map<FLMetal.ItemType, RegistryObject<Item>>> METAL_ITEMS = Helpers.mapOfKeys(FLMetal.class, metal ->
        Helpers.mapOfKeys(FLMetal.ItemType.class, type ->
            register("metal/" + type.name() + "/" + metal.name(), () -> type.create(metal))
        )
    );

    public static final Map<FLMetal, RegistryObject<BucketItem>> METAL_FLUID_BUCKETS = Helpers.mapOfKeys(FLMetal.class, metal ->
        register("bucket/metal/" + metal.name(), () -> new BucketItem(FLFluids.METALS.get(metal).source(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)))
    );

    public static final Map<ExtraFluid, RegistryObject<BucketItem>> EXTRA_FLUID_BUCKETS = Helpers.mapOfKeys(ExtraFluid.class, fluid ->
        register("bucket/" + fluid.getSerializedName(), () -> new BucketItem(FLFluids.EXTRA_FLUIDS.get(fluid).source(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(MISC)))
    );

    private static Item.Properties prop()
    {
        return new Item.Properties().tab(MISC);
    }

    private static RegistryObject<DecayingItem> registerDynamicFood(String name)
    {
        return registerDynamicFood(name, true);
    }

    private static RegistryObject<DecayingItem> registerDynamicFood(String name, boolean edible)
    {
        return register(name, () -> new DecayingItem(foodProperties(edible)));
    }

    private static RegistryObject<DynamicBowlFood> registerContainerFood(String name)
    {
        return registerContainerFood(name, true);
    }

    private static RegistryObject<DynamicBowlFood> registerContainerFood(String name, boolean edible)
    {
        return register(name, () -> new DynamicBowlFood(foodProperties(edible)));
    }

    private static Item.Properties foodProperties(boolean edible)
    {
        Item.Properties properties = new Item.Properties().tab(FOOD);
        if (edible)
        {
            properties.food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build());
        }
        return properties;
    }

    private static RegistryObject<Item> register(String name, CreativeModeTab group)
    {
        return register(name, () -> new Item(new Item.Properties().tab(group)));
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item)
    {
        return ITEMS.register(name.toLowerCase(Locale.ROOT), item);
    }
}
