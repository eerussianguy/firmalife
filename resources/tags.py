from mcresources import ResourceManager, utils

from constants import *


def generate(rm: ResourceManager):
    ### TAGS ###

    rm.entity_tag('drops_rennet', 'tfc:goat', 'tfc:yak')
    rm.entity_tag('drops_three_rennet', 'tfc:cow', 'tfc:sheep', 'tfc:musk_ox')


def block_and_item_tag(rm: ResourceManager, name_parts: utils.ResourceIdentifier, *values: utils.ResourceIdentifier, replace: bool = False):
    rm.block_tag(name_parts, *values, replace=replace)
    rm.item_tag(name_parts, *values, replace=replace)
