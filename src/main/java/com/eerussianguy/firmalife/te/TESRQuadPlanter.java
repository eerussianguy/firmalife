package com.eerussianguy.firmalife.te;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import com.eerussianguy.firmalife.init.PlanterRegistry;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class TESRQuadPlanter extends TileEntitySpecialRenderer<TEQuadPlanter>
{
    private final ModelSmallPlant model = new ModelSmallPlant();

    @Override
    public void render(TEQuadPlanter te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        if (te.hasWorld())
        {
            ResourceLocation texture2 = new ResourceLocation(MOD_ID, "textures/blocks/crop/beet_6.png");
            bindTexture(texture2);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.5F, 0.0F);
            GlStateManager.enableAlpha();
            model.render(1.0f);
            GlStateManager.popMatrix();
            /*IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (cap != null)
            {
                ItemStack slotItem = cap.getStackInSlot(0);
                if (!slotItem.isEmpty())
                {
                    PlanterRegistry plant = PlanterRegistry.get(slotItem);
                    if (plant != null)
                    {
                        ResourceLocation name = plant.getRegistryName();
                        if (name != null)
                        {
                            ResourceLocation texture = new ResourceLocation(MOD_ID, "textures/blocks/crop/" + name.getPath() + "_" + te.getStage(0) + ".png");
                            ResourceLocation texture2 = new ResourceLocation(MOD_ID, "textures/blocks/climate_station.png");
                            this.bindTexture(texture2);

                            GlStateManager.pushMatrix();
                            GlStateManager.translate(0.0F, 0.5F, 0.0F);
                            GlStateManager.enableAlpha();
                            this.model.render(1.0f);
                            GlStateManager.popMatrix();
                        }
                    }
                }
                for (int slot = 0; slot < 4; slot++)
                {
                    // do something
                }
            }*/

        }
    }
}
