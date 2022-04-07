from mcresources import ResourceManager, RecipeContext, utils
from constants import *


def generate(rm: ResourceManager):
    rm.crafting_shaped('crafting/peel', ['X', 'Y'], {'X': 'minecraft:bowl', 'Y': '#forge:rods/wooden'}, 'firmalife:peel').with_advancement('#forge:rods/wooden')



