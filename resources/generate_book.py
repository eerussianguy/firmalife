import os
from argparse import ArgumentParser
from typing import Mapping

from mcresources import ResourceManager, utils
from mcresources.type_definitions import ResourceIdentifier, JsonObject, ResourceLocation

from constants import *
from i18n import I18n


class LocalInstance:
    INSTANCE_DIR = os.getenv('LOCAL_MINECRAFT_INSTANCE')  # The location of a local .minecraft directory, for testing in external minecraft instance (as hot reloading works much better)

    @staticmethod
    def wrap(rm: ResourceManager):
        def data(name_parts: ResourceIdentifier, data_in: JsonObject):
            return rm.write((LocalInstance.INSTANCE_DIR, '/'.join(utils.str_path(name_parts))), data_in)

        if LocalInstance.INSTANCE_DIR is not None:
            rm.data = data
            return rm
        return None

def main():
    parser = ArgumentParser('generate_book.py')
    parser.add_argument('--translate', type=str, default='en_us')

    args = parser.parse_args()

    rm = ResourceManager('tfc', '../src/main/resources')
    i18n = I18n.create(args.translate)

    print('Writing book')
    make_book(rm, i18n)

    i18n.flush()

    if LocalInstance.wrap(rm):
        print('Copying into local instance at: %s' % LocalInstance.INSTANCE_DIR)
        make_book(rm, I18n.create('en_us'), local_instance=True)

    print('Done')

def make_book(rm: ResourceManager, i18n: I18n, local_instance: bool = False):
    rm.domain = 'tfc'  # DOMAIN CHANGE
    book = Book(rm, 'field_guide', i18n, local_instance)
    book.template('smoking_recipe', custom_component(0, 0, 'SmokingComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('drying_recipe', custom_component(0, 0, 'DryingComponent', {'recipe': '#recipe'}), text_component(0, 45))

    book.category('firmalife', 'Firmalife', 'All about the Firmalife addon', 'firmalife:cured_oven_top', is_sorted=True, entries=(
        entry('cheese', 'Cheese', 'firmalife:food/gouda', pages=(
            text('Making $(thing)cheese$() in Firmalife is a little more involved than in vanilla TFC. There are two new kinds of milk: $(thing)Yak Milk$(), and $(thing)Goat Milk$(). These are obtained from milking the $(l:mechanics/animal_husbandry#yak)Yak$() and $(l:mechanics/animal_husbandry#goat)Goat$(), respectively. Milking the $$(l:mechanics/animal_husbandry#cow)Cow$() still produces the old kind of milk.'),
            text('Like usual, milk must be $(thing)curdled$() first. To curdle milk, you need $(thing)Rennet$(). Rennet comes from the stomach of $(thing)Ruminant$() animals. This includes $(l:mechanics/animal_husbandry#yak)Yaks$(), $(l:mechanics/animal_husbandry#cow)Cows$(), $(l:mechanics/animal_husbandry#sheep)Sheep$(), $(l:mechanics/animal_husbandry#goat)Goats$(), and $(l:mechanics/animal_husbandry#musk_ox)Musk Oxen$(). To curdle milk, seal it in a $(l:mechanics/barrels)Barrel$() with Rennet for 4 hours.'),
            crafting('firmalife:crafting/cheesecloth', text_contents='Curdled milk must be converted to $(thing)Curds$() by sealing it in a barrel with $(thing)Cheesecloth$(). Cheesecloth is not reusable.'),
            crafting('firmalife:crafting/rajya_metok_wheel', text_contents='You are ready to make $(thing)Dry Cheese$() if you wish. You can make $(thing)Rajya Metok$() from $(thing)Yak Curds$(), $(thing)Chevre$() from $(thing)Goat Curds$(), and $(thing)Cheddar$() from $(thing)Milk Curds$().'),
            crafting('firmalife:crafting/chevre_wheel', 'firmalife:crafting/cheddar_wheel'),
            text('Your other option is to make $(thing)Wet Cheeses$(). These are made by sealing the curds in a barrel of $(thing)Salt Water$(). You can make $(thing)Shosha$() from $(thing)Yak Curds$(), $(thing)Feta$() from $(thing)Goat Curds$(), and $(thing)Gouda$() from $(thing)Milk Curds$().'),
            text('Cheese wheels are blocks that should be placed in order to help them last. To improve their quality and shelf life, cheese wheels should be $(thing)Aged$() in a $(l:firmalife/cellar)Cellar$(). In order to obtain edible cheese from a cheese wheel, it should be sliced off the wheel by clicking $(item)$(k:key.use)$() with a $(thing)Knife(). If the block is simply broken, the aging is lost!').anchor('aging'),
            multimultiblock('The aging stages of a wheel of Gouda: $(thing)Fresh$(), $(thing)Aged$(), and $(thing)Vintage$().', *[block_spotlight('', '', 'firmalife:gouda_wheel[age=%s]' % age) for age in ('fresh', 'aged', 'vintage')]),
        )),
        entry('climate_station', 'Climate Station', 'firmalife:climate_station', pages=(
            text('The $(thing)Climate Station$() is a block that manages the $(l:firmalife/greenhouse)Greenhouse$() and the $(l:firmalife/cellar)Cellar$(). When its corresponding multiblock is built correctly, it will show water on its sides. When it it is invalid, it will show ice. The Climate Station must be placed on the first level of the multiblock, touching a wall.'),
            multimultiblock('The climate station in it\'s valid and invalid state.', *[block_spotlight('', '', 'firmalife:climate_station[stasis=%s]' % b) for b in ('true', 'false')]),
            text('$(li)It updates periodically on its own, or when placed/broken.$()$(li)When a climate station updates, it tells all the blocks inside the multiblock that they can operate. For example, it lets $(l:firmalife/cheese)Cheese$() begin aging.$()$(li)Press $(item)$(k:key.use)$() to force update the Climate Station and the blocks inside the multiblock.', 'Climate Station Tips'),
            crafting('firmalife:crafting/climate_station', text_contents='The climate station is crafted like this.'),
        )),
        entry('cellar', 'Cellars', 'firmalife:sealed_bricks', pages=(
            text('The $(thing)Cellar$() is a multiblock device controlled by a $(l:firmalife/climate_station)Climate Station$(). The Cellar multiblock\'s only requirement is that it be in an enclosed area surrounded by $(thing)Sealed Bricks$() or $(thing)Sealed Brick Doors$() on all sides. The Climate Station must be placed on the first level of the cellar, touching a wall.'),
            multiblock('An Example Cellar', 'This is just one of many cellars that you could make!', True, multiblock_id='firmalife:cellar'),
            crafting('firmalife:crafting/sealed_bricks', 'firmalife:crafting/sealed_door'),
            text('$(thing)Beeswax$() is obtained from $(l:firmalife/beekeeping)Beekeeping$().$(br)Cellars are used for $(l:firmalife/cheese#aging)Aging Cheese$().'),
        )),
        entry('greenhouse', 'Greenhouse', 'firmalife:sealed_bricks', pages=(
            text('The $(thing)Greenhouse$() is a multiblock device controlled by a $(l:firmalife/climate_station)Climate Station$(). It allows growing crops year round. The Greenhouse has an array of types and blocks to choose from. However, building a greenhouse is quite simple. Like the $(l:firmalife/cellar)Cellar$(), it should be an enclosed area of blocks belonging to the same $(thing)Greenhouse Type$(). The floor of the greenhouse may be any solid block.'),
            text('$(thing)Greenhouse Types$() are families of greenhouse blocks that can be used interchangeably in a greenhouse. Most greenhouse blocks $(thing)age$(). For example, $(thing)Treated Wood$() greenhouse blocks become $(thing)Weathered Treated Wood$() blocks. Since both of those block types belong to the same greenhouse type, your greenhouse will continue to function.'),
            text('These are the $(thing)Greenhouse Types$() available, with the block types they can age into:$(br)$(br)$(li)Treated Wood: Weathered $()$(li)Copper: Exposed, Weathered, Oxidized$()$(li)Iron: Rusted$(), $(li)Stainless Steel (does not age)$()', 'Greenhouse Types'),
            text('There are four types of $(thing)Greenhouse Blocks$(): Walls, Doors, Roofs, and Roof Tops. Roofs and Roof Tops are stairs and slabs, respectively. These can be combined however you choose to form the structure of the greenhouse.'),
            multimultiblock('An example greenhouse, in each main type.', *[multiblock('', '', True, multiblock_id='firmalife:%s_greenhouse' % g) for g in ('treated_wood', 'copper', 'iron', 'stainless_steel')]),
            text('The next two pages contain recipes for the four main greenhouse block types. While they are only shown for Iron greenhouses, the iron rods in the recipe can be replaced with $(thing)Treated Lumber$(), $(thing)Copper Rods$(), or $(thing)Stainless Steel Rods$(). For information on Stainless Steel, see $(l:firmalife/stainless_steel)this linked page$().'),
            crafting('firmalife:crafting/greenhouse/iron_greenhouse_wall', 'firmalife:crafting/greenhouse/iron_greenhouse_roof'),
            crafting('firmalife:crafting/greenhouse/iron_greenhouse_door', 'firmalife:crafting/greenhouse/iron_greenhouse_roof_top'),
            text('There are many blocks that operate inside Greenhouses:$(br)$(li)$(l:firmalife/planters)Planters$(), for growing crops$()'),
        )),
        entry('planters', 'Planters', 'firmalife:large_planter', pages=(
            text('$(thing)Planters$() are used to grow crops inside a $(l:firmalife/greenhouse)Greenhouse$(). To see the status of a planter, you can look at it while holding a $(thing)Hoe$(). Crops in planters consume $(l:mechanics/fertilizers)Nutrients$() in a similar way to $(l:food/crops)Crops$(). Planters should be placed inside a valid Greenhouse and activated with a $(l:firmalife/climate_station)Climate Station$(). Planters need at least some natural sunlight to work.').anchor('planters'),
            crafting('firmalife:crafting/watering_can', text_contents='Planters must be $(thing)Watered$() to grow. This is done with a $(thing)Watering Can$(), crafted from a $(thing)Wooden Bucket$() of $(thing)Water$() and $(thing)Lumber$(). Press $(item)$(k:key.use)$() with it to water nearby planters. Refill it by pressing $(item)$(k:key.use)$() on a water source.'),
            crafting('firmalife:crafting/large_planter', text_contents='$(thing)Large Planters$() are the most simple kind of planter. They grow a single crop from seed, and are harvested with $(item)$(k:key.use)$() when mature.'),
            text('Large Planters can grow $(thing)Green Beans$(), $(thing)Tomatoes$(), $(thing)Sugarcane$(), $(thing)Jute$(), and $(thing)Grains$(). However, to grow Grains, you need a $(thing)Copper$() or better Greenhouse.'),
            crafting('firmalife:crafting/quad_planter', text_contents='$(thing)Quad Planters$() grow four individual crops at once. These crops all draw from the same nutrient pool, and can be harvested individually with $(item)$(k:key.use)$() when mature.'),
            text('Quad Planters can grow $(thing)Beets$(), $(thing)Cabbage$(), $(thing)Carrots$(), $(thing)Garlic$(), $(thing)Onions$(), $(thing)Potatoes$(), and $(thing)Soybeans$(). These crops can be grown in any greenhouse type.'),
            crafting('firmalife:crafting/bonsai_planter', text_contents='$(thing)Bonsai Planters$() grow small fruit trees from their saplings. The fruit can be picked with $(item)$(k:key.use)$().'),
            text('Bonsai Planters can grow any fruit tree type, except $(thing)Bananas$(), which need a $(thing)Hanging Planter$(). They all consume Nitrogen as their main nutrient. They need an $(thing)Iron$() or better greenhouse to grow.'),
            crafting('firmalife:crafting/hanging_planter', text_contents='$(thing)Hanging Planters$() grow crops upside down. When mature, they can be harvested with $(item)$(k:key.use)$().'),
            text('Hanging Planters grow $(thing)Squash$(), from their seeds, and $(thing)Bananas$(), from their sapling. Squash can be grown in any greenhouse, but Bananas require an $(thing)Iron$() or better greenhouse to grow. Hanging planters need to anchor to a solid block above them.'),
            crafting('firmalife:crafting/trellis_planter', text_contents='$(thing)Trellis Planters$() grow berry bushes. Berries can be picked with $(item)$(k:key.use)$().'),
            text('Trellis Planters have the unique property of $(thing)propagating$() berry bushes. If another trellis planter is placed on top of another, and the one below has a mature berry bush, it has a chance to grow upwards into the next one. Trellis planters can grow any berry bush except $(thing)Cranberries$(), but require an $(thing)Iron$() or better greenhouse to work. Bushes prefer Nitrogen.'),
        )),
        entry('beekeeping', 'Beekeeping', 'firmalife:beehive', pages=(
            text('$(thing)Beehives$() are a place to house bees. Beehives need $(thing)Beehive Frames$() inside them for the bees to leave. Removing frames from an active hive will cause the bees to attack you, unless done at night, or with a $(thing)Firepit$() underneath the hive active.'),
            crafting('firmalife:crafting/beehive', 'firmalife:crafting/beehive_frame'),
            text('Beehives know about the area in a 5 block radius from them. If there are at least 10 flowers around the hive, there is a chance an empty frame will be populated with a $(thing)Queen$(). This is indicated by bee particles flying around the hive.'),
            text('If a beehive has two frames with queens, and an empty frame, the two colonies have a chance of $(thing)Breeding$() and producing a new queen in the empty frame. This has the effect of passing on the $(thing)Abilities$() of each parent to the offspring. Abilities are different traits bees have that change how they effect the world around them. They are on a scale of 1-10, with 10 being the max.'),
            crafting('firmalife:crafting/honey_jar_open', text_contents='Bees also produce $(thing)Honey$(). Using $(item)$(k:key.use)$() with an $(l:firmalife/jar)Empty Jar$() on a hive that visibly has honey gives you a $(thing)Honey Jar$(). Opening a Honey Jar gives you $(thing)Raw Honey$(), a $(thing)Sugar$() substitute.'),
            crafting('firmalife:crafting/scrape_beehive_frame', text_contents='Crafting a filled frame with a $(thing)Knife$() gives you $(thing)Beeswax$(), which has many uses'),
            crafting('firmalife:crafting/treated_lumber', text_contents='The most important use of beeswax is in creating $(thing)Treated Lumber$().'),
            text('$(li)Bees can help fertilize planters!$()$(li)Scraping a frame sacrifices the queen. Be smart!$()$(li)Being wet prevents bees from attacking you.$()', 'Bee Tips'),
            text('$(li)$(thing)Hardiness$(): Allows bees to produce honey at lower temperatures. Hardiness 10 allows up to -16°C, whereas Hardiness 1 allows up to 2°C.$()$(li)$(thing)Production$(): Improves the speed of honey production.$()$(li)$(thing)Mutant$(): Increases variability in the traits passed during breeding$()', 'List of Abilities'),
            text('$(li)$(thing)Fertility$(): Increases likelihood of breeding$()$(li)$(thing)Crop Affinity$(): Likelihood of spreading a small amount of nutrients to crops$()$(li)$(thing)Nature Restoration$()Causes new flowers and lilypads to spawn around the hive$()$(li)$(thing)Calmness$()Decreases likelihood of bees attacking you$()'),
        )),
        entry('jar', 'Jars', 'firmalife:empty_jar', pages=(
            text('$(thing)Jars$() are a way of storing certain items. They can be placed on the ground, in groups of up to four. They are most useful as a way of storing $(l:firmalife/beekeeping)Honey$() indefinitely.'),
            multimultiblock('Each possible arrangement of jars.', *[block_spotlight('', '', 'firmalife:honey_jar[count=%s]' % i) for i in range(1, 5)]),
            crafting('firmalife:crafting/empty_jar', text_contents='It all starts with an $(thing)Empty Jar$(). Jars can be emptied, discarding the contents, by sealing them in a $(l:mechanics/barrels)Barrel$() of Water$().'),
            text('The recipes to fill and empty jars are typically simple crafting recipes. Some jars cannot have their insides removed without a special recipe.$(br)$(br)One use of jars is making $(thing)Fruit Preserves$(). To do this, boil an $(thing)Empty Jar$(), a $(thing)Sweetener$(), and fresh $(thing)Fruit$().'),
            text('$(li)Fruit Preserves$()$(li)Guano Jar$()$(li)Honey Jar$()$(li)Rotten Compost Jar$()$(li)Compost Jar$()', 'Jar Types'),
        )),
        entry('stainless_steel', 'Stainless Steel', 'firmalife:metal/ingot/stainless_steel', pages=(
            text('$(thing)Stainless Steel$() and $(thing)Chromium$() are $(thing)Steel-tier$() metals added by Firmalife. They are used in the construction of $(l:firmalife/greenhouse)Stainless Steel Greenhouses$().'),
            alloy_recipe('Stainless Steel', 'firmalife:metal/ingot/stainless_steel', ('Chromium', 20, 30), ('Nickel', 10, 20), ('Steel', 60, 80), text_content=''),
            item_spotlight('firmalife:ore/small_chromite', text_contents='Chromite is an ore that is melted to obtain Chromium. It is found in $(thing)Igneous Intrusive$() and $(thing)Metamorphic$() rocks.'),
            text('$(li)Granite$()$(li)Diorite$()$(li)Gabbro$()$(li)Slate$()$(li)Phyllite$()$(li)Schist$()$(li)Gneiss$()$(li)Marble$()', 'All Chromium Rocks')
        )),
        entry('drying', 'Drying', 'firmalife:drying_mat', pages=(
            crafting('firmalife:crafting/drying_mat', text_contents='The $(thing)Drying Mat$() is used to dry items. It is made with $(thing)Fruit Leaves$(), which are obtained from breaking the leaves of $(thing)Fruit Trees$().'),
            text('To use the drying mat, place it out on the sun and add an item to it with $(item)$(k:key.use)$(). After a half day, it will be dried. If it rains, the drying process must start over.'),
            drying_recipe('firmalife:drying/drying_fruit', 'Drying fruit is a common use of the drying mat. Dried fruit is used in some recipes, and lasts longer.'),
            drying_recipe('firmalife:drying/tofu', 'Tofu is made using a drying mat.'),
            drying_recipe('firmalife:drying/cinnamon', 'Cinnamon is made using a drying mat.'),
        )),
        entry('smoking', 'Smoking', 'tfc:food/venison', pages=(
            two_tall_block_spotlight('Smoking', 'Wool string is used to hang items for $(thing)Smoking$(). To place it, just use $(item)$(k:key.use)$().', 'tfc:firepit[lit=true]', 'firmalife:wool_string'),
            text('Smoking is used to preserve $(thing)Meat$() and $(thing)Cheese$(). To smoke meat, it must have first been $(thing)Brined$() by sealing it in a $(thing)Barrel$() with $(thing)Brine$(). You may also salt it first. Cheese does not have this requirement.'),
            text('To start the smoking process, add the item to the string above a firepit. The firepit must be within four blocks, directly underneath the string. The string should begin to generate some smoke if it is working. It is important to note that the firepit must only be burned with $(thing)Logs$(). Using something like $(thing)Peat$() will instantly give your food the $(thing)Disgusting$() trait!'),
            text('The smoking process takes 8 in-game hours. Happy smoking!'),
        )),
        entry('ovens', 'Ovens', 'firmalife:cured_top_oven', pages=(
            text('$(thing)Ovens$() are a great way of cooking lots of food in a way that improves their shelf life. Oven-baked food decays at 90% of the rate of regular food. Ovens are a multiblock structure consisting of a $(thing)Bottom Oven$(), $(thing)Top Oven$(), and optionally $(thing)Chimneys$(). These blocks start off as clay, and must be $(thing)Cured$() by raising their temperature to a certain amount for long enough.'),
            clay_knapping('firmalife:clay_knapping/oven_top', 'The recipe for the top oven.'),
            clay_knapping('firmalife:clay_knapping/oven_bottom', 'The recipe for the top oven.'),
            clay_knapping('firmalife:clay_knapping/oven_chimney', 'The recipe for the top oven.'),
            crafting('tfc:crafting/bricks', text_contents='Ovens are insulated with $(thing)Bricks$(), other oven blocks, or anything that can insulate a Forge. This means you can use stone blocks, if you want!'),
            crafting('firmalife:crafting/peel', text_contents='The $(thing)Peel$() is the only safe way to remove hot items from an Oven. Just $(item)$(k:key.use)$() on it while holding it to retrieve items. Otherwise, you may get burned!'),
            text('The Oven first consists of the Top Oven placed on top of the Bottom Oven. All sides of each oven part, besides the front face, should then be covered with Oven Insulation blocks, as covered two pages ago. You may choose to use $(thing)Oven Chimneys$() as insulation. Placing a stack of chimneys directly behind the oven causes the smoke from the oven to travel up and out of it. If you don\'t do this, smoke will quickly fill up your house, which is very distracting!'),
            multimultiblock('An example oven structure, uncured and cured.', *[multiblock('', '', True, (
                ('     ', '  C  '),
                ('     ', '  C  '),
                ('WT0TW', 'WWCWW'),
                ('WBBBW', 'WWCWW'),
            ), {
                '0': 'firmalife:%stop_oven' % pref,
                'T': 'firmalife:%stop_oven' % pref,
                'B': 'firmalife:%sbottom_oven' % pref,
                'W': 'minecraft:bricks',
                'C': 'firmalife:%schimney' % pref,
            }) for pref in ('cured_', '')]),
            text('The Bottom Oven is used to hold fuel, which may only be logs. Press $(item)$(k:key.use)$() to add or remove them. The bottom oven is also the part of the oven which may be lit with a $(thing)Firestarter$() or other tool. It transfers heat contained in it to the top oven.'),
            text('The Top Oven contains the items that are being cooked. It will draw heat from the Bottom Oven and slowly release it over time. This means that even if your fuel runs out, your Top Oven can continue to work for a little while. Adding items to it is as simple as pressing $(item)$(k:key.use)$(). Remember to use a $(thing)Peel$() to remove the items after.'),
            text('Curing Oven blocks is easy, but requires patience. Simply start running your Bottom Oven as you would normally, and then wait. If an oven block is above 600 degrees for about 80 seconds, it will cure itself and any oven blocks around it. The curing effect will pass all the way up chimneys nearby.')
        )),
        entry('bread', 'Bread', 'tfc:food/rye_bread', pages=(
            text('To make $(Thing)Bread$(), one first must get $(thing)Yeast$(). To get your first yeast, seal $(l:firmalife/drying)Dried Fruit$() in a Barrel of $(thing)Water$(). After three days, $(thing)Yeast Starter$() will form.$(br)From now on, your yeast can be fed by sealing Yeast Starter in a Barrel with $(thing)Flour$(). This causes it to multiply. 1 flour per 100mB of Yeast produces 600mB of Yeast. That\'s a good deal!'),
            crafting('tfc:crafting/barley_dough', text_contents='Yeast Starter, Sugar, and Flour can be combined to make $(thing)Dough$(). Dough can be cooked like normal to produce $(thing)Bread!$().'),
        )),
        entry('more_fertilizer', 'More Fertilizer Options', 'firmalife:iron_composter', pages=(
            text('Given a greater need for fertilization in Firmalife, there are more options for getting fertilizers.'),
            drying_recipe('firmalife:drying/dry_grass', 'Thatch can be $(l:firmalife/drying)Dried$() into $(thing)Dry Grass$(), which can be used in a Composter as a brown item.'),
            crafting('firmalife:crafting/iron_composter', 'The Composter can be upgraded to an $(thing)Iron Composter$(), which works the same, except it produces compost four times as fast.'),
            multimultiblock('The possible fill levels of the iron composter', *[block_spotlight('', '', 'firmalife:iron_composter[type=normal,stage=%s]' % i) for i in range(0, 9)]),
        )),
    ))

    rm.domain = 'firmalife'  # DOMAIN RESET

# ==================== Book Resource Generation API Functions =============================


class Component(NamedTuple):
    type: str
    x: int
    y: int
    data: JsonObject


class Page(NamedTuple):
    type: str
    data: JsonObject
    custom: bool  # If this page is a custom template.
    anchor_id: str | None  # Anchor for referencing from other pages
    link_ids: List[str]  # Items that are linked to this page
    translation_keys: Tuple[str, ...]  # Keys into 'data' that need to be passed through the Translation

    def anchor(self, anchor_id: str) -> 'Page':
        return Page(self.type, self.data, self.custom, anchor_id, self.link_ids, self.translation_keys)

    def link(self, *link_ids: str) -> 'Page':
        for link_id in link_ids:
            if link_id.startswith('#'):  # Patchouli format for linking tags
                link_id = 'tag:' + link_id[1:]
            self.link_ids.append(link_id)
        return self

    def translate(self, i18n: I18n):
        for key in self.translation_keys:
            if key in self.data and self.data[key] is not None:
                self.data[key] = i18n.translate(self.data[key])


class Entry(NamedTuple):
    entry_id: str
    name: str
    icon: str
    pages: Tuple[Page]
    advancement: str | None


class Book:

    def __init__(self, rm: ResourceManager, root_name: str, i18n: I18n, local_instance: bool):
        self.rm: ResourceManager = rm
        self.root_name = root_name
        self.category_count = 0
        self.i18n = i18n
        self.local_instance = local_instance

    def template(self, template_id: str, *components: Component):
        self.rm.data(('patchouli_books', self.root_name, 'en_us', 'templates', template_id), {
            'components': [{
                'type': c.type, 'x': c.x, 'y': c.y, **c.data
            } for c in components]
        })

    def category(self, category_id: str, name: str, description: str, icon: str, parent: str | None = None, is_sorted: bool = False, entries: Tuple[Entry, ...] = ()):
        """
        :param category_id: The id of this category.
        :param name: The name of this category.
        :param description: The description for this category. This displays in the category's main page, and can be formatted.
        :param icon: The icon for this category. This can either be an ItemStack String, if you want an item to be the icon, or a resource location pointing to a square texture. If you want to use a resource location, make sure to end it with .png.
        :param parent: The parent category to this one. If this is a sub-category, simply put the name of the category this is a child to here. If not, don't define it. This should be fully-qualified and of the form domain:name where domain is the same as the domain of your Book ID.
        :param is_sorted: If the entries within this category are sorted
        :param entries: A list of entries (call entry() for each)

        https://vazkiimods.github.io/Patchouli/docs/reference/category-json/
        """
        self.rm.data(('patchouli_books', self.root_name, self.i18n.lang, 'categories', category_id), {
            'name': self.i18n.translate(name),
            'description': self.i18n.translate(description),
            'icon': icon,
            'parent': parent,
            'sortnum': self.category_count
        })
        self.category_count += 1

        category_res: ResourceLocation = utils.resource_location(self.rm.domain, category_id)

        assert not isinstance(entries, Entry), 'One entry in singleton entries, did you forget a comma after entry(), ?\n  at: %s' % str(entries)
        for i, e in enumerate(entries):
            assert not isinstance(e.pages, Page), 'One entry in singleton pages, did you forget a comma after page(), ?\n  at: %s' % str(e.pages)

            extra_recipe_mappings = {}
            for index, p in enumerate(e.pages):
                for link in p.link_ids:
                    extra_recipe_mappings[link] = index
            if not extra_recipe_mappings:  # Exclude if there's nothing here
                extra_recipe_mappings = None

            # Validate no duplicate anchors or links
            seen_anchors = set()
            seen_links = set()
            for p in e.pages:
                if p.anchor_id:
                    assert p.anchor_id not in seen_anchors, 'Duplicate anchor "%s" on page %s' % (p.anchor_id, p)
                    seen_anchors.add(p.anchor_id)
                for link in p.link_ids:
                    assert link not in seen_links, 'Duplicate link "%s" on page %s' % (link, p)
                    seen_links.add(link)

            # Separately translate each page
            entry_name = self.i18n.translate(e.name)
            for p in e.pages:
                p.translate(self.i18n)

            self.rm.data(('patchouli_books', self.root_name, self.i18n.lang, 'entries', category_res.path, e.entry_id), {
                'name': entry_name,
                'category': self.prefix(category_res.path),
                'icon': e.icon,
                'pages': [{
                    'type': self.prefix(p.type) if p.custom else p.type,
                    'anchor': p.anchor_id,
                    **p.data
                } for p in e.pages],
                'advancement': e.advancement,
                'read_by_default': True,
                'sortnum': i if is_sorted else None,
                'extra_recipe_mappings': extra_recipe_mappings
            })

    def prefix(self, path: str) -> str:
        """ In a local instance, domains are all under patchouli, otherwise under tfc """
        return ('patchouli' if self.local_instance else 'tfc') + ':' + path


def entry(entry_id: str, name: str, icon: str, advancement: str | None = None, pages: Tuple[Page, ...] = ()) -> Entry:
    """
    :param entry_id: The id of this entry.
    :param name: The name of this entry.
    :param icon: The icon for this entry. This can either be an ItemStack String, if you want an item to be the icon, or a resource location pointing to a square texture. If you want to use a resource location, make sure to end it with .png
    :param advancement: The name of the advancement you want this entry to be locked behind. See Locking Content with Advancements for more info on locking content.
    :param pages: The array of pages for this entry.

    https://vazkiimods.github.io/Patchouli/docs/reference/entry-json/
    """
    return Entry(entry_id, name, icon, pages, advancement)


def text(text_contents: str, title: str | None = None) -> Page:
    """
    Text pages should always be the first page in any entry. If a text page is the first page in an entry, it'll display the header you see in the left page. For all other pages, it'll display as you can see in the right one.
    :param text_contents: The text to display on this page. This text can be formatted.
    :param title An optional title to display at the top of the page. If you set this, the rest of the text will be shifted down a bit. You can't use "title" in the first page of an entry.
    :return:
    """
    return page('patchouli:text', {'text': text_contents, 'title': title}, translation_keys=('text', 'title'))


def image(*images: str, text_contents: str | None = None, border: bool = True) -> Page:
    """
    :param images: An array with images to display. Images should be in resource location format. For example, the value botania:textures/gui/entries/banners.png will point to /assets/botania/textures/gui/entries/banners.png in the resource pack. For best results, make your image file 256 by 256, but only place content in the upper left 200 by 200 area. This area is then rendered at a 0.5x scale compared to the rest of the book in pixel size.
    If there's more than one image in this array, arrow buttons are shown like in the picture, allowing the viewer to switch between images.
    :param text_contents: The text to display on this page, under the image. This text can be formatted.
    :param border: Defaults to false. Set to true if you want the image to be bordered, like in the picture. It's suggested that border is set to true for images that use the entire canvas, whereas images that don't touch the corners shouldn't have it.
    """
    return page('patchouli:image', {'images': images, 'text': text_contents, 'border': border}, translation_keys=('text',))

def entity(entity_type: str, text_contents: str = None, title: str = None, scale: float = 0.7, offset: float = None, rotate: bool = None, default_rotation: float = None) -> Page:
    """
    :param entity_type: The entity type
    :param text_contents: The text to display under the entity display
    :param title: The title of the page
    :param scale: The scale of the entity. Defaults to 1
    :param offset: The vertical offset of the entity renderer. Defaults to 0
    :param rotate: Whether the entity should rotate in the view. Defaults to true.
    :param default_rotation: The rotation at which the entity is displayed. Only used if rotate is False.
    """
    return page('patchouli:entity', {'entity': entity_type, 'scale': scale, 'offset': offset, 'rotate': rotate, 'default_rotation': default_rotation, 'name': title, 'text': text_contents})

def crafting(first_recipe: str, second_recipe: str | None = None, title: str | None = None, text_contents: str | None = None) -> Page:
    """
    :param first_recipe: The ID of the first recipe you want to show.
    :param second_recipe: The ID of the second recipe you want to show. Displaying two recipes is optional.
    :param title: The title of the page, to be displayed above both recipes. This is optional, but if you include it, only this title will be displayed, rather than the names of both recipe output items.
    :param text_contents: The text to display on this page, under the recipes. This text can be formatted.
    Note: the text will not display if there are two recipes with two different outputs, and "title" is not set. This is the case of the image displayed, in which both recipes have the output names displayed, and there's no space for text.
    """
    return page('patchouli:crafting', {'recipe': first_recipe, 'recipe2': second_recipe, 'title': title, 'text': text_contents}, translation_keys=('text', 'title'))


def item_spotlight(item: str, title: str | None = None, link_recipe: bool = False, text_contents: str | None = None) -> Page:
    """
    :param item: An ItemStack String representing the item to be spotlighted.
    :param title: A custom title to show instead on top of the item. If this is empty or not defined, it'll use the item's name instead.
    :param link_recipe: Defaults to false. Set this to true to mark this spotlight page as the "recipe page" for the item being spotlighted. If you do so, when looking at pages that display the item, you can shift-click the item to be taken to this page. Highly recommended if the spotlight page has instructions on how to create an item by non-conventional means.
    :param text_contents: The text to display on this page, under the item. This text can be formatted.
    """
    return page('patchouli:spotlight', {'item': item, 'title': title, 'link_recipes': link_recipe, 'text': text_contents}, translation_keys=('title', 'text'))


def block_spotlight(title: str, text_content: str, block: str, lower: str | None = None) -> Page:
    """ A shortcut for making a single block multiblock that is meant to act the same as item_spotlight() but for blocks """
    return multiblock(title, text_content, False, pattern=(('X',), ('0',)), mapping={'X': block, '0': lower})


def two_tall_block_spotlight(title: str, text_content: str, lower: str, upper: str) -> Page:
    """ A shortcut for making a single block multiblock for a double tall block, such as crops or tall grass """
    return multiblock(title, text_content, False, pattern=(('X',), ('Y',), ('0',)), mapping={'X': upper, 'Y': lower})

def multiblock(title: str, text_content: str, enable_visualize: bool, pattern: Tuple[Tuple[str, ...], ...] | None = None, mapping: Mapping[str, str] | None = None, offset: Tuple[int, int, int] | None = None, multiblock_id: str | None = None) -> Page:
    """
    Page type: "patchouli:multiblock"

    :param title: The name of the multiblock you're displaying. Shows as a header above the multiblock display.
    :param text_content: The text to display on this page, under the multiblock. This text can be formatted.
    :param enable_visualize: Set this to false to disable the "Visualize" button.
    :param pattern: Terse explanation of the format: the pattern attribute is an array of array of strings. It is indexed in the following order: y (top to bottom), x (west to east), then z (north to south).
    :param mapping: Patchouli already provides built in characters for Air and (Any Block), which are respectively a space, and an underscore, so we don't have to account for those. Patchouli uses the same vanilla logic to parse blockstate predicate as, for example, the /execute if block ~ ~ ~ <PREDICATE> command. This means you can use block ID's, tags, as well as specify blockstate properties you want to constraint. Therefore, we have:
    :param offset: An int array of 3 values ([X, Y, Z]) to offset the multiblock relative to its center.
    :param multiblock_id: For modders only. The ID of the multiblock you want to display.
    """
    data = {'name': title, 'text': text_content, 'enable_visualize': enable_visualize}
    if multiblock_id is not None:
        return page('patchouli:multiblock', {'multiblock_id': multiblock_id, **data}, translation_keys=('name', 'text'))
    elif pattern is not None and mapping is not None:
        return page('patchouli:multiblock', {'multiblock': {
            'pattern': pattern,
            'mapping': mapping,
            'offset': offset,
        }, **data}, translation_keys=('name', 'text'))
    else:
        raise ValueError('multiblock page must have either \'multiblock\' or \'pattern\' and \'mapping\' entries')


def empty() -> Page:
    return page('patchouli:empty', {})

# Firmalife Pages

def drying_recipe(recipe: str, text_content: str) -> Page:
    return page('drying_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))

def smoking_recipe(recipe: str, text_content: str) -> Page:
    return page('smoking_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))

# ==============
# TFC Page Types
# ==============

def multimultiblock(text_content: str, *pages) -> Page:
    return page('multimultiblock', {'text': text_content, 'multiblocks': [p.data['multiblock'] if 'multiblock' in p.data else p.data['multiblock_id'] for p in pages]}, custom=True, translation_keys=('text',))


def rock_knapping_typical(recipe_with_category_format: str, text_content: str) -> Page:
    return rock_knapping(*[recipe_with_category_format % c for c in ROCK_CATEGORIES], text_content=text_content)


def rock_knapping(*recipes: str, text_content: str) -> Page:
    return page('rock_knapping_recipe', {'recipes': recipes, 'text': text_content}, custom=True, translation_keys=('text',))


def leather_knapping(recipe: str, text_content: str) -> Page:
    return page('leather_knapping_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))


def clay_knapping(recipe: str, text_content: str) -> Page:
    return page('clay_knapping_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))


def fire_clay_knapping(recipe: str, text_content: str) -> Page:
    return page('fire_clay_knapping_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))


def heat_recipe(recipe: str, text_content: str) -> Page:
    return page('heat_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))


def quern_recipe(recipe: str, text_content: str) -> Page:
    return page('quern_recipe', {'recipe': recipe, 'text': text_content}, custom=True)


def anvil_recipe(recipe: str, text_content: str) -> Page:
    return page('anvil_recipe', {'recipe': recipe, 'text': text_content}, custom=True)


def alloy_recipe(title: str, ingot: str, *components: Tuple[str, int, int], text_content: str) -> Page:
    recipe = ''.join(['$(li)%d - %d %% : $(thing)%s$()' % (lo, hi, alloy) for (alloy, lo, hi) in components])
    return item_spotlight(ingot, title, False, '$(br)$(bold)Requirements:$()$(br)' + recipe + '$(br2)' + text_content)


def fertilizer(item: str, text_contents: str, n: float = 0, p: float = 0, k: float = 0) -> Page:
    text_contents += ' $(br)'
    if n > 0:
        text_contents += '$(li)$(b)Nitrogen: %d$()' % (n * 100)
    if p > 0:
        text_contents += '$(li)$(6)Phosphorous: %d$()' % (p * 100)
    if k > 0:
        text_contents += '$(li)$(d)Potassium: %d$()' % (k * 100)
    return item_spotlight(item, text_contents=text_contents)

def page(page_type: str, page_data: JsonObject, custom: bool = False, translation_keys: Tuple[str, ...] = ()) -> Page:
    return Page(page_type, page_data, custom, None, [], translation_keys)

# Components

def text_component(x: int, y: int) -> Component:
    return Component('patchouli:text', x, y, {'text': '#text'})


def header_component(x: int, y: int) -> Component:
    return Component('patchouli:header', x, y, {'text': '#header'})


def seperator_component(x: int, y: int) -> Component:
    return Component('patchouli:separator', x, y, {})


def custom_component(x: int, y: int, class_name: str, data: JsonObject) -> Component:
    return Component('patchouli:custom', x, y, {'class': 'com.eerussianguy.firmalife.compat.patchouli.' + class_name, **data})


if __name__ == '__main__':
    main()

