package valorless.discordchat.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import valorless.discordchat.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class CameraResourcePackManager {

    private File resourcePackFile;
    private static HashMap<Material, BufferedImage> imageHashMap = new HashMap<>();
    private boolean isLoaded;

    public void initialize() {
        File dataFolder = Bukkit.getPluginManager().getPlugin("Cameras").getDataFolder();
        File mapDir = new File(dataFolder, "resource-packs");
        if (!mapDir.exists()) {
            mapDir.mkdir();
        }

        if (mapDir.listFiles().length == 0) {
            //Bukkit.getLogger().info("No resource pack found, downloading... (this may take a while)");
            Bukkit.getLogger().info("No resource pack found.");
            //this.downloadResourcePack();
        }

        for (File file : mapDir.listFiles()) {
            if (!file.getName().endsWith(".zip")) {
                this.resourcePackFile = file;
            } else {
                file.delete();
            }
        }

        if (this.resourcePackFile == null) {
            Bukkit.getLogger().warning("No resource pack found. Please restart.");
            return;
        }

        Bukkit.getLogger().info("Loading in resource pack (this may take a while)");

        new BukkitRunnable() {
            @Override
            public void run() {
                initializeImageHashmap();
                cancel();
            }
        }.runTaskAsynchronously(Main.plugin);
    }

    public File getTextureByMaterial(Material material) {
        if (this.resourcePackFile == null) {
            Bukkit.getLogger().warning("Tried getting texture file but no resource path found.");
            return null;
        }

        String textureName = material.toString().toLowerCase();
        File[] listOfFiles = this.resourcePackFile.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = file.getName();

                if (fileName.toLowerCase().contains(textureName))
                    return file;
                while (textureName.contains("_")) {
                    textureName = textureName.substring(0, textureName.lastIndexOf('_'));
                    if (fileName.toLowerCase().contains(textureName))
                        return file;
                }
            }
        }

        return null;
    }

    private void initializeImageHashmap() {
        if (this.resourcePackFile == null) {
            Bukkit.getLogger().warning("Tried getting texture file but no resource path found.");
            return;
        }

        for (Material material : Material.values()) {
            File textureFile = this.getTextureByMaterial(material);
            if (textureFile != null) {
                try {
                    BufferedImage image = ImageIO.read(textureFile);
                    imageHashMap.put(material, image);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        Bukkit.getLogger().info("Loaded " + this.imageHashMap.size() + " textures from resource pack "
                + this.resourcePackFile.getName());
        this.isLoaded = true;
    }

    public static HashMap<Material, BufferedImage> getImageHashMap() {
        return imageHashMap;
    }

    public boolean isLoaded() {
        return this.isLoaded;
    }
}
