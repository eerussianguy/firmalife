from mcresources import ResourceManager
from mcresources import utils, loot_tables
from constants import *


def generate(rm: ResourceManager):
    bot_variants = four_rotations_mp('firmalife:block/oven_fire', (90, None, 180, 270), 'lit', True)
    for stage in ('bricks', 'clay'):
        for part in ('bottom', 'top'):
            tex = 'minecraft:block/bricks' if stage == 'bricks' else 'firmalife:block/unfired_bricks'
            rm.block_model('oven_%s_%s' % (part, stage), parent='firmalife:block/oven_%s' % part, textures={'0': tex, 'particle': tex})
        bot_variants += four_rotations_mp('firmalife:block/oven_bottom_%s' % stage, (90, None, 180, 270), 'cured', True if stage == 'bricks' else False)
    for i in range(1, 5):
        bot_variants += four_rotations_mp('firmalife:block/oven_logs_%s' % i, (90, None, 180, 270), 'logs', i)

    rm.blockstate_multipart('oven_bottom', *bot_variants).with_lang(lang('bottom oven')).with_tag('firmalife:oven_blocks')
    rm.item_model('oven_bottom', parent='firmalife:block/oven_bottom_clay')
    rm.blockstate('oven_top', variants={
        **four_rotations('firmalife:block/oven_top_bricks', (90, None, 180, 270), prefix='cured=true,'),
        **four_rotations('firmalife:block/oven_top_clay', (90, None, 180, 270), prefix='cured=false,')
    }).with_lang(lang('top oven')).with_item_model().with_tag('firmalife:oven_blocks')
    rm.item_model('oven_top', parent='firmalife:block/oven_top_clay')

    rm.block_model('oven_chimney_bricks', parent='firmalife:block/oven_chimney', textures={'cured': 'minecraft:block/bricks', 'particle': 'minecraft:block/bricks'})
    rm.block_model('oven_chimney_clay', parent='firmalife:block/oven_chimney', textures={'cured': 'firmalife:block/unfired_bricks', 'particle': 'firmalife:block/unfired_bricks'})
    rm.blockstate('oven_chimney', variants={'cured=true': {'model': 'firmalife:block/oven_chimney_bricks'}, 'cured=false': {'model': 'firmalife:block/oven_chimney_clay'}}).with_lang(lang('oven chimney')).with_tag('firmalife:oven_blocks')
    rm.item_model('oven_chimney', parent='firmalife:block/oven_chimney_clay')

    for item in SIMPLE_ITEMS:
        rm.item_model(item).with_lang(lang(item))
    for be in BLOCK_ENTITIES:
        rm.lang('firmalife.block_entity.%s' % be, lang(be))


def four_rotations(model: str, rots: Tuple[Any, Any, Any, Any], suffix: str = '', prefix: str = '') -> Dict[str, Dict[str, Any]]:
    return {
        '%sfacing=east%s' % (prefix, suffix): {'model': model, 'y': rots[0]},
        '%sfacing=north%s' % (prefix, suffix): {'model': model, 'y': rots[1]},
        '%sfacing=south%s' % (prefix, suffix): {'model': model, 'y': rots[2]},
        '%sfacing=west%s' % (prefix, suffix): {'model': model, 'y': rots[3]}
    }


def four_rotations_mp(model: str, rots: Tuple[Any, Any, Any, Any], condition_name: str, condition_value: Any) -> List:
    return [
        [{'facing': 'east', condition_name: condition_value}, {'model': model, 'y': rots[0]}],
        [{'facing': 'north', condition_name: condition_value}, {'model': model, 'y': rots[1]}],
        [{'facing': 'south', condition_name: condition_value}, {'model': model, 'y': rots[2]}],
        [{'facing': 'west', condition_name: condition_value}, {'model': model, 'y': rots[3]}]
    ]
