from typing import NamedTuple, Dict, Optional, Set

from mcresources import ResourceManager, utils

from constants import lang
from data import item_heat
from assets import slab_loot

class Metal(NamedTuple):
    tier: int
    types: Set[str]
    heat_capacity_base: float  # Do not access directly, use one of specific or ingot heat capacity.
    melt_temperature: float
    melt_metal: Optional[str]

    def specific_heat_capacity(self) -> float: return round(300 / self.heat_capacity_base) / 100_000
    def ingot_heat_capacity(self) -> float: return 1 / self.heat_capacity_base


class MetalItem(NamedTuple):
    type: str
    smelt_amount: int
    parent_model: str
    tag: Optional[str]
    mold: bool
    durability: bool

FL_METALS: Dict[str, Metal] = {
    'stainless_steel': Metal(4, {'part'}, 0.35, 1540, None),
    'chromium': Metal(4, {'part'}, 0.35, 1250, None),
}

METAL_ITEMS: Dict[str, MetalItem] = {
    'ingot': MetalItem('all', 100, 'item/generated', 'forge:ingots', True, False),
    'double_ingot': MetalItem('part', 200, 'item/generated', 'forge:double_ingots', False, False),
    'sheet': MetalItem('part', 200, 'item/generated', 'forge:sheets', False, False),
    'double_sheet': MetalItem('part', 400, 'item/generated', 'forge:double_sheets', False, False),
    'rod': MetalItem('part', 50, 'item/handheld_rod', 'forge:rods', False, False),
}

METAL_BLOCKS: Dict[str, MetalItem] = {
    'block': MetalItem('part', 100, 'block/block', None, False, False),
    'block_slab': MetalItem('part', 50, 'block/block', None, False, False),
    'block_stairs': MetalItem('part', 75, 'block/block', None, False, False)
}

METAL_ITEMS_AND_BLOCKS = {**METAL_ITEMS, **METAL_BLOCKS}

def generate(rm: ResourceManager):

    chromium_ore_heats(rm)
    for metal, metal_data in FL_METALS.items():
        rm.data(('tfc', 'metals', metal), {
            'tier': metal_data.tier,
            'fluid': 'firmalife:metal/%s' % metal,
            'melt_temperature': metal_data.melt_temperature,
            'specific_heat_capacity': metal_data.specific_heat_capacity(),
            'ingots': utils.ingredient('#forge:ingots/%s' % metal),
            'double_ingots': utils.ingredient('#forge:double_ingots/%s' % metal),
            'sheets': utils.ingredient('#forge:sheets/%s' % metal)
        })

        for item, item_data in METAL_ITEMS_AND_BLOCKS.items():
            if item_data.type in metal_data.types or item_data.type == 'all':
                item_name = 'firmalife:metal/block/%s_%s' % (metal, item.replace('block_', '')) if 'block_' in item else 'firmalife:metal/%s/%s' % (item, metal)
                item_heat(rm, ('metal', metal + '_' + item), item_name, metal_data.ingot_heat_capacity(), metal_data.melt_temperature, mb=item_data.smelt_amount)

        def item(_variant: str) -> str:
            return 'firmalife:metal/%s/%s' % (_variant, metal)

        # Metal Items
        for metal_item, metal_item_data in METAL_ITEMS.items():
            if metal_item_data.type in metal_data.types or metal_item_data.type == 'all':
                texture = 'firmalife:item/metal/%s/%s' % (metal_item, metal)
                the_item = rm.item_model(('metal', '%s' % metal_item, '%s' % metal), texture, parent=metal_item_data.parent_model)
                the_item.with_lang(lang('%s %s', metal, metal_item))

        rm.blockstate(('fluid', 'metal', metal)).with_block_model({'particle': 'block/lava_still'}, parent=None).with_lang(lang('Molten %s', metal))
        rm.lang('fluid.firmalife.metal.%s' % metal, lang('Molten %s', metal))
        rm.fluid_tag(metal, 'firmalife:metal/%s' % metal, 'firmalife:metal/flowing_%s' % metal)
        rm.fluid_tag('tfc:molten_metals', *['firmalife:metal/%s' % metal])

        item = rm.custom_item_model(('bucket', 'metal', metal), 'forge:fluid_container', {
            'parent': 'forge:item/bucket',
            'fluid': 'firmalife:metal/%s' % metal
        })
        item.with_lang(lang('molten %s bucket', metal))
        rm.lang('metal.firmalife.%s' % metal, lang(metal))

        for metal_block in METAL_BLOCKS:
            if metal_block == 'block' or metal_block == 'block_stairs' or metal_block == 'block_slab':
                block = rm.blockstate(('metal', 'block', metal)).with_block_model().with_lang(lang('%s plated block', metal)).with_item_model().with_block_loot('firmalife:metal/block/%s' % metal)
                block.make_slab()
                rm.block(('metal', 'block', '%s_slab' % metal)).with_lang(lang('%s plated slab', metal))
                rm.block(('metal', 'block', '%s_stairs' % metal)).with_lang(lang('%s plated stairs', metal)).with_block_loot('firmalife:metal/block/%s_stairs' % metal)
                block.make_stairs()
                slab_loot(rm, 'firmalife:metal/block/%s_slab' % metal)

def chromium_ore_heats(rm: ResourceManager):
    ore = 'chromite'
    metal_data = FL_METALS['chromium']
    item_heat(rm, ('ore', ore), ['firmalife:ore/small_%s' % ore, 'firmalife:ore/normal_%s' % ore, 'firmalife:ore/poor_%s' % ore, 'firmalife:ore/rich_%s' % ore], metal_data.ingot_heat_capacity(), int(metal_data.melt_temperature), mb=40)
