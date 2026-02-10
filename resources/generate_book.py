import format_lang
from constants import FRUITS
from patchouli import *
from argparse import ArgumentParser
from typing import Optional

BOOK_LANGUAGES = ('en_us', 'zh_cn', 'zh_tw', 'ja_jp', 'ru_ru')
MOD_LANGUAGES = ('en_us', 'de_de', 'ko_kr', 'ru_ru', 'uk_ua', 'zh_cn', 'zh_tw', 'ja_jp')

class LocalInstance:
    INSTANCE_DIR = None

    @staticmethod
    def wrap(rm: ResourceManager):
        def data(name_parts: ResourceIdentifier, data_in: JsonObject, root_domain: str = 'data'):
            return rm.write((LocalInstance.INSTANCE_DIR, '/'.join(utils.str_path(name_parts))), data_in)

        if LocalInstance.INSTANCE_DIR is not None:
            rm.data = data
            return rm
        return None

def main_with_args():
    parser = ArgumentParser('generate_book.py')
    parser.add_argument('--translate', type=str, default='en_us', help='The language to translate to')
    parser.add_argument('--local', type=str, default=None, help='The directory of a local .minecraft to copy into')
    parser.add_argument('--translate-all', type=str, default=None, help='If all languages should be translated')
    parser.add_argument('--format', type=str, default=None, help='Format the mod languages')
    parser.add_argument('--reverse-translate', type=str, default=None, help='Reverse a translation from the mod files.')

    args = parser.parse_args()

    if args.format:
        do_format()
        return

    if args.translate_all:
        do_format()
        for la in BOOK_LANGUAGES:
            main(la, args.local, False, reverse_translate=args.reverse_translate is not None)
    else:
        main(args.translate, args.local, False, reverse_translate=args.reverse_translate is not None)

def do_format():
    # format_lang.main(False, 'minecraft', BOOK_LANGUAGES)
    format_lang.main(False, 'firmalife', MOD_LANGUAGES)

def main(translate_lang: str, local_minecraft_dir: Optional[str], validate: bool, validating_rm: ResourceManager = None, reverse_translate: bool = False):
    LocalInstance.INSTANCE_DIR = local_minecraft_dir

    rm = ResourceManager('tfc', './src/main/resources')
    if validate:
        rm = validating_rm
    i18n = I18n(translate_lang, validate)

    print('Writing book at %s' % translate_lang)
    make_book(rm, i18n, local_instance=False, reverse_translate=reverse_translate)

    i18n.flush()

    if LocalInstance.wrap(rm):
        print('Copying %s book into local instance at: %s' % (translate_lang, LocalInstance.INSTANCE_DIR))
        make_book(rm, I18n(translate_lang, validate), local_instance=True)


# def main():
#     for language in BOOK_LANGUAGES:
#         rm = ResourceManager('tfc', '../src/main/resources')
#         i18n = I18n.create(language)
#
#         print('Writing book %s' % language)
#         make_book(rm, i18n)
#
#         i18n.flush()
#
#         if LocalInstance.wrap(rm) and language == 'en_us':
#             print('Copying into local instance at: %s' % LocalInstance.INSTANCE_DIR)
#             make_book(rm, I18n.create('en_us'), local_instance=True)
#
#         print('Done')

def make_book(rm: ResourceManager, i18n: I18n, local_instance: bool = False, reverse_translate: bool = False):
    book = Book(rm, 'field_guide', {}, i18n, local_instance, reverse_translate)
    book.template('smoking_recipe', custom_component(0, 0, 'SmokingComponent', {'recipe': '#recipe'}), text_component(0, 45))
    book.template('drying_recipe', custom_component(0, 0, 'DryingComponent', {'recipe': '#recipe'}), text_component(0, 45))

    book.category('firmalife', 'Firmalife', 'All about the Firmalife addon', 'firmalife:brick_oven_top', is_sorted=True, entries=(
        entry('differences_from_tfc', 'Differences from TFC', 'tfc:textures/item/food/wheat_bread.png', pages=(
            text('Firmalife makes a few changes to how things operate in regular TFC. This chapter exists to help direct you towards areas where this is very different.'),
            text('$(l:firmalife/cheese)Cheese$() is made through a more complex process. It can be placed in world, and has the option of aging in a $(l:firmalife/cellar)Cellar$().', title='Cheese Aging'),
            text('$(l:firmalife/bread)Bread$() is made through a more complex process, requiring yeast and sweetener. The regular TFC bread recipe makes flatbread, which is worse nutritionally.', title='Bread Making'),
            text('Firmalife has a greater emphasis on sugar. While it can still be obtained through sugar cane, consider using honey (from bees) or making sugar from beets!', title='Sweeteners'),
        )),
        entry('cheese', 'Cheese', 'firmalife:textures/item/food/gouda.png', pages=(
            text('Making $(thing)cheese$() in Firmalife is a little more involved than in vanilla TFC. There are two new kinds of milk: $(thing)Yak Milk$(), and $(thing)Goat Milk$(). These are obtained from milking the $(l:mechanics/animal_husbandry#yak)Yak$() and $(l:mechanics/animal_husbandry#goat)Goat$(), respectively. Milking the $(l:mechanics/animal_husbandry#cow)Cow$() still produces the old kind of milk.'),
            text('Like usual, milk must be $(thing)curdled$() first. To curdle milk, you need $(thing)Rennet$(). Rennet comes from the stomach of $(thing)Ruminant$() animals. This includes $(l:mechanics/animal_husbandry#yak)Yaks$(), $(l:mechanics/animal_husbandry#cow)Cows$(), $(l:mechanics/animal_husbandry#sheep)Sheep$(), $(l:mechanics/animal_husbandry#goat)Goats$(), and $(l:mechanics/animal_husbandry#musk_ox)Musk Oxen$(). To curdle milk, seal it in a $(l:mechanics/barrels)Barrel$() with Rennet for 4 hours.'),
            crafting('firmalife:crafting/cheesecloth', text_contents='Curdled milk must be converted to $(thing)Curds$() by sealing it in a barrel with $(thing)Cheesecloth$(). Cheesecloth is not reusable.').anchor('cheesecloth'),
            crafting('firmalife:crafting/cheddar_wheel', text_contents='You are ready to make $(thing)Dry Cheese$() if you wish. You can make $(thing)Rajya Metok$() from $(thing)Yak Curds$(), $(thing)Chevre$() from $(thing)Goat Curds$(), and $(thing)Cheddar$() from $(thing)Milk Curds$().'),
            crafting('firmalife:crafting/chevre_wheel', 'firmalife:crafting/rajya_metok_wheel'),
            text('Your other option is to make $(thing)Wet Cheeses$(). These are made by sealing the curds in a barrel of $(thing)Salt Water$(). You can make $(thing)Shosha$() from $(thing)Yak Curds$(), $(thing)Feta$() from $(thing)Goat Curds$(), and $(thing)Gouda$() from $(thing)Milk Curds$().'),
            text('Cheese wheels are blocks that should be placed in order to help them last. To improve their quality and shelf life, cheese wheels should be $(thing)Aged$() in a $(l:firmalife/cellar)Cellar$(). In order to obtain edible cheese from a cheese wheel, it should be sliced off the wheel by clicking $(item)$(k:key.use)$() with a $(thing)Knife$(). If the block is simply broken, the aging is lost!').anchor('aging'),
            multimultiblock('The aging stages of a wheel of Gouda: $(thing)Fresh$(), $(thing)Aged$(), and $(thing)Vintage$().', *[block_spotlight('', '', 'firmalife:gouda_wheel[age=%s]' % age) for age in ('fresh', 'aged', 'vintage')]),
        )),
        entry('climate_station', 'Climate Station', 'firmalife:climate_station', pages=(
            text('The $(thing)Climate Station$() is a block that manages the $(l:firmalife/greenhouse)Greenhouse$() and the $(l:firmalife/cellar)Cellar$(). When its corresponding multiblock is built correctly, it will show water on its sides. When it is invalid, it will show ice. The Climate Station must be placed on the first level of the multiblock, touching a part of the greenhouse structure. If it is not touching the structure, you may $(item)$(k:key.use)$() it with a block from that greenhouse type to tell it what to look for.'),
            multimultiblock('The climate station in its valid and invalid state.', *[block_spotlight('', '', 'firmalife:climate_station[stasis=%s]' % b) for b in ('true', 'false')]),
            text('$(li)It updates periodically on its own, or when placed/broken.$()$(li)When a climate station updates, it tells all the blocks inside the multiblock that they can operate. For example, it lets $(l:firmalife/cheese)Cheese$() begin aging.$()$(li)Press $(item)$(k:key.use)$() to force update the Climate Station and the blocks inside the multiblock.', 'Climate Station Tips'),
            crafting('firmalife:crafting/climate_station', text_contents='The climate station is crafted like this.'),
            text('A note about greenhouse construction: The largest greenhouse is 31x31x31, centered on the climate station, or 15 blocks in each direction. Centering the climate station in the greenhouse will allow you to take advantage of the full size range. You can also internally subdivide a greenhouse into multiple by using walls and multiple climate stations to make it larger.'),
            empty_last_page(),
        )),
        entry('cellar', 'Cellars', 'firmalife:sealed_bricks', pages=(
            text('The $(thing)Cellar$() is a multiblock device controlled by a $(l:firmalife/climate_station)Climate Station$(). The Cellar multiblock\'s only requirement is that it be in an enclosed area surrounded by $(thing)Sealed Bricks$() or $(thing)Sealed Brick Doors$() on all sides. The Climate Station must be placed on the first level of the cellar, touching a wall.'),
            multiblock('An Example Cellar', 'This is just one of many cellars that you could make!', True, multiblock_id='firmalife:cellar'),
            crafting('firmalife:crafting/sealed_bricks', 'firmalife:crafting/sealed_brick_door'),
            text('$(thing)Beeswax$() is obtained from $(l:firmalife/beekeeping)Beekeeping$().$(br)Cellars are used for $(l:firmalife/cheese#aging)Aging Cheese$().'),
            text('The cellar is used for food preservation, for example by using $(l:firmalife/food_shelves)Food Shelves$() and $(l:firmalife/hangers)Hangers$(). The cellar performs better in environments with cooler average temperatures for food preservation. Below 0 degrees, decay modifiers work slightly better. Below -12 degrees, they perform much better.'),
            empty_last_page()
        )),
        entry('food_shelves', 'Food Shelves', 'firmalife:wood/food_shelf/pine', pages=(
            text('The $(thing)Food Shelf$() is a device for storing food. It can only be used in a valid $(l:firmalife/cellar)Cellar$(). Food shelves can contain a full stack of one food item. Adding and removing the item can be done with $(item)$(k:key.use)$(). Items in valid food shelves receive a decay modifier that is better than vessels.'),
            crafting('firmalife:crafting/wood/food_shelf/acacia', text_contents='The food shelf is made from planks and lumber.'),
        )),
        entry('hangers', 'Hangers', 'firmalife:wood/hanger/pine', pages=(
            text('The $(thing)Hanger$() is a device for storing meat or garlic. It can only be used in a valid $(l:firmalife/cellar)Cellar$(). Food shelves can contain a full stack of one item. Adding and removing the item can be done with $(item)$(k:key.use)$(). Items in valid food shelves receive a decay modifier that is better than shelves or vessels.'),
            crafting('firmalife:crafting/wood/hanger/acacia', text_contents='The hanger is made from planks and string.'),
        )),
        entry('jarbnet', 'Jarbnets', 'firmalife:wood/jarbnet/pine', pages=(
            text('The jarbnet is a cosmetic storage block for $(l:tfc:mechanics/jarring)Jars$(), Candles, and Jugs. It can be opened and closed by clicking with an empty hand and $(item)$(k:key.sneak)$() pressed. If candles are inside, it can be lit to produce a small amount of light.'),
            crafting('firmalife:crafting/wood/jarbnet/acacia'),
        )),
        entry('greenhouse', 'Greenhouse', 'firmalife:sealed_bricks', pages=(
            text('The $(thing)Greenhouse$() is a multiblock device controlled by a $(l:firmalife/climate_station)Climate Station$(). It allows growing crops year round. The Greenhouse has an array of types and blocks to choose from. However, building a greenhouse is quite simple. Like the $(l:firmalife/cellar)Cellar$(), it should be an enclosed area of blocks belonging to the same $(thing)Greenhouse Type$(). The floor of the greenhouse may be non-air block.'),
            text('The walls of greenhouses must be solid faces. Panel walls should be placed so that the face that is on the exterior of the block (eg. the face you can place a torch on) faces into the greenhouse. The same applies for roofs, except that slabs are always considered valid roof blocks. Trapdoors and doors are also always valid and require no special placement.'),
            text('$(thing)Greenhouse Types$() are families of greenhouse blocks that can be used interchangeably in a greenhouse. Most greenhouse blocks $(thing)age$(). For example, $(thing)Treated Wood$() greenhouse blocks become $(thing)Weathered Treated Wood$() blocks. Since both of those block types belong to the same greenhouse type, your greenhouse will continue to function. Stainless steel greenhouses do not age.'),
            text('Upgrading your greenhouse type allows using more planter types. Each tier grants more planters. $(br)$(br)$(li)Treated Wood: Quad Planters. $()$(li)Copper: Large and Hydroponic Planters.$()$(li)Iron: Rusted, Trellis$() $(li)Stainless Steel (no further upgrade)$()', 'Greenhouse Types'),
            text('There are four types of regular $(thing)Greenhouse Blocks$(): Walls, Doors, Roofs, and Roof Tops. Roofs and Roof Tops are stairs and slabs, respectively. There are also thinner versions of these blocks, also known as panel walls, trapdoors, and panel roofs. These can be combined however you choose to form the structure of the greenhouse.'),
            multimultiblock('An example greenhouse, in each main type.', *[multiblock('', '', True, multiblock_id='firmalife:%s_greenhouse' % g) for g in ('treated_wood', 'copper', 'iron', 'stainless_steel')]),
            text('There are many blocks that operate inside Greenhouses:$(br)$(li)$(l:firmalife/planters)Planters$(), for growing crops$(), and $(br)$(li)$(l:firmalife/irrigation)Sprinklers$(), various devices that add water to planters.'),
            text('The next four pages contain recipes for the main greenhouse block types. While they are only shown for Iron greenhouses, the iron rods in the recipe can be replaced with $(thing)Treated Lumber$(), $(thing)Copper Rods$(), or $(thing)Stainless Steel Rods$(). For information on Stainless Steel, see $(l:firmalife/stainless_steel)this linked page$().'),
            crafting('firmalife:crafting/iron_greenhouse_wall', 'firmalife:crafting/iron_greenhouse_roof'),
            crafting('firmalife:crafting/iron_greenhouse_door', 'firmalife:crafting/iron_greenhouse_roof_top'),
            crafting('firmalife:crafting/iron_greenhouse_trapdoor', 'firmalife:crafting/iron_greenhouse_panel_roof'),
            crafting('firmalife:crafting/iron_greenhouse_panel_wall', 'firmalife:crafting/iron_greenhouse_port'),
        )),
        entry('irrigation', 'Irrigation', 'firmalife:sprinkler', pages=(
            text('The $(thing)Sprinkler$() is a device that sprinkles water in a 5x6x5 area centered on the block below the sprinkler block. You know it is working when it drips out water particles. Sprinklers placed facing up irrigate the same 5x6x5 area above.'),
            anvil_recipe('firmalife:anvil/sprinkler', 'The sprinkler is made with a $(thing)Copper Sheet$().'),
            text('Sprinklers must be connected to a system of pipes that feed it water in order to work. This is done by connecting a series of $(thing)Copper Pipes$() to them. Copper Pipes transport water up to 32 blocks to a sprinkler. They are connected to $(thing)Irrigation Tanks$() or $(thing)Pumping Stations$().'),
            anvil_recipe('firmalife:anvil/copper_pipe', 'The copper pipe is made with a sheet.'),
            two_tall_block_spotlight('', '', 'firmalife:pumping_station', 'firmalife:irrigation_tank'),
            text('Pumping stations must be above a source block of water in order to work, and be connected to mechanical power. Irrigation tanks can also serve water through their ports on the sides, provided that they are stacked at most 3 blocks high above a pumping station on other tanks.'),
            crafting('firmalife:crafting/pumping_station', 'firmalife:crafting/irrigation_tank'),
            crafting('firmalife:crafting/oxidized_copper_pipe', text_contents='Oxidized pipes are the same as regular copper pipes, except they do not connect to the other kind of pipe.'),
            crafting('firmalife:crafting/iron_greenhouse_port', text_contents='Greenhouse ports have a single pipe inside of them. They can be used to pass water through the walls of greenhouses!'),
            empty_last_page(),
        )),
        entry('planters', 'Planters', 'firmalife:large_planter', pages=(
            text('$(thing)Planters$() are used to grow crops inside a $(l:firmalife/greenhouse)Greenhouse$(). To see the status of a planter, you can look at it while holding a $(thing)Hoe$(). Crops in planters consume $(l:mechanics/fertilizers)Nutrients$() in a similar way to $(l:mechanics/crops)Crops$(). Planters should be placed inside a valid Greenhouse and activated with a $(l:firmalife/climate_station)Climate Station$(). Planters need at least some natural sunlight to work.').anchor('planters'),
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
            crafting('firmalife:crafting/hydroponic_planter', text_contents='$(thing)Hydroponic Planters$() grow rice and cranberry bushes. They work the same as a quad planter, except that they do not need to be watered.'),
            text('Hydroponic Planters must instead, have a $(li)$(l:firmalife/irrigation)Sprinkler Pipe$() below them that is supplying water. Without the pipe they will not grow. There is no hard limit to how many hydroponic planters may be fed by one station.'),
            crafting('firmalife:crafting/sweeper', text_contents='$(thing)Sweepers$() are used to harvest planters other than Quad and Hydroponic planters. They must be connected to a rotating $(thing)Axle$() on their bottom face.'),
            crafting('firmalife:crafting/picker', text_contents='$(thing)Pickers$() can be powered via redstone signal from above. They extend down to pick the crops from Quad and Hydroponic planters.')
        )),
        entry('beekeeping', 'Beekeeping', 'firmalife:beehive', pages=(
            text('$(thing)Bees$() are a wild insect that can be farmed as livestock. They are prized for their ability to produce honey, beeswax, and other products. All bees need to live in some form of nest. This section will teach you all you need to know for the care and keeping of bees.'),
            two_tall_block_spotlight('Wild Beehive', 'Wild beehives spawn in forests between 5-30°C and 150-450mm of groundwater.', 'firmalife:wild_beehive[facing=north,honey=true]', 'tfc:wood/log/oak[axis=z]'),
            text('Bees, in general, are active above 11°C. A $(thing)Wild Beehive$() will always contain bees upon world generation, and will not lose the bees unless disturbed. If time has passed and the bees are active, the wild beehive will show that it is dripping with honey.'),
            text('Breaking a wild beehive or the tree it is attached to will always cause bees to attack nearby players. This takes the form of a $(thing)Swarm$() effect. The bee swarm effect can be mitigated by wearing a full suit of $(thing)Bee Armor$() or by moving underwater. Wild beehives drop $(thing)Honey$(), $(thing)Beeswax$(), and the useful $(thing)Wild Honeycomb$() item.'),
            crafting('firmalife:crafting/skep', text_contents='The $(thing)Skep$() is the smallest man-made beehive.'),
            text('The most useful feature of the skep is that it can be picked up and moved by the player. However, the player can only carry one skep at a time without becoming $(thing)Overburdened$(). If a wild beehive within 15 blocks is $(thing)active$(), within a day it should start a $(thing)Swarm$(). A swarm is signified by a line of particles between two beehives.'),
            text('When a swarm is complete, the skep will now contain a $(thing)Queen$(). Holding a hoe and looking at the hive will show you this information. The Queen represents the existence of a bee colony, and she has a number of data points: her age, species, traits, as well as any diseases or infections the colony has.'),
            text('On their own, skeps are only moderately useful for maintaining bees. This is because skeps can only contain 1 piece of honey at a time. $(thing)Raw Honey$() can be added or removed from a skep with $(item)$(k:key.use)$(). Note that to avoid angering bees, you should harvest only when it is dark outside.'),
            crafting('firmalife:crafting/beehive_frame', 'firmalife:crafting/beehive'),
            text('$(thing)Wooden Beehives$() are the main device used for beekeeping. They contain up to four $(thing)Frames$(). A frame can contain 1 piece of $(thing)Raw Honey$() in it. To get your bees into a Wooden Beehive, place your $(thing)Skep$() with a queen inside within 5 blocks of it, and make sure the beehive has at least one frame, and that it is warm enough. The swarm will follow soon after, and last for a day.'),
            text('Beehives know about a 5 block square radius surrounding the hive. In order to produce honey, this radius must contain at least 10 flowers. The chance of producing honey scales up based on the amount of flowers, with a max of 60. Bee traits also play a role in the chance to make honey. Honey can only be deposited in an $(thing)Empty Beehive Frame$().'),
            crafting('firmalife:crafting/beeswax', text_contents='$(thing)Scraping$() a beehive frame (right clicking the item with a knife also works) makes $(thing)Beeswax$().'),
            crafting('firmalife:crafting/centrifuge', text_contents='The $(thing)Centrifuge$() is used to process $(thing)Scraped Beehive Frames$() into $(thing)Raw Honey$().'),
            text('The $(thing)Centrifuge$() can be operated via $(item)$(k:key.use)$() or via mechanical power from an axle above the block. $(item)$(k:key.use)$() to put Scraped Beehive Frames in. $(thing)Raw Honey$() will be spawned outside the Centrifuge upon completion. The leftover frames can be reused in a Beehive.'),
            crafting('firmalife:crafting/sugared_beehive_frame', 'firmalife:crafting/honeyed_beehive_frame'),
            text('Bees don\'t just produce honey for you, they produce it to survive! When bees have no frames available, or if it is too cold (read the hoe tooltip to see) they will consume honey every 12 days by default. When they are out of honey, they have a chance every day to $(thing)die$().'),
            text('$(thing)Sugared Beehive Frames$() as well as manually-filled $(thing)Filled Beehive Frames$() can be manually added into hives to help sustain bees over winter. This is why skeps have trouble surviving over winter--they can only contain one piece of honey, and have no frames.'),
            crafting('firmalife:crafting/insulating_beehive_frame', text_contents='Adding a $(thing)Insulating Beehive Frame$() adds 2°C of extra temperature resistance to a hive. Adding multiple has no further effect.'),
            text('Colonies can still $(thing)Swarm$() from Wooden Beehives. The beneficial type of swarming involves $(thing)Splitting$() a hive. A hive that is at least 24 days old (by default), warm, full of honey, and free of $(thing)Genetic Diseases$() can split into two new colonies if a nearby hive is available. This also results in $(thing)Genetic Mutation$().'),
            text('Swarming can also be caused by a lack of food in the hive. If a hive sees another hive with honey, and lacks its own, it will in some cases try to swarm to that hive. Bees infected with the $(thing)Varroa$() parasite will also attempt to invade other hives, taking them over.'),
            text('If no wild bees are available, bees can also be attracted to Skeps via $(thing)Bee Bait$(). This takes the form of $(thing)Wild Honeycomb$() and $(thing)Aromatic Honeycomb$(). Add these to a $(thing)Skep$() with $(item)$(k:key.use)$() and surround the Skep with at least 30 flowers. There is a 1 / 8 chance daily to attract a bee in this case.'),
            crafting('firmalife:crafting/aromatic_honeycomb', text_contents='The $(thing)Aromatic Honeycomb$() is an alternative to the Wild Honeycomb made from herbs.'),
            crafting('firmalife:crafting/jarring_food/raw_honey', text_contents='Honey lasts forever if stored in a $(thing)Jar$().'),
            text('This chapter covered the main aspects of beekeeping. For information on species, traits, genetic diseases, and parasitic infections, see the next chapter, $(l:firmalife/beekeeping_reference)Beekeeping Reference$().'),
        )),
        entry('beekeeping_reference', 'Beekeeping Reference', 'firmalife:textures/item/queen_bee.png', pages=(
            text('This chapter is a continuation of the $(l:firmalife/beekeeping)Beekeeping$() chapter.$(br)The queen bee of a hive has certain abilities. These are passed on and changed when the hive splits. New abilities can only be obtained from the wild, except for Calmness, which has a change to appear during a split in captivity.'),
            text('$(li)$(thing)Hardiness$(): Allows bees to produce honey at lower temperatures. Each hardiness grants an extra 2°C.$()$(li)$(thing)Production$(): Improves the speed of honey production.$()$(li)$(thing)Mutant$(): Increases variability in the traits passed during splitting swarms$().$(li)$(thing)Fertility$(): Increases likelihood of swarming.$()', 'List of Abilities'),
            text('$(li)$(thing)Crop Affinity$(): Likelihood of spreading a small amount of nutrients to nearby crops.$()$(li)$(thing)Nature Restoration$(): Causes new flowers and water plants to spawn around the hive.$()$(li)$(thing)Calmness$(): Decreases likelihood of bees attacking you$().$(li)$(thing)Infection Resistance$(): Decreases likelihood of developing genetic diseases and parasitic infections.'),

            text('Bees with high Mutant ability have a chance of developing a $(thing)Genetic Disease$(). Bees with a genetic disease cannot reproduce.'),
            text('Bees subject to improper conditions can develop $(thing)Parasitic Infections$(). These conditions do not have to do with their species and may spontaneously occur in wild bees. These conditions include: the hive touching any other block besides for the block below it; temperatures below -18 or above 27°C; rainfall below 50 or above 470mm. Infected bees cannot make honey.'),

            text('$(thing)Bee Species$() are found in different climates, and their conditions describe what you may get from a $(thing)Wild Beehive$() or an attracted wild swarm. Some species are rarer than others.', title='Bee Species'),
            text('$(li)$(thing)Western Honey Bee$(): Calmness, Production. 5-20°C, 100-400mm. Extremely common. Western bees also spawn when no other bee species is eligible to spawn.$()$(li)$(thing)Asian Honey Bee$(): Fertility, Nature Restoration. 10-30°C, 250-400mm. Common.$()$(li)$(thing)Giant Honey Bee$(): Fertility. 15-30°C, 300-450mm. Common.$()'),
            text('$(li)$(thing)Dwarf Honey Bee$(): Fertility, Production. 20-30°C, 300-400mm. Common.$()$(li)$(thing)Black Dwarf Honey Bee$(): Mutant, Nature Restoration. 12-22°C, 150-350mm. Uncommon.$()$(li)$(thing)Koschevnikov\'s Honey Bee$(): Hardiness, Production, Nature Restoration. 12-16°C, 200-375mm. Very Rare.$()$(li)$(thing)Himalayan Giant Honey Bee$(): Hardiness, Mutant, Production. 10-19°C, 150-250mm, y>96. Rare.$()'),
            text('$(li)$(thing)Philippine Honey Bee$(): Calmness, Crop Affinity. 15-20°C, 350-450mm. Very Rare.$()$(li)$(thing)Borneo Mountain Honey Bee$(): Hardiness, Infection Resistance. 13-22°C, 150-400mm, y>96. Common.$()$(li)$(thing)Indonesian Giant Honey Bee$(): Nature Restoration, Fertility, Infection Resistance. 16-20°C, 350-420mm. Uncommon.$()$(li)$(thing)Africanized Honey Bee$(): Hardiness. 10-25°C. 100-320mm. Common.$()'),

            crafting('firmalife:crafting/beekeeper_boots', 'firmalife:crafting/beekeeper_leggings'),
            crafting('firmalife:crafting/beekeeper_chestplate', 'firmalife:crafting/beekeeper_helmet'),
            empty_last_page(),
        )),
        entry('stainless_steel', 'Stainless Steel', 'firmalife:metal/ingot/stainless_steel', pages=(
            text('$(thing)Stainless Steel$() and $(thing)Chromium$() are $(thing)Steel-tier$() metals added by Firmalife. They are used in the construction of $(l:firmalife/greenhouse)Stainless Steel Greenhouses$().'),
            alloy_recipe('Stainless Steel', 'firmalife:metal/ingot/stainless_steel', ('Chromium', 20, 30), ('Nickel', 10, 20), ('Steel', 60, 80), text_content=''),
            item_spotlight('firmalife:ore/small_chromite', text_contents='Chromite is an ore that is melted to obtain Chromium. It is found in $(thing)Igneous Intrusive$() and $(thing)Metamorphic$() rocks.'),
            text('$(li)Granite$()$(li)Diorite$()$(li)Gabbro$()$(li)Slate$()$(li)Phyllite$()$(li)Schist$()$(li)Gneiss$()$(li)Marble$()', 'All Chromium Rocks')
        )),
        entry('drying', 'Drying', 'firmalife:drying_mat', pages=(
            text('The $(thing)Drying Mat$() is used to dry items. It is made with $(thing)Fruit Leaves$(), which are obtained from breaking the leaves of $(thing)Fruit Trees$().'),
            crafting('firmalife:crafting/drying_mat', text_contents='The recipe for the drying mat.'),
            text('To use the drying mat, place it out on the sun and add an item to it with $(item)$(k:key.use)$(). After a half day, it will be dried. If it rains, the drying process must start over.'),
            crafting('firmalife:crafting/solar_drier', text_contents='The solar drier functions the same as the drying mat, but 12x as fast.'),
            text('Drying mats can be automated. Pushing a piston head against a drying mat will pop the item off. Dropping an item onto a drying mat will place it on the mat.'),
            drying_recipe('firmalife:drying/dry_fruits', 'Drying fruit is a common use of the drying mat. Dried fruit is used in some recipes, and lasts longer.'),
            drying_recipe('firmalife:drying/food/tofu', 'Tofu is made using a drying mat.'),
            empty_last_page()
        )),
        entry('smoking', 'Smoking', 'tfc:textures/item/food/venison.png', pages=(
            text('Wool string is used to hang items for $(thing)Smoking$(). To place it, just use $(item)$(k:key.use)$().'),
            two_tall_block_spotlight('Smoking', 'A piece of string above a firepit.', 'tfc:firepit[lit=true]', 'firmalife:wool_string'),
            text('Smoking is used to preserve $(thing)Meat$() and $(l:mechanics/dairy)Cheese$(). To smoke meat, it must have first been $(thing)Brined$() by sealing it in a $(thing)Barrel$() with $(thing)Brine$(). You may also salt it first. Cheese does not have this requirement.'),
            text('To start the smoking process, add the item to the string above a firepit. The firepit must be within four blocks, directly underneath the string. The string should begin to generate some smoke if it is working. It is important to note that the firepit must only be burned with $(thing)Logs$(). Using something like $(thing)Peat$() will instantly give your food the $(thing)Disgusting$() trait!'),
            text('The smoking process takes 8 in-game hours. Happy smoking!'),
            empty_last_page()
        )),
        entry('ovens', 'Ovens', 'firmalife:brick_oven_top', pages=(
            text('$(thing)Ovens$() are a great way of cooking lots of food in a way that improves their shelf life. Oven-baked food decays at 90% of the rate of regular food. Ovens are a multiblock structure consisting of a $(thing)Bottom Oven$(), $(thing)Top Oven$(), and optionally $(thing)Chimneys$(). These blocks start off as clay, and must be $(thing)Cured$() by raising their temperature to a certain amount for long enough.$(br)$(l:firmalife/oven_appliances)Oven Appliances$() extend oven functionality.'),
            knapping('firmalife:knapping/clay_oven_top', 'The recipe for the top oven.'),
            knapping('firmalife:knapping/clay_oven_bottom', 'The recipe for the bottom oven.'),
            knapping('firmalife:knapping/clay_oven_chimney', 'The recipe for the oven chimney    .'),
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
            }) for pref in ('brick_', 'clay_')]),
            text('The Bottom Oven is used to hold fuel, which may only be logs. Press $(item)$(k:key.use)$() to add or remove them. The bottom oven is also the part of the oven which may be lit with a $(thing)Firestarter$() or other tool. It transfers heat contained in it to the top oven.'),
            text('The Top Oven contains the items that are being cooked. It will draw heat from the Bottom Oven and slowly release it over time. This means that even if your fuel runs out, your Top Oven can continue to work for a little while. Adding items to it is as simple as pressing $(item)$(k:key.use)$(). Remember to use a $(thing)Peel$() to remove the items after.'),
            text('Curing Oven blocks is easy, but requires patience. Simply start running your Bottom Oven as you would normally, and then wait. If an oven block is above 600 degrees for about 80 seconds, it will cure itself and any oven blocks around it. The curing effect will pass all the way up chimneys nearby.'),
            crafting('firmalife:crafting/oven_insulation', text_contents='Crafting oven insulation for your Top or Bottom Oven allows you to remove the need for insulating it with blocks. It does not remove the need for the chimney. Use $(item)$(k:key.use)$() to apply it.'),
            crafting('firmalife:crafting/brick_countertop', text_contents='Countertops are aesthetic blocks that count as oven insulation, and have an appearance that matches that of oven blocks. They are a nice aesthetic choice for your kitchen.'),
            text('Ovens also have $(thing)Finishes$() that can be used to change their appearance. These finishes are applied to the basic brick stage of the oven (or brick blocks themselves), and are cosmetic. Finishes can be mixed and matched. They are applied with $(item)$(k:key.use)$().'),
            crafting('firmalife:crafting/rustic_bricks', 'firmalife:crafting/rustic_finish'),
            crafting('firmalife:crafting/stone_finish', 'firmalife:crafting/tile_brick'),
            crafting('firmalife:crafting/tile_bricks', 'firmalife:crafting/tile_finish'),
            empty_last_page()
        )),
        entry('oven_appliances', 'Oven Appliances', 'firmalife:vat', pages=(
            text('$(l:firmalife/ovens)Ovens$() have a number of devices that interact with them, that extend their functionality. This is because ovens are modular in nature.'),
            crafting('firmalife:crafting/clay_oven_hopper', text_contents='The $(thing)Oven Hopper$() will input logs into any Bottom Oven that it is facing. It holds 16 logs (4 stacks of 4, like a log pile), and its inventory is fed by dropping items in the top. It can also be fed via automation from other mods.'),
            crafting('firmalife:crafting/ashtray', text_contents='The $(thing)Ashtray$() collects $(thing)Wood Ash$() when placed below a $(thing)Bottom Oven Block$(). There is a 0.5 chance it gains ash when fuel is consumed. Ash is extracted with $(item)$(k:key.use)$() and inserted via attacking it.'),
            crafting('firmalife:crafting/vat', text_contents='The $(thing)Vat$() produces some select boiling recipes in bulk. It has one slot for items, and 10,000mB of fluid space, similar to a barrel.').anchor('vat'),
            text('For example, the vat can be used to make $(thing)Olive Oil Water$() using a ratio of 1 Olive Paste to 200 mB Water. To use a vat, $(item)$(k:key.use)$() it with fluids and items to add them to the inventory. With an empty hand and $(item)$(k:key.sneak)$() held, click to seal and unseal the vat. A vat will not boil until it is sealed.'),
            text('Vats should be placed on the block above a $(thing)Bottom Oven$(). If the vat would overflow on completion of the recipe, it will not boil, so be sure not to overfill it -- especially with recipes that produce more fluid than they consume! Vats can be opened and closed with $(thing)Redstone$().'),
            two_tall_block_spotlight('', '', 'firmalife:brick_oven_bottom', 'firmalife:vat'),
            crafting('firmalife:crafting/jarring_station', text_contents='The $(thing)Jarring Station$() is used to fill jars from the vat. The jarring station has a spout on one side that should point to the station.'),
            text('Using the Vat, Sugar Water can be made by adding sweetener to water. When the vat finishes, the fluid will change color. Clicking it with an $(thing)Empty Jar With Lid$() will fill it.'),
            text('With $(item)$(k:key.use)$(), add empty jars with lids to the jarring station. When it detects jam, it will automatically fill the jars with $(thing)Jam$() and seal them, requiring 500 mB of input fluid per jar.'),
            text('Pots and Grills from TFC can be placed on top of a $(thing)Bottom Oven$(). These devices will get heat automatically from the bottom oven. These will work exactly like the regular pot and grill, except for how they receive heat from below.'),
            empty_last_page()
        )),
        entry('bread', 'Bread', 'tfc:textures/item/food/barley_bread.png', pages=(
            text('To make $(thing)Bread$(), one first must get $(thing)Yeast$(). To get your first yeast, seal $(l:firmalife/drying)Dried Fruit$() in a Barrel of $(thing)Water$(). After three days, $(thing)Yeast Starter$() will form.$(br)From now on, your yeast can be fed by sealing Yeast Starter in a Barrel with $(thing)Flour$(). This causes it to multiply. 1 flour per 100mB of Yeast produces 600mB of Yeast. That\'s a good deal!'),
            crafting('firmalife:crafting/food/barley_dough', text_contents='Yeast Starter, Sweetener, and Flour can be combined to make $(thing)Dough$(). Dough can be cooked like normal to produce $(thing)Bread!$().'),
            crafting('firmalife:crafting/food/barley_slice', text_contents='Once baked, you can use a $(thing)knife$() to cut bread into $(thing)slices$(). These can then either be used for $(l:tfc:mechanics/sandwiches)sandwich making$(), or cooked into $(thing)toast$() which can be spread with $(thing)butter$() or preserves.', title='Sliced Bread'),
            crafting('firmalife:crafting/food/toast_with_butter', 'firmalife:crafting/food/toast_with_jam', title='Toast')
        )),
        entry('more_fertilizer', 'More Fertilizer Options', 'firmalife:compost_tumbler', pages=(
            text('Given a greater need for fertilization in Firmalife, there are more options for getting $(l:mechanics/fertilizers)fertilizers$().'),
            text('$(thing)Compost Tumblers$() are a great way to produce more fertilizer. They must be connected to mechanical power in order to work. It can only be interacted with when not powered, so consider connecting it to a clutch!'),
            crafting('firmalife:crafting/compost_tumbler', text_contents='The compost tumbler is unique in that it takes more types of compost, and does not require precise ratios in order to work.'),
            text('The tumbler can take green and brown items like a regular composter. It can also take pottery sherds, charcoal, fish, and bones in small amounts.'),
            crafting('firmalife:crafting/pottery_sherd', text_contents='Smashing pottery with a hammer yields sherds.'),
            text('Green and brown items count the same, being on the range 1-4, but the new additions like fish always count for 1. Adding too much weird stuff to the composter causes it to produce rotten compost. Further, you will not know it is rotten until the very end! If the compost is more than fifteen percent bones, fish, or pottery, or more than twenty percent charcoal. it will rot. Or, if there are 10 or more green units than brown units, it will rot.'),
            text('Favorable amounts of certain additions can extend or shorten the length of time it takes for the compost to complete. Play around with it and see what happens.$(br)If 32 units are in the composter, 3 compost will be produced. If at least 24, 2 compost will be made. If 16 or more, 1 will be made. Below that, and there will be no compost.'),
            empty_last_page(),
        )),
        entry('mixing_bowl', 'Mixing Bowl', 'firmalife:mixing_bowl', pages=(
            text('The mixing bowl is a way of mixing items and fluids together in a friendly way. $(item)$(k:key.use)$() on it with a $(thing)Spoon$() to add it to the bowl, which allows it to operate.'),
            crafting('firmalife:crafting/mixing_bowl', text_contents='Requires a $(thing)Spoon$() to use.'),
        )),
        entry('herbs_and_spices', 'Herbs and Spices', 'firmalife:spice/basil_leaves', pages=(
            text('In Firmalife, there are a number of small plants you can collect and grow on your own, some of which can be used for cooking. These are called herbs. These plants, once harvested, may be cultivated in a $(thing)Greenhouse$(), in which they consume 20 points of N, P, and K.'),
            text('Open to the next page to see information on all herb spawning locations and what they can be used for.'),
            block_spotlight('', 'Basil spawns between 0-32°C and 100-500mm of groundwater.', 'firmalife:plant/basil'),
            crafting('firmalife:crafting/spice/basil_leaves', text_contents='Basil leaves, made from basil, are used in pizza.'),
            block_spotlight('', 'Bay Laurel spawns between 10-32°C and 100-300mm of groundwater.', 'firmalife:plant/bay_laurel'),
            crafting('firmalife:crafting/spice/bay_leaves', text_contents='Bay laurel is chopped into bay leaves and used with cilantro to make flavorful rice in a pot.'),
            block_spotlight('', 'Cardamom spawns between 17-24°C and 330-500mm of groundwater.', 'firmalife:plant/cardamom'),
            quern_recipe('firmalife:quern/spice/ground_cardamom', 'Ground Cardamom is made from grinding the cardamom plant, which is used to make Rice Pilaf.'),
            block_spotlight('', 'Cilantro spawns between 10-24°C and 90-300mm of groundwater.', 'firmalife:plant/cilantro'),
            crafting('firmalife:crafting/spice/chopped_cilantro', text_contents='Cilantro can be chopped and used for salsa and used with bay leaves in a pot to make flavorful cooked rice.'),
            block_spotlight('', 'Cumin spawns between 16-27°C and 100-300mm of groundwater.', 'firmalife:plant/cumin'),
            quern_recipe('firmalife:quern/spice/ground_cumin', 'Ground Cumin is made from grinding the cumin plant, which is used to make Carne Asada.'),
            block_spotlight('', 'Oregano spawns between 20-27°C and 90-350mm of groundwater.', 'firmalife:plant/oregano'),
            crafting('firmalife:crafting/food/raw_lasagna', text_contents='Oregano is used for lasagna.'),
            block_spotlight('', 'Pimento spawns between 18-24°C and 200-400mm of groundwater.', 'firmalife:plant/pimento'),
            quern_recipe('firmalife:quern/spice/allspice', 'Allspice is made from grinding the pimento plant, which is used to make Spiced Flour for use in Pumpkin Pie.'),
            block_spotlight('', 'Vanilla spawns between 22-30°C and 350-500mm of groundwater.', 'firmalife:plant/vanilla'),
            drying_recipe('firmalife:drying/spice/vanilla', 'Vanilla must be dried, and then can be made into ice cream.'),
        )),
        entry('fruit_trees', 'Firmalife Fruits', 'firmalife:plant/fig_sapling', pages=(
            text('Firmalife adds some fruiting plants on top of those added by TFC.'),
            text('To improve readability, entries start on the next page.'),
            *detail_fruit_tree('cocoa', 'Cocoa trees are used to make $(l:firmalife/chocolate)Chocolate$(). They fruit in June.'),
            *detail_fruit_tree('fig', 'They fruit in May.'),
        )),
        entry('berry_bushes', 'Berry Bushes', 'firmalife:plant/pineapple_bush', pages=(
            text('Firmalife adds some berry bushes. For information on wild grape bushes, see $(l:firmalife/wine)winemaking$().'),
            item_spotlight('firmalife:food/nightshade_berry', text_contents='First is nightshade. Nightshade is a poisonous berry. When put into soup, it makes poisonous $(thing)Stinky Soup$(). It is found between 200-400mm of rain and 7-24 C temperature in forests.'),
            item_spotlight('firmalife:food/pineapple', text_contents='Pineapple bushes are found 250-500mm of rainfall and 20-32 C temperature in forests. Pineapples are like any other fruit, except that they can be made into $(thing)Pineapple Leather$().'),
            crafting('firmalife:crafting/pineapple_fiber', text_contents='Pineapples that have been $(l:firmalife/drying)Dried$() can be crafted into pineapple fiber.'),
            crafting('firmalife:crafting/pineapple_yarn', text_contents='Pineapple yarn is made by crafting a $(thing)Spindle$() with the fiber.'),
            loom_recipe('firmalife:loom/pineapple_leather', text_content='Finally, pineapple leather can be me woven in a $(l:tfc:mechanics/weaving)Loom$(). It is a plant substitute for regular leather than can be used for knapping, crafting, and other uses!')
        )),
        entry('chocolate', 'Chocolate', 'firmalife:textures/item/food/dark_chocolate.png', pages=(
            text('$(thing)Chocolate-making$() takes a few processing steps, for not much of a reward. It\'s important to remember, when playing Firmalife, that being a chocolatier is for your personal enjoyment and pleasure, rather than for trying to extract maximum value from any given input.'),
            text('To start chocolate processing, cocoa beans must first be $(thing)roasted$() in an $(l:firmalife/ovens)Oven$() to make $(thing)Roasted Cocoa Beans$(). Then, craft the roasted beans with a $(thing)Knife$() to split the beans into $(thing)Cocoa Powder$() and $(thing)Cocoa Butter$().'),
            text('The $(l:firmalife/mixing_bowl)Mixing Bowl$() is used to mix cocoa powder, butter, and sweetener (sugar or honey) to make $(thing)Chocolate Blends$(). The ratio of cocoa butter to powder determines what comes out:$(br)$(li)1 Powder, 1 Butter, 1 Sweetener: Milk Chocolate$()$(li)2 Powder, 1 Sweetener: Dark Chocolate$()$(li)2 Butter, 1 Sweetener: White Chocolate$()'),
            drying_recipe('firmalife:drying/food/dark_chocolate', 'Finally, chocolate is dried on a $(l:firmalife/drying)Drying Mat$() to make $(thing)Chocolate$().')
        )),
        entry('wine', 'Winemaking', 'firmalife:textures/item/food/white_grapes.png', pages=(
            text('$(thing)Winemaking$() is the science of turning grapes into alcohol. There is time spent gathering resources, and spending time crafting those resources together, as well as time spent $(thing)enjoying$() the product. Please note that wine in Firmalife (on its own) has no special use beyond regular TFC alcohol. You should make it only if you want to have $(thing)fun$().'),
            item_spotlight('firmalife:plant/wild_red_grapes', text_contents='Red grapes spawn from 0-30 C, and 125-500 rainfall, or almost the entire habitable area.'),
            item_spotlight('firmalife:plant/wild_white_grapes', text_contents='White grapes spawn from 0-30 C, and 125-500 rainfall, or almost the entire habitable area.'),
            crafting('firmalife:crafting/grape_trellis_post', text_contents='Grapes must be grown on trellises constructed from these special posts and jute fiber.'),
            text('To construct a trellis, place two posts on top of each other. Move two blocks to the left or right and repeat the action. Then, $(item)$(k:key.use)$() the side of one of the top and bottom posts with $(thing)Jute Fiber$() to string lines between the posts. Grape trellises can be chained horizontally to create rows of grapes.'),
            multimultiblock('A grape trellis.',
            multiblock('', '', False, (('XYX',), ('X0X',),), {'X': 'firmalife:grape_trellis_post[axis=x,string_plus=true,string_minus=true]', '0': 'firmalife:grape_string_plant_red[axis=z,lifecycle=healthy,stage=0]', 'Y': 'firmalife:grape_string[axis=z]'}),
                multiblock('', '', False, (('ZYZ',),('X0X',),), {'X': 'firmalife:grape_trellis_post[axis=x,string_plus=true,string_minus=true]', '0': 'firmalife:grape_string_plant_red[axis=z,lifecycle=healthy,stage=2]', 'Y': 'firmalife:grape_string_red[axis=z,lifecycle=healthy]', 'Z': 'firmalife:grape_trellis_post_red[axis=x,lifecycle=healthy,string_plus=true,string_minus=true]'}),
            ),
            text('Provided the climate requirements are satisfied, the grape will grow up and over the trellis over the course of a few months. It will fruit in the month of July, flowering the month prior. Grapes can the be harvested. Grapes can also be grown in greenhouses on trellises.'),
            crafting('firmalife:crafting/wood/stomping_barrel/acacia', text_contents='The stomping barrel is used to smash grapes. A quern may also be used.'),
            text('To use a stomping barrel, $(item)$(k:key.use)$() with fresh grapes. Then, jump up and down on the barrel 16 times. $(item)$(k:key.use)$() with an empty hand to retrieve the items.$(br2)Then, seal the grapes in a barrel for 5 days to $(thing)ferment$() them.'),
            crafting('firmalife:crafting/wood/barrel_press/acacia', text_contents='The barrel press is the last step in grape processing.'),
            text('The leftmost slot can contain up to 16 grapes. Four grape items are needed for a bottle of wine. The four central slots are for mixing the grapes with other ingredients, but this is optional. Using only red or white grapes yields red or white wine. Adding at least one red grape to white wine makes Rose. Adding sugar to white wine makes dessert wine. Adding tirage mixture (yeast and sweetener in a barrel) makes sparkling wine.'),
            glassworking_recipe('firmalife:glassworking/empty_olivine_wine_bottle', 'Wine must be bottled in a proper wine bottle, made of non-silica glass.'),
            crafting('firmalife:crafting/bottle_label', text_contents='$(thing)Bottle labels$() can be renamed in a scribing station, and will add their name to the wine\'s tooltip.'),
            text('Wine must be provided with a $(thing)Cork$(), made by soaking $(thing)Treated Lumber$() in $(thing)Limewater$() for a day. When all is complete, use the bottle slot to fill the wine, or by pressing $(item)$(k:key.use)$() with a bottle in hand.'),
        )),
        entry('wine_consumption', 'Wine Consumption', 'firmalife:textures/item/olivine_wine_bottle.png', pages=(
            text('The discerning sommelier will be able to detect subtleties in the wine that is produced under different conditions. Indeed, this is possible in the world of Firmalife as well. It starts in the fields in which those grapes were grown -- a row of grapes is deeply affected by the ambient environment and soil.'),
            text('Grapes can have three terrain related traits -- \'Gravel Grown\', \'Slope Grown\', and \'Dirt Grown\', based on the environment nearby. Wine also records the Koppen Climate Classification of the area in which it is bottled. Grapes grown near bees have the \'Bee Pollinated\' trait.'),
            text('Wine begins aging as soon as it is bottled, and stops aging when the cork is removed. The cork can be removed by $(item)$(k:key.use)$() on the bottle item with a knife item. Otherwise, wine bottles work a little like buckets, and can be emptied into barrels or other devices.'),
            crafting('firmalife:crafting/wood/keg/pine', text_contents='The $(thing)Keg$() is a 2x2x2 barrel block that can contain loads of items or fluids. Perfect for your vinery!'),
            crafting('firmalife:crafting/wood/wine_shelf/hickory', text_contents='The $(thing)Wine Shelf$() is the perfect accessory for your vinery, allowing you to display and store your wine bottles in style.'),
            empty_last_page(),
        )),
        entry('pie', 'Pie', 'firmalife:textures/item/food/cooked_pie.png', pages=(
            text('Firmalife allows the creation of a variety of delectable baked goods. This chapter will give you all you need to start eating delicious and creative meals for breakfast, lunch, and dinner.'),
            text('$(thing)Butter is the base of many such foods. Using a $(l:firmalife/mixing_bowl)Mixing Bowl$(), combine 1000 mB of $(thing)Cream$() with one item of $(thing)Salt$(). Cream is made by sealing 1000 mB of milk in a barrel with a $(l:mechanics/firmalife#cheesecloth)Cheesecloth$().'),
            text('One useful meal is the $(thing)Pie$(). In your mixing bowl, combine butter, flower, and a sweetener (sugar, honey) to make $(thing)Pie Dough$(). To directly make $(thing)Pumpkin Pie$(), mix in an egg, two pumpkin chunks, and sweetener. Craft it with a $(thing)Pie Pan$() and bake it in an $(l:firmalife/ovens)Oven$() to complete the recipe.'),
            crafting('firmalife:crafting/spice/allspice', text_contents='Spiced flour is used in the creation of pumpkin pie. $(l:firmalife/herbs_and_spices)Pimento$() is an herb found in warm, moderately wet climates.'),
            crafting('firmalife:crafting/food/raw_pumpkin_pie', text_contents='$(thing)Pie Pans$() can be smithed in an Anvil from $(thing)Cast Iron$().'),
            crafting('firmalife:crafting/food/filled_pie', text_contents='Pie Dough, Preserves, and a Pie Pan will net you a $(thing)Filled Pie$(), which can then be finished in the oven for a delicious pie.'),
        )),
        entry('pizza', 'Pizza', 'firmalife:textures/item/food/cooked_pizza.png', pages=(
            text('To make $(thing)Pizza$(), mix Dough, Salt, Basil Leaves, and 100 mB of oil in a $(l:firmalife/mixing_bowl)Mixing Bowl$(). Oil can be olive oil, or soybean oil (the product of soybean paste sealed in a barrel of water).'),
            crafting('firmalife:crafting/food/shredded_cheese', text_contents='You\'ll need some shredded cheese to start with.').anchor('shredded_cheese'),
            text('To make $(thing)Tomato Sauce$(), boil a tomato, salt, garlic, and water all together in a pot. Alternatively, using a $(l:firmalife/oven_appliances#vat)Vat$(), boil the crafted version of those ingredients, also known as $(thing)Tomato Sauce Mix$().').anchor('tomato_sauce'),
            crafting('firmalife:crafting/pizza_with_ingredients_2', text_contents='Pizza is made from 1-3 of vegetables, cooked meats, or fruits, one shredded cheese, and one tomato sauce.'),
        )),
        entry('pasta', 'Pasta', 'firmalife:textures/item/food/cooked_pasta.png', pages=(
            text('There are two types of $(thing)Noodles$(): Egg and Rice. Rice flour, maize flour, salt, and 1000 mB of milk together in a $(l:firmalife/mixing_bowl)Mixing Bowl$() makes $(thing)Rice Noodles$(). Flour, an egg, salt, and 1000 mB of milk makes $(thing)Egg Noodles$().'),
            crafting('firmalife:crafting/food/raw_lasagna', text_contents='$(thing)Lasagna$() can be made directly from egg noodles with $(l:firmalife/pizza#tomato_sauce)Tomato Sauce$(), cooked meat, and oregano, which can then be baked in an $(l:firmalife/ovens)Oven$().'),
            text('Pasta is then completed by boiling it in water. For either kind of noodles, it must be retrieved from the pot by clicking with a $(thing)Bowl$().'),
            crafting('firmalife:crafting/food/pasta_with_tomato_sauce', text_contents='Crafting cooked egg noodles (pasta) with tomato sauce makes delicious pasta with tomato sauce!'),
        )),
        entry('burritos_and_tacos', 'Burritos and Tacos', 'firmalife:textures/item/food/burrito.png', pages=(
            text('The journey of making a $(thing)Tortilla$() is a long process, but rewarding. Start with $(thing)Maize Grain$(). Boil it in a pot of $(thing)Limewater$(), and seal it in a barrel of water to make $(thing)Nixtamal$(). Then crush it in a $(thing)Quern$() to make $(thing)Masa Flour$().'),
            text('Crafting the Masa Flour with a bucket of water makes $(thing)Masa$(), the dough of a tortilla. This can be heated to make a $(thing)Corn Tortilla$(). Baking a Tortilla in an oven makes a $(thing)Taco Shell$().'),
            crafting('firmalife:crafting/food/tortilla_chips', 'firmalife:crafting/food/nachos'),
            crafting('firmalife:crafting/food/salsa', text_contents='A tomato, cilantro, salt, and a knife will yield you $(thing)Salsa$().'),
            crafting('firmalife:crafting/food/burrito', text_contents='The $(thing)Burrito$() is made from cooked meat, $(l:firmalife/pizza#shredded_cheese)Shredded Cheese$(), $(l:firmalife/pizza#tomato_sauce)Tomato Sauce$(), a vegetable, and Salsa.'),
            crafting('firmalife:crafting/food/taco', text_contents='The $(thing)Taco$() is made from the same ingredients, except with a $(thing)Taco Shell$().'),
        ))
    ))

    book.build()

# Firmalife Pages

def knapping(recipe: str, text_content: TranslatableStr) -> Page: return recipe_page('knapping_recipe', recipe, text_content)

def drying_recipe(recipe: str, text_content: str) -> Page:
    return page('drying_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))

def smoking_recipe(recipe: str, text_content: str) -> Page:
    return page('smoking_recipe', {'recipe': recipe, 'text': text_content}, custom=True, translation_keys=('text',))

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
    main_with_args()

