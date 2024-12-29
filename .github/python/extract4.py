import os
import json
import shutil
from pathlib import Path
from PIL import Image

# Define paths
RESOURCE_PACK_PATH = os.path.dirname(os.path.abspath(__file__))
TEXTURES_PATH = os.path.join(RESOURCE_PACK_PATH, "pack/assets")  # Base path for textures (now more flexible)
MODELS_PATH = os.path.join(RESOURCE_PACK_PATH, "pack/assets/minecraft/models/item")  # Corrected models path
OUTPUT_PATH = os.path.join(RESOURCE_PACK_PATH, "extracted_textures")  # Corrected output folder name
FOUND_LOG_PATH = os.path.join(RESOURCE_PACK_PATH, "found_files.log")  # Log file for found files
MISSING_LOG_PATH = os.path.join(RESOURCE_PACK_PATH, "missing_files.log")  # Log file for missing files

# Missing and found file trackers
found_files = []
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
                if "pulling_" in model_name:  # Skip any textures containing "_pulling"
                    continue
                if "_arrow" in model_name:
                    continue
                if "_firework" in model_name:
                    continue
                texture_path = resolve_texture_path(model_name)
                if texture_path:
                    copy_and_rename_texture(texture_path, f"{parent_material}-{custom_model_data}.png")
                else:
                    log_missing(f"Texture for {parent_material}-{custom_model_data} not found.")

    # Write logs for found and missing files
    if found_files:
        with open(FOUND_LOG_PATH, "w") as log_file:
            log_file.write("\n".join(found_files))
        print(f"Found files log written to {FOUND_LOG_PATH}")

    if missing_files:
        with open(MISSING_LOG_PATH, "w") as log_file:
            log_file.write("\n".join(missing_files))
        print(f"Missing files log written to {MISSING_LOG_PATH}")

def resolve_texture_path(model_name):
    """Resolve the texture path from a given model name."""
    if not model_name:
        return None

    # Extract namespace and remove prefix (e.g., "minecraft:", "stellarity:", etc.)
    namespace, model_name_without_namespace = extract_namespace(model_name)

    # Ensure the model name structure is kept intact and convert slashes correctly
    texture_path = os.path.join(TEXTURES_PATH, namespace, "textures", model_name_without_namespace.replace(":", os.sep).replace("/", os.sep) + ".png")

    # Log the path being checked
    print(f"Checking texture path: {texture_path}")
    
    if os.path.exists(texture_path):
        log_found(f"Found texture for {model_name}: {texture_path}")
        return texture_path
    else:
        log_missing(f"Texture for {model_name} not found at {texture_path}")
        return None

def extract_namespace(model_name):
    """Extract namespace from model name (e.g., 'minecraft:item/stone' -> 'minecraft', 'stellarity:item/rageroot' -> 'stellarity')."""
    if ":" in model_name:
        namespace, name = model_name.split(":", 1)
        return namespace, name
    return "minecraft", model_name  # Default to 'minecraft' if no namespace is provided

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
                log_found(f"Cropped and renamed {texture_path} to {new_path}")
            else:
                shutil.copy(texture_path, new_path)
                log_found(f"Copied {texture_path} to {new_path}")
    except Exception as e:
        log_missing(f"Error processing {texture_path}: {e}")

def log_missing(message):
    """Log missing files or errors."""
    print(message)
    missing_files.append(message)

def log_found(message):
    """Log found files."""
    print(message)
    found_files.append(message)

if __name__ == "__main__":
    process_resource_pack()
