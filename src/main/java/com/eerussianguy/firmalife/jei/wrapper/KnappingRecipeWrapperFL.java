/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package com.eerussianguy.firmalife.jei.wrapper;

import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.dries007.tfc.compat.jei.wrappers.KnappingRecipeWrapper;
import net.minecraft.util.ResourceLocation;

import com.eerussianguy.firmalife.init.KnappingFL;
import mezz.jei.api.IGuiHelper;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.compat.jei.wrappers.KnappingRecipeWrapper;

import static com.google.common.collect.Maps.newHashMap;

@ParametersAreNonnullByDefault
public class KnappingRecipeWrapperFL extends KnappingRecipeWrapper
{
    public static final ResourceLocation PUMPKIN_TEXTURE = new ResourceLocation("minecraft", "textures/blocks/pumpkin_side.png");

    public static final Map<KnappingType, ResourceLocation> HIGHMAP = Maps.newHashMap();

    public static final Map<KnappingType, ResourceLocation> LOWMAP = Maps.newHashMap();

    static
    {
        HIGHMAP.put(KnappingFL.PUMPKIN, PUMPKIN_TEXTURE);
        LOWMAP.put(KnappingFL.PUMPKIN, null);
    }

    public KnappingRecipeWrapperFL(KnappingRecipe recipe, IGuiHelper guiHelper)
    {
        super(recipe, guiHelper, HIGHMAP.get(recipe.getType()), LOWMAP.get(recipe.getType()));
    }
}
