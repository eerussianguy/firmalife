from mcresources import ResourceManager, utils, advancements
from mcresources.advancements import AdvancementCategory
from mcresources.type_definitions import Json

from constants import *


def generate(rm: ResourceManager):
    story = AdvancementCategory(rm, 'story', 'firmalife:textures/block/sealed_bricks.png')

    story.advancement('root', icon('firmalife:empty_jar'), 'Firmalife Story', 'Things to do with Firmalife.', None, root_trigger(), chat=False)
    story.advancement('rennet', icon('firmalife:rennet'), 'A Useful Stomach', 'Kill an animal for some rennet.', 'root', inventory_changed('firmalife:rennet'))
    story.advancement('cheese_wheel', icon('firmalife:chevre_wheel'), 'A Wheel of Cheese', 'Craft your first cheese wheel.', 'rennet', inventory_changed('#firmalife:cheese_wheels'))
    story.advancement('all_cheese', icon('firmalife:food/gouda'), 'Lactose Larry', 'Cut off a slice of every kind of cheese.', 'cheese_wheel', multiple(*[inventory_changed('firmalife:food/%s' % c, name=c) for c in ('gouda', 'chevre', 'cheddar', 'feta', 'rajya_metok', 'shosha')]), requirements=[[c] for c in ('gouda', 'chevre', 'cheddar', 'feta', 'rayja_metok', 'shosha')], frame='challenge')
    story.advancement('climate_station', icon('firmalife:climate_station'), 'Climate Control', 'Craft a climate station.', 'root', inventory_changed('firmalife:climate_station'))
    story.advancement('cellar', icon('firmalife:sealed_bricks'), 'A Waxy Solution', 'Craft a cellar block.', 'climate_station', inventory_changed('#firmalife:cellar_insulation'))
    story.advancement('big_cellar', icon('firmalife:sealed_door'), 'The Dungeon', 'Activate a cellar of more than 200 blocks.', 'cellar', generic('firmalife:big_cellar', None), frame='goal')
    story.advancement('shelf', icon('firmalife:wood/food_shelf/kapok'), 'Shelve it', 'Craft a food shelf.', 'cellar', inventory_changed('#firmalife:food_shelves'))
    story.advancement('hanger', icon('firmalife:wood/hanger/birch'), 'Hang it', 'Craft a food hanger.', 'cellar', inventory_changed('#firmalife:hangers'))
    story.advancement('greenhouse', icon('firmalife:copper_greenhouse_wall'), 'It\'s like a house, but Green.', 'Craft a greenhouse block.', 'climate_station', inventory_changed('#firmalife:greenhouse'))
    story.advancement('big_greenhouse', icon('firmalife:stainless_steel_greenhouse_wall'), 'Food for Days', 'Activate a stainless steel greenhouse of more than 200 blocks.', 'greenhouse', generic('firmalife:big_stainless_greenhouse', None), frame='goal')
    story.advancement('dribbler', icon('firmalife:dribbler'), 'Dribbler', 'Craft a Dribbler.', 'greenhouse', inventory_changed('firmalife:dribbler'))
    story.advancement('sprinkler', icon('firmalife:sprinkler'), 'Sprinkler', 'Craft a Sprinkler.', 'greenhouse', inventory_changed('firmalife:sprinkler'))
    story.advancement('planters', icon('firmalife:large_planter'), 'Planter City', 'Craft every kind of greenhouse planter.', 'greenhouse', multiple(*[inventory_changed('firmalife:%s_planter' % p, name=p) for p in PLANTERS]), requirements=[[p] for p in PLANTERS], frame='goal')
    story.advancement('nutritive_basin', icon('firmalife:nutritive_basin'), 'Balance (TM)', 'Craft a nutritive basin.', 'greenhouse', inventory_changed('firmalife:nutritive_basin'))
    story.advancement('hive', icon('firmalife:beehive'), 'So they just move in on their own?', 'Craft a beehive.', 'root', inventory_changed('firmalife:beehive'))
    story.advancement('wax', icon('firmalife:beeswax'), 'Does this hurt the bee?', 'Get some beeswax from a hive.', 'hive', inventory_changed('firmalife:beeswax'))
    story.advancement('jars', icon('firmalife:empty_jar'), 'minecraft.jar', 'Craft an empty jar.', 'hive', inventory_changed('firmalife:empty_jar'))
    story.advancement('jarbnet', icon('firmalife:wood/jarbnet/palm'), 'Cupholder', 'Craft a jarbnet.', 'jars', inventory_changed('#firmalife:jarbnets'))
    story.advancement('preserves', icon('firmalife:blueberry_jar'), 'Cannery', 'Craft fruit preserves.', 'jars', inventory_changed('#firmalife:foods/preserves'))
    story.advancement('jarring_station', icon('firmalife:jarring_station'), 'Industrial Cannery', 'Craft a jarring station.', 'preserves', inventory_changed('firmalife:jarring_station'), frame='challenge')
    story.advancement('smoker', icon('tfc:food/venison'), 'Up in Smoke', 'Place some string for smoking.', 'root', placed_block('firmalife:wool_string'))
    story.advancement('dry', icon('firmalife:drying_mat'), 'Hydrophobic', 'Craft a drying mat.', 'root', inventory_changed('firmalife:drying_mat'))
    story.advancement('oven', icon('firmalife:oven_bottom'), 'Bread Machine', 'Craft a top and bottom oven, and a chimney.', 'root', multiple(inventory_changed('firmalife:oven_bottom', name='ob'), inventory_changed('firmalife:oven_top', name='ot'), inventory_changed('firmalife:oven_chimney', name='oc')), requirements=[['ot'], ['oc'], ['ob']])
    story.advancement('oven_hopper', icon('firmalife:oven_hopper'), 'Hop it', 'Craft an oven hopper.', 'oven', inventory_changed('firmalife:oven_hopper'))
    story.advancement('ashtray', icon('firmalife:ashtray'), 'Free Fertilizer', 'Craft an ashtray.', 'oven', inventory_changed('firmalife:ashtray'))
    story.advancement('vat', icon('firmalife:vat'), 'A Large Vat', 'Craft a vat.', 'oven', inventory_changed('firmalife:vat'))
    story.advancement('stovetop_pot', icon('tfc:ceramic/pot'), 'Stovetop Pot', 'Put a pot on a bottom oven.', 'oven', generic('firmalife:stovetop_pot', None))
    story.advancement('stovetop_grill', icon('tfc:wrought_iron_grill'), 'Stovetop Grill', 'Put a grill on a bottom oven.', 'oven', generic('firmalife:stovetop_grill', None))
    story.advancement('oven_finishes', icon('firmalife:oven_insulation'), 'Finish him!', 'Craft all oven finishes, and oven insulation.', 'oven', multiple(*[inventory_changed('firmalife:%s' % c, name=c) for c in ('oven_insulation', 'rustic_finish', 'stone_finish', 'tile_finish')]), requirements=[[c] for c in ('oven_insulation', 'rustic_finish', 'stone_finish', 'tile_finish')])
    story.advancement('mixer', icon('firmalife:mixing_bowl'), 'Mixer', 'Craft a mixing bowl.', 'wax', inventory_changed('firmalife:mixing_bowl'))
    story.advancement('chocolate', icon('firmalife:food/dark_chocolate'), 'Chocolatier', 'Make some chocolate.', 'mixer', inventory_changed('#firmalife:foods/chocolate'))
    story.advancement('chromite', icon('firmalife:ore/small_chromite'), 'Shiny and Chrome', 'Find some chromite.', 'root', inventory_changed('firmalife:ore/small_chromite'))
    story.advancement('iron_composter', icon('firmalife:iron_composter'), 'Rusty Composter', 'Craft an iron composter', 'root', inventory_changed('firmalife:iron_composter'))
    story.advancement('pizza', icon('firmalife:food/cooked_pizza'), 'Pizzeria', 'Bake some pizza in an oven.', 'oven', inventory_changed('firmalife:food/cooked_pizza'))
    story.advancement('pie', icon('firmalife:food/cooked_pie'), 'Pie in the Face', 'Bake a pie.', 'oven', inventory_changed('firmalife:food/cooked_pie'))
    story.advancement('burrito_taco', icon('firmalife:food/burrito'), 'Taqueria', 'Make a burrito and a taco.', 'oven', multiple(inventory_changed('firmalife:food/taco', name='taco'), inventory_changed('firmalife:food/burrito', name='burrito')), requirements=[['taco'], ['burrito']], frame='challenge')
    story.advancement('baller', icon('firmalife:seed_ball'), 'Baller', 'Craft a seed ball.', 'root', inventory_changed('firmalife:seed_ball'))
    story.advancement('pickled_egg', icon('firmalife:food/pickled_egg'), 'You put WHAT in this?', 'Craft a pickled egg.', 'root', inventory_changed('firmalife:food/pickled_egg'))
    story.advancement('bacon', icon('firmalife:food/cooked_bacon'), 'Sizzle', 'Cook some bacon', 'root', inventory_changed('firmalife:food/bacon'))

def kill_mob(mob: str, other: Dict = None) -> Json:
    return generic('minecraft:player_killed_entity', {'entity': [entity_predicate(mob, other)]})

# if the predicate is (and usually it will be) an EntityPredicate.Composite, this should be inside an array.
# EntityPredicate.Composite wraps a vanilla loot event in a trigger event
def entity_predicate(mob: str, other: Dict = None) -> Json:
    dic = {
        'condition': 'minecraft:entity_properties',
        'predicate': {'type': mob},  # can be a hashtag to refer to entity tags
        'entity': 'this',
    }
    if other is not None:
        dic.update(other)
    return dic


def consume_item(item: str, name: str = 'item_consumed') -> Json:
    if isinstance(item, str) and name == 'item_consumed':
        name = item.split(':')[1]
    return generic('minecraft:consume_item', {'item': utils.item_predicate(item)}, name=name)

def icon(name: str) -> Json:
    return {'item': name}

def biome(biome_name: str) -> Json:
    return generic('minecraft:location', {'location': {'biome': 'tfc:%s' % biome_name}}, name=biome_name)

def multiple(*conditions: Json) -> Json:
    merged = {}
    for c in conditions:
        merged.update(c)
    return merged

def generic(trigger_type: str, conditions: Json, name: str = 'special_condition') -> Json:
    return {name: {'trigger': trigger_type, 'conditions': conditions}}

def inventory_changed(item: str | Json, name: str = 'item_obtained') -> Json:
    if isinstance(item, str) and name == 'item_obtained':
        name = item.split(':')[1]
    return {name: advancements.inventory_changed(item)}

def item_use_on_block(block: str, item: str, name: str = 'item_use_on_block_condition'):
    block_json = {'tag': block[1:]} if block[0] == '#' else {'blocks': [block]}
    return {name: {'trigger': 'minecraft:item_used_on_block', 'conditions': {
        'location': {'block': block_json},
        'item': {'items': [item]}
    }}}

def placed_block(block: str, name: str = 'block_placed_condition') -> Json:
    return {name: {'trigger': 'minecraft:placed_block', 'conditions': {'block': block}}}

def root_trigger() -> Json:
    return {'in_game_condition': {'trigger': 'minecraft:tick'}}