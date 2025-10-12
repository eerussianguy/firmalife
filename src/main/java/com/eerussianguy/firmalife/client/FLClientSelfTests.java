package com.eerussianguy.firmalife.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.eerussianguy.firmalife.common.FLCreativeTabs;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.CompostTumblerBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.greenhouse.PlanterType;
import com.eerussianguy.firmalife.common.capabilities.bee.GeneticDisease;
import com.eerussianguy.firmalife.common.capabilities.bee.ParasiticInfection;
import com.eerussianguy.firmalife.common.capabilities.wine.WineType;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FoodAge;
import com.google.common.base.Stopwatch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.FirmaLife;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.slf4j.Logger;

import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.IcePileBlock;
import net.dries007.tfc.common.blocks.MoltenBlock;
import net.dries007.tfc.common.blocks.PouredGlassBlock;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCLightBlock;
import net.dries007.tfc.common.blocks.rock.RockAnvilBlock;

import static net.dries007.tfc.util.SelfTests.*;

public final class FLClientSelfTests
{
    private static final Logger LOGGER = FirmaLife.LOGGER;

    public static void runClientSelfTests()
    {
        if (FLHelpers.ASSERTIONS_ENABLED)
        {
//            TFCConfig.COMMON.enableDatapackTests.set(true);
            final Stopwatch tick = Stopwatch.createStarted();
            throwIfAny(
                validateModels(),
                validateTranslationsAndCreativeTabs()
            );
            NeoForge.EVENT_BUS.post(new ClientSelfTestEvent()); // For other mods, as this is invoked via a tricky mixin
            LOGGER.info("Client self tests passed in {}", tick.stop());
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean validateModels()
    {
        final BlockModelShaper shaper = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
        final BakedModel missingModel = shaper.getModelManager().getMissingModel();
        final TextureAtlasSprite missingParticle = missingModel.getParticleIcon();

        final List<BlockState> missingModelErrors = FLBlocks.BLOCK.getEntries().stream()
            .flatMap(states(s -> s.getRenderShape() == RenderShape.MODEL && shaper.getBlockModel(s) == missingModel))
            .toList();
        final List<BlockState> missingParticleErrors = FLBlocks.BLOCK.getEntries().stream()
            .flatMap(states(s -> !s.isAir() && shaper.getParticleIcon(s) == missingParticle))
            .toList();

        return logErrors("{} block states with missing models:", missingModelErrors, FirmaLife.LOGGER)
            | logErrors("{} block states with missing particles:", missingParticleErrors, FirmaLife.LOGGER);
    }

    /**
     * Detects any missing translation keys, for all items, in all creative tabs.
     */
    @SuppressWarnings("ConstantConditions")
    private static boolean validateTranslationsAndCreativeTabs()
    {
        final Set<String> missingTranslations = Bootstrap.getMissingTranslations();
        final List<ItemStack> stacks = new ArrayList<>();
        final Set<Item> items = new HashSet<>();

        boolean error = false;

        FLCreativeTabs.fillFirmalifeTab(null, (stack, vis) -> {
            stacks.add(stack);
            items.add(stack.getItem());
        });

        final Set<Class<? extends Block>> blocksWithNoCreativeTabItem = Set.of(SnowPileBlock.class, IcePileBlock.class, BloomBlock.class, MoltenBlock.class, TFCLightBlock.class, RockAnvilBlock.class, PouredGlassBlock.class);
        final List<? extends Item> missingItems = FLItems.ITEM.getEntries().stream().map(DeferredHolder::get)
            .filter(item -> !items.contains(item)
                && !(item instanceof BlockItem bi && blocksWithNoCreativeTabItem.contains(bi.getBlock().getClass()))
            )
            .toList();

        error |= logErrors("{} items were not found in any TFC creative tab", missingItems, LOGGER);

        for (ItemStack stack : stacks)
        {
            error |= validateTranslation(LOGGER, missingTranslations, stack.getHoverName());
        }

        final SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        BuiltInRegistries.SOUND_EVENT.keySet().forEach(sound -> Optional.ofNullable(soundManager.getSoundEvent(sound)).map(WeighedSoundEvents::getSubtitle).ifPresent(subtitle -> validateTranslation(LOGGER, missingTranslations, subtitle)));

        for (CreativeModeTab tab : CreativeModeTabs.allTabs())
        {
            error |= validateTranslation(LOGGER, missingTranslations, tab.getDisplayName());
        }

        error |= Stream.of(FoodAge.class, PlanterType.class, CompostTumblerBlockEntity.AdditionType.class, WineType.class, GeneticDisease.class, ParasiticInfection.class)
            .flatMap(claz -> Stream.of(claz.getEnumConstants()))
            .anyMatch(constant -> validateTranslation(LOGGER, missingTranslations, FLHelpers.translateEnum(constant)));

        return error | logErrors("{} missing translation keys:", missingTranslations, LOGGER);
    }
}
