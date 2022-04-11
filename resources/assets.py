from mcresources import ResourceManager, ItemContext
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

    for item in SIMPLE_ITEMS:
        rm.item_model(item).with_lang(lang(item))
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