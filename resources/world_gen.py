from typing import Union, get_args

from mcresources import ResourceManager, utils
from mcresources.type_definitions import ResourceIdentifier, JsonObject, Json, VerticalAnchor

from constants import *

POOR = 70, 25, 5  # = 1550
NORMAL = 35, 40, 25  # = 2400
RICH = 15, 25, 60  # = 2550

class Vein(NamedTuple):
    ore: str  # The name of the ore (as found in ORES)
    vein_type: str  # Either 'cluster', 'pipe' or 'disc'
    rarity: int
    size: int
    min_y: int
    max_y: int
    density: float
    grade: tuple[int, int, int]  # (poor, normal, rich) weights
    rocks: tuple[str, ...]  # Rock, or rock categories
    biomes: str | None
    height: int
    radius: int
    deposits: bool
    indicator_rarity: int  # Above-ground indicators
    underground_rarity: int  # Underground indicators
    underground_count: int
    project: bool | None  # Project to surface
    project_offset: bool | None  # Project offset
    near_lava: bool | None

    @staticmethod
    def new(
        ore: str,
        rarity: int,
        size: int,
        min_y: int,
        max_y: int,
        density: float,
        rocks: tuple[str, ...],

        vein_type: str = 'cluster',
        grade: tuple[int, int, int] = (),
        biomes: str = None,
        height: int = 2,  # For disc type veins, `size` is the width
        radius: int = 5,  # For pipe type veins, `size` is the height
        deposits: bool = False,
        indicator: int = 12,  # Indicator rarity
        deep_indicator: tuple[int, int] = (1, 0),  # Pair of (rarity, count) for underground indicators
        project: str | bool = None,  # Projects to surface. Either True or 'offset'
        near_lava: bool | None = None,
    ):
        assert 0 < density < 1
        assert isinstance(rocks, tuple), 'Forgot the trailing comma in a single element tuple: %s' % repr(rocks)
        assert vein_type in ('cluster', 'disc', 'pipe')
        assert project is None or project is True or project == 'offset'

        underground_rarity, underground_count = deep_indicator
        return Vein(ore, 'tfc:%s_vein' % vein_type, rarity, size, min_y, max_y, density, grade, rocks, biomes, height, radius, deposits, indicator, underground_rarity, underground_count, None if project is None else True, None if project != 'offset' else True, near_lava)

    def config(self) -> dict[str, Any]:
        cfg = {
            'rarity': self.rarity,
            'density': self.density,
            'min_y': self.min_y,
            'max_y': self.max_y,
            'project': self.project,
            'project_offset': self.project_offset,
            'biomes': self.biomes,
            'near_lava': self.near_lava,
        }
        if self.vein_type == 'tfc:cluster_vein':
            cfg.update(size=self.size)
        elif self.vein_type == 'tfc:pipe_vein':
            cfg.update(min_skew=5, max_skew=13, min_slant=0, max_slant=2, sign=0, height=self.size, radius=self.radius)
        else:
            cfg.update(size=self.size, height=self.height)
        return cfg

ORE_VEINS: Dict[str, Vein] = {
    'normal_chromite': Vein.new('chromite', 24, 20, 40, 130, 0.25, ('igneous_extrusive', 'metamorphic'), grade=POOR, deposits=True, indicator=14),
    'deep_chromite': Vein.new('chromite', 45, 40, -80, 20, 0.6, ('igneous_intrusive', 'metamorphic'), grade=RICH, indicator=0, deep_indicator=(1, 4)),
}

def generate(rm: ResourceManager):
    placed_feature_tag(rm, 'tfc:in_biome/veins', *['firmalife:vein/%s' % v for v in ORE_VEINS.keys()])

    for vein_name, vein in ORE_VEINS.items():
        rocks = expand_rocks(vein.rocks)
        configured_placed_feature(rm, ('vein', vein_name), vein.vein_type, {
            **vein.config(),
            'random_name': vein_name,
            'blocks': [{
                'replace': ['tfc:rock/raw/%s' % rock],
                'with': vein_ore_blocks(vein, rock)
            } for rock in rocks],
            'indicator': {
                'rarity': vein.indicator_rarity,
                'depth': 35,
                'underground_rarity': vein.underground_rarity,
                'underground_count': vein.underground_count,
                'blocks': [{
                    'block': 'firmalife:ore/small_%s' % vein.ore
                }]
            },
        })

    for fruit, info in FRUITS.items():
        config = {
            'min_temperature': info.min_temp,
            'max_temperature': info.max_temp,
            'min_rainfall': info.min_rain,
            'max_rainfall': info.max_rain,
            'max_forest': 'normal'
        }
        feature = 'firmalife:fruit_trees'
        state = 'firmalife:plant/%s_growing_branch' % fruit
        configured_placed_feature(rm, ('plant', fruit), feature, {'state': state}, ('tfc:climate', config), decorate_heightmap('world_surface_wg'), decorate_square(), decorate_chance(50))

        placed_feature_tag(rm, 'tfc:feature/fruit_trees', 'firmalife:plant/%s' % fruit, 'firmalife:plant/%s' % fruit)

    configured_patch_feature(rm, 'hollow_shell', patch_config('firmalife:hollow_shell[fluid=empty]', 1, 15, 5, 'salt'), decorate_chance(20), decorate_square(), decorate_climate(-30, 20, 150, 500))
    placed_feature_tag(rm, 'tfc:feature/shore_decorations', 'firmalife:hollow_shell')

    for berry, data in STILL_BUSHES.items():
        bush_block = 'firmalife:plant/%s_bush[lifecycle=healthy,stage=0]' % berry
        configured_patch_feature(rm, ('plant', berry + '_bush'), patch_config(bush_block, 1, 4, 4, False), decorate_climate(data[2], data[3], data[0], data[1], min_forest='sparse'), decorate_square(), decorate_chance(30), biome_check=False)
        placed_feature_tag(rm, 'tfc:feature/berry_bushes', 'firmalife:plant/%s_bush_patch' % berry)


Heightmap = Literal['motion_blocking', 'motion_blocking_no_leaves', 'ocean_floor', 'ocean_floor_wg', 'world_surface', 'world_surface_wg']

class PatchConfig(NamedTuple):
    block: str
    y_spread: int
    xz_spread: int
    tries: int
    any_water: bool
    salt_water: bool
    custom_feature: str
    custom_config: Json


def decorate_climate(min_temp: Optional[float] = None, max_temp: Optional[float] = None, min_rain: Optional[float] = None, max_rain: Optional[float] = None, needs_forest: Optional[bool] = False, fuzzy: Optional[bool] = None, min_forest: Optional[str] = None, max_forest: Optional[str] = None) -> Json:
    return {
        'type': 'tfc:climate',
        'min_temperature': min_temp,
        'max_temperature': max_temp,
        'min_rainfall': min_rain,
        'max_rainfall': max_rain,
        'min_forest': 'normal' if needs_forest else min_forest,
        'max_forest': max_forest,
        'fuzzy': fuzzy
    }

def patch_config(block: str, y_spread: int, xz_spread: int, tries: int = 64, water: Union[bool, Literal['salt']] = False, custom_feature: Optional[str] = None, custom_config: Json = None) -> PatchConfig:
    return PatchConfig(block, y_spread, xz_spread, tries, water == 'salt' or water == True, water == 'salt', custom_feature, custom_config)

def configured_patch_feature(rm: ResourceManager, name_parts: ResourceIdentifier, patch: PatchConfig, *patch_decorators: Json, extra_singular_decorators: Optional[List[Json]] = None, biome_check: bool = True):
    feature = 'minecraft:simple_block'
    config = {'to_place': {'type': 'minecraft:simple_state_provider', 'state': utils.block_state(patch.block)}}
    singular_decorators = []

    if patch.any_water:
        feature = 'tfc:block_with_fluid'
        if patch.salt_water:
            singular_decorators.append(decorate_matching_blocks('tfc:fluid/salt_water'))
        else:
            singular_decorators.append(decorate_air_or_empty_fluid())
    else:
        singular_decorators.append(decorate_replaceable())

    if patch.custom_feature is not None:
        assert patch.custom_config
        feature = patch.custom_feature
        config = patch.custom_config

    heightmap: Heightmap = 'world_surface_wg'
    if patch.any_water:
        heightmap = 'ocean_floor_wg'
        singular_decorators.append(decorate_would_survive_with_fluid(patch.block))
    else:
        singular_decorators.append(decorate_would_survive(patch.block))

    if extra_singular_decorators is not None:
        singular_decorators += extra_singular_decorators
    if biome_check:
        patch_decorators = [*patch_decorators, decorate_biome()]

    res = utils.resource_location(rm.domain, name_parts)
    patch_feature = res.join() + '_patch'
    singular_feature = utils.resource_location(rm.domain, name_parts)

    rm.configured_feature(patch_feature, 'minecraft:random_patch', {
        'tries': patch.tries,
        'xz_spread': patch.xz_spread,
        'y_spread': patch.y_spread,
        'feature': singular_feature.join()
    })
    rm.configured_feature(singular_feature, feature, config)
    rm.placed_feature(patch_feature, patch_feature, *patch_decorators)
    rm.placed_feature(singular_feature, singular_feature, decorate_heightmap(heightmap), *singular_decorators)

def decorate_matching_blocks(*blocks: str) -> Json:
    return decorate_block_predicate({
        'type': 'matching_blocks',
        'blocks': list(blocks)
    })

def decorate_biome() -> Json:
    return 'tfc:biome'

def decorate_would_survive(block: str) -> Json:
    return decorate_block_predicate({
        'type': 'would_survive',
        'state': utils.block_state(block)
    })


def decorate_would_survive_with_fluid(block: str) -> Json:
    return decorate_block_predicate({
        'type': 'tfc:would_survive_with_fluid',
        'state': utils.block_state(block)
    })

def decorate_replaceable() -> Json:
    return decorate_block_predicate({'type': 'tfc:replaceable'})

def decorate_air_or_empty_fluid() -> Json:
    return decorate_block_predicate({'type': 'tfc:air_or_empty_fluid'})


def decorate_block_predicate(predicate: Json) -> Json:
    return {
        'type': 'block_predicate_filter',
        'predicate': predicate
    }

def decorate_heightmap(heightmap: Heightmap) -> Json:
    assert heightmap in get_args(Heightmap)
    return 'minecraft:heightmap', {'heightmap': heightmap.upper()}

def decorate_square() -> Json:
    return 'minecraft:in_square'

def decorate_chance(rarity_or_probability: Union[int, float]) -> Json:
    return {'type': 'minecraft:rarity_filter', 'chance': round(1 / rarity_or_probability) if isinstance(rarity_or_probability, float) else rarity_or_probability}

def placed_feature_tag(rm: ResourceManager, name_parts: ResourceIdentifier, *values: ResourceIdentifier):
    return rm.tag(name_parts, 'worldgen/placed_feature', *values)

def configured_feature_tag(rm: ResourceManager, name_parts: ResourceIdentifier, *values: ResourceIdentifier):
    return rm.tag(name_parts, 'worldgen/configured_feature', *values)

def biome_tag(rm: ResourceManager, name_parts: ResourceIdentifier, *values: ResourceIdentifier):
    return rm.tag(name_parts, 'worldgen/biome', *values)

def vein_ore_blocks(vein: Vein, rock: str) -> List[Dict[str, Any]]:
    poor, normal, rich = vein.grade
    ore_blocks = [{
        'weight': poor,
        'block': 'firmalife:ore/poor_%s/%s' % (vein.ore, rock)
    }, {
        'weight': normal,
        'block': 'firmalife:ore/normal_%s/%s' % (vein.ore, rock)
    }, {
        'weight': rich,
        'block': 'firmalife:ore/rich_%s/%s' % (vein.ore, rock)
    }]
    return ore_blocks

def expand_rocks(rocks: list[str]) -> list[str]:
    assert all(r in ROCKS or r in ROCK_CATEGORIES for r in rocks)
    return [
        rock
        for spec in rocks
        for rock in ([spec] if spec in ROCKS else [r for r, d in ROCKS.items() if d.category == spec])
    ]


def vein_density(density: int) -> float:
    assert 0 <= density <= 100, 'Invalid density: %s' % str(density)
    return round(density * 0.01, 2)

def configured_placed_feature(rm: ResourceManager, name_parts: ResourceIdentifier, feature: Optional[ResourceIdentifier] = None, config: JsonObject = None, *placements: Json):
    res = utils.resource_location(rm.domain, name_parts)
    if feature is None:
        feature = res
    rm.configured_feature(res, feature, config)
    rm.placed_feature(res, res, *placements)

