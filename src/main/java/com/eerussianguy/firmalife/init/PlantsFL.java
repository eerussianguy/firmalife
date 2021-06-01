package com.eerussianguy.firmalife.init;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.world.classic.worldgen.trees.TreeGenSequoia;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public class PlantsFL
{
    public static final List<Plant> WRAPPERS = new ArrayList<>(8);

    private static final int[] MAY = new int[]{0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0};
    private static final int[] JUNE = new int[]{0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0};
    private static final int[] JULY = new int[]{0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0};
    private static final int[] AUGUST = new int[]{0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0};

    public static final Tree CINNAMON_TREE = new Tree(new ResourceLocation(TerraFirmaCraft.MOD_ID, "cinnamon"), new TreeGenSequoia(), 28, 35, 280, 400, 0f, 1f, 0, 4, 15, 4, false, null, false, 15, 0, 0);

    private static final Plant BASIL = reg(new PlantWrapper("basil", JULY, 13.0F, 25.0F, 200.0F, 500.0F));
    private static final Plant BAY_LAUREL = reg(new PlantWrapper("bay_laurel", MAY, 5.0F, 18.0F, 200.0F, 500.0F));
    private static final Plant CARDAMOM = reg(new PlantWrapper("cardamom", JUNE, 16.0F, 30.0F, 100.0F, 400.0F));
    private static final Plant CILANTRO = reg(new PlantWrapper("cilantro", AUGUST, 14.0F, 31.0F, 300.0F, 500.0F));
    private static final Plant CUMIN = reg(new PlantWrapper("cumin", JULY, 10.0F, 30.0F, 150.0F, 350.0F));
    private static final Plant OREGANO = reg(new PlantWrapper("oregano", MAY, 14.0F, 28.0F, 50.0F, 300.0F));
    private static final Plant PIMENTO = reg(new PlantWrapper("pimento", JUNE, 10.0F, 18.0F, 100.0F, 300.0F));
    private static final Plant VANILLA = reg(new PlantWrapper("vanilla", JULY, 13.0F, 29.0F, 200.0F, 450.0F));

    private static PlantWrapper reg(PlantWrapper wrapper)
    {
        WRAPPERS.add(wrapper);
        return wrapper;
    }

    private static class PlantWrapper extends Plant
    {
        public PlantWrapper(@Nonnull String name, int[] stages, float minTemp, float maxTemp, float minRain, float maxRain)
        {
            super(new ResourceLocation(TerraFirmaCraft.MOD_ID, name), PlantType.STANDARD, stages, false, false, minTemp, maxTemp, minTemp, maxTemp, minRain, maxRain, 9, 15, 1, 0, 0, 0.8F, null);
        }
    }
}
