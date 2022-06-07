package com.eerussianguy.firmalife.common.capabilities.bee;

import java.util.List;
import java.util.Random;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.common.util.INBTSerializable;

public interface IBee extends INBTSerializable<CompoundTag>
{
    void setHasQueen(boolean exists);

    boolean hasQueen();

    int[] getAbilityMap();

    void setAbilities(int[] abilities);

    void setAbility(BeeAbility ability, int value);

    default int getAbility(BeeAbility ability)
    {
        return getAbilityMap()[ability.ordinal()];
    }

    default void initFreshAbilities(Random random)
    {
        int[] values = new int[BeeAbility.SIZE];
        for (int i = 0; i < values.length; i++)
        {
            values[i] = random.nextInt(4) + 1;
        }
        setAbilities(values);
        setHasQueen(true);
    }

    default void setAbilitiesFromParents(IBee parent1, IBee parent2, Random random)
    {
        int[] parent1Abilities = parent1.getAbilityMap();
        int[] parent2Abilities = parent2.getAbilityMap();
        int[] myAbilities = getAbilityMap();
        int mutation = (parent1Abilities[BeeAbility.MUTANT.ordinal()] + parent2Abilities[BeeAbility.MUTANT.ordinal()]) / 2;

        for (BeeAbility ability : BeeAbility.VALUES)
        {
            int average = (parent1Abilities[ability.ordinal()] + parent2Abilities[ability.ordinal()]) / 2;
            setAbility(ability, Mth.nextInt(random, average - mutation, average + mutation));
        }
        setHasQueen(true);
    }

    default void addTooltipInfo(List<Component> tooltip)
    {
        if (hasQueen())
        {
            tooltip.add(new TranslatableComponent("firmalife.bee.queen").withStyle(ChatFormatting.GOLD));
            tooltip.add(new TranslatableComponent("firmalife.bee.abilities").withStyle(ChatFormatting.WHITE));
            for (BeeAbility ability : BeeAbility.VALUES)
            {
                tooltip.add(new TranslatableComponent("firmalife.bee.ability." + ability.getSerializedName(), String.valueOf(getAbility(ability))).withStyle(ChatFormatting.GRAY));
            }
        }
        else
        {
            tooltip.add(new TranslatableComponent("firmalife.bee.no_queen").withStyle(ChatFormatting.RED));
        }
    }

}
