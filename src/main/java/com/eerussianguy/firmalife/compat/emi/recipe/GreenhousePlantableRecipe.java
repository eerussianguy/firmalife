package com.eerussianguy.firmalife.compat.emi.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.eerussianguy.firmalife.compat.emi.FLEmiPlugin;
import com.eerussianguy.firmalife.compat.emi.widget.EmiBlockWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import org.jetbrains.annotations.Nullable;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.common.blocks.greenhouse.PlanterType;
import com.eerussianguy.firmalife.common.util.GreenhouseType;
import com.eerussianguy.firmalife.common.util.Plantable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

@SuppressWarnings("UnnecessaryLocalVariable")
public class GreenhousePlantableRecipe implements EmiRecipe
{

    private static final int WIDTH = 200;
    private static final int HEIGHT = 65;

    private final Plantable plantable;
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final PlanterType planter;
    private final int tier;
    private final int stages;
    private final float extraSeedChance;
    private final ItemStack seed;
    private final ItemStack crop;
    private final Plantable.NutrientList nutrient;
    private final List<EmiIngredient> inputs;
    private final List<EmiIngredient> catalysts;
    private final List<EmiStack> outputs;

    public GreenhousePlantableRecipe(Plantable plantable)
    {
        this.plantable = plantable;
        this.id = Objects.requireNonNull(Plantable.MANAGER.getId(plantable));
        this.ingredient = plantable.ingredient();
        this.planter = plantable.planter();
        this.tier = plantable.tier();
        this.stages = plantable.stages();
        this.extraSeedChance = plantable.extraSeedChance();
        this.seed = plantable.getSeed();
        this.crop = plantable.getCrop();
        this.nutrient = plantable.getNutrients();

        this.inputs = List.of(EmiIngredient.of(this.ingredient));
        this.catalysts = List.of(EmiIngredient.of(Ingredient.of(resolvePlanterBlock(planter).asItem())));

        List<EmiStack> outputs = new ArrayList<>();
        if (!this.crop.isEmpty())
        {
            outputs.add(EmiStack.of(this.crop));
        }
        if (!this.seed.isEmpty())
        {
            outputs.add(EmiStack.of(this.seed));
        }
        this.outputs = outputs;
    }

    @Override
    public EmiRecipeCategory getCategory()
    {
        return FLEmiPlugin.GREENHOUSE_PLANTABLE;
    }

    @Override
    public @Nullable ResourceLocation getId()
    {
        return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "/greenhouse/" + id.getPath());
    }

    @Override
    public List<EmiIngredient> getInputs()
    {
        return inputs;
    }

    @Override
    public List<EmiIngredient> getCatalysts()
    {
        return catalysts;
    }

    @Override
    public List<EmiStack> getOutputs()
    {
        return outputs;
    }

    @Override
    public int getDisplayWidth()
    {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight()
    {
        return HEIGHT;
    }

    public Plantable getPlantable()
    {
        return this.plantable;
    }

    public Ingredient getIngredient()
    {
        return this.ingredient;
    }

    public int getTier()
    {
        return this.tier;
    }

    public int getStages()
    {
        return this.stages;
    }

    public ItemStack getSeed()
    {
        return this.seed;
    }

    public ItemStack getCrop()
    {
        return this.crop;
    }

    public Plantable.NutrientList getNutrients()
    {
        return this.nutrient;
    }

    public Plantable.NutrientList getNutrient()
    {
        return this.nutrient;
    }

    /**
     * Gets the planter block by {@link PlanterType}
     *
     * @param type Planter type.
     * @return Planter block.
     */
    public static Block resolvePlanterBlock(PlanterType type)
    {
        return type.resolvePlanterBlock();
    }

    /**
     * Gets the greenhouse component name by tier
     *
     * @param tier Tier number of the greenhouse.
     * @return Component name of the greenhouse.
     */
    public static Component getGreenhouseNameByTier(int tier)
    {
        return GreenhouseType.getGreenhouseNameByTier(tier);
    }

    /**
     * Creates a blank item widget that does not render anything.
     * Useful when you want to be able to hover over another element to view an item tooltip.
     *
     * @param holder  WidgetHolder.
     * @param stack   Item stack.
     * @param offsetX X-position on screen.
     * @param offsetY Y-position on screen.
     */
    public static void createBlankItemWidget(WidgetHolder holder, EmiIngredient stack, int offsetX, int offsetY)
    {
        SlotWidget widget = new SlotWidget(stack, offsetX, offsetY)
        {
            @Override
            public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta)
            {
            }
        };
        widget.drawBack(false);
        holder.add(widget);
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {

        Font font = Minecraft.getInstance().font;
        Component tierText = Component.translatable("firmalife.tooltip.minimum_tier", getGreenhouseNameByTier(tier));

        int offset = 6;
        int slotSize = 18;
        int textHeight = font.lineHeight;
        int slotCentering = (offset / 2) + (slotSize / 2);
        int slotSpacing = offset + slotSize;

        int ingredientX = offset * 2;
        int ingredientY = 15;
        int planterX = ingredientX;
        int planterY = ingredientY + slotSize;

        int nutrientX = ingredientX + slotSize + 20;
        int nutrientY = 2;

        int arrowX = (WIDTH / 2) - (slotSize / 2);
        int arrowY = ingredientY + (slotSize / 2) + 8;

        int tierTextWidth = font.width(tierText);
        int tierX = arrowX + (slotSize / 2) - (tierTextWidth / 2);
        int tierY = arrowY + 20;

        int cropX = WIDTH - (slotSize * 2) - (offset * 2);
        int cropY = arrowY - 2;

        int seed1X = cropX + slotSpacing;
        int seed1Y = cropY - slotCentering;
        int seed2X = seed1X;
        int seed2Y = seed1Y + slotSpacing;

        // Re-align crop slot if there is no seed output.
        if (seed.isEmpty())
        {
            cropX += slotSize;
        }
        // Re-align seed slot if there is no extra seed chance.
        if (!seed.isEmpty() && extraSeedChance == 0)
        {
            seed1Y = cropY;
        }

        // Hanging planters switch the planter and ingredient positions.
        if (planter == PlanterType.HANGING)
        {
            planterY = ingredientY;
            ingredientY = planterY + slotSize;
        }

        // Trellis planters get separated a bit more to look better.
        if (planter == PlanterType.TRELLIS)
        {
            planterY += (offset / 2);
            ingredientY -= (offset / 2);
        }

        // Input Seed Display.
        widgets.addSlot(EmiIngredient.of(ingredient), ingredientX, ingredientY);

        // Planter Display.
        createBlankItemWidget(widgets, EmiIngredient.of(Ingredient.of(resolvePlanterBlock(planter).asItem())), planterX, planterY);
        widgets.add(new EmiBlockWidget(resolvePlanterBlock(planter).defaultBlockState().setValue(FLStateProperties.WATERED, true), planterX, planterY, 100, 20, -25f, 45f, LightTexture.FULL_BLOCK));

        // Fertilizer Display.
        final float n = nutrient.nitrogen();
        final float p = nutrient.phosphorous();
        final float k = nutrient.potassium();
        final boolean consumesNutrients = n > 0 || p > 0 || k > 0;

        widgets.addText(Component.translatable("tfc.tooltip.fertilizer.nitrogen", Plantable.formatNutrientAmount(n, consumesNutrients)).getVisualOrderText(), nutrientX, nutrientY, 0xFFFFFF, true);
        nutrientY += textHeight;
        widgets.addText(Component.translatable("tfc.tooltip.fertilizer.phosphorus", Plantable.formatNutrientAmount(p, consumesNutrients)).getVisualOrderText(), nutrientX, nutrientY, 0xFFFFFF, true);
        nutrientY += textHeight;
        widgets.addText(Component.translatable("tfc.tooltip.fertilizer.potassium", Plantable.formatNutrientAmount(k, consumesNutrients)).getVisualOrderText(), nutrientX, nutrientY, 0xFFFFFF, true);

        // Arrow Display.
        widgets.addFillingArrow(arrowX, arrowY, 2000);

        // Greenhouse Tier Display.
        widgets.addText(tierText, tierX, tierY, 0xFFFFFF, true);

        // Output Crop Display.
        widgets.addSlot(EmiStack.of(crop), cropX, cropY).recipeContext(this);

        // Output Seed Display.
        if (!seed.isEmpty())
        {
            widgets.addSlot(EmiStack.of(seed), seed1X, seed1Y).recipeContext(this);
        }

        // Bonus Output Seed Display.
        if (!seed.isEmpty() && extraSeedChance > 0)
        {
            widgets.addSlot(EmiStack.of(seed).setChance(extraSeedChance), seed2X, seed2Y).recipeContext(this);
        }

    }
}
