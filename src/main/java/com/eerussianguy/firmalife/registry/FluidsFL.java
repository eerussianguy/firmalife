package com.eerussianguy.firmalife.registry;

import javax.annotation.Nonnull;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.eerussianguy.firmalife.init.FoodDataFL;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.objects.fluids.properties.DrinkableProperty;
import net.dries007.tfc.objects.fluids.properties.FluidWrapper;

//duplicating the fluid registration logic from TFC as not to create conflicts
public final class FluidsFL
{
    private static final ResourceLocation STILL = new ResourceLocation(TerraFirmaCraft.MOD_ID, "blocks/fluid_still");
    private static final ResourceLocation FLOW = new ResourceLocation(TerraFirmaCraft.MOD_ID, "blocks/fluid_flow");

    private static final HashBiMap<Fluid, FluidWrapper> WRAPPERS = HashBiMap.create();

    public static FluidWrapper YEAST_STARTER;
    public static FluidWrapper COCONUT_MILK;
    public static FluidWrapper YAK_MILK;
    public static FluidWrapper GOAT_MILK;
    public static FluidWrapper ZEBU_MILK;

    private static ImmutableSet<FluidWrapper> allFiniteFluids;

    public static ImmutableSet<FluidWrapper> getAllFiniteFluids()
    {
        return allFiniteFluids;
    }

    public static void registerFluids()
    {
        DrinkableProperty milkProperty = player -> {
            if (player.getFoodStats() instanceof IFoodStatsTFC)
            {
                IFoodStatsTFC foodStats = (IFoodStatsTFC) player.getFoodStats();
                foodStats.addThirst(10);
                foodStats.getNutrition().addBuff(FoodData.MILK);
            }
        };

        allFiniteFluids = ImmutableSet.<FluidWrapper>builder().add(
            YEAST_STARTER = registerFluid(new Fluid("yeast_starter", STILL, FLOW, 0xFFa79464)),
            COCONUT_MILK = registerFluid(new Fluid("coconut_milk", STILL, FLOW, 0xFFfcfae2)).with(DrinkableProperty.DRINKABLE, milkProperty),
            YAK_MILK = registerFluid(new Fluid("yak_milk", STILL, FLOW, 0xFFfcfaec)).with(DrinkableProperty.DRINKABLE, milkProperty),
            GOAT_MILK = registerFluid(new Fluid("goat_milk", STILL, FLOW, 0xFFf6f6eb)).with(DrinkableProperty.DRINKABLE, milkProperty),
            ZEBU_MILK = registerFluid(new Fluid("zebu_milk", STILL, FLOW, 0xFFefede6)).with(DrinkableProperty.DRINKABLE, milkProperty)
        ).build();
    }

    private static FluidWrapper registerFluid(@Nonnull Fluid newFluid)
    {
        boolean isDefault = !FluidRegistry.isFluidRegistered(newFluid.getName());

        if (!isDefault)
        {
            // Fluid was already registered with this name, default to that fluid
            newFluid = FluidRegistry.getFluid(newFluid.getName());
        }
        else
        {
            // No fluid found, we are safe to register our default
            FluidRegistry.registerFluid(newFluid);
        }
        FluidRegistry.addBucketForFluid(newFluid);
        FluidWrapper properties = new FluidWrapper(newFluid, isDefault);
        WRAPPERS.put(newFluid, properties);
        return properties;
    }
}
