from mcresources import ResourceManager

from constants import *


def generate(rm: ResourceManager):
    # https://github.com/emilyploszaj/emi/wiki/Tag-Translation

    def fl_item(tag: str, name: str = None): emi_tag(rm, 'item', 'firmalife', tag, name)
    def fl_fluid(tag: str, name: str = None): emi_tag(rm, 'fluid', 'firmalife', tag, name)

    fl_item('foods/aged_cheeses', 'Aged Cheeses')
    fl_item('foods/bread_slices', 'Bread Slices')
    fl_item('foods/cheese_wheels', 'Cheese Wheels')
    fl_item('foods/cheeses', 'Cheeses')
    fl_item('foods/chocolate', 'Chocolate')
    fl_item('foods/chocolate_blends', 'Chocolate Blends')
    fl_item('foods/cooked_meats_and_substitutes', 'Cooked Meats And Substitutes')
    fl_item('foods/cooked_poultry', 'Cooked Poultry')
    fl_item('foods/egg_noodle_flour', 'Egg Noodle Flours')
    fl_item('foods/filled_wine_bottles', 'Filled Wine Bottles')
    fl_item('foods/flatbreads', 'Flatbreads')
    fl_item('foods/grapes', 'Grapes')
    fl_item('foods/pizza_ingredients', 'Pizza Ingredients')
    fl_item('foods/raw_eggs', 'Raw Eggs')
    fl_item('foods/smashed_grapes', 'Smashed Grapes')
    fl_item('foods/washable', 'Washable Foods')

    fl_item('barrel_presses')
    fl_item('bee_bait', 'Bee Baits')
    fl_item('beehive_frames')
    fl_item('beekeeper_armor')
    fl_item('can_be_pressed_like_grapes', 'Pressable Fruits')
    fl_item('empty_wine_bottles')
    fl_item('filled_beehive_frames')
    fl_item('hangers')
    fl_item('herbs')
    fl_item('jarbnets')
    fl_item('kegs')
    fl_item('oven_fuel', 'Oven Fuels')
    fl_item('pie_pans')
    fl_item('pumpkin_knapping', 'Knappable Pumpkins')
    fl_item('shelves', 'Food Shelves')
    fl_item('smoking_fuel', 'Smoking Fuels')
    fl_item('stomping_barrels')
    fl_item('usable_in_stovetop_soup', 'Stovetop Soup Ingredients')
    fl_item('wine_bottles')
    fl_item('wine_shelves')

    fl_fluid('milks')
    fl_fluid('oils')
    fl_fluid('wine', 'Wines')

    rm.data('emi:tag/exclusions/firmalife', {
        'fluid': ignore_fl(
            # 'usable_in' tags are only really referenced via code
            'usable_in_hollow_shell',
            'usable_in_mixing_bowl',
            'usable_in_vat',
            'usable_in_wine_glass',
        ),
        'item': ignore_fl(
            'can_be_hung',
            'feeds_yeast',
            'foods/dynamic_foods',
            'usable_on_oven',
        )
    }, root_domain='assets')


def emi_tag(rm: ResourceManager, tag_type: str, namespace: str, tag: str, name: str):
    if name is None:
        name = lang(tag)
    rm.lang('tag.%s.%s.%s' % (tag_type, namespace, tag.replace('/', '.')), name)


def ignore_fl(*tags: str) -> list[str]:
    return ['firmalife:%s' % tag for tag in tags]
