package com.eerussianguy.firmalife.client.screen;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.StovetopPotBlockEntity;
import com.eerussianguy.firmalife.common.container.StovetopPotContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.screen.BlockEntityScreen;
import net.dries007.tfc.common.capabilities.BlockCapabilities;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.tooltip.Tooltips;

public class StovetopPotScreen extends BlockEntityScreen<StovetopPotBlockEntity, StovetopPotContainer>
{
    private static final ResourceLocation BACKGROUND = FLHelpers.identifier("textures/gui/stovetop_pot.png");

    public StovetopPotScreen(StovetopPotContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, BACKGROUND);
        inventoryLabelY += 20;
        imageHeight += 20;
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderLabels(graphics, mouseX, mouseY);
        if (blockEntity.isBoiling())
        {
            drawDisabled(graphics, 0, StovetopPotBlockEntity.SLOTS - 1);
        }

        final String text;
        if (blockEntity.isBoiling())
        {
            text = I18n.get("tfc.tooltip.pot_boiling");
        }
//        else if (blockEntity.getOutput() != null && !blockEntity.getOutput().isEmpty())
//        {
//            text = I18n.get("tfc.tooltip.pot_finished");
//        }
        else
        {
            text = I18n.get("tfc.tooltip.pot_ready");
        }

        final int x = 118 - font.width(text) / 2;
        graphics.drawString(font, text, x, 56, 0x404040, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderTooltip(graphics, mouseX, mouseY);

        final int left = getGuiLeft(), top = getGuiTop();
        if (mouseX >= left + 54 && mouseY >= top + 48 && mouseX < left + 86 && mouseY < top + 74)
        {
            final IFluidHandler fluidHandler = Helpers.getCapability(BlockCapabilities.FLUID, blockEntity);
            final FluidStack fluid = fluidHandler != null ? fluidHandler.getFluidInTank(0) : FluidStack.EMPTY;
            if (!fluid.isEmpty())
            {
                graphics.renderTooltip(font, Tooltips.fluidUnitsAndCapacityOf(fluid, FluidHelpers.BUCKET_VOLUME), mouseX, mouseY);
            }
        }

        if (RenderHelpers.isInside(mouseX, mouseY, leftPos + 30, topPos + 76 - 51, 15, 51))
        {
            final var text = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(blockEntity.getTemperature());
            if (text != null)
            {
                graphics.renderTooltip(font, text, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);
        int temp = (int) (51 * blockEntity.getTemperature() / Heat.maxVisibleTemperature());
        if (temp > 0)
        {
            graphics.blit(texture, leftPos + 30, topPos + 76 - Math.min(51, temp), 176, 0, 15, 5);
        }
    }
}
