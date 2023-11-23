from mcresources import ResourceManager, utils
from constants import *

def generate(rm: ResourceManager):
    ### TAGS ###
    rm.item_tag('usable_on_oven', 'firmalife:peel')
    rm.item_tag('tfc:sweetener', 'firmalife:raw_honey')
    rm.item_tag('tfc:foods/flour', *['tfc:food/%s_flour' % g for g in TFC_GRAINS])
    rm.item_tag('tfc:foods/dough', *['firmalife:food/%s_dough' % g for g in TFC_GRAINS])
    rm.item_tag('firmalife:foods/extra_dough', *['firmalife:food/%s_dough' % g for g in TFC_GRAINS])
    rm.item_tag('feeds_yeast', '#tfc:foods/flour')
    rm.item_tag('foods/slices', *['firmalife:food/%s_slice' % g for g in TFC_GRAINS])
    rm.item_tag('foods/flatbreads', *['firmalife:food/%s_flatbread' % g for g in TFC_GRAINS])
    rm.item_tag('foods/pizza_ingredients', '#tfc:foods/vegetables', '#tfc:foods/fruits', '#tfc:foods/cooked_meats')
    rm.item_tag('tfc:sandwich_bread', '#firmalife:foods/slices', '#firmalife:foods/flatbreads')
    rm.item_tag('foods/cheeses', 'firmalife:food/gouda', 'firmalife:food/chevre', 'firmalife:food/shosha', 'firmalife:food/feta', 'firmalife:food/rajya_metok', 'firmalife:food/cheddar')
    rm.item_tag('smoking_fuel', '#minecraft:logs')
    rm.item_tag('oven_fuel', '#minecraft:logs', 'tfc:stick_bundle')
    rm.item_tag('chocolate_blends', 'firmalife:food/milk_chocolate_blend', 'firmalife:food/dark_chocolate_blend', 'firmalife:food/white_chocolate_blend')
    rm.item_tag('foods/chocolate', 'firmalife:food/milk_chocolate', 'firmalife:food/dark_chocolate', 'firmalife:food/white_chocolate')
    rm.item_tag('tfc:foods/can_be_salted', 'firmalife:food/butter')
    rm.item_tag('tfc:usable_on_tool_rack', 'firmalife:spoon', 'firmalife:peel')
    rm.item_tag('pumpkin_knapping', 'tfc:pumpkin')
    rm.item_tag('tfc:any_knapping', '#firmalife:pumpkin_knapping')
    rm.item_tag('foods/heatable', 'firmalife:food/raw_pizza', 'firmalife:food/filled_pie', 'firmalife:food/raw_pumpkin_pie', 'firmalife:food/corn_tortilla', 'firmalife:food/masa', '#firmalife:foods/slices', 'firmalife:food/cocoa_beans', 'firmalife:food/bacon')
    rm.item_tag('foods/dynamic', 'firmalife:food/raw_pizza', 'firmalife:food/filled_pie', 'firmalife:food/cooked_pizza', 'firmalife:food/cooked_pie', 'firmalife:food/burrito', 'firmalife:food/taco', 'firmalife:food/stinky_soup')
    rm.item_tag('foods/washable', 'firmalife:food/filled_pie', 'firmalife:food/cooked_pie', 'firmalife:food/raw_pumpkin_pie', 'minecraft:pumpkin_pie', 'firmalife:food/stinky_soup')
    rm.item_tag('pie_pans', 'firmalife:pie_pan')
    rm.item_tag('can_be_hung', '#tfc:foods/meats', 'tfc:food/garlic')
    rm.item_tag('tfc:compost_greens_low', 'firmalife:fruit_leaf')
    rm.item_tag('tfc:compost_greens', 'firmalife:food/nightshade_berry')
    rm.item_tag('foods/cooked_meats_and_substitutes', '#tfc:foods/cooked_meats', 'firmalife:food/tofu')
    rm.item_tag('forge:leather', 'firmalife:pineapple_leather')
    rm.item_tag('usable_in_stovetop_soup', '#tfc:foods/usable_in_soup')
    rm.item_tag('beekeeper_armor', *['firmalife:beekeeper_%s' % p for p in ARMOR_SECTIONS])

    block_and_item_tag(rm, 'tfc:wild_fruits', 'firmalife:plant/pineapple_bush', 'firmalife:plant/nightshade_bush', 'firmalife:plant/fig_sapling', 'firmalife:plant/cocoa_sapling')

    rm.block_tag('oven_insulation', 'minecraft:bricks', '#tfc:forge_insulation', '#firmalife:oven_blocks', 'minecraft:brick_stairs', 'minecraft:brick_slab', 'firmalife:sealed_bricks')
    rm.block_tag('minecraft:mineable/pickaxe', '#firmalife:oven_blocks')
    rm.block_tag('planters', *['firmalife:%s_planter' % p for p in PLANTERS])
    rm.block_tag('bee_restoration_plants', *['tfc:plant/%s' % p for p in TFC_FLOWERS])
    rm.block_tag('bee_restoration_water_plants', *['tfc:plant/%s' % p for p in TFC_FLOATING_FLOWERS])
    rm.block_tag('bee_plants', '#firmalife:bee_restoration_plants', '#firmalife:bee_restoration_water_plants', '#firmalife:planters')
    block_and_item_tag(rm, 'all_iron_greenhouse', '#firmalife:iron_greenhouse', '#firmalife:rusted_iron_greenhouse')
    block_and_item_tag(rm, 'all_copper_greenhouse', *['#firmalife:%s_greenhouse' % g for g in ('exposed_copper', 'weathered_copper', 'copper', 'oxidized_copper')])
    block_and_item_tag(rm, 'all_treated_wood_greenhouse', '#firmalife:treated_wood_greenhouse', '#firmalife:weathered_treated_wood_greenhouse')
    block_and_item_tag(rm, 'greenhouse', '#firmalife:all_iron_greenhouse', '#firmalife:all_copper_greenhouse', '#firmalife:all_treated_wood_greenhouse', '#firmalife:stainless_steel_greenhouse')
    block_and_item_tag(rm, 'cellar_insulation', 'firmalife:sealed_bricks', 'firmalife:sealed_door', 'firmalife:sealed_trapdoor', 'firmalife:sealed_wall')
    rm.block_tag('always_valid_greenhouse_wall', '#minecraft:doors', '#minecraft:trapdoors')
    rm.block_tag('drops_fruit_leaf', '#tfc:fruit_tree_leaves')
    rm.block_tag('buzzing_leaves', 'firmalife:plant/fig_leaves')
    rm.block_tag('pipe_replaceable', '#tfc:dirt', '#tfc:grass', '#minecraft:base_stone_overworld', '#forge:gravel', '#minecraft:sand', '#tfc:can_carve')
    rm.block_tag('tfc:thorny_bushes', 'firmalife:plant/pineapple_bush')
    rm.block_tag('minecraft:climbable', 'firmalife:dark_ladder')
    rm.block_tag('drops_ice_shavings', 'minecraft:ice')

    rm.block_tag('minecraft:mineable/axe', *['firmalife:plant/%s_branch' % t for t in FRUITS], *['firmalife:plant/%s_growing_branch' % t for t in FRUITS])
    rm.block_tag('tfc:mineable_with_sharp_tool', *['firmalife:plant/%s_leaves' % t for t in FRUITS], *['firmalife:plant/%s_sapling' % t for t in FRUITS], *['firmalife:plant/%s_bush' % b for b in STILL_BUSHES.keys()])
    rm.block_tag('tfc:replaceable_plants', *['firmalife:plant/%s' % p for p in HERBS], 'firmalife:plant/butterfly_grass')
    rm.block_tag('tfc:mineable_with_glass_saw', 'firmalife:reinforced_poured_glass')

    rm.entity_tag('drops_rennet', 'tfc:goat', 'tfc:yak')
    rm.entity_tag('drops_three_rennet', 'tfc:cow', 'tfc:sheep', 'tfc:musk_ox')

    rm.fluid_tag('tfc:alcohols', 'firmalife:pina_colada', 'firmalife:mead')
    rm.fluid_tag('tfc:milks', 'firmalife:yak_milk', 'firmalife:goat_milk', 'firmalife:coconut_milk')
    rm.fluid_tag('tfc:drinkables', 'firmalife:chocolate')
    rm.fluid_tag('tfc:ingredients', *['firmalife:%s' % fluid for fluid in EXTRA_FLUIDS])
    rm.fluid_tag('usable_in_mixing_bowl', '#tfc:usable_in_pot')
    rm.fluid_tag('usable_in_hollow_shell', '#tfc:usable_in_wooden_bucket')
    rm.fluid_tag('usable_in_vat', '#tfc:usable_in_pot', 'firmalife:fruity_fluid')

    # Ore tags
    ore = 'chromite'
    rm.block_tag('forge:ores', '#forge:ores/%s' % ore)
    rm.block_tag('forge:ores/%s' % ore, '#firmalife:ores/%s/poor' % ore, '#firmalife:ores/%s/normal' % ore, '#firmalife:ores/%s/rich' % ore)
    rm.item_tag('tfc:ore_pieces', 'firmalife:ore/poor_%s' % ore, 'firmalife:ore/normal_%s' % ore, 'firmalife:ore/rich_%s' % ore)
    rm.item_tag('tfc:small_ore_pieces', 'firmalife:ore/small_%s' % ore)
    for rock in TFC_ROCKS.keys():
        rm.block_tag('ores/%s/poor' % ore, 'firmalife:ore/poor_%s/%s' % (ore, rock))
        rm.block_tag('ores/%s/normal' % ore, 'firmalife:ore/normal_%s/%s' % (ore, rock))
        rm.block_tag('ores/%s/rich' % ore, 'firmalife:ore/rich_%s/%s' % (ore, rock))


def block_and_item_tag(rm: ResourceManager, name_parts: utils.ResourceIdentifier, *values: utils.ResourceIdentifier, replace: bool = False):
    rm.block_tag(name_parts, *values, replace=replace)
    rm.item_tag(name_parts, *values, replace=replace)