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


    for jar, remainder, ing in JARS:
        make_jar(rm, jar, remainder, ing)
    for fruit in FL_FRUITS:
        rm.crafting_shapeless('crafting/unseal_%s_jar' % fruit, (not_rotten('firmalife:jar/%s' % fruit), ), 'firmalife:jar/%s_unsealed' % fruit).with_advancement('firmalife:jar/%s' % fruit)

    # Firmalife Recipes
    knapping_type(rm, 'pumpkin', {'ingredient': not_rotten('#firmalife:pumpkin_knapping'), 'count': 1}, None, 'tfc:item.knapping.leather', False, False, False, 'tfc:pumpkin')



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


    ore = 'chromite'
    for rock, data in TFC_ROCKS.items():
        for grade in ORE_GRADES.keys():
            rm.block_tag('tfc:can_start_collapse', 'firmalife:ore/%s_%s/%s' % (grade, ore, rock))
            rm.block_tag('tfc:can_collapse', 'firmalife:ore/%s_%s/%s' % (grade, ore, rock))


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
