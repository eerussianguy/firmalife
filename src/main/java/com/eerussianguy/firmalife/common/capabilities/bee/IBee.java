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

    /**
     * Init two, perhaps three abilities chosen randomly
     */
    default void initFreshAbilities(Random random)
    {
        final int[] values = BeeAbility.fresh();

        values[random.nextInt(values.length)] = random.nextInt(4) + 1;
        values[random.nextInt(values.length)] = random.nextInt(4) + 1;
        if (random.nextFloat() < 0.1f)
        {
            values[random.nextInt(values.length)] = random.nextInt(4) + 1;
        }
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
        int mutation = (parent1Abilities[BeeAbility.MUTANT.ordinal()] + parent2Abilities[BeeAbility.MUTANT.ordinal()]) / 2;
        mutation = Mth.clamp(mutation, 1, 5);

        for (BeeAbility ability : BeeAbility.VALUES)
        {
            int average = (parent1Abilities[ability.ordinal()] + parent2Abilities[ability.ordinal()]) / 2;
            if (average >= 1)
            {
                setAbility(ability, Mth.nextInt(random, average - mutation, average + mutation));
            }
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
                final int amount = getAbility(ability);
                if (amount > 0)
                {
                    tooltip.add(new TranslatableComponent("firmalife.bee.ability." + ability.getSerializedName(), String.valueOf(amount)).withStyle(ChatFormatting.GRAY));
                }
            }
        }
        else
        {
            tooltip.add(new TranslatableComponent("firmalife.bee.no_queen").withStyle(ChatFormatting.RED));
        }
    }

}
