// Code mostly copied from net.dries007.tfc.util.agriculture.Crop

/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package com.eerussianguy.firmalife.init;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.eerussianguy.firmalife.registry.BlocksFL;
import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.classic.worldgen.WorldGenWildCrops;

public enum StemCrop implements ICrop
{


    // temp definitions copied from tfc crops
    // pumpkin is maize
    // melon is sugarcane
    PUMPKIN(() -> BlocksFL.PUMPKIN_FRUIT, 10f, 19f, 40f, 45f, 110f, 140f, 400f, 450f, 6, 0.6f),
    MELON(() -> BlocksFL.MELON_FRUIT, 12f, 20f, 38f, 45f, 50f, 160f, 410f, 450f, 8, 0.5f);

    static
    {
        for (ICrop crop : values())
        {
            WorldGenWildCrops.register(crop);
        }
    }

    // block to spawn when grown
    private final Supplier<Block> cropBlock;
    // temperature compatibility range
    private final float tempMinAlive, tempMinGrow, tempMaxGrow, tempMaxAlive;
    // rainfall compatibility range
    private final float rainMinAlive, rainMinGrow, rainMaxGrow, rainMaxAlive;
    // growth
    private final int growthStages; // the number of blockstates the crop has for growing, ignoring wild state
    private final float growthTime; // Time is measured in % of months, scales with calendar month length

    StemCrop(Supplier<Block> cropBlock, float tempMinAlive, float tempMinGrow, float tempMaxGrow, float tempMaxAlive, float rainMinAlive, float rainMinGrow, float rainMaxGrow, float rainMaxAlive, int growthStages, float growthTime)
    {
        this.cropBlock = cropBlock;

        this.tempMinAlive = tempMinAlive;
        this.tempMinGrow = tempMinGrow;
        this.tempMaxGrow = tempMaxGrow;
        this.tempMaxAlive = tempMaxAlive;

        this.rainMinAlive = rainMinAlive;
        this.rainMinGrow = rainMinGrow;
        this.rainMaxGrow = rainMaxGrow;
        this.rainMaxAlive = rainMaxAlive;

        this.growthStages = growthStages;
        this.growthTime = growthTime; // This is measured in % of months
    }

    // crop life behavior copied from base tfc
    @Override
    public long getGrowthTicks()
    {
        return (long) (growthTime * CalendarTFC.CALENDAR_TIME.getDaysInMonth() * ICalendar.TICKS_IN_DAY);
    }

    @Override
    public int getMaxStage()
    {
        return growthStages - 1;
    }

    @Override
    public boolean isValidConditions(float temperature, float rainfall)
    {
        return tempMinAlive < temperature && temperature < tempMaxAlive && rainMinAlive < rainfall && rainfall < rainMaxAlive;
    }

    @Override
    public boolean isValidForGrowth(float temperature, float rainfall)
    {
        return tempMinGrow < temperature && temperature < tempMaxGrow && rainMinGrow < rainfall && rainfall < rainMaxGrow;
    }
    // end crop life behavior copied from base tfc

    //crop itself shouldn't drop anything
    @Nonnull
    @Override
    public ItemStack getFoodDrop(int currentStage)
    {
        return ItemStack.EMPTY;
    }

    public Block getCropBlock()
    {
        return cropBlock.get();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInfo(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        if (GuiScreen.isShiftKeyDown())
        {
            tooltip.add(TextFormatting.GRAY + I18n.format("tfc.tooltip.climate_info"));
            tooltip.add(TextFormatting.BLUE + I18n.format("tfc.tooltip.climate_info_rainfall", (int) rainMinGrow, (int) rainMaxGrow));
            tooltip.add(TextFormatting.GOLD + I18n.format("tfc.tooltip.climate_info_temperature", String.format("%.1f", tempMinGrow), String.format("%.1f", tempMaxGrow)));
        }
        else
        {
            tooltip.add(TextFormatting.GRAY + I18n.format("tfc.tooltip.hold_shift_for_climate_info"));
        }
    }
}
