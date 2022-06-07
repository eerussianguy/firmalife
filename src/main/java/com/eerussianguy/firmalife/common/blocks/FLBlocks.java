package com.eerussianguy.firmalife.common.blocks;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.eerussianguy.firmalife.common.blockentities.*;
import com.eerussianguy.firmalife.common.blocks.greenhouse.*;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.items.JarsBlockItem;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCMaterials;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

import static net.dries007.tfc.common.TFCItemGroup.*;

@SuppressWarnings("unused")
public class FLBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, com.eerussianguy.firmalife.Firmalife.MOD_ID);

    public static final RegistryObject<Block> CURED_OVEN_BOTTOM = register("cured_oven_bottom", () -> new OvenBottomBlock(ExtendedProperties.of(Properties.of(Material.STONE).strength(4.0f).randomTicks().lightLevel(FLBlocks::lightEmission).sound(SoundType.STONE).noOcclusion()).blockEntity(FLBlockEntities.OVEN_BOTTOM).serverTicks(OvenBottomBlockEntity::serverTick), null), DECORATIONS);
    public static final RegistryObject<Block> CURED_OVEN_TOP = register("cured_oven_top", () -> new OvenTopBlock(ExtendedProperties.of(Properties.of(Material.STONE).strength(4.0f).randomTicks().sound(SoundType.STONE).noOcclusion()).blockEntity(FLBlockEntities.OVEN_TOP).serverTicks(OvenTopBlockEntity::serverTick), null), DECORATIONS);
    public static final RegistryObject<Block> CURED_OVEN_CHIMNEY = register("cured_oven_chimney", () -> new OvenChimneyBlock(Properties.of(Material.STONE).strength(4.0f).sound(SoundType.STONE).noOcclusion(), null), DECORATIONS);

    public static final RegistryObject<Block> OVEN_BOTTOM = register("oven_bottom", () -> new OvenBottomBlock(ExtendedProperties.of(Properties.of(Material.STONE).strength(4.0f).randomTicks().lightLevel(FLBlocks::lightEmission).sound(SoundType.STONE).noOcclusion()).blockEntity(FLBlockEntities.OVEN_BOTTOM).serverTicks(OvenBottomBlockEntity::serverTick), FLBlocks.CURED_OVEN_BOTTOM), DECORATIONS);
    public static final RegistryObject<Block> OVEN_TOP = register("oven_top", () -> new OvenTopBlock(ExtendedProperties.of(Properties.of(Material.STONE).strength(4.0f).randomTicks().sound(SoundType.STONE).noOcclusion()).blockEntity(FLBlockEntities.OVEN_TOP).serverTicks(OvenTopBlockEntity::serverTick), FLBlocks.CURED_OVEN_TOP), DECORATIONS);
    public static final RegistryObject<Block> OVEN_CHIMNEY = register("oven_chimney", () -> new OvenChimneyBlock(Properties.of(Material.STONE).strength(4.0f).sound(SoundType.STONE).noOcclusion(), FLBlocks.CURED_OVEN_CHIMNEY), DECORATIONS);

    public static final RegistryObject<Block> DRYING_MAT = register("drying_mat", () -> new DryingMatBlock(ExtendedProperties.of(Properties.of(Material.DECORATION).strength(3.0f).sound(SoundType.AZALEA_LEAVES)).flammable(60, 30).blockEntity(FLBlockEntities.DRYING_MAT).serverTicks(DryingMatBlockEntity::serverTick)), DECORATIONS);
    public static final RegistryObject<Block> BEEHIVE = register("beehive", () -> new FLBeehiveBlock(ExtendedProperties.of(Properties.of(Material.WOOD).strength(0.6f).sound(SoundType.WOOD)).flammable(60, 30).blockEntity(FLBlockEntities.BEEHIVE).serverTicks(FLBeehiveBlockEntity::serverTick)));

    public static final RegistryObject<Block> HONEY_JAR = register("honey_jar", () -> new JarsBlock(ExtendedProperties.of(Properties.of(Material.DECORATION).noCollission().noOcclusion().instabreak().sound(SoundType.GLASS).randomTicks())), b -> new JarsBlockItem(b, new Item.Properties().tab(MISC)));
    public static final RegistryObject<Block> COMPOST_JAR = register("compost_jar", () -> new JarsBlock(ExtendedProperties.of(Properties.of(Material.DECORATION).noCollission().noOcclusion().instabreak().sound(SoundType.GLASS).randomTicks())), b -> new JarsBlockItem(b, new Item.Properties().tab(MISC)));
    public static final RegistryObject<Block> ROTTEN_COMPOST_JAR = register("rotten_compost_jar", () -> new JarsBlock(ExtendedProperties.of(Properties.of(Material.DECORATION).noCollission().noOcclusion().instabreak().sound(SoundType.GLASS).randomTicks())), b -> new JarsBlockItem(b, new Item.Properties().tab(MISC)));
    public static final RegistryObject<Block> GUANO_JAR = register("guano_jar", () -> new JarsBlock(ExtendedProperties.of(Properties.of(Material.DECORATION).noCollission().noOcclusion().instabreak().sound(SoundType.GLASS).randomTicks())), b -> new JarsBlockItem(b, new Item.Properties().tab(MISC)));

    public static final RegistryObject<Block> CLIMATE_STATION = register("climate_station", () -> new ClimateStationBlock(ExtendedProperties.of(Properties.of(Material.WOOD).strength(3.0f).sound(SoundType.WOOD).randomTicks()).blockEntity(FLBlockEntities.CLIMATE_STATION).flammable(60, 30)), DECORATIONS);
    public static final RegistryObject<Block> LARGE_PLANTER = register("large_planter", () -> new LargePlanterBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.CROP).strength(1f)).blockEntity(FLBlockEntities.LARGE_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)), DECORATIONS);
    public static final RegistryObject<Block> QUAD_PLANTER = register("quad_planter", () -> new QuadPlanterBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.CROP).strength(1f)).blockEntity(FLBlockEntities.QUAD_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)), DECORATIONS);
    public static final RegistryObject<Block> BONSAI_PLANTER = register("bonsai_planter", () -> new BonsaiPlanterBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.CROP).strength(1f)).blockEntity(FLBlockEntities.BONSAI_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)), DECORATIONS);
    public static final RegistryObject<Block> HANGING_PLANTER = register("hanging_planter", () -> new HangingPlanterBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.CROP).strength(1f)).blockEntity(FLBlockEntities.HANGING_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)), DECORATIONS);

    public static final Map<Greenhouse, Map<Greenhouse.BlockType, RegistryObject<Block>>> GREENHOUSE_BLOCKS = Helpers.mapOfKeys(Greenhouse.class, greenhouse ->
        Helpers.mapOfKeys(Greenhouse.BlockType.class, type ->
            register(greenhouse.name() + "_greenhouse_" + type.name(), type.create(greenhouse), type.createBlockItem(new Item.Properties().tab(DECORATIONS)))
        )
    );

    public static final Map<FLMetal, RegistryObject<LiquidBlock>> METAL_FLUIDS = Helpers.mapOfKeys(FLMetal.class, metal ->
        register("fluid/metal/" + metal.name(), () -> new LiquidBlock(FLFluids.METALS.get(metal).getSecond(), Properties.of(TFCMaterials.MOLTEN_METAL).noCollission().strength(100f).noDrops()))
    );

    public static int lightEmission(BlockState state)
    {
        return state.getValue(BlockStateProperties.LIT) ? 15 : 0;
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier)
    {
        return register(name, blockSupplier, (Function<T, ? extends BlockItem>) null);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, CreativeModeTab group)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, new Item.Properties().tab(group)));
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, Item.Properties blockItemProperties)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, blockItemProperties));
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, @Nullable Function<T, ? extends BlockItem> blockItemFactory)
    {
        final String actualName = name.toLowerCase(Locale.ROOT);
        final RegistryObject<T> block = BLOCKS.register(actualName, blockSupplier);
        if (blockItemFactory != null)
        {
            FLItems.ITEMS.register(actualName, () -> blockItemFactory.apply(block.get()));
        }
        return block;
    }
}
