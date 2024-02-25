package com.eerussianguy.firmalife.common.blockentities;

import java.util.HashSet;
import java.util.Set;

import com.eerussianguy.firmalife.common.util.GreenhouseType;
import com.eerussianguy.firmalife.common.util.Mechanics;
import net.minecraft.core.BlockPos;
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

    public ClimateStationBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.CLIMATE_STATION.get(), pos, state);
        positions = new HashSet<>();
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        type = ClimateType.byId(nbt.getInt("climateType"));
        long[] array = nbt.getLongArray("positions");
        positions.clear();
        positions = new HashSet<>(array.length);
        for (long pos : array)
        {
            positions.add(BlockPos.of(pos));
        }
        if (nbt.contains("favoriteType"))
        {
            favoriteGreenhouseType = new ResourceLocation(nbt.getString("favoriteType"));
        }
        favoriteIsCellar = nbt.getBoolean("favoriteIsCellar");
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
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
        favoriteGreenhouseType = type.id;
        favoriteIsCellar = false;
    }

    public void setFavoriteIsCellar()
    {
        favoriteGreenhouseType = null;
        favoriteIsCellar = true;
    }

    @Nullable
    public GreenhouseType getFavoriteType()
    {
        if (favoriteGreenhouseType != null)
        {
            return GreenhouseType.get(favoriteGreenhouseType);
        }
        return null;
    }

    public boolean favoriteIsCellar()
    {
        return favoriteIsCellar;
    }
}
