package com.eerussianguy.firmalife.compat.jei;

import java.util.List;
import java.util.Locale;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.common.blocks.greenhouse.PlanterType;
import com.eerussianguy.firmalife.common.util.GreenhouseType;
import com.eerussianguy.firmalife.common.util.Plantable;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;

import net.dries007.tfc.compat.jei.JEIIntegration;
import net.dries007.tfc.compat.jei.category.BaseRecipeCategory;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnnecessaryLocalVariable")
public class GreenhousePlantableCategory extends AbstractRecipeCategory<Plantable>
{
    private static final int WIDTH = 200;
    private static final int HEIGHT = 65;

    protected final IDrawableStatic slot;
    protected final IDrawableStatic arrow;
    protected final IDrawableAnimated arrowAnimated;

    public GreenhousePlantableCategory(RecipeType<Plantable> type, IGuiHelper helper)
    {
        super(type, Component.translatable("tfc.jei.greenhouse_plantable"), helper.createDrawableIngredient(JEIIntegration.ITEM_STACK, new ItemStack(FLBlocks.LARGE_PLANTER.get())), WIDTH, HEIGHT);
        this.slot = helper.getSlotDrawable();
        this.arrow = helper.createDrawable(BaseRecipeCategory.ICONS, 0, 14, 22, 16);
        this.arrowAnimated = helper.createAnimatedDrawable(helper.createDrawable(BaseRecipeCategory.ICONS, 22, 14, 22, 16), 80, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public @Nullable ResourceLocation getRegistryName(Plantable recipe)
    {
        return Plantable.MANAGER.getId(recipe);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Plantable recipe, IFocusGroup focuses)
    {
        PlanterType planter = recipe.planter();
        ItemStack seed = recipe.getSeed();
        ItemStack crop = recipe.getCrop();
        float extraSeedChance = recipe.extraSeedChance();

        int offset = 6;
        int slotSize = 18;
        int slotCentering = (offset / 2) + (slotSize / 2);
        int slotSpacing = offset + slotSize;

        int ingredientX = offset * 2;
        int ingredientY = 15;
        int planterX = ingredientX;
        int planterY = ingredientY + slotSize;

        int arrowY = ingredientY + (slotSize / 2) + 8;

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

        // Input Seed
        builder.addSlot(RecipeIngredientRole.INPUT, ingredientX + 1, ingredientY + 1)
            .setBackground(this.slot, -1, -1)
            .addIngredients(recipe.ingredient());

        // Planter
        builder.addSlot(RecipeIngredientRole.CATALYST, planterX + 1, planterY + 1)
            .addItemStack(new ItemStack(planter.resolvePlanterBlock()))
            .setCustomRenderer(VanillaTypes.ITEM_STACK, new IIngredientRenderer<>()
            {
                @Override
                public void render(GuiGraphics graphics, ItemStack ingredient)
                {
                }

                @Override
                public List<Component> getTooltip(ItemStack ingredient, TooltipFlag tooltipFlag)
                {
                    Minecraft mc = Minecraft.getInstance();
                    return ingredient.isEmpty() ? List.of() : ingredient.getTooltipLines(Item.TooltipContext.of(mc.level), mc.player, tooltipFlag);
                }
            });

        // Crop Output
        if (!crop.isEmpty())
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, cropX + 1, cropY + 1)
                .setBackground(this.slot, -1, -1)
                .addItemStack(crop);
        }

        // Seed Output
        if (!seed.isEmpty())
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, seed1X + 1, seed1Y + 1)
                .setBackground(this.slot, -1, -1)
                .addItemStack(seed);
        }

        // Bonus Seed Output
        if (!seed.isEmpty() && extraSeedChance > 0)
        {
            builder.addSlot(RecipeIngredientRole.OUTPUT, seed2X + 1, seed2Y + 1)
                .setBackground(this.slot, -1, -1)
                .addItemStack(seed)
                .addRichTooltipCallback((recipeSlotView, tooltip) -> tooltip.add(Component.translatable("firmalife.tooltip.chance", String.format(Locale.ROOT, "%.0f%%", extraSeedChance * 100))));
        }
    }

    @Override
    public void draw(Plantable recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY)
    {
        Font font = Minecraft.getInstance().font;

        int offset = 6;
        int slotSize = 18;
        int textHeight = font.lineHeight;

        int ingredientX = offset * 2;
        int ingredientY = 15;
        int planterX = ingredientX;
        int planterY = ingredientY + slotSize;

        PlanterType planter = recipe.planter();

        // Hanging planters switch the planter and ingredient positions.
        if (planter == PlanterType.HANGING)
        {
            planterY = ingredientY;
        }

        // Trellis planters get separated a bit more to look better.
        if (planter == PlanterType.TRELLIS)
        {
            planterY += (offset / 2);
        }

        renderPlanter(graphics, planter.resolvePlanterBlock().defaultBlockState().setValue(FLStateProperties.WATERED, true), planterX, planterY);

        int nutrientX = ingredientX + slotSize + 20;
        int nutrientY = 2;

        int arrowX = (WIDTH / 2) - (slotSize / 2);
        int arrowY = 15 + (slotSize / 2) + 8;

        Component tierText = Component.translatable("firmalife.tooltip.minimum_tier", GreenhouseType.getGreenhouseNameByTier(recipe.tier()));
        int tierTextWidth = font.width(tierText);
        int tierX = arrowX + (slotSize / 2) - (tierTextWidth / 2);
        int tierY = arrowY + 20;

        arrow.draw(graphics, arrowX, arrowY);
        arrowAnimated.draw(graphics, arrowX, arrowY);

        graphics.drawString(font, tierText, tierX, tierY, 0xFFFFFF, true);

        final Plantable.NutrientList nutrient = recipe.getNutrients();
        final float n = nutrient.nitrogen();
        final float p = nutrient.phosphorous();
        final float k = nutrient.potassium();
        final boolean consumesNutrients = n > 0 || p > 0 || k > 0;

        graphics.drawString(font, Component.translatable("tfc.tooltip.fertilizer.nitrogen", Plantable.formatNutrientAmount(n, consumesNutrients)), nutrientX, nutrientY, 0xFFFFFF, true);
        nutrientY += textHeight;
        graphics.drawString(font, Component.translatable("tfc.tooltip.fertilizer.phosphorus", Plantable.formatNutrientAmount(p, consumesNutrients)), nutrientX, nutrientY, 0xFFFFFF, true);
        nutrientY += textHeight;
        graphics.drawString(font, Component.translatable("tfc.tooltip.fertilizer.potassium", Plantable.formatNutrientAmount(k, consumesNutrients)), nutrientX, nutrientY, 0xFFFFFF, true);
    }

    private void renderPlanter(GuiGraphics graphics, BlockState blockState, int x, int y)
    {
        if (blockState.isAir())
            return;

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(x + 9, y + 9, 100);
        poseStack.mulPose(Axis.XP.rotationDegrees(-25f));
        poseStack.mulPose(Axis.YP.rotationDegrees(45f));
        poseStack.scale(20, -20, 20);
        poseStack.translate(-0.5, -0.5, -0.5);

        RenderType renderType = ItemBlockRenderTypes.getRenderType(blockState, false);
        if (blockState.getRenderShape() != RenderShape.MODEL)
        {
            renderType = RenderType.solid();
        }

        MultiBufferSource.BufferSource bufferSource = graphics.bufferSource();

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
            blockState,
            poseStack,
            bufferSource,
            LightTexture.FULL_BLOCK,
            OverlayTexture.NO_OVERLAY,
            ModelData.EMPTY,
            renderType);

        bufferSource.endBatch(renderType);

        poseStack.popPose();
    }
}
