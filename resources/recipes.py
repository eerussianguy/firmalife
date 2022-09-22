from enum import Enum
from typing import Union

from mcresources import ResourceManager, RecipeContext, utils
from mcresources.type_definitions import Json, ResourceIdentifier

from constants import *


class Rules(Enum):
    hit_any = 'hit_any'
    hit_not_last = 'hit_not_last'
    hit_last = 'hit_last'
    hit_second_last = 'hit_second_last'
    hit_third_last = 'hit_third_last'
    draw_any = 'draw_any'
    draw_last = 'draw_last'
    draw_not_last = 'draw_not_last'
    draw_second_last = 'draw_second_last'
    draw_third_last = 'draw_third_last'
    punch_any = 'punch_any'
    punch_last = 'punch_last'
    punch_not_last = 'punch_not_last'
    punch_second_last = 'punch_second_last'
    punch_third_last = 'punch_third_last'
    bend_any = 'bend_any'
    bend_last = 'bend_last'
    bend_not_last = 'bend_not_last'
    bend_second_last = 'bend_second_last'
    bend_third_last = 'bend_third_last'
    upset_any = 'upset_any'
    upset_last = 'upset_last'
    upset_not_last = 'upset_not_last'
    upset_second_last = 'upset_second_last'
    upset_third_last = 'upset_third_last'
    shrink_any = 'shrink_any'
    shrink_last = 'shrink_last'
    shrink_not_last = 'shrink_not_last'
    shrink_second_last = 'shrink_second_last'
    shrink_third_last = 'shrink_third_last'

def generate(rm: ResourceManager):
    # Crafting
    rm.crafting_shaped('crafting/peel', ['X', 'Y'], {'X': 'minecraft:bowl', 'Y': '#forge:rods/wooden'}, 'firmalife:peel').with_advancement('#forge:rods/wooden')
    damage_shapeless(rm, 'crafting/spoon', ('#forge:rods/wooden', '#tfc:lumber', '#tfc:knives'), 'firmalife:spoon').with_advancement('#forge:rods/wooden')
    rm.crafting_shaped('crafting/mixing_bowl', ['XYX', 'YXY'], {'X': 'firmalife:treated_lumber', 'Y': 'tfc:glue'}, 'firmalife:mixing_bowl').with_advancement('firmalife:treated_lumber')
    rm.crafting_shapeless('crafting/empty_jar', ('minecraft:glass', '#tfc:lumber'), (4, 'firmalife:empty_jar')).with_advancement('minecraft:glass')
    rm.crafting_shaped('crafting/drying_mat', ['XXX'], {'X': 'firmalife:fruit_leaf'}, 'firmalife:drying_mat').with_advancement('firmalife:fruit_leaf')
    rm.crafting_shaped('crafting/solar_drier', ['SGS', ' M ', 'WWW'], {'M': 'firmalife:drying_mat', 'S': '#forge:rods/stainless_steel', 'G': 'minecraft:glass', 'W': 'firmalife:treated_lumber'}, 'firmalife:solar_drier').with_advancement('firmalife:drying_mat')
    damage_shapeless(rm, 'crafting/basil_leaves', ('firmalife:plant/basil', '#tfc:knives'), (2, 'firmalife:spice/basil_leaves')).with_advancement('firmalife:plant/basil')
    rm.crafting_shapeless('crafting/bee_candle', ('firmalife:beeswax', '#forge:string'), '4 tfc:candle').with_advancement('firmalife:beeswax')
    rm.crafting_shaped('crafting/sealed_bricks', ['XXX', 'XYX', 'XXX'], {'X': '#forge:stone_bricks', 'Y': 'firmalife:beeswax'}, '8 firmalife:sealed_bricks').with_advancement('firmalife:beeswax')
    rm.crafting_shaped('crafting/sealed_door', ['YX', 'YX', 'YX'], {'X': 'firmalife:sealed_bricks', 'Y': '#forge:rods/brass'}, 'firmalife:sealed_door').with_advancement('firmalife:sealed_bricks')
    rm.crafting_shaped('crafting/quad_planter', ['YY', 'XX', 'XX'], {'X': 'minecraft:brick', 'Y': 'tfc:compost'}, 'firmalife:quad_planter').with_advancement('tfc:compost')
    rm.crafting_shaped('crafting/large_planter', ['XYX', 'XXX'], {'X': 'minecraft:brick', 'Y': 'tfc:compost'}, 'firmalife:large_planter').with_advancement('tfc:compost')
    rm.crafting_shaped('crafting/hanging_planter', ['XXX', 'XYX'], {'X': 'minecraft:brick', 'Y': 'tfc:compost'}, 'firmalife:hanging_planter').with_advancement('tfc:compost')
    rm.crafting_shaped('crafting/trellis_planter', ['X X', 'X X', 'XYX'], {'X': 'minecraft:brick', 'Y': 'tfc:compost'}, 'firmalife:trellis_planter').with_advancement('tfc:compost')
    rm.crafting_shaped('crafting/bonsai_planter', ['X X', 'XYX', 'XXX'], {'X': 'minecraft:brick', 'Y': 'tfc:compost'}, 'firmalife:bonsai_planter').with_advancement('tfc:compost')
    rm.crafting_shaped('crafting/treated_lumber', ['XXX', 'XYX', 'XXX'], {'X': '#tfc:lumber', 'Y': 'firmalife:beeswax'}, '8 firmalife:treated_lumber').with_advancement('firmalife:beeswax')
    rm.crafting_shaped('crafting/beehive_frame', ['X X', ' X ', 'X X'], {'X': '#tfc:lumber'}, 'firmalife:beehive_frame').with_advancement('#tfc:lumber')
    rm.crafting_shaped('crafting/beehive', ['XYX', 'XZX', 'XYX'], {'X': '#tfc:lumber', 'Y': 'firmalife:beehive_frame', 'Z': 'tfc:thatch'}, 'firmalife:beehive').with_advancement('#tfc:lumber')
    rm.crafting_shaped('crafting/iron_composter', ['XYX'], {'X': '#forge:sheets/wrought_iron', 'Y': 'tfc:composter'}, 'firmalife:iron_composter').with_advancement('tfc:composter')
    rm.crafting_shaped('crafting/rajya_metok_wheel', ['XXX', 'YYY', 'XXX'], {'X': 'tfc:powder/salt', 'Y': 'firmalife:food/yak_curd'}, 'firmalife:rajya_metok_wheel').with_advancement('firmalife:food/yak_curd')
    rm.crafting_shaped('crafting/chevre_wheel', ['XXX', 'YYY', 'XXX'], {'X': 'tfc:powder/salt', 'Y': 'firmalife:food/goat_curd'}, 'firmalife:chevre_wheel').with_advancement('firmalife:food/goat_curd')
    rm.crafting_shaped('crafting/cheddar_wheel', ['XXX', 'YYY', 'XXX'], {'X': 'tfc:powder/salt', 'Y': 'firmalife:food/milk_curd'}, 'firmalife:cheddar_wheel').with_advancement('firmalife:food/milk_curd')
    rm.crafting_shaped('crafting/cheesecloth', ['XX'], {'X': '#tfc:high_quality_cloth'}, '8 firmalife:cheesecloth').with_advancement('#tfc:high_quality_cloth')
    rm.crafting_shaped('crafting/climate_station', ['BXB', 'OYO', 'BXB'], {'Y': 'minecraft:blue_stained_glass', 'X': 'tfc:brass_mechanisms', 'O': '#forge:dusts/redstone', 'B': '#minecraft:planks'}, 'firmalife:climate_station').with_advancement('#tfc:brass_mechanisms')
    rm.crafting_shapeless('crafting/watering_can', (fluid_item_ingredient('1000 minecraft:water'), 'tfc:wooden_bucket', '#tfc:lumber'), 'firmalife:watering_can')
    damage_shapeless(rm, 'crafting/shredded_cheese', ('#tfc:knives', '#firmalife:foods/cheeses'), '4 firmalife:food/shredded_cheese').with_advancement('#firmalife:foods/cheeses')
    rm.crafting_shapeless('crafting/pickled_egg', ('minecraft:clay_ball', 'tfc:powder/wood_ash', 'tfc:powder/salt', 'tfc:food/boiled_egg'), 'firmalife:food/pickled_egg')
    rm.crafting_shaped('crafting/seed_ball', [' X ', 'XYX', ' X '], {'X': '#tfc:seeds', 'Y': 'tfc:compost'}, 'firmalife:seed_ball').with_advancement('tfc:compost')
    rm.crafting_shapeless('crafting/raw_pumpkin_pie', ('firmalife:food/pumpkin_pie_dough', 'firmalife:pie_pan'), 'firmalife:food/raw_pumpkin_pie').with_advancement('firmalife:food/pumpkin_pie_dough')
    rm.domain = 'tfc'
    rm.crafting_shapeless('crafting/pumpkin_pie', ('firmalife:food/cooked_pumpkin_pie',), 'minecraft:pumpkin_pie').with_advancement('firmalife:food/cooked_pumpkin_pie')
    rm.domain = 'firmalife'

    write_crafting_recipe(rm, 'cocoa_butter_powder', {
        'type': 'tfc:extra_products_shapeless_crafting',
        'extra_products': utils.item_stack_list('firmalife:food/cocoa_powder'),
        'recipe': {
            'type': 'tfc:damage_inputs_shapeless_crafting',
            'recipe': {
                'type': 'minecraft:crafting_shapeless',
                'ingredients': utils.item_stack_list((not_rotten('firmalife:food/roasted_cocoa_beans'), '#tfc:knives')),
                'result': utils.item_stack('firmalife:food/cocoa_butter')
            }
        }
    })

    for jar, remainder, _, ing in JARS:
        make_jar(rm, jar, remainder, ing)
    for fruit in TFC_FRUITS:
        make_jar(rm, fruit)
        simple_pot_recipe(rm, '%s_jar' % fruit, [utils.ingredient('firmalife:empty_jar'), utils.ingredient('#firmalife:sweetener'), not_rotten(has_trait('tfc:food/%s' % fruit, 'firmalife:dried', True))], '1000 minecraft:water', None, ['firmalife:%s_jar' % fruit])
    for fruit in FL_FRUITS:
        make_jar(rm, fruit)
        simple_pot_recipe(rm, '%s_jar' % fruit, [utils.ingredient('firmalife:empty_jar'), utils.ingredient('#firmalife:sweetener'), not_rotten(has_trait('firmalife:food/%s' % fruit, 'firmalife:dried', True))], '1000 minecraft:water', None, ['firmalife:%s_jar' % fruit])


    beet = not_rotten('tfc:food/beet')
    simple_pot_recipe(rm, 'beet_sugar', [beet, beet, beet, beet, beet], '1000 tfc:salt_water', output_items=['minecraft:sugar', 'minecraft:sugar', 'minecraft:sugar'])
    simple_pot_recipe(rm, 'soy_mixture', [not_rotten('tfc:food/soybean'), not_rotten('tfc:food/soybean'), utils.ingredient('tfc:powder/salt'), utils.ingredient('tfc:powder/salt')], '1000 minecraft:water', output_items=['firmalife:food/soy_mixture', 'firmalife:food/soy_mixture'])

    barrel_instant_recipe(rm, 'clean_any_bowl', '#firmalife:foods/washable', '100 minecraft:water', output_item=item_stack_provider(empty_bowl=True))

    barrel_sealed_recipe(rm, 'cleaning_jar', 'Cleaning Jar', 1000, '#firmalife:jars', '1000 minecraft:water', output_item='firmalife:empty_jar')
    barrel_sealed_recipe(rm, 'yeast_starter', 'Yeast Starter', 24000 * 3, not_rotten(has_trait('#tfc:foods/fruits', 'firmalife:dried')), '100 minecraft:water', output_fluid='100 firmalife:yeast_starter')
    barrel_sealed_recipe(rm, 'feed_yeast', 'Feeding Yeast', 12000, not_rotten('#firmalife:feeds_yeast'), '100 firmalife:yeast_starter', output_fluid='600 firmalife:yeast_starter')
    barrel_sealed_recipe(rm, 'pina_colada', 'Pina Colada', 1000, not_rotten('firmalife:food/frothy_coconut'), '1000 tfc:rum', output_fluid='1000 firmalife:pina_colada')
    barrel_sealed_recipe(rm, 'curdled_milk', 'Curdling Milk', 4000, 'firmalife:rennet', '2000 minecraft:milk', output_fluid='2000 tfc:curdled_milk')
    barrel_sealed_recipe(rm, 'curdled_yak_milk', 'Curdling Yak Milk', 4000, 'firmalife:rennet', '2000 firmalife:yak_milk', output_fluid='2000 firmalife:curdled_yak_milk')
    barrel_sealed_recipe(rm, 'curdled_goat_milk', 'Curdling Goat Milk', 4000, 'firmalife:rennet', '2000 firmalife:goat_milk', output_fluid='2000 firmalife:curdled_goat_milk')
    barrel_sealed_recipe(rm, 'milk_curd', 'Milk Curd', 1000, 'firmalife:cheesecloth', '1000 tfc:curdled_milk', output_item='firmalife:food/milk_curd')
    barrel_sealed_recipe(rm, 'goat_milk_curd', 'Goat Curd', 1000, 'firmalife:cheesecloth', '1000 firmalife:curdled_goat_milk', output_item='firmalife:food/goat_curd')
    barrel_sealed_recipe(rm, 'yak_milk_curd', 'Yak Curd', 1000, 'firmalife:cheesecloth', '1000 firmalife:curdled_yak_milk', output_item='firmalife:food/yak_curd')
    barrel_sealed_recipe(rm, 'cream', 'Cream', 1000, 'firmalife:cheesecloth', '1000 #tfc:milks', output_item='firmalife:cheesecloth', output_fluid='1000 firmalife:cream')

    barrel_sealed_recipe(rm, 'shosha', 'Shosha Wheel', 16000, '3 firmalife:food/yak_curd', '750 tfc:salt_water', output_item='firmalife:shosha_wheel')
    barrel_sealed_recipe(rm, 'feta', 'Feta Wheel', 16000, '3 firmalife:food/goat_curd', '750 tfc:salt_water', output_item='firmalife:feta_wheel')
    rm.domain = 'tfc'  # DOMAIN CHANGE
    barrel_sealed_recipe(rm, 'cheese', 'Gouda Wheel', 16000, '3 firmalife:food/milk_curd', '750 tfc:salt_water', output_item='firmalife:gouda_wheel')
    rm.domain = 'firmalife'  # DOMAIN RESET

    clay_knapping(rm, 'oven_top', ['XXXXX', 'XX XX', 'X   X', 'X   X', 'XXXXX'], 'firmalife:oven_top')
    clay_knapping(rm, 'oven_bottom', ['XX XX', 'X   X', 'X   X', 'XX XX', 'XXXXX'], 'firmalife:oven_bottom')
    clay_knapping(rm, 'oven_chimney', ['XX XX', 'XX XX', 'XX XX'], 'firmalife:oven_chimney')

    oven_recipe(rm, 'cooked_pie', not_rotten('firmalife:food/filled_pie'), 400, result_item=item_stack_provider('firmalife:food/cooked_pie', other_modifier='firmalife:copy_dynamic_food', remove_trait='firmalife:raw'))
    oven_recipe(rm, 'cooked_pizza', not_rotten('firmalife:food/raw_pizza'), 400, result_item=item_stack_provider('firmalife:food/cooked_pizza', other_modifier='firmalife:copy_dynamic_food', remove_trait='firmalife:raw'))
    oven_recipe(rm, 'pumpkin_pie', not_rotten('firmalife:food/raw_pumpkin_pie'), 400, result_item=item_stack_provider('firmalife:food/cooked_pumpkin_pie'))
    oven_recipe(rm, 'roasted_cocoa_beans', not_rotten('firmalife:food/cocoa_beans'), 400, result_item=item_stack_provider('firmalife:food/roasted_cocoa_beans'))

    # Firmalife Recipes
    for carving, pattern in CARVINGS.items():
        pumpkin_knapping(rm, carving, pattern, 'firmalife:carved_pumpkin/%s' % carving)
    pumpkin_knapping(rm, 'face', ['XXXXX', 'X X X', 'XXXXX', 'X   X', 'XXXXX'], 'minecraft:carved_pumpkin')
    pumpkin_knapping(rm, 'chunks', [' X X ', 'X X X', ' X X ', 'X X X', ' X X '], 'firmalife:food/pumpkin_chunks')

    drying_recipe(rm, 'drying_fruit', not_rotten(lacks_trait('#tfc:foods/fruits', 'firmalife:dried')), item_stack_provider(copy_input=True, add_trait='firmalife:dried'))
    drying_recipe(rm, 'cinnamon', 'firmalife:cinnamon_bark', item_stack_provider('firmalife:spice/cinnamon'))
    drying_recipe(rm, 'dry_grass', 'tfc:thatch', item_stack_provider('tfc:groundcover/dead_grass'))
    drying_recipe(rm, 'tofu', 'firmalife:food/soy_mixture', item_stack_provider('firmalife:food/tofu', copy_food=True))

    for dirt in ('loam', 'sandy_loam', 'silty_loam', 'silt'):
        drying_recipe(rm, '%s_dirt' % dirt, 'tfc:mud/%s' % dirt, item_stack_provider('tfc:dirt/%s' % dirt))

    smoking_recipe(rm, 'meat', not_rotten(has_trait(lacks_trait('#tfc:foods/raw_meats', 'firmalife:smoked'), 'tfc:brined')), item_stack_provider(copy_input=True, add_trait='firmalife:smoked'))
    smoking_recipe(rm, 'cheese', not_rotten(lacks_trait('#firmalife:foods/cheeses', 'firmalife:smoked')), item_stack_provider(copy_input=True, add_trait='firmalife:smoked'))

    mixing_recipe(rm, 'butter', ingredients=[utils.ingredient('tfc:powder/salt')], fluid='1000 firmalife:cream', output_item='firmalife:food/butter')
    mixing_recipe(rm, 'pie_dough', ingredients=[not_rotten('firmalife:food/butter'), not_rotten('#tfc:foods/flour'), utils.ingredient('#firmalife:sweetener')], fluid='1000 minecraft:water', output_item='firmalife:food/pie_dough')
    mixing_recipe(rm, 'pumpkin_pie_dough', ingredients=[utils.ingredient('minecraft:egg'), not_rotten('firmalife:food/pumpkin_chunks'), not_rotten('firmalife:food/pumpkin_chunks'), not_rotten('#tfc:foods/flour'), utils.ingredient('#firmalife:sweetener')], fluid='1000 minecraft:water', output_item='firmalife:food/pumpkin_pie_dough')
    mixing_recipe(rm, 'pizza_dough', ingredients=[not_rotten('#tfc:foods/dough'), utils.ingredient('tfc:powder/salt'), utils.ingredient('firmalife:spice/basil_leaves')], fluid='1000 tfc:olive_oil', output_item='4 firmalife:food/pizza_dough')
    mixing_recipe(rm, 'dark_chocolate_blend', ingredients=[utils.ingredient('#firmalife:sweetener'), not_rotten('firmalife:food/cocoa_powder'), not_rotten('firmalife:food/cocoa_powder')], fluid='1000 #tfc:milks', output_item='2 firmalife:food/dark_chocolate_blend')
    mixing_recipe(rm, 'white_chocolate_blend', ingredients=[utils.ingredient('#firmalife:sweetener'), not_rotten('firmalife:food/cocoa_butter'), not_rotten('firmalife:food/cocoa_butter')], fluid='1000 #tfc:milks', output_item='2 firmalife:food/white_chocolate_blend')
    mixing_recipe(rm, 'milk_chocolate_blend', ingredients=[utils.ingredient('#firmalife:sweetener'), not_rotten('firmalife:food/cocoa_butter'), not_rotten('firmalife:food/cocoa_powder')], fluid='1000 #tfc:milks', output_item='2 firmalife:food/milk_chocolate_blend')

    meal_shapeless(rm, 'crafting/filled_pie', (not_rotten('firmalife:food/pie_dough'), '#firmalife:foods/preserves', '#firmalife:pie_pans'), 'firmalife:food/filled_pie', 'firmalife:pie').with_advancement('firmalife:food/pie_dough')
    meal_shapeless(rm, 'crafting/raw_pizza', (not_rotten('firmalife:food/pizza_dough'), not_rotten('#firmalife:foods/pizza_ingredients'), not_rotten('#firmalife:foods/pizza_ingredients'), not_rotten('firmalife:food/shredded_cheese')), 'firmalife:food/raw_pizza', 'firmalife:pizza').with_advancement('firmalife:food/pizza_dough')

    # Greenhouse
    for block in GREENHOUSE_BLOCKS:
        for first, second in CLEANING_PAIRS.items():
            if block != 'door':
                chisel_recipe(rm, 'cleaning/%s_greenhouse_%s' % (first, block), 'firmalife:%s_greenhouse_%s' % (first, block), 'firmalife:%s_greenhouse_%s' % (second, block), 'smooth')
            else:
                damage_shapeless(rm, 'crafting/cleaning/%s_greenhouse_%s' % (first, block), ('#tfc:chisels', 'firmalife:%s_greenhouse_%s' % (first, block)), 'firmalife:%s_greenhouse_%s' % (second, block)).with_advancement('firmalife:%s_greenhouse_%s' % (first, block))
    for greenhouse, metal, namespace in (('iron', 'wrought_iron', 'tfc'), ('copper', 'copper', 'tfc'), ('stainless_steel', 'stainless_steel', 'firmalife'), ('treated_wood', 'treated_lumber', 'firmalife')):
        rod = '%s:metal/rod/%s' % (namespace, metal) if greenhouse != 'treated_wood' else 'firmalife:treated_lumber'
        mapping = {'X': rod, 'Y': 'minecraft:glass'}
        rm.crafting_shaped('crafting/greenhouse/%s_greenhouse_wall' % greenhouse, ['XYX', 'XYX', 'XYX'], mapping, (8, 'firmalife:%s_greenhouse_wall' % greenhouse)).with_advancement(rod)
        rm.crafting_shaped('crafting/greenhouse/%s_greenhouse_roof_top' % greenhouse, ['XYX', 'YXY'], mapping, (8, 'firmalife:%s_greenhouse_roof_top' % greenhouse)).with_advancement(rod)
        rm.crafting_shaped('crafting/greenhouse/%s_greenhouse_roof' % greenhouse, ['Y  ', 'XY ', 'XXY'], mapping, (4, 'firmalife:%s_greenhouse_roof' % greenhouse)).with_advancement(rod)
        rm.crafting_shaped('crafting/greenhouse/%s_greenhouse_door' % greenhouse, ['XY', 'XY', 'XY'], mapping, (2, 'firmalife:%s_greenhouse_door' % greenhouse)).with_advancement(rod)

    # Grain Stuff
    for grain in TFC_GRAINS:
        damage_shapeless(rm, 'crafting/%s_slice' % grain, ('tfc:food/%s_bread' % grain, '#tfc:knives'), '2 firmalife:food/%s_slice' % grain).with_advancement('tfc:food/%s_bread' % grain)

        rm.crafting_shapeless('crafting/%s_dough' % grain, (not_rotten('tfc:food/%s_flour' % grain), fluid_item_ingredient('100 firmalife:yeast_starter'), '#firmalife:sweetener'), (4, 'firmalife:food/%s_dough' % grain)).with_advancement('tfc:food/%s_grain' % grain)

        oven_recipe(rm, grain + '_bread', not_rotten('firmalife:food/%s_dough' % grain), 200, result_item=item_stack_provider('tfc:food/%s_bread' % grain))
        heat_recipe(rm, 'toast', not_rotten('#firmalife:foods/slices'), 200, result_item=item_stack_provider('firmalife:food/toast'))
        rm.domain = 'tfc'  # DOMAIN CHANGE
        heat_recipe(rm, grain + '_dough', not_rotten('tfc:food/%s_dough' % grain), 200, result_item=item_stack_provider('firmalife:food/%s_flatbread' % grain, copy_food=True))
        rm.domain = 'firmalife'  # DOMAIN RESET

    ore = 'chromite'
    for rock, data in TFC_ROCKS.items():
        cobble = 'tfc:rock/cobble/%s' % rock
        collapse_recipe(rm, '%s_cobble' % rock, [
            'firmalife:ore/poor_%s/%s' % (ore, rock),
            'firmalife:ore/normal_%s/%s' % (ore, rock),
            'firmalife:ore/rich_%s/%s' % (ore, rock)
        ], cobble)
        for grade in ORE_GRADES.keys():
            rm.block_tag('can_start_collapse', 'firmalife:ore/%s_%s/%s' % (grade, ore, rock))
            rm.block_tag('can_collapse', 'firmalife:ore/%s_%s/%s' % (grade, ore, rock))

    alloy_recipe(rm, 'stainless_steel', 'stainless_steel', ('firmalife:chromium', 0.2, 0.3), ('tfc:nickel', 0.1, 0.2), ('tfc:steel', 0.6, 0.8))
    anvil_recipe(rm, 'pie_pan', '#forge:sheets/cast_iron', '4 firmalife:pie_pan', 1, Rules.hit_last, Rules.hit_second_last, Rules.draw_third_last)

    for recipe in DISABLED_TFC_RECIPES:
        rm.domain = 'tfc' # DOMAIN CHANGE
        disable_recipe(rm, recipe)
        rm.domain = 'firmalife' # DOMAIN RESET

def disable_recipe(rm: ResourceManager, name_parts: ResourceIdentifier):
    # noinspection PyTypeChecker
    rm.recipe(name_parts, None, {}, conditions='forge:false')

def write_crafting_recipe(rm: ResourceManager, name_parts: ResourceIdentifier, data: Json) -> RecipeContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'data', res.domain, 'recipes', 'crafting', res.path), data)
    return RecipeContext(rm, res)

def meal_shapeless(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredients: Json, result: str, mod: str) -> RecipeContext:
    return advanced_shapeless(rm, name_parts, ingredients, item_stack_provider(result, other_modifier=mod), primary_ingredient=utils.ingredient(ingredients[0]))

def advanced_shapeless(rm: ResourceManager, name_parts: ResourceIdentifier, ingredients: Json, result: Json, primary_ingredient: Json, group: str = None, conditions: Optional[Json] = None) -> RecipeContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'data', res.domain, 'recipes', res.path), {
        'type': 'tfc:advanced_shapeless_crafting',
        'group': group,
        'ingredients': utils.item_stack_list(ingredients),
        'result': result,
        'primary_ingredient': utils.ingredient(primary_ingredient),
        'conditions': utils.recipe_condition(conditions)
    })
    return RecipeContext(rm, res)

def advanced_shaped(rm: ResourceManager, name_parts: ResourceIdentifier, pattern: Sequence[str], ingredients: Json, result: Json, input_xy: Tuple[int, int], group: str = None, conditions: Optional[Json] = None) -> RecipeContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'data', res.domain, 'recipes', res.path), {
        'type': 'tfc:advanced_shaped_crafting',
        'group': group,
        'pattern': pattern,
        'key': utils.item_stack_dict(ingredients, ''.join(pattern)[0]),
        'result': item_stack_provider(result),
        'input_row': input_xy[1],
        'input_column': input_xy[0],
        'conditions': utils.recipe_condition(conditions)
    })
    return RecipeContext(rm, res)

def collapse_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient, result: Optional[utils.Json] = None, copy_input: Optional[bool] = None):
    assert result is not None or copy_input
    rm.recipe(('collapse', name_parts), 'tfc:collapse', {
        'ingredient': ingredient,
        'result': result,
        'copy_input': copy_input
    })

def make_jar(rm: ResourceManager, jar: str, remainder: int = -1, ing: str = None):
    if ing is not None:
        if remainder == 8:
            rm.crafting_shaped('crafting/%s_jar' % jar, ['XXX', 'XYX', 'XXX'], {'X': ing, 'Y': 'firmalife:empty_jar'}, 'firmalife:%s_jar' % jar).with_advancement('firmalife:empty_jar')
        elif remainder == 1:
            rm.crafting_shapeless('crafting/%s_jar' % jar, ('firmalife:empty_jar', ing), 'firmalife:%s_jar' % jar).with_advancement('firmalife:empty_jar')
        rm.crafting_shapeless('crafting/%s_jar_open' % jar, ('firmalife:%s_jar' % jar), (remainder, ing))

def fluid_item_ingredient(fluid: Json, delegate: Json = None):
    return {
        'type': 'tfc:fluid_item',
        'ingredient': delegate,
        'fluid_ingredient': fluid_stack_ingredient(fluid)
    }

def damage_shapeless(rm: ResourceManager, name_parts: ResourceIdentifier, ingredients: Json, result: Json, group: str = None, conditions: utils.Json = None) -> RecipeContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'data', res.domain, 'recipes', res.path), {
        'type': 'tfc:damage_inputs_shapeless_crafting',
        'recipe': {
            'type': 'minecraft:crafting_shapeless',
            'group': group,
            'ingredients': utils.item_stack_list(ingredients),
            'result': utils.item_stack(result),
            'conditions': utils.recipe_condition(conditions)
        }
    })
    return RecipeContext(rm, res)

def chisel_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, result: str, mode: str):
    rm.recipe(('chisel', mode, name_parts), 'tfc:chisel', {
        'ingredient': ingredient,
        'result': result,
        'mode': mode,
        'extra_drop': item_stack_provider(result) if mode == 'slab' else None
    })

def mixing_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredients: Json = None, fluid: str = None, output_fluid: str = None, output_item: str = None) -> RecipeContext:
    rm.recipe(('mixing_bowl', name_parts), 'firmalife:mixing_bowl', {
        'ingredients': ingredients if ingredients is not None else None,
        'fluid_ingredient': fluid_stack_ingredient(fluid) if fluid is not None else None,
        'output_fluid': fluid_stack(output_fluid) if output_fluid is not None else None,
        'output_item': utils.item_stack(output_item) if output_item is not None else None
    })

def drying_recipe(rm: ResourceManager, name: utils.ResourceIdentifier, item: Any, result: Json) -> RecipeContext:
    return rm.recipe(('drying', name), 'firmalife:drying', {
        'ingredient': utils.ingredient(item) if isinstance(item, str) else item,
        'result': result
    })

def smoking_recipe(rm: ResourceManager, name: utils.ResourceIdentifier, item: Any, result: Json) -> RecipeContext:
    return rm.recipe(('smoking', name), 'firmalife:smoking', {
        'ingredient': utils.ingredient(item) if isinstance(item, str) else item,
        'result': result
    })

def has_trait(ingredient: Json, trait: str, invert: bool = False) -> Json:
    return {
        'type': 'tfc:lacks_trait' if invert else 'tfc:has_trait',
        'trait': trait,
        'ingredient': utils.ingredient(ingredient)
    }

def lacks_trait(ingredient: Json, trait: str) -> Json:
    return has_trait(ingredient, trait, True)

def not_rotten(ingredient: Json) -> Json:
    return {
        'type': 'tfc:not_rotten',
        'ingredient': utils.ingredient(ingredient)
    }

def item_stack_provider(data_in: Json = None, copy_input: bool = False, copy_heat: bool = False, copy_food: bool = False, copy_oldest_food: bool = False, reset_food: bool = False, add_heat: float = None, add_trait: str = None, remove_trait: str = None, empty_bowl: bool = False, copy_forging: bool = False, other_modifier: str = None, other_other_modifier: str = None) -> Json:
    if isinstance(data_in, dict):
        return data_in
    stack = utils.item_stack(data_in) if data_in is not None else None
    modifiers = [k for k, v in (
        ('tfc:copy_input', copy_input),
        ('tfc:copy_heat', copy_heat),
        ('tfc:copy_food', copy_food),
        ('tfc:reset_food', reset_food),
        ('tfc:empty_bowl', empty_bowl),
        ('tfc:copy_forging_bonus', copy_forging),
        ('tfc:copy_oldest_food', copy_oldest_food),
        (other_modifier, other_modifier is not None),
        (other_other_modifier, other_other_modifier is not None),
        ({'type': 'tfc:add_heat', 'temperature': add_heat}, add_heat is not None),
        ({'type': 'tfc:add_trait', 'trait': add_trait}, add_trait is not None),
        ({'type': 'tfc:remove_trait', 'trait': remove_trait}, remove_trait is not None)
    ) if v]
    if modifiers:
        return {
            'stack': stack,
            'modifiers': modifiers
        }
    return stack

def anvil_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: Json, result: Json, tier: int, *rules: Rules, bonus: bool = None):
    rm.recipe(('anvil', name_parts), 'tfc:anvil', {
        'input': utils.ingredient(ingredient),
        'result': item_stack_provider(result),
        'tier': tier,
        'rules': [r.name for r in rules],
        'apply_forging_bonus': bonus
    })

def welding_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, first_input: Json, second_input: Json, result: Json, tier: int, ):
    rm.recipe(('welding', name_parts), 'tfc:welding', {
        'first_input': utils.ingredient(first_input),
        'second_input': utils.ingredient(second_input),
        'tier': tier,
        'result': item_stack_provider(result)
    })

def simple_pot_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredients: Json, fluid: str, output_fluid: str = None, output_items: Json = None, duration: int = 2000, temp: int = 300):
    rm.recipe(('pot', name_parts), 'tfc:pot', {
        'ingredients': ingredients,
        'fluid_ingredient': fluid_stack_ingredient(fluid),
        'duration': duration,
        'temperature': temp,
        'fluid_output': fluid_stack(output_fluid) if output_fluid is not None else None,
        'item_output': [utils.item_stack(item) for item in output_items] if output_items is not None else None
    })

def oven_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, temperature: float, result_item: Optional[Union[str, Json]], duration: int = 1000) -> RecipeContext:
    result_item = item_stack_provider(result_item) if isinstance(result_item, str) else result_item
    return rm.recipe(('oven', name_parts), 'firmalife:oven', {
        'ingredient': utils.ingredient(ingredient),
        'result_item': result_item,
        'temperature': temperature,
        'duration': duration
    })

def heat_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, temperature: float, result_item: Optional[Union[str, Json]], result_fluid: Optional[str] = None) -> RecipeContext:
    result_item = item_stack_provider(result_item) if isinstance(result_item, str) else result_item
    result_fluid = None if result_fluid is None else fluid_stack(result_fluid)
    return rm.recipe(('heating', name_parts), 'tfc:heating', {
        'ingredient': utils.ingredient(ingredient),
        'result_item': result_item,
        'result_fluid': result_fluid,
        'temperature': temperature
    })

def fluid_stack(data_in: Json) -> Json:
    if isinstance(data_in, dict):
        return data_in
    fluid, tag, amount, _ = utils.parse_item_stack(data_in, False)
    assert not tag, 'fluid_stack() cannot be a tag'
    return {
        'fluid': fluid,
        'amount': amount
    }

def casting_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, mold: str, metal: str, amount: int, break_chance: float):
    rm.recipe(('casting', name_parts), 'tfc:casting', {
        'mold': {'item': 'tfc:ceramic/%s_mold' % mold},
        'fluid': fluid_stack_ingredient('%d firmalife:metal/%s' % (amount, metal)),
        'result': utils.item_stack('firmalife:metal/%s/%s' % (mold, metal)),
        'break_chance': break_chance
    })

def fluid_stack_ingredient(data_in: Json) -> Json:
    if isinstance(data_in, dict):
        return {
            'ingredient': fluid_ingredient(data_in['ingredient']),
            'amount': data_in['amount']
        }
    if pair := utils.maybe_unordered_pair(data_in, int, object):
        amount, fluid = pair
        return {'ingredient': fluid_ingredient(fluid), 'amount': amount}
    fluid, tag, amount, _ = utils.parse_item_stack(data_in, False)
    if tag:
        return {'ingredient': {'tag': fluid}, 'amount': amount}
    else:
        return {'ingredient': fluid, 'amount': amount}

def fluid_ingredient(data_in: Json) -> Json:
    if isinstance(data_in, dict):
        return data_in
    elif isinstance(data_in, List):
        return [*utils.flatten_list([fluid_ingredient(e) for e in data_in])]
    else:
        fluid, tag, amount, _ = utils.parse_item_stack(data_in, False)
        if tag:
            return {'tag': fluid}
        else:
            return fluid

def barrel_instant_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, input_item: Optional[Json] = None, input_fluid: Optional[Json] = None, output_item: Optional[Json] = None, output_fluid: Optional[Json] = None, sound: Optional[str] = None):
    rm.recipe(('barrel', name_parts), 'tfc:barrel_instant', {
        'input_item': item_stack_ingredient(input_item) if input_item is not None else None,
        'input_fluid': fluid_stack_ingredient(input_fluid) if input_fluid is not None else None,
        'output_item': item_stack_provider(output_item) if output_item is not None else None,
        'output_fluid': fluid_stack(output_fluid) if output_fluid is not None else None,
        'sound': sound
    })

def barrel_sealed_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, translation: str, duration: int, input_item: Optional[Json] = None, input_fluid: Optional[Json] = None, output_item: Optional[Json] = None, output_fluid: Optional[Json] = None, on_seal: Optional[Json] = None, on_unseal: Optional[Json] = None, sound: Optional[str] = None):
    rm.recipe(('barrel', name_parts), 'tfc:barrel_sealed', {
        'input_item': item_stack_ingredient(input_item) if input_item is not None else None,
        'input_fluid': fluid_stack_ingredient(input_fluid) if input_fluid is not None else None,
        'output_item': item_stack_provider(output_item) if isinstance(output_item, str) else output_item,
        'output_fluid': fluid_stack(output_fluid) if output_fluid is not None else None,
        'duration': duration,
        'on_seal': on_seal,
        'on_unseal': on_unseal,
        'sound': sound
    })
    res = utils.resource_location('firmalife', name_parts)
    rm.lang('tfc.recipe.barrel.' + res.domain + '.barrel.' + res.path.replace('/', '.'), lang(translation))

def item_stack_ingredient(data_in: Json):
    if isinstance(data_in, dict):
        return {
            'ingredient': utils.ingredient(data_in['ingredient']),
            'count': data_in['count'] if data_in.get('count') is not None else None
        }
    if pair := utils.maybe_unordered_pair(data_in, int, object):
        count, item = pair
        return {'ingredient': fluid_ingredient(item), 'count': count}
    item, tag, count, _ = utils.parse_item_stack(data_in, False)
    if tag:
        return {'ingredient': {'tag': item}, 'count': count}
    else:
        return {'ingredient': {'item': item}, 'count': count}

def delegate_recipe(rm: ResourceManager, name_parts: ResourceIdentifier, recipe_type: str, delegate: Json) -> RecipeContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'data', res.domain, 'recipes', res.path), {
        'type': recipe_type,
        'recipe': delegate
    })
    return RecipeContext(rm, res)

def pumpkin_knapping(rm: ResourceManager, name_parts: ResourceIdentifier, pattern: List[str], result: Json, outside_slot_required: bool = None):
    knapping_recipe(rm, 'firmalife:pumpkin_knapping', name_parts, pattern, result, outside_slot_required)

def clay_knapping(rm: ResourceManager, name_parts: ResourceIdentifier, pattern: List[str], result: Json, outside_slot_required: bool = None):
    knapping_recipe(rm, 'tfc:clay_knapping', name_parts, pattern, result, outside_slot_required)

def knapping_recipe(rm: ResourceManager, knapping_type: str, name_parts: utils.ResourceIdentifier, pattern: List[str], result: utils.Json, outside_slot_required: bool = None):
    rm.recipe((knapping_type, name_parts), knapping_type, {
        'outside_slot_required': outside_slot_required,
        'pattern': pattern,
        'result': utils.item_stack(result)
    })

def alloy_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, metal: str, *parts: Tuple[str, float, float]):
    rm.recipe(('alloy', name_parts), 'tfc:alloy', {
        'result': 'firmalife:%s' % metal,
        'contents': [{
            'metal': p[0],
            'min': p[1],
            'max': p[2]
        } for p in parts]
    })
