package valorless.discordchat.linking;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import valorless.discordchat.Main;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.config.Config;

/**
 * Manages linking between Minecraft player UUIDs and Discord user IDs.
 *
 * <p>This class supports a two-step pending process where each side (Minecraft or Discord)
 * can initiate linking, and the link is finalized once both sides provide matching information.
 * It also provides utilities to query and remove existing links.</p>
 */
public class Linking implements Listener{
	
	/**
	 * Backing configuration file used to persist finalized links on disk.
	 */
	protected static Config dataFile;
	
	/**
	 * Finalized link data mapping Minecraft UUID to Discord ID.
	 */
	private static HashMap<UUID, Long> data = new HashMap<>();
	/**
	 * Pending link data used to store partial link attempts from either side.
	 * Keys are string representations of either UUID or Discord ID, values are the counterpart.
	 */
	private static HashMap<String, String> pending = new HashMap<>();
	
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
	
	/**
	 * Initiate or complete a link from the Minecraft side.
	 *
	 * <p>If no pending entry exists for the given UUID, it creates one pointing to the Discord ID
	 * and waits for the Discord side to confirm. If a matching pending entry already exists for both
	 * UUID and Discord ID, the link is finalized and stored in {@link #data}.</p>
	 *
	 * @param uuid the Minecraft player's UUID
	 * @param discordID the Discord user's numeric ID
	 * @param channelID the Discord channel to send status messages to (optional)
	 * @return true if the link was finalized, false if it remains pending or failed
	 */
	public static Boolean addLink(UUID uuid, Long discordID, Long channelID) {
		Log.Info(Main.plugin, "Attempting to link Minecraft UUID " + uuid.toString() + " with Discord ID " + Main.bot.getUsernameByID(discordID));
		if(isLinked(uuid)) return false;
		
		if(!pending.containsKey(uuid.toString()) && !isPending("" + discordID)) {
			pending.put(uuid.toString(), "" + discordID);
		}
		if(check(uuid, discordID, channelID)) {
			return true;
		}else {
			OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
			if(player.isOnline()) {
				player.getPlayer().sendMessage("§7[§9DiscordChat§7]§r A link request has been initiated for your account.\nPlease confirm the link on Discord.");
			}
			return false;
		}
	}
	
	/**
	 * Initiate or complete a link from the Discord side.
	 *
	 * <p>If no pending entry exists for the given Discord ID, it creates one pointing to the UUID
	 * and waits for the Minecraft side to confirm. If a matching pending entry already exists for both
	 * Discord ID and UUID, the link is finalized and stored in {@link #data}.</p>
	 *
	 * @param discordID the Discord user's numeric ID
	 * @param uuid the Minecraft player's UUID
	 * @param channelID the Discord channel to send status messages to (optional)
	 * @return true if the link was finalized, false if it remains pending or failed
	 */
	public static Boolean addLink(Long discordID, UUID uuid, Long channelID) {
		Log.Info(Main.plugin, "Attempting to link Discord ID " + discordID + " with Minecraft UUID " + uuid.toString());
		if(isLinked(discordID)) return false;
		
		if(!pending.containsKey("" + discordID) && !isPending(uuid.toString())) {
			pending.put("" + discordID, uuid.toString());
		}
		if(check(uuid, discordID, channelID)) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Determine whether a given key (UUID or Discord ID as string) exists in the pending map
	 * either as a key or value awaiting confirmation.
	 *
	 * @param key the UUID or Discord ID string to check
	 * @return true if the key is part of a pending link, false otherwise
	 */
	public static Boolean isPending(String key) {
		for(String k : pending.keySet()) {
			Log.Info(Main.plugin, "Pending key: " + k + " -> " + pending.get(k));
			if(pending.get(k).equalsIgnoreCase(key)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if both sides have initiated a link and finalize it if so.
	 *
	 * @param uuid the Minecraft player's UUID
	 * @param discordID the Discord user's numeric ID
	 * @param channelID the Discord channel to send status messages to (optional)
	 * @return true if the link was finalized, false otherwise
	 */
	public static Boolean check(UUID uuid, Long discordID, Long channelID) {
		if(isPending(uuid.toString()) && isPending("" + discordID)) {
			data.put(uuid, discordID);
			Bukkit.getScheduler().runTask(Main.plugin, () -> {
				Bukkit.getPluginManager().callEvent(new LinkEvent(uuid, discordID, channelID));
			});
			return true;
		} else return false;
	}
	
	/**
	 * Remove a link by Minecraft UUID.
	 *
	 * @param uuid the Minecraft player's UUID to unlink
	 * @return true if a link existed and was removed, false otherwise
	 */
	public static Boolean unlink(UUID uuid) {
		Long discordID = data.get(uuid);
		if(discordID != null) {
			data.remove(uuid);
			dataFile.Set("data." + uuid.toString(), null);
			Bukkit.getScheduler().runTask(Main.plugin, () -> {
				Bukkit.getPluginManager().callEvent(new UnlinkEvent(uuid, discordID));
			});
			Log.Info(Main.plugin, "Unlinked Minecraft player " + Bukkit.getOfflinePlayer(uuid).getName() + " from Discord ID: " + discordID);
			Main.bot.removeRole(discordID, 1466922269984161883L);
			return true;
		}
		return false;
	}
	
	/**
	 * Remove a link by Discord ID.
	 *
	 * @param discordID the Discord user's numeric ID to unlink
	 * @return true if a link existed and was removed, false otherwise
	 */
	public static Boolean unlink(Long discordID) {
		UUID uuid = getMinecraftUUID(discordID);
		if(uuid != null) {
			data.remove(uuid);
			dataFile.Set("data." + uuid.toString(), null);
			Bukkit.getScheduler().runTask(Main.plugin, () -> {
				Bukkit.getPluginManager().callEvent(new UnlinkEvent(uuid, discordID));
			});
			Log.Info(Main.plugin, "Unlinked Discord ID: " + discordID + " from Minecraft player " + Bukkit.getOfflinePlayer(uuid).getName());
			Main.bot.removeRole(discordID, 1466922269984161883L);
			return true;
		}
		return false;
	}
	
	/**
	 * Check if a Minecraft UUID is linked.
	 *
	 * @param uuid the Minecraft player's UUID
	 * @return true if the UUID has a finalized link, false otherwise
	 */
	public static boolean isLinked(UUID uuid) {
		return data.containsKey(uuid);
	}
	
	/**
	 * Check if a Discord ID is linked.
	 *
	 * @param discordID the Discord user's numeric ID
	 * @return true if the Discord ID has a finalized link, false otherwise
	 */
	public static boolean isLinked(Long discordID) {
		return data.containsValue(discordID);
	}
	
	/**
	 * Get the Discord ID linked to a Minecraft UUID.
	 *
	 * @param uuid the Minecraft player's UUID
	 * @return the linked Discord ID, or null if not linked
	 */
	public static Long getDiscordID(UUID uuid) {
		return data.getOrDefault(uuid, null);
	}
	
	/**
	 * Get the Minecraft UUID linked to a Discord ID.
	 *
	 * @param discordID the Discord user's numeric ID
	 * @return the linked Minecraft UUID, or null if not linked
	 */
	public static UUID getMinecraftUUID(Long discordID) {
		return data.entrySet().stream()
				.filter(entry -> entry.getValue().equals(discordID))
				.map(entry -> entry.getKey())
				.findFirst()
				.orElse(null);
	}
	
	/**
	 * Handle a successful link event to notify both Minecraft and Discord, clean up pending state,
	 * and assign any necessary roles.
	 *
	 * @param event the LinkEvent containing player UUID, Discord ID, and channel context
	 */
	@EventHandler
	public void onLink(LinkEvent event) {
		UUID uuid = event.getPlayer();
		Long discordID = event.getDiscordID();
		Long channelID = event.getChannelID();
		
		Log.Info(Main.plugin, "Linked Minecraft player " + Bukkit.getOfflinePlayer(uuid).getName() + " to Discord User: " + Main.bot.getUsernameByID(discordID));
		
		// Clean up pending entries after a successful link
		pending.remove(uuid.toString());
		pending.remove("" + discordID);
		
		if(Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(uuid).isOnline()) {
			Bukkit.getPlayer(uuid).sendMessage("§7[§9DiscordChat§7]§r §aYour account has been linked to Discord User: " + Main.bot.getUsernameByID(discordID));
		}
		
		if(channelID != null) {
			String message = String.format("<@%s> Account successfully linked to Minecraft player **%s**.", 
					discordID, 
					Bukkit.getOfflinePlayer(uuid).getName());
			MessageChannel channel = Main.bot.GetChannelByID(channelID);
			Main.bot.SendMessage(channel, message);
		}
		
		Main.bot.addRole(discordID, 1466922269984161883L);
	}
	
	/**
	 * Find an online player by exact username (case-insensitive).
	 *
	 * @param username the Minecraft username to search for
	 * @return the Player if online and matched, otherwise null
	 */
	public static Player getPlayer(String username) {
	    return Bukkit.getOnlinePlayers().stream()
	            .filter(player -> player.getName().equalsIgnoreCase(username))
	            .findFirst()
	            .orElse(null);
	}

}