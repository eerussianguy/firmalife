package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FLStateProperties
{
    public static final IntegerProperty LOGS = IntegerProperty.create("logs", 0, 4);
    public static final BooleanProperty WATERED = BooleanProperty.create("watered");
}
