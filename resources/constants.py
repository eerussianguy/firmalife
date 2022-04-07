from typing import Dict, List, NamedTuple, Sequence, Optional, Literal, Tuple, Any, Set


SIMPLE_ITEMS = ('peel', 'fruit_leaf')
BLOCK_ENTITIES = ('oven_bottom', 'oven_top')
TFC_FRUITS = ('banana', 'blackberry', 'blueberry', 'bunchberry', 'cherry', 'cloudberry', 'cranberry', 'elderberry', 'gooseberry', 'green_apple', 'lemon', 'olive', 'orange', 'peach', 'plum', 'raspberry', 'red_apple', 'snowberry', 'strawberry', 'wintergreen_berry')

DEFAULT_LANG = {
    'firmalife.tooltip.food_trait.dried': 'Dried',
    'firmalife.block_entity.oven_bottom': 'Bottom Oven',
    'firmalife.block_entity.oven_top': 'Top Oven'
}

def lang(key: str, *args) -> str:
    return ((key % args) if len(args) > 0 else key).replace('_', ' ').replace('/', ' ').title()
