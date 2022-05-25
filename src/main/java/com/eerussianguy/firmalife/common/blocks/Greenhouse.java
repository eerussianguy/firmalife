package com.eerussianguy.firmalife.common.blocks;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import org.jetbrains.annotations.Nullable;

public enum Greenhouse
{
    RUSTED_IRON(SoundType.METAL, false),
    IRON(SoundType.METAL, false, RUSTED_IRON),
    OXIDIZED_COPPER(SoundType.COPPER, false),
    WEATHERED_COPPER(SoundType.COPPER, false, OXIDIZED_COPPER),
    EXPOSED_COPPER(SoundType.COPPER, false, WEATHERED_COPPER),
    COPPER(SoundType.COPPER, false, EXPOSED_COPPER),
    WEATHERED_TREATED_WOOD(SoundType.WOOD, true),
    TREATED_WOOD(SoundType.WOOD, true, WEATHERED_TREATED_WOOD),
    STAINLESS_STEEL(SoundType.LANTERN, false);

    @Nullable
    private final Greenhouse next;
    private final SoundType sound;
    private final boolean flammable;

    Greenhouse(SoundType sound, boolean flammable)
    {
        this.next = null;
        this.sound = sound;
        this.flammable = flammable;
    }

    Greenhouse(SoundType sound, boolean flammable, Greenhouse next)
    {
        this.next = next;
        this.sound = sound;
        this.flammable = flammable;
    }

    public enum BlockType
    {
        GLASS((green, type) -> new GreenhouseGlassBlock(properties(green), type.getNext(green))),
        GLASS_STAIRS((green, type) -> new GreenhouseStairBlock(properties(green), () -> FLBlocks.GREENHOUSE_BLOCKS.get(green).get(GLASS).get().defaultBlockState(), type.getNext(green))),
        GLASS_SLAB((green, type) -> new GreenhouseSlabBlock(properties(green), type.getNext(green))),
        GLASS_DOOR((green, type) -> new GreenhouseDoorBlock(properties(green), type.getNext(green)));

        public static ExtendedProperties properties(Greenhouse green)
        {
            ExtendedProperties prop = ExtendedProperties.of(BlockBehaviour.Properties.of(Material.METAL).sound(green.sound).strength(4.0f).noOcclusion().randomTicks());
            if (green.flammable) prop = prop.flammable(60, 30);
            return prop;
        }

        private final BiFunction<Greenhouse, Greenhouse.BlockType, ? extends Block> factory;
        private final BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory;

        BlockType(BiFunction<Greenhouse, Greenhouse.BlockType, ? extends Block> factory)
        {
            this(factory, BlockItem::new);
        }

        BlockType(BiFunction<Greenhouse, Greenhouse.BlockType, ? extends Block> factory, BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory)
        {
            this.factory = factory;
            this.blockItemFactory = blockItemFactory;
        }

        public Supplier<Block> create(Greenhouse type)
        {
            return () -> factory.apply(type, this);
        }

        public Function<Block, BlockItem> createBlockItem(Item.Properties properties)
        {
            return block -> blockItemFactory.apply(block, properties);
        }

        @Nullable
        public Supplier<? extends Block> getNext(Greenhouse green)
        {
            return green.next == null ? null : FLBlocks.GREENHOUSE_BLOCKS.get(green.next).get(this);
        }
    }

}
