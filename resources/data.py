from mcresources import ResourceManager

from constants import *
from mcresources import utils


def generate(rm: ResourceManager):
    rm.item_tag('usable_on_oven', 'firmalife:peel')
    rm.item_tag('sweetener', 'minecraft:sugar', 'firmalife:raw_honey')

    rm.block_tag('oven_insulation', 'minecraft:bricks', '#tfc:forge_insulation', '#firmalife:oven_blocks')
    rm.block_tag('minecraft:mineable/pickaxe', '#firmalife:oven_blocks')
    rm.block_tag('planters', *['firmalife:%s_planter' % p for p in PLANTERS])
    rm.block_tag('bee_restoration_plants', *['tfc:plant/%s' % p for p in TFC_FLOWERS])
    rm.block_tag('bee_restoration_water_plants', *['tfc:plant/%s' % p for p in TFC_FLOATING_FLOWERS])
    rm.block_tag('bee_plants', '#firmalife:bee_restoration_plants', '#firmalife:bee_restoration_water_plants', '#firmalife:planters')
    rm.block_tag('all_iron_greenhouse', '#firmalife:iron_greenhouse', '#firmalife:rusted_iron_greenhouse')
    rm.block_tag('all_copper_greenhouse', *['#firmalife:%s_greenhouse' % g for g in ('exposed_copper', 'weathered_copper', 'copper', 'oxidized_copper')])
    rm.block_tag('all_treated_wood_greenhouse', '#firmalife:treated_wood_greenhouse', '#firmalife:weathered_treated_wood_greenhouse')
    rm.block_tag('greenhouse', '#firmalife:all_iron_greenhouse', '#firmalife:all_copper_greenhouse', '#firmalife:all_treated_wood_greenhouse', '#firmalife:stainless_steel_greenhouse')

    greenhouse(rm, 'treated_wood', '#firmalife:all_treated_wood_greenhouse', 5)
    greenhouse(rm, 'copper', '#firmalife:all_copper_greenhouse', 10)  # allows grain
    greenhouse(rm, 'iron', '#firmalife:all_iron_greenhouse', 15)  # allows fruit trees
    greenhouse(rm, 'stainless_steel', '#firmalife:stainless_steel_greenhouse', 20)

    for grain in ('barley', 'oat', 'rye', 'wheat', 'rice'):
        simple_plantable(rm, grain, 'nitrogen' if grain == 'barley' else 'phosphorous', 7, planter='large', tier=10)
    simple_plantable(rm, 'maize', 'nitrogen' if grain == 'barley' else 'phosphorous', 4, planter='large', tier=10, firmalife=True)

    simple_plantable(rm, 'beet', 'potassium', 5)
    simple_plantable(rm, 'cabbage', 'nitrogen', 5)
    simple_plantable(rm, 'carrot', 'potassium', 4)
    simple_plantable(rm, 'garlic', 'nitrogen', 4)
    simple_plantable(rm, 'potato', 'potassium', 6)
    simple_plantable(rm, 'onion', 'nitrogen', 6)
    simple_plantable(rm, 'soybean', 'nitrogen', 6)

    simple_plantable(rm, 'green_bean', 'nitrogen', 4, planter='large', firmalife=True)
    simple_plantable(rm, 'tomato', 'potassium', 4, planter='large', firmalife=True)
    simple_plantable(rm, 'sugarcane', 'potassium', 4, planter='large', firmalife=True)
    plantable(rm, 'jute', 'tfc:seeds/jute', 'tfc:jute', 'potassium', 'firmalife:block/crop/jute', 4, 'large')

    bonsai_plantable(rm, 'cherry', 'nitrogen')
    bonsai_plantable(rm, 'green_apple', 'nitrogen')
    bonsai_plantable(rm, 'lemon', 'nitrogen')
    bonsai_plantable(rm, 'olive', 'nitrogen')
    bonsai_plantable(rm, 'orange', 'nitrogen')
    bonsai_plantable(rm, 'peach', 'nitrogen')
    bonsai_plantable(rm, 'plum', 'nitrogen')
    bonsai_plantable(rm, 'red_apple', 'nitrogen')

    hanging_plantable(rm, 'squash', 'tfc:seeds/squash', 'tfc:food/squash', 'potassium')
    hanging_plantable(rm, 'banana', 'tfc:plant/banana_sapling', 'tfc:food/banana', 'nitrogen', tier=15, seed_chance=0.08)


def greenhouse(rm: ResourceManager, name: str, block_ingredient: str, tier: int):
    rm.data(('firmalife', 'greenhouse', name), {
        'ingredient': utils.ingredient(block_ingredient),
        'tier': tier
    })

def hanging_plantable(rm: ResourceManager, name: str, seed: str, crop: str, nutrient: str, tier: int = None, seed_chance: float = 0.5):
    plantable(rm, name, seed, crop, nutrient, 'firmalife:block/crop/%s' % name, 4, 'hanging', tier, seed_chance)

def bonsai_plantable(rm: ResourceManager, name: str, nutrient: str, firmalife: bool = False):
    plantable(rm, name, 'tfc:plant/%s_sapling' % name, 'tfc:food/%s' % name, nutrient, 'tfc:block/fruit_tree/%s' % name if not firmalife else 'firmalife:block/fruit_tree/%s' % name, 0, 'bonsai', 15, 0.08)

def simple_plantable(rm: ResourceManager, name: str, nutrient: str, stages: int, planter: str = 'quad', tier: int = None, firmalife: bool = False):
    plantable(rm, name, 'tfc:seeds/%s' % name, 'tfc:food/%s' % name, nutrient, 'tfc:block/crop/%s' % name if not firmalife else 'firmalife:block/crop/%s' % name, stages, planter, tier)

def plantable(rm: ResourceManager, name: str, seed: str, crop: str, nutrient: str, texture: str, stages: int, planter: str = 'quad', tier: int = None, seed_chance: float = 0.5):
    rm.data(('firmalife', 'plantable', name), {
        'planter': planter,
        'ingredient': utils.ingredient(seed),
        'seed': utils.item_stack(seed),
        'crop': utils.item_stack(crop),
        'nutrient': nutrient,
        'stages': stages,
        'texture': texture,
        'tier': tier,
        'extra_seed_chance': seed_chance
    })


def item_heat(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, heat_capacity: float, melt_temperature: Optional[int] = None):
    if melt_temperature is not None:
        forging_temperature = melt_temperature * 0.6
        welding_temperature = melt_temperature * 0.8
    else:
        forging_temperature = welding_temperature = None
    rm.data(('tfc', 'item_heats', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'heat_capacity': heat_capacity,
        'forging_temperature': forging_temperature,
        'welding_temperature': welding_temperature
    })
