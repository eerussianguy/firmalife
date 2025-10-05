package com.eerussianguy.firmalife.common.blockentities;

import net.dries007.tfc.common.component.heat.IHeatConsumer;

public interface CrucibleLikeHeatBlock extends IHeatConsumer
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

    default void setTemperatureIfWarmer(float temperature)
    {
        // Override to still cause an update to the stability ticks
        if (temperature >= getTemperature())
        {
            setTemperature(temperature);
        }
    }
}
