package valorless.discordchat;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import valorless.valorlessutils.ValorlessUtils.Log;

public class PlayerCache implements Listener {

	private static HashMap<String, UUID> cache = new HashMap<>();
	
	public static void init() {
		Bukkit.getPluginManager().registerEvents(new PlayerCache(), Main.plugin);
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> {
			for(OfflinePlayer player : Bukkit.getOfflinePlayers()) {
				UUID uuid = player.getUniqueId();
				String name = player.getName();
				cache.put(name, uuid);
			}
			Log.Info(Main.plugin, "PlayerCache initialized with " + cache.size() + " entries.");
		});
	}
	
	public static UUID getUUID(String name) {
		for (String key : cache.keySet()) {
			if (key.equalsIgnoreCase(name)) {
				return cache.get(key);
			}
		}
		return null;
	}
	
	public static OfflinePlayer getPlayer(String name) {
		for (String key : cache.keySet()) {
			if (key.equalsIgnoreCase(name)) {
				UUID uuid = cache.get(key);
				return Bukkit.getOfflinePlayer(uuid);
			}
		}
		return null;
	}
	
	public static HashMap<String, UUID> getCache() {
		return cache;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		String name = event.getPlayer().getName();
		cache.put(name,  uuid);
	}
	
}
