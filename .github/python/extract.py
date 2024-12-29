import os
import json
import shutil
from pathlib import Path

# Define paths
RESOURCE_PACK_PATH = os.path.dirname(os.path.abspath(__file__))
TEXTURES_PATH = os.path.join(RESOURCE_PACK_PATH, "pack/assets/minecraft/textures")
MODELS_PATH = os.path.join(RESOURCE_PACK_PATH, "pack/assets/minecraft/models/item")
OUTPUT_PATH = os.path.join(RESOURCE_PACK_PATH, "extracted_textures")  # Destination folder for renamed textures

def process_resource_pack():
    if not os.path.exists(MODELS_PATH):
        print(f"Models directory not found: {MODELS_PATH}")
        return

    # Ensure the output folder exists
    os.makedirs(OUTPUT_PATH, exist_ok=True)

    for model_file in Path(MODELS_PATH).rglob("*.json"):
        with open(model_file, "r") as file:
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
                texture_path = resolve_texture_path(override.get("model"))
                if texture_path:
                    copy_and_rename_texture(texture_path, f"{parent_material}-{custom_model_data}.png")

def resolve_texture_path(model_name):
    """Resolve the texture path from a given model name."""
    if not model_name:
        return None

    model_path = os.path.join(RESOURCE_PACK_PATH, "pack/assets/minecraft/models", model_name + ".json")
    if not os.path.exists(model_path):
        print(f"Model file not found: {model_path}")
        return None

    with open(model_path, "r") as file:
        try:
            model_data = json.load(file)
        except json.JSONDecodeError as e:
            print(f"Error reading {model_path}: {e}")
            return None

    textures = model_data.get("textures", {})
    texture_name = textures.get("layer0")  # Assumes layer0 is the main texture
    if texture_name:
        return os.path.join(TEXTURES_PATH, texture_name.replace("minecraft:", "").replace("/", os.sep) + ".png")
    return None

def copy_and_rename_texture(texture_path, new_name):
    """Copy and rename the texture file."""
    if not os.path.exists(texture_path):
        print(f"Texture file not found: {texture_path}")
        return

    new_path = os.path.join(OUTPUT_PATH, new_name)
    try:
        shutil.copy(texture_path, new_path)
        print(f"Copied and renamed {texture_path} to {new_path}")
    except OSError as e:
        print(f"Error copying {texture_path}: {e}")

if __name__ == "__main__":
    process_resource_pack()
