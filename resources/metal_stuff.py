from typing import NamedTuple, Dict, Optional, Set

from mcresources import ResourceManager, utils

from constants import lang
from data import item_heat
from assets import slab_loot
from recipes import anvil_recipe, Rules, welding_recipe, heat_recipe, casting_recipe

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
                if item_data.tag is not None:
                    rm.item_tag(item_data.tag, '#%s/%s' % (item_data.tag, metal))
                    rm.item_tag(item_data.tag + '/' + metal, item_name)

                rm.item_tag('tfc:metal_item/%s' % metal, item_name)
                item_heat(rm, ('metal', metal + '_' + item), item_name, metal_data.ingot_heat_capacity(), metal_data.melt_temperature, mb=item_data.smelt_amount)

        if 'part' in metal_data.types:
            rm.block_tag('minecraft:stairs', 'tfc:metal/block/%s_stairs' % metal)
            rm.block_tag('minecraft:slabs', 'tfc:metal/block/%s_slab' % metal)

        def item(_variant: str) -> str:
            return 'firmalife:metal/%s/%s' % (_variant, metal)

        # Metal Items
        for metal_item, metal_item_data in METAL_ITEMS.items():
            if metal_item_data.type in metal_data.types or metal_item_data.type == 'all':
                texture = 'firmalife:item/metal/%s/%s' % (metal_item, metal)
                the_item = rm.item_model(('metal', '%s' % metal_item, '%s' % metal), texture, parent=metal_item_data.parent_model)
                the_item.with_lang(lang('%s %s', metal, metal_item))

        anvil_recipe(rm, '%s_sheet' % metal, item('double_ingot'), item('sheet'), metal_data.tier, Rules.hit_last, Rules.hit_second_last, Rules.hit_third_last)
        anvil_recipe(rm, '%s_rod' % metal, item('ingot'), '2 firmalife:metal/rod/%s' % metal, metal_data.tier, Rules.bend_last, Rules.draw_second_last, Rules.draw_third_last)
        welding_recipe(rm, '%s_double_ingot' % metal, item('ingot'), item('ingot'), item('double_ingot'), metal_data.tier - 1)
        welding_recipe(rm, '%s_double_sheet' % metal, item('sheet'), item('sheet'), item('double_sheet'), metal_data.tier - 1)

        for item, item_data in METAL_ITEMS.items():
            if item_data.type == 'all' or item_data.type in metal_data.types:
                heat_recipe(rm, ('metal', '%s_%s' % (metal, item)), 'firmalife:metal/%s/%s' % (item, metal), metal_data.melt_temperature, None, '%d firmalife:metal/%s' % (item_data.smelt_amount, metal))
        for item, item_data in METAL_ITEMS.items():
            if item == 'ingot' or (item_data.mold and 'tool' in metal_data.types and metal_data.tier <= 2):
                casting_recipe(rm, '%s_%s' % (metal, item), item, metal, item_data.smelt_amount, 0.1 if item == 'ingot' else 1)
            if item == 'ingot':
                casting_recipe(rm, '%s_%s_fire' % (metal, item), 'fire_ingot', metal, item_data.smelt_amount, 0.01, 'firmalife:metal/ingot/%s' % metal)
        rm.blockstate(('fluid', 'metal', metal)).with_block_model({'particle': 'block/lava_still'}, parent=None).with_lang(lang('Molten %s', metal)).with_tag('minecraft:replaceable')
        rm.lang('fluid.firmalife.metal.%s' % metal, lang('Molten %s', metal))
        rm.fluid_tag(metal, 'firmalife:metal/%s' % metal, 'firmalife:metal/flowing_%s' % metal)
        rm.fluid_tag('tfc:molten_metals', *['firmalife:metal/%s' % metal])

        item = rm.custom_item_model(('bucket', 'metal', metal), 'forge:bucket', {
            'parent': 'forge:item/bucket',
            'fluid': 'firmalife:metal/%s' % metal
        })
        item.with_lang(lang('molten %s bucket', metal))
        rm.lang('metal.firmalife.%s' % metal, lang(metal))

        rm.item_tag('forge:ingots/%s' % metal)
        rm.item_tag('tfc:pileable_ingots', '#forge:ingots/%s' % metal)
        if len(metal_data.types) > 0:
            rm.item_tag('forge:sheets/%s' % metal)
            rm.item_tag('tfc:pileable_double_ingots', '#forge:double_ingots/%s' % metal)
            rm.item_tag('tfc:pileable_sheets', '#forge:sheets/%s' % metal)

        for metal_block in METAL_BLOCKS:
            if metal_block == 'block' or metal_block == 'block_stairs' or metal_block == 'block_slab':
                block = rm.blockstate(('metal', 'block', metal)).with_block_model().with_lang(lang('%s plated block', metal)).with_item_model().with_block_loot('firmalife:metal/block/%s' % metal).with_tag('minecraft:mineable/pickaxe')
                block.make_slab()
                rm.block(('metal', 'block', '%s_slab' % metal)).with_lang(lang('%s plated slab', metal))
                rm.block(('metal', 'block', '%s_stairs' % metal)).with_lang(lang('%s plated stairs', metal)).with_block_loot('firmalife:metal/block/%s_stairs' % metal)
                block.make_stairs()
                slab_loot(rm, 'tfc:metal/block/%s_slab' % metal)

def chromium_ore_heats(rm: ResourceManager):
    ore = 'chromite'
    metal_data = FL_METALS['chromium']
    item_heat(rm, ('ore', ore), ['firmalife:ore/small_%s' % ore, 'firmalife:ore/normal_%s' % ore, 'firmalife:ore/poor_%s' % ore, 'firmalife:ore/rich_%s' % ore], metal_data.ingot_heat_capacity(), int(metal_data.melt_temperature), mb=40)
    temp = FL_METALS['chromium'].melt_temperature
    heat_recipe(rm, ('ore', 'small_%s' % ore), 'firmalife:ore/small_%s' % ore, temp, None, '10 firmalife:metal/chromium')
    heat_recipe(rm, ('ore', 'poor_%s' % ore), 'firmalife:ore/poor_%s' % ore, temp, None, '15 firmalife:metal/chromium')
    heat_recipe(rm, ('ore', 'normal_%s' % ore), 'firmalife:ore/normal_%s' % ore, temp, None, '25 firmalife:metal/chromium')
    heat_recipe(rm, ('ore', 'rich_%s' % ore), 'firmalife:ore/rich_%s' % ore, temp, None, '35 firmalife:metal/chromium')
