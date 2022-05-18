from mcresources import ResourceManager

from constants import *
from mcresources import utils


def generate(rm: ResourceManager):
    rm.item_tag('usable_on_oven', 'firmalife:peel')
    rm.block_tag('oven_insulation', 'minecraft:bricks', '#tfc:forge_insulation', '#firmalife:oven_blocks')
    rm.block_tag('minecraft:mineable/pickaxe', '#firmalife:oven_blocks')
    rm.block_tag('all_iron_greenhouse', '#firmalife:iron_greenhouse', '#firmalife:rusted_iron_greenhouse')
    rm.block_tag('all_copper_greenhouse', *['#firmalife:%s_greenhouse' % greenhouse for greenhouse in ('exposed_copper', 'weathered_copper', 'copper', 'oxidized_copper')])
    rm.block_tag('all_treated_wood_greenhouse', '#firmalife:treated_wood_greenhouse', '#firmalife:weathered_treated_wood_greenhouse')
    rm.block_tag('greenhouse', '#firmalife:all_iron_greenhouse', '#firmalife:all_copper_greenhouse', '#firmalife:all_treated_wood_greenhouse', '#firmalife:stainless_steel_greenhouse')
