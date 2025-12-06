import os.path

from PIL import Image, ImageDraw, ImageEnhance
from PIL.Image import Transpose

from constants import *

path = '../src/main/resources/assets/firmalife/textures/'
templates_path = 'texture_templates/'

def get_jar_color(name: str):
    img = Image.open(path + 'item/jar/' + name + '.png').convert('RGBA')
    return img.getpixel((7, 7))

def easy_colorize(color, from_path, to_path, saturation: float = 1):
    img = Image.open(from_path + '.png')
    new_image = put_on_all_pixels(img, color)
    if saturation != 1:
        new_image = ImageEnhance.Color(new_image).enhance(saturation)
    new_image.save(to_path + '.png')

def put_on_all_pixels(img: Image, color) -> Image:
    if isinstance(color, int):
        color = (color, color, color, 255)
    img = img.convert('RGBA')
    for x in range(0, img.width):
        for y in range(0, img.height):
            dat = img.getpixel((x, y))
            grey = (dat[0] + dat[1] + dat[2]) / 3 / 255
            if dat[3] > 0:
                tup = (int(color[0] * grey), int(color[1] * grey), int(color[2] * grey))
                img.putpixel((x, y), tup)
    return img

def big_barrel(wood: str):
    log = Image.open(templates_path + f'wood/log/{wood}.png').convert('RGBA')
    log_90 = Image.open(templates_path + f'wood/log/{wood}.png').convert('RGBA').transpose(Transpose.TRANSVERSE)
    sheet = Image.open(templates_path + f'wood/sheet/{wood}.png').convert('RGBA')
    plank = Image.open(templates_path + f'wood/planks/{wood}.png').convert('RGBA')
    plank_90 = Image.open(templates_path + f'wood/planks/{wood}.png').convert('RGBA').transpose(Transpose.TRANSVERSE)
    log_mask = Image.open(templates_path + f'bigbarrel/log_template.png').convert('L')
    sheet_mask = Image.open(templates_path + f'bigbarrel/sheet_template.png').convert('L')
    plank_mask = Image.open(templates_path + f'bigbarrel/planks_template.png').convert('L')
    side_plank_mask = Image.open(templates_path + f'bigbarrel/side_planks_template.png').convert('L')
    side_log_mask = Image.open(templates_path + f'bigbarrel/side_logs_template.png').convert('L')
    big_log = fill_image(log, 32, 32, 16, 16)
    big_log_90 = fill_image(log_90, 32, 32, 16, 16)
    big_sheet = fill_image(sheet, 32, 32, 16, 16)
    big_plank = fill_image(plank, 32, 32, 16, 16)
    big_plank_90 = fill_image(plank_90, 32, 32, 16, 16)

    base_img = Image.new('RGBA', (32, 32), color=(0, 0, 0, 0))
    base_img.paste(big_plank_90, mask=plank_mask)
    base_img.paste(big_sheet, mask=sheet_mask)
    base_img.paste(big_log, mask=log_mask)

    side_img = Image.new('RGBA', (32, 32), color=(0, 0, 0, 0))
    side_img.paste(big_plank, mask=side_plank_mask)
    side_img.paste(big_log, mask=side_log_mask)
    side_img2 = Image.new('RGBA', (32, 32), color=(0, 0, 0, 0))
    side_img2.paste(big_plank_90, mask=side_plank_mask)
    side_img2.paste(big_log_90, mask=side_log_mask)

    i = 0
    for x, y in ((0, 0), (16, 0), (0, 16), (16, 16)):
        img = base_img.copy().crop((x, y, x + 16, y + 16))
        img.save(path + 'block/wood/big_barrel/%s_%s.png' % (wood, i))
        img2 = side_img.copy().crop((x, y, x + 16, y + 16))
        img2.save(path + 'block/wood/big_barrel/%s_%s_side.png' % (wood, i))
        img2.save(path + 'block/wood/big_barrel/%s_%s_top.png' % (wood, i))
        i += 1


def fill_image(tile_instance, width: int, height: int, tile_width: int, tile_height: int):
    image_instance = Image.new('RGBA', (width, height))
    for i in range(0, int(width / tile_width)):
        for j in range(0, int(height / tile_height)):
            image_instance.paste(tile_instance, (i * tile_width, j * tile_height))
    return image_instance

def main():
    for wood in TFC_WOODS.keys():
        big_barrel(wood)
    for fruit in FL_FRUITS:
        jar_color = get_jar_color(fruit)
        # easy_colorize(jar_color, 'texture_templates/jar_content', path + 'block/jar/%s' % fruit, 2)
        easy_colorize(jar_color, 'texture_templates/jam', path + 'item/food/%s_jam' % fruit, 2)


if __name__ == '__main__':
    main()