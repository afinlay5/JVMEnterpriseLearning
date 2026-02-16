import os

ROOT_DIR = r"G:\My Drive\Wardrobe"

for current_dir, subdirs, files in os.walk(ROOT_DIR):
    # Skip if directory already has subfolders
    if subdirs:
        continue

    # Skip if there are no files
    if not files:
        continue

    media_path = os.path.join(current_dir, "Media")

    # Create Media folder if it doesn't exist
    if not os.path.exists(media_path):
        os.mkdir(media_path)
        print(f"Created Media folder in: {current_dir}")
