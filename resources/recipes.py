from enum import Enum
from itertools import repeat
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
    # Chisel
    def chisel_stair_slab(name: str, ingredient: str):
        chisel_recipe(rm, name + '_stairs', ingredient, ingredient + '_stairs', 'stair')
        chisel_recipe(rm, name + '_slab', ingredient, ingredient + '_slab', 'slab')
    chisel_stair_slab('tiles', 'firmalife:tiles')
    chisel_stair_slab('rustic_bricks', 'firmalife:rustic_bricks')

    # Vat

    vat_recipe(rm, 'olive_oil_water', 'tfc:olive_paste', '200 minecraft:water', output_fluid='200 tfc:olive_oil_water')
    vat_recipe(rm, 'tallow', 'tfc:blubber', '200 minecraft:water', output_fluid='200 tfc:tallow')
    vat_recipe(rm, 'lye', 'tfc:powder/wood_ash', '200 minecraft:water', output_fluid='200 tfc:lye')
    vat_recipe(rm, 'cooked_rice', {'ingredient': not_rotten('tfc:food/rice_grain')}, '200 minecraft:water', output_item='tfc:food/cooked_rice')
    vat_recipe(rm, 'boiled_egg', {'ingredient': not_rotten('#firmalife:foods/raw_eggs')}, '200 minecraft:water', output_item='tfc:food/boiled_egg')
    for color in COLORS:
        vat_recipe(rm, '%s_dye' % color, 'minecraft:%s_dye' % color, '1000 minecraft:water', output_fluid='1000 tfc:%s_dye' % color)
    vat_recipe(rm, 'beet_sugar', {'count': 5, 'ingredient': not_rotten('tfc:food/beet')}, '1000 tfc:salt_water', output_item='3 minecraft:sugar')
    vat_recipe(rm, 'soy_mixture', {'ingredient': not_rotten('tfc:food/soybean')}, '1000 tfc:salt_water', output_item='firmalife:food/soy_mixture')
    vat_recipe(rm, 'cured_maize', {'ingredient': not_rotten('tfc:food/maize_grain')}, '1000 tfc:limewater', output_item='firmalife:food/cured_maize')
    vat_recipe(rm, 'tomato_sauce', {'ingredient': not_rotten('firmalife:food/tomato_sauce_mix')}, '200 minecraft:water', output_item='firmalife:food/tomato_sauce')
    vat_recipe(rm, 'sugar_water', '#tfc:sweetener', '1000 minecraft:water', output_fluid='500 firmalife:sugar_water')

    for jar, remainder, ing in JARS:
        make_jar(rm, jar, remainder, ing)
    for fruit in FL_FRUITS:
        ing = not_rotten(lacks_trait('firmalife:food/%s' % fruit, 'firmalife:dried'))
        vat_recipe(rm, '%s_jar' % fruit, {'ingredient': ing}, '500 firmalife:sugar_water', jar='firmalife:jar/%s' % fruit, output_texture='firmalife:block/jar/%s' % fruit)
        for count in (2, 3, 4):
            rm.recipe(('pot', 'jam_%s_%s' % (fruit, count)), 'tfc:pot_jam', {
                'ingredients': [ing] * count + [utils.ingredient('#tfc:sweetener')],
                'fluid_ingredient': fluid_stack_ingredient('100 minecraft:water'),
                'duration': 500,
                'temperature': 300,
                'result': utils.item_stack('%s firmalife:jar/%s' % (count, fruit)),
                'texture': 'firmalife:block/jar/%s' % fruit
            })
        rm.crafting_shapeless('crafting/unseal_%s_jar' % fruit, (not_rotten('firmalife:jar/%s' % fruit), ), 'firmalife:jar/%s_unsealed' % fruit).with_advancement('firmalife:jar/%s' % fruit)
    for fruit in TFC_FRUITS:
        ing = {'ingredient': not_rotten(lacks_trait('tfc:food/%s' % fruit, 'firmalife:dried'))}
        vat_recipe(rm, '%s_jar' % fruit, ing, '500 firmalife:sugar_water', jar='tfc:jar/%s' % fruit, output_texture='tfc:block/jar/%s' % fruit)

    beet = not_rotten('tfc:food/beet')
    simple_pot_recipe(rm, 'beet_sugar', [beet, beet, beet, beet, beet], '100 tfc:salt_water', output_items=['minecraft:sugar', 'minecraft:sugar', 'minecraft:sugar'])
    simple_pot_recipe(rm, 'beet_sugar_freshwater', [beet, beet, beet, beet, utils.ingredient('tfc:powder/salt')], '100 minecraft:water', output_items=['minecraft:sugar', 'minecraft:sugar'])
    simple_pot_recipe(rm, 'soy_mixture', [not_rotten('tfc:food/soybean'), not_rotten('tfc:food/soybean'), utils.ingredient('tfc:powder/salt'), utils.ingredient('tfc:powder/salt')], '100 minecraft:water', output_items=['firmalife:food/soy_mixture', 'firmalife:food/soy_mixture'])
    simple_pot_recipe_5(rm, 'cured_maize', not_rotten('tfc:food/maize_grain'), '100 tfc:limewater', output_items='firmalife:food/cured_maize', duration=3000)
    simple_pot_recipe(rm, 'tomato_sauce', [not_rotten('tfc:food/tomato'), utils.ingredient('tfc:powder/salt'), not_rotten('tfc:food/garlic')], '100 minecraft:water', output_items=['firmalife:food/tomato_sauce', 'firmalife:food/tomato_sauce', 'firmalife:food/tomato_sauce', 'firmalife:food/tomato_sauce', 'firmalife:food/tomato_sauce'])
    simple_pot_recipe(rm, 'chocolate', [utils.ingredient('#tfc:sweetener'), not_rotten('#firmalife:foods/chocolate')], '1000 #tfc:milks', output_fluid='1000 firmalife:chocolate')
    bowl_recipe_5(rm, 'cooked_pasta', not_rotten('firmalife:food/raw_egg_noodles'), '100 minecraft:water', output_items='firmalife:food/cooked_pasta', duration=2000, data={'food': {'hunger': 4, 'saturation': 2, 'decay_modifier': 3, 'grain': 1.5}})
    bowl_recipe_5(rm, 'cooked_rice_noodles', not_rotten('firmalife:food/raw_rice_noodles'), '100 minecraft:water', output_items='firmalife:food/cooked_rice_noodles', duration=2000, data={'food': {'hunger': 4, 'saturation': 2, 'decay_modifier': 3, 'grain': 1.5}})

    soup_food = not_rotten(utils.ingredient('#tfc:foods/usable_in_soup'))
    for duration, count in ((1000, 3), (1150, 4), (1300, 5)):
        ingr = [soup_food] * count
        ingr.append(not_rotten(utils.ingredient('firmalife:food/nightshade_berry')))
        rm.recipe(('pot', 'stinky_soup_%s' % count), 'firmalife:stinky_soup', {
            'ingredients': ingr,
            'fluid_ingredient': fluid_stack_ingredient('100 minecraft:water'),
            'duration': duration,
            'temperature': 300
        })

    barrel_instant_recipe(rm, 'tirage', '#tfc:sweetener', '100 firmalife:yeast_starter', output_item='firmalife:tirage_mixture')
    barrel_instant_recipe(rm, 'clean_any_bowl', '#firmalife:foods/washable', '100 minecraft:water', output_item=item_stack_provider(other_modifier='firmalife:empty_pan'))
    for glass in ('olivine', 'volcanic', 'hematitic'):
        barrel_instant_recipe(rm, 'clean_%s_wine_bottle' % glass, 'firmalife:%s_wine_bottle' % glass, '100 minecraft:water', output_item='firmalife:empty_%s_wine_bottle' % glass)

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
    barrel_sealed_recipe(rm, 'nixtamal', 'Nixtamal', 1000, not_rotten('firmalife:food/cured_maize'), '100 minecraft:water', output_item='firmalife:food/nixtamal')
    barrel_sealed_recipe(rm, 'mead', 'Mead', 72000, 'firmalife:raw_honey', '100 minecraft:water', output_fluid='100 firmalife:mead')
    barrel_sealed_recipe(rm, 'fermented_red_grapes', 'Fermenting Red Grapes', 24000 * 5, not_rotten(lacks_trait('firmalife:food/smashed_red_grapes', 'firmalife:fermented')), output_item=item_stack_provider(copy_input=True, add_trait='firmalife:fermented'))
    barrel_sealed_recipe(rm, 'fermented_white_grapes', 'Fermenting White Grapes', 24000 * 5, not_rotten(lacks_trait('firmalife:food/smashed_white_grapes', 'firmalife:fermented')), output_item=item_stack_provider(copy_input=True, add_trait='firmalife:fermented'))
    barrel_sealed_recipe(rm, 'cork', 'Making Corks', 24000, 'firmalife:treated_lumber', '1000 tfc:limewater', output_item='8 firmalife:cork')
    barrel_sealed_recipe(rm, 'soybean_oil', 'Soybean Oil', 24000, 'firmalife:food/soybean_paste', '100 minecraft:water', output_fluid='250 firmalife:soybean_oil')

    barrel_sealed_recipe(rm, 'shosha', 'Shosha Wheel', 16000, '3 firmalife:food/yak_curd', '750 tfc:salt_water', output_item='firmalife:shosha_wheel')
    barrel_sealed_recipe(rm, 'feta', 'Feta Wheel', 16000, '3 firmalife:food/goat_curd', '750 tfc:salt_water', output_item='firmalife:feta_wheel')
    barrel_sealed_recipe(rm, 'gouda', 'Gouda Wheel', 16000, '3 firmalife:food/milk_curd', '750 tfc:salt_water', output_item='firmalife:gouda_wheel')

    quern_recipe(rm, 'masa', not_rotten('firmalife:food/nixtamal'), 'firmalife:food/masa_flour', count=4)
    quern_recipe(rm, 'crushed_red_grapes', not_rotten('firmalife:food/red_grapes'), item_stack_provider('firmalife:food/smashed_red_grapes', copy_food=True))
    quern_recipe(rm, 'crushed_white_grapes', not_rotten('firmalife:food/white_grapes'), item_stack_provider('firmalife:food/smashed_white_grapes', copy_food=True))
    quern_recipe(rm, 'soybean_paste', not_rotten('firmalife:food/dehydrated_soybeans'), item_stack_provider('firmalife:food/soybean_paste', copy_food=True))

    loom_recipe(rm, 'pineapple_leather', '16 firmalife:pineapple_yarn', 'firmalife:pineapple_leather', 16, 'firmalife:block/pineapple')

    clay_knapping(rm, 'oven_top', ['XXXXX', 'XX XX', 'X   X', 'X   X', 'XXXXX'], 'firmalife:oven_top')
    clay_knapping(rm, 'oven_bottom', ['XX XX', 'X   X', 'X   X', 'XX XX', 'XXXXX'], 'firmalife:oven_bottom')
    clay_knapping(rm, 'oven_chimney', ['XX XX', 'XX XX', 'XX XX'], 'firmalife:oven_chimney')

    oven_recipe(rm, 'cooked_pie', not_rotten('firmalife:food/filled_pie'), 400, result_item=item_stack_provider('firmalife:food/cooked_pie', other_modifier='firmalife:copy_dynamic_food'))
    oven_recipe(rm, 'cooked_pizza', not_rotten('firmalife:food/raw_pizza'), 400, result_item=item_stack_provider('firmalife:food/cooked_pizza', other_modifier='firmalife:copy_dynamic_food'))
    oven_recipe(rm, 'pumpkin_pie', not_rotten('firmalife:food/raw_pumpkin_pie'), 400, result_item=item_stack_provider('minecraft:pumpkin_pie', other_modifier='firmalife:copy_dynamic_food'))
    oven_recipe(rm, 'roasted_cocoa_beans', not_rotten('firmalife:food/cocoa_beans'), 400, result_item=item_stack_provider('firmalife:food/roasted_cocoa_beans'))
    oven_recipe(rm, 'taco_shell', not_rotten('firmalife:food/corn_tortilla'), 400, result_item=item_stack_provider('firmalife:food/taco_shell'))
    oven_recipe(rm, 'sugar_cookie', not_rotten('firmalife:food/cookie_dough'), 400, result_item=item_stack_provider('firmalife:food/sugar_cookie'))
    oven_recipe(rm, 'chocolate_chip_cookie', not_rotten('firmalife:food/chocolate_chip_cookie_dough'), 400, result_item=item_stack_provider('firmalife:food/chocolate_chip_cookie'))
    oven_recipe(rm, 'hardtack', not_rotten('firmalife:food/hardtack_dough'), 400, result_item=item_stack_provider('firmalife:food/hardtack'))
    oven_recipe(rm, 'lasagna', not_rotten('firmalife:food/raw_lasagna'), 400, result_item=item_stack_provider('firmalife:food/cooked_lasagna'))

    # Firmalife Recipes
    knapping_type(rm, 'pumpkin', {'ingredient': not_rotten('#firmalife:pumpkin_knapping'), 'count': 1}, None, 'tfc:item.knapping.leather', False, False, False, 'tfc:pumpkin')

    for carving, pattern in CARVINGS.items():
        pumpkin_knapping(rm, carving, pattern, 'firmalife:carved_pumpkin/%s' % carving)
    pumpkin_knapping(rm, 'face', ['XXXXX', 'X X X', 'XXXXX', 'X   X', 'XXXXX'], 'minecraft:carved_pumpkin')
    pumpkin_knapping(rm, 'chunks', [' X X ', 'X X X', ' X X ', 'X X X', ' X X '], '4 tfc:food/pumpkin_chunks')

    drying_recipe(rm, 'drying_fruit', not_rotten(lacks_trait('#tfc:foods/fruits', 'firmalife:dried')), item_stack_provider(copy_input=True, add_trait='firmalife:dried'))
    drying_recipe(rm, 'cinnamon', 'firmalife:cinnamon_bark', item_stack_provider('firmalife:spice/cinnamon'))
    drying_recipe(rm, 'dry_grass', 'tfc:thatch', item_stack_provider('tfc:groundcover/dead_grass'))
    drying_recipe(rm, 'tofu', 'firmalife:food/soy_mixture', item_stack_provider('firmalife:food/tofu', copy_food=True))
    drying_recipe(rm, 'vanilla', 'firmalife:plant/vanilla', item_stack_provider('firmalife:spice/vanilla'))
    drying_recipe(rm, 'dehydrated_soybeans', not_rotten('tfc:food/soybean'), item_stack_provider('firmalife:food/dehydrated_soybeans', copy_food=True))
    for choc in ('milk', 'white', 'dark'):
        drying_recipe(rm, '%s_chocolate' % choc, 'firmalife:food/%s_chocolate_blend' % choc, item_stack_provider('firmalife:food/%s_chocolate' % choc))
    for dirt in ('loam', 'sandy_loam', 'silty_loam', 'silt'):
        drying_recipe(rm, '%s_dirt' % dirt, 'tfc:mud/%s' % dirt, item_stack_provider('tfc:dirt/%s' % dirt))

    stomping_recipe(rm, 'red_grapes', not_rotten(lacks_trait('firmalife:food/red_grapes', 'firmalife:dried')), item_stack_provider('firmalife:food/smashed_red_grapes'), 'firmalife:block/red_unsmashed_grapes', 'firmalife:block/red_smashed_grapes')
    stomping_recipe(rm, 'white_grapes', not_rotten(lacks_trait('firmalife:food/white_grapes', 'firmalife:dried')), item_stack_provider('firmalife:food/smashed_white_grapes'), 'firmalife:block/white_unsmashed_grapes', 'firmalife:block/white_smashed_grapes')
    stomping_recipe(rm, 'charcoal', 'minecraft:charcoal', item_stack_provider('4 tfc:powder/charcoal'), 'tfc:block/charcoal_pile', 'tfc:block/powder/charcoal', 'tfc:block.charcoal.fall')
    stomping_recipe(rm, 'soybean_paste', not_rotten('firmalife:food/dehydrated_soybeans'), item_stack_provider('firmalife:food/soybean_paste'), 'firmalife:block/dehydrated_soybeans', 'firmalife:block/soybean_paste')

    smoking_recipe(rm, 'meat', not_rotten(has_trait(lacks_trait('#tfc:foods/raw_meats', 'firmalife:smoked'), 'tfc:brined')), item_stack_provider(copy_input=True, add_trait='firmalife:smoked'))
    smoking_recipe(rm, 'cheese', not_rotten(lacks_trait('#firmalife:foods/cheeses', 'firmalife:smoked')), item_stack_provider(copy_input=True, add_trait='firmalife:smoked'))

    mixing_recipe(rm, 'butter', ingredients=[utils.ingredient('tfc:powder/salt')], fluid='1000 firmalife:cream', output_item='firmalife:food/butter')
    mixing_recipe(rm, 'pie_dough', ingredients=[not_rotten('firmalife:food/butter'), not_rotten('#tfc:foods/flour'), utils.ingredient('#tfc:sweetener')], fluid='1000 minecraft:water', output_item='firmalife:food/pie_dough')
    mixing_recipe(rm, 'pumpkin_pie_dough', ingredients=[utils.ingredient('#firmalife:foods/raw_eggs'), not_rotten('tfc:food/pumpkin_chunks'), not_rotten('tfc:food/pumpkin_chunks'), not_rotten('#tfc:foods/flour'), utils.ingredient('#tfc:sweetener')], fluid='1000 minecraft:water', output_item='firmalife:food/pumpkin_pie_dough')
    mixing_recipe(rm, 'pizza_dough', ingredients=[not_rotten('#tfc:foods/dough'), utils.ingredient('tfc:powder/salt'), utils.ingredient('firmalife:spice/basil_leaves')], fluid='100 #firmalife:oils', output_item='4 firmalife:food/pizza_dough')
    mixing_recipe(rm, 'dark_chocolate_blend', ingredients=[utils.ingredient('#tfc:sweetener'), not_rotten('firmalife:food/cocoa_powder'), not_rotten('firmalife:food/cocoa_powder')], fluid='1000 #tfc:milks', output_item='2 firmalife:food/dark_chocolate_blend')
    mixing_recipe(rm, 'white_chocolate_blend', ingredients=[utils.ingredient('#tfc:sweetener'), not_rotten('firmalife:food/cocoa_butter'), not_rotten('firmalife:food/cocoa_butter')], fluid='1000 #tfc:milks', output_item='2 firmalife:food/white_chocolate_blend')
    mixing_recipe(rm, 'milk_chocolate_blend', ingredients=[utils.ingredient('#tfc:sweetener'), not_rotten('firmalife:food/cocoa_butter'), not_rotten('firmalife:food/cocoa_powder')], fluid='1000 #tfc:milks', output_item='2 firmalife:food/milk_chocolate_blend')
    mixing_recipe(rm, 'vanilla_ice_cream', ingredients=[utils.ingredient('#tfc:sweetener'), utils.ingredient('firmalife:spice/vanilla'), utils.ingredient('firmalife:ice_shavings')], fluid='1000 firmalife:cream', output_item='2 firmalife:food/vanilla_ice_cream')
    mixing_recipe(rm, 'chocolate_ice_cream', ingredients=[not_rotten('firmalife:food/vanilla_ice_cream')], fluid='1000 firmalife:chocolate', output_item='firmalife:food/chocolate_ice_cream')
    mixing_recipe(rm, 'strawberry_ice_cream', ingredients=[not_rotten('firmalife:food/vanilla_ice_cream'), not_rotten('tfc:food/strawberry'), not_rotten('tfc:food/strawberry')], output_item='firmalife:food/strawberry_ice_cream')
    mixing_recipe(rm, 'cookie_dough', ingredients=[not_rotten('#firmalife:foods/raw_eggs'), utils.ingredient('firmalife:spice/vanilla'), not_rotten('firmalife:food/butter'), utils.ingredient('#tfc:sweetener'), not_rotten('#tfc:foods/flour')], output_item='4 firmalife:food/cookie_dough')
    mixing_recipe(rm, 'chocolate_chip_cookie_dough', ingredients=[not_rotten('#firmalife:foods/chocolate'), not_rotten('firmalife:food/cookie_dough'), not_rotten('firmalife:food/cookie_dough'), not_rotten('firmalife:food/cookie_dough'), not_rotten('firmalife:food/cookie_dough')], output_item='4 firmalife:food/chocolate_chip_cookie_dough')
    mixing_recipe(rm, 'hardtack_dough', ingredients=[not_rotten('#tfc:foods/flour'), utils.ingredient('tfc:powder/salt')], fluid='1000 minecraft:water', output_item='4 firmalife:food/hardtack_dough')
    mixing_recipe(rm, 'egg_noodles', ingredients=[not_rotten('#firmalife:foods/egg_noodle_flour'), utils.ingredient('tfc:powder/salt'), utils.ingredient('minecraft:egg')], fluid='1000 #tfc:milks', output_item='firmalife:food/raw_egg_noodles')
    mixing_recipe(rm, 'rice_noodles', ingredients=[not_rotten('tfc:food/rice_flour'), not_rotten('tfc:food/maize_flour'), utils.ingredient('tfc:powder/salt')], fluid='1000 #tfc:milks', output_item='2 firmalife:food/raw_rice_noodles')

    pie_mod = {
        'food': {
            'hunger': 4,
            'saturation': 1,
            'water': 0.5,
            'decay_modifier': 4.5,
            'grain': 1.0,
            'dairy': 0.5,
            'fruit': 1.5,
        },
        'portions': [{
            'nutrient_modifier': 0.8,
            'water_modifier': 0.8,
            'saturation_modifier': 0.8,
        }]
    }
    pizza_mod = {
        'food': {
            'hunger': 4,
            'saturation': 1,
            'decay_modifier': 4.5,
            'grain': 1.0,
            'dairy': 0.25,
        },
        'portions': [{
            'nutrient_modifier': 0.8,
            'water_modifier': 0.8,
            'saturation_modifier': 0.8,
        }]
    }
    burrito_mod = {
        'food': {
            'hunger': 4,
            'saturation': 4.0,
            'decay_modifier': 4.5,
        },
        'portions': [{
            'nutrient_modifier': 0.8,
            'water_modifier': 0.8,
            'saturation_modifier': 0.8,
        }]
    }
    pp_mod = {
        'food': {
            'hunger': 4,
            'saturation': 4.0,
            'decay_modifier': 4,
            'fruit': 1.5,
            'grain': 1,
            'water': 4,
        }
    }
    meal_shapeless(rm, 'crafting/filled_pie', (not_rotten('firmalife:food/pie_dough'), '#tfc:foods/preserves', '#firmalife:pie_pans'), 'firmalife:food/filled_pie', pie_mod, other_other_mod='firmalife:add_pie_pan').with_advancement('firmalife:food/pie_dough')
    meal_shapeless(rm, 'crafting/raw_pumpkin_pie', (not_rotten('firmalife:food/pumpkin_pie_dough'), '#firmalife:pie_pans'), 'firmalife:food/raw_pumpkin_pie', pp_mod, other_other_mod='firmalife:add_pie_pan').with_advancement('firmalife:food/pie_dough')
    meal_shapeless(rm, 'crafting/raw_pizza3', (not_rotten('firmalife:food/pizza_dough'), not_rotten('#firmalife:foods/pizza_ingredients'), not_rotten('#firmalife:foods/pizza_ingredients'), not_rotten('firmalife:food/shredded_cheese'), not_rotten('firmalife:food/tomato_sauce')), 'firmalife:food/raw_pizza', pizza_mod).with_advancement('firmalife:food/pizza_dough')
    meal_shapeless(rm, 'crafting/raw_pizza2', (not_rotten('firmalife:food/pizza_dough'), not_rotten('#firmalife:foods/pizza_ingredients'), not_rotten('firmalife:food/shredded_cheese'), not_rotten('firmalife:food/tomato_sauce')), 'firmalife:food/raw_pizza', pizza_mod).with_advancement('firmalife:food/pizza_dough')
    meal_shapeless(rm, 'crafting/raw_pizza', (not_rotten('firmalife:food/pizza_dough'), not_rotten('firmalife:food/shredded_cheese'), not_rotten('firmalife:food/tomato_sauce')), 'firmalife:food/raw_pizza', pizza_mod).with_advancement('firmalife:food/pizza_dough')
    meal_shapeless(rm, 'crafting/burrito', (not_rotten('#firmalife:foods/cooked_meats_and_substitutes'), not_rotten('firmalife:food/shredded_cheese'), not_rotten('firmalife:food/corn_tortilla'), not_rotten('#tfc:foods/vegetables'), not_rotten('firmalife:food/salsa')), 'firmalife:food/burrito', burrito_mod).with_advancement('firmalife:food/corn_tortilla')
    meal_shapeless(rm, 'crafting/taco', (not_rotten('#firmalife:foods/cooked_meats_and_substitutes'), not_rotten('firmalife:food/shredded_cheese'), not_rotten('firmalife:food/taco_shell'), not_rotten('#tfc:foods/vegetables'), not_rotten('firmalife:food/salsa')), 'firmalife:food/taco', burrito_mod).with_advancement('firmalife:food/taco_shell')
    meal_shapeless(rm, 'crafting/maki_roll', (not_rotten('tfc:food/cooked_rice'), not_rotten('tfc:food/dried_seaweed'), not_rotten('#firmalife:foods/raw_fish')), 'firmalife:food/maki_roll', burrito_mod).with_advancement('tfc:food/cooked_rice')
    meal_shapeless(rm, 'crafting/futo_maki_roll', (not_rotten('tfc:food/cooked_rice'), not_rotten('tfc:food/dried_seaweed'), not_rotten('tfc:food/shellfish')), 'firmalife:food/futo_maki_roll', burrito_mod).with_advancement('tfc:food/cooked_rice')
    meal_shapeless(rm, 'crafting/tomato_pasta', (not_rotten('firmalife:food/cooked_pasta'), not_rotten('firmalife:food/tomato_sauce')), 'firmalife:food/pasta_with_tomato_sauce', burrito_mod, other_other_mod='firmalife:copy_bowl').with_advancement('firmalife:food/cooked_pasta')

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
        mapping['Y'] = 'firmalife:reinforced_glass'
        rm.crafting_shaped('crafting/greenhouse/%s_greenhouse_trapdoor' % greenhouse, ['XYX', 'YXY'], mapping, (8, 'firmalife:%s_greenhouse_trapdoor' % greenhouse)).with_advancement(rod)
        rm.crafting_shaped('crafting/greenhouse/%s_greenhouse_panel_roof' % greenhouse, ['Y  ', 'XY ', 'XXY'], mapping, (4, 'firmalife:%s_greenhouse_panel_roof' % greenhouse)).with_advancement(rod)
        rm.crafting_shaped('crafting/greenhouse/%s_greenhouse_panel_wall' % greenhouse, ['XYX', 'XYX', 'XYX'], mapping, (8, 'firmalife:%s_greenhouse_panel_wall' % greenhouse)).with_advancement(rod)
        rm.crafting_shaped('crafting/greenhouse/%s_greenhouse_port' % greenhouse, ['XY'], {'X': 'firmalife:%s_greenhouse_wall' % greenhouse, 'Y': 'firmalife:copper_pipe'}, (8, 'firmalife:%s_greenhouse_port' % greenhouse)).with_advancement(rod)

    sandwich_modifier = {
        'food': {
            'hunger': 4,
            'water': 0.5,
            'saturation': 1,
            'decay_modifier': 4.5
        },
        'portions': [{
            'ingredient': utils.ingredient('#tfc:sandwich_bread'),
            'nutrient_modifier': 0.5,
            'saturation_modifier': 0.5,
            'water_modifier': 0.5,
        }, {
            'nutrient_modifier': 0.8,
            'water_modifier': 0.8,
            'saturation_modifier': 0.8,
        }]
    }
    # Grain Stuff
    for grain in TFC_GRAINS:
        damage_shapeless(rm, 'crafting/%s_slice' % grain, ('tfc:food/%s_bread' % grain, '#tfc:knives'), '2 firmalife:food/%s_slice' % grain).with_advancement('tfc:food/%s_bread' % grain)

        rm.crafting_shapeless('crafting/%s_dough' % grain, (not_rotten('tfc:food/%s_flour' % grain), fluid_item_ingredient('100 firmalife:yeast_starter'), '#tfc:sweetener'), (4, 'firmalife:food/%s_dough' % grain)).with_advancement('tfc:food/%s_grain' % grain)

        oven_recipe(rm, grain + '_bread', not_rotten('firmalife:food/%s_dough' % grain), 200, result_item=item_stack_provider('tfc:food/%s_bread' % grain))
        heat_recipe(rm, 'toast', not_rotten('#firmalife:foods/slices'), 200, result_item=item_stack_provider('firmalife:food/toast'))
        rm.domain = 'tfc'  # DOMAIN CHANGE
        heat_recipe(rm, grain + '_dough', not_rotten('tfc:food/%s_dough' % grain), 200, result_item=item_stack_provider('firmalife:food/%s_flatbread' % grain, copy_food=True))
        rm.domain = 'firmalife'  # DOMAIN RESET

        for sandwich_bread in ('slice', 'flatbread'):
            f_item = 'firmalife:food/%s_%s' % (grain, sandwich_bread)
            sandwich_pattern = ['ZX ', 'YYY', ' X ']
            sandwich_ingredients = {'X': not_rotten(f_item), 'Y': not_rotten('#tfc:foods/usable_in_sandwich'), 'Z': '#tfc:knives'}
            jam_sandwich_pattern = ['ZX ', 'JYY', ' X ']
            jam_sandwich_ingredients = {'X': not_rotten(f_item), 'Y': not_rotten('#tfc:foods/usable_in_jam_sandwich'), 'Z': '#tfc:knives', 'J': '#tfc:foods/preserves'}
            delegate_recipe(rm, 'crafting/%s_sandwich_%s' % (grain, sandwich_bread), 'tfc:damage_inputs_shaped_crafting', {
                'type': 'tfc:advanced_shaped_crafting',
                'pattern': sandwich_pattern,
                'key': utils.item_stack_dict(sandwich_ingredients, ''.join(sandwich_pattern)[0]),
                'result': item_stack_provider('2 tfc:food/%s_bread_sandwich' % grain, meal=sandwich_modifier),
                'input_row': 0,
                'input_column': 0,
            }).with_advancement(f_item)
            delegate_recipe(rm, 'crafting/%s_sandwich_%s_with_jam' % (grain, sandwich_bread), 'tfc:damage_inputs_shaped_crafting', {
                'type': 'tfc:advanced_shaped_crafting',
                'pattern': jam_sandwich_pattern,
                'key': utils.item_stack_dict(jam_sandwich_ingredients, ''.join(jam_sandwich_pattern)[0]),
                'result': item_stack_provider('2 tfc:food/%s_bread_jam_sandwich' % grain, meal=sandwich_modifier),
                'input_row': 0,
                'input_column': 0,
            }).with_advancement(f_item)

    heat_recipe(rm, 'corn_tortilla', not_rotten('firmalife:food/masa'), 200, result_item=item_stack_provider('firmalife:food/corn_tortilla', copy_food=True))
    heat_recipe(rm, 'bacon', not_rotten('firmalife:food/bacon'), 200, result_item=item_stack_provider('firmalife:food/cooked_bacon', copy_food=True))
    heat_recipe(rm, 'copper_pipe', 'firmalife:copper_pipe', 1080, result_item=None, result_fluid='25 tfc:metal/copper')
    heat_recipe(rm, 'oxidized_copper_pipe', 'firmalife:oxidized_copper_pipe', 1080, result_item=None, result_fluid='25 tfc:metal/copper')
    heat_recipe(rm, 'stainless_steel_jar_lid', 'firmalife:stainless_steel_jar_lid', 1540, result_item=None, result_fluid='6 firmalife:metal/stainless_steel')

    ore = 'chromite'
    for rock, data in TFC_ROCKS.items():
        cobble = 'tfc:rock/cobble/%s' % rock
        collapse_recipe(rm, '%s_cobble' % rock, [
            'firmalife:ore/poor_%s/%s' % (ore, rock),
            'firmalife:ore/normal_%s/%s' % (ore, rock),
            'firmalife:ore/rich_%s/%s' % (ore, rock)
        ], cobble)
        for grade in ORE_GRADES.keys():
            rm.block_tag('tfc:can_start_collapse', 'firmalife:ore/%s_%s/%s' % (grade, ore, rock))
            rm.block_tag('tfc:can_collapse', 'firmalife:ore/%s_%s/%s' % (grade, ore, rock))

    alloy_recipe(rm, 'stainless_steel', 'stainless_steel', ('firmalife:chromium', 0.2, 0.3), ('tfc:nickel', 0.1, 0.2), ('tfc:steel', 0.6, 0.8))
    anvil_recipe(rm, 'pie_pan', '#forge:sheets/cast_iron', '4 firmalife:pie_pan', 1, Rules.hit_last, Rules.hit_second_last, Rules.draw_third_last)
    anvil_recipe(rm, 'sprinkler', '#forge:sheets/copper', 'firmalife:sprinkler', 1, Rules.hit_last, Rules.hit_second_last, Rules.punch_third_last)
    anvil_recipe(rm, 'copper_pipe', '#forge:sheets/copper', '8 firmalife:copper_pipe', 1, Rules.draw_last, Rules.bend_not_last)
    anvil_recipe(rm, 'stainless_steel_jar_lid', '#forge:ingots/stainless_steel', '16 firmalife:stainless_steel_jar_lid',4, Rules.hit_last, Rules.hit_second_last, Rules.punch_third_last)

    glass_recipe(rm, 'reinforced_glass_pane', ['flatten', 'soda_ash', 'table_pour'], 'tfc:silica_glass_batch', 'firmalife:reinforced_poured_glass')
    for glass in ('olivine', 'hematitic', 'volcanic'):
        glass_recipe(rm, '%s_wine_bottle' % glass, ['blow', 'blow', 'pinch', 'saw'], 'tfc:%s_glass_batch' % glass, 'firmalife:empty_%s_wine_bottle' % glass)
    glass_recipe(rm, 'wine_glass', ['blow', 'blow', 'pinch', 'saw'], 'tfc:silica_glass_batch', '2 firmalife:wine_glass')

    for recipe in DISABLED_TFC_RECIPES:
        rm.domain = 'tfc' # DOMAIN CHANGE
        disable_recipe(rm, recipe)
        rm.domain = 'firmalife' # DOMAIN RESET

def disable_recipe(rm: ResourceManager, name_parts: ResourceIdentifier):
    # noinspection PyTypeChecker
    rm.recipe(name_parts, None, {}, conditions='forge:false')

def glass_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, steps: List[str], batch: str, result: str):
    rm.recipe(('glassworking', name_parts), 'tfc:glassworking', {
        'operations': steps,
        'batch': utils.ingredient(batch),
        'result': utils.item_stack(result)
    })

def write_crafting_recipe(rm: ResourceManager, name_parts: ResourceIdentifier, data: Json) -> RecipeContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'data', res.domain, 'recipes', 'crafting', res.path), data)
    return RecipeContext(rm, res)

def meal_shapeless(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredients: Json, result: str, meal_mod: Dict[str, Any], other_other_mod: str = None) -> RecipeContext:
    return advanced_shapeless(rm, name_parts, ingredients, item_stack_provider(result, other_modifier={'type': 'tfc:meal', **meal_mod}, other_other_modifier=other_other_mod), primary_ingredient=utils.ingredient(ingredients[0]))

def advanced_shapeless(rm: ResourceManager, name_parts: ResourceIdentifier, ingredients: Json, result: Json, primary_ingredient: Json = None, group: str = None, conditions: Optional[Json] = None) -> RecipeContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'data', res.domain, 'recipes', res.path), {
        'type': 'tfc:advanced_shapeless_crafting',
        'group': group,
        'ingredients': utils.item_stack_list(ingredients),
        'result': result,
        'primary_ingredient': None if primary_ingredient is None else utils.ingredient(primary_ingredient),
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

def make_jar(rm: ResourceManager, jar: str, remainder: int = 1, ing: str = None, sealed: bool = False):
    jar_name = 'tfc:empty_jar'
    if ing is not None:
        if remainder == 8:
            rm.crafting_shaped('crafting/%s_jar' % jar, ['XXX', 'XYX', 'XXX'], {'X': ing, 'Y': jar_name}, 'firmalife:jar/%s' % jar).with_advancement('tfc:empty_jar')
        elif remainder == 1:
            rm.crafting_shapeless('crafting/%s_jar' % jar, (jar_name, ing), 'firmalife:jar/%s' % jar).with_advancement('tfc:empty_jar')
        rm.crafting_shapeless('crafting/%s_jar_open' % jar, ('firmalife:jar/%s' % jar), (remainder, ing))

def fluid_item_ingredient(fluid: Json, delegate: Json = None):
    return {
        'type': 'tfc:fluid_item',
        'ingredient': delegate,
        'fluid_ingredient': fluid_stack_ingredient(fluid)
    }

def no_remainder_shapeless(rm: ResourceManager, name_parts: ResourceIdentifier, ingredients: Json, result: Json, group: str = None, conditions: utils.Json = None) -> RecipeContext:
    return delegate_recipe(rm, name_parts, 'tfc:no_remainder_shapeless_crafting', {
        'type': 'minecraft:crafting_shapeless',
        'group': group,
        'ingredients': utils.item_stack_list(ingredients),
        'result': utils.item_stack(result),
        'conditions': utils.recipe_condition(conditions)
    })

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

def damage_shaped(rm: ResourceManager, name_parts: utils.ResourceIdentifier, pattern: Sequence[str], ingredients: Json, result: Json, group: str = None, conditions: Optional[Json] = None) -> RecipeContext:
    return delegate_recipe(rm, name_parts, 'tfc:damage_inputs_shaped_crafting', {
        'type': 'minecraft:crafting_shaped',
        'group': group,
        'pattern': pattern,
        'key': utils.item_stack_dict(ingredients, ''.join(pattern)[0]),
        'result': utils.item_stack(result),
        'conditions': utils.recipe_condition(conditions)
    })

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

def stomping_recipe(rm: ResourceManager, name: utils.ResourceIdentifier, item: Any, result: Json, input_texture: str, output_texture: str, sound: str = 'minecraft:entity.slime.squish') -> RecipeContext:
    return rm.recipe(('stomping', name), 'firmalife:stomping', {
        'ingredient': utils.ingredient(item) if isinstance(item, str) else item,
        'result': result,
        'input_texture': input_texture,
        'output_texture': output_texture,
        'sound': sound
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
        'ingredient': utils.ingredient(ingredient),
    }

def lacks_trait(ingredient: Json, trait: str) -> Json:
    return has_trait(ingredient, trait, True)

def not_rotten(ingredient: Json) -> Json:
    return {
        'type': 'tfc:not_rotten',
        'ingredient': utils.ingredient(ingredient)
    }

def item_stack_provider(data_in: Json = None, copy_input: bool = False, copy_heat: bool = False, copy_food: bool = False, copy_oldest_food: bool = False, reset_food: bool = False, add_heat: float = None, add_trait: str = None, remove_trait: str = None, empty_bowl: bool = False, copy_forging: bool = False, other_modifier: str | Json = None, other_other_modifier: str = None, meal: Json = None) -> Json:
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
        ({'type': 'tfc:remove_trait', 'trait': remove_trait}, remove_trait is not None),
        ({'type': 'tfc:meal', **(meal if meal is not None else {})}, meal is not None)
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

def simple_pot_recipe_5(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: Json, fluid: str, output_fluid: str = None, output_items: Json = None, duration: int = 2000, temp: int = 300, serializer: str = 'tfc:pot', data: Dict[str, Any] = None):
    for i in range(1, 6):
        simple_pot_recipe(rm, name_parts + '_' + str(i), [*[ingredient] * i], fluid, output_fluid, [*[output_items] * i], duration, temp, serializer, data)

def bowl_recipe_5(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: Json, fluid: str, output_fluid: str = None, output_items: str = None, duration: int = 2000, temp: int = 300, data: Dict[str, Any] = None):
    for i in range(1, 6):
        simple_pot_recipe(rm, name_parts + '_' + str(i), [*[ingredient] * i], fluid, output_fluid, '%s %s' % (i, output_items), duration, temp, 'firmalife:bowl_pot', data)

def simple_pot_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredients: Json, fluid: str, output_fluid: str = None, output_items: Json = None, duration: int = 2000, temp: int = 300, serializer: str = 'tfc:pot', data: Dict[str, Any] = None):
    dat = {
        'ingredients': ingredients,
        'fluid_ingredient': fluid_stack_ingredient(fluid),
        'duration': duration,
        'temperature': temp,
        'fluid_output': fluid_stack(output_fluid) if output_fluid is not None else None,
        'item_output': utils.item_stack(output_items) if isinstance(output_items, str) else [utils.item_stack(item) for item in output_items] if output_items is not None else None,
    }
    if data is not None:
        dat = {**dat, **data}
    rm.recipe(('pot', name_parts), serializer, dat)

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

def casting_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, mold: str, metal: str, amount: int, break_chance: float, item: str = None):
    rm.recipe(('casting', name_parts), 'tfc:casting', {
        'mold': {'item': 'tfc:ceramic/%s_mold' % mold},
        'fluid': fluid_stack_ingredient('%d firmalife:metal/%s' % (amount, metal)),
        'result': utils.item_stack('firmalife:metal/%s/%s' % (mold, metal)) if item is None else utils.item_stack(item),
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

def vat_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, input_item: Optional[Json] = None, input_fluid: Optional[Json] = None, output_item: Optional[Json] = None, output_fluid: Optional[Json] = None, length: int = None, temp: float = None, jar: str = None, output_texture: str = None):
    rm.recipe(('vat', name_parts), 'firmalife:vat', {
        'input_item': item_stack_ingredient(input_item) if input_item is not None else None,
        'input_fluid': fluid_stack_ingredient(input_fluid) if input_fluid is not None else None,
        'output_item': item_stack_provider(output_item) if output_item is not None else None,
        'output_fluid': fluid_stack(output_fluid) if output_fluid is not None else None,
        'length': length,
        'temperature': temp,
        'jar': utils.item_stack(jar) if jar is not None else None,
        'output_texture': output_texture,
    })

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

def delegate_recipe(rm: ResourceManager, name_parts: ResourceIdentifier, recipe_type: str, delegate: Json, data: Json = {}) -> RecipeContext:
    return write_crafting_recipe(rm, name_parts, {
        'type': recipe_type,
        **data,
        'recipe': delegate,
    })

def pumpkin_knapping(rm: ResourceManager, name_parts: ResourceIdentifier, pattern: List[str], result: Json, outside_slot_required: bool = None):
    knapping_recipe(rm, name_parts, 'firmalife:pumpkin', pattern, result, None, outside_slot_required)

def clay_knapping(rm: ResourceManager, name_parts: ResourceIdentifier, pattern: List[str], result: Json, outside_slot_required: bool = None):
    stack = utils.item_stack(result)
    if ('count' in stack and stack['count'] == 1) or 'count' not in stack:
        rm.item_tag('clay_recycle_5', stack['item'])
    else:
        rm.item_tag('clay_recycle_1', stack['item'])
    knapping_recipe(rm, name_parts, 'tfc:clay', pattern, result, None, outside_slot_required)


def knapping_recipe(rm: ResourceManager, name_parts: ResourceIdentifier, knap_type: str, pattern: List[str], result: Json, ingredient: Json, outside_slot_required: bool):
    for part in pattern:
        assert 0 < len(part) < 6, 'Incorrect length: %s' % part
    rm.recipe((knap_type.split(':')[1] + '_knapping', name_parts), 'tfc:knapping', {
        'knapping_type': knap_type,
        'outside_slot_required': outside_slot_required,
        'pattern': pattern,
        'ingredient': None if ingredient is None else utils.ingredient(ingredient),
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

def quern_recipe(rm: ResourceManager, name: ResourceIdentifier, item: str, result: str, count: int = 1) -> RecipeContext:
    result = result if not isinstance(result, str) else utils.item_stack((count, result))
    return rm.recipe(('quern', name), 'tfc:quern', {
        'ingredient': utils.ingredient(item),
        'result': result
    })

def loom_recipe(rm: ResourceManager, name: utils.ResourceIdentifier, ingredient: Json, result: Json, steps: int, in_progress_texture: str):
    return rm.recipe(('loom', name), 'tfc:loom', {
        'ingredient': item_stack_ingredient(ingredient),
        'result': utils.item_stack(result),
        'steps_required': steps,
        'in_progress_texture': in_progress_texture
    })


def knapping_type(rm: ResourceManager, name_parts: ResourceIdentifier, item_input: Json, amount_to_consume: Optional[int], click_sound: str, consume_after_complete: bool, use_disabled_texture: bool, spawns_particles: bool, jei_icon_item: Json):
    rm.data(('tfc', 'knapping_types', name_parts), {
        'input': item_stack_ingredient(item_input),
        'amount_to_consume': amount_to_consume,
        'click_sound': click_sound,
        'consume_after_complete': consume_after_complete,
        'use_disabled_texture': use_disabled_texture,
        'spawns_particles': spawns_particles,
        'jei_icon_item': utils.item_stack(jei_icon_item)
    })
