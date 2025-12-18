package com.eerussianguy.firmalife.common.capabilities.bee;

import java.util.HashMap;
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

import net.dries007.tfc.util.Helpers;
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
    public static final BeeComponent DEFAULT_QUEEN = new BeeComponent(Map.of(), true, GeneticDisease.NONE, ParasiticInfection.NONE);

    public static BeeComponent initFreshAbilities(RandomSource random)
    {
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

    public BeeComponent mutate(RandomSource random)
    {
        if (!hasQueen)
            return BeeComponent.DEFAULT;
        final Map<BeeAbility, Integer> map = new HashMap<>();
        final int mutant = getAbility(BeeAbility.MUTANT);
        abilities.forEach((ability, strength) -> {
            final int value = strength + Mth.ceil(Helpers.uniform(random, -mutant / 2f, mutant / 2f));
            map.put(ability, value);
        });
        GeneticDisease disease = GeneticDisease.NONE;
        if (mutant > 5 && random.nextFloat() < mutant / 10f - 0.2f)
        {
            disease = GeneticDisease.VALUES[random.nextInt(GeneticDisease.VALUES.length)];
        }
        return new BeeComponent(map, true, disease, ParasiticInfection.NONE);
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
            tooltip.accept(Component.translatable("firmalife.bee.dead").withStyle(ChatFormatting.RED));
        }
    }
}
