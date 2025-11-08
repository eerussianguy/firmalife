package com.eerussianguy.firmalife.providers;

import java.util.concurrent.CompletableFuture;
import com.eerussianguy.firmalife.Accessors;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.data.FluidHeat;

public class BuiltinFluidHeats extends DataManagerProvider<FluidHeat> implements Accessors
{
    public static final float HEAT_CAPACITY = 0.003f;

    public BuiltinFluidHeats(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(FluidHeat.MANAGER, output, lookup, TerraFirmaCraft.MOD_ID);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add(FLMetal.CHROMIUM, 0.35f, 1250);
        add(FLMetal.STAINLESS_STEEL, 0.35f, 1540);
    }

    private void add(FLMetal metal, float baseHeatCapacity, float meltTemperature)
    {
        add(metal.getSerializedName(), new FluidHeat(FLFluids.METALS.get(metal).getSource(), meltTemperature, HEAT_CAPACITY / baseHeatCapacity));
    }

}
