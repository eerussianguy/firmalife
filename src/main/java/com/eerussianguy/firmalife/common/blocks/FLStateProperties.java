package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import com.eerussianguy.firmalife.common.util.FoodAge;

public class FLStateProperties
{
    public static final IntegerProperty LOGS = IntegerProperty.create("logs", 0, 4);
    public static final BooleanProperty WATERED = BooleanProperty.create("watered");
    public static final BooleanProperty STASIS = BooleanProperty.create("stasis");
    public static final BooleanProperty AGING = BooleanProperty.create("aging");
    public static final BooleanProperty HONEY = BooleanProperty.create("honey");
    public static final BooleanProperty BEES = BooleanProperty.create("bees");
    public static final EnumProperty<FoodAge> AGE = EnumProperty.create("age", FoodAge.class);
}
