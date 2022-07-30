from PIL import Image, ImageDraw, ImageEnhance

from constants import *

path = '../src/main/resources/assets/firmalife/textures/'

def get_jar_color(name: str):
    img = Image.open(path + 'item/jar/' + name + '.png')
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

def main():
    for fruit in TFC_FRUITS:
        jar_color = get_jar_color(fruit)
        easy_colorize(jar_color, 'texture_templates/jar_content', path + 'block/jar/%s' % fruit, 2)
    for fruit in FL_FRUITS:
        jar_color = get_jar_color(fruit)
        easy_colorize(jar_color, 'texture_templates/jar_content', path + 'block/jar/%s' % fruit, 2)


if __name__ == '__main__':
    main()