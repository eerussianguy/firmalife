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
    rm.crafting_shapeless('crafting/empty_jar', ('minecraft:glass', '#tfc:lumber'), (4, 'firmalife:empty_jar')).with_advancement('minecraft:glass')

    for jar, remainder, tex, ing in JARS:
        if ing is not None:
            if remainder == 8:
                rm.crafting_shaped('crafting/%s_jar' % jar, ['XXX', 'XYX', 'XXX'], {'X': ing, 'Y': 'firmalife:empty_jar'}, 'firmalife:%s_jar' % jar).with_advancement(ing)
            rm.crafting_shapeless('crafting/%s_jar_open' % jar, ('firmalife:%s_jar' % jar), (remainder, ing))


    # Firmalife
    drying_recipe(rm, 'drying_fruit', intersect(utils.ingredient('#tfc:foods/fruits'), not_ingredient(has_trait(None, 'firmalife:dried'))), item_stack_provider(copy_input=True, add_trait='firmalife:dried'))
    drying_recipe(rm, 'cinnamon', 'firmalife:cinnamon_bark', item_stack_provider('firmalife:spice/cinnamon'))

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

def drying_recipe(rm: ResourceManager, name: utils.ResourceIdentifier, item: Any, result: Json) -> RecipeContext:
    return rm.recipe(('drying', name), 'firmalife:drying', {
        'ingredient': utils.ingredient(item) if isinstance(item, str) else item,
        'result': result
    })

def intersect(ingredient1: Json, ingredient2: Json):
    return {
        'type': 'forge:intersection',
        'children': [ingredient1, ingredient2]
    }

def has_trait(ingredient: Json, trait: str) -> Json:
    return {
        'type': 'tfc:has_trait',
        'trait': trait,
        'ingredient': utils.ingredient(ingredient) if ingredient is not None else None
    }

def not_ingredient(data_in: Json):
    return {'type': 'tfc:not', 'ingredient': data_in}

def item_stack_provider(data_in: Json = None, copy_input: bool = False, copy_heat: bool = False, copy_food: bool = False, reset_food: bool = False, add_heat: float = None, add_trait: str = None, remove_trait: str = None, empty_bowl: bool = False) -> Json:
    if isinstance(data_in, dict):
        return data_in
    stack = utils.item_stack(data_in) if data_in is not None else None
    modifiers = [k for k, v in (
        ('tfc:copy_input', copy_input),
        ('tfc:copy_heat', copy_heat),
        ('tfc:copy_food', copy_food),
        ('tfc:reset_food', reset_food),
        ('tfc:empty_bowl', empty_bowl),
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
        'fluid': fluid_stack_ingredient('%d tfc:metal/%s' % (amount, metal)),
        'result': utils.item_stack('tfc:metal/%s/%s' % (mold, metal)),
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




