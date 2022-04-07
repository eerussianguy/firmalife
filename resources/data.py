from mcresources import ResourceManager

from constants import *
from mcresources import utils


def generate(rm: ResourceManager):
    rm.item_tag('usable_on_oven', 'firmalife:peel')
    rm.block_tag('oven_insulation', 'minecraft:bricks', '#tfc:forge_insulation')
    rm.block_tag('minecraft:mineable/pickaxe', '#firmalife:oven_blocks')
