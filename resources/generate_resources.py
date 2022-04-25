import argparse

from mcresources import ResourceManager, utils
import assets
import recipes
import data


def main():
    parser = argparse.ArgumentParser(description='Generate resources for Firmalife')
    rm = ResourceManager('firmalife', resource_dir='../src/main/resources')
    parser.add_argument('--clean', action='store_true', dest='clean', help='Clean all auto generated resources')
    args = parser.parse_args()

    if args.clean:
        # Stupid windows file locking errors.
        for tries in range(1, 1 + 3):
            try:
                utils.clean_generated_resources('/'.join(rm.resource_dir))
                print('Clean Success')
                return
            except:
                print('Failed, retrying (%d / 3)' % tries)
        print('Clean Aborted')
        return

    generate_all(rm)
    print('New = %d, Modified = %d, Unchanged = %d, Errors = %d' % (rm.new_files, rm.modified_files, rm.unchanged_files, rm.error_files))


def generate_all(rm: ResourceManager):
    assets.generate(rm)
    recipes.generate(rm)
    data.generate(rm)

    rm.flush()


if __name__ == '__main__':
    main()
