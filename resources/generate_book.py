import os
from argparse import ArgumentParser

from mcresources.type_definitions import ResourceIdentifier

from constants import FRUITS, STILL_BUSHES
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
        entry('differences_from_tfc', 'Differences from TFC', 'tfc:food/wheat_bread', pages=(
            text('Firmalife makes a few changes to how things operate in regular TFC. This chapter exists to help direct you towards areas where this is very different.'),
            text('$(l:firmalife:firmalife/cheese)Cheese$() is made through a more complex process. It can be placed in world, and has the option of aging in a $(l:firmalife:firmalife/cellar)Cellar$().', title='Cheese Aging'),
            text('$(l:firmalife:firmalife/bread)Bread$() is made through a more complex process, requiring yeast and sweetener. The regular TFC bread recipe makes flatbread, which is worse nutritionally.', title='Bread Making'),
            text('Firmalife has a greater emphasis on sugar. While it can still be obtained through sugar cane, consider using honey (from bees) or making sugar from beets!', title='Sweeteners'),
        )),
        entry('cheese', 'Cheese', 'firmalife:food/gouda', pages=(
            text('Making $(thing)cheese$() in Firmalife is a little more involved than in vanilla TFC. There are two new kinds of milk: $(thing)Yak Milk$(), and $(thing)Goat Milk$(). These are obtained from milking the $(l:mechanics/animal_husbandry#yak)Yak$() and $(l:mechanics/animal_husbandry#goat)Goat$(), respectively. Milking the $(l:mechanics/animal_husbandry#cow)Cow$() still produces the old kind of milk.'),
            text('Like usual, milk must be $(thing)curdled$() first. To curdle milk, you need $(thing)Rennet$(). Rennet comes from the stomach of $(thing)Ruminant$() animals. This includes $(l:mechanics/animal_husbandry#yak)Yaks$(), $(l:mechanics/animal_husbandry#cow)Cows$(), $(l:mechanics/animal_husbandry#sheep)Sheep$(), $(l:mechanics/animal_husbandry#goat)Goats$(), and $(l:mechanics/animal_husbandry#musk_ox)Musk Oxen$(). To curdle milk, seal it in a $(l:mechanics/barrels)Barrel$() with Rennet for 4 hours.'),
            crafting('firmalife:crafting/cheesecloth', text_contents='Curdled milk must be converted to $(thing)Curds$() by sealing it in a barrel with $(thing)Cheesecloth$(). Cheesecloth is not reusable.'),
            crafting('firmalife:crafting/cheddar_wheel', text_contents='You are ready to make $(thing)Dry Cheese$() if you wish. You can make $(thing)Rajya Metok$() from $(thing)Yak Curds$(), $(thing)Chevre$() from $(thing)Goat Curds$(), and $(thing)Cheddar$() from $(thing)Milk Curds$().'),
            crafting('firmalife:crafting/chevre_wheel', 'firmalife:crafting/rajya_metok_wheel'),
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
            text('The cellar is used for food preservation, for example by using $(l:firmalife:firmalife/food_shelves)Food Shelves$() and $(l:firmalife:firmalife/hangers)Hangers$(). The cellar performs better in environments with cooler average temperatures for food preservation. Below 0 degrees, decay modifiers work slightly better. Below -12 degrees, they perform much better.'),
            empty_last_page()
        )),
        entry('food_shelves', 'Food Shelves', 'firmalife:wood/food_shelf/pine', pages=(
            text('The $(thing)Food Shelf$() is a device for storing food. It can only be used in a valid $(l:firmalife:firmalife/cellar)Cellar$(). Food shelves can contain a full stack of one food item. Adding and removing the item can be done with $(item)$(k:key.use)$(). Items in valid food shelves receive a decay modifier that is better than vessels.'),
            crafting('firmalife:crafting/wood/acacia_shelf', text_contents='The food shelf is made from planks and lumber.'),
        )),
        entry('hangers', 'Hangers', 'firmalife:wood/hanger/pine', pages=(
            text('The $(thing)Hanger$() is a device for storing meat or garlic. It can only be used in a valid $(l:firmalife:firmalife/cellar)Cellar$(). Food shelves can contain a full stack of one item. Adding and removing the item can be done with $(item)$(k:key.use)$(). Items in valid food shelves receive a decay modifier that is better than shelves or vessels.'),
            crafting('firmalife:crafting/wood/acacia_hanger', text_contents='The hanger is made from planks and string.'),
        )),
        entry('jarbnet', 'Jarbnets', 'firmalife:wood/jarbnet/pine', pages=(
            text('The jarbnet is a cosmetic storage block for $(l:firmalife:firmalife/jars)Jars$(), Candles, and Jugs. It can be opened and closed by clicking with an empty hand and $(item)$(k:key.sneak)$() pressed. If candles are inside, it can be lit to produce a small amount of light.'),
            crafting('firmalife:crafting/wood/acacia_jarbnet'),
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
            text('The $(thing)Sprinkler$() is a device that sprinkles water in a 5x6x5 area centered on the block below the sprinkler block. It automatically fills with water from any fluid tank or pipe placed above it. You know it is working when it drips out water particles.'),
            two_tall_block_spotlight('Starter Sprinkler Setup', 'The easiest way to set up a sprinkler is with a barrel of water.', 'firmalife:sprinkler', 'tfc:wood/barrel/pine'),
            anvil_recipe('firmalife:anvil/sprinkler', 'The sprinkler is made with a $(thing)Red Steel Sheet$().'),
            anvil_recipe('firmalife:anvil/dribbler', 'The dribbler is made with a $(thing)Stainless Steel Sheet$(). It works like a sprinkler, but only waters the 7 blocks below it!'),
            # text('The $(thing)Squirting Moisture Transducer$(), or the SMT, is an advanced device for watering planters. The SMT must be placed in a valid $(l:firmalife:firmalife/greenhouse)Greenhouse$() to work.'),
            # crafting('firmalife:crafting/squirting_moisture_transducer', text_contents='The SMT is crafted from $(l:firmalife:firmalife/stainless_steel)Stainless Steel$().'),
            # crafting('firmalife:crafting/embedded_pipe', text_contents='You may notice that the SMT does not work on its own. It must be used with a number of $(thing)Embedded Pipes$().'),
            # text('When hovering over an SMT with a hoe, you will see that it wants $(thing)Embedded Pipes$(). To add pipes, $(item)$(k:key.use)$() on it while holding a pipe. The SMT can hold 32 pipes. The SMT will automatically drive pipes into the ground below it. The SMT can only drive pipes through natural blocks like dirt, grass, and rock, but not ore! The SMT will say in its hoe overlay when it has enough pipes to operate.'),
            # text('Depending on the moisture content of its surrounding environment, the SMT has a minimum number of pipes to work. When it is operating, the SMT will water a box of 4 blocks in all directions every minute, releasing particles when doing so.'),
            # empty_last_page(),
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
            crafting('firmalife:crafting/hydroponic_planter', text_contents='$(thing)Hydroponic Planters$() grow rice and cranberry bushes. They work the same as a quad planter, except that they do not need to be watered. Instead, a $(thing)Nutritive Vat$() must be placed below them. Without the vat they will not grow.'),
            crafting('firmalife:crafting/nutritive_basin', text_contents='Nutritive basins must be filled with water, using $(item)$(k:key.use)$().'),
        )),
        entry('beekeeping', 'Beekeeping', 'firmalife:beehive', pages=(
            text('$(thing)Beehives$() are a place to house bees. Beehives need $(thing)Beehive Frames$() inside them for the bees to live. Removing frames from an active hive will cause the bees to attack you, unless done at night, or with a $(thing)Firepit$() underneath the hive active. Beehives can share flowers. The benefit of flowers diminishes after 60 flowers.'),
            crafting('firmalife:crafting/beehive', 'firmalife:crafting/beehive_frame'),
            text('Beehives know about the area in a 5 block radius from them. If there are at least 10 flowers around the hive, there is a chance an empty frame will be populated with a $(thing)Queen$(). This is indicated by bee particles flying around the hive. Having 4 empty frames in a hive greatly increases the chances of bees moving in.'),
            text('If a beehive has two frames with queens, and an empty frame, the two colonies have a chance of $(thing)Breeding$() and producing a new queen in the empty frame. This has the effect of passing on the $(thing)Abilities$() of each parent to the offspring. Abilities are different traits bees have that change how they effect the world around them. They are on a scale of 1-10, with 10 being the max.'),
            crafting('firmalife:crafting/honey_jar_open', text_contents='Bees also produce $(thing)Honey$(). Using $(item)$(k:key.use)$() with an $(l:firmalife:firmalife/jar)Empty Jar$() on a hive that visibly has honey gives you a $(thing)Honey Jar$(). Opening a Honey Jar gives you $(thing)Raw Honey$(), a $(thing)Sugar$() substitute.'),
            text('Pressing $(item)$(k:key.use)$() a filled frame in your inventory with a $(thing)Knife$() gives you $(thing)Beeswax$(), which has many uses. However, this kills the queen inside the frame so be careful!'),
            crafting('firmalife:crafting/treated_lumber', text_contents='The most important use of beeswax is in creating $(thing)Treated Lumber$().'),
            text('$(li)Bees can help fertilize planters!$()$(li)Scraping a frame sacrifices the queen. Be smart!$()$(li)Being wet prevents bees from attacking you.$()', 'Bee Tips'),
            text('$(li)$(thing)Hardiness$(): Allows bees to produce honey at lower temperatures. Hardiness 10 allows up to -16°C, whereas Hardiness 1 allows up to 2°C.$()$(li)$(thing)Production$(): Improves the speed of honey production.$()$(li)$(thing)Mutant$(): Increases variability in the traits passed during breeding$().', 'List of Abilities'),
            text('$(li)$(thing)Fertility$(): Increases likelihood of breeding.$()$(li)$(thing)Crop Affinity$(): Likelihood of spreading a small amount of nutrients to crops.$()$(li)$(thing)Nature Restoration$(): Causes new flowers and lilypads to spawn around the hive.$()$(li)$(thing)Calmness$(): Decreases likelihood of bees attacking you$().'),
            text('Bees with high Mutant ability have a chance of developing a $(thing)Genetic Disease$(). Diseased bees pass on their disease to their offspring, and don\'t produce honey.'),
            empty_last_page(),
        )),
        entry('jar', 'Jars', 'firmalife:empty_jar', pages=(
            text('$(thing)Jars$() are a way of storing certain items. They can be placed on the ground as blocks, in groups of up to four. They are most useful as a way of storing $(l:firmalife:firmalife/beekeeping)Honey$() indefinitely.'),
            multimultiblock('Each possible arrangement of jars.', *[block_spotlight('', '', 'firmalife:honey_jar[count=%s]' % i) for i in range(1, 5)]),
            crafting('firmalife:crafting/empty_jar', text_contents='It all starts with an $(thing)Empty Jar$(). Jars can be emptied, discarding the contents, by sealing them in a $(l:mechanics/barrels)Barrel$() of $(thing)Water$().'),
            text('The recipes to fill and empty jars are typically simple crafting recipes. Some jars cannot have their insides removed without a special recipe.$(br)$(br)One use of jars is making $(thing)Fruit Preserves$(). To do this, boil an $(thing)Empty Jar$(), a $(thing)Sweetener$(), and fresh $(thing)Fruit$().'),
            text('$(li)Fruit Preserves$()$(li)Guano Jar$()$(li)Honey Jar$()$(li)Rotten Compost Jar$()$(li)Compost Jar$()', 'Jar Types'),
            text('Using the $(l:firmalife:firmalife/oven_appliances#vat)Vat$(), $(thing)Sugar Water$() can be made by adding sweetener to water. Then, adding fruit causes $(thing)Fruity Fluid$() to be made. Putting a jar in the vat can then fill it. Be careful: trying to pipe or transport fruity fluid through too many transfers could ruin it!'),
            crafting('firmalife:crafting/jarring_station', text_contents='The $(thing)Jarring Station$(), if placed with its spout facing a vat of $(thing)Fruity Fluid$(), will automatically fill up to 9 jars placed inside.'),
            empty_last_page(),
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
            crafting('firmalife:crafting/solar_drier', text_contents='The solar drier functions the same as the drying mat, but 12x as fast.'),
            drying_recipe('firmalife:drying/drying_fruit', 'Drying fruit is a common use of the drying mat. Dried fruit is used in some recipes, and lasts longer.'),
            drying_recipe('firmalife:drying/tofu', 'Tofu is made using a drying mat.'),
            drying_recipe('firmalife:drying/cinnamon', 'Cinnamon is made using a drying mat.'),
            empty_last_page()
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
            text('$(thing)Ovens$() are a great way of cooking lots of food in a way that improves their shelf life. Oven-baked food decays at 90% of the rate of regular food. Ovens are a multiblock structure consisting of a $(thing)Bottom Oven$(), $(thing)Top Oven$(), and optionally $(thing)Chimneys$(). These blocks start off as clay, and must be $(thing)Cured$() by raising their temperature to a certain amount for long enough.$(br)$(l:firmalife:firmalife/oven_appliances)Oven Appliances$() extend oven functionality.'),
            clay_knapping('tfc:clay_knapping/oven_top', 'The recipe for the top oven.'),
            clay_knapping('tfc:clay_knapping/oven_bottom', 'The recipe for the bottom oven.'),
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
                '0': 'firmalife:%soven_top[facing=north]' % pref,
                'T': 'firmalife:%soven_top[facing=north]' % pref,
                'B': 'firmalife:%soven_bottom[facing=north]' % pref,
                'W': 'minecraft:bricks',
                'C': 'firmalife:%soven_chimney' % pref,
            }) for pref in ('cured_', '')]),
            text('The Bottom Oven is used to hold fuel, which may only be logs. Press $(item)$(k:key.use)$() to add or remove them. The bottom oven is also the part of the oven which may be lit with a $(thing)Firestarter$() or other tool. It transfers heat contained in it to the top oven.'),
            text('The Top Oven contains the items that are being cooked. It will draw heat from the Bottom Oven and slowly release it over time. This means that even if your fuel runs out, your Top Oven can continue to work for a little while. Adding items to it is as simple as pressing $(item)$(k:key.use)$(). Remember to use a $(thing)Peel$() to remove the items after.'),
            text('Curing Oven blocks is easy, but requires patience. Simply start running your Bottom Oven as you would normally, and then wait. If an oven block is above 600 degrees for about 80 seconds, it will cure itself and any oven blocks around it. The curing effect will pass all the way up chimneys nearby.'),
            crafting('firmalife:crafting/oven_insulation', text_contents='Crafting oven insulation for your Bottom Oven allows you to remove the need for insulating it on the sides and back. It does not remove the need for the chimney. Use $(item)$(k:key.use)$() to apply it.'),
            crafting('firmalife:crafting/brick_countertop', text_contents='Countertops are aesthetic blocks that count as oven insulation, and have an appearance that matches that of oven blocks. They are a nice aesthetic choice for your kitchen.'),
            text('Ovens also have $(thing)Finishes$() that can be used to change their appearance. These finishes are applied to the basic brick stage of the oven (or brick blocks themselves), and are cosmetic. Finishes can be mixed and matched. They are applied with $(item)$(k:key.use)$().'),
            crafting('firmalife:crafting/rustic_finish', 'firmalife:crafting/stone_finish'),
            crafting('firmalife:crafting/tile_finish')
        )),
        entry('oven_appliances', 'Oven Appliances', 'tfc:vat', pages=(
            text('$(l:firmalife:firmalife/ovens)Ovens$() have a number of devices that interact with them, that extend their functionality. This is because ovens are modular in nature.'),
            crafting('firmalife:crafting/oven_hopper', text_contents='The $(thing)Oven Hopper$() will input logs into any Bottom Oven that it is facing. It holds 16 logs (4 stacks of 4, like a log pile), and its inventory is fed by dropping items in the top. It can also be fed via automation from other mods.'),
            crafting('firmalife:crafting/ashtray', text_contents='The $(thing)Ashtray$() collects $(thing)Wood Ash$() when placed below a $(thing)Bottom Oven Block$(). There is a 0.5 chance it gains ash when fuel is consumed. Ash is extracted with $(item)$(k:key.use)$() and inserted via attacking it.'),
            crafting('firmalife:crafting/vat', text_contents='The $(thing)Vat$() produces some select boiling recipes in bulk. It has one slot for items, and 10,000mB of fluid space, similar to a barrel.').anchor('vat'),
            text('For example, the vat can be used to make $(thing)Olive Oil Water$() using a ratio of 1 Olive Paste to 200 mB Water. To use a vat, $(item)$(k:key.use)$() it with fluids and items to add them to the inventory. With an empty hand and $(item)$(k:key.sneak)$() held, click to seal and unseal the vat. A vat will not boil until it is sealed.'),
            text('Vats should be placed on the block above a $(thing)Bottom Oven$(). If the vat would overflow on completion of the recipe, it will not boil, so be sure not to overfill it -- especially with recipes that produce more fluid than they consume!'),
            two_tall_block_spotlight('', '', 'firmalife:cured_oven_bottom', 'firmalife:vat'),
            text('Pots and Grills from TFC can be placed on top of a $(thing)Bottom Oven$(). These devices will get heat automatically from the bottom oven. They come with a couple restrictions: Each has only 4 slots, and the pot is only used for making soup. It cannot execute regular pot recipes.'),
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
        )),
        entry('fruit_trees', 'Firmalife Fruits', 'firmalife:plant/fig_sapling', pages=(
            text('Firmalife adds some fruiting plants on top of those added by TFC.'),
            text('To improve readability, entries start on the next page.'),
            *detail_fruit_tree('cocoa', 'Cocoa trees are used to make $(l:firmalife:firmalife/chocolate)Chocolate$().'),
            *detail_fruit_tree('fig'),
        )),
        entry('berry_bushes', 'Berry Bushes', 'firmalife:plant/pineapple_bush', pages=(
            text('Firmalife adds some berry bushes with interesting uses.'),
            item_spotlight('firmalife:food/nightshade_berry', text_contents='First is nightshade. Nightshade is a poisonous berry. When put into soup, it makes poisonous $(thing)Stinky Soup$(). It is found between 200-400mm of rain and 7-24 C temperature in forests.'),
            item_spotlight('firmalife:food/pineapple', text_contents='Pineapple bushes are found 250-500mm of rainfall and 20-32 C temperature in forests. Pineapples are like any other fruit, except that they can be made into $(thing)Pineapple Leather$().'),
            crafting('firmalife:crafting/pineapple_fiber', text_contents='Pineapples that have been $(l:firmalife:firmalife/drying)Dried$() can be crafted into pineapple fiber.'),
            crafting('firmalife:crafting/pineapple_yarn', text_contents='Pineapple yarn is made by crafting a $(thing)Spindle$() with the fiber.'),
            loom_recipe('firmalife:loom/pineapple_leather', text_content='Finally, pineapple leather can be me woven in a $(l:tfc:mechanics/weaving)Loom$(). It is a plant substitute for regular leather than can be used for knapping, crafting, and other uses!')
        )),
        entry('chocolate', 'Chocolate', 'firmalife:food/dark_chocolate', pages=(
            text('$(thing)Chocolate-making$() takes a few processing steps, for not much of a reward. It\'s important to remember, when playing Firmalife, that being a chocolatier is for your personal enjoyment and pleasure, rather than for trying to extract maximum value from any given input.'),
            text('To start chocolate processing, cocoa beans must first be $(thing)roasted$() in an $(l:firmalife:firmalife/ovens)Oven$() to make $(thing)Roasted Cocoa Beans$(). Then, craft the roasted beans with a $(thing)Knife$() to split the beans into $(thing)Cocoa Powder$() and $(thing)Cocoa Powder$().'),
            text('The $(l:firmalife:firmalife/mixing_bowl)Mixing Bowl$() is used to mix cocoa powder, butter, and sweetener (sugar or honey) to make $(thing)Chocolate Blends$(). The ratio of cocoa butter to powder determines what comes out:$(br)$(li)1 Powder, 1 Butter, 1 Sweetener: Milk Chocolate$()$(li)2 Powder, 1 Sweetener: Dark Chocolate$()$(li)2 Butter, 1 Sweetener: White Chocolate$()'),
            drying_recipe('firmalife:drying/dark_chocolate', 'Finally, chocolate is dried on a $(l:firmalife:firmalife/drying)Drying Mat$() to make $(thing)Chocolate$().')
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

def detail_fruit_tree(fruit: str, text_contents: str = '', right: Page = None, an: str = 'a') -> Tuple[Page, Page, Page]:
    data = FRUITS[fruit]
    left = text('$(bold)$(l:the_world/climate#temperature)Temperature$(): %d - %d °C$(br)$(bold)$(l:mechanics/hydration)Rainfall$(): %d - %dmm$(br2)%s' % (data.min_temp, data.max_temp, data.min_rain, data.max_rain, text_contents), title=('%s tree' % fruit).replace('_', ' ').title()).anchor(fruit)
    if right is None:
        right = multimultiblock('The monthly stages of %s %s tree' % (an, fruit.replace('_', ' ').title()), *[two_tall_block_spotlight('', '', 'firmalife:plant/%s_branch[up=true,down=true]' % fruit, 'firmalife:plant/%s_leaves[lifecycle=%s]' % (fruit, life)) for life in ('dormant', 'healthy', 'flowering', 'fruiting')])
    return left, right, page_break()

if __name__ == '__main__':
    main()

