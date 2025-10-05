package com.eerussianguy.firmalife.common.capabilities.bee;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import net.dries007.tfc.world.Codecs;

public record BeeComponent(
    Map<BeeAbility, Integer> abilities,
    boolean hasQueen,
    GeneticDisease geneticDisease,
    ParasiticInfection parasiticInfection
)
{
    public static final Codec<BeeComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codecs.mapListCodec(Codecs.recordPairCodec(BeeAbility.CODEC, "ability", Codec.INT, "value")).fieldOf("abilities").forGetter(c -> c.abilities),
        Codec.BOOL.fieldOf("has_queen").forGetter(c -> c.hasQueen),
        GeneticDisease.CODEC.fieldOf("genetic_disease").forGetter(c -> c.geneticDisease),
        ParasiticInfection.CODEC.fieldOf("parasitic_infection").forGetter(c -> c.parasiticInfection)
    ).apply(i, BeeComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BeeComponent> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.map(HashMap::new, BeeAbility.STREAM_CODEC, ByteBufCodecs.VAR_INT), c -> c.abilities,
        ByteBufCodecs.BOOL, c -> c.hasQueen,
        GeneticDisease.STREAM_CODEC, c -> c.geneticDisease,
        ParasiticInfection.STREAM_CODEC, c -> c.parasiticInfection,
        BeeComponent::new
    );

    public static final BeeComponent DEFAULT = new BeeComponent(Map.of(), false, GeneticDisease.NONE, ParasiticInfection.NONE);

    public static BeeComponent initFreshAbilities(RandomSource random)
    {
        final int[] values = BeeAbility.fresh();

        final Map<BeeAbility, Integer> map = new HashMap<>();
        map.put(BeeAbility.random(random), random.nextInt(3) + 1);
        map.put(BeeAbility.random(random), random.nextInt(3) + 1);

        if (random.nextFloat() < 0.1f)
        {
            map.put(BeeAbility.random(random), random.nextInt(3) + 1);
        }

        return new BeeComponent(map, true, GeneticDisease.NONE, ParasiticInfection.NONE);
    }

    public static BeeComponent withDiseases(BeeComponent component, @Nullable GeneticDisease disease, @Nullable ParasiticInfection infection)
    {
        return new BeeComponent(Map.copyOf(component.abilities), component.hasQueen, disease != null ? disease : component.geneticDisease, infection != null ? infection : component.parasiticInfection);
    }

    public static BeeComponent setAbilitiesFromParents(BeeComponent parent1, BeeComponent parent2, RandomSource random)
    {
        final Map<BeeAbility, Integer> babyMap = new HashMap<>();
        int mutation = (parent1.getAbility(BeeAbility.MUTANT) + parent2.getAbility(BeeAbility.MUTANT)) / 2;
        mutation = Mth.clamp(mutation, 1, 5);

        int abilitiesSet = 0;
        List<BeeAbility> abilities = Arrays.asList(BeeAbility.VALUES);
        Collections.shuffle(abilities);
        for (BeeAbility ability : abilities)
        {
            int average = (parent1.getAbility(ability) + parent2.getAbility(ability)) / 2;
            if (average >= 1 && abilitiesSet < 4)
            {
                abilitiesSet++;
                final int newValue = Mth.clamp(Mth.nextInt(random, average - mutation, average + mutation), 0, 10);
                if (newValue > 0)
                {
                    babyMap.put(ability, newValue);
                }
            }
        }
        GeneticDisease disease;
        ParasiticInfection infection;
        if (parent1.geneticDisease != GeneticDisease.NONE)
        {
            disease = parent1.geneticDisease;
        }
        else
        {
            disease = parent2.geneticDisease;
        }
        if (parent1.parasiticInfection != ParasiticInfection.NONE)
        {
            infection = parent1.parasiticInfection;
        }
        else
        {
            infection = parent2.parasiticInfection;
        }
        if (mutation >= 4 && random.nextInt(5) == 0)
        {
            disease = GeneticDisease.VALUES[Mth.nextInt(random, 0, GeneticDisease.VALUES.length)];
        }
        return new BeeComponent(babyMap, true, disease, infection);
    }

    public int getAbility(BeeAbility ability)
    {
        return abilities.getOrDefault(ability, 0);
    }

    public boolean hasGeneticDisease()
    {
        return geneticDisease != GeneticDisease.NONE;
    }

    public boolean hasParasiticInfection()
    {
        return parasiticInfection != ParasiticInfection.NONE;
    }

    public void addTooltipInfo(Consumer<Component> tooltip)
    {
        if (hasQueen())
        {
            tooltip.accept(Component.translatable("firmalife.bee.queen").withStyle(ChatFormatting.GOLD));
            if (hasGeneticDisease())
            {
                tooltip.accept(Component.translatable("firmalife.bee.genetic_disease", Component.translatable("firmalife.bee.disease" + geneticDisease)).withStyle(ChatFormatting.RED));
            }
            if (hasParasiticInfection())
            {
                tooltip.accept(Component.translatable("firmalife.bee.parasitic_infection", Component.translatable("firmalife.bee.infection" + geneticDisease)).withStyle(ChatFormatting.RED));
            }
            tooltip.accept(Component.translatable("firmalife.bee.abilities").withStyle(ChatFormatting.WHITE));
            for (BeeAbility ability : BeeAbility.VALUES)
            {
                final int amount = getAbility(ability);
                if (amount > 0)
                {
                    tooltip.accept(Component.translatable("firmalife.bee.ability." + ability.getSerializedName(), String.valueOf(amount)).withStyle(ChatFormatting.GRAY));
                }
            }
        }
        else
        {
            tooltip.accept(Component.translatable("firmalife.bee.no_queen").withStyle(ChatFormatting.RED));
        }
    }
}
