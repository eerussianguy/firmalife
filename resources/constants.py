from typing import Dict, List, NamedTuple, Sequence, Optional, Literal, Tuple, Any, Set


SIMPLE_ITEMS = ('peel', 'fruit_leaf', 'cinnamon', 'cinnamon_bark', 'ground_cinnamon', 'beeswax', 'frothy_coconut', 'pineapple_leather', 'pineapple_yarn', 'rennet', 'white_chocolate_blend', 'dark_chocolate_blend', 'milk_chocolate_blend')
BLOCK_ENTITIES = ('oven_bottom', 'oven_top', 'drying_mat')
TFC_FRUITS = ('banana', 'blackberry', 'blueberry', 'bunchberry', 'cherry', 'cloudberry', 'cranberry', 'elderberry', 'gooseberry', 'green_apple', 'lemon', 'olive', 'orange', 'peach', 'plum', 'raspberry', 'red_apple', 'snowberry', 'strawberry', 'wintergreen_berry')

DEFAULT_LANG = {
    'firmalife.tooltip.food_trait.dried': 'Dried',
    'firmalife.jei.drying': 'Drying'
}

def lang(key: str, *args) -> str:
    return ((key % args) if len(args) > 0 else key).replace('_', ' ').replace('/', ' ').title()
