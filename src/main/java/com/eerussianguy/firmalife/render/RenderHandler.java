package com.eerussianguy.firmalife.render;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import com.eerussianguy.firmalife.blocks.BlockClimateStation;
import com.eerussianguy.firmalife.util.ClientHelpers;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = MOD_ID, value = Side.CLIENT)
public class RenderHandler
{
    public static Queue<Runnable> TO_RUN = new LinkedList<>();

    @SubscribeEvent
    public static void onRenderTick(RenderWorldLastEvent event)
    {
        Runnable runnable = TO_RUN.poll();
        if (runnable != null)
        {
            EntityPlayerSP entity = Minecraft.getMinecraft().player;
            Vec3d trans = ClientHelpers.getEntityMovementPartial(entity, event.getPartialTicks());

            GlStateManager.disableAlpha();
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.setTranslation(-trans.x, -trans.y, -trans.z);
            runnable.run();
            buffer.setTranslation(0.0D, 0.0D, 0.0D);
            GlStateManager.enableAlpha();
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        final ItemStack stack = event.getItemStack();
        final Item item = stack.getItem();
        if (item instanceof ItemBlock)
        {
            Block block = ((ItemBlock) item).getBlock();
            if (block instanceof BlockClimateStation)
            {
                BlockClimateStation station = (BlockClimateStation) block;
                switch (station.tier)
                {
                    case 1:
                        event.getToolTip().add("Enables enhanced flaw detection for your greenhouse.");
                        event.getToolTip().add("Right click to show either the protected region, or the incorrect block.");
                        break;
                    case 2:
                        event.getToolTip().add("Enhanced climate regulation makes planters grow 10.5% faster.");
                        break;
                    case 3:
                        event.getToolTip().add("Enables growing grains in the greenhouse.");
                        break;
                    case 4:
                        event.getToolTip().add("Enables growing fruit trees in the greenhouse.");
                        break;
                    case 5:
                        event.getToolTip().add("Distributes steam to your spouts and sprinklers, eliminating the need to feed them with barrels.");
                        break;
                }
            }
        }
    }
}
