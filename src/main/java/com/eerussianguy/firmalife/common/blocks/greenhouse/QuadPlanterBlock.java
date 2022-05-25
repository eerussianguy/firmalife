package com.eerussianguy.firmalife.common.blocks.greenhouse;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;

public class QuadPlanterBlock extends DeviceBlock
{
    public QuadPlanterBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }
}
