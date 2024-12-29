import os
import json
import shutil
from pathlib import Path
from PIL import Image

# Define paths
RESOURCE_PACK_PATH = "C:/folder-containing-this-file"  # Default path to your resource pack
TEXTURES_PATH = os.path.join(RESOURCE_PACK_PATH, "pack/assets/minecraft/textures")
MODELS_PATH = os.path.join(RESOURCE_PACK_PATH, "pack/assets/minecraft/models/item")
OUTPUT_PATH = os.path.join(RESOURCE_PACK_PATH, "extracted_textures")  # Destination folder for renamed textures
LOG_PATH = os.path.join(RESOURCE_PACK_PATH, "missing_files.log")  # Log file for missing files

# Missing file tracker
missing_files = []

def process_resource_pack():
    if not os.path.exists(MODELS_PATH):
        print(f"Models directory not found: {MODELS_PATH}")
        return

    # Ensure the output folder exists
    os.makedirs(OUTPUT_PATH, exist_ok=True)

    for model_file in Path(MODELS_PATH).rglob("*.json"):
        with open(model_file, "r", encoding="utf-8") as file:  # Use UTF-8 encoding
            try:
                model_data = json.load(file)
            except json.JSONDecodeError as e:
                print(f"Error reading {model_file}: {e}")
                continue

        # Extract material and CustomModelData
        parent_material = Path(model_file).stem  # Assumes file name is the material name
        overrides = model_data.get("overrides", [])
        
        for override in overrides:
            predicate = override.get("predicate", {})
            custom_model_data = predicate.get("custom_model_data")
            if custom_model_data is not None:
                model_name = override.get("model", "").replace("minecraft:", "")  # Remove "minecraft:" prefix
                if "_pulling" in model_name:  # Skip any textures containing "_pulling"
                    continue
                if "_arrow" in model_name:  # Skip any textures containing "_pulling"
                    continue
                if "_firework" in model_name:  # Skip any textures containing "_pulling"
                    continue
                texture_path = resolve_texture_path(model_name)
                if texture_path:
                    copy_and_rename_texture(texture_path, f"{parent_material}-{custom_model_data}.png")
                else:
                    log_missing(f"Texture for {parent_material}-{custom_model_data} not found.")

    # Write missing files log
    if missing_files:
        with open(LOG_PATH, "w") as log_file:
            log_file.write("\n".join(missing_files))
        print(f"Missing files logged to {LOG_PATH}")

def resolve_texture_path(model_name):
    """Resolve the texture path from a given model name."""
    if not model_name:
        return None

    model_path = os.path.join(RESOURCE_PACK_PATH, "pack/assets/minecraft/models", model_name.replace("/", os.sep) + ".json")
    if not os.path.exists(model_path):
        log_missing(f"Model file not found: {model_path}")
        return None

    with open(model_path, "r", encoding="utf-8") as file:  # Use UTF-8 encoding
        try:
            model_data = json.load(file)
        except json.JSONDecodeError as e:
            log_missing(f"Error reading model file: {model_path} - {e}")
            return None

    textures = model_data.get("textures", {})
    texture_name = textures.get("layer0")  # Assumes layer0 is the main texture
    if texture_name:
        # Remove "minecraft:" prefix if present
        texture_name = texture_name.replace("minecraft:", "")
        # Resolve texture path
        texture_path = os.path.join(TEXTURES_PATH, texture_name.replace("/", os.sep) + ".png")
        if os.path.exists(texture_path):
            return texture_path
        else:
            # Try common extensions or alternative paths
            guessed_path = guess_texture_path(texture_name)
            if guessed_path:
                return guessed_path

    return None

def guess_texture_path(texture_name):
    """Attempt to guess the texture path for edge cases."""
    common_extensions = [".png", ".jpg", ".tga"]
    texture_base = texture_name.replace("/", os.sep)
    for ext in common_extensions:
        guessed_path = os.path.join(TEXTURES_PATH, texture_base + ext)
        if os.path.exists(guessed_path):
            return guessed_path
    log_missing(f"Guessed texture not found: {texture_name}")
    return None

def copy_and_rename_texture(texture_path, new_name):
    """Copy and rename the texture file. Crop if it is an animated texture."""
    if not os.path.exists(texture_path):
        log_missing(f"Texture file not found: {texture_path}")
        return

    new_path = os.path.join(OUTPUT_PATH, new_name)

    try:
        # Open the image to check dimensions
        with Image.open(texture_path) as img:
            width, height = img.size
            if height > width:  # Likely an animated texture
                frame_height = width  # Animation frame height is the same as the width
                cropped = img.crop((0, 0, width, frame_height))  # Crop the topmost frame
                cropped.save(new_path)
                print(f"Cropped and renamed {texture_path} to {new_path}")
            else:
                shutil.copy(texture_path, new_path)
                print(f"Copied {texture_path} to {new_path}")
    except Exception as e:
        log_missing(f"Error processing {texture_path}: {e}")

def log_missing(message):
    """Log missing files or errors."""
    print(message)
    missing_files.append(message)

if __name__ == "__main__":
    process_resource_pack()
