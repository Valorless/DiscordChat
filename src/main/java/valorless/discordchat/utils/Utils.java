package valorless.discordchat.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Utils {
    /**
     * @return New 27 slot ItemStack[] populated with random items and amounts.
     */
    public static ItemStack[] RandomInventory() {
        Random RANDOM = new Random();
        ItemStack[] inventory = new ItemStack[27]; // Represents a 27-slot inventory

        // Create a list of materials that are not air and can be used in inventory
        List<Material> validMaterials = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.isItem()) { // Ensure it's an item
                validMaterials.add(material);
            }
        }

        // Create a list to hold the random items and air
        List<ItemStack> randomItems = new ArrayList<>();

        // Fill with random items
        for (int i = 0; i < 20; i++) { // 20 slots with items
            Material randomMaterial = validMaterials.get(RANDOM.nextInt(validMaterials.size()));
            int maxStackSize = randomMaterial.getMaxStackSize(); // Get the max stack size for the item
            int randomAmount = maxStackSize > 1 ? RANDOM.nextInt(maxStackSize) + 1 : 1; // Random amount (1-maxStackSize)
            randomItems.add(new ItemStack(randomMaterial, randomAmount));
        }

        // Fill the remaining slots with air
        for (int i = 0; i < 7; i++) {
            randomItems.add(null); // Null represents an empty slot
        }

        // Shuffle the list to scatter items and air randomly
        Collections.shuffle(randomItems);

        // Copy the items to the inventory array
        for (int i = 0; i < randomItems.size(); i++) {
            inventory[i] = randomItems.get(i);
        }

        return inventory;
    }
}
