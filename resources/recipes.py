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
    knapping_type(rm, 'pumpkin', {'ingredient': not_rotten('#firmalife:pumpkin_knapping'), 'count': 1}, None, 'tfc:item.knapping.leather', False, False, False, 'tfc:pumpkin')

    ore = 'chromite'
    for rock, data in TFC_ROCKS.items():
        for grade in ORE_GRADES.keys():
            rm.block_tag('tfc:can_start_collapse', 'firmalife:ore/%s_%s/%s' % (grade, ore, rock))
            rm.block_tag('tfc:can_collapse', 'firmalife:ore/%s_%s/%s' % (grade, ore, rock))


def not_rotten(ingredient: Json) -> Json:
    return {
        'type': 'tfc:not_rotten',
        'ingredient': utils.ingredient(ingredient)
    }

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
