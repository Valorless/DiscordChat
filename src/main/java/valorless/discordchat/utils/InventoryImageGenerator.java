package valorless.discordchat.utils;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

import valorless.discordchat.Main;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.nbt.NBT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Utility class for generating graphical representations of Minecraft inventories.
 *
 * <p>The class provides configurable options for slot size, rendering styles, and
 * a caching mechanism for item icons. It includes inner classes and static members
 * to handle colors, fonts, and other visual settings for inventory rendering.</p>
 */
public class InventoryImageGenerator {

	/** 
	 * The size of each inventory slot in pixels.
	 */
	public static int slotSize = 64;

	/** 
	 * The width of slot borders in pixels.
	 */
	public static int width = 2;

	/**
	 * The font used for rendering item counts and text on the inventory image.<br>
	 * Defaults to MinecraftFont.ttf, and falls back to Arial.
	 */
	static Font font;

	/**
	 * A cache for storing pre-loaded item icons by their {@link Material}.
	 * <p>This helps to avoid reloading item icons repeatedly, improving performance.</p>
	 */
	static Map<Material, BufferedImage> cache = new HashMap<Material, BufferedImage>();

	/**
	 * A scheduled task for managing cache cleanup operations.
	 * <p>Can be used to stop the caching process if needed.</p>
	 */
	public static BukkitTask cche;

	/** 
	 * A placeholder image used when an item icon cannot be found.
	 */
	static BufferedImage missing;

	/**
	 * Inner class for defining the color scheme used in inventory rendering.
	 */
	public static class Colors {

	    /** 
	     * The background color of the inventory.
	     */
	    public static Color background = new Color(139, 139, 139);

	    /** 
	     * The primary color of slot borders.
	     */
	    public static Color slot = new Color(50, 50, 50);

	    /** 
	     * The highlight color for emphasized slot borders.
	     */
	    public static Color slotHighlight = Color.WHITE;

	    /** 
	     * The color of text rendered on the inventory image.
	     */
	    public static Color text = Color.WHITE;

	    /** 
	     * The color of text shadows for better readability.
	     */
	    public static Color textShadow = new Color(50, 50, 50);
	}
		
	/**
	 * Generates an inventory image based on the provided item stack array.
	 *
	 * <p>This method renders a graphical representation of a Minecraft inventory
	 * using item icons and slot grids. It allows saving the generated image locally or sending it via a remote service.</p>
	 *
	 * @param items     an array of {@link ItemStack} representing the inventory contents
	 * @param rows      the number of inventory rows to render
	 * @param inventory whether to shift hotbar, and render equipment slots from the inventory (Requires 5 rows)
	 * @return a {@link String} representing the UUID of the generated image, which serves as its unique identifier,<br>
	 * or null if there is an error reading fonts or writing the image
	 */
    public static String generate(ItemStack[] items, int rows, boolean inventory) {
    	Log.Info(Main.plugin, "Generating Inventory");
    	try {
            font = Font.createFont(Font.TRUETYPE_FONT, new File(Main.plugin.getDataFolder(), "MinecraftFont.ttf")).deriveFont(20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            font = new Font("Arial", Font.PLAIN, 12); // Default font if Minecraft font fails to load
        }
    	
    	if(inventory) {
    		items = Inventory(items);
    	}
    	
        int cols = 9;      // Inventory columns
        BufferedImage image = new BufferedImage(cols * slotSize + width, rows * slotSize + width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
	    

        // Draw slots (grid)
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                int slotX = x * slotSize;
                int slotY = y * slotSize;

                // Draw slot background
                g.setColor(Colors.background);
                g.fillRect(slotX, slotY, slotSize + width, slotSize + width);
                
                // Draw slots
                g.setColor(Colors.slot);
                DrawRectWidth(g, slotX, slotY);
                //g.drawRect(slotX, slotY, slotSize, slotSize);
                g.setColor(Colors.slotHighlight);
                DrawRectWidth(g, slotX + width, slotY + width);
                //g.drawRect(slotX+1, slotY+1, slotSize, slotSize);

                // Draw item if present
                int index = y * cols + x;
                if (index < items.length && items[index] != null) {
                    ItemStack item = items[index];

                    // Load item icon (placeholder example)
                    BufferedImage itemIcon = loadItemIcon(item);
                    g.drawImage(itemIcon, slotX+10, slotY+10, slotSize-15, slotSize-15, null);

                    // Draw item count
                    if(item.getAmount() > 1) {
                    	String count = String.valueOf(item.getAmount());
                    	g.setFont(font);
                    	DrawAmountText(g, count, slotX, slotY);
                    	//g.setColor(new Color(50, 50, 50));
                    	//g.drawString(count, slotX + slotSize - 56, slotY + slotSize - 2);
                    	//g.setColor(Color.WHITE);
                    	//g.drawString(count, slotX + slotSize - 58, slotY + slotSize - 4);
                    }
                }
            }
        }

        g.setColor(Colors.slot);
        DrawRectWidth(g, 0, 0, cols*slotSize, rows*slotSize);

        g.dispose();

        UUID id = UUID.randomUUID();
        
        try {
        	if(Main.config.GetBool("save-locally")) {
        		File path = Bukkit.getPluginManager().getPlugin("WebServer").getDataFolder();
        		ImageIO.write(image, "PNG", new File(path, Main.config.GetString("save-location") + id.toString() + ".png"));
        		FileCleanup.Clean(path.getPath() +  Main.config.GetString("save-location"), Main.config.GetInt("cleanup-age"));
        	}else {
        		ImageSender.sendImage(image, id.toString());
        	}
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
        return id.toString();
        
        // Save the image to a file
        //ImageIO.write(image, "png", outputFile);
    }
    
    private static void DrawAmountText(Graphics2D g, String text, int slotX, int slotY) {
    	// Shadow
    	g.setColor(Colors.textShadow);
    	g.drawString(text, slotX + slotSize - 56, slotY + slotSize - 2);
    	
    	// Text
    	g.setColor(Colors.text);
    	g.drawString(text, slotX + slotSize - 58, slotY + slotSize - 4);
    }
    
    /**
     * @param g Graphics2D
     * @param slotX int - X position
     * @param slotY int - Y position
     * @param scale int, int - X & Y scale
     */
    private static void DrawRectWidth(Graphics2D g, int slotX, int slotY, int...scale) {
    	if(scale != null && scale.length != 0) {
    		for(int i = 0; i < width; i++) {
    			g.drawRect(slotX + i, slotY + i, scale[0], scale[1]);
    		} 
    	}else {
    		// Each Rect is 1px wide, so we loop through width
    		for(int i = 0; i < width; i++) {
    			g.drawRect(slotX + i, slotY + i, slotSize, slotSize);
    		} 
    	}
    }
    
    public static ItemStack[] Inventory(ItemStack[] items) {
        if (items == null || items.length <= 5) {
            return items; // Handle empty or too-short arrays
        }
        
        // Create a new array with the desired length
        ItemStack[] newArray = new ItemStack[items.length - 5];
        
        // Copy equipment
        ItemStack[] eq = new ItemStack[5];
        eq[0] = items[items.length-5];
        eq[1] = items[items.length-4];
        eq[2] = items[items.length-3];
        eq[3] = items[items.length-2];
        eq[4] = items[items.length-1];

        // Copy the elements from the original array to the new array, excluding the last 5
        System.arraycopy(items, 0, newArray, 0, newArray.length);

		// Put hotbar behind
        shiftFirstNineToEnd(newArray);
        
        // Add equipment to the newly sorted Array.
        List<ItemStack> itemStackList = new ArrayList<>(Arrays.asList(newArray));
        itemStackList.addAll(Arrays.asList(eq));

        return itemStackList.toArray(new ItemStack[0]);
    }
    
    
   /**
    * Shift the top row of the inventory to the bottom.<br>
    * The first row in a player inventory is typically the hotbar,
    * but that typically sits at the bottom in the inventory view.
    * 
    * @param items Array to be arranged.
    * @return Rearranged array.
    */
    public static void shiftFirstNineToEnd(ItemStack[] items) {
        if (items == null || items.length < 9) {
            return; // Handle empty or too-short arrays
        }

        // Create a temporary array to store the first 9 items
        ItemStack[] firstNine = new ItemStack[9];
        System.arraycopy(items, 0, firstNine, 0, 9);

        // Shift the remaining items to the beginning
        System.arraycopy(items, 9, items, 0, items.length - 9);

        // Copy the first 9 items to the end
        System.arraycopy(firstNine, 0, items, items.length - 9, 9);
    }

    /**
     * Loads the icon for a given {@link ItemStack}.
     *
     * <p>This method attempts to retrieve a custom item icon if specific metadata or tags
     * are present. If no custom icon is found, it falls back to a cached default icon for
     * the item's {@link Material}. If no cache entry exists, a placeholder "missing" icon
     * is returned.</p>
     *
     * @param item The {@link ItemStack} whose icon is to be loaded.
     * @return A {@link BufferedImage} representing the item's icon.
     *
     * <ul>
     *     <li>If the item has custom model data, it attempts to load a corresponding custom icon file.</li>
     *     <li>If the item has a "bag-uuid" NBT tag, it attempts to load the "havenbag.png" icon.</li>
     *     <li>If no custom criteria are met, the method checks for a cached icon in {@code cache}.</li>
     *     <li>If no cache entry exists, a generic "missing" icon is returned.</li>
     * </ul>
     */
    public static BufferedImage loadItemIcon(ItemStack item) {
        // Check if the item has metadata (e.g., custom model data or custom NBT tags)
        if (item.hasItemMeta()) {
            // Check if the item has custom model data in its metadata
            if (item.getItemMeta().hasCustomModelData()) {
                Log.Debug(Main.plugin, "custom item"); // Log for debugging
                // Generate the filename for the custom icon based on the item's material and custom model data
                String mat = item.getType().toString().toLowerCase();
                BufferedImage custom = loadItemIconServer(String.format("%s-%s.png", mat, item.getItemMeta().getCustomModelData()));
                
                // If a custom icon exists, return it
                if (custom != null) return custom;
            }
        }

        // Check for the special "bag-uuid" NBT tag, which identifies "HavenBag" items
        if (NBT.Has(item, "bag-uuid")) {
            Log.Debug(Main.plugin, "HavenBag"); // Log for debugging
            // Attempt to load the specific "havenbag.png" icon
            BufferedImage custom = loadItemIconServer("havenbag.png");
            
            // If the HavenBag icon exists, return it
            if (custom != null) return custom;
        }
        
        // Check if the item's type (Material) has a cached icon
        if (cache.containsKey(item.getType())) {
            // Return the cached icon
            return cache.get(item.getType());
        } else {
            // If no cached icon is found, return the "missing" placeholder icon
            return missing;
        }
    }
    
    /**
     * Loads a custom item icon from the server's "custom" folder.
     *
     * <p>This method checks if a PNG file with the given filename exists in the "custom"
     * subfolder of the plugin's data directory. If the file exists, it attempts to read
     * it as a {@link BufferedImage}. If the file doesn't exist or an error occurs during
     * reading, the method returns {@code null}.</p>
     *
     * @param fileName The name of the PNG file to load (e.g., "example-icon.png").
     * @return A {@link BufferedImage} of the icon, or {@code null} if the file is not found
     *         or cannot be loaded.
     */
    private static BufferedImage loadItemIconServer(String fileName) {
        // Log the filename being requested for debugging purposes
        Log.Info(Main.plugin, fileName);

        // Get the path to the "custom" folder in the plugin's data directory
        File rendersFolder = new File(Main.plugin.getDataFolder(), "custom");

        // Create a File object pointing to the specific image file
        File imageFile = new File(rendersFolder, fileName);

        // Check if the file exists in the "custom" folder
        if (!imageFile.exists()) {
            // If the file doesn't exist, return null to indicate a missing resource
            return null;
        }

        // Attempt to load the image as a BufferedImage
        try {
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            // Log a severe error if the image cannot be read
            Main.plugin.getLogger().severe("Failed to load image: " + imageFile.getPath());
            e.printStackTrace();
            return null;
        }
    }
       
    /**
     * Downloads an image from the specified URL and scales it to the size of an inventory slot.
     *
     * <p>This method fetches an image from the provided URL, reads it into a {@link BufferedImage},
     * and attempts to scale it to the dimensions defined by {@code slotSize}. If the download
     * or processing fails, it returns a default placeholder image (represented by a cached
     * {@link Material#BARRIER} icon).</p>
     *
     * @param imageUrl The URL from which to download the image.
     * @return A {@link BufferedImage} scaled to the inventory slot size.<br>
     *         if the download or processing fails, the placeholder "missing" icon
     * 		   is returned.
     */
    private static BufferedImage downloadImage(String imageUrl) {
        try {
            // Parse the URL to ensure it is valid
            URL url = new URL(imageUrl);

            // Open a stream to read the image data from the URL
            InputStream in = url.openStream();

            // Read the image data into a BufferedImage
            BufferedImage image = ImageIO.read(in);

            // Scale the image to fit the slot size
            image.getScaledInstance(slotSize, slotSize, Image.SCALE_FAST);

            // Return the scaled image
            return image;
        } catch (IOException e) {
            // If an error occurs, return a placeholder "barrier" image from the cache
            return cache.get(Material.BARRIER);
        }
    }

	public static String capitalizeFirstLetters(String input) {
		// Convert the entire string to lowercase
		String result = input.toLowerCase();
		
		// If the input is empty, return the same string
		if (result.isEmpty()) {
			return result;
		}
	
		// Capitalize the first letter
		result = Character.toUpperCase(result.charAt(0)) + result.substring(1);
	
		// Iterate over the string to find underscores and capitalize the letter after them
		for (int i = 1; i < result.length(); i++) {
			if (result.charAt(i) == '_') {
				// Capitalize the character after the underscore
				if (i + 1 < result.length()) {
					result = result.substring(0, i + 1) + Character.toUpperCase(result.charAt(i + 1)) + result.substring(i + 2);
				}
			}
		}
	
		return result;
	}
	
	/**
	 * Loads and caches textures for all materials that are items.
	 *
	 * <p>This method initializes the texture cache for materials by iterating through all 
	 * {@link Material} values and loading their corresponding textures using the {@link ImageLoader}.
	 * If a material is not an item (e.g., blocks or other non-item types), it is skipped. A default 
	 * texture for missing items (based on {@link Material#BARRIER}) is also loaded. After caching, 
	 * any missing resources are written to a specified file.</p>
	 *
	 * <p>The textures are stored in a static {@code Map<Material, BufferedImage>} called {@code cache}.</p>
	 */
	public static void LoadCache() {
	    Log.Info(Main.plugin, "Attempting to cache textures.");

	    // Load the default "missing" texture for items that fail to load
	    missing = ImageLoader.loadImage(Material.BARRIER.toString().toLowerCase());

	    // Iterate through all materials
	    for (Material mat : Material.values()) {
	        // Skip materials that are not items
	        if (!mat.isItem()) continue;

	        // Load the texture for the current material and cache it
	        cache.put(mat, ImageLoader.loadImage(mat.toString().toLowerCase()));
	    }

	    // Log completion of the caching process
	    Log.Info(Main.plugin, "Textures cached.");

	    // Write missing resources (those that couldn't be loaded) to a file
	    ImageLoader.writeMissingResourcesToFile("missing_resources.txt");
	}
	
	
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