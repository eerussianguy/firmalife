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
    RUSTED_IRON(SoundType.METAL),
    IRON(SoundType.METAL, RUSTED_IRON),
    OXIDIZED_COPPER(SoundType.COPPER),
    WEATHERED_COPPER(SoundType.COPPER, OXIDIZED_COPPER),
    EXPOSED_COPPER(SoundType.COPPER, WEATHERED_COPPER),
    COPPER(SoundType.COPPER, EXPOSED_COPPER),
    WEATHERED_TREATED_WOOD(SoundType.WOOD),
    TREATED_WOOD(SoundType.WOOD, WEATHERED_TREATED_WOOD),
    STAINLESS_STEEL(SoundType.LANTERN);

    @Nullable
    private final Greenhouse next;
    private final SoundType sound;

    Greenhouse(SoundType sound)
    {
        this.next = null;
        this.sound = sound;
    }

    Greenhouse(SoundType sound, Greenhouse next)
    {
        this.next = next;
        this.sound = sound;
    }

    public enum BlockType
    {
        WALL((green, type) -> new GreenhouseWallBlock(properties(green), type.getNext(green))),
        ROOF((green, type) -> new GreenhouseStairBlock(properties(green), () -> FLBlocks.GREENHOUSE_BLOCKS.get(green).get(WALL).get().defaultBlockState(), type.getNext(green))),
        ROOF_TOP((green, type) -> new GreenhouseSlabBlock(properties(green), type.getNext(green))),
        DOOR((green, type) -> new GreenhouseDoorBlock(properties(green), type.getNext(green)));

        public static ExtendedProperties properties(Greenhouse green)
        {
            return ExtendedProperties.of(BlockBehaviour.Properties.of(Material.METAL).sound(green.sound).strength(4.0f).noOcclusion().randomTicks());
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
