package com.eerussianguy.firmalife.common.items;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.Helpers;

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
        RAW(1f),
        SHELVED(0.4f),
        SHELVED_2(0.35f),
        SHELVED_3(0.25f),
        HUNG(0.35f),
        HUNG_2(0.3f),
        HUNG_3(0.25f)
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

    public static final FoodTrait DRIED = register(Default.DRIED);
    public static final FoodTrait FRESH = register(Default.FRESH);
    public static final FoodTrait AGED = register(Default.AGED);
    public static final FoodTrait VINTAGE = register(Default.VINTAGE);
    public static final FoodTrait OVEN_BAKED = register(Default.OVEN_BAKED);
    public static final FoodTrait SMOKED = register(Default.SMOKED);
    public static final FoodTrait RANCID_SMOKED = register(Default.RANCID_SMOKED);
    public static final FoodTrait RAW = register(Default.RAW);
    public static final FoodTrait SHELVED = register(Default.SHELVED);
    public static final FoodTrait SHELVED_2 = register(Default.SHELVED_2);
    public static final FoodTrait SHELVED_3 = register(Default.SHELVED_3);
    public static final FoodTrait HUNG = register(Default.HUNG);
    public static final FoodTrait HUNG_2 = register(Default.HUNG_2);
    public static final FoodTrait HUNG_3 = register(Default.HUNG_3);

    private static FoodTrait register(FLFoodTraits.Default trait)
    {
        return FoodTrait.register(FLHelpers.identifier(trait.name), new WrappedFT(() -> FLConfig.SERVER.foodTraits.get(trait).get().floatValue(), "firmalife.tooltip.food_trait." + trait.name));
    }

    private static class WrappedFT extends FoodTrait
    {
        private final Supplier<Float> decayModifier;
        @Nullable private final String translationKey;

        public WrappedFT(Supplier<Float> decayModifier, @Nullable String translationKey)
        {
            super(1f, translationKey);
            this.decayModifier = decayModifier;
            this.translationKey = translationKey;
        }

        @Override
        public float getDecayModifier()
        {
            return decayModifier.get();
        }

        @Override
        public void addTooltipInfo(ItemStack stack, List<Component> text)
        {
            if (this.translationKey != null)
            {
                MutableComponent component = Helpers.translatable(this.translationKey);
                if (this.decayModifier.get() > 1.0F)
                {
                    component.withStyle(ChatFormatting.RED);
                }

                text.add(component);
            }
        }

    }
}
