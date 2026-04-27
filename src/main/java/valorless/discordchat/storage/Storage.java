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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import valorless.discordchat.ConfigValidation;
import valorless.discordchat.Lang;
import valorless.discordchat.Main;
import valorless.discordchat.linking.Linking;
import valorless.discordchat.utils.Json;
import valorless.valorlessutils.logging.Log;
import valorless.valorlessutils.config.Config;

/**
 * Storage class responsible for managing all persistent data related to the plugin, including account links, inventories, and enderchests.
 * This class provides methods for initializing and shutting down the storage system, as well as nested classes for each type of data.
 */
public class Storage {

	/**
	 * Initialize the Storage service by initializing all relevant data managers and registering necessary event listeners.
	 * This should be called when the plugin is enabled to set up the storage system and load any persisted data.
	 */
	public static void init() {
		Accounts.init();
		Inventories.init();
		Enderchests.init();	
	}

	/**
	 * Gracefully shut down the Storage service by saving all relevant data to disk.
	 * This should be called when the plugin is disabled to ensure no data loss.
	 */
	public static void shutdown() {
		Accounts.shutdown();
		Inventories.shutdown();
		Enderchests.shutdown();
	}

	/**
	 * Manages Minecraft-to-Discord account link persistence and validation.
	 */
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
			dataFile = ConfigValidation.validateAndGetConfig("links.yml");
			//dataFile = new Config("links.yml");
			Linking.setLinkingRole(Long.parseLong(dataFile.getString("config.discord-role")));

			int i = 0;
			ConfigurationSection section = dataFile.getFile().getSection("data");
			if(section == null) {
				section = dataFile.getFile().createSection("data");
			}
			Set<String> keys = section.getKeys(false);
			for(String key : keys) {
				UUID uuid = UUID.fromString(key);
				long discordID = Long.parseLong(dataFile.getString("data." + key));
				if(discordID == 0L) {
					Log.warning(Main.plugin, "Invalid Discord ID for UUID: " + key);
					continue;
				}
				if(checkNull(uuid)){
					continue;
				}
				data.put(uuid, discordID);
				Log.debug(Main.plugin, "Loaded link: " + uuid + " -> " + discordID);
				i++;
			}
			Log.info(Main.plugin, "Loaded " + i + " linked accounts.");
			dataFile.saveConfig(); // Save any potential cleanups immediately
		}
		
		/**
		 * Persist all finalized link data from memory to the configuration file.
		 */
		private static void saveData() {
			for(Entry<UUID, Long> entry : data.entrySet()) {
				dataFile.set("data." + entry.getKey().toString(), entry.getValue().toString());
			}
			dataFile.saveConfig();
		}

		/**
		 * Check if the given UUID corresponds to a valid offline player.
		 * This is used to validate stored links.
		 *
		 * @param uuid the UUID to check
		 * @return true if the UUID does not correspond to any offline player, false otherwise
		 */
		private static boolean checkNull(UUID uuid){
			return Bukkit.getOfflinePlayer(uuid).getName() == null;
		}
	}
	
	/**
	 * Tracks and persists snapshots of player inventories, and keeps them updated on join/quit.
	 */
	public static class Inventories implements Listener {

		/**
		 * Data structure representing a single inventory slot, including display name, item name, type, and amount.
		 */
		public static class InventorySlot {
			public String display;
			public String name;
			public String item;
			public int amount;

			/**
			 * Constructs an InventorySlot with the given display name, item name, type, and amount.
			 *
			 * @param display the custom display name of the item (if any), with color codes removed
			 * @param name the base name of the item (if any), with color codes removed
			 * @param item the translated material type of the item
			 * @param amount the quantity of the item in this slot
			 */
			public InventorySlot(String display, String name, String item, int amount) {
				this.display = display;
				this.name = name;
				this.item = item;
				this.amount = amount;
			}
		}

		/**
		 * Data structure representing a player's inventory, consisting of a list of inventory slots.
		 */
		public static class InventoryEntry {
			/** List of inventory slots representing the contents of a player's inventory.
			 * Each slot includes the display name, item name, type, and amount of the item in that slot.
			 */
			public List<InventorySlot> slots;

			/**
			 * Constructs an InventoryEntry with the given list of inventory slots.
			 *
			 * @param slots the list of InventorySlot objects representing the player's inventory contents
			 */
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
			ConfigurationSection section = dataFile.getFile().getSection("data");
			if(section == null) {
				section = dataFile.getFile().createSection("data");
			}
			Set<String> keys = section.getKeys(false);
			for(String key : keys) {
				UUID uuid = UUID.fromString(key);
				if(Bukkit.getOfflinePlayer(uuid).getName() == null) continue;
				String value = dataFile.getString("data." + key);
				data.put(uuid, fromJson(value));
				Log.debug(Main.plugin, "Loaded inventory: " + uuid.toString() + " -> " + value);
				i++;
			}
			Log.info(Main.plugin, "Loaded " + i + " inventories.");
		}
		
		/**
		 * Persist all finalized link data from memory to the configuration file.
		 */
		private static void saveData() {
			for(Entry<UUID, InventoryEntry> entry : data.entrySet()) {
				dataFile.set("data." + entry.getKey().toString(), toJson(entry.getValue()));
			}
			dataFile.saveConfig();
		}
		
		/**
		 * Deserialize an inventory entry from JSON.
		 *
		 * @param json serialized inventory entry JSON
		 * @return deserialized inventory entry
		 */
		public static InventoryEntry fromJson(String json) {
	        return Json.builder.fromJson(json, InventoryEntry.class);
	    }
		
		/**
		 * Serialize an object to JSON for storage.
		 *
		 * @param object object to serialize
		 * @return JSON representation of the object
		 */
		public static String toJson(Object object) {
			return Json.builder.toJson(object);
		}
		
		/**
		 * Get an inventory snapshot for the given player UUID.
		 *
		 * <p>If the player is currently online, a fresh snapshot is generated from the live inventory.
		 * Otherwise, the last persisted snapshot is returned.</p>
		 *
		 * @param uuid player UUID
		 * @return inventory entry for the player, or {@code null} if none exists
		 */
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
		
		/**
		 * Update the cached inventory snapshot for the given player.
		 *
		 * @param player player whose inventory should be captured
		 */
		public static void updateEntry(Player player) {
			Log.debug(Main.plugin, "Updating inventory for player: " + player.getName());
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
		
		/**
		 * Refresh the joining player's cached inventory snapshot.
		 *
		 * @param event join event
		 */
		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent event) {
			updateEntry(event.getPlayer());
		}
		
		/**
		 * Refresh the leaving player's cached inventory snapshot.
		 *
		 * @param event quit event
		 */
		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent event) {
			updateEntry(event.getPlayer());
		}
		
	}
	
	/**
	 * Tracks and persists snapshots of player ender chests, and keeps them updated on join/quit.
	 */
	public static class Enderchests implements Listener {
		
		/**
		 * Data structure representing a single ender chest slot, including display name, item name,
		 * translated item type, and amount.
		 */
		public static class EnderchestSlot {
			public String display;
			public String name;
			public String item;
			public int amount;
			
			/**
			 * Constructs an EnderchestSlot with the given display name, item name, type, and amount.
			 *
			 * @param display the custom display name of the item, if present
			 * @param name the custom item name, if present
			 * @param item translated item type
			 * @param amount quantity of the item in this slot
			 */
			public EnderchestSlot(String display, String name, String item, int amount) {
				this.display = display;
				this.name = name;
				this.item = item;
				this.amount = amount;
			}
		}
		
		/**
		 * Data structure representing a player's ender chest snapshot.
		 */
		public static class EnderchestEntry {
			public List<EnderchestSlot> slots;
			
			/**
			 * Constructs an EnderchestEntry with the given slot list.
			 *
			 * @param slots ender chest slots to include in the snapshot
			 */
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
			ConfigurationSection section = dataFile.getFile().getSection("data");
			if(section == null) {
				section = dataFile.getFile().createSection("data");
			}
			Set<String> keys = section.getKeys(false);
			for(String key : keys) {
				UUID uuid = UUID.fromString(key);
				if(Bukkit.getOfflinePlayer(uuid).getName() == null) continue;
				String value = dataFile.getString("data." + key);
				data.put(uuid, fromJson(value));
				Log.debug(Main.plugin, "Loaded enderchest: " + uuid.toString() + " -> " + value);
				i++;
			}
			Log.info(Main.plugin, "Loaded " + i + " enderchests.");
		}
		
		/**
		 * Persist all finalized link data from memory to the configuration file.
		 */
		private static void saveData() {
			for(Entry<UUID, EnderchestEntry> entry : data.entrySet()) {
				dataFile.set("data." + entry.getKey().toString(), toJson(entry.getValue()));
			}
			dataFile.saveConfig();
		}
		
		/**
		 * Deserialize an ender chest entry from JSON.
		 *
		 * @param json serialized ender chest entry JSON
		 * @return deserialized ender chest entry
		 */
		public static EnderchestEntry fromJson(String json) {
	        return Json.builder.fromJson(json, EnderchestEntry.class);
	    }
		
		/**
		 * Serialize an object to JSON for storage.
		 *
		 * @param object object to serialize
		 * @return JSON representation of the object
		 */
		public static String toJson(Object object) {
			return Json.builder.toJson(object);
		}
		
		/**
		 * Get an ender chest snapshot for the given player UUID.
		 *
		 * <p>If the player is currently online, a fresh snapshot is generated from the live ender chest.
		 * Otherwise, the last persisted snapshot is returned.</p>
		 *
		 * @param uuid player UUID
		 * @return ender chest entry for the player, or {@code null} if none exists
		 */
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
		
		/**
		 * Update the cached ender chest snapshot for the given player.
		 *
		 * @param player player whose ender chest should be captured
		 */
		public static void updateEntry(Player player) {
			Log.debug(Main.plugin, "Updating enderchest for player: " + player.getName());
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
		
		/**
		 * Refresh the joining player's cached ender chest snapshot.
		 *
		 * @param event join event
		 */
		@EventHandler
		public void onPlayerJoin(PlayerJoinEvent event) {
			updateEntry(event.getPlayer());
		}
		
		/**
		 * Refresh the leaving player's cached ender chest snapshot.
		 *
		 * @param event quit event
		 */
		@EventHandler
		public void onPlayerQuit(PlayerQuitEvent event) {
			updateEntry(event.getPlayer());
		}
	}
	
}
