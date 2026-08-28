from mcresources import ResourceManager
from mcresources.advancements import AdvancementCategory, inventory_changed
from mcresources.type_definitions import Json

from constants import *


def generate(rm: ResourceManager):
    story = AdvancementCategory(rm, 'story', 'firmalife:textures/block/sealed_bricks.png')

    story.advancement('root', icon('tfc:empty_jar_with_lid'), 'Firmalife Story', 'Things to do with Firmalife.', None, root_trigger(), chat=False)
    story.advancement('rennet', icon('firmalife:rennet'), 'A Useful Stomach', 'Kill an animal for some rennet.', 'root', inventory_changed('firmalife:rennet'))
    story.advancement('cheese_wheel', icon('firmalife:chevre_wheel'), 'A Wheel of Cheese', 'Craft your first cheese wheel.', 'rennet', inventory_changed('#firmalife:cheese_wheels'))
    story.advancement('all_cheese', icon('firmalife:food/gouda'), 'Lactose Larry', 'Cut off a slice of every kind of cheese.', 'cheese_wheel', multiple(*[inventory_changed('firmalife:food/%s' % c, name=c) for c in ('gouda', 'chevre', 'cheddar', 'feta', 'rajya_metok', 'shosha', 'blue_cheese')]), requirements=[[c] for c in ('gouda', 'chevre', 'cheddar', 'feta', 'rajya_metok', 'shosha', 'blue_cheese')], frame='challenge')
    story.advancement('climate_station', icon('firmalife:climate_station'), 'Climate Control', 'Craft a climate station.', 'root', inventory_changed('firmalife:climate_station'))
    story.advancement('cellar', icon('firmalife:sealed_bricks'), 'A Waxy Solution', 'Craft a cellar block.', 'climate_station', inventory_changed('#firmalife:cellar_insulation'))
    story.advancement('big_cellar', icon('firmalife:sealed_door'), 'The Dungeon', 'Activate a cellar of more than 200 blocks.', 'cellar', generic('firmalife:big_cellar', None), frame='goal')
    story.advancement('shelf', icon('firmalife:wood/food_shelf/kapok'), 'Shelve it', 'Craft a food shelf.', 'cellar', inventory_changed('#firmalife:food_shelves'))
    story.advancement('hanger', icon('firmalife:wood/hanger/birch'), 'Hang it', 'Craft a food hanger.', 'cellar', inventory_changed('#firmalife:hangers'))
    story.advancement('greenhouse', icon('firmalife:copper_greenhouse_wall'), 'It\'s like a house, but Green.', 'Craft a greenhouse block.', 'climate_station', inventory_changed('#firmalife:greenhouse'))
    story.advancement('big_greenhouse', icon('firmalife:stainless_steel_greenhouse_wall'), 'Food for Days', 'Activate a stainless steel greenhouse of more than 200 blocks.', 'greenhouse', generic('firmalife:big_stainless_greenhouse', None), frame='goal')
    story.advancement('sprinkler', icon('firmalife:sprinkler'), 'Sprinkler', 'Craft a Sprinkler.', 'greenhouse', inventory_changed('firmalife:sprinkler'))
    story.advancement('planters', icon('firmalife:large_planter'), 'Planter City', 'Craft every kind of greenhouse planter.', 'greenhouse', multiple(*[inventory_changed('firmalife:%s_planter' % p, name=p) for p in PLANTERS]), requirements=[[p] for p in PLANTERS], frame='goal')
    story.advancement('hive', icon('firmalife:beehive'), 'Honey Machine', 'Craft a beehive.', 'root', inventory_changed('firmalife:beehive'))
    story.advancement('wax', icon('firmalife:beeswax'), 'Does this hurt the bee?', 'Get some beeswax from a hive.', 'hive', inventory_changed('firmalife:beeswax'))
    story.advancement('jars', icon('tfc:empty_jar_with_lid'), 'minecraft.jar', 'Craft an empty jar.', 'hive', inventory_changed('tfc:empty_jar_with_lid'))
    story.advancement('jarbnet', icon('firmalife:wood/jarbnet/palm'), 'Cupholder', 'Craft a jarbnet.', 'jars', inventory_changed('#firmalife:jarbnets'))
    story.advancement('jarring_station', icon('firmalife:jarring_station'), 'Industrial Cannery', 'Craft a jarring station.', 'jars', inventory_changed('firmalife:jarring_station'), frame='challenge')
    story.advancement('smoker', icon('tfc:food/venison'), 'Up in Smoke', 'Place some string for smoking.', 'root', placed_block('firmalife:wool_string'))
    story.advancement('dry', icon('firmalife:drying_mat'), 'Hydrophobic', 'Craft a drying mat.', 'root', inventory_changed('firmalife:drying_mat'))
    story.advancement('oven', icon('firmalife:clay_oven_bottom'), 'Bread Machine', 'Craft a top and bottom oven, and a chimney.', 'root', multiple(inventory_changed('firmalife:clay_oven_bottom', name='ob'), inventory_changed('firmalife:clay_oven_top', name='ot'), inventory_changed('firmalife:clay_oven_chimney', name='oc')), requirements=[['ot'], ['oc'], ['ob']])
    story.advancement('oven_hopper', icon('firmalife:clay_oven_hopper'), 'Hop it', 'Craft an oven hopper.', 'oven', inventory_changed('firmalife:clay_oven_hopper'))
    story.advancement('ashtray', icon('firmalife:ashtray'), 'Free Fertilizer', 'Craft an ashtray.', 'oven', inventory_changed('firmalife:ashtray'))
    story.advancement('vat', icon('firmalife:vat'), 'A Large Vat', 'Craft a vat.', 'oven', inventory_changed('firmalife:vat'))
    story.advancement('stovetop_pot', icon('tfc:ceramic/pot'), 'Stovetop Pot', 'Put a pot on a bottom oven.', 'oven', generic('firmalife:stovetop_pot', None))
    story.advancement('stovetop_grill', icon('tfc:wrought_iron_grill'), 'Stovetop Grill', 'Put a grill on a bottom oven.', 'oven', generic('firmalife:stovetop_grill', None))
    story.advancement('oven_finishes', icon('firmalife:oven_insulation'), 'Finish him!', 'Craft all oven finishes, and oven insulation.', 'oven', multiple(*[inventory_changed('firmalife:%s' % c, name=c) for c in ('oven_insulation', 'rustic_finish', 'stone_finish', 'tile_finish')]), requirements=[[c] for c in ('oven_insulation', 'rustic_finish', 'stone_finish', 'tile_finish')])
    story.advancement('mixer', icon('firmalife:mixing_bowl'), 'Mixer', 'Craft a mixing bowl.', 'wax', inventory_changed('firmalife:mixing_bowl'))
    story.advancement('chocolate', icon('firmalife:food/dark_chocolate'), 'Chocolatier', 'Make some chocolate.', 'mixer', inventory_changed('#firmalife:foods/chocolate'))
    story.advancement('chromite', icon('firmalife:ore/small_chromite'), 'Shiny and Chrome', 'Find some chromite.', 'root', inventory_changed('firmalife:ore/small_chromite'))
    story.advancement('compost_tumbler', icon('firmalife:compost_tumbler'), 'Rusty Composter', 'Craft a composter tumbler', 'root', inventory_changed('firmalife:compost_tumbler'))
    story.advancement('pizza', icon('firmalife:food/cooked_pizza'), 'Pizzeria', 'Bake some pizza in an oven.', 'oven', inventory_changed('firmalife:food/cooked_pizza'))
    story.advancement('pie', icon('firmalife:food/cooked_pie'), 'Pie in the Face', 'Bake a pie.', 'oven', inventory_changed('firmalife:food/cooked_pie'))
    story.advancement('burrito_taco', icon('firmalife:food/burrito'), 'Taqueria', 'Make a burrito and a taco.', 'oven', multiple(inventory_changed('firmalife:food/taco', name='taco'), inventory_changed('firmalife:food/burrito', name='burrito')), requirements=[['taco'], ['burrito']], frame='challenge')
    story.advancement('baller', icon('firmalife:seed_ball'), 'Baller', 'Craft a seed ball.', 'root', inventory_changed('firmalife:seed_ball'))
    story.advancement('pickled_egg', icon('firmalife:food/pickled_egg'), 'You put WHAT in this?', 'Craft a pickled egg.', 'root', inventory_changed('firmalife:food/pickled_egg'))
    story.advancement('bacon', icon('firmalife:food/cooked_bacon'), 'Sizzle', 'Cook some bacon', 'root', inventory_changed('firmalife:food/bacon'))


def icon(name: str) -> Json:
    return {'id': name}

def multiple(*conditions: Json) -> Json:
    merged = {}
    for c in conditions:
        merged.update(c)
    return merged

def generic(trigger_type: str, conditions: Json, name: str = 'special_condition') -> Json:
    return {name: {'trigger': trigger_type, 'conditions': conditions}}

def placed_block(block: str, name: str = 'block_placed_condition') -> Json:
    return {name: {'trigger': 'minecraft:placed_block', 'conditions': {'location': [{'block': block, 'condition': 'minecraft:block_state_property'}]}}}

def root_trigger() -> Json:
    return {'in_game_condition': {'trigger': 'minecraft:tick'}}