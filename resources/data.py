from mcresources import ResourceManager

from constants import *
from mcresources import utils


def generate(rm: ResourceManager):
    rm.item_tag('usable_on_oven', 'firmalife:peel')
    rm.block_tag('oven_insulation', 'minecraft:bricks', '#tfc:forge_insulation', '#firmalife:oven_blocks')
    rm.block_tag('minecraft:mineable/pickaxe', '#firmalife:oven_blocks')
    rm.block_tag('all_iron_greenhouse', '#firmalife:iron_greenhouse', '#firmalife:rusted_iron_greenhouse')
    rm.block_tag('all_copper_greenhouse', *['#firmalife:%s_greenhouse' % g for g in ('exposed_copper', 'weathered_copper', 'copper', 'oxidized_copper')])
    rm.block_tag('all_treated_wood_greenhouse', '#firmalife:treated_wood_greenhouse', '#firmalife:weathered_treated_wood_greenhouse')
    rm.block_tag('greenhouse', '#firmalife:all_iron_greenhouse', '#firmalife:all_copper_greenhouse', '#firmalife:all_treated_wood_greenhouse', '#firmalife:stainless_steel_greenhouse')

    greenhouse(rm, 'treated_wood', '#firmalife:all_treated_wood_greenhouse', 5)
    greenhouse(rm, 'copper', '#firmalife:all_copper_greenhouse', 10)
    greenhouse(rm, 'iron', '#firmalife:all_iron_greenhouse', 15)
    greenhouse(rm, 'stainless_steel', '#firmalife:stainless_steel_greenhouse', 20)

    for grain in ('barley', 'oat', 'rye', 'wheat', 'rice'):
        simple_plantable(rm, grain, 'nitrogen' if grain == 'barley' else 'phosphorous', 7, True)

    simple_plantable(rm, 'beet', 'potassium', 5)
    simple_plantable(rm, 'cabbage', 'nitrogen', 5)
    simple_plantable(rm, 'carrot', 'potassium', 4)
    simple_plantable(rm, 'garlic', 'nitrogen', 4)
    simple_plantable(rm, 'potato', 'potassium', 6)
    simple_plantable(rm, 'onion', 'nitrogen', 6)
    simple_plantable(rm, 'soybean', 'nitrogen', 6)

    #todo: large planting for green bean, sugarcane, tomato, jute, maize


def greenhouse(rm: ResourceManager, name: str, block_ingredient: str, tier: int):
    rm.data(('tfc', 'greenhouse', name), {
        'ingredient': utils.ingredient(block_ingredient),
        'tier': tier
    })

def simple_plantable(rm: ResourceManager, name: str, nutrient: str, stages: int, large: bool = False):
    plantable(rm, name, 'tfc:seeds/%s' % name, 'tfc:food/%s' % name, nutrient, 'tfc:block/crop/%s' % name, stages, large)

def plantable(rm: ResourceManager, name: str, seed: str, crop: str, nutrient: str, texture: str, stages: int, large: bool = False):
    rm.data(('tfc', 'plantable', name), {
        'ingredient': utils.ingredient(seed),
        'seed': utils.item_stack(seed),
        'crop': utils.item_stack(crop),
        'nutrient': nutrient,
        'large': large,
        'stages': stages,
        'texture': texture
    })
