package com.eerussianguy.firmalife.providers;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import com.eerussianguy.firmalife.Accessors;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.fluids.crafting.CompoundFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SingleFluidIngredient;
import net.neoforged.neoforge.fluids.crafting.TagFluidIngredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.fluids.FluidHolder;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;

import static com.eerussianguy.firmalife.common.FLTags.Fluids.*;

public class BuiltinFluidTags extends TagsProvider<Fluid> implements Accessors
{
    private final ExistingFileHelper.IResourceType resourceType;

    public BuiltinFluidTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(event.getGenerator().getPackOutput(), Registries.FLUID, lookup, FirmaLife.MOD_ID, event.getExistingFileHelper());
        this.resourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", Registries.tagsDirPath(registryKey));
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(USABLE_IN_MIXING_BOWL).addTag(TFCTags.Fluids.USABLE_IN_POT);
        tag(USABLE_IN_HOLLOW_SHELL).addTag(TFCTags.Fluids.USABLE_IN_WOODEN_BUCKET);
        tag(USABLE_IN_VAT).addTag(TFCTags.Fluids.USABLE_IN_POT);
        tag(USABLE_IN_WINE_GLASS).addTag(TFCTags.Fluids.DRINKABLES);
        tag(WINE).add(FLFluids.WINE_FLUIDS);
        tag(MILKS).add(
            FLFluids.EXTRA_FLUIDS.get(ExtraFluid.GOAT_MILK).getSource(),
            FLFluids.EXTRA_FLUIDS.get(ExtraFluid.YAK_MILK).getSource(),
            NeoForgeMod.MILK.get()
        );
        tag(OILS).add(
            TFCFluids.SIMPLE_FLUIDS.get(SimpleFluid.OLIVE_OIL).getSource(),
            TFCFluids.SIMPLE_FLUIDS.get(SimpleFluid.CANOLA_OIL).getSource(),
            FLFluids.EXTRA_FLUIDS.get(ExtraFluid.SOYBEAN_OIL).getSource()
        );
        tag(TFCTags.Fluids.ALCOHOLS).add(
            FLFluids.EXTRA_FLUIDS.get(ExtraFluid.PINA_COLADA).getSource(),
            FLFluids.EXTRA_FLUIDS.get(ExtraFluid.MEAD).getSource()
        ).addTag(WINE);
        tag(TFCTags.Fluids.DRINKABLES).add(
            FLFluids.EXTRA_FLUIDS.get(ExtraFluid.CHOCOLATE).getSource()
        );
        tag(TFCTags.Fluids.INGREDIENTS).add(FLFluids.EXTRA_FLUIDS);
        tag(TFCTags.Fluids.MOLTEN_METALS).add(FLFluids.METALS);
    }

    @Override
    protected FluidTagAppender tag(TagKey<Fluid> tag)
    {
        return new FluidTagAppender(getOrCreateRawBuilder(tag), modId);
    }

    @SuppressWarnings("UnusedReturnValue")
    static class FluidTagAppender extends TagAppender<Fluid> implements Accessors
    {
        FluidTagAppender(TagBuilder builder, String modId)
        {
            super(builder);
        }

        FluidTagAppender add(Fluid... fluids) { return add(Arrays.stream(fluids)); }
        FluidTagAppender add(Stream<Fluid> fluids) { fluids.forEach(b -> add(key(b))); return this; }
        FluidTagAppender add(Map<?, ? extends FluidHolder<? extends Fluid>> fluids) { fluids.values().forEach(v -> add(v.getSource())); return this; }
        FluidTagAppender add(FluidIngredient ingredient)
        {
            switch (ingredient)
            {
                case TagFluidIngredient tag -> addTag(tag.tag());
                case SingleFluidIngredient item -> add(item.fluid().value());
                case CompoundFluidIngredient comp -> comp.children().forEach(this::add);
                default -> throw new AssertionError("Unhandled ingredient type: " + ingredient);
            }
            return this;
        }

        @Override public FluidTagAppender addTag(TagKey<Fluid> tag) { return (FluidTagAppender) super.addTag(tag); }

        private ResourceKey<Fluid> key(Fluid fluid)
        {
            return BuiltInRegistries.FLUID.getResourceKey(fluid).orElseThrow();
        }
    }
}
