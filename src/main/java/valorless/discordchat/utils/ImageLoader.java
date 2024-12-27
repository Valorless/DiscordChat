package valorless.discordchat.utils;

import javax.imageio.ImageIO;

import valorless.discordchat.Main;
import valorless.valorlessutils.ValorlessUtils.Log;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageLoader {

	private static final List<String> missingResources = new ArrayList<>();
	
    public static BufferedImage loadImage(String resource) {
    	//String path = String.format("%s/renders/%s.png", Main.plugin.getDataFolder().toString() + resourcePath);
    	
    	String path = "renders/" + resource + ".png";
    	//Log.Info(Main.plugin, path);
        try (InputStream input = Main.plugin.getResource(path)) {
            if (input == null) {
            	missingResources.add(resource);
                //throw new IllegalArgumentException("Resource not found: " + resourcePath);
                Log.Error(Main.plugin, "Resource not found: " + resource);
                return null;
            }
            BufferedImage image = ImageIO.read(input);
            image.getScaledInstance(InventoryImageGenerator.slotSize, InventoryImageGenerator.slotSize, Image.SCALE_FAST);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void writeMissingResourcesToFile(String fileName) {
    	if(missingResources.isEmpty()) return;
    	File dataFolder = Main.plugin.getDataFolder();
    	// Write the file in the data folder
        File file = new File(dataFolder, fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String resource : missingResources) {
                writer.write(resource);
                writer.newLine();
            }
            System.out.println("Missing resources written to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveImage(BufferedImage image, String fileName) {
        File dataFolder = Main.plugin.getDataFolder();
        File rendersFolder = new File(dataFolder, "renders");

        // Create the file in the data folder
        File outputFile = new File(rendersFolder, fileName + ".png");
        
        if (image == null) {
        	missingResources.add(fileName);
            //throw new IllegalArgumentException("Resource not found: " + resourcePath);
            Log.Error(Main.plugin, "Resource not found: " + fileName);
            return;
        }

        try {
            image.getScaledInstance(InventoryImageGenerator.slotSize, InventoryImageGenerator.slotSize, Image.SCALE_FAST);
            // Write the BufferedImage as a PNG file
            ImageIO.write(image, "png", outputFile);
            Main.plugin.getLogger().info("Image saved successfully to: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            
        	Main.plugin.getLogger().severe("Failed to save image: " + e.getMessage());
            e.printStackTrace();
        }
    }
}