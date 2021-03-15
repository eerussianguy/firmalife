package com.eerussianguy.firmalife.items;

import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

import com.eerussianguy.firmalife.init.FoodDataFL;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.FoodHandler;
import net.dries007.tfc.api.capability.food.Nutrient;

@ParametersAreNonnullByDefault
public class ItemTrailMix extends ItemFoodFL
{
    public ItemTrailMix()
    {
        super(FoodDataFL.TRAIL_MIX);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new TrailMixHandler(nbt, data);
    }

    public static class TrailMixHandler extends FoodHandler
    {
        private final FoodData rootData;

        public TrailMixHandler(@Nullable NBTTagCompound nbt, FoodData data)
        {
            super(nbt, data);

            this.rootData = data;
        }

        public void initCreationFoods(List<FoodData> ingredients)
        {
            // Nutrition and saturation of sandwich is (average of breads) + 0.8f (sum of ingredients), +1 bonus saturation
            float[] nutrition = new float[Nutrient.TOTAL];
            float water = 0f;
            float saturation = 1f;
            for (FoodData ingredient : ingredients)
            {
                for (int i = 0; i < nutrition.length; i++)
                {
                    nutrition[i] += 0.8f * ingredient.getNutrients()[i];
                }
                saturation += 0.8f * ingredient.getSaturation();
                water += 0.8f * ingredient.getWater();
            }
            this.data = new FoodData(4, water, saturation, nutrition, rootData.getDecayModifier());
        }

        @Override
        protected boolean isDynamic()
        {
            return true;
        }
    }
}
