from typing import Dict, List, NamedTuple, Sequence, Optional, Literal, Tuple, Any, Set


SIMPLE_ITEMS = ('peel', 'fruit_leaf', 'cinnamon', 'cinnamon_bark', 'ground_cinnamon', 'beeswax', 'frothy_coconut', 'pineapple_leather', 'pineapple_yarn', 'rennet', 'white_chocolate_blend', 'dark_chocolate_blend', 'milk_chocolate_blend', 'watering_can')
SIMPLE_FOODS = ('frothy_coconut', 'white_chocolate_blend', 'dark_chocolate_blend', 'milk_chocolate_blend')
SIMPLE_SPICES = ('ground_cinnamon', 'cinnamon')
SIMPLE_BLOCKS: Dict[str, str] = {
    #'climate_station': 'minecraft:mineable/axe'
}
BLOCK_ENTITIES = ('oven_bottom', 'oven_top', 'drying_mat')
TFC_FRUITS = ('banana', 'blackberry', 'blueberry', 'bunchberry', 'cherry', 'cloudberry', 'cranberry', 'elderberry', 'gooseberry', 'green_apple', 'lemon', 'olive', 'orange', 'peach', 'plum', 'raspberry', 'red_apple', 'snowberry', 'strawberry', 'wintergreen_berry')

GREENHOUSES = ('rusted_iron', 'iron', 'oxidized_copper', 'weathered_copper', 'exposed_copper', 'copper', 'weathered_treated_wood', 'treated_wood', 'stainless_steel')
GREENHOUSE_BLOCKS = ('roof', 'roof_top', 'wall', 'door')
CLEANING_PAIRS: Dict[str, str] = {
    'rusted_iron': 'iron',
    'oxidized_copper': 'copper',
    'weathered_copper': 'copper',
    'exposed_copper': 'copper',
    'weathered_treated_wood': 'treated_wood'
}

DEFAULT_LANG = {
    'firmalife.tooltip.food_trait.dried': 'Dried',
    'firmalife.greenhouse.valid_block': '§aClimate and skylight valid',
    'firmalife.greenhouse.invalid_block': '§cClimate or skylight not valid',
    'firmalife.greenhouse.wrong_tier': 'To grow this crop, upgrade to a better greenhouse',
    'firmalife.greenhouse.wrong_type': 'This crop does not grow in this kind of planter.',
    'firmalife.greenhouse.found': 'Found a greenhouse of %s blocks',
    
    'firmalife.jei.drying': 'Drying',
}

def lang(key: str, *args) -> str:
    return ((key % args) if len(args) > 0 else key).replace('_', ' ').replace('/', ' ').title()
