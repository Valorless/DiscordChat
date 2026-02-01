package valorless.discordchat.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import valorless.discordchat.Lang;
import valorless.discordchat.Main;
import valorless.discordchat.linking.Linking;
import valorless.discordchat.utils.Json;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.config.Config;

public class Storage {
	
	public static void init() {
		Accounts.init();
		Inventories.init();
		Enderchests.init();	
	}
	
	public static void shutdown() {
		Accounts.shutdown();
		Inventories.shutdown();
		Enderchests.shutdown();
	}

	public static class Accounts {
		/**
		 * Backing configuration file used to persist finalized links on disk.
		 */
		public static Config dataFile;
		
		/**
		 * Finalized link data mapping Minecraft UUID to Discord ID.
		 */
		public static HashMap<UUID, Long> data = new HashMap<>();
		
		/**
		 * Pending link data used to store partial link attempts from either side.
		 * Keys are string representations of either UUID or Discord ID, values are the counterpart.
		 */
		public static HashMap<String, String> pending = new HashMap<>();
		
		/**
		 * Initialize the Linking service by registering event listeners and loading persisted data.
		 */
		public static void init() {
			Bukkit.getPluginManager().registerEvents(new Linking(), Main.plugin);
			loadData();
		}
		
		/**
		 * Gracefully shut down the Linking service by saving current link data to disk.
		 */
		public static void shutdown() {
			saveData();
		}
		
		/**
		 * Load persisted link data from the configuration file into memory.
		 *
		 * <p>Creates the data section if it does not exist and logs the number of loaded links.</p>
		 */
		private static void loadData() {
			dataFile = new Config(Main.plugin, "links.yml");
			int i = 0;
			ConfigurationSection section = dataFile.GetFile().getSection("data");
			if(section == null) {
				section = dataFile.GetFile().createSection("data");
			}
			Set<String> keys = section.getKeys(false);
			for(String key : keys) {
				UUID uuid = UUID.fromString(key);
				Long discordID = Long.valueOf(dataFile.GetString("data." + key));
				if(discordID == 0L) {
					Log.Warning(Main.plugin, "Invalid Discord ID for UUID: " + key);
					continue;
				}
				data.put(uuid, discordID);
				Log.Debug(Main.plugin, "Loaded link: " + uuid.toString() + " -> " + discordID);
				i++;
			}
			Log.Info(Main.plugin, "Loaded " + i + " linked accounts.");
		}
		
		/**
		 * Persist all finalized link data from memory to the configuration file.
		 */
		private static void saveData() {
			for(Entry<UUID, Long> entry : data.entrySet()) {
				dataFile.Set("data." + entry.getKey().toString(), entry.getValue().toString());
			}
			dataFile.SaveConfig();
		}
	}
	
	public static class Inventories implements Listener {
		
		public static class InventorySlot {
			public String display;
			public String name;
			public String item;
			public int amount;
			
			public InventorySlot(String display, String name, String item, int amount) {
				this.display = display;
				this.name = name;
				this.item = item;
				this.amount = amount;
			}
		}
		
		public static class InventoryEntry {
			public List<InventorySlot> slots = new ArrayList<>();
			
			public InventoryEntry(List<InventorySlot> slots) {
				this.slots = slots;
			}
		}
		
		/**
		 * Backing configuration file used to persist finalized links on disk.
		 */
		protected static Config dataFile;
		
		/**
		 * Finalized link data mapping Minecraft UUID to Discord ID.
		 */
		protected static HashMap<UUID, InventoryEntry> data = new HashMap<>();
		
		/**
		 * Initialize the Linking service by registering event listeners and loading persisted data.
		 */
		public static void init() {
			Bukkit.getPluginManager().registerEvents(new Storage.Inventories(), Main.plugin);
			loadData();
		}
		
		/**
		 * Gracefully shut down the Linking service by saving current link data to disk.
		 */
		public static void shutdown() {
			saveData();
		}
		
		/**
		 * Load persisted link data from the configuration file into memory.
		 *
		 * <p>Creates the data section if it does not exist and logs the number of loaded links.</p>
		 */
		private static void loadData() {
			dataFile = new Config(Main.plugin, "inventories.yml");
			int i = 0;
			ConfigurationSection section = dataFile.GetFile().getSection("data");
			if(section == null) {
				section = dataFile.GetFile().createSection("data");
			}
			Set<String> keys = section.getKeys(false);
			for(String key : keys) {
				UUID uuid = UUID.fromString(key);
				String value = dataFile.GetString("data." + key);
				data.put(uuid, fromJson(value));
				Log.Debug(Main.plugin, "Loaded inventory: " + uuid.toString() + " -> " + value);
				i++;
			}
			Log.Info(Main.plugin, "Loaded " + i + " inventories.");
		}
		
		/**
		 * Persist all finalized link data from memory to the configuration file.
		 */
		private static void saveData() {
			for(Entry<UUID, InventoryEntry> entry : data.entrySet()) {
				dataFile.Set("data." + entry.getKey().toString(), toJson(entry.getValue()));
			}
			dataFile.SaveConfig();
		}
		
		public static InventoryEntry fromJson(String json) {
	        return Json.builder.fromJson(json, InventoryEntry.class);
	    }
		
		public static String toJson(Object object) {
			return Json.builder.toJson(object);
		}
		
		public static InventoryEntry getInventory(UUID uuid) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
			if(player.isOnline()) {
				List<InventorySlot> slots = new ArrayList<>();
				for(ItemStack item : player.getPlayer().getInventory().getContents()) {
					if(item != null && item.getType() != Material.AIR) {
						String name = null;
						String display = null;
						if(item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
							display = Lang.RemoveColorCodesAndFormatting(item.getItemMeta().getDisplayName()); // Use custom display name if available
						}
						if(item.getItemMeta() != null && item.getItemMeta().hasItemName()) {
							name = Lang.RemoveColorCodesAndFormatting(item.getItemMeta().getItemName()); // Use custom item name if available
						}
						String type = Main.translator.Translate(item.getType().getTranslationKey());
						slots.add(new InventorySlot(display, name, type, item.getAmount())); // Store item name and amount
					}
				}
				return new InventoryEntry(slots);
			}
			return data.getOrDefault(uuid, null);
		}
		
		public static void updateEntry(Player player) {
			Log.Info(Main.plugin, "Updating inventory for player: " + player.getName());
			List<InventorySlot> slots = new ArrayList<>();
			for(ItemStack item : player.getInventory().getContents()) {
				if(item != null && item.getType() != Material.AIR) {
					String name = null;
					String display = null;
					if(item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
						display = Lang.RemoveColorCodesAndFormatting(item.getItemMeta().getDisplayName()); // Use custom display name if available
					}
					if(item.getItemMeta() != null && item.getItemMeta().hasItemName()) {
						name = Lang.RemoveColorCodesAndFormatting(item.getItemMeta().getItemName()); // Use custom item name if available
					}
					String type = Main.translator.Translate(item.getType().getTranslationKey());
					slots.add(new InventorySlot(display, name, type, item.getAmount())); // Store item name and amount
				}
			}
			InventoryEntry inv = new InventoryEntry(slots);
			data.put(player.getUniqueId(), inv);
		}
		
		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent event) {
			updateEntry(event.getPlayer());
		}
		
		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent event) {
			updateEntry(event.getPlayer());
		}
		
	}
	
	public static class Enderchests implements Listener {
		
		public static class EnderchestSlot {
			public String display;
			public String name;
			public String item;
			public int amount;
			
			public EnderchestSlot(String display, String name, String item, int amount) {
				this.display = display;
				this.name = name;
				this.item = item;
				this.amount = amount;
			}
		}
		
		public static class EnderchestEntry {
			public List<EnderchestSlot> slots = new ArrayList<>();
			
			public EnderchestEntry(List<EnderchestSlot> slots) {
				this.slots = slots;
			}
		}
		
		/**
		 * Backing configuration file used to persist finalized links on disk.
		 */
		protected static Config dataFile;
		
		/**
		 * Finalized link data mapping Minecraft UUID to Discord ID.
		 */
		protected static HashMap<UUID, EnderchestEntry> data = new HashMap<>();
		
		/**
		 * Initialize the Linking service by registering event listeners and loading persisted data.
		 */
		public static void init() {
			Bukkit.getPluginManager().registerEvents(new Storage.Enderchests(), Main.plugin);
			loadData();
		}
		
		/**
		 * Gracefully shut down the Linking service by saving current link data to disk.
		 */
		public static void shutdown() {
			saveData();
		}
		
		/**
		 * Load persisted link data from the configuration file into memory.
		 *
		 * <p>Creates the data section if it does not exist and logs the number of loaded links.</p>
		 */
		private static void loadData() {
			dataFile = new Config(Main.plugin, "enderchests.yml");
			int i = 0;
			ConfigurationSection section = dataFile.GetFile().getSection("data");
			if(section == null) {
				section = dataFile.GetFile().createSection("data");
			}
			Set<String> keys = section.getKeys(false);
			for(String key : keys) {
				UUID uuid = UUID.fromString(key);
				String value = dataFile.GetString("data." + key);
				data.put(uuid, fromJson(value));
				Log.Debug(Main.plugin, "Loaded enderchest: " + uuid.toString() + " -> " + value);
				i++;
			}
			Log.Info(Main.plugin, "Loaded " + i + " enderchests.");
		}
		
		/**
		 * Persist all finalized link data from memory to the configuration file.
		 */
		private static void saveData() {
			for(Entry<UUID, EnderchestEntry> entry : data.entrySet()) {
				dataFile.Set("data." + entry.getKey().toString(), toJson(entry.getValue()));
			}
			dataFile.SaveConfig();
		}
		
		public static EnderchestEntry fromJson(String json) {
	        return Json.builder.fromJson(json, EnderchestEntry.class);
	    }
		
		public static String toJson(Object object) {
			return Json.builder.toJson(object);
		}
		
		public static EnderchestEntry getEnderchest(UUID uuid) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
			if(player.isOnline()) {
				List<EnderchestSlot> slots = new ArrayList<>();
				for(ItemStack item : player.getPlayer().getEnderChest().getContents()) {
					if(item != null && item.getType() != Material.AIR) {
						String name = null;
						String display = null;
						if(item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
							display = Lang.RemoveColorCodesAndFormatting(item.getItemMeta().getDisplayName()); // Use custom display name if available
						}
						if(item.getItemMeta() != null && item.getItemMeta().hasItemName()) {
							name = Lang.RemoveColorCodesAndFormatting(item.getItemMeta().getItemName()); // Use custom item name if available
						}
						String type = Main.translator.Translate(item.getType().getTranslationKey());
						slots.add(new EnderchestSlot(display, name, type, item.getAmount())); // Store item name and amount
					}
				}
				return new EnderchestEntry(slots);
			}
			return data.getOrDefault(uuid, null);
		}
		
		public static void updateEntry(Player player) {
			Log.Info(Main.plugin, "Updating enderchest for player: " + player.getName());
			List<EnderchestSlot> slots = new ArrayList<>();
			for(ItemStack item : player.getEnderChest().getContents()) {
				if(item != null && item.getType() != Material.AIR) {
					String name = null;
					String display = null;
					if(item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
						display = Lang.RemoveColorCodesAndFormatting(item.getItemMeta().getDisplayName()); // Use custom display name if available
					}
					if(item.getItemMeta() != null && item.getItemMeta().hasItemName()) {
						name = Lang.RemoveColorCodesAndFormatting(item.getItemMeta().getItemName()); // Use custom item name if available
					}
					String type = Main.translator.Translate(item.getType().getTranslationKey());
					slots.add(new EnderchestSlot(display, name, type, item.getAmount())); // Store item name and amount
				}
			}
			EnderchestEntry inv = new EnderchestEntry(slots);
			data.put(player.getUniqueId(), inv);
		}
		
		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent event) {
			updateEntry(event.getPlayer());
		}
		
		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent event) {
			updateEntry(event.getPlayer());
		}
	}
	
}
