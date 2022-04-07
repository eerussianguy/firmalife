from typing import Dict, List, NamedTuple, Sequence, Optional, Literal, Tuple, Any, Set


SIMPLE_ITEMS = ('peel', 'fruit_leaf')
BLOCK_ENTITIES = ('oven_bottom', 'oven_top')

def lang(key: str, *args) -> str:
    return ((key % args) if len(args) > 0 else key).replace('_', ' ').replace('/', ' ').title()
