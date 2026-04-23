package valorless.discordchat.hooks;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.player.PlayerProfile;

import valorless.discordchat.Main;
import valorless.valorlessutils.cache.PlayerCache;
import valorless.valorlessutils.logging.Log;

/**
 * Integration helper for mcMMO.
 * Provides detection, logging, and access to the plugin instance.
 */
public class mcmmoHook {
	
	/** Attempts to detect mcMMO and logs whether integration is active. */
	public static void Hook() {
		JavaPlugin plugin = Main.plugin;
		
		Log.debug(plugin, "Attempting to hook mcMMO.");
		if (Bukkit.getPluginManager().getPlugin("mcMMO") != null) {
    		Log.info(plugin, "mcMMO integrated!");
		}else {
			Log.debug(plugin, "mcMMO not detected.");
		}
	}
	
	/** Returns true when mcMMO is present on the server. */
	public static boolean isHooked() {
		if (Bukkit.getPluginManager().getPlugin("mcMMO") != null) {
    		return true;
		}else {
			return false;
		}
	}
	
	/** Returns the mcMMO plugin instance. */
	public static JavaPlugin getPlugin() {
		return (JavaPlugin) Bukkit.getPluginManager().getPlugin("mcMMO");
	}
	
	public static PlayerProfile getPlayerProfile(UUID uuid) {
		return mcMMO.getDatabaseManager().loadPlayerProfile(uuid);
	}
	
	public static String formatProfile(PlayerProfile profile) {
		return null; // Placeholder for future implementation
	}
	
	public static HashMap<String, Integer> getTop() {
		Log.debug(Main.plugin, "Fetching top mcMMO power levels.");
		HashMap<String, Integer> skillLevels = new HashMap<>();
    	PlayerCache.getCache().forEach((name, uuid) -> {
    		try {
    			skillLevels.put(name, ExperienceAPI.getPowerLevelOffline(uuid));
    		}catch(Exception e) {
				// Ignore players without mcMMO data
			}
		});
    	return skillLevels;
	}
}