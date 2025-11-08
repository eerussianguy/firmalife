package com.eerussianguy.firmalife;

import com.eerussianguy.firmalife.client.FLClientEvents;
import com.eerussianguy.firmalife.client.FLClientForgeEvents;
import com.eerussianguy.firmalife.common.FLCreativeTabs;
import com.eerussianguy.firmalife.common.FLEvents;
import com.eerussianguy.firmalife.common.FLForgeEvents;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.capabilities.FLComponents;
import com.eerussianguy.firmalife.common.container.FLMenuTypes;
import com.eerussianguy.firmalife.common.entities.FLEntities;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.misc.FLEffects;
import com.eerussianguy.firmalife.common.misc.FLInteractionManager;
import com.eerussianguy.firmalife.common.misc.FLLoot;
import com.eerussianguy.firmalife.common.misc.FLParticles;
import com.eerussianguy.firmalife.common.misc.FLSounds;
import com.eerussianguy.firmalife.common.recipes.FLRecipeSerializers;
import com.eerussianguy.firmalife.common.recipes.FLRecipeTypes;
import com.eerussianguy.firmalife.common.recipes.data.FLItemStackModifiers;
import com.eerussianguy.firmalife.common.util.FLAdvancements;
import com.eerussianguy.firmalife.common.util.FLArmorMaterials;
import com.eerussianguy.firmalife.common.util.FLDataManagers;
import com.eerussianguy.firmalife.common.worldgen.FLFeatures;
import com.eerussianguy.firmalife.compat.patchouli.FLPatchouliIntegration;
import com.eerussianguy.firmalife.compat.tooltip.TheOneProbeIntegration;
import com.eerussianguy.firmalife.config.FLConfig;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(FirmaLife.MOD_ID)
public class FirmaLife
{
    public static final String MOD_ID = "firmalife";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final boolean JEI = ModList.get().isLoaded("jei");
    public static final boolean EMI = ModList.get().isLoaded("emi");
    public static final boolean JADE = ModList.get().isLoaded("jade");
    public static final boolean THE_ONE_PROBE = ModList.get().isLoaded("theoneprobe");

    public FirmaLife(ModContainer mod, IEventBus bus)
    {
        FLItems.ITEM.register(bus);
        FLBlocks.BLOCK.register(bus);
        FLFluids.FLUID.register(bus);
        FLFluids.FLUID_TYPES.register(bus);
        FLBlockEntities.BLOCK_ENTITY.register(bus);
        FLRecipeTypes.RECIPE_TYPE.register(bus);
        FLRecipeTypes.POT_OUTPUT_TYPE.register(bus);
        FLRecipeSerializers.RECIPE_SERIALIZER.register(bus);
        FLItemStackModifiers.TYPES.register(bus);
        FLMenuTypes.MENU.register(bus);
        FLEffects.EFFECT.register(bus);
        FLEntities.ENTITY.register(bus);
        FLParticles.PARTICLE_TYPE.register(bus);
        FLFeatures.FEATURE.register(bus);
        FLSounds.SOUND.register(bus);
        FLCreativeTabs.CREATIVE_TAB.register(bus);
        FLAdvancements.TRIGGER_TYPE.register(bus);
        FLArmorMaterials.ARMOR_MATERIAL.register(bus);
        FLComponents.COMPONENT.register(bus);
        FLLoot.registerAll(bus);

        FLDataManagers.init();

        bus.addListener(this::setup);
        bus.addListener(FLComponents::register);

        FLConfig.init();
        FLEvents.init(bus);
        FLForgeEvents.init();
        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            FLClientEvents.init(bus);
            FLClientForgeEvents.init();
        }

        if (THE_ONE_PROBE)
            TheOneProbeIntegration.init(bus);

    }

    public void setup(FMLCommonSetupEvent event)
    {
        // Vanilla registries are not thread safe
        event.enqueueWork(() -> {
            FLInteractionManager.init();
            FLBlocks.registerFlowerPotFlowers();
        });
        FLPatchouliIntegration.registerMultiBlocks();
    }

}
