from enum import Enum, auto

from mcresources import ResourceManager, loot_tables
from mcresources.type_definitions import Json

from constants import *
from mcresources import utils

from recipes import fluid_ingredient

class Size(Enum):
    tiny = auto()
    very_small = auto()
    small = auto()
    normal = auto()
    large = auto()
    very_large = auto()
    huge = auto()


class Weight(Enum):
    very_light = auto()
    light = auto()
    medium = auto()
    heavy = auto()
    very_heavy = auto()

class Category(Enum):
    fruit = auto()
    vegetable = auto()
    grain = auto()
    bread = auto()
    dairy = auto()
    meat = auto()
    cooked_meat = auto()
    other = auto()

def generate(rm: ResourceManager):
    ### TAGS ###
    rm.item_tag('usable_on_oven', 'firmalife:peel')
    rm.item_tag('sweetener', 'minecraft:sugar', 'firmalife:raw_honey')
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
    rm.item_tag('foods/heatable', 'firmalife:food/raw_pizza', 'firmalife:food/filled_pie', 'firmalife:food/raw_pumpkin_pie', 'firmalife:food/corn_tortilla', 'firmalife:food/masa', '#firmalife:foods/slices', 'firmalife:food/cocoa_beans', 'firmalife:food/bacon')
    rm.item_tag('foods/dynamic', 'firmalife:food/raw_pizza', 'firmalife:food/filled_pie', 'firmalife:food/cooked_pizza', 'firmalife:food/cooked_pie', 'firmalife:food/burrito', 'firmalife:food/taco', 'firmalife:food/stinky_soup')
    rm.item_tag('foods/washable', 'firmalife:food/filled_pie', 'firmalife:food/cooked_pie', 'firmalife:food/raw_pumpkin_pie', 'firmalife:food/cooked_pumpkin_pie', 'firmalife:food/stinky_soup')
    rm.item_tag('pie_pans', 'firmalife:pie_pan')
    rm.item_tag('contains_pie_pan', 'firmalife:food/cooked_pumpkin_pie')
    rm.item_tag('can_be_hung', '#tfc:foods/meats', 'tfc:food/garlic')
    rm.item_tag('tfc:compost_greens_low', 'firmalife:fruit_leaf')
    rm.item_tag('foods/cooked_meats_and_substitutes', '#tfc:foods/cooked_meats', 'firmalife:food/tofu')
    rm.item_tag('forge:leather', 'firmalife:pineapple_leather')
    rm.item_tag('usable_in_stovetop_soup', '#tfc:foods/usable_in_soup')
    rm.item_tag('beekeeper_armor', *['firmalife:beekeeper_%s' % p for p in ARMOR_SECTIONS])

    block_and_item_tag(rm, 'tfc:wild_fruits', 'firmalife:plant/pineapple_bush', 'firmalife:plant/nightshade_bush', 'firmalife:plant/fig_sapling', 'firmalife:plant/cocoa_sapling')

    rm.block_tag('oven_insulation', 'minecraft:bricks', '#tfc:forge_insulation', '#firmalife:oven_blocks', 'minecraft:brick_stairs', 'minecraft:brick_slab')
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
    rm.block_tag('drops_fruit_leaf', '#tfc:fruit_tree_leaves')
    rm.block_tag('buzzing_leaves', 'firmalife:plant/fig_leaves')
    rm.block_tag('pipe_replaceable', '#tfc:dirt', '#tfc:grass', '#minecraft:base_stone_overworld', '#forge:gravel', '#minecraft:sand', '#tfc:can_carve')
    rm.block_tag('tfc:thorny_bushes', 'firmalife:plant/pineapple_bush')
    rm.block_tag('minecraft:climbable', 'firmalife:dark_ladder')
    rm.block_tag('drops_ice_shavings', 'minecraft:ice')

    rm.block_tag('minecraft:mineable/axe', *['firmalife:plant/%s_branch' % t for t in FRUITS], *['firmalife:plant/%s_growing_branch' % t for t in FRUITS])
    rm.block_tag('tfc:mineable_with_sharp_tool', *['firmalife:plant/%s_leaves' % t for t in FRUITS], *['firmalife:plant/%s_sapling' % t for t in FRUITS])

    rm.entity_tag('drops_rennet', 'tfc:goat', 'tfc:yak')
    rm.entity_tag('drops_three_rennet', 'tfc:cow', 'tfc:sheep', 'tfc:musk_ox')

    rm.fluid_tag('tfc:alcohols', 'firmalife:pina_colada')
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

    ### JSON DATA ###
    greenhouse(rm, 'treated_wood', '#firmalife:all_treated_wood_greenhouse', 5)
    greenhouse(rm, 'copper', '#firmalife:all_copper_greenhouse', 10)  # allows grain
    greenhouse(rm, 'iron', '#firmalife:all_iron_greenhouse', 15)  # allows fruit trees
    greenhouse(rm, 'stainless_steel', '#firmalife:stainless_steel_greenhouse', 20)

    for grain in ('barley', 'oat', 'rye', 'wheat'):
        simple_plantable(rm, grain, 'nitrogen' if grain == 'barley' else 'phosphorous', 7, planter='large', tier=10)
    simple_plantable(rm, 'maize', 'nitrogen' if grain == 'barley' else 'phosphorous', 4, planter='large', tier=10, firmalife=True)

    simple_plantable(rm, 'beet', 'potassium', 5)
    simple_plantable(rm, 'cabbage', 'nitrogen', 5)
    simple_plantable(rm, 'carrot', 'potassium', 4)
    simple_plantable(rm, 'garlic', 'nitrogen', 4)
    simple_plantable(rm, 'potato', 'potassium', 6)
    simple_plantable(rm, 'onion', 'nitrogen', 6)
    simple_plantable(rm, 'soybean', 'nitrogen', 6)

    for herb in HERBS:
        plantable(rm, herb, 'firmalife:plant/%s' % herb, 'firmalife:plant/%s' % herb, 'nitrogen', ['firmalife:block/plant/%s/%s' % (herb, i) for i in range(0, 2)], 1, seed_chance=0.8, )

    plantable(rm, 'rice', 'tfc:seeds/rice', 'tfc:food/rice', 'phosphorous', ['tfc:block/crop/rice_%s' % i for i in range(0, 8)], 7, 'hydroponic', tier=10)
    plantable(rm, 'cranberry', 'tfc:plant/cranberry_bush', 'tfc:food/cranberry', 'phosphorous', ['firmalife:block/crop/cranberry_%s' % i for i in range(0, 4)], 3, 'hydroponic', tier=10)

    simple_plantable(rm, 'green_bean', 'nitrogen', 4, planter='large', firmalife=True)
    simple_plantable(rm, 'tomato', 'potassium', 4, planter='large', firmalife=True)
    simple_plantable(rm, 'sugarcane', 'potassium', 4, planter='large', firmalife=True)
    plantable(rm, 'jute', 'tfc:seeds/jute', 'tfc:jute', 'potassium', ['firmalife:block/crop/jute_%s' % i for i in range(0, 5)], 4, 'large')
    plantable(rm, 'papyrus', 'tfc:seeds/papyrus', 'tfc:papyrus', 'potassium', ['firmalife:block/crop/papyrus_%s' % i for i in range(0, 6)], 5, 'large')

    bonsai_plantable(rm, 'cherry', 'nitrogen')
    bonsai_plantable(rm, 'green_apple', 'nitrogen')
    bonsai_plantable(rm, 'lemon', 'nitrogen')
    bonsai_plantable(rm, 'olive', 'nitrogen')
    bonsai_plantable(rm, 'orange', 'nitrogen')
    bonsai_plantable(rm, 'peach', 'nitrogen')
    bonsai_plantable(rm, 'plum', 'nitrogen')
    bonsai_plantable(rm, 'red_apple', 'nitrogen')
    bonsai_plantable(rm, 'cocoa', 'nitrogen', firmalife=True, food='firmalife:food/cocoa_beans')
    bonsai_plantable(rm, 'fig', 'nitrogen', firmalife=True)

    hanging_plantable(rm, 'squash', 'tfc:seeds/squash', 'tfc:food/squash', 'potassium')
    hanging_plantable(rm, 'pumpkin', 'tfc:seeds/pumpkin', 'tfc:pumpkin', 'phosphorous', tier=15)
    hanging_plantable(rm, 'melon', 'tfc:seeds/melon', 'tfc:melon', 'phosphorous', tier=15)
    hanging_plantable(rm, 'banana', 'tfc:plant/banana_sapling', 'tfc:food/banana', 'nitrogen', tier=15, seed_chance=0.08)

    trellis_plantable(rm, 'blackberry', 'tfc:plant/blackberry_bush', 'tfc:food/blackberry', 'nitrogen')
    trellis_plantable(rm, 'blueberry', 'tfc:plant/blueberry_bush', 'tfc:food/blueberry', 'nitrogen')
    trellis_plantable(rm, 'raspberry', 'tfc:plant/raspberry_bush', 'tfc:food/raspberry', 'nitrogen')
    trellis_plantable(rm, 'elderberry', 'tfc:plant/elderberry_bush', 'tfc:food/elderberry', 'nitrogen')
    trellis_plantable(rm, 'bunchberry', 'tfc:plant/bunchberry_bush', 'tfc:food/bunchberry', 'nitrogen')
    trellis_plantable(rm, 'cloudberry', 'tfc:plant/cloudberry_bush', 'tfc:food/cloudberry', 'nitrogen')
    trellis_plantable(rm, 'gooseberry', 'tfc:plant/gooseberry_bush', 'tfc:food/gooseberry', 'nitrogen')
    trellis_plantable(rm, 'snowberry', 'tfc:plant/snowberry_bush', 'tfc:food/snowberry', 'nitrogen')
    trellis_plantable(rm, 'strawberry', 'tfc:plant/strawberry_bush', 'tfc:food/strawberry', 'nitrogen')
    trellis_plantable(rm, 'wintergreen_berry', 'tfc:plant/wintergreen_berry_bush', 'tfc:food/wintergreen_berry', 'nitrogen')
    trellis_plantable(rm, 'nightshade', 'firmalife:plant/nightshade_bush', 'firmalife:food/nightshade_berry', 'nitrogen', firmalife=True)
    trellis_plantable(rm, 'pineapple', 'firmalife:plant/pineapple_bush', 'firmalife:food/pineapple', 'nitrogen', firmalife=True)
    # missing is cranberries. hydroponic planter?

    # Drinkable
    drinkable(rm, 'chocolate', 'firmalife:chocolate', thirst=10, food={'hunger': 0, 'saturation': 1.0, 'dairy': 1.0})

    # Food: HUNGER, SATURATION, WATER, DECAY
    decayable(rm, 'frothy_coconut', 'firmalife:food/frothy_coconut', Category.vegetable)
    food_item(rm, 'tofu', 'firmalife:food/tofu', Category.vegetable, 4, 2, 2, 0.75, protein=1.5)
    decayable(rm, 'soy_mixture', 'firmalife:food/soy_mixture', Category.vegetable)
    decayable(rm, 'yak_curd', 'firmalife:food/yak_curd', Category.dairy)
    decayable(rm, 'goat_curd', 'firmalife:food/goat_curd', Category.dairy)
    decayable(rm, 'milk_curd', 'firmalife:food/milk_curd', Category.dairy)
    food_item(rm, 'slices', '#firmalife:foods/slices', Category.grain, 4, 0.75, 0, 1.5, grain=1)
    food_item(rm, 'toast', 'firmalife:food/toast', Category.grain, 4, 1.5, 0, 1, grain=1)
    food_item(rm, 'toast_with_jam', 'firmalife:food/toast_with_jam', Category.other, 4, 2, 1, 2, grain=1, fruit=0.75)
    food_item(rm, 'toast_with_butter', 'firmalife:food/toast_with_butter', Category.other, 4, 2, 1, 2, grain=1, dairy=0.25)
    food_item(rm, 'bacon', 'firmalife:food/bacon', Category.meat, 4, 0, 0, 2, protein=0.5)
    food_item(rm, 'cooked_bacon', 'firmalife:food/cooked_bacon', Category.cooked_meat, 4, 2, 0, 2, protein=0.5)
    food_item(rm, 'garlic_bread', 'firmalife:food/garlic_bread', Category.other, 4, 2, 0, 2, grain=1, veg=1, dairy=0.1)
    food_item(rm, 'flatbreads', '#firmalife:foods/flatbreads', Category.grain, 4, 0.75, 0, 1, grain=0.5)
    food_item(rm, 'cheeses', '#firmalife:foods/cheeses', Category.dairy, 4, 2, 0, 0.3, dairy=3)
    food_item(rm, 'shredded_cheese', 'firmalife:food/shredded_cheese', Category.dairy, 4, 2, 0, 0.3, dairy=0.75)
    food_item(rm, 'pickled_egg', 'firmalife:food/pickled_egg', Category.other, 4, 2, 10, 0.3, protein=1.5, dairy=0.25)
    decayable(rm, 'chocolate_blends', '#firmalife:chocolate_blends', Category.dairy)
    food_item(rm, 'chocolate', '#firmalife:foods/chocolate', Category.other, 4, 1, 0, 0.3, dairy=0.5, grain=0.5)
    decayable(rm, 'doughs', '#firmalife:foods/extra_dough', Category.other, decay=2)
    decayable(rm, 'butter', 'firmalife:food/butter', Category.other)
    decayable(rm, 'pie_dough', 'firmalife:food/pie_dough', Category.other)
    decayable(rm, 'pizza_dough', 'firmalife:food/pizza_dough', Category.other)
    food_item(rm, 'pumpkin_chunks', 'firmalife:food/pumpkin_chunks', Category.fruit, 4, 1, 5, 1.5, fruit=0.75)
    decayable(rm, 'pumpkin_pie_dough', 'firmalife:food/pumpkin_pie_dough', Category.other)
    decayable(rm, 'raw_pumpkin_pie', 'firmalife:food/raw_pumpkin_pie', Category.other)
    decayable(rm, 'cooked_pumpkin_pie', 'firmalife:food/cooked_pumpkin_pie', Category.other)
    decayable(rm, 'cocoa_beans', 'firmalife:food/cocoa_beans', Category.other, decay=0.25)
    food_item(rm, 'fig', 'firmalife:food/fig', Category.fruit, 4, 1, 5, 0.8, fruit=0.9)
    decayable(rm, 'roasted_cocoa_beans', 'firmalife:food/roasted_cocoa_beans', Category.other)
    decayable(rm, 'cocoa_powder', 'firmalife:food/cocoa_powder', Category.other, decay=0.25)
    decayable(rm, 'cocoa_butter', 'firmalife:food/cocoa_butter', Category.other, decay=0.25)
    decayable(rm, 'cured_maize', 'firmalife:food/cured_maize', Category.other, decay=1.0)
    decayable(rm, 'tomato_sauce_mix', 'firmalife:food/tomato_sauce_mix', Category.other, decay=2.0)
    decayable(rm, 'nixtamal', 'firmalife:food/nixtamal', Category.other, decay=0.3)
    decayable(rm, 'masa_flour', 'firmalife:food/masa_flour', Category.other, decay=0.8)
    decayable(rm, 'masa', 'firmalife:food/masa', Category.other, decay=2.0)
    food_item(rm, 'corn_tortilla', 'firmalife:food/corn_tortilla', Category.grain, 4, 1, 0, 0.8, grain=0.6)
    food_item(rm, 'taco_shell', 'firmalife:food/taco_shell', Category.grain, 4, 1, 0, 0.8, grain=0.6)
    food_item(rm, 'tomato_sauce', 'firmalife:food/tomato_sauce', Category.vegetable, 1, 1, 1, 1, veg=0.75)
    food_item(rm, 'salsa', 'firmalife:food/salsa', Category.vegetable, 1, 1, 1, 0.8, veg=0.5)
    food_item(rm, 'pineapple', 'firmalife:food/pineapple', Category.fruit, 4, 1, 1, 0.85, fruit=0.75)
    food_item(rm, 'nightshade_berry', 'firmalife:food/nightshade_berry', Category.other, 4, 1, 1, 0.85, fruit=3.0)
    food_item(rm, 'vanilla_ice_cream', 'firmalife:food/vanilla_ice_cream', Category.other, 4, 1, 1, 5, dairy=0.75)
    food_item(rm, 'chocolate_ice_cream', 'firmalife:food/chocolate_ice_cream', Category.other, 4, 1.5, 1, 5, dairy=0.5, grain=0.25)
    food_item(rm, 'strawberry_ice_cream', 'firmalife:food/strawberry_ice_cream', Category.other, 4, 1.5, 1, 5, dairy=0.5, fruit=0.5)
    food_item(rm, 'banana_split', 'firmalife:food/banana_split', Category.other, 4, 2, 1, 5, fruit=3.5, dairy=1.75, grain=0.25)

    item_size(rm, 'jars', '#firmalife:jars', Size.very_large, Weight.medium)
    item_size(rm, 'beehive_frame', 'firmalife:beehive_frame', Size.very_small, Weight.very_heavy)
    item_size(rm, 'cheese_wheels', '#firmalife:cheese_wheels', Size.very_large, Weight.very_heavy)
    item_size(rm, 'dynamic_foods', '#firmalife:foods/dynamic', Size.very_small, Weight.very_heavy)

    item_heat(rm, 'heatable_foods', '#firmalife:foods/heatable', 1)

    for fruit, data in FRUITS.items():
        climate_range(rm, 'plant/%s_tree' % fruit, hydration=(hydration_from_rainfall(data.min_rain), 100, 0), temperature=(data.min_temp - 7, data.max_temp + 7, 0))
    for berry, data in STILL_BUSHES.items():
        climate_range(rm, 'plant/%s_bush' % berry, hydration=(hydration_from_rainfall(data[0]), 100, 0), temperature=(data[2], data[3], 0))

    ### MISC DATA ###
    global_loot_modifiers(rm, 'firmalife:fruit_leaf', 'firmalife:rennet', 'firmalife:rennet_three', 'firmalife:ice_shavings')
    global_loot_modifier(rm, 'rennet', 'firmalife:add_item', {'item': utils.item_stack('2 firmalife:rennet')}, match_entity_tag('firmalife:drops_rennet'))
    global_loot_modifier(rm, 'rennet_three', 'firmalife:add_item', {'item': utils.item_stack('3 firmalife:rennet')}, match_entity_tag('firmalife:drops_three_rennet'))
    global_loot_modifier(rm, 'fruit_leaf', 'firmalife:add_item', {'item': utils.item_stack('firmalife:fruit_leaf'), 'chance': 0.5}, match_block_ingredient('firmalife:drops_fruit_leaf'))
    global_loot_modifier(rm, 'ice_shavings', 'firmalife:add_item', {'item': utils.item_stack('firmalife:ice_shavings')}, match_block_ingredient('firmalife:drops_ice_shavings'))


def greenhouse(rm: ResourceManager, name: str, block_ingredient: str, tier: int):
    rm.data(('firmalife', 'greenhouse', name), {
        'ingredient': utils.ingredient(block_ingredient),
        'tier': tier
    })

def trellis_plantable(rm: ResourceManager, name: str, ingredient: str, crop: str, nutrient: str, firmalife: bool = False):
    tex = 'firmalife:block/berry_bush/%s%s_bush' if firmalife else 'tfc:block/berry_bush/%s%s_bush'
    plantable(rm, name, ingredient, crop, nutrient, [tex % (pref, name) for pref in ('', 'dry_', 'flowering_', 'fruiting_')], 0, 'trellis', 15, 0, False)

def hanging_plantable(rm: ResourceManager, name: str, seed: str, crop: str, nutrient: str, tier: int = None, seed_chance: float = 0.5):
    plantable(rm, name, seed, crop, nutrient, ['firmalife:block/crop/%s_%s' % (name, i) for i in range(0, 5)], 4, 'hanging', tier, seed_chance, specials=['firmalife:block/crop/%s_fruit' % name])

def bonsai_plantable(rm: ResourceManager, name: str, nutrient: str, firmalife: bool = False, food: str = None):
    space = 'firmalife' if firmalife else 'tfc'
    plantable(rm, name, '%s:plant/%s_sapling' % (space, name), '%s:food/%s' % (space, name) if food is None else food, nutrient, ['%s:block/fruit_tree/%s%s' % (space, name, suff) for suff in ('_fruiting_leaves', '_dry_leaves', '_flowering_leaves', '_branch', '_leaves')], 0, 'bonsai', 15, 0.08)

def simple_plantable(rm: ResourceManager, name: str, nutrient: str, stages: int, planter: str = 'quad', tier: int = None, firmalife: bool = False):
    tex = 'tfc:block/crop/%s_%s' if not firmalife else 'firmalife:block/crop/%s_%s'
    plantable(rm, name, 'tfc:seeds/%s' % name, 'tfc:food/%s' % name, nutrient, [tex % (name, i) for i in range(0, stages + 1)], stages, planter, tier)

def plantable(rm: ResourceManager, name: str, seed: str, crop: str, nutrient: str, texture: List[str], stages: int, planter: str = 'quad', tier: int = None, seed_chance: float = 0.5, seeds: bool = True, specials: List[str] = None):
    rm.data(('firmalife', 'plantable', name), {
        'planter': planter,
        'ingredient': utils.ingredient(seed),
        'seed': utils.item_stack(seed) if seeds else None,
        'crop': utils.item_stack(crop),
        'nutrient': nutrient,
        'stages': stages,
        'texture': texture,
        'specials': [] if specials is None else specials,
        'tier': tier,
        'extra_seed_chance': seed_chance
    })


def item_heat(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, heat_capacity: float, melt_temperature: Optional[float] = None, mb: Optional[int] = None):
    if melt_temperature is not None:
        forging_temperature = round(melt_temperature * 0.6)
        welding_temperature = round(melt_temperature * 0.8)
    else:
        forging_temperature = welding_temperature = None
    if mb is not None:
        # Interpret heat capacity as a specific heat capacity - so we need to scale by the mB present. Baseline is 100 mB (an ingot)
        # Higher mB = higher heat capacity = heats and cools slower = consumes proportionally more fuel
        heat_capacity = round(10 * heat_capacity * mb) / 1000
    rm.data(('tfc', 'item_heats', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'heat_capacity': heat_capacity,
        'forging_temperature': forging_temperature,
        'welding_temperature': welding_temperature
    })

def global_loot_modifier(rm: ResourceManager, name: str, mod_type: str, data_in: Json, *conditions: utils.Json):
    rm.write((*rm.resource_dir, 'data', rm.domain, 'loot_modifiers', name), {
        'type': mod_type,
        'conditions': [c for c in conditions],
        **data_in
    })

# note for the mcresources dev: these work exactly the same as tags so if you implement this, do it like that
def global_loot_modifiers(rm: ResourceManager, *modifiers: str):
    rm.write((*rm.resource_dir, 'data', 'forge', 'loot_modifiers', 'global_loot_modifiers'), {
        'replace': False,
        'entries': [m for m in modifiers]
    })

def match_entity_tag(tag: str):
    return {
        'condition': 'minecraft:entity_properties',
        'predicate': {
            'type': '#' + tag
        },
        'entity': 'this'
    }

def match_block_ingredient(tag: str):
    return {
        'condition': 'firmalife:block_ingredient',
        'ingredient': {'tag': tag}
    }

def decayable(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, category: Category, decay: float = 3):
    food_item(rm, name_parts, ingredient, category, 4, 0, 0, decay)

def food_item(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, category: Category, hunger: int, saturation: float, water: int, decay: float, fruit: Optional[float] = None, veg: Optional[float] = None, protein: Optional[float] = None, grain: Optional[float] = None, dairy: Optional[float] = None):
    rm.item_tag('tfc:foods', ingredient)
    rm.data(('tfc', 'food_items', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'category': category.name,
        'hunger': hunger,
        'saturation': saturation,
        'water': water if water != 0 else None,
        'decay_modifier': decay,
        'fruit': fruit,
        'vegetables': veg,
        'protein': protein,
        'grain': grain,
        'dairy': dairy
    })
    if category in (Category.fruit, Category.vegetable):
        rm.item_tag('tfc:foods/%ss' % category.name.lower(), ingredient)
    if category in (Category.meat, Category.cooked_meat):
        rm.item_tag('tfc:foods/meats', ingredient)
        if category == Category.cooked_meat:
            rm.item_tag('tfc:foods/cooked_meats', ingredient)
        else:
            rm.item_tag('tfc:foods/raw_meats', ingredient)
    if category == Category.dairy:
        rm.item_tag('tfc:foods/dairy', ingredient)

def drinkable(rm: ResourceManager, name_parts: utils.ResourceIdentifier, fluid: utils.Json, thirst: Optional[int] = None, intoxication: Optional[int] = None, effects: Optional[utils.Json] = None, food: Optional[utils.Json] = None):
    rm.data(('tfc', 'drinkables', name_parts), {
        'ingredient': fluid_ingredient(fluid),
        'thirst': thirst,
        'intoxication': intoxication,
        'effects': effects,
        'food': food
    })


def item_size(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, size: Size, weight: Weight):
    rm.data(('tfc', 'item_sizes', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'size': size.name,
        'weight': weight.name
    })

def climate_range(rm: ResourceManager, name_parts: utils.ResourceIdentifier, hydration: Tuple[int, int, int] = None, temperature: Tuple[float, float, float] = None):
    data = {}
    if hydration is not None:
        data.update({'min_hydration': hydration[0], 'max_hydration': hydration[1], 'hydration_wiggle_range': hydration[2]})
    if temperature is not None:
        data.update({'min_temperature': temperature[0], 'max_temperature': temperature[1], 'temperature_wiggle_range': temperature[2]})
    rm.data(('tfc', 'climate_ranges', name_parts), data)


def hydration_from_rainfall(rainfall: int) -> int:
    return rainfall * 60 // 500

def block_and_item_tag(rm: ResourceManager, name_parts: utils.ResourceIdentifier, *values: utils.ResourceIdentifier, replace: bool = False):
    rm.block_tag(name_parts, *values, replace=replace)
    rm.item_tag(name_parts, *values, replace=replace)

