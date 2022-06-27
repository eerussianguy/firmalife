from typing import Dict, List, NamedTuple, Sequence, Optional, Literal, Tuple, Any, Set

OreGrade = NamedTuple('OreGrade', weight=int, grind_amount=int)
RockCategory = Literal['sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive']
Rock = NamedTuple('Rock', category=RockCategory, sand=str)
Vein = NamedTuple('Vein', ore=str, type=str, rarity=int, size=int, min_y=int, max_y=int, density=float, poor=float, normal=float, rich=float, rocks=List[str], spoiler_ore=str, spoiler_rarity=int, spoiler_rocks=List[str], biomes=Optional[str], height=Optional[int], deposits=bool)

SIMPLE_ITEMS = ('peel', 'fruit_leaf', 'cinnamon_bark', 'beeswax', 'pineapple_leather', 'pineapple_yarn', 'raw_honey', 'rennet', 'watering_can', 'treated_lumber', 'beehive_frame', 'empty_jar', 'cheesecloth')
SIMPLE_FOODS = ('frothy_coconut', 'white_chocolate_blend', 'dark_chocolate_blend', 'milk_chocolate_blend', 'tofu', 'soy_mixture', 'yak_curd', 'goat_curd', 'milk_curd', 'cheddar', 'chevre', 'rajya_metok', 'gouda', 'feta', 'shosha')
SIMPLE_SPICES = ('ground_cinnamon', 'cinnamon')
SIMPLE_BLOCKS: Dict[str, str] = {
    'sealed_bricks': 'minecraft:mineable/pickaxe'
}
BLOCK_ENTITIES = ('oven_bottom', 'oven_top', 'drying_mat', 'beehive')
EXTRA_FLUIDS = ('yeast_starter', 'coconut_milk', 'yak_milk', 'goat_milk', 'curdled_yak_milk', 'curdled_goat_milk', 'pina_colada')
JARS: Sequence[Tuple[str, int, str, str]] = (
    ('honey', 1, 'minecraft:block/honey_block_side', 'firmalife:raw_honey'),
    ('compost', 8, 'firmalife:block/potting_soil_wet', 'tfc:compost'),
    ('rotten_compost', 8, 'firmalife:block/rotten_soil', 'tfc:rotten_compost'),
    ('guano', 8, 'minecraft:block/dead_brain_coral_block', 'tfc:groundcover/guano'),
)
CHEESE_WHEELS = ('rajya_metok', 'cheddar', 'gouda', 'feta', 'chevre', 'shosha')

TFC_GRAINS = ('wheat', 'rye', 'barley', 'rice', 'maize', 'oat')
TFC_FRUIT_TREES = ('cherry', 'green_apple', 'lemon', 'olive', 'orange', 'peach', 'plum', 'red_apple')
TFC_FRUITS = ('banana', 'blackberry', 'blueberry', 'bunchberry', 'cherry', 'cloudberry', 'cranberry', 'elderberry', 'gooseberry', 'green_apple', 'lemon', 'olive', 'orange', 'peach', 'plum', 'raspberry', 'red_apple', 'snowberry', 'strawberry', 'wintergreen_berry')
TFC_FLOWERS = ('canna', 'goldenrod', 'allium', 'anthurium', 'houstonia', 'blood_lily', 'blue_orchid', 'blue_ginger', 'butterfly_milkweed', 'black_orchid',
               'dandelion', 'desert_flame', 'field_horsetail', 'grape_hyacinth', 'heliconia', 'kangaroo_paw', 'labrador_tea', 'lady_fern', 'calendula',
               'meads_milkweed', 'nasturtium', 'oxeye_daisy', 'poppy', 'primrose', 'pulsatilla', 'sacred_datura', 'silver_spurflower', 'snapdragon_pink',
               'snapdragon_red', 'snapdragon_white', 'snapdragon_yellow', 'strelitzia', 'sword_fern', 'trillium', 'tropical_milkweed', 'tulip_orange', 'tulip_pink',
               'tulip_red', 'tulip_white')
TFC_FLOATING_FLOWERS = ('duckweed', 'lotus', 'pistia', 'water_canna', 'water_lily')
TFC_ROCKS: Dict[str, Rock] = {
    'granite': Rock('igneous_intrusive', 'white'),
    'diorite': Rock('igneous_intrusive', 'white'),
    'gabbro': Rock('igneous_intrusive', 'black'),
    'shale': Rock('sedimentary', 'black'),
    'claystone': Rock('sedimentary', 'brown'),
    'limestone': Rock('sedimentary', 'white'),
    'conglomerate': Rock('sedimentary', 'green'),
    'dolomite': Rock('sedimentary', 'black'),
    'chert': Rock('sedimentary', 'yellow'),
    'chalk': Rock('sedimentary', 'white'),
    'rhyolite': Rock('igneous_extrusive', 'red'),
    'basalt': Rock('igneous_extrusive', 'red'),
    'andesite': Rock('igneous_extrusive', 'red'),
    'dacite': Rock('igneous_extrusive', 'red'),
    'quartzite': Rock('metamorphic', 'white'),
    'slate': Rock('metamorphic', 'brown'),
    'phyllite': Rock('metamorphic', 'brown'),
    'schist': Rock('metamorphic', 'green'),
    'gneiss': Rock('metamorphic', 'green'),
    'marble': Rock('metamorphic', 'yellow')
}
ROCK_CATEGORIES: List[str] = ['sedimentary', 'metamorphic', 'igneous_extrusive', 'igneous_intrusive']
ORE_GRADES: Dict[str, OreGrade] = {
    'normal': OreGrade(50, 5),
    'poor': OreGrade(30, 3),
    'rich': OreGrade(20, 7)
}

GREENHOUSES = ('rusted_iron', 'iron', 'oxidized_copper', 'weathered_copper', 'exposed_copper', 'copper', 'weathered_treated_wood', 'treated_wood', 'stainless_steel')
GREENHOUSE_BLOCKS = ('roof', 'roof_top', 'wall', 'door')
CLEANING_PAIRS: Dict[str, str] = {
    'rusted_iron': 'iron',
    'oxidized_copper': 'copper',
    'weathered_copper': 'copper',
    'exposed_copper': 'copper',
    'weathered_treated_wood': 'treated_wood'
}
PLANTERS = ('hanging', 'bonsai', 'quad', 'large', 'trellis')


# Default parameters for common ore veins
# rarity, size, min_y, max_y, density, poor, normal, rich
POOR_METAL_ORE = (80, 15, 0, 100, 40, 40, 30, 10)
NORMAL_METAL_ORE = (60, 20, -32, 75, 60, 20, 50, 30)
DEEP_METAL_ORE = (100, 30, -64, 30, 70, 10, 30, 60)
SURFACE_METAL_ORE = (20, 15, 60, 210, 50, 60, 30, 10)

POOR_S_METAL_ORE = (100, 12, 0, 100, 40, 60, 30, 10)
NORMAL_S_METAL_ORE = (70, 15, -32, 60, 60, 20, 50, 30)
DEEP_S_METAL_ORE = (110, 25, -64, 30, 70, 10, 30, 60)

DEEP_MINERAL_ORE = (90, 10, -48, 100, 60, 0, 0, 0)
HIGH_MINERAL_ORE = (90, 10, 0, 210, 60, 0, 0, 0)

def vein(ore: str, vein_type: str, rarity: int, size: int, min_y: int, max_y: int, density: float, poor: float, normal: float, rich: float, rocks: List[str], spoiler_ore: Optional[str] = None, spoiler_rarity: int = 0, spoiler_rocks: List[str] = None, biomes: str = None, height: int = 0, deposits: bool = False):
    # Factory method to allow default values
    return Vein(ore, vein_type, rarity, size, min_y, max_y, density, poor, normal, rich, rocks, spoiler_ore, spoiler_rarity, spoiler_rocks, biomes, height, deposits)


def preset_vein(ore: str, vein_type: str, rocks: List[str], spoiler_ore: Optional[str] = None, spoiler_rarity: int = 0, spoiler_rocks: List[str] = None, biomes: str = None, height: int = 0, preset: Tuple[int, int, int, int, int, int, int, int] = None, deposits: bool = False):
    assert preset is not None
    return Vein(ore, vein_type, preset[0], preset[1], preset[2], preset[3], preset[4], preset[5], preset[6], preset[7], rocks, spoiler_ore, spoiler_rarity, spoiler_rocks, biomes, height, deposits)


ORE_VEINS: Dict[str, Vein] = {
    'normal_chromite': preset_vein('chromite', 'cluster', ['igneous_intrusive', 'metamorphic'], preset=NORMAL_METAL_ORE),
    'surface_chromite': preset_vein('chromite', 'cluster', ['igneous_intrusive', 'metamorphic'], preset=SURFACE_METAL_ORE),
    'deep_chromite': preset_vein('chromite', 'cluster', ['igneous_intrusive', 'metamorphic'], preset=DEEP_METAL_ORE),
}

DEFAULT_LANG = {
    'effect.firmalife.swarm': 'Swarm',
    'firmalife.tooltip.food_trait.dried': 'Dried',
    'firmalife.tooltip.food_trait.aged': 'Aged',
    'firmalife.tooltip.food_trait.vintage': 'Vintage',
    'firmalife.tooltip.food_trait.fresh': 'Fresh',
    'firmalife.tooltip.food_trait.oven_baked': 'Oven Baked',
    'firmalife.tooltip.food_trait.smoked': 'Smoked',
    'firmalife.tooltip.food_trait.rancid_smoked': 'Rancid Smoked',
    'firmalife.cellar.found': 'Found a cellar of %s blocks',
    'firmalife.greenhouse.valid_block': '§aGrowing',
    'firmalife.greenhouse.invalid_block': '§cNot Growing',
    'firmalife.greenhouse.error_unknown': 'Unknown reason for failure to grow',
    'firmalife.greenhouse.no_sky': 'There is not enough sky light to grow',
    'firmalife.greenhouse.climate_invalid': 'This block is not in a valid greenhouse. Try clicking your climate station.',
    'firmalife.greenhouse.air_above': 'Planters need an air block above them to work',
    'firmalife.greenhouse.dehydrated': 'This planter needs to be watered with a Watering Can',
    'firmalife.greenhouse.wrong_tier': 'To grow this crop, upgrade to a better greenhouse',
    'firmalife.greenhouse.wrong_type': 'This crop does not grow in this planter. It grows in a ',
    'firmalife.greenhouse.found': 'Found a greenhouse of %s blocks',
    'firmalife.planter.growth_water': 'Growth: %s, Water: %s',

    'firmalife.bee.queen': 'Queen',
    'firmalife.bee.no_queen': 'No Queen',
    'firmalife.bee.abilities': 'Abilities:',
    'firmalife.bee.ability.hardiness': 'Hardiness %s',
    'firmalife.bee.ability.production': 'Production %s',
    'firmalife.bee.ability.mutant': 'Mutant %s',
    'firmalife.bee.ability.fertility': 'Fertility %s',
    'firmalife.bee.ability.crop_affinity': 'Crop Affinity %s',
    'firmalife.bee.ability.nature_restoration': 'Nature Restoration %s',
    'firmalife.bee.ability.calmness': 'Calmness %s',

    'firmalife.enum.plantertype.hanging': 'Hanging Planter',
    'firmalife.enum.plantertype.trellis': 'Trellis Planter',
    'firmalife.enum.plantertype.bonsai': 'Bonsai Planter',
    'firmalife.enum.plantertype.quad': 'Quad Planter',
    'firmalife.enum.plantertype.large': 'Large Planter',


    'tfc.jei.drying': 'Drying',
    'tfc.jei.smoking': 'Smoking',
}

def lang(key: str, *args) -> str:
    return ((key % args) if len(args) > 0 else key).replace('_', ' ').replace('/', ' ').title()
