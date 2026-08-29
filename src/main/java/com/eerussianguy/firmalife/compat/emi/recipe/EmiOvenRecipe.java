package com.eerussianguy.firmalife.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.widget.AnimatedTextureWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.TextureWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import com.eerussianguy.firmalife.common.recipes.OvenRecipe;
import com.eerussianguy.firmalife.compat.emi.FLEmiPlugin;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendars;

public class EmiOvenRecipe extends EmiItemRecipe<OvenRecipe>
{
    private final float temperature;
    private final int duration;

    public EmiOvenRecipe(ResourceLocation id, OvenRecipe recipe)
    {
        super(FLEmiPlugin.OVEN, id, recipe, recipe.getIngredient(), recipe.getResult());
        temperature = recipe.getTemperature();
        duration = recipe.getDuration();
    }

    @Override
    protected void generateMiddleWidgets(WidgetLayout widgets, int y)
    {
        final EmiTexture empty = EmiTexture.EMPTY_FLAME;
        final EmiTexture full = EmiTexture.FULL_FLAME;
        final int x = widgets.last(Position.RIGHT, 6);

        widgets.add(new TextureWidget(empty.texture, x, y + 2, empty.width, empty.height, empty.u, empty.v));
        widgets.add(new AnimatedTextureWidget(full.texture, x, y + 2, full.width, full.height, full.u, full.v, 8000, false, true, true));
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        super.addWidgets(widgets);

        final @Nullable Component heat = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(temperature);
        if (heat != null)
        {
            widgets.addText(heat, getDisplayWidth() / 2, getMargin() + 2, 0xffffffff, true)
                .horizontalAlign(TextWidget.Alignment.CENTER)
                .verticalAlign(TextWidget.Alignment.CENTER);
        }
        widgets.addText(Calendars.CLIENT.getTimeDelta(duration), getDisplayWidth() / 2, getDisplayHeight() - 2, 0xffffffff, true)
            .horizontalAlign(TextWidget.Alignment.CENTER)
            .verticalAlign(TextWidget.Alignment.END);
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiOvenRecipe oven)
        {
            final int diff = Float.compare(temperature, oven.temperature);
            if (diff != 0)
            {
                return diff;
            }
        }
        return super.compareTo(other);
    }

    @Override
    protected int getPaddingTop()
    {
        return 10;
    }

    @Override
    protected int getPaddingBottom()
    {
        return 10;
    }

    @Override
    protected int getPaddingLeft()
    {
        return 10;
    }

    @Override
    protected int getPaddingRight()
    {
        return 10;
    }
}
