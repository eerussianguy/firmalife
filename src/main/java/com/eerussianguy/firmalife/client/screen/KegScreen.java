package com.eerussianguy.firmalife.client.screen;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.KegBlockEntity;
import com.eerussianguy.firmalife.common.blocks.KegCoreBlock;
import com.eerussianguy.firmalife.common.container.KegContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.screen.BlockEntityScreen;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.tooltip.Tooltips;


public class KegScreen extends BlockEntityScreen<KegBlockEntity, KegContainer>
{
    private static final ResourceLocation BACKGROUND = FLHelpers.identifier("textures/gui/keg.png");

    private static final Component SEAL = Component.translatable(TerraFirmaCraft.MOD_ID + ".tooltip.seal_barrel");
    private static final Component UNSEAL = Component.translatable(TerraFirmaCraft.MOD_ID + ".tooltip.unseal_barrel");
    private static final int MAX_RECIPE_NAME_LENGTH = 100;

    public KegScreen(KegContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);
        imageHeight += 3 * 18 + 20;
        inventoryLabelY += 3 * 18 + 21;
    }

    @Override
    public void init()
    {
        super.init();
        addRenderableWidget(new KegSealButton(blockEntity, getGuiLeft(), getGuiTop(), isSealed() ? UNSEAL : SEAL));
    }


    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderLabels(graphics, mouseX, mouseY);
        if (isSealed())
        {
            drawDisabled(graphics, KegBlockEntity.SLOT_FLUID_CONTAINER_IN, KegBlockEntity.SLOT_INPUT_END);

            // Draw the text displaying both the seal date, and the recipe name
            final @Nullable Component recipe = blockEntity.getRecipeTooltip();
            if (recipe != null)
            {
                // todo 1.21: isn't there a method that draws a fixed-width string but moving back and forth for overlong strings?
                if (font.width(recipe) > MAX_RECIPE_NAME_LENGTH)
                {
                    int line = 0;
                    for (FormattedCharSequence text : font.split(recipe, MAX_RECIPE_NAME_LENGTH))
                    {
                        graphics.drawString(font, text, 10 + Math.floorDiv(MAX_RECIPE_NAME_LENGTH - font.width(text), 2), 120 + (line * font.lineHeight), 0x404040, false);
                        line++;
                    }
                }
                else
                {
                    graphics.drawString(font, recipe.getString(), 10 + Math.floorDiv(MAX_RECIPE_NAME_LENGTH - font.width(recipe), 2), 128, 0x404040, false);
                }
            }
            final String date = Calendars.CLIENT.getExactTimeAndDate(blockEntity.getSealedTick()).getString();
            graphics.drawString(font, date, imageWidth / 2 - font.width(date) / 2, 138, 0x404040, false);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);
        final FluidStack fluidStack = blockEntity.getInventory().getFluidInTank(0);
        if (!fluidStack.isEmpty())
        {
            final TextureAtlasSprite sprite = RenderHelpers.getAndBindFluidSprite(fluidStack);
            final int fillHeight = (int) Math.ceil((float) 106 * fluidStack.getAmount() / (float) KegBlockEntity.CAPACITY);

            RenderHelpers.fillAreaWithSprite(graphics, sprite, leftPos + 8, topPos + 124 - fillHeight, 16, fillHeight, 16, 16);

            resetToBackgroundSprite();
        }

        graphics.blit(texture, getGuiLeft() + 7, getGuiTop() + 17, 176, 0, 18, 107);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);
        final int relX = mouseX - getGuiLeft();
        final int relY = mouseY - getGuiTop();

        if (relX >= 7 && relY >= 17 && relX < 7 + 18 && relY < 17 + 107)
        {
            final FluidStack fluid = blockEntity.getInventory().getFluidInTank(0);
            if (!fluid.isEmpty())
            {
                graphics.renderTooltip(font, Tooltips.fluidUnitsOf(fluid), mouseX, mouseY);
            }
        }
    }

    private boolean isSealed()
    {
        return blockEntity.getBlockState().getValue(KegCoreBlock.SEALED);
    }

}
