from typing import NamedTuple, Dict, Optional

from mcresources import ResourceManager, utils

from constants import lang
from data import item_heat
from recipes import anvil_recipe, Rules, welding_recipe, heat_recipe, casting_recipe

Metal = NamedTuple('Metal', tier=int, types=set, heat_capacity=float, melt_temperature=float, melt_metal=Optional[str])
MetalItem = NamedTuple('MetalItem', type=str, smelt_amount=int, parent_model=str, tag=Optional[str], mold=bool)

FL_METALS: Dict[str, Metal] = {
    'stainless_steel': Metal(6, {'part'}, 0.35, 1540, None)
}

METAL_ITEMS: Dict[str, MetalItem] = {
    'ingot': MetalItem('all', 100, 'item/generated', 'forge:ingots', True),
    'double_ingot': MetalItem('part', 200, 'item/generated', 'forge:double_ingots', False),
    'sheet': MetalItem('part', 200, 'item/generated', 'forge:sheets', False),
    'double_sheet': MetalItem('part', 400, 'item/generated', 'forge:double_sheets', False),
    'rod': MetalItem('part', 100, 'item/generated', 'forge:rods', False),
}

def generate(rm: ResourceManager):
    for metal, metal_data in FL_METALS.items():
        rm.data(('tfc', 'metals', metal), {
            'tier': metal_data.tier,
            'fluid': 'firmalife:metal/%s' % metal,
            'melt_temperature': metal_data.melt_temperature,
            'heat_capacity': metal_data.heat_capacity,
            'ingots': utils.ingredient('#forge:ingots/%s' % metal),
            'sheets': utils.ingredient('#forge:sheets/%s' % metal)
        })
        for item, item_data in METAL_ITEMS.items():
            if item_data.type in metal_data.types or item_data.type == 'all':
                if item_data.tag is not None:
                    rm.item_tag(item_data.tag + '/' + metal, 'firmalife:metal/%s/%s' % (item, metal))
                    ingredient = utils.item_stack('#%s/%s' % (item_data.tag, metal))
                else:
                    ingredient = utils.item_stack('firmalife:metal/%s/%s' % (item, metal))
                item_heat(rm, ('metal', metal + '_' + item), ingredient, metal_data.heat_capacity, metal_data.melt_temperature)

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
        rm.blockstate(('fluid', 'metal', metal)).with_block_model({'particle': 'block/lava_still'}, parent=None).with_lang(lang('Molten %s', metal))
        rm.lang('fluid.firmalife.metal.%s' % metal, lang('Molten %s', metal))
        rm.fluid_tag(metal, 'firmalife:metal/%s' % metal, 'firmalife:metal/flowing_%s' % metal)

        item = rm.custom_item_model(('bucket', 'metal', metal), 'forge:bucket', {
            'parent': 'forge:item/bucket',
            'fluid': 'firmalife:metal/%s' % metal
        })
        item.with_lang(lang('molten %s bucket', metal))
