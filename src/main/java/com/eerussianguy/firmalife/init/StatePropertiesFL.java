package com.eerussianguy.firmalife.init;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.util.EnumFacing;

public class StatePropertiesFL
{
    public static final PropertyBool CURED = PropertyBool.create("cured");
    public static final PropertyBool GROWN = PropertyBool.create("grown");
    public static final PropertyBool CAN_GROW = PropertyBool.create("can_grow");
    public static final PropertyBool WET = PropertyBool.create("wet");
    public static final PropertyBool WATERED = PropertyBool.create("watered");
    public static final PropertyBool NEEDS_SOURCE = PropertyBool.create("needs_source");
    public static final PropertyBool CONNECTED = PropertyBool.create("connected");
    public static final PropertyBool TOP = PropertyBool.create("top");
    public static final PropertyBool GLASS = PropertyBool.create("glass");
    public static final PropertyBool STASIS = PropertyBool.create("stasis");
    public static final PropertyEnum<EnumFacing.Axis> XZ = PropertyEnum.create("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 2);
    public static final PropertyInteger JARS = PropertyInteger.create("jars", 1, 4);
    public static final PropertyInteger WEDGES = PropertyInteger.create("wedges", 0, 3);
    public static final PropertyEnum<AgingFL> AGE = PropertyEnum.create("age", AgingFL.class);
    public static final PropertyInteger CLAY = PropertyInteger.create("clay", 0, 4);
}
