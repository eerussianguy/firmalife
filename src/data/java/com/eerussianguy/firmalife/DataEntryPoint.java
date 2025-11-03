package com.eerussianguy.firmalife;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.capabilities.FLComponents;
import com.eerussianguy.firmalife.common.misc.FLDamageTypes;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.GreenhouseType;
import com.eerussianguy.firmalife.providers.BuiltinBlockTags;
import com.eerussianguy.firmalife.providers.BuiltinClimateRanges;
import com.eerussianguy.firmalife.providers.BuiltinDamageTypes;
import com.eerussianguy.firmalife.providers.BuiltinFoods;
import com.eerussianguy.firmalife.providers.BuiltinPlantables;
import com.eerussianguy.firmalife.providers.BuiltinRecipes;
import com.eerussianguy.firmalife.providers.DataManagerProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.util.data.Drinkable;
import net.dries007.tfc.util.data.LampFuel;

import static com.eerussianguy.firmalife.FirmaLife.*;

@EventBusSubscriber(modid = MOD_ID)
public class DataEntryPoint
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        final PackOutput output = event.getGenerator().getPackOutput();
        final var lookup = add(event, new DatapackBuiltinEntriesProvider(
            event.getGenerator().getPackOutput(), event.getLookupProvider(),
            new RegistrySetBuilder()
                .add(Registries.DAMAGE_TYPE, BuiltinDamageTypes::new)
            , Set.of(MOD_ID, TerraFirmaCraft.MOD_ID, "minecraft")
        )).getRegistryProvider();


        final var blockTags = add(event, new BuiltinBlockTags(event, lookup)).contentsGetter();

        tags(event, Registries.DAMAGE_TYPE, lookup, (provider, tags) -> {
            tags.tag(DamageTypeTags.BYPASSES_ARMOR).add(FLDamageTypes.OVEN, FLDamageTypes.SWARM);
            tags.tag(DamageTypeTags.BYPASSES_EFFECTS).add(FLDamageTypes.SWARM);
        });

        add(event, new BuiltinClimateRanges(output, lookup));
        add(event, new BuiltinPlantables(output, lookup));
        add(event, new BuiltinFoods(output, lookup));
        add(event, new DataManagerProvider<LampFuel>(LampFuel.MANAGER, output, lookup)
        {

            @Override
            protected void addData(HolderLookup.Provider provider)
            {
                add("soybean_oil", new LampFuel(FluidIngredient.of(FLFluids.EXTRA_FLUIDS.get(ExtraFluid.SOYBEAN_OIL).getSource()), BlockIngredient.of(TFCTags.Blocks.LAMPS), 7000));
            }
        });
        add(event, new DataManagerProvider<Drinkable>(Drinkable.MANAGER, output, lookup)
        {
            @Override
            protected void addData(HolderLookup.Provider provider)
            {
                add("chocolate", new Drinkable(FluidIngredient.of(FLFluids.EXTRA_FLUIDS.get(ExtraFluid.CHOCOLATE).getSource()), 0, false, FoodData.ofDrink(10, 0), List.of()));
            }
        });
        add(event, new DataManagerProvider<GreenhouseType>(GreenhouseType.MANAGER, output, lookup)
        {
            @Override
            protected void addData(HolderLookup.Provider provider)
            {
                add("treated_wood", new GreenhouseType(BlockIngredient.of(FLTags.Blocks.ALL_TREATED_WOOD_GREENHOUSE), 5, Component.translatable("greenhouse.firmalife.treated_wood")));
                add("copper", new GreenhouseType(BlockIngredient.of(FLTags.Blocks.ALL_COPPER_GREENHOUSE), 10, Component.translatable("greenhouse.firmalife.copper")));
                add("iron", new GreenhouseType(BlockIngredient.of(FLTags.Blocks.ALL_IRON_GREENHOUSE), 15, Component.translatable("greenhouse.firmalife.iron")));
                add("stainless_steel", new GreenhouseType(BlockIngredient.of(FLTags.Blocks.STAINLESS_STEEL_GREENHOUSE), 20, Component.translatable("greenhouse.firmalife.stainless_steel")));
            }
        });

        add(event, new BuiltinRecipes(output, lookup));
    }

    private static <T extends DataProvider> T add(GatherDataEvent event, T provider)
    {
        return event.getGenerator().addProvider(true, provider);
    }

    private static <T> void tags(GatherDataEvent event, ResourceKey<Registry<T>> registry, CompletableFuture<HolderLookup.Provider> lookup, BiConsumer<HolderLookup.Provider, TagLookup<T>> callback)
    {
        add(event, new TagsProvider<T>(event.getGenerator().getPackOutput(), registry, lookup, MOD_ID, event.getExistingFileHelper())
        {
            @Override
            protected void addTags(HolderLookup.Provider provider)
            {
                callback.accept(provider, this::tag);
            }
        });
    }

    @FunctionalInterface
    interface TagLookup<T>
    {
        TagsProvider.TagAppender<T> tag(TagKey<T> tag);
    }
}
