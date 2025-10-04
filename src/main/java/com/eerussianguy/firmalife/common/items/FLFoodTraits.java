package com.eerussianguy.firmalife.common.items;

import java.util.Locale;
import java.util.Set;
import com.eerussianguy.firmalife.config.FLConfig;
import com.google.common.collect.ImmutableSet;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.common.component.food.FoodTraits;

public class FLFoodTraits
{
    public enum Default
    {
        DRIED(0.5f),
        FRESH(1.1f),
        AGED(0.9f),
        VINTAGE(0.6f),
        OVEN_BAKED(0.9f),
        SMOKED(0.7f),
        RANCID_SMOKED(2.0f),
        SHELVED(0.4f),
        SHELVED_2(0.35f),
        SHELVED_3(0.25f),
        HUNG(0.35f),
        HUNG_2(0.3f),
        HUNG_3(0.25f),
        FERMENTED(0.25f),
        BEE_POLLINATED(0.8f),
        DIRT_GROWN(0.9f),
        GRAVEL_GROWN(0.8f),
        SLOPE_GROWN(0.8f),
        ;

        private final float mod;
        private final String name;

        Default(float mod)
        {
            this.mod = mod;
            this.name = name().toLowerCase(Locale.ROOT);
        }

        public String getName()
        {
            return name;
        }

        public String getCapitalizedName()
        {
            return name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
        }

        public float getMod()
        {
            return mod;
        }
    }


    public static void init() { }

    public static final DeferredHolder<FoodTrait, FoodTrait> DRIED = register(Default.DRIED);
    public static final DeferredHolder<FoodTrait, FoodTrait> FRESH = register(Default.FRESH);
    public static final DeferredHolder<FoodTrait, FoodTrait> AGED = register(Default.AGED);
    public static final DeferredHolder<FoodTrait, FoodTrait> VINTAGE = register(Default.VINTAGE);
    public static final DeferredHolder<FoodTrait, FoodTrait> OVEN_BAKED = register(Default.OVEN_BAKED);
    public static final DeferredHolder<FoodTrait, FoodTrait> SMOKED = register(Default.SMOKED);
    public static final DeferredHolder<FoodTrait, FoodTrait> RANCID_SMOKED = register(Default.RANCID_SMOKED);
    public static final DeferredHolder<FoodTrait, FoodTrait> SHELVED = register(Default.SHELVED);
    public static final DeferredHolder<FoodTrait, FoodTrait> SHELVED_2 = register(Default.SHELVED_2);
    public static final DeferredHolder<FoodTrait, FoodTrait> SHELVED_3 = register(Default.SHELVED_3);
    public static final DeferredHolder<FoodTrait, FoodTrait> HUNG = register(Default.HUNG);
    public static final DeferredHolder<FoodTrait, FoodTrait> HUNG_2 = register(Default.HUNG_2);
    public static final DeferredHolder<FoodTrait, FoodTrait> HUNG_3 = register(Default.HUNG_3);
    public static final DeferredHolder<FoodTrait, FoodTrait> FERMENTED = register(Default.FERMENTED);
    public static final DeferredHolder<FoodTrait, FoodTrait> BEE_POLLINATED = register(Default.BEE_POLLINATED);
    public static final DeferredHolder<FoodTrait, FoodTrait> DIRT_GROWN = register(Default.DIRT_GROWN);
    public static final DeferredHolder<FoodTrait, FoodTrait> GRAVEL_GROWN = register(Default.GRAVEL_GROWN);
    public static final DeferredHolder<FoodTrait, FoodTrait> SLOPE_GROWN = register(Default.SLOPE_GROWN);

    public static final Set<DeferredHolder<FoodTrait, FoodTrait>> WINE_TRAITS = ImmutableSet.of(BEE_POLLINATED, DIRT_GROWN, GRAVEL_GROWN, SLOPE_GROWN);

    private static DeferredHolder<FoodTrait, FoodTrait> register(FLFoodTraits.Default trait)
    {
        return FoodTraits.TRAITS.register(trait.name.toLowerCase(Locale.ROOT), () -> new FoodTrait(FLConfig.SERVER.foodTraits.get(trait), "tfc.tooltip.food_trait." + trait.name.toLowerCase(Locale.ROOT)));
    }

}
