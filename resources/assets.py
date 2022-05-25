from mcresources import ResourceManager, ItemContext, BlockContext, block_states
from mcresources import utils, loot_tables

from constants import *


def generate(rm: ResourceManager):
    bot_variants = four_rotations_mp('firmalife:block/oven_fire', (90, None, 180, 270), 'lit', True)
    for stage in ('bricks', 'clay'):
        tex = 'minecraft:block/bricks' if stage == 'bricks' else 'firmalife:block/unfired_bricks'
        for part in ('bottom', 'top'):
            rm.block_model('oven_%s_%s' % (part, stage), parent='firmalife:block/oven_%s' % part, textures={'0': tex, 'particle': tex})
        rm.block_model('oven_particle_%s' % stage, parent='tfc:block/empty', textures={'particle': tex})
    for i in range(1, 5):
        bot_variants += four_rotations_mp('firmalife:block/oven_logs_%s' % i, (90, None, 180, 270), 'logs', i)

    rm.block_model('oven_chimney_bricks', parent='firmalife:block/oven_chimney', textures={'cured': 'minecraft:block/bricks', 'particle': 'minecraft:block/bricks'})
    rm.block_model('oven_chimney_clay', parent='firmalife:block/oven_chimney', textures={'cured': 'firmalife:block/unfired_bricks', 'particle': 'firmalife:block/unfired_bricks'})
    for stage in ('bricks', 'clay'):
        pref = 'cured_' if stage == 'bricks' else ''
        last_bot_variants = bot_variants.copy()
        last_bot_variants += four_rotations_mp_free('firmalife:block/oven_bottom_%s' % stage, (90, None, 180, 270))
        last_bot_variants += [{'model': 'firmalife:block/oven_particle_%s' % stage}]
        rm.blockstate_multipart('%soven_bottom' % pref, *last_bot_variants).with_lang(lang('%sbottom oven', pref)).with_tag('firmalife:oven_blocks').with_block_loot('firmalife:%soven_bottom' % pref)
        rm.item_model('%soven_bottom' % pref, parent='firmalife:block/oven_bottom_%s' % stage, no_textures=True)
        rm.blockstate('%soven_top' % pref, variants={**four_rotations('firmalife:block/oven_top_%s' % stage, (90, None, 180, 270))}).with_lang(lang('%stop oven', pref)).with_tag('firmalife:oven_blocks').with_block_loot('firmalife:%soven_top' % pref)
        rm.item_model('%soven_top' % pref, parent='firmalife:block/oven_top_%s' % stage, no_textures=True)
        rm.blockstate('%soven_chimney' % pref, model='firmalife:block/oven_chimney_%s' % stage).with_lang(lang('%soven chimney', pref)).with_tag('firmalife:oven_blocks').with_tag('firmalife:chimneys').with_block_loot('firmalife:%soven_chimney' % pref)
        rm.item_model('%soven_chimney' % pref, parent='firmalife:block/oven_chimney_%s' % stage, no_textures=True)

    rm.blockstate('drying_mat', model='firmalife:block/drying_mat').with_item_model().with_tag('tfc:mineable_with_sharp_tool').with_lang(lang('drying mat'))

    for fruit in TFC_FRUITS:
        rm.item_model(('not_dried', fruit), 'tfc:item/food/%s' % fruit)
        rm.item_model(('dried', fruit), 'firmalife:item/dried/dried_%s' % fruit)
        item_model_property(rm, 'tfc:food/%s' % fruit, [{'predicate': {'firmalife:dry': 1}, 'model': 'firmalife:item/dried/%s' % fruit}], {'parent': 'firmalife:item/not_dried/%s' % fruit})

    for greenhouse in GREENHOUSES:
        tex = 'firmalife:block/greenhouse/%s_glass' % greenhouse
        side_tex = tex if greenhouse.find('copper') != -1 else tex + '_divided'
        mine = 'minecraft:mineable/axe' if greenhouse in ('weathered_treated_wood', 'treated_wood') else 'minecraft:mineable/pickaxe'
        glass = rm.blockstate('%s_glass' % greenhouse, use_default_model=True).with_block_loot('firmalife:%s_glass' % greenhouse).with_item_model().with_block_model({'all': tex}).with_lang(lang('%s Greenhouse Glass', greenhouse)).with_tag(mine)
        glass.make_stairs(bottom_texture=tex, side_texture=side_tex, top_texture=tex)
        stairs = rm.block('%s_glass_stairs' % greenhouse).with_tag('minecraft:stairs').with_block_loot('firmalife:%s_glass_stairs' % greenhouse).with_lang(lang('%s greenhouse stairs', greenhouse)).with_tag(mine)
        glass.make_slab(bottom_texture=tex, side_texture=side_tex, top_texture=tex)
        slab = rm.block('%s_glass_slab' % greenhouse).with_tag('minecraft:slabs').with_lang(lang('%s greenhouse slab', greenhouse)).with_tag(mine)
        glass.make_door(bottom_texture='firmalife:block/greenhouse/%s_door_bottom' % greenhouse, top_texture='firmalife:block/greenhouse/%s_door_top' % greenhouse).with_tag('minecraft:doors').with_tag(mine).with_lang(lang('%s greenhouse door', greenhouse))
        door = rm.block('%s_glass_door' % greenhouse)
        door_loot(door, 'firmalife:%s_glass_door' % greenhouse)
        slab_loot(slab, 'firmalife:%s_glass_slab' % greenhouse)

        rm.block_tag('%s_greenhouse' % greenhouse, *['firmalife:' + greenhouse + '_glass%s' % suffix for suffix in ('', '_stairs', '_slab', '_door')])

    for block, tag in SIMPLE_BLOCKS.items():
        rm.blockstate(block).with_block_model().with_tag(tag).with_lang(lang(block)).with_item_model()
    for item in SIMPLE_ITEMS:
        rm.item_model(item).with_lang(lang(item))
    for item in SIMPLE_FOODS:
        rm.item_model('food/%s' % item).with_lang(lang(item))
    for item in SIMPLE_SPICES:
        rm.item_model('spice/%s' % item).with_lang(lang(item))
    for be in BLOCK_ENTITIES:
        rm.lang('firmalife.block_entity.%s' % be, lang(be))

    for key, value in DEFAULT_LANG.items():
        rm.lang(key, value)


def item_model_property(rm: ResourceManager, name_parts: utils.ResourceIdentifier, overrides: utils.Json, data: Dict[str, Any]) -> ItemContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'assets', res.domain, 'models', 'item', res.path), {
        **data,
        'overrides': overrides
    })
    return ItemContext(rm, res)


def four_rotations(model: str, rots: Tuple[Any, Any, Any, Any], suffix: str = '', prefix: str = '') -> Dict[str, Dict[str, Any]]:
    return {
        '%sfacing=east%s' % (prefix, suffix): {'model': model, 'y': rots[0]},
        '%sfacing=north%s' % (prefix, suffix): {'model': model, 'y': rots[1]},
        '%sfacing=south%s' % (prefix, suffix): {'model': model, 'y': rots[2]},
        '%sfacing=west%s' % (prefix, suffix): {'model': model, 'y': rots[3]}
    }

def four_rotations_mp_free(model: str, rots: Tuple[Any, Any, Any, Any]) -> List:
    return [
        [{'facing': 'east'}, {'model': model, 'y': rots[0]}],
        [{'facing': 'north'}, {'model': model, 'y': rots[1]}],
        [{'facing': 'south'}, {'model': model, 'y': rots[2]}],
        [{'facing': 'west'}, {'model': model, 'y': rots[3]}]
    ]

def four_rotations_mp(model: str, rots: Tuple[Any, Any, Any, Any], condition_name: str, condition_value: Any) -> List:
    return [
        [{'facing': 'east', condition_name: condition_value}, {'model': model, 'y': rots[0]}],
        [{'facing': 'north', condition_name: condition_value}, {'model': model, 'y': rots[1]}],
        [{'facing': 'south', condition_name: condition_value}, {'model': model, 'y': rots[2]}],
        [{'facing': 'west', condition_name: condition_value}, {'model': model, 'y': rots[3]}]
    ]

def slab_loot(block: BlockContext, loot: str) -> 'BlockContext':
    return block.with_block_loot({
        'name': loot,
        'functions': [{
            'function': 'minecraft:set_count',
            'conditions': [loot_tables.block_state_property(loot + '[type=double]')],
            'count': 2,
            'add': False
        }]
    })

def door_loot(block: BlockContext, loot: str) -> 'BlockContext':
    return block.with_block_loot({'name': loot, 'conditions': [loot_tables.block_state_property(loot + '[half=lower]')]})

