package com.eerussianguy.firmalife.common.blocks;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.eerussianguy.firmalife.common.blockentities.*;
import com.eerussianguy.firmalife.common.blocks.greenhouse.*;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.items.JarsBlockItem;
import com.eerussianguy.firmalife.common.util.Carving;
import com.eerussianguy.firmalife.common.util.ExtraFluid;
import com.eerussianguy.firmalife.common.util.FLMetal;
import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.GroundcoverBlock;
import net.dries007.tfc.common.blocks.TFCMaterials;
import net.dries007.tfc.common.blocks.devices.JackOLanternBlock;
import net.dries007.tfc.common.blocks.devices.TFCComposterBlock;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

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
    public static final RegistryObject<Block> BEEHIVE = register("beehive", () -> new FLBeehiveBlock(ExtendedProperties.of(Properties.of(Material.WOOD).strength(0.6f).sound(SoundType.WOOD)).flammable(60, 30).blockEntity(FLBlockEntities.BEEHIVE).serverTicks(FLBeehiveBlockEntity::serverTick)), DECORATIONS);
    public static final RegistryObject<Block> IRON_COMPOSTER = register("iron_composter", () -> new TFCComposterBlock(ExtendedProperties.of(Properties.of(Material.WOOD).strength(0.6F).noOcclusion().sound(SoundType.METAL).randomTicks()).flammable(60, 90).blockEntity(FLBlockEntities.IRON_COMPOSTER)), DECORATIONS);
    public static final RegistryObject<Block> WOOL_STRING = register("wool_string", () -> new StringBlock(ExtendedProperties.of(Properties.of(Material.CLOTH_DECORATION).noCollission().strength(2.0f).sound(SoundType.WOOL).randomTicks()).blockEntity(FLBlockEntities.STRING).serverTicks(StringBlockEntity::serverTick), TFCItems.WOOL_YARN));
    public static final RegistryObject<Block> MIXING_BOWL = register("mixing_bowl", () -> new MixingBowlBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.STONE).strength(1f).noOcclusion()).blockEntity(FLBlockEntities.MIXING_BOWL).ticks(MixingBowlBlockEntity::serverTick, MixingBowlBlockEntity::clientTick)), DECORATIONS);

    public static final RegistryObject<Block> HONEY_JAR = register("honey_jar", () -> new JarsBlock(jarProperties()), b -> new JarsBlockItem(b, new Item.Properties().tab(MISC)));
    public static final RegistryObject<Block> COMPOST_JAR = register("compost_jar", () -> new JarsBlock(jarProperties()), b -> new JarsBlockItem(b, new Item.Properties().tab(MISC)));
    public static final RegistryObject<Block> ROTTEN_COMPOST_JAR = register("rotten_compost_jar", () -> new JarsBlock(jarProperties()), b -> new JarsBlockItem(b, new Item.Properties().tab(MISC)));
    public static final RegistryObject<Block> GUANO_JAR = register("guano_jar", () -> new JarsBlock(jarProperties()), b -> new JarsBlockItem(b, new Item.Properties().tab(MISC)));
    public static final Map<Food, RegistryObject<Block>> FRUIT_PRESERVES = Helpers.mapOfKeys(Food.class, FLItems.TFC_FRUITS::contains, food -> register(food.name().toLowerCase(Locale.ROOT) + "_jar", () -> new JarsBlock(jarProperties()), b -> new JarsBlockItem(b, new Item.Properties().tab(MISC))));

    public static final RegistryObject<Block> CHEDDAR_WHEEL = register("cheddar_wheel", () -> new CheeseWheelBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.WART_BLOCK).strength(2f).randomTicks()).blockEntity(FLBlockEntities.TICK_COUNTER), FLItems.FOODS.get(FLFood.CHEDDAR)), DECORATIONS);
    public static final RegistryObject<Block> CHEVRE_WHEEL = register("chevre_wheel", () -> new CheeseWheelBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.WART_BLOCK).strength(2f).randomTicks()).blockEntity(FLBlockEntities.TICK_COUNTER), FLItems.FOODS.get(FLFood.CHEVRE)), DECORATIONS);
    public static final RegistryObject<Block> RAJYA_METOK_WHEEL = register("rajya_metok_wheel", () -> new CheeseWheelBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.WART_BLOCK).strength(2f).randomTicks()).blockEntity(FLBlockEntities.TICK_COUNTER), FLItems.FOODS.get(FLFood.RAJYA_METOK)), DECORATIONS);
    public static final RegistryObject<Block> GOUDA_WHEEL = register("gouda_wheel", () -> new CheeseWheelBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.WART_BLOCK).strength(2f).randomTicks()).blockEntity(FLBlockEntities.TICK_COUNTER), FLItems.FOODS.get(FLFood.GOUDA)), DECORATIONS);
    public static final RegistryObject<Block> FETA_WHEEL = register("feta_wheel", () -> new CheeseWheelBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.WART_BLOCK).strength(2f).randomTicks()).blockEntity(FLBlockEntities.TICK_COUNTER), FLItems.FOODS.get(FLFood.FETA)), DECORATIONS);
    public static final RegistryObject<Block> SHOSHA_WHEEL = register("shosha_wheel", () -> new CheeseWheelBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.WART_BLOCK).strength(2f).randomTicks()).blockEntity(FLBlockEntities.TICK_COUNTER), FLItems.FOODS.get(FLFood.SHOSHA)), DECORATIONS);

    public static final RegistryObject<Block> CLIMATE_STATION = register("climate_station", () -> new ClimateStationBlock(ExtendedProperties.of(Properties.of(Material.WOOD).strength(3.0f).sound(SoundType.WOOD).randomTicks()).blockEntity(FLBlockEntities.CLIMATE_STATION).flammable(60, 30)), DECORATIONS);
    public static final RegistryObject<Block> LARGE_PLANTER = register("large_planter", () -> new LargePlanterBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.CROP).strength(1f)).blockEntity(FLBlockEntities.LARGE_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)), DECORATIONS);
    public static final RegistryObject<Block> QUAD_PLANTER = register("quad_planter", () -> new QuadPlanterBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.CROP).strength(1f)).blockEntity(FLBlockEntities.QUAD_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)), DECORATIONS);
    public static final RegistryObject<Block> BONSAI_PLANTER = register("bonsai_planter", () -> new BonsaiPlanterBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.CROP).strength(1f)).blockEntity(FLBlockEntities.BONSAI_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)), DECORATIONS);
    public static final RegistryObject<Block> HANGING_PLANTER = register("hanging_planter", () -> new HangingPlanterBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.CROP).strength(1f)).blockEntity(FLBlockEntities.HANGING_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)), DECORATIONS);
    public static final RegistryObject<Block> TRELLIS_PLANTER = register("trellis_planter", () -> new TrellisPlanterBlock(ExtendedProperties.of(Properties.of(Material.DIRT).sound(SoundType.CROP).strength(1f)).blockEntity(FLBlockEntities.TRELLIS_PLANTER).serverTicks(LargePlanterBlockEntity::serverTick)), DECORATIONS);

    public static final RegistryObject<Block> SEALED_BRICKS = register("sealed_bricks", () -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()), DECORATIONS);
    public static final RegistryObject<Block> SEALED_DOOR = register("sealed_door", () -> new DoorBlock(BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.STONE).strength(2.0f, 10).requiresCorrectToolForDrops()), DECORATIONS);

    public static final Map<Carving, RegistryObject<Block>> CARVED_PUMPKINS = Helpers.mapOfKeys(Carving.class, carve ->
        register("carved_pumpkin/" + carve.getSerializedName(), () -> new CarvedPumpkinBlock(Properties.of(Material.VEGETABLE, MaterialColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).isValidSpawn(FLBlocks::always)))
    );

    public static final Map<Carving, RegistryObject<Block>> JACK_O_LANTERNS = Helpers.mapOfKeys(Carving.class, carve ->
        register("lit_pumpkin/" + carve.getSerializedName(), () -> new JackOLanternBlock(ExtendedProperties.of(Properties.of(Material.VEGETABLE, MaterialColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).randomTicks().isValidSpawn(FLBlocks::always)).blockEntity(FLBlockEntities.TICK_COUNTER), CARVED_PUMPKINS.get(carve)))
    );

    public static final RegistryObject<Block> SMALL_CHROMITE = register("ore/small_chromite", () -> GroundcoverBlock.looseOre(Properties.of(Material.GRASS).strength(0.05F, 0.0F).sound(SoundType.NETHER_ORE).noCollission()), ORES);
    public static final Map<Rock, Map<Ore.Grade, RegistryObject<Block>>> CHROMITE_ORES = Helpers.mapOfKeys(Rock.class, rock ->
        Helpers.mapOfKeys(Ore.Grade.class, grade ->
            register(("ore/" + grade.name() + "_chromite" + "/" + rock.name()), () -> new Block(Properties.of(Material.STONE).sound(SoundType.STONE).strength(3, 10).requiresCorrectToolForDrops()), TFCItemGroup.ORES)
        )
    );

    public static final Map<Greenhouse, Map<Greenhouse.BlockType, RegistryObject<Block>>> GREENHOUSE_BLOCKS = Helpers.mapOfKeys(Greenhouse.class, greenhouse ->
        Helpers.mapOfKeys(Greenhouse.BlockType.class, type ->
            register(greenhouse.name() + "_greenhouse_" + type.name(), type.create(greenhouse), type.createBlockItem(new Item.Properties().tab(DECORATIONS)))
        )
    );

    public static final Map<FLMetal, RegistryObject<LiquidBlock>> METAL_FLUIDS = Helpers.mapOfKeys(FLMetal.class, metal ->
        register("fluid/metal/" + metal.name(), () -> new LiquidBlock(FLFluids.METALS.get(metal).source(), Properties.of(TFCMaterials.MOLTEN_METAL).noCollission().strength(100f).noDrops()))
    );

    public static final Map<ExtraFluid, RegistryObject<LiquidBlock>> EXTRA_FLUIDS = Helpers.mapOfKeys(ExtraFluid.class, fluid ->
        register("fluid/" + fluid.getSerializedName(), () -> new LiquidBlock(FLFluids.EXTRA_FLUIDS.get(fluid).source(), Properties.of(Material.WATER).noCollission().strength(100f).noDrops()))
    );

    public static ExtendedProperties jarProperties()
    {
        return ExtendedProperties.of(Properties.of(Material.DECORATION).noCollission().noOcclusion().instabreak().sound(SoundType.GLASS).randomTicks());
    }

    private static boolean always(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> type)
    {
        return true;
    }

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
