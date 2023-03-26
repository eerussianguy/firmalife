package com.eerussianguy.firmalife.common.blockentities;

import net.dries007.tfc.common.capabilities.heat.IHeatBlock;

public interface CrucibleLikeHeatBlock extends IHeatBlock
{
    void setTargetTemperature(float temp);

    float getTargetTemperature();

    void resetStability();

    @Override
    default void setTemperature(float temperature)
    {
        setTargetTemperature(temperature);
        resetStability();
    }

    @Override
    default void setTemperatureIfWarmer(float temperature)
    {
        // Override to still cause an update to the stability ticks
        if (temperature >= getTemperature())
        {
            setTemperature(temperature);
        }
    }
}
