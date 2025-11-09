from mcresources import ResourceManager, utils

from constants import *


def generate(rm: ResourceManager):
    ### TAGS ###

    # block_and_item_tag(rm, 'tfc:wild_fruits', 'firmalife:plant/pineapple_bush', 'firmalife:plant/nightshade_bush', 'firmalife:plant/fig_sapling', 'firmalife:plant/cocoa_sapling')

    rm.entity_tag('drops_rennet', 'tfc:goat', 'tfc:yak')
    rm.entity_tag('drops_three_rennet', 'tfc:cow', 'tfc:sheep', 'tfc:musk_ox')

    rm.fluid_tag('tfc:alcohols', 'firmalife:pina_colada', 'firmalife:mead', '#firmalife:wine')
    rm.fluid_tag('tfc:milks', 'firmalife:yak_milk', 'firmalife:goat_milk', 'firmalife:coconut_milk')
    rm.fluid_tag('tfc:drinkables', 'firmalife:chocolate')
    rm.fluid_tag('tfc:ingredients', *['firmalife:%s' % fluid for fluid in EXTRA_FLUIDS])
    rm.fluid_tag('wine', *['firmalife:%s_wine' % r for r in WINES])
    rm.fluid_tag('usable_in_mixing_bowl', '#tfc:usable_in_pot')
    rm.fluid_tag('usable_in_hollow_shell', '#tfc:usable_in_wooden_bucket')
    rm.fluid_tag('usable_in_wine_glass', '#tfc:drinkables')
    rm.fluid_tag('usable_in_vat', '#tfc:usable_in_pot')
    rm.fluid_tag('oils', 'firmalife:soybean_oil', 'tfc:olive_oil')


def block_and_item_tag(rm: ResourceManager, name_parts: utils.ResourceIdentifier, *values: utils.ResourceIdentifier, replace: bool = False):
    rm.block_tag(name_parts, *values, replace=replace)
    rm.item_tag(name_parts, *values, replace=replace)
