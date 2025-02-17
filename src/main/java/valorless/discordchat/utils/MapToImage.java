package valorless.discordchat.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import valorless.discordchat.Main;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MapToImage {
    
    public static String getMapAsImage(ItemStack mapItem) {
        // Ensure it's a filled map
        if (mapItem.getType() != Material.FILLED_MAP) {
        	return null;
        }

        MapMeta meta = (MapMeta) mapItem.getItemMeta();
        if (meta == null || !meta.hasMapId()) {
        	return null;
        }

        // Get MapView from ID
        int mapId = meta.getMapId();

        File file = new File(Bukkit.getPluginManager().getPlugin("Cameras").getDataFolder().getAbsolutePath() + "/maps/map_" + mapId + ".png");
        
        // Read the image from the file and return the BufferedImage
        BufferedImage image = null;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
        
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
        }
        
        return id.toString();
    }
}
