package com.eerussianguy.firmalife.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.eerussianguy.firmalife.common.FLCreativeTabs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import com.eerussianguy.firmalife.FirmaLife;
import org.slf4j.Logger;

import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.IcePileBlock;
import net.dries007.tfc.common.blocks.MoltenBlock;
import net.dries007.tfc.common.blocks.PouredGlassBlock;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCLightBlock;
import net.dries007.tfc.common.blocks.rock.RockAnvilBlock;
import net.dries007.tfc.common.blocks.rock.RockDisplayCategory;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.ForgingBonus;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.calendar.Day;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.climate.KoppenClimateClassification;
import net.dries007.tfc.world.chunkdata.ForestType;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;
import static net.dries007.tfc.util.SelfTests.*;

public final class FLClientSelfTests
{
    private static final Logger LOGGER = FirmaLife.LOGGER;

    @SuppressWarnings("deprecation")
    public static boolean validateModels()
    {
        final BlockModelShaper shaper = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
        final BakedModel missingModel = shaper.getModelManager().getMissingModel();
        final TextureAtlasSprite missingParticle = missingModel.getParticleIcon();

        final List<BlockState> missingModelErrors = stream(ForgeRegistries.BLOCKS, MOD_ID)
            .flatMap(states(s -> s.getRenderShape() == RenderShape.MODEL && shaper.getBlockModel(s) == missingModel))
            .toList();
        final List<BlockState> missingParticleErrors = stream(ForgeRegistries.BLOCKS, MOD_ID)
            .flatMap(states(s -> !s.isAir() && shaper.getParticleIcon(s) == missingParticle))
            .toList();

        validateTranslationsAndCreativeTabs();

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
        final List<Item> missingItems = stream(ForgeRegistries.ITEMS, FirmaLife.MOD_ID)
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
        ForgeRegistries.SOUND_EVENTS.getKeys().forEach(sound -> Optional.ofNullable(soundManager.getSoundEvent(sound)).map(WeighedSoundEvents::getSubtitle).ifPresent(subtitle -> validateTranslation(LOGGER, missingTranslations, subtitle)));

        for (CreativeModeTab tab : CreativeModeTabs.allTabs())
        {
            error |= validateTranslation(LOGGER, missingTranslations, tab.getDisplayName());
        }

        error |= Stream.of(ForgeStep.class, ForgingBonus.class, Metal.Tier.class, Heat.class, Nutrient.class, Size.class, Weight.class, Day.class, Month.class, KoppenClimateClassification.class, ForestType.class, RockDisplayCategory.class, RockDisplayCategory.class)
            .anyMatch(clazz -> validateTranslations(LOGGER, missingTranslations, clazz));

        return error | logErrors("{} missing translation keys:", missingTranslations, LOGGER);
    }
}
