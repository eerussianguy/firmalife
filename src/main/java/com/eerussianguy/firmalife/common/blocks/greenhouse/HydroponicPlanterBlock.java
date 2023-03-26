package com.eerussianguy.firmalife.common.blocks.greenhouse;

import net.dries007.tfc.common.blocks.ExtendedProperties;

public class HydroponicPlanterBlock extends QuadPlanterBlock
{
    public HydroponicPlanterBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public PlanterType getPlanterType()
    {
        return PlanterType.HYDROPONIC;
    }
}
