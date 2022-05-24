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

    greenhouse(rm, 'iron', '#firmalife:all_iron_greenhouse')
    greenhouse(rm, 'copper', '#firmalife:all_copper_greenhouse')
    greenhouse(rm, 'treated_wood', '#firmalife:all_treated_wood_greenhouse')
    greenhouse(rm, 'stainless_steel', '#firmalife:stainless_steel_greenhouse')


def greenhouse(rm: ResourceManager, name: str, block_ingredient: str):
    rm.data(('tfc', 'greenhouse', name), {
        'ingredient': utils.ingredient(block_ingredient)
    })
