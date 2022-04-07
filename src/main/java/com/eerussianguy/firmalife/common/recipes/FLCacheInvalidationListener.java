package com.eerussianguy.firmalife.common.recipes;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.server.ServerLifecycleHooks;

import net.dries007.tfc.util.SyncReloadListener;

public enum FLCacheInvalidationListener implements SyncReloadListener
{
    INSTANCE;

    @Override
    public void reloadSync()
    {
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null)
        {
            invalidateServerCaches(server);
        }
    }

    public void invalidateServerCaches(MinecraftServer server)
    {
        final RecipeManager manager = server.getRecipeManager();

        DryingRecipe.CACHE.reload(manager.getAllRecipesFor(FLRecipeTypes.DRYING.get()));
    }
}
