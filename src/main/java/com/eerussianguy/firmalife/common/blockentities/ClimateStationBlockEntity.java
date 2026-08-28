package com.eerussianguy.firmalife.common.blockentities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.eerussianguy.firmalife.common.util.GreenhouseType;
import com.eerussianguy.firmalife.common.util.Mechanics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntity;

public class ClimateStationBlockEntity extends TFCBlockEntity
{
    private Set<BlockPos> positions;
    private ClimateType type = ClimateType.GREENHOUSE;
    @Nullable private ResourceLocation favoriteGreenhouseType = null;
    private boolean favoriteIsCellar = false;
    private int size = 0;
    @Nullable private ResourceLocation structureType = null;

    public ClimateStationBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.CLIMATE_STATION.get(), pos, state);
        positions = new HashSet<>();
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        super.loadAdditional(nbt, access);
        type = ClimateType.byId(nbt.getInt("climateType"));
        long[] array = nbt.getLongArray("positions");
        positions.clear();
        positions = new HashSet<>(array.length);
        for (long pos : array)
        {
            positions.add(BlockPos.of(pos));
        }
        favoriteGreenhouseType = nbt.contains("favoriteType") ? ResourceLocation.tryParse(nbt.getString("favoriteType")) : null;
        favoriteIsCellar = nbt.getBoolean("favoriteIsCellar");
        size = nbt.getInt("size");
        structureType = nbt.contains("structureType") ? ResourceLocation.tryParse(nbt.getString("structureType")) : null;
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        super.saveAdditional(nbt, access);
        nbt.putInt("climateType", type.ordinal());
        final long[] array = new long[positions.size()];
        int i = 0;
        for (BlockPos pos : positions)
        {
            array[i] = pos.asLong();
            i++;
        }
        nbt.putLongArray("positions", array);
        if (favoriteGreenhouseType != null)
            nbt.putString("favoriteType", favoriteGreenhouseType.toString());
        nbt.putBoolean("favoriteIsCellar", favoriteIsCellar);
        nbt.putInt("size", size);
        if (structureType != null)
            nbt.putString("structureType", structureType.toString());
    }

    public void updateValidity(boolean valid, int tier)
    {
        assert level != null;
        positions.forEach(pos -> {
            final ClimateReceiver receiver = ClimateReceiver.get(level, pos);
            if (receiver != null)
            {
                receiver.setValid(level, pos, valid, tier, type);
            }
        });
    }

    public void setType(ClimateType cellar)
    {
        type = cellar;
    }

    public void setStructureInfo(@Nullable GreenhouseType structureType, int size)
    {
        final ResourceLocation id = structureType == null ? null : GreenhouseType.MANAGER.getId(structureType);
        if (!Objects.equals(this.structureType, id) || this.size != size)
        {
            this.structureType = id;
            this.size = size;
            markForSync();
        }
    }

    public int getSize()
    {
        return size;
    }

    @Nullable
    public GreenhouseType getStructureType()
    {
        return structureType == null ? null : GreenhouseType.MANAGER.get(structureType);
    }

    public void setPositions(Set<BlockPos> positions)
    {
        this.positions = positions;
    }

    public boolean setFavorite(ItemStack held)
    {
        if (held.getItem() instanceof BlockItem bi)
        {
            final BlockState state = bi.getBlock().defaultBlockState();
            if (Mechanics.CELLAR.test(state))
            {
                setFavoriteIsCellar();
                return true;
            }
            final var type = GreenhouseType.get(state);
            if (type != null)
            {
                setFavorite(type);
                return true;
            }
        }
        return false;
    }

    public void setFavorite(GreenhouseType type)
    {
        final ResourceLocation id = GreenhouseType.MANAGER.getId(type);
        if (id != null)
        {
            favoriteGreenhouseType = id;
            favoriteIsCellar = false;
        }
    }

    public void setFavoriteIsCellar()
    {
        favoriteGreenhouseType = null;
        favoriteIsCellar = true;
    }

    @Nullable
    public GreenhouseType getFavoriteType()
    {
        return favoriteGreenhouseType == null ? null : GreenhouseType.MANAGER.get(favoriteGreenhouseType);
    }

    public boolean favoriteIsCellar()
    {
        return favoriteIsCellar;
    }
}
