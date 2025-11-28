package com.eerussianguy.firmalife.providers;

import java.util.concurrent.CompletableFuture;
import com.eerussianguy.firmalife.Accessors;
import com.eerussianguy.firmalife.FirmaLife;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.common.entities.TFCEntities;

import static com.eerussianguy.firmalife.common.FLTags.Entities.*;

public class BuiltinEntityTypeTags extends TagsProvider<EntityType<?>> implements Accessors
{
    public BuiltinEntityTypeTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(event.getGenerator().getPackOutput(), Registries.ENTITY_TYPE, lookup, FirmaLife.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(DROPS_RENNET).add(TFCEntities.GOAT.key(), TFCEntities.YAK.key());
        tag(DROPS_MORE_RENNET).add(TFCEntities.COW.key(), TFCEntities.SHEEP.key(), TFCEntities.MUSK_OX.key());
    }
}
