from mcresources import ResourceManager, utils
import assets
import recipes
import data


def main():
    rm = ResourceManager('firmalife', resource_dir='../src/main/resources')

    # utils.clean_generated_resources('/'.join(rm.resource_dir))
    generate_all(rm)
    print('New = %d, Modified = %d, Unchanged = %d, Errors = %d' % (rm.new_files, rm.modified_files, rm.unchanged_files, rm.error_files))


def generate_all(rm: ResourceManager):
    assets.generate(rm)
    recipes.generate(rm)
    data.generate(rm)

    rm.flush()


if __name__ == '__main__':
    main()
