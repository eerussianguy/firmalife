package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class FLStateProperties
{
    public static final BooleanProperty CURED = BooleanProperty.create("cured");
    public static final IntegerProperty LOGS = IntegerProperty.create("logs", 0, 4);
}
