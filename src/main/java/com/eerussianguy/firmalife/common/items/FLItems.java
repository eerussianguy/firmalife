package com.eerussianguy.firmalife.common.items;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


import com.eerussianguy.firmalife.Firmalife;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.DecayingItem;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.TFCItemGroup.MISC;

@SuppressWarnings("unused")
public class FLItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Firmalife.MOD_ID);

    public static final EnumSet<Food> TFC_FRUITS = EnumSet.of(Food.BANANA, Food.BLACKBERRY, Food.BLUEBERRY, Food.BUNCHBERRY, Food.CHERRY, Food.CLOUDBERRY, Food.CRANBERRY, Food.ELDERBERRY, Food.GOOSEBERRY, Food.GREEN_APPLE, Food.LEMON, Food.OLIVE, Food.ORANGE, Food.PEACH, Food.PLUM, Food.RASPBERRY, Food.RED_APPLE, Food.SNOWBERRY, Food.STRAWBERRY, Food.WINTERGREEN_BERRY);

    public static final RegistryObject<Item> BEEHIVE_FRAME = register("beehive_frame", () -> new BeehiveFrameItem(prop()));
    public static final RegistryObject<Item> BEESWAX = register("beeswax", MISC);
    public static final RegistryObject<Item> CINNAMON_BARK = register("cinnamon_bark", MISC);
    public static final RegistryObject<Item> CHEESECLOTH = register("cheesecloth", MISC);
    public static final RegistryObject<Item> FRUIT_LEAF = register("fruit_leaf", MISC);
    public static final RegistryObject<Item> EMPTY_JAR = register("empty_jar", MISC);
    public static final RegistryObject<Item> PEEL = register("peel", MISC);
    public static final RegistryObject<Item> PINEAPPLE_LEATHER = register("pineapple_leather", MISC);
    public static final RegistryObject<Item> PINEAPPLE_YARN = register("pineapple_yarn", MISC);
    public static final RegistryObject<Item> RAW_HONEY = register("raw_honey", MISC);
    public static final RegistryObject<Item> RENNET = register("rennet", MISC);
    public static final RegistryObject<Item> TREATED_LUMBER = register("treated_lumber", MISC);
    public static final RegistryObject<Item> WATERING_CAN = register("watering_can", () -> new WateringCanItem(prop()));

    public static final Map<Spice, RegistryObject<Item>> SPICES = Helpers.mapOfKeys(Spice.class, spice -> register("spice/" + spice.name(), MISC));
    public static final Map<FLFood, RegistryObject<Item>> FOODS = Helpers.mapOfKeys(FLFood.class, food -> register("food/" + food.name(), () -> new DecayingItem(new Item.Properties().food(food.getFoodProperties()).tab(TFCItemGroup.FOOD))));

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

    private static RegistryObject<Item> register(String name, CreativeModeTab group)
    {
        return register(name, () -> new Item(new Item.Properties().tab(group)));
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item)
    {
        return ITEMS.register(name.toLowerCase(Locale.ROOT), item);
    }
}
