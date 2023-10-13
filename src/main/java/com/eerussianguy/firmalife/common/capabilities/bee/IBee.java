package com.eerussianguy.firmalife.common.capabilities.bee;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.util.INBTSerializable;

public interface IBee extends INBTSerializable<CompoundTag>
{
    int MAX_DISEASE = 5;
    int MAX_INFECTION = 5;

    void setHasQueen(boolean exists);

    boolean hasQueen();

    int[] getAbilityMap();

    void setAbilities(int[] abilities);

    void setAbility(BeeAbility ability, int value);

    void setGeneticDisease(int id);

    int getGeneticDisease();

    default boolean hasGeneticDisease()
    {
        return getGeneticDisease() != -1;
    }

    void setParasiticInfection(int id);

    int getParasiticInfection();

    default boolean hasParasiticInfection()
    {
        return getParasiticInfection() != -1;
    }

    default int getAbility(BeeAbility ability)
    {
        return getAbilityMap()[ability.ordinal()];
    }

    /**
     * Init two, perhaps three abilities chosen randomly
     */
    default void initFreshAbilities(RandomSource random)
    {
        final int[] values = BeeAbility.fresh();

        values[random.nextInt(values.length)] = random.nextInt(3) + 1;
        values[random.nextInt(values.length)] = random.nextInt(3) + 1;
        if (random.nextFloat() < 0.1f)
        {
            values[random.nextInt(values.length)] = random.nextInt(3) + 1;
        }
        setAbilities(values);
        setHasQueen(true);
    }

    default void setAbilitiesFromParents(IBee parent1, IBee parent2, RandomSource random)
    {
        int[] parent1Abilities = parent1.getAbilityMap();
        int[] parent2Abilities = parent2.getAbilityMap();
        int mutation = (parent1Abilities[BeeAbility.MUTANT.ordinal()] + parent2Abilities[BeeAbility.MUTANT.ordinal()]) / 2;
        mutation = Mth.clamp(mutation, 1, 5);

        int abilitiesSet = 0;
        List<BeeAbility> abilities = Arrays.asList(BeeAbility.VALUES);
        Collections.shuffle(abilities);
        for (BeeAbility ability : abilities)
        {
            int average = (parent1Abilities[ability.ordinal()] + parent2Abilities[ability.ordinal()]) / 2;
            if (average >= 1 && abilitiesSet < 4)
            {
                abilitiesSet++;
                setAbility(ability, Mth.nextInt(random, average - mutation, average + mutation));
            }
        }
        setHasQueen(true);
        if (parent1.hasGeneticDisease())
        {
            setGeneticDisease(parent1.getGeneticDisease());
        }
        else
        {
            setGeneticDisease(parent2.getGeneticDisease());
        }
        if (mutation >= 4 && random.nextInt(5) == 0)
        {
            setGeneticDisease(Mth.nextInt(random, 0, MAX_DISEASE));
        }
    }

    default void addTooltipInfo(List<Component> tooltip)
    {
        if (hasQueen())
        {
            tooltip.add(Component.translatable("firmalife.bee.queen").withStyle(ChatFormatting.GOLD));
            if (getGeneticDisease() != -1)
            {
                tooltip.add(Component.translatable("firmalife.bee.genetic_disease", Component.translatable("firmalife.bee.disease" + getGeneticDisease())).withStyle(ChatFormatting.RED));
            }
            if (getParasiticInfection() != -1)
            {
                tooltip.add(Component.translatable("firmalife.bee.parasitic_infection", Component.translatable("firmalife.bee.infection" + getGeneticDisease())).withStyle(ChatFormatting.RED));
            }
            tooltip.add(Component.translatable("firmalife.bee.abilities").withStyle(ChatFormatting.WHITE));
            for (BeeAbility ability : BeeAbility.VALUES)
            {
                final int amount = getAbility(ability);
                if (amount > 0)
                {
                    tooltip.add(Component.translatable("firmalife.bee.ability." + ability.getSerializedName(), String.valueOf(amount)).withStyle(ChatFormatting.GRAY));
                }
            }
        }
        else
        {
            tooltip.add(Component.translatable("firmalife.bee.no_queen").withStyle(ChatFormatting.RED));
        }
    }

}
