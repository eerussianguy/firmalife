import itertools

from mcresources import ResourceManager, ItemContext, BlockContext, block_states
from mcresources import utils
from mcresources.type_definitions import JsonObject

from constants import *


def generate(rm: ResourceManager):
    bot_variants = four_rotations_mp('firmalife:block/oven_fire', (90, None, 180, 270), 'lit', True)
    for i in range(1, 5):
        bot_variants += four_rotations_mp('firmalife:block/oven_logs_%s' % i, (90, None, 180, 270), 'logs', i)
    variants = {
        'brick': 'minecraft:block/bricks',
        'rustic': 'firmalife:block/rustic_bricks',
        'tile': 'firmalife:block/tiles',
        'stone': 'firmalife:block/sealed_bricks',
        'clay': 'firmalife:block/unfired_bricks'
    }
    for var, base_tex in variants.items():
        bot_front = 'firmalife:block/%s_oven_bottom' % var
        top_front = 'firmalife:block/%s_oven_top' % var
        surface = 'firmalife:block/%s_oven_surface' % var
        hop_front = 'firmalife:block/%s_oven_hopper_front' % var
        hop_top = 'firmalife:block/%s_oven_hopper_top' % var
        side = 'firmalife:block/%s_oven_side' % var if var != 'clay' else base_tex

        rm.block_model('%s_oven_bottom' % var, parent='firmalife:block/oven_bottom', textures={'main': base_tex, 'side': side, 'front': bot_front, 'top': surface})
        rm.block_model('%s_oven_top' % var, parent='firmalife:block/oven_top', textures={'main': base_tex, 'side': side, 'front': top_front, 'top': base_tex})
        rm.block_model('%s_oven_particle' % var, parent='tfc:block/empty', textures={'particle': base_tex})
        rm.block_model('%s_oven_chimney' % var, parent='firmalife:block/oven_chimney', textures={'cured': base_tex})
        rm.block_model('%s_oven_chimney_alt' % var, parent='firmalife:block/oven_chimney_alt', textures={'cured': base_tex})
        rm.block_model('%s_oven_hopper' % var, parent='minecraft:block/orientable', textures={'top': hop_top, 'side': side, 'front': hop_front})
        if var != 'clay':
            rm.block_model('%s_oven_bottom_insulated' % var, parent='firmalife:block/oven_bottom', textures={'main': base_tex, 'side': 'firmalife:block/oven_insulation', 'front': bot_front + '_insulated', 'top': surface})

        pref = 'cured_%s_' % var
        if var == 'brick':
            pref = 'cured_'
        elif var == 'clay':
            pref = ''

        for bottom_type in ('insulated_', 'cured_'):
            if not (var == 'clay' and bottom_type == 'insulated_'):
                fixed_pref = pref.replace('cured_', bottom_type)
                last_bot_variants = bot_variants.copy()
                last_bot_variants += four_rotations_mp_free('firmalife:block/%s_oven_bottom%s' % (var, '_insulated' if bottom_type == 'insulated_' else ''), (90, None, 180, 270))
                last_bot_variants += [{'model': 'firmalife:block/%s_oven_particle' % var}]
                rm.blockstate_multipart('%soven_bottom' % fixed_pref, *last_bot_variants).with_lang(lang('%sbottom oven', fixed_pref))
                rm.item_model('%soven_bottom' % fixed_pref, parent='firmalife:block/%s_oven_bottom' % var, no_textures=True)

        rm.blockstate('%soven_top' % pref, variants={**four_rotations('firmalife:block/%s_oven_top' % var, (90, None, 180, 270))}).with_lang(lang('%stop oven', pref))
        rm.item_model('%soven_top' % pref, parent='firmalife:block/%s_oven_top' % var, no_textures=True)
        rm.blockstate('%soven_chimney' % pref, variants={'alt=true': {'model': 'firmalife:block/%s_oven_chimney_alt' % var}, 'alt=false': {'model': 'firmalife:block/%s_oven_chimney' % var}}).with_lang(lang('%soven chimney', pref))
        rm.item_model('%soven_chimney' % pref, parent='firmalife:block/%s_oven_chimney' % var, no_textures=True)
        rm.blockstate('%soven_hopper' % pref, variants={**four_rotations('firmalife:block/%s_oven_hopper' % var, (90, None, 180, 270))}).with_lang(lang('%soven hopper', pref))
        rm.item_model('%soven_hopper' % pref, parent='firmalife:block/%s_oven_hopper' % var, no_textures=True)

        if var != 'clay':
            block = rm.blockstate('%s_countertop' % var).with_item_model().with_lang(lang('%s countertop', var))
            block.with_block_model(parent='minecraft:block/cube_column', textures={'end': 'firmalife:block/%s_countertop' % var, 'side': side})

    block = rm.blockstate('ashtray', variants=dict(('stage=%s' % i, {'model': 'firmalife:block/ashtray_%s' % i}) for i in range(0, 11)))
    block.with_lang(lang('ashtray'))
    for i in range(0, 11):
        rm.block_model('ashtray_%s' % i, parent='minecraft:block/cube_column', textures={'side': 'firmalife:block/ashtray_side_%s' % i, 'end': 'firmalife:block/ashtray_top'})
    rm.item_model('ashtray', parent='firmalife:block/ashtray_0', no_textures=True)

    block = rm.blockstate('plate').with_item_model().with_lang(lang('plate'))

    block = rm.blockstate('jarring_station', variants={**four_rotations('firmalife:block/jarring_station_dynamic', (90, None, 180, 270))})
    rm.custom_block_model('jarring_station_dynamic', 'firmalife:jarring_station', {'base': {'parent': 'firmalife:block/jarring_station'}})
    block.with_item_model().with_lang(lang('jarring station'))

    rm.blockstate('drying_mat', model='firmalife:block/drying_mat').with_item_model().with_lang(lang('drying mat'))
    rm.blockstate('solar_drier', model='firmalife:block/solar_drier').with_item_model().with_lang(lang('solar drier'))
    rm.blockstate('hollow_shell', model='firmalife:block/hollow_shell').with_lang(lang('hollow shell'))
    block = rm.blockstate('mixing_bowl', model='firmalife:block/mixing_bowl').with_item_model().with_lang(lang('mixing bowl'))

    rm.blockstate('picker', model='firmalife:block/picker_base').with_lang(lang('picker'))
    rm.item_model('picker', parent='firmalife:block/picker', no_textures=True)
    rm.blockstate('sweeper', model='tfc:block/empty').with_lang(lang('sweeper'))
    rm.item_model('sweeper', parent='firmalife:block/sweeper_item', no_textures=True)

    for fruit in TFC_FRUITS:
        rm.item_model(('not_dried', fruit), 'tfc:item/food/%s' % fruit)
        rm.item_model(('dried', fruit), 'firmalife:item/dried/dried_%s' % fruit)
        item_model_property(rm, 'tfc:food/%s' % fruit, [{'predicate': {'firmalife:dry': 1}, 'model': 'firmalife:item/dried/%s' % fruit}], {'parent': 'firmalife:item/not_dried/%s' % fruit})

    for fruit in FL_FRUITS:
        rm.item_model(('not_dried', fruit), 'firmalife:item/food/%s' % fruit)
        rm.item_model(('dried', fruit), 'firmalife:item/dried/dried_%s' % fruit)
        item_model_property(rm, 'firmalife:food/%s' % fruit, [{'predicate': {'firmalife:dry': 1}, 'model': 'firmalife:item/dried/%s' % fruit}], {'parent': 'firmalife:item/not_dried/%s' % fruit})
        rm.item('food/%s' % fruit).with_lang(lang(fruit))
        rm.item_model(('food', fruit + '_jam'), 'firmalife:item/food/%s_jam' % fruit).with_lang(lang('%s jam', fruit))

    for greenhouse in GREENHOUSES:
        greenhouse_slab(rm, greenhouse, 'firmalife:block/greenhouse/%s' % greenhouse, 'firmalife:block/greenhouse/%s_glass' % greenhouse)
        greenhouse_stairs(rm, greenhouse, 'firmalife:block/greenhouse/%s' % greenhouse, 'firmalife:block/greenhouse/%s_glass' % greenhouse)
        greenhouse_wall(rm, greenhouse, 'firmalife:block/greenhouse/%s' % greenhouse, 'firmalife:block/greenhouse/%s_glass' % greenhouse)
        greenhouse_port(rm, greenhouse, 'firmalife:block/greenhouse/%s' % greenhouse, 'firmalife:block/greenhouse/%s_glass' % greenhouse)
        greenhouse_panel_wall(rm, greenhouse, 'firmalife:block/greenhouse/%s' % greenhouse, 'firmalife:block/greenhouse/%s_glass' % greenhouse)
        greenhouse_panel_roof(rm, greenhouse, 'firmalife:block/greenhouse/%s' % greenhouse, 'firmalife:block/greenhouse/%s_glass' % greenhouse)
        greenhouse_trapdoor(rm, greenhouse, 'firmalife:block/greenhouse/%s_glass' % greenhouse)
        greenhouse_door(rm, greenhouse, 'firmalife:block/greenhouse/%s_door_bottom' % greenhouse, 'firmalife:block/greenhouse/%s_door_top' % greenhouse)

    for planter in ('quad_planter', 'large_planter', 'bonsai_planter', 'hanging_planter'):
        for state in ('wet', 'dry'):
            rm.block_model('%s_%s' % (planter, state), parent='firmalife:block/%s' % planter, textures={'soil': 'firmalife:block/potting_soil_%s' % state})
            rm.custom_block_model('%s_%s_dynamic' % (planter, state), 'firmalife:%s' % planter, {'base': {'parent': 'firmalife:block/%s_%s' % (planter, state)}})
        rm.blockstate(planter, variants={
            'watered=true': {'model': 'firmalife:block/%s_wet_dynamic' % planter},
            'watered=false': {'model': 'firmalife:block/%s_dry_dynamic' % planter}
        }).with_lang(lang(planter))
        rm.item_model(planter, parent='firmalife:block/%s_dry' % planter, no_textures=True)

    for planter in ('trellis_planter', 'hydroponic_planter'):
        rm.custom_block_model('%s_dynamic' % planter, 'firmalife:%s' % planter, {'base': {'parent': 'firmalife:block/%s' % planter}})
    rm.blockstate('trellis_planter', model='firmalife:block/trellis_planter_dynamic').with_lang(lang('trellis planter')).with_item_model()
    rm.blockstate('hydroponic_planter', model='firmalife:block/hydroponic_planter_dynamic').with_lang(lang('hydroponic planter')).with_item_model()

    rm.blockstate('vat', variants={
        'sealed=false': {'model': 'firmalife:block/vat'},
        'sealed=true': {'model': 'firmalife:block/vat_sealed'},
    }).with_lang(lang('vat')).with_item_model()

    rm.blockstate('wool_string', variants={
        'axis=x': {'model': 'firmalife:block/wool_string'},
        'axis=z': {'model': 'firmalife:block/wool_string', 'y': 90}
    }).with_block_model(parent='firmalife:block/string', textures={'string': 'minecraft:block/white_wool'}).with_lang(lang('wool string')).with_item_model()

    rm.blockstate('grape_string', variants={
        'axis=x': {'model': 'firmalife:block/grape_trellis_string_center', 'y': 90},
        'axis=z': {'model': 'firmalife:block/grape_trellis_string_center'}
    }).with_lang(lang('grape string'))

    rm.blockstate('grape_string_plant_red', variants=dict(
       ('axis=%s,lifecycle=%s,stage=%s' % (a, l, s), {'model': 'firmalife:block/%s_%s' % (m, s), 'y': y if y != 0 else None})
        for a, y in (('x', 90), ('z', 0)) for s in range(0, 3) for l, m in (('dormant', 'grape_dead'), ('healthy', 'grape'), ('fruiting', 'grape'), ('flowering', 'grape'))
    )).with_lang(lang('red grape plant'))

    rm.blockstate('grape_string_plant_white', variants=dict(
        ('axis=%s,lifecycle=%s,stage=%s' % (a, l, s), {'model': 'firmalife:block/%s_%s' % (m, s), 'y': y if y != 0 else None})
        for a, y in (('x', 90), ('z', 0)) for s in range(0, 3) for l, m in (('dormant', 'grape_dead'), ('healthy', 'grape'), ('fruiting', 'grape'), ('flowering', 'grape'))
    )).with_lang(lang('white grape plant'))

    rm.blockstate('grape_string_red', variants={
        'axis=x,lifecycle=dormant': {'model': 'firmalife:block/grape_top_dead', 'y': 90},
        'axis=z,lifecycle=dormant': {'model': 'firmalife:block/grape_top_dead'},
        'axis=x,lifecycle=healthy': {'model': 'firmalife:block/grape_top_0', 'y': 90},
        'axis=z,lifecycle=healthy': {'model': 'firmalife:block/grape_top_0'},
        'axis=x,lifecycle=flowering': {'model': 'firmalife:block/grape_top_1', 'y': 90},
        'axis=z,lifecycle=flowering': {'model': 'firmalife:block/grape_top_1'},
        'axis=x,lifecycle=fruiting': {'model': 'firmalife:block/grape_top_red', 'y': 90},
        'axis=z,lifecycle=fruiting': {'model': 'firmalife:block/grape_top_red'},
    }).with_lang(lang('red grape plant'))

    rm.blockstate('grape_string_white', variants={
        'axis=x,lifecycle=dormant': {'model': 'firmalife:block/grape_top_dead', 'y': 90},
        'axis=z,lifecycle=dormant': {'model': 'firmalife:block/grape_top_dead'},
        'axis=x,lifecycle=healthy': {'model': 'firmalife:block/grape_top_0', 'y': 90},
        'axis=z,lifecycle=healthy': {'model': 'firmalife:block/grape_top_0'},
        'axis=x,lifecycle=flowering': {'model': 'firmalife:block/grape_top_1', 'y': 90},
        'axis=z,lifecycle=flowering': {'model': 'firmalife:block/grape_top_1'},
        'axis=x,lifecycle=fruiting': {'model': 'firmalife:block/grape_top_white', 'y': 90},
        'axis=z,lifecycle=fruiting': {'model': 'firmalife:block/grape_top_white'},
    }).with_lang(lang('white grape plant'))

    rm.blockstate('grape_fluff_red', variants={
        'lifecycle=dormant': [{'model': 'firmalife:block/grape_fluff_dormant_%s' % i} for i in range(1, 4)],
        'lifecycle=healthy': [{'model': 'firmalife:block/grape_fluff_healthy_%s' % i} for i in range(1, 4)],
        'lifecycle=fruiting': [{'model': 'firmalife:block/grape_fluff_fruiting_red_%s' % i} for i in range(1, 4)],
        'lifecycle=flowering': [{'model': 'firmalife:block/grape_fluff_flowering_%s' % i} for i in range(1, 4)],
    }, use_default_model=False).with_lang(lang('red grape plant'))
    rm.blockstate('grape_fluff_white', variants={
        'lifecycle=dormant': [{'model': 'firmalife:block/grape_fluff_dormant_%s' % i} for i in range(1, 4)],
        'lifecycle=healthy': [{'model': 'firmalife:block/grape_fluff_healthy_%s' % i} for i in range(1, 4)],
        'lifecycle=fruiting': [{'model': 'firmalife:block/grape_fluff_fruiting_white_%s' % i} for i in range(1, 4)],
        'lifecycle=flowering': [{'model': 'firmalife:block/grape_fluff_flowering_%s' % i} for i in range(1, 4)],
    }, use_default_model=False).with_lang(lang('white grape plant'))

    for i in range(1, 4):
        rm.block_model('grape_fluff_dormant_%s' % i, textures={'all': 'firmalife:block/crop/grape_leaves_dead'}, parent='tfc:block/groundcover/fallen_leaves_height%s' % str(i * 2))
        rm.block_model('grape_fluff_flowering_%s' % i, textures={'all': 'firmalife:block/crop/grape_leaves_flowering'}, parent='tfc:block/groundcover/fallen_leaves_height%s' % str(i * 2))
        rm.block_model('grape_fluff_healthy_%s' % i, textures={'all': 'firmalife:block/crop/grape_leaves'}, parent='tfc:block/groundcover/fallen_leaves_height%s' % str(i * 2))
        rm.block_model('grape_fluff_fruiting_red_%s' % i, textures={'all': 'firmalife:block/crop/grape_leaves_red'}, parent='tfc:block/groundcover/fallen_leaves_height%s' % str(i * 2))
        rm.block_model('grape_fluff_fruiting_white_%s' % i, textures={'all': 'firmalife:block/crop/grape_leaves_white'}, parent='tfc:block/groundcover/fallen_leaves_height%s' % str(i * 2))

    rm.block_model('plant/wild_grapes_dead', parent='tfc:block/plant/stationary_bush_1', textures={'bush': 'firmalife:block/crop/grape_leaves_dead'})
    for color in ('red', 'white'):
        rm.blockstate('plant/wild_%s_grapes' % color, variants={
            'mature=false': {'model': 'firmalife:block/plant/wild_grapes_dead'},
            'mature=true': {'model': 'firmalife:block/plant/wild_%s_grapes' % color},
        }).with_lang(lang('wild %s grapes', color))
        rm.item_model('plant/wild_%s_grapes' % color, parent='firmalife:block/plant/wild_%s_grapes' % color, no_textures=True)
        rm.block_model('plant/wild_%s_grapes' % color, parent='tfc:block/plant/stationary_bush_2', textures={'bush': 'firmalife:block/crop/grape_leaves_%s' % color})

    rm.blockstate_multipart('grape_trellis_post',
        ({'axis': 'x'}, {'model': 'firmalife:block/grape_trellis'}),
        ({'axis': 'z'}, {'model': 'firmalife:block/grape_trellis', 'y': 90}),
        ({'axis': 'x', 'string_plus': True}, {'model': 'firmalife:block/grape_trellis_string', 'y': 180}),
        ({'axis': 'z', 'string_plus': True}, {'model': 'firmalife:block/grape_trellis_string', 'y': 90}),
        ({'axis': 'x', 'string_minus': True}, {'model': 'firmalife:block/grape_trellis_string'}),
        ({'axis': 'z', 'string_minus': True}, {'model': 'firmalife:block/grape_trellis_string', 'y': 270}),
    ).with_lang(lang('grape_trellis_post'))
    rm.item_model('grape_trellis_post', parent='firmalife:block/grape_trellis', no_textures=True)

    rm.blockstate_multipart('grape_trellis_post_red',
        ({'axis': 'x'}, {'model': 'firmalife:block/grape_trellis'}),
        ({'axis': 'z'}, {'model': 'firmalife:block/grape_trellis', 'y': 90}),
        ({'axis': 'x', 'lifecycle': 'healthy'}, {'model': 'firmalife:block/grape_top_0_norope'}),
        ({'axis': 'z', 'lifecycle': 'healthy'}, {'model': 'firmalife:block/grape_top_0_norope', 'y': 90}),
        ({'axis': 'x', 'lifecycle': 'flowering'}, {'model': 'firmalife:block/grape_top_1_norope'}),
        ({'axis': 'z', 'lifecycle': 'flowering'}, {'model': 'firmalife:block/grape_top_1_norope', 'y': 90}),
        ({'axis': 'x', 'lifecycle': 'fruiting'}, {'model': 'firmalife:block/grape_top_red_norope'}),
        ({'axis': 'z', 'lifecycle': 'fruiting'}, {'model': 'firmalife:block/grape_top_red_norope', 'y': 90}),
        ({'axis': 'x', 'lifecycle': 'dormant'}, {'model': 'firmalife:block/grape_top_dead_norope'}),
        ({'axis': 'z', 'lifecycle': 'dormant'}, {'model': 'firmalife:block/grape_top_dead_norope', 'y': 90}),
        ({'axis': 'x', 'string_plus': True}, {'model': 'firmalife:block/grape_trellis_string', 'y': 180}),
        ({'axis': 'z', 'string_plus': True}, {'model': 'firmalife:block/grape_trellis_string', 'y': 90}),
        ({'axis': 'x', 'string_minus': True}, {'model': 'firmalife:block/grape_trellis_string'}),
        ({'axis': 'z', 'string_minus': True}, {'model': 'firmalife:block/grape_trellis_string', 'y': 270}),
    ).with_lang(lang('red grape plant'))
    rm.blockstate_multipart('grape_trellis_post_white',
        ({'axis': 'x'}, {'model': 'firmalife:block/grape_trellis'}),
        ({'axis': 'z'}, {'model': 'firmalife:block/grape_trellis', 'y': 90}),
        ({'axis': 'x', 'lifecycle': 'healthy'}, {'model': 'firmalife:block/grape_top_0_norope'}),
        ({'axis': 'z', 'lifecycle': 'healthy'}, {'model': 'firmalife:block/grape_top_0_norope', 'y': 90}),
        ({'axis': 'x', 'lifecycle': 'flowering'}, {'model': 'firmalife:block/grape_top_1_norope'}),
        ({'axis': 'z', 'lifecycle': 'flowering'}, {'model': 'firmalife:block/grape_top_1_norope', 'y': 90}),
        ({'axis': 'x', 'lifecycle': 'fruiting'}, {'model': 'firmalife:block/grape_top_white_norope'}),
        ({'axis': 'z', 'lifecycle': 'fruiting'}, {'model': 'firmalife:block/grape_top_white_norope', 'y': 90}),
        ({'axis': 'x', 'lifecycle': 'dormant'}, {'model': 'firmalife:block/grape_top_dead_norope'}),
        ({'axis': 'z', 'lifecycle': 'dormant'}, {'model': 'firmalife:block/grape_top_dead_norope', 'y': 90}),
        ({'axis': 'x', 'string_plus': True}, {'model': 'firmalife:block/grape_trellis_string', 'y': 180}),
        ({'axis': 'z', 'string_plus': True}, {'model': 'firmalife:block/grape_trellis_string', 'y': 90}),
        ({'axis': 'x', 'string_minus': True}, {'model': 'firmalife:block/grape_trellis_string'}),
        ({'axis': 'z', 'string_minus': True}, {'model': 'firmalife:block/grape_trellis_string', 'y': 270}),
    ).with_lang(lang('white grape plant'))

    for color in ('red', 'white'):
        rm.item_model('seeds/%s_grape' % color, 'firmalife:item/seeds/%s_grape' % color).with_lang(lang('%s grape seeds', color))

    for color in ('hematitic', 'olivine', 'volcanic'):
        rm.block_model(f'{color}_wine_bottle', {'bottle_cork': 'firmalife:block/wine/bottle_cork', 'bottle': f'firmalife:block/wine/{color}_bottle', 'bottle_neck': f'firmalife:block/wine/{color}_bottle_neck'}, 'firmalife:block/wine_bottle')
        rm.block_model(f'empty_{color}_wine_bottle', {'bottle_cork': 'tfc:block/empty'}, f'firmalife:block/{color}_wine_bottle')

    rm.blockstate('climate_station', variants={
        'stasis=true': {'model': 'firmalife:block/climate_station_valid'},
        'stasis=false': {'model': 'firmalife:block/climate_station_invalid'}
    }).with_lang(lang('climate station'))
    rm.item_model('climate_station', parent='firmalife:block/climate_station_invalid', no_textures=True)
    for variant in ('valid', 'invalid'):
        tex = 'firmalife:block/greenhouse/climate_station/%s' % variant
        rm.block_model('firmalife:climate_station_%s' % variant, {'west': tex, 'east': tex, 'north': tex, 'south': tex, 'particle': tex, 'up': 'firmalife:block/greenhouse/climate_station/top', 'down': 'firmalife:block/greenhouse/climate_station/end'}, 'block/cube')

    rm.blockstate('stovetop_grill').with_lang(lang('stovetop grill'))
    rm.blockstate('stovetop_pot').with_lang(lang('stovetop pot'))

    rm.blockstate('beehive', variants={
        **four_rotations('firmalife:block/beehive_open_honey', (90, None, 180, 270), ',honey=true,open=true'),
        **four_rotations('minecraft:block/beehive_honey', (90, None, 180, 270), ',honey=true,open=false'),
        **four_rotations('firmalife:block/beehive_open', (90, None, 180, 270), ',honey=false,open=true'),
        **four_rotations('minecraft:block/beehive', (90, None, 180, 270), ',honey=false,open=false')
    }).with_lang(lang('wooden beehive'))
    rm.item_model('beehive', parent='minecraft:block/beehive', no_textures=True)

    rm.blockstate('wild_beehive', variants={
        **four_rotations('firmalife:block/bee_nest_hanging', (90, None, 180, 270), ',honey=false'),
        **four_rotations('firmalife:block/bee_nest_hanging_honey', (90, None, 180, 270), ',honey=true'),
    }).with_lang(lang('wild beehive'))
    rm.item_model('wild_beehive', parent='firmalife:block/bee_nest_hanging', no_textures=True)

    rm.blockstate('centrifuge', variants=four_rotations('firmalife:block/centrifuge', (90, None, 180, 270))).with_lang(lang('centrifuge'))
    rm.item_model('centrifuge', parent='firmalife:block/centrifuge', no_textures=True)

    rm.blockstate('skep', variants={
        **four_rotations('firmalife:block/skep', (90, None, 180, 270), ',honey=false'),
        **four_rotations('firmalife:block/skep_honey', (90, None, 180, 270), ',honey=true'),
    }).with_lang(lang('skep'))
    rm.item_model('skep', parent='firmalife:block/skep', no_textures=True)

    rm.blockstate('compost_tumbler', variants=four_rotations('firmalife:block/compost_tumbler', (90, None, 180, 270))).with_lang(lang('compost tumbler'))
    rm.item_model('compost_tumbler', parent='firmalife:block/compost_tumbler_inventory', no_textures=True)

    block = rm.block('sealed').make_trapdoor().make_wall(texture='firmalife:block/sealed_bricks')
    make_door(block)
    rm.block('sealed_trapdoor').with_lang(lang('sealed trapdoor'))
    rm.block('sealed_wall').with_lang(lang('sealed wall'))
    block = rm.block('firmalife:sealed_door').with_lang(lang('sealed door'))

    rm.blockstate('dark_ladder', variants=four_rotations('firmalife:block/dark_ladder', (90, None, 180, 270))).with_block_model(textures={'texture': 'firmalife:block/dark_ladder', 'particle': 'firmalife:block/dark_ladder'}, parent='minecraft:block/ladder').with_lang(lang('dark ladder'))
    rm.item_model('dark_ladder', 'firmalife:block/dark_ladder')

    rm.blockstate('reinforced_poured_glass').with_block_model({'all': 'firmalife:block/reinforced_glass'}, parent='tfc:block/template_poured_glass').with_lang(lang('reinforced poured glass'))
    rm.item_model('reinforced_poured_glass', 'firmalife:item/reinforced_glass')

    block = rm.blockstate('pumping_station', variants=four_rotations('firmalife:block/pumping_station', (90, None, 180, 270)))
    block.with_lang(lang('pumping station')).with_item_model()

    rm.blockstate('irrigation_tank').with_lang(lang('irrigation tank')).with_item_model()

    rm.blockstate('sprinkler', variants={'axis=x': {'model': 'firmalife:block/sprinkler'}, 'axis=z': {'model': 'firmalife:block/sprinkler', 'y': 90}}).with_lang(lang('sprinkler')).with_item_model()
    rm.blockstate('floor_sprinkler').with_lang(lang('floor sprinkler'))

    for pref in ('center', 'inventory', 'on', 'off'):
        rm.block_model('oxidized_pipe_%s' % pref, {'0': 'firmalife:block/greenhouse/oxidized_copper'}, parent='firmalife:block/pipe_%s' % pref)

    for pref in ('oxidized_', ''):
        pipe_on = 'firmalife:block/%spipe_on' % pref
        pipe_off = 'firmalife:block/%spipe_off' % pref

        block = rm.blockstate_multipart(
            '%scopper_pipe' % pref,
            {'model': 'firmalife:block/%spipe_center' % pref},
            ({'north': True}, {'model': pipe_on, 'x': 270}),
            ({'north': False}, {'model': pipe_off, 'x': 270}),
            ({'south': True}, {'model': pipe_on, 'x': 90}),
            ({'south': False}, {'model': pipe_off, 'x': 90}),
            ({'east': True}, {'model': pipe_on, 'x': 90, 'y': 270}),
            ({'east': False}, {'model': pipe_off, 'x': 90, 'y': 270}),
            ({'west': True}, {'model': pipe_on, 'x': 90, 'y': 90}),
            ({'west': False}, {'model': pipe_off, 'x': 90, 'y': 90}),
            ({'down': True}, {'model': pipe_on}),
            ({'down': False}, {'model': pipe_off}),
            ({'up': True}, {'model': pipe_on, 'x': 180}),
            ({'up': False}, {'model': pipe_off, 'x': 180}),
        )
        block.with_lang(lang('%scopper_pipe', pref))
        rm.item_model('%scopper_pipe' % pref, parent='firmalife:block/%spipe_inventory' % pref, no_textures=True)

    for cheese in CHEESE_WHEELS:
        states = [({'rack': True}, {'model': 'tfc:block/barrel_rack'})]
        for age in ('fresh', 'aged', 'vintage'):
            for i in range(1, 5):
                surf = 'firmalife:block/cheese/%s_wheel_surface_%s' % (cheese, age)
                rm.block_model('cheese/%s_%s_%s' % (cheese, age, i), parent='firmalife:block/cheese_%s' % i, textures={'surface': surf, 'particle': surf, 'down': surf, 'inside': 'firmalife:block/cheese/%s_wheel_inner_%s' % (cheese, age)})
                states.append(({'age': age, 'count': i}, {'model': 'firmalife:block/cheese/%s_%s_%s' % (cheese, age, i)}))
        block = rm.blockstate_multipart('%s_wheel' % cheese, *states).with_lang(lang('%s cheese wheel', cheese))
        rm.item_model('firmalife:%s_wheel' % cheese, parent='firmalife:block/cheese/%s_fresh_4' % cheese, no_textures=True)

    ore = 'chromite'
    for grade in ORE_GRADES.keys():
        rm.item_model('firmalife:ore/%s_%s' % (grade, ore)).with_lang(lang('%s %s', grade, ore))
    block = rm.blockstate('ore/small_%s' % ore, variants={"": four_ways('firmalife:block/small_%s' % ore)}, use_default_model=False)
    block.with_lang(lang('small %s', ore))
    rm.item_model('ore/small_%s' % ore).with_lang(lang('small %s', ore))
    for rock, data in TFC_ROCKS.items():
        for grade in ORE_GRADES.keys():
            block = rm.blockstate(('ore', grade + '_' + ore, rock), 'firmalife:block/ore/%s_%s/%s' % (grade, ore, rock))
            block.with_block_model({
                'all': 'tfc:block/rock/raw/%s' % rock,
                'particle': 'tfc:block/rock/raw/%s' % rock,
                'overlay': 'firmalife:block/ore/%s_%s' % (grade, ore)
            }, parent='tfc:block/ore')
            block.with_item_model().with_lang(lang('%s %s %s', grade, rock, ore))
            rm.block('firmalife:ore/%s_%s/%s/prospected' % (grade, ore, rock)).with_lang(lang(ore))

    for carving in CARVINGS.keys():
        for variant, lang_part in (('lit_pumpkin', 'Jack o\'Lantern'), ('carved_pumpkin', 'Carved Pumpkin')):
            name = '%s/%s' % (variant, carving)
            rm.block_model(name, parent='minecraft:block/carved_pumpkin', textures={'front': 'firmalife:block/%s/%s' % (variant, carving)})
            rm.blockstate(name, variants=four_rotations('firmalife:block/%s' % name, (90, 0, 180, 270))).with_lang(lang('%s %s', carving, lang_part))
            rm.item_model('firmalife:%s' % name, parent='firmalife:block/%s' % name, no_textures=True)

    for var in ('rustic_bricks', 'tiles'):
        block = rm.block(var).make_slab().make_stairs().make_wall()
        for extra in ('_slab', '_stairs', '_wall'):
            rm.block('firmalife:%s%s' % (var, extra)).with_lang(lang('%s%s', var.replace('bricks', 'brick').replace('tiles', 'tile'), extra))

    for variant in ('gold', 'red', 'purple'):
        rm.block_model('plant/butterfly_grass_%s' % variant, parent='firmalife:block/tinted_cross_overlay', textures={'cross': 'firmalife:block/plant/butterfly_grass/base', 'overlay': 'firmalife:block/plant/butterfly_grass/%s' % variant})
    rm.blockstate('plant/butterfly_grass', variants={'': [{'model': 'firmalife:block/plant/butterfly_grass_%s' % variant, 'y': rot} for variant in ('gold', 'red', 'purple') for rot in (None, 90)]}, use_default_model=False).with_lang(lang('butterfly grass'))
    simple_plant_data(rm, 'firmalife:plant/butterfly_grass')
    flower_pot_cross(rm, 'butterfly grass', 'firmalife:plant/potted/butterfly_grass', 'plant/flowerpot/butterfly_grass', 'firmalife:block/plant/butterfly_grass/base', tinted=True)
    rm.item_model('plant/butterfly_grass', 'firmalife:block/plant/butterfly_grass/base')

    lifecycle_to_model = {'healthy': '', 'dormant': 'dry_', 'fruiting': 'fruiting_', 'flowering': 'flowering_'}
    lifecycles = ('healthy', 'dormant', 'fruiting', 'flowering')
    for berry in STILL_BUSHES.keys():
        rm.blockstate('plant/%s_bush' % berry, variants=dict(
            (
                'lifecycle=%s,stage=%d' % (lifecycle, stage),
                {'model': 'firmalife:block/plant/%s%s_bush_%d' % (lifecycle_to_model[lifecycle], berry, stage)}
            ) for lifecycle, stage in itertools.product(lifecycles, range(0, 3))
        )).with_lang(lang('%s Bush', berry))

        rm.item_model('plant/%s_bush' % berry, parent='firmalife:block/plant/%s_bush_2' % berry, no_textures=True)
        for lifecycle, stage in itertools.product(lifecycle_to_model.values(), range(0, 3)):
            rm.block_model('plant/%s%s_bush_%d' % (lifecycle, berry, stage), parent='tfc:block/plant/stationary_bush_%d' % stage, textures={'bush': 'firmalife:block/berry_bush/' + lifecycle + '%s_bush' % berry})

    for herb in HERBS:
        for stage in ('0', '1'):
            rm.block_model('plant/%s_%s' % (herb, stage), parent='minecraft:block/cross', textures={'cross': 'firmalife:block/plant/%s/%s' % (herb, stage)})
        # rm.blockstate('plant/%s' % herb, variants={'age=0': {'model': 'firmalife:block/plant/%s_0' % herb}, 'age=1': {'model': 'firmalife:block/plant/%s_1' % herb}}).with_lang(lang(herb))
        rm.blockstate('plant/%s' % herb, variants=dict((f'age={i}', {'model': f'firmalife:block/plant/{herb}_{j}'}) for (i, j) in ((0, 0), (1, 1), (2, 1), (3, 1)))).with_lang(lang(herb))
        simple_plant_data(rm, 'firmalife:plant/%s' % herb, straw=False)
        rm.item_model('plant/%s' % herb, 'firmalife:block/plant/%s/1' % herb)
        flower_pot_cross(rm, herb, 'firmalife:plant/potted/%s' % herb, 'plant/flowerpot/%s' % herb, 'firmalife:block/plant/%s/1' % herb)

    for wood in TFC_WOODS.keys():
        block = rm.blockstate('firmalife:wood/food_shelf/%s' % wood, variants=four_rotations('firmalife:block/wood/food_shelf/%s_dynamic' % wood, (270, 180, None, 90)))
        block.with_lang(lang('%s food shelf', wood))
        rm.item_model('firmalife:wood/food_shelf/%s' % wood, parent='firmalife:block/wood/food_shelf/%s' % wood, no_textures=True)
        rm.custom_block_model('firmalife:wood/food_shelf/%s_dynamic' % wood, 'firmalife:food_shelf', {'base': {'parent': 'firmalife:block/wood/food_shelf/%s' % wood}})
        rm.block_model('firmalife:wood/food_shelf/%s' % wood, parent='firmalife:block/food_shelf_base', textures={'wood': 'tfc:block/wood/planks/%s' % wood})

        block = rm.blockstate('firmalife:wood/hanger/%s' % wood, model='firmalife:block/wood/hanger/%s_dynamic' % wood)
        block.with_lang(lang('%s hanger' % wood))
        rm.custom_block_model('firmalife:wood/hanger/%s_dynamic' % wood, 'firmalife:hanger', {'base': {'parent': 'firmalife:block/wood/hanger/%s' % wood}})
        rm.item_model('firmalife:wood/hanger/%s' % wood, parent='firmalife:block/wood/hanger/%s' % wood, no_textures=True)
        rm.block_model('firmalife:wood/hanger/%s' % wood, parent='firmalife:block/hanger_base', textures={'wood': 'tfc:block/wood/planks/%s' % wood, 'string': 'minecraft:block/white_wool'})

        block = rm.blockstate('firmalife:wood/jarbnet/%s' % wood, variants={
            **four_rotations('firmalife:block/wood/jarbnet/%s_dynamic' % wood, (90, None, 180, 270), suffix=',open=true'),
            **four_rotations('firmalife:block/wood/jarbnet/%s_shut_dynamic' % wood, (90, None, 180, 270), suffix=',open=false'),
        })
        block.with_lang(lang('%s jarbnet', wood))
        rm.item_model('firmalife:wood/jarbnet/%s' % wood, parent='firmalife:block/wood/jarbnet/%s' % wood, no_textures=True)
        textures = {'planks': 'tfc:block/wood/planks/%s' % wood, 'sheet': 'tfc:block/wood/sheet/%s' % wood, 'log': 'tfc:block/wood/log/%s' % wood}
        rm.block_model('firmalife:wood/jarbnet/%s' % wood, parent='firmalife:block/jarbnet', textures=textures)
        rm.block_model('firmalife:wood/jarbnet/%s_shut' % wood, parent='firmalife:block/jarbnet_shut', textures=textures)
        rm.custom_block_model('firmalife:wood/jarbnet/%s_dynamic' % wood, 'firmalife:jarbnet', {'base': {'parent': 'firmalife:block/wood/jarbnet/%s' % wood}})
        rm.custom_block_model('firmalife:wood/jarbnet/%s_shut_dynamic' % wood, 'firmalife:jarbnet', {'base': {'parent': 'firmalife:block/wood/jarbnet/%s_shut' % wood}})

        tex = {
            '0': f'firmalife:block/wood/big_barrel/{wood}_3_side',
            '1': f'firmalife:block/wood/big_barrel/{wood}_0',
            '2': f'firmalife:block/wood/big_barrel/{wood}_0_side',
            '3': f'firmalife:block/wood/big_barrel/{wood}_1',
            '4': f'firmalife:block/wood/big_barrel/{wood}_1_side',
            '5': f'firmalife:block/wood/big_barrel/{wood}_2',
            '6': f'firmalife:block/wood/big_barrel/{wood}_2_side',
            '7': f'firmalife:block/wood/big_barrel/{wood}_3',
            '8': f'firmalife:block/wood/big_barrel/{wood}_3_top',
            '9': f'firmalife:block/wood/big_barrel/{wood}_0_top',
            '10': f'firmalife:block/wood/big_barrel/{wood}_1_top',
            '11': f'firmalife:block/wood/big_barrel/{wood}_2_top',
            '12': f'tfc:block/wood/log/{wood}',
        }
        for i in range(0, 8):
            rm.block_model('wood/big_barrel/%s_%s' % (wood, i), parent='firmalife:block/big_barrel_%s' % i, textures=tex)
        rm.block_model('wood/big_barrel/%s_item' % wood, parent='firmalife:block/big_barrel_item', textures=tex)
        rm.item_model('wood/keg/%s' % wood, parent='firmalife:block/wood/big_barrel/%s_item' % wood, no_textures=True)
        block = rm.blockstate('wood/keg/%s' % wood, variants=dict(
            ('barrel_part=%s,facing=%s' % (i, f), {'model': 'firmalife:block/wood/big_barrel/%s_%s' % (wood, i), 'y': y if y != 0 else None})
            for f, y in (('east', 90), ('north', 0), ('south', 180), ('west', 270)) for i in range(0, 8)
        )).with_lang(lang('%s keg' % wood))

        rm.blockstate('wood/stomping_barrel/%s' % wood).with_block_model({'0': 'tfc:block/wood/sheet/%s' % wood}, 'firmalife:block/stomping_barrel').with_lang(lang('%s stomping barrel', wood))
        rm.item_model('wood/stomping_barrel/%s' % wood, parent='firmalife:block/wood/stomping_barrel/%s' % wood, no_textures=True)

        rm.blockstate('wood/barrel_press/%s' % wood).with_block_model({'0': 'tfc:block/wood/sheet/%s' % wood}, 'firmalife:block/barrel_press').with_lang(lang('%s barrel press', wood))
        rm.item_model('wood/barrel_press/%s' % wood, parent='firmalife:block/wood/barrel_press/%s' % wood, no_textures=True)

        block = rm.blockstate('wood/wine_shelf/%s' % wood, variants=four_rotations('firmalife:block/wood/wine_shelf/%s_dynamic' % wood, (90, None, 180, 270)))
        block.with_block_model({'0': 'tfc:block/wood/planks/%s' % wood, '2': 'tfc:block/wood/sheet/%s' % wood, '3': 'tfc:block/wood/stripped_log/%s' % wood}, 'firmalife:block/wine_shelf')
        block.with_lang(lang('%s wine shelf', wood))
        rm.item_model('wood/wine_shelf/%s' % wood, parent='firmalife:block/wood/wine_shelf/%s' % wood, no_textures=True)
        rm.custom_block_model('firmalife:wood/wine_shelf/%s_dynamic' % wood, 'firmalife:wine_shelf', {'base': {'parent': 'firmalife:block/wood/wine_shelf/%s' % wood}})

    for fruit in FRUITS.keys():
        for prefix in ('', 'growing_'):
            block = rm.blockstate_multipart('plant/' + fruit + '_' + prefix + 'branch',
                ({'model': 'firmalife:block/plant/%s_branch_core' % fruit}),
                ({'down': True}, {'model': 'firmalife:block/plant/%s_branch_down' % fruit}),
                ({'up': True}, {'model': 'firmalife:block/plant/%s_branch_up' % fruit}),
                ({'north': True}, {'model': 'firmalife:block/plant/%s_branch_side' % fruit, 'y': 90}),
                ({'south': True}, {'model': 'firmalife:block/plant/%s_branch_side' % fruit, 'y': 270}),
                ({'west': True}, {'model': 'firmalife:block/plant/%s_branch_side' % fruit}),
                ({'east': True}, {'model': 'firmalife:block/plant/%s_branch_side' % fruit, 'y': 180})
                ).with_lang(lang('%s Branch', fruit))
            for part in ('down', 'side', 'up', 'core'):
                rm.block_model('firmalife:plant/%s_branch_%s' % (fruit, part), parent='tfc:block/plant/branch_%s' % part, textures={'bark': 'firmalife:block/fruit_tree/%s_branch' % fruit})
            rm.blockstate('plant/%s_leaves' % fruit, variants={
                'lifecycle=flowering': {'model': 'firmalife:block/plant/%s_flowering_leaves' % fruit},
                'lifecycle=fruiting': {'model': 'firmalife:block/plant/%s_fruiting_leaves' % fruit},
                'lifecycle=dormant': {'model': 'firmalife:block/plant/%s_dry_leaves' % fruit},
                'lifecycle=healthy': {'model': 'firmalife:block/plant/%s_leaves' % fruit}
            }).with_item_model().with_lang(lang('%s Leaves', fruit))
            for life in ('', '_fruiting', '_flowering', '_dry'):
                rm.block_model('firmalife:plant/%s%s_leaves' % (fruit, life), parent='block/leaves', textures={'all': 'firmalife:block/fruit_tree/%s%s_leaves' % (fruit, life)})

            rm.blockstate(('plant', '%s_sapling' % fruit), variants={'saplings=%d' % i: {'model': 'firmalife:block/plant/%s_sapling_%d' % (fruit, i)} for i in range(1, 4 + 1)}).with_lang(lang('%s Sapling', fruit))
            for stage in range(2, 4 + 1):
                rm.block_model(('plant', '%s_sapling_%d' % (fruit, stage)), parent='tfc:block/plant/cross_%s' % stage, textures={'cross': 'firmalife:block/fruit_tree/%s_sapling' % fruit})
            rm.block_model(('plant', '%s_sapling_1' % fruit), {'cross': 'firmalife:block/fruit_tree/%s_sapling' % fruit}, 'block/cross')
            rm.item_model(('plant', '%s_sapling' % fruit), 'firmalife:block/fruit_tree/%s_sapling' % fruit)
            flower_pot_cross(rm, '%s sapling' % fruit, 'firmalife:plant/potted/%s_sapling' % fruit, 'plant/flowerpot/%s_sapling' % fruit, 'firmalife:block/fruit_tree/%s_sapling' % fruit)

    contained_fluid(rm, 'hollow_shell', 'firmalife:item/hollow_shell', 'firmalife:item/hollow_shell_overlay').with_lang(lang('Hollow Shell'))
    contained_fluid(rm, 'wine_glass', 'firmalife:item/wine_glass', 'firmalife:item/wine_glass_overlay').with_lang(lang('wine glass'))

    peel(rm, 'peel', 'firmalife:item/peel')

    for name in JARS:
        rm.block_model('jar/%s' % name, textures={'1': 'firmalife:block/jar/%s' % name}, parent='tfc:block/jar')
        rm.item_model('jar/%s' % name, 'firmalife:item/jar/%s' % name).with_lang(lang('jar of %s', name))
    for fruit in FL_FRUITS:
        rm.block_model('jar/%s' % fruit, textures={'1': 'firmalife:block/jar/%s' % fruit}, parent='tfc:block/jar')
        rm.block_model('jar/%s_unsealed' % fruit, textures={'1': 'firmalife:block/jar/%s' % fruit, '2': 'tfc:block/jar_no_lid'}, parent='tfc:block/jar')
        rm.item_model('jar/%s' % fruit, 'firmalife:item/jar/%s' % fruit).with_lang(lang('%s jam', fruit))
        rm.item_model('jar/%s_unsealed' % fruit, 'firmalife:item/jar/%s_unsealed' % fruit).with_lang(lang('%s jam', fruit))

    for block, tag in SIMPLE_BLOCKS.items():
        rm.blockstate(block).with_block_model().with_lang(lang(block)).with_item_model()
    for item in SIMPLE_ITEMS:
        rm.item_model(item).with_lang(lang(item))
    for item in SIMPLE_FOODS:
        rm.item_model('food/%s' % item).with_lang(lang(item))
    for grain in TFC_GRAINS:
        rm.item_model('food/%s_slice' % grain).with_lang(lang('%s slice', grain))
        rm.item_model('food/%s_flatbread' % grain).with_lang(lang('%s flatbread', grain))
        rm.item_model('food/%s_dough' % grain, 'tfc:item/food/%s_dough' % grain).with_lang(lang('%s dough', grain))
        rm.item_model('tfc:food/%s_dough' % grain, 'firmalife:item/food/%s_flatbread_dough' % grain)
        rm.lang('item.tfc.food.%s_dough' % grain, lang('%s flatbread dough', grain))
    for item in SIMPLE_SPICES:
        rm.item_model('spice/%s' % item).with_lang(lang(item))
    for be in BLOCK_ENTITIES:
        rm.lang('firmalife.block_entity.%s' % be, lang(be))
    for fluid in EXTRA_FLUIDS:
        water_based_fluid(rm, fluid)
    for fluid in WINES:
        water_based_fluid(rm, fluid + '_wine')

    for key, value in DEFAULT_LANG.items():
        rm.lang(key, value)


def contained_fluid(rm: ResourceManager, name_parts: utils.ResourceIdentifier, base: str, overlay: str) -> 'ItemContext':
    return rm.custom_item_model(name_parts, 'tfc:fluid_container', {
        'parent': 'neoforge:item/default',
        'textures': {
            'base': base,
            'fluid': overlay
        }
    })


def flower_pot_cross(rm: ResourceManager, simple_name: str, name: str, model: str, texture: str, tinted: bool = False):
    rm.blockstate(name, model='firmalife:block/%s' % model).with_lang(lang('potted %s', simple_name))
    rm.block_model(model, parent='minecraft:block/tinted_flower_pot_cross' if tinted else 'minecraft:block/flower_pot_cross', textures={'plant': texture, 'dirt': 'tfc:block/dirt/entisol'})


def simple_plant_data(rm: ResourceManager, p: str, bees: bool = True, straw: bool = True):
    ...


def item_model_property(rm: ResourceManager, name_parts: utils.ResourceIdentifier, overrides: utils.Json, data: Dict[str, Any]) -> ItemContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write(('assets', res.domain, 'models', 'item', res.path), {
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

def peel(rm: ResourceManager, name_parts: str, texture: str) -> 'ItemContext':
    rm.item(name_parts).with_lang(lang(name_parts))
    rm.item_model(name_parts + '_in_hand', {'particle': texture}, parent='minecraft:item/trident_in_hand')
    rm.item_model(name_parts + '_gui', texture)
    model = rm.domain + ':item/' + name_parts
    return rm.custom_item_model(name_parts, 'neoforge:separate_transforms', {
        'gui_light': 'front',
        'base': {'parent': model + '_in_hand'},
        'perspectives': {
            'none': {'parent': model + '_gui'},
            'fixed': {'parent': model + '_gui'},
            'ground': {'parent': model + '_gui'},
            'gui': {'parent': model + '_gui'}
        }
    })


def greenhouse_stairs(rm: ResourceManager, name: str, frame: str, glass: str) -> 'BlockContext':
    block_name = '%s_greenhouse_roof' % name
    stair_model = 'firmalife:block/greenhouse/%s_roof' % name
    stair_model_inner = 'firmalife:block/greenhouse/%s_roof_inner' % name
    stair_model_outer = 'firmalife:block/greenhouse/%s_roof_outer' % name

    textures = {'glass': glass, 'steel': frame}
    block = rm.blockstate(block_name, variants=block_states.stairs_variants(stair_model, stair_model_inner, stair_model_outer))
    rm.block_model('greenhouse/%s_roof' % name, textures=textures, parent='firmalife:block/greenhouse_roof')
    rm.block_model('greenhouse/%s_roof_inner' % name, textures=textures, parent='firmalife:block/greenhouse_roof_inner')
    rm.block_model('greenhouse/%s_roof_outer' % name, textures=textures, parent='firmalife:block/greenhouse_roof_outer')
    rm.item_model(block_name, parent='firmalife:block/greenhouse/%s_roof' % name, no_textures=True)
    block.with_lang(lang('%s greenhouse roof', name))
    return block

def greenhouse_slab(rm: ResourceManager, name: str, frame: str, glass: str) -> 'BlockContext':
    wall = 'firmalife:block/greenhouse/%s_wall_both' % name
    top = 'firmalife:block/greenhouse/%s_roof_top' % name
    top_upper = 'firmalife:block/greenhouse/%s_roof_top_upper' % name
    block_name = '%s_greenhouse_roof_top' % name

    textures = {'glass': glass, 'steel': frame}
    block = rm.blockstate(block_name, variants=block_states.slab_variants(wall, top, top_upper)).with_lang(lang('%s greenhouse roof top', name))
    rm.block_model('greenhouse/%s_roof_top' % name, textures, parent='firmalife:block/greenhouse_roof_top')
    rm.block_model('greenhouse/%s_roof_top_upper' % name, textures, parent='firmalife:block/greenhouse_roof_top_upper')
    rm.item_model(block_name, parent='firmalife:block/greenhouse/%s_roof_top' % name, no_textures=True)
    return block


def greenhouse_wall(rm: ResourceManager, name: str, frame: str, glass: str) -> 'BlockContext':
    rm.block_model('greenhouse/%s_wall' % name, {'glass': glass + '_both', 'steel': frame}, parent='firmalife:block/greenhouse_wall')
    rm.block_model('greenhouse/%s_wall_down' % name, {'glass': glass + '_down', 'steel': frame}, parent='firmalife:block/greenhouse_wall_down')
    rm.block_model('greenhouse/%s_wall_up' % name, {'glass': glass + '_up', 'steel': frame}, parent='firmalife:block/greenhouse_wall_up')
    rm.block_model('greenhouse/%s_wall_both' % name, {'glass': glass, 'steel': frame}, parent='firmalife:block/greenhouse_wall_both')

    block = rm.blockstate('%s_greenhouse_wall' % name, variants={
        'down=false,up=false': {'model': 'firmalife:block/greenhouse/%s_wall' % name},
        'down=true,up=false': {'model': 'firmalife:block/greenhouse/%s_wall_down' % name},
        'down=false,up=true': {'model': 'firmalife:block/greenhouse/%s_wall_up' % name},
        'down=true,up=true': {'model': 'firmalife:block/greenhouse/%s_wall_both' % name}
    }).with_lang(lang('%s greenhouse wall', name))
    rm.item_model('%s_greenhouse_wall' % name, parent='firmalife:block/greenhouse/%s_wall_both' % name, no_textures=True)
    return block


def greenhouse_port(rm: ResourceManager, name: str, frame: str, glass: str) -> 'BlockContext':
    rm.block_model('greenhouse/%s_port_inv' % name, {'glass': glass + '_both', 'steel': frame}, parent='firmalife:block/greenhouse_port_inv')
    block = rm.blockstate_multipart('%s_greenhouse_port' % name,
        ({'axis': 'x'}, {'model': 'firmalife:block/pipe_inventory', 'x': 90, 'y': 90}),
        ({'axis': 'z'}, {'model': 'firmalife:block/pipe_inventory', 'x': 90}),
        ({'down': False, 'up': False}, {'model': 'firmalife:block/greenhouse/%s_wall' % name}),
        ({'down': True, 'up': False}, {'model': 'firmalife:block/greenhouse/%s_wall_down' % name}),
        ({'down': False, 'up': True}, {'model': 'firmalife:block/greenhouse/%s_wall_up' % name}),
        ({'down': True, 'up': True}, {'model': 'firmalife:block/greenhouse/%s_wall_both' % name})
    ).with_lang(lang('%s greenhouse port', name))
    rm.item_model('%s_greenhouse_port' % name, parent='firmalife:block/greenhouse/%s_port_inv' % name, no_textures=True)
    return block

def greenhouse_panel_wall(rm: ResourceManager, name: str, frame: str, glass: str) -> 'BlockContext':
    rm.block_model('greenhouse/%s_panel_wall' % name, {'glass': glass + '_both', 'steel': frame}, parent='firmalife:block/greenhouse_wall_panel')
    rm.block_model('greenhouse/%s_panel_wall_down' % name, {'glass': glass + '_down', 'steel': frame}, parent='firmalife:block/greenhouse_wall_panel')
    rm.block_model('greenhouse/%s_panel_wall_up' % name, {'glass': glass + '_up', 'steel': frame}, parent='firmalife:block/greenhouse_wall_panel')
    rm.block_model('greenhouse/%s_panel_wall_both' % name, {'glass': glass, 'steel': frame}, parent='firmalife:block/greenhouse_wall_panel')

    block = rm.blockstate('%s_greenhouse_panel_wall' % name, variants={
        **four_rotations('firmalife:block/greenhouse/%s_panel_wall' % name, (90, None, 180, 270), prefix='down=false,', suffix=',up=false'),
        **four_rotations('firmalife:block/greenhouse/%s_panel_wall_down' % name, (90, None, 180, 270), prefix='down=true,', suffix=',up=false'),
        **four_rotations('firmalife:block/greenhouse/%s_panel_wall_up' % name, (90, None, 180, 270), prefix='down=false,', suffix=',up=true'),
        **four_rotations('firmalife:block/greenhouse/%s_panel_wall_both' % name, (90, None, 180, 270), prefix='down=true,', suffix=',up=true'),
    }).with_lang(lang('%s greenhouse panel wall', name))
    rm.item_model('%s_greenhouse_panel_wall' % name, parent='firmalife:block/greenhouse/%s_panel_wall_both' % name, no_textures=True)
    return block


def greenhouse_panel_roof(rm: ResourceManager, name: str, frame: str, glass: str) -> 'BlockContext':
    rm.block_model('greenhouse/%s_panel_roof' % name, {'glass': glass + '_both', 'steel': frame}, parent='firmalife:block/greenhouse_roof_panel')
    rm.block_model('greenhouse/%s_panel_roof_cw' % name, {'glass': glass + '_both', 'steel': frame}, parent='firmalife:block/greenhouse_roof_panel_cw')
    rm.block_model('greenhouse/%s_panel_roof_ccw' % name, {'glass': glass + '_both', 'steel': frame}, parent='firmalife:block/greenhouse_roof_panel_ccw')
    rm.block_model('greenhouse/%s_panel_roof_both' % name, {'glass': glass + '_both', 'steel': frame}, parent='firmalife:block/greenhouse_roof_panel_both')

    block = rm.blockstate('%s_greenhouse_panel_roof' % name, variants={
        **four_rotations('firmalife:block/greenhouse/%s_panel_roof' % name, (90, None, 180, 270), prefix='cw=false,ccw=false,'),
        **four_rotations('firmalife:block/greenhouse/%s_panel_roof_cw' % name, (90, None, 180, 270), prefix='cw=true,ccw=false,'),
        **four_rotations('firmalife:block/greenhouse/%s_panel_roof_ccw' % name, (90, None, 180, 270), prefix='cw=false,ccw=true,'),
        **four_rotations('firmalife:block/greenhouse/%s_panel_roof_both' % name, (90, None, 180, 270), prefix='cw=true,ccw=true,')
    }).with_lang(lang('%s greenhouse panel roof', name))
    rm.item_model('%s_greenhouse_panel_roof' % name, parent='firmalife:block/greenhouse/%s_panel_roof' % name, no_textures=True)
    return block


def greenhouse_trapdoor(rm: ResourceManager, name: str, glass: str) -> 'BlockContext':
    rm.block('%s_greenhouse' % name).make_trapdoor(texture=glass)
    block = rm.block('%s_greenhouse_trapdoor' % name).with_lang(lang('%s greenhouse trapdoor' % name))
    return block


def greenhouse_door(rm: ResourceManager, name: str, bot: str, upper: str) -> 'BlockContext':
    door = '%s_greenhouse_door' % name
    block = rm.block(door).with_lang(lang(door))
    make_door(rm.block('%s_greenhouse' % name), top_texture=upper, bottom_texture=bot)
    rm.item_model(door)
    return block


def water_based_fluid(rm: ResourceManager, name: str):
    rm.blockstate(('fluid', name)).with_block_model({'particle': 'minecraft:block/water_still'}, parent=None).with_lang(lang(name))

    item = rm.custom_item_model(('bucket', name), 'neoforge:fluid_container', {
        'parent': 'neoforge:item/bucket',
        'fluid': 'firmalife:%s' % name
    })
    item.with_lang(lang('%s bucket', name))

    rm.lang('fluid.firmalife.%s' % name, lang(name))

def four_ways(model: str) -> List[Dict[str, Any]]:
    return [
        {'model': model, 'y': 90},
        {'model': model},
        {'model': model, 'y': 180},
        {'model': model, 'y': 270}
    ]

def make_door(block_context: BlockContext, door_suffix: str = '_door', top_texture: Optional[str] = None, bottom_texture: Optional[str] = None) -> 'BlockContext':
    """
    Generates all blockstates and models required for a standard door
    """
    door = block_context.res.join() + door_suffix
    block = block_context.res.join('block/') + door_suffix
    bottom = block + '_bottom'
    top = block + '_top'

    if top_texture is None:
        top_texture = top
    if bottom_texture is None:
        bottom_texture = bottom

    block_context.rm.blockstate(door, variants=door_blockstate(block))
    for model in ('bottom_left', 'bottom_left_open', 'bottom_right', 'bottom_right_open', 'top_left', 'top_left_open', 'top_right', 'top_right_open'):
        block_context.rm.block_model(door + '_' + model, {'top': top_texture, 'bottom': bottom_texture}, parent='block/door_%s' % model)
    block_context.rm.item_model(door)
    return block_context

def door_blockstate(base: str) -> JsonObject:
    left = base + '_bottom_left'
    left_open = base + '_bottom_left_open'
    right = base + '_bottom_right'
    right_open = base + '_bottom_right_open'
    top_left = base + '_top_left'
    top_left_open = base + '_top_left_open'
    top_right = base + '_top_right'
    top_right_open = base + '_top_right_open'
    return {
        'facing=east,half=lower,hinge=left,open=false': {'model': left},
        'facing=east,half=lower,hinge=left,open=true': {'model': left_open, 'y': 90},
        'facing=east,half=lower,hinge=right,open=false': {'model': right},
        'facing=east,half=lower,hinge=right,open=true': {'model': right_open, 'y': 270},
        'facing=east,half=upper,hinge=left,open=false': {'model': top_left},
        'facing=east,half=upper,hinge=left,open=true': {'model': top_left_open, 'y': 90},
        'facing=east,half=upper,hinge=right,open=false': {'model': top_right},
        'facing=east,half=upper,hinge=right,open=true': {'model': top_right_open, 'y': 270},
        'facing=north,half=lower,hinge=left,open=false': {'model': left, 'y': 270},
        'facing=north,half=lower,hinge=left,open=true': {'model': left_open},
        'facing=north,half=lower,hinge=right,open=false': {'model': right, 'y': 270},
        'facing=north,half=lower,hinge=right,open=true': {'model': right_open, 'y': 180},
        'facing=north,half=upper,hinge=left,open=false': {'model': top_left, 'y': 270},
        'facing=north,half=upper,hinge=left,open=true': {'model': top_left_open},
        'facing=north,half=upper,hinge=right,open=false': {'model': top_right, 'y': 270},
        'facing=north,half=upper,hinge=right,open=true': {'model': top_right_open, 'y': 180},
        'facing=south,half=lower,hinge=left,open=false': {'model': left, 'y': 90},
        'facing=south,half=lower,hinge=left,open=true': {'model': left_open, 'y': 180},
        'facing=south,half=lower,hinge=right,open=false': {'model': right, 'y': 90},
        'facing=south,half=lower,hinge=right,open=true': {'model': right_open},
        'facing=south,half=upper,hinge=left,open=false': {'model': top_left, 'y': 90},
        'facing=south,half=upper,hinge=left,open=true': {'model': top_left_open, 'y': 180},
        'facing=south,half=upper,hinge=right,open=false': {'model': top_right, 'y': 90},
        'facing=south,half=upper,hinge=right,open=true': {'model': top_right_open},
        'facing=west,half=lower,hinge=left,open=false': {'model': left, 'y': 180},
        'facing=west,half=lower,hinge=left,open=true': {'model': left_open, 'y': 270},
        'facing=west,half=lower,hinge=right,open=false': {'model': right, 'y': 180},
        'facing=west,half=lower,hinge=right,open=true': {'model': right_open, 'y': 90},
        'facing=west,half=upper,hinge=left,open=false': {'model': top_left, 'y': 180},
        'facing=west,half=upper,hinge=left,open=true': {'model': top_left_open, 'y': 270},
        'facing=west,half=upper,hinge=right,open=false': {'model': top_right, 'y': 180},
        'facing=west,half=upper,hinge=right,open=true': {'model': top_right_open, 'y': 90}
    }
