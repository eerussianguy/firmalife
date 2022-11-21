import os
from argparse import ArgumentParser

from mcresources.type_definitions import ResourceIdentifier

from patchouli import *
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
    rm.domain = 'firmalife'  # DOMAIN CHANGE
    book = Book(rm, 'field_guide', {}, i18n, local_instance)
    book.template('smoking_recipe', custom_component(0, 0, 'SmokingComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('drying_recipe', custom_component(0, 0, 'DryingComponent', {'recipe': '#recipe'}), text_component(0, 45))

    book.category('firmalife', 'Firmalife', 'All about the Firmalife addon', 'firmalife:cured_oven_top', is_sorted=True, entries=(
        entry('cheese', 'Cheese', 'firmalife:food/gouda', pages=(
            text('Making $(thing)cheese$() in Firmalife is a little more involved than in vanilla TFC. There are two new kinds of milk: $(thing)Yak Milk$(), and $(thing)Goat Milk$(). These are obtained from milking the $(l:mechanics/animal_husbandry#yak)Yak$() and $(l:mechanics/animal_husbandry#goat)Goat$(), respectively. Milking the $(l:mechanics/animal_husbandry#cow)Cow$() still produces the old kind of milk.'),
            text('Like usual, milk must be $(thing)curdled$() first. To curdle milk, you need $(thing)Rennet$(). Rennet comes from the stomach of $(thing)Ruminant$() animals. This includes $(l:mechanics/animal_husbandry#yak)Yaks$(), $(l:mechanics/animal_husbandry#cow)Cows$(), $(l:mechanics/animal_husbandry#sheep)Sheep$(), $(l:mechanics/animal_husbandry#goat)Goats$(), and $(l:mechanics/animal_husbandry#musk_ox)Musk Oxen$(). To curdle milk, seal it in a $(l:mechanics/barrels)Barrel$() with Rennet for 4 hours.'),
            crafting('firmalife:crafting/cheesecloth', text_contents='Curdled milk must be converted to $(thing)Curds$() by sealing it in a barrel with $(thing)Cheesecloth$(). Cheesecloth is not reusable.'),
            crafting('firmalife:crafting/cheddar_wheel', text_contents='You are ready to make $(thing)Dry Cheese$() if you wish. You can make $(thing)Rajya Metok$() from $(thing)Yak Curds$(), $(thing)Chevre$() from $(thing)Goat Curds$(), and $(thing)Cheddar$() from $(thing)Milk Curds$().'),
            crafting('firmalife:crafting/chevre_wheel', 'firmalife:crafting/cheddar_wheel'),
            text('Your other option is to make $(thing)Wet Cheeses$(). These are made by sealing the curds in a barrel of $(thing)Salt Water$(). You can make $(thing)Shosha$() from $(thing)Yak Curds$(), $(thing)Feta$() from $(thing)Goat Curds$(), and $(thing)Gouda$() from $(thing)Milk Curds$().'),
            text('Cheese wheels are blocks that should be placed in order to help them last. To improve their quality and shelf life, cheese wheels should be $(thing)Aged$() in a $(l:firmalife:firmalife/cellar)Cellar$(). In order to obtain edible cheese from a cheese wheel, it should be sliced off the wheel by clicking $(item)$(k:key.use)$() with a $(thing)Knife$(). If the block is simply broken, the aging is lost!').anchor('aging'),
            multimultiblock('The aging stages of a wheel of Gouda: $(thing)Fresh$(), $(thing)Aged$(), and $(thing)Vintage$().', *[block_spotlight('', '', 'firmalife:gouda_wheel[age=%s]' % age) for age in ('fresh', 'aged', 'vintage')]),
        )),
        entry('climate_station', 'Climate Station', 'firmalife:climate_station', pages=(
            text('The $(thing)Climate Station$() is a block that manages the $(l:firmalife:firmalife/greenhouse)Greenhouse$() and the $(l:firmalife:firmalife/cellar)Cellar$(). When its corresponding multiblock is built correctly, it will show water on its sides. When it is invalid, it will show ice. The Climate Station must be placed on the first level of the multiblock, touching a wall.'),
            multimultiblock('The climate station in its valid and invalid state.', *[block_spotlight('', '', 'firmalife:climate_station[stasis=%s]' % b) for b in ('true', 'false')]),
            text('$(li)It updates periodically on its own, or when placed/broken.$()$(li)When a climate station updates, it tells all the blocks inside the multiblock that they can operate. For example, it lets $(l:firmalife:firmalife/cheese)Cheese$() begin aging.$()$(li)Press $(item)$(k:key.use)$() to force update the Climate Station and the blocks inside the multiblock.', 'Climate Station Tips'),
            crafting('firmalife:crafting/climate_station', text_contents='The climate station is crafted like this.'),
        )),
        entry('cellar', 'Cellars', 'firmalife:sealed_bricks', pages=(
            text('The $(thing)Cellar$() is a multiblock device controlled by a $(l:firmalife:firmalife/climate_station)Climate Station$(). The Cellar multiblock\'s only requirement is that it be in an enclosed area surrounded by $(thing)Sealed Bricks$() or $(thing)Sealed Brick Doors$() on all sides. The Climate Station must be placed on the first level of the cellar, touching a wall.'),
            multiblock('An Example Cellar', 'This is just one of many cellars that you could make!', True, multiblock_id='firmalife:cellar'),
            crafting('firmalife:crafting/sealed_bricks', 'firmalife:crafting/sealed_door'),
            text('$(thing)Beeswax$() is obtained from $(l:firmalife:firmalife/beekeeping)Beekeeping$().$(br)Cellars are used for $(l:firmalife:firmalife/cheese#aging)Aging Cheese$().'),
        )),
        entry('food_shelves', 'Food Shelves', 'firmalife:wood/food_shelf/pine', pages=(
            text('The $(thing)Food Shelf$() is a device for storing food. It can only be used in a valid $(l:firmalife:firmalife/cellar)Cellar$(). Food shelves can contain a full stack of one food item. Adding and removing the item can be done with $(item)$(k:key.use)$(). Items in valid food shelves receive a decay modifier that is better than vessels.'),
            crafting('firmalife:crafting/wood/acacia_shelf', text_contents='The food shelf is made from planks and lumber.'),
        )),
        entry('hangers', 'Hangers', 'firmalife:wood/hanger/pine', pages=(
            text('The $(thing)Hanger$() is a device for storing meat or garlic. It can only be used in a valid $(l:firmalife:firmalife/cellar)Cellar$(). Food shelves can contain a full stack of one item. Adding and removing the item can be done with $(item)$(k:key.use)$(). Items in valid food shelves receive a decay modifier that is better than shelves or vessels.'),
            crafting('firmalife:crafting/wood/acacia_hanger', text_contents='The hanger is made from planks and string.'),
        )),
        entry('greenhouse', 'Greenhouse', 'firmalife:sealed_bricks', pages=(
            text('The $(thing)Greenhouse$() is a multiblock device controlled by a $(l:firmalife:firmalife/climate_station)Climate Station$(). It allows growing crops year round. The Greenhouse has an array of types and blocks to choose from. However, building a greenhouse is quite simple. Like the $(l:firmalife:firmalife/cellar)Cellar$(), it should be an enclosed area of blocks belonging to the same $(thing)Greenhouse Type$(). The floor of the greenhouse may be any solid block.'),
            text('$(thing)Greenhouse Types$() are families of greenhouse blocks that can be used interchangeably in a greenhouse. Most greenhouse blocks $(thing)age$(). For example, $(thing)Treated Wood$() greenhouse blocks become $(thing)Weathered Treated Wood$() blocks. Since both of those block types belong to the same greenhouse type, your greenhouse will continue to function.'),
            text('These are the $(thing)Greenhouse Types$() available, with the block types they can age into:$(br)$(br)$(li)Treated Wood: Weathered $()$(li)Copper: Exposed, Weathered, Oxidized$()$(li)Iron: Rusted$() $(li)Stainless Steel (does not age)$()', 'Greenhouse Types'),
            text('There are four types of $(thing)Greenhouse Blocks$(): Walls, Doors, Roofs, and Roof Tops. Roofs and Roof Tops are stairs and slabs, respectively. These can be combined however you choose to form the structure of the greenhouse.'),
            multimultiblock('An example greenhouse, in each main type.', *[multiblock('', '', True, multiblock_id='firmalife:%s_greenhouse' % g) for g in ('treated_wood', 'copper', 'iron', 'stainless_steel')]),
            text('The next two pages contain recipes for the four main greenhouse block types. While they are only shown for Iron greenhouses, the iron rods in the recipe can be replaced with $(thing)Treated Lumber$(), $(thing)Copper Rods$(), or $(thing)Stainless Steel Rods$(). For information on Stainless Steel, see $(l:firmalife:firmalife/stainless_steel)this linked page$().'),
            crafting('firmalife:crafting/greenhouse/iron_greenhouse_wall', 'firmalife:crafting/greenhouse/iron_greenhouse_roof'),
            crafting('firmalife:crafting/greenhouse/iron_greenhouse_door', 'firmalife:crafting/greenhouse/iron_greenhouse_roof_top'),
            text('There are many blocks that operate inside Greenhouses:$(br)$(li)$(l:firmalife:firmalife/planters)Planters$(), for growing crops$(), and $(br)$(li)$(l:firmalife:firmalife/sprinklers)Sprinklers$(), various devices that add water to planters.'),
            empty_last_page()
        )),
        entry('sprinklers', 'Sprinklers', 'firmalife:squirting_moisture_transducer', pages=(
            text('The $(thing)Squirting Moisture Transducer$(), or the SMT, is an advanced device for watering planters. The SMT must be placed in a valid $(l:firmalife:firmalife/greenhouse)Greenhouse$() to work.'),
            crafting('firmalife:crafting/squirting_moisture_transducer', text_contents='The SMT is crafted from $(l:firmalife:firmalife/stainless_steel)Stainless Steel$().'),
            crafting('firmalife:crafting/embedded_pipe', text_contents='You may notice that the SMT does not work on its own. It must be used with a number of $(thing)Embedded Pipes$().'),
            text('When hovering over an SMT with a hoe, you will see that it wants $(thing)Embedded Pipes$(). To add pipes, $(item)$(k:key.use)$() on it while holding a pipe. The SMT can hold 32 pipes. The SMT will automatically drive pipes into the ground below it. The SMT can only drive pipes through natural blocks like dirt, grass, and rock, but not ore! The SMT will say in its hoe overlay when it has enough pipes to operate.'),
            text('Depending on the moisture content of its surrounding environment, the SMT has a minimum number of pipes to work. When it is operating, the SMT will water a box of 4 blocks in all directions every minute, releasing particles when doing so.'),
            empty_last_page(),
        )),
        entry('planters', 'Planters', 'firmalife:large_planter', pages=(
            text('$(thing)Planters$() are used to grow crops inside a $(l:firmalife:firmalife/greenhouse)Greenhouse$(). To see the status of a planter, you can look at it while holding a $(thing)Hoe$(). Crops in planters consume $(l:mechanics/fertilizers)Nutrients$() in a similar way to $(l:mechanics/crops)Crops$(). Planters should be placed inside a valid Greenhouse and activated with a $(l:firmalife:firmalife/climate_station)Climate Station$(). Planters need at least some natural sunlight to work.').anchor('planters'),
            crafting('firmalife:crafting/watering_can', text_contents='Planters must be $(thing)Watered$() to grow. This is done with a $(thing)Watering Can$(), crafted from a $(thing)Wooden Bucket$(), a container of $(thing)Water$() and $(thing)Lumber$(). Press $(item)$(k:key.use)$() with it to water nearby planters. Refill it by pressing $(item)$(k:key.use)$() on a water source.'),
            crafting('firmalife:crafting/large_planter', text_contents='$(thing)Large Planters$() are the most simple kind of planter. They grow a single crop from seed, and are harvested with $(item)$(k:key.use)$() when mature.'),
            text('Large Planters can grow $(thing)Green Beans$(), $(thing)Tomatoes$(), $(thing)Sugarcane$(), $(thing)Jute$(), and $(thing)Grains$(). However, to grow Grains, you need a $(thing)Copper$() or better Greenhouse.'),
            crafting('firmalife:crafting/quad_planter', text_contents='$(thing)Quad Planters$() grow four individual crops at once. These crops all draw from the same nutrient pool, and can be harvested individually with $(item)$(k:key.use)$() when mature.'),
            text('Quad Planters can grow $(thing)Beets$(), $(thing)Cabbage$(), $(thing)Carrots$(), $(thing)Garlic$(), $(thing)Onions$(), $(thing)Potatoes$(), and $(thing)Soybeans$(). These crops can be grown in any greenhouse type.'),
            crafting('firmalife:crafting/bonsai_planter', text_contents='$(thing)Bonsai Planters$() grow small fruit trees from their saplings. The fruit can be picked with $(item)$(k:key.use)$().'),
            text('Bonsai Planters can grow any fruit tree type, except $(thing)Bananas$(), which need a $(thing)Hanging Planter$(). They all consume Nitrogen as their main nutrient. They need an $(thing)Iron$() or better greenhouse to grow.'),
            crafting('firmalife:crafting/hanging_planter', text_contents='$(thing)Hanging Planters$() grow crops upside down. When mature, they can be harvested with $(item)$(k:key.use)$().'),
            text('Hanging Planters grow $(thing)Squash$(), from their seeds, and $(thing)Bananas$(), from their saplings. Squash can be grown in any greenhouse, but Bananas require an $(thing)Iron$() or better greenhouse to grow. Hanging planters need to anchor to a solid block above them.'),
            crafting('firmalife:crafting/trellis_planter', text_contents='$(thing)Trellis Planters$() grow berry bushes. Berries can be picked with $(item)$(k:key.use)$().'),
            text('Trellis Planters have the unique property of $(thing)propagating$() berry bushes. If a trellis planter is placed on top of another, and the one below has a mature berry bush, it has a chance to grow upwards into the next one. Trellis planters can grow any berry bush except $(thing)Cranberries$(), but require an $(thing)Iron$() or better greenhouse to work. Bushes prefer Nitrogen.'),
        )),
        entry('beekeeping', 'Beekeeping', 'firmalife:beehive', pages=(
            text('$(thing)Beehives$() are a place to house bees. Beehives need $(thing)Beehive Frames$() inside them for the bees to live. Removing frames from an active hive will cause the bees to attack you, unless done at night, or with a $(thing)Firepit$() underneath the hive active.'),
            crafting('firmalife:crafting/beehive', 'firmalife:crafting/beehive_frame'),
            text('Beehives know about the area in a 5 block radius from them. If there are at least 10 flowers around the hive, there is a chance an empty frame will be populated with a $(thing)Queen$(). This is indicated by bee particles flying around the hive.'),
            text('If a beehive has two frames with queens, and an empty frame, the two colonies have a chance of $(thing)Breeding$() and producing a new queen in the empty frame. This has the effect of passing on the $(thing)Abilities$() of each parent to the offspring. Abilities are different traits bees have that change how they effect the world around them. They are on a scale of 1-10, with 10 being the max.'),
            crafting('firmalife:crafting/honey_jar_open', text_contents='Bees also produce $(thing)Honey$(). Using $(item)$(k:key.use)$() with an $(l:firmalife:firmalife/jar)Empty Jar$() on a hive that visibly has honey gives you a $(thing)Honey Jar$(). Opening a Honey Jar gives you $(thing)Raw Honey$(), a $(thing)Sugar$() substitute.'),
            text('Pressing $(item)$(k:key.use)$() a filled frame in your inventory with a $(thing)Knife$() gives you $(thing)Beeswax$(), which has many uses. However, this kills the queen inside the frame so be careful!'),
            crafting('firmalife:crafting/treated_lumber', text_contents='The most important use of beeswax is in creating $(thing)Treated Lumber$().'),
            text('$(li)Bees can help fertilize planters!$()$(li)Scraping a frame sacrifices the queen. Be smart!$()$(li)Being wet prevents bees from attacking you.$()', 'Bee Tips'),
            text('$(li)$(thing)Hardiness$(): Allows bees to produce honey at lower temperatures. Hardiness 10 allows up to -16°C, whereas Hardiness 1 allows up to 2°C.$()$(li)$(thing)Production$(): Improves the speed of honey production.$()$(li)$(thing)Mutant$(): Increases variability in the traits passed during breeding$().', 'List of Abilities'),
            text('$(li)$(thing)Fertility$(): Increases likelihood of breeding.$()$(li)$(thing)Crop Affinity$(): Likelihood of spreading a small amount of nutrients to crops.$()$(li)$(thing)Nature Restoration$(): Causes new flowers and lilypads to spawn around the hive.$()$(li)$(thing)Calmness$(): Decreases likelihood of bees attacking you$().'),
        )),
        entry('jar', 'Jars', 'firmalife:empty_jar', pages=(
            text('$(thing)Jars$() are a way of storing certain items. They can be placed on the ground, in groups of up to four. They are most useful as a way of storing $(l:firmalife:firmalife/beekeeping)Honey$() indefinitely.'),
            multimultiblock('Each possible arrangement of jars.', *[block_spotlight('', '', 'firmalife:honey_jar[count=%s]' % i) for i in range(1, 5)]),
            crafting('firmalife:crafting/empty_jar', text_contents='It all starts with an $(thing)Empty Jar$(). Jars can be emptied, discarding the contents, by sealing them in a $(l:mechanics/barrels)Barrel$() of $(thing)Water$().'),
            text('The recipes to fill and empty jars are typically simple crafting recipes. Some jars cannot have their insides removed without a special recipe.$(br)$(br)One use of jars is making $(thing)Fruit Preserves$(). To do this, boil an $(thing)Empty Jar$(), a $(thing)Sweetener$(), and fresh $(thing)Fruit$().'),
            text('$(li)Fruit Preserves$()$(li)Guano Jar$()$(li)Honey Jar$()$(li)Rotten Compost Jar$()$(li)Compost Jar$()', 'Jar Types'),
            empty_last_page()
        )),
        entry('stainless_steel', 'Stainless Steel', 'firmalife:metal/ingot/stainless_steel', pages=(
            text('$(thing)Stainless Steel$() and $(thing)Chromium$() are $(thing)Steel-tier$() metals added by Firmalife. They are used in the construction of $(l:firmalife:firmalife/greenhouse)Stainless Steel Greenhouses$().'),
            alloy_recipe('Stainless Steel', 'firmalife:metal/ingot/stainless_steel', ('Chromium', 20, 30), ('Nickel', 10, 20), ('Steel', 60, 80), text_content=''),
            item_spotlight('firmalife:ore/small_chromite', text_contents='Chromite is an ore that is melted to obtain Chromium. It is found in $(thing)Igneous Intrusive$() and $(thing)Metamorphic$() rocks.'),
            text('$(li)Granite$()$(li)Diorite$()$(li)Gabbro$()$(li)Slate$()$(li)Phyllite$()$(li)Schist$()$(li)Gneiss$()$(li)Marble$()', 'All Chromium Rocks')
        )),
        entry('drying', 'Drying', 'firmalife:drying_mat', pages=(
            text('The $(thing)Drying Mat$() is used to dry items. It is made with $(thing)Fruit Leaves$(), which are obtained from breaking the leaves of $(thing)Fruit Trees$().'),
            crafting('firmalife:crafting/drying_mat', text_contents='The recipe for the drying mat.'),
            text('To use the drying mat, place it out on the sun and add an item to it with $(item)$(k:key.use)$(). After a half day, it will be dried. If it rains, the drying process must start over.'),
            drying_recipe('firmalife:drying/drying_fruit', 'Drying fruit is a common use of the drying mat. Dried fruit is used in some recipes, and lasts longer.'),
            drying_recipe('firmalife:drying/tofu', 'Tofu is made using a drying mat.'),
            drying_recipe('firmalife:drying/cinnamon', 'Cinnamon is made using a drying mat.'),
        )),
        entry('smoking', 'Smoking', 'tfc:food/venison', pages=(
            text('Wool string is used to hang items for $(thing)Smoking$(). To place it, just use $(item)$(k:key.use)$().'),
            two_tall_block_spotlight('Smoking', 'A piece of string above a firepit.', 'tfc:firepit[lit=true]', 'firmalife:wool_string'),
            text('Smoking is used to preserve $(thing)Meat$() and $(l:mechanics/dairy)Cheese$(). To smoke meat, it must have first been $(thing)Brined$() by sealing it in a $(thing)Barrel$() with $(thing)Brine$(). You may also salt it first. Cheese does not have this requirement.'),
            text('To start the smoking process, add the item to the string above a firepit. The firepit must be within four blocks, directly underneath the string. The string should begin to generate some smoke if it is working. It is important to note that the firepit must only be burned with $(thing)Logs$(). Using something like $(thing)Peat$() will instantly give your food the $(thing)Disgusting$() trait!'),
            text('The smoking process takes 8 in-game hours. Happy smoking!'),
            empty_last_page()
        )),
        entry('ovens', 'Ovens', 'firmalife:cured_oven_top', pages=(
            text('$(thing)Ovens$() are a great way of cooking lots of food in a way that improves their shelf life. Oven-baked food decays at 90% of the rate of regular food. Ovens are a multiblock structure consisting of a $(thing)Bottom Oven$(), $(thing)Top Oven$(), and optionally $(thing)Chimneys$(). These blocks start off as clay, and must be $(thing)Cured$() by raising their temperature to a certain amount for long enough.'),
            clay_knapping('tfc:clay_knapping/oven_top', 'The recipe for the bottom oven.'),
            clay_knapping('tfc:clay_knapping/oven_bottom', 'The recipe for the top oven.'),
            clay_knapping('tfc:clay_knapping/oven_chimney', 'The recipe for the oven chimney    .'),
            crafting('tfc:crafting/bricks', text_contents='Ovens are insulated with $(thing)Bricks$(), other oven blocks, or anything that can insulate a Forge. This means you can use stone blocks, if you want!'),
            crafting('firmalife:crafting/peel', text_contents='The $(thing)Peel$() is the only safe way to remove hot items from an Oven. Just $(item)$(k:key.use)$() on it while holding it to retrieve items. Otherwise, you may get burned!'),
            text('The Oven first consists of the Top Oven placed on top of the Bottom Oven. All sides of each oven part, besides the front face, should then be covered with Oven Insulation blocks, as covered two pages ago. You may choose to use $(thing)Oven Chimneys$() as insulation. Placing a stack of chimneys directly behind the oven causes the smoke from the oven to travel up and out of it. If you don\'t do this, smoke will quickly fill up your house, which is very distracting!'),
            multimultiblock('An example oven structure, uncured and cured.', *[multiblock('', '', True, (
                ('     ', '  C  '),
                ('     ', '  C  '),
                ('WT0TW', 'WWCWW'),
                ('WBBBW', 'WWCWW'),
            ), {
                '0': 'firmalife:%soven_top[facing=south]' % pref,
                'T': 'firmalife:%soven_top[facing=south]' % pref,
                'B': 'firmalife:%soven_bottom[facing=south]' % pref,
                'W': 'minecraft:bricks',
                'C': 'firmalife:%soven_chimney' % pref,
            }) for pref in ('cured_', '')]),
            text('The Bottom Oven is used to hold fuel, which may only be logs. Press $(item)$(k:key.use)$() to add or remove them. The bottom oven is also the part of the oven which may be lit with a $(thing)Firestarter$() or other tool. It transfers heat contained in it to the top oven.'),
            text('The Top Oven contains the items that are being cooked. It will draw heat from the Bottom Oven and slowly release it over time. This means that even if your fuel runs out, your Top Oven can continue to work for a little while. Adding items to it is as simple as pressing $(item)$(k:key.use)$(). Remember to use a $(thing)Peel$() to remove the items after.'),
            text('Curing Oven blocks is easy, but requires patience. Simply start running your Bottom Oven as you would normally, and then wait. If an oven block is above 600 degrees for about 80 seconds, it will cure itself and any oven blocks around it. The curing effect will pass all the way up chimneys nearby.'),
            empty_last_page()
        )),
        entry('bread', 'Bread', 'tfc:food/rye_bread', pages=(
            text('To make $(thing)Bread$(), one first must get $(thing)Yeast$(). To get your first yeast, seal $(l:firmalife:firmalife/drying)Dried Fruit$() in a Barrel of $(thing)Water$(). After three days, $(thing)Yeast Starter$() will form.$(br)From now on, your yeast can be fed by sealing Yeast Starter in a Barrel with $(thing)Flour$(). This causes it to multiply. 1 flour per 100mB of Yeast produces 600mB of Yeast. That\'s a good deal!'),
            crafting('firmalife:crafting/barley_dough', text_contents='Yeast Starter, Sweetener, and Flour can be combined to make $(thing)Dough$(). Dough can be cooked like normal to produce $(thing)Bread!$().'),
        )),
        entry('more_fertilizer', 'More Fertilizer Options', 'firmalife:iron_composter', pages=(
            text('Given a greater need for fertilization in Firmalife, there are more options for getting $(l:mechanics/fertilizers)fertilizers$().'),
            drying_recipe('firmalife:drying/dry_grass', 'Thatch can be $(l:firmalife:firmalife/drying)Dried$() into $(thing)Dry Grass$(), which can be used in a Composter as a brown item.'),
            crafting('firmalife:crafting/iron_composter', text_contents='The Composter can be upgraded to an $(thing)Iron Composter$(), which works the same, except it produces compost four times as fast.'),
            multimultiblock('The possible fill levels of the iron composter', *[block_spotlight('', '', 'firmalife:iron_composter[type=normal,stage=%s]' % i) for i in range(0, 9)]),
        )),
        entry('mixing_bowl', 'Mixing Bowl', 'firmalife:mixing_bowl', pages=(
            text('The mixing bowl is a way of mixing items and fluids together in a friendly way. $(item)$(k:key.use)$() on it with a $(thing)Spoon$() to add it to the bowl, which allows it to operate.'),
            crafting('firmalife:crafting/mixing_bowl', text_contents='Requires a $(thing)Spoon$() to use.'),
        )),
        entry('herbs_and_spices', 'Herbs and Spices', 'firmalife:spice/basil_leaves', pages=(
            text('In Firmalife, there are a number of small plants you can collect and grow on your own, which have cooking properties. The easiest way to obtain these plants is with a $(thing)Seed Ball$(). To use a $(thing)Seed Ball$(), just $(item)$(k:key.use)$() to throw it, like a snowball. This will spawn $(thing)Butterfly Grass$() in the area.'),
            crafting('firmalife:crafting/seed_ball', text_contents='The recipe for the seed ball requires $(l:firmalife:firmalife/more_fertilizer)Compost$() and 4 $(thing)Seeds$().'),
            block_spotlight('', 'A butterfly grass plant.', 'firmalife:plant/butterfly_grass'),
            text('Butterfly grass will mature over time. When one reaches maturity, it has a chance to spread to surrounding blocks, or turn into something new. Butterfly grass blocks that have been spread by another grass block do not spread anymore.'),
            block_spotlight('', 'Basil is one of the plants that can be spawned by butterfly grass.', 'firmalife:plant/basil'),
            crafting('firmalife:crafting/basil_leaves', text_contents='Basil leaves are used in pizza.'),
        ))
    ))

    rm.domain = 'firmalife'  # DOMAIN RESET

# Firmalife Pages

def drying_recipe(recipe: str, text_content: str) -> Page:
    return page('drying_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',), firmalife=True)

def smoking_recipe(recipe: str, text_content: str) -> Page:
    return page('smoking_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',), firmalife=True)

def alloy_recipe(title: str, ingot: str, *components: Tuple[str, int, int], text_content: str) -> Page:
    recipe = ''.join(['$(li)%d - %d %% : $(thing)%s$()' % (lo, hi, alloy) for (alloy, lo, hi) in components])
    return item_spotlight(ingot, title, False, '$(br)$(bold)Requirements:$()$(br)' + recipe + '$(br2)' + text_content)

def custom_component(x: int, y: int, class_name: str, data: JsonObject) -> Component:
    return Component('patchouli:custom', x, y, {'class': 'com.eerussianguy.firmalife.compat.patchouli.' + class_name, **data})


if __name__ == '__main__':
    main()

