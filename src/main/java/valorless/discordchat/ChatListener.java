package valorless.discordchat;

import org.bukkit.*;
import valorless.discordchat.discord.Bot;
import valorless.discordchat.hooks.EssentialsHook;
import valorless.discordchat.utils.Extra;
import valorless.discordchat.utils.ItemStackToPng;
import valorless.discordchat.utils.MapToImage;
import valorless.valorlessutils.logging.Log;
import valorless.valorlessutils.config.Config;
import valorless.valorlessutils.items.ItemUtils;
import valorless.valorlessutils.utils.Utils;

import org.bukkit.plugin.java.JavaPlugin;

import net.ess3.api.IUser;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class ChatListener implements Listener { // Primary objective of BanListener is to listen for Ban commands.
	public static JavaPlugin plugin;
	public static Config config;
	public static String url;
	static int strikes = 0;
	static boolean kick = false;

	public static void onEnable() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				strikes = 0;
			}
		}, 0L, 6000L);
	}

	@EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onAsyncPlayerChatEventMonitor(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}

		if(EventPriority.valueOf(config.getString("chat-event-priority")) == EventPriority.MONITOR) {
			ProccessMessage(event);
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onAsyncPlayerChatEventHighest(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}

		if(EventPriority.valueOf(config.getString("chat-event-priority")) == EventPriority.HIGHEST) {
			ProccessMessage(event);
		}
	}

	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = false)
	public void onAsyncPlayerChatEventHigh(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}

		if(EventPriority.valueOf(config.getString("chat-event-priority")) == EventPriority.HIGH) {
			ProccessMessage(event);
		}
	}

	@EventHandler (priority = EventPriority.LOW, ignoreCancelled = false)
	public void onAsyncPlayerChatEventLow(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}

		if(EventPriority.valueOf(config.getString("chat-event-priority")) == EventPriority.LOW) {
			ProccessMessage(event);
		}
	}

	@EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onAsyncPlayerChatEventLowest(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}

		if(EventPriority.valueOf(config.getString("chat-event-priority")) == EventPriority.LOWEST) {
			ProccessMessage(event);
		}
	}

	@EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}

		if(EventPriority.valueOf(config.getString("chat-event-priority")) == EventPriority.NORMAL) {
			ProccessMessage(event);
		}
	}

	void ProccessMessage(AsyncPlayerChatEvent event) {
		List<String> list = Main.filter.getStringList("chat-filter");
		String filtermsg = event.getMessage().toLowerCase();

		// Loop through each blocked word
		for (String entry : list) {
			// Create a regex pattern for whole word matching
			String regex = "\\b" + Pattern.quote(entry.toLowerCase()) + "\\b";
			if (filtermsg.matches(".*" + regex + ".*")) {
				// If a match is found, block the message
				event.getPlayer().sendMessage(String.format(Main.filter.getString("chat-filter-message"), entry));
				event.setCancelled(true);

				String msg = "§c[BLOCKED] " + event.getPlayer().getName() + ": " + event.getMessage();
				Log.error(plugin, msg);

				// Notify staff about the blocked message
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.hasPermission("discordchat.reload") || player.isOp()) {
						player.sendMessage(msg);
					}
				}
				return; // Stop further processing once a word is blocked
			}
		}
		if(filtermsg.contains("@")) {
			for (String entry : list) {
				String lowerEntry = entry.toLowerCase(); // Normalize case

				if (filtermsg.contains(lowerEntry)) { // Use contains() instead of regex
					// If a match is found, block the message
					event.getPlayer().sendMessage(String.format(Main.filter.getString("chat-filter-message"), entry));
					event.setCancelled(true);

					String msg = "§c[BLOCKED] " + event.getPlayer().getName() + ": " + event.getMessage();
					Log.error(plugin, msg);

					// Notify staff about the blocked message
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.hasPermission("discordchat.reload") || player.isOp()) {
							player.sendMessage(msg);
						}
					}
					return; // Stop further processing once a word is blocked
				}
			}
		}
		/*
		for(String entry : list) {
			if(filtermsg.contains(entry.toLowerCase())) {
				event.getPlayer().sendMessage(String.format(Main.filter.getString("chat-filter-message"), entry));
				event.setCancelled(true);
				String msg = "§c[BLOCKED] " + event.getPlayer().getName() + ": " + event.getMessage();
				Log.Error(plugin, msg);
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(player.hasPermission("discordchat.reload") || player.isOp()) {
						player.sendMessage(msg);
					}
				}
				return;
			}
			if(event.getMessage().contains(entry)) {
				event.getPlayer().sendMessage(String.format(Main.filter.getString("chat-filter-message"), entry));
				event.setCancelled(true);
				String msg = "§c[BLOCKED] " + event.getPlayer().getName() + ": " + event.getMessage();
				Log.Error(plugin, msg);
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(player.hasPermission("discordchat.reload") || player.isOp()) {
						player.sendMessage(msg);
					}
				}
				return;
			}
		}
		 */

		String message = Lang.Get("message")
				.replace("%player%", event.getPlayer().getName())
				.replace("%message%", event.getMessage());

		SendWebhook(event.getPlayer(), message, event.getPlayer().getInventory().getItemInMainHand());
	}


	int r = 0;
	@EventHandler (priority = EventPriority.HIGH)
	public void onAchievementGet(PlayerAdvancementDoneEvent event) {
		if(Main.enabled == false) {
			Log.warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		if(!config.getBool("achievement")) return;
		GameRule<Boolean> rule = null;
		try{
			rule = (GameRule<Boolean>) GameRule.getByName("announceAdvancements");
		}catch(IncompatibleClassChangeError e){
			try {
				rule = (GameRule<Boolean>) GameRule.getByName("show_advancement_messages");
			}catch(IncompatibleClassChangeError ex){
				rule = (GameRule<Boolean>) Registry.GAME_RULE.get(new NamespacedKey("minecraft", "show_advancement_messages"));
			}
		}
		if(r != 0 && rule == null) {
			Log.error(plugin, "Failed to get the advancements game rule.");
			r = 1;
		}
		if(rule == null || event.getPlayer().getWorld().getGameRuleValue(rule) == false) return;
		if(event.getAdvancement() == null) return;
		if(event.getAdvancement().getDisplay() == null) return;
		if(event.getAdvancement().getDisplay().getTitle() == null) return;
		if(event.getAdvancement().getDisplay().getDescription() == null) return;
		if(IsAchievementIgnored(event.getAdvancement().getDisplay().getTitle())) return;

		Bot.newChain().async(() -> {
			Player player = event.getPlayer();
			String title = "**%player%** has unlocked **%message%**."
					.replace("%player%", event.getPlayer().getName())
					.replace("%message%", event.getAdvancement().getDisplay().getTitle());

			DiscordWebhook webhook = new DiscordWebhook(config.getString("webhook-url"));
			webhook.setUsername(FormatUsername(player, config.getString("player-username")));
			//webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
			webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); // Fallback image, should the player not have a valid UUID. Might not work anymore..
			if(!Utils.IsStringNullOrEmpty(player.getUniqueId().toString())) {
				webhook.setAvatarUrl("https://visage.surgeplay.com/bust/512/" + player.getUniqueId().toString() + ".png"); // Get player UUID the normal way.
			}else {
				Log.debug(plugin, "Failed second attempt to get UUID of player" + player.getName() + ".");
				Log.debug(plugin, "Cannot set the bot's picture.");
			}

			webhook.addEmbed(new DiscordWebhook.EmbedObject()
					.setTitle(FormatMessage(player, title))
					.setDescription(FormatMessage(player, event.getAdvancement().getDisplay().getDescription() + "."))
					.setColor(Color.decode("#2afa4d"))
			);

			try {
				//Log.Info(plugin, "Executing webhook.");
				webhook.execute();
			} catch (IOException e) {
				e.printStackTrace();
				ConnectionFailed();
			}
		}).execute();
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		//Log.Debug(plugin, event.getDeathMessage().toString());
		//Log.Debug(plugin, event.toString());
		if(Main.enabled == false) {
			Log.warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		if(!config.getBool("death")) return;
		if(event.getDeathMessage() == null) return;

		if(Utils.IsStringNullOrEmpty(event.getDeathMessage())) {
			event.setDeathMessage(String.format("%s died.", event.getEntity().getName()));
		}

		Bot.newChain().async(() -> {
			Player player = event.getEntity();

			DiscordWebhook webhook = new DiscordWebhook(config.getString("webhook-url"));
			webhook.setUsername(FormatUsername(player, config.getString("player-username").replace("%player%", player.getName())));
			String message = event.getDeathMessage().replace(event.getEntity().getName(), "**" + event.getEntity().getName() + "**");
			//webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
			webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); // Fallback image, should the player not have a valid UUID. Might not work anymore..
			if(!Utils.IsStringNullOrEmpty(player.getUniqueId().toString())) {
				webhook.setAvatarUrl("https://visage.surgeplay.com/bust/512/" + player.getUniqueId().toString() + ".png"); // Get player UUID the normal way.
			}else {
				Log.debug(plugin, "Failed second attempt to get UUID of player" + player.getName() + ".");
				Log.debug(plugin, "Cannot set the bot's picture.");
			}

			webhook.addEmbed(new DiscordWebhook.EmbedObject()
					.setTitle(FormatMessage(event.getEntity(), message + "."))
					.setColor(Color.decode("#ff2b2b"))
			);

			try {
				//Log.Info(plugin, "Executing webhook.");
				webhook.execute();
			} catch (IOException e) {
				e.printStackTrace();
				ConnectionFailed();
			}
		}).execute();
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(event.getPlayer().hasPermission("essentials.silentjoin")) {
			if(!Utils.IsStringNullOrEmpty(Main.config.getString("custom-join"))) {
				String join = Main.config.getString("custom-join");
				join = join.replace("%username%", event.getPlayer().getName());
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(player.hasPermission("essentials.silentjoin")) {
						player.sendMessage(Lang.Parse(Lang.ParsePlaceholders(join + " &8(Vanished)", event.getPlayer())));
					}
				}
				if(Utils.IsStringNullOrEmpty(event.getJoinMessage())) {

				}
				else {
					event.setJoinMessage(null);
				}
			}
			return;
			//Let players with silentjoin see others with silentjoin join, but say vanished. same for quit
		}

		if(!Utils.IsStringNullOrEmpty(Main.config.getString("custom-join"))) {
			String join = Main.config.getString("custom-join");
			join = join.replace("%username%", event.getPlayer().getName());
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(Lang.Parse(Lang.ParsePlaceholders(join, event.getPlayer())));
			}
			if(Utils.IsStringNullOrEmpty(event.getJoinMessage())) {

			}
			else {
				event.setJoinMessage(join);
			}
		}

		if(Main.config.getBool("join-sound.enabled")) {
			Location loc = event.getPlayer().getLocation();
			if(event.getPlayer().hasPlayedBefore()) {
				Sound s = new Sound(Main.config.getString("join-sound.sound"),
						Main.config.getDouble("join-sound.volume"),
						Main.config.getDouble("join-sound.pitch"));
				for(Player player : Bukkit.getOnlinePlayers()) {
					s.play(player);
				}
			}else if(Main.config.getBool("new-player-sound.enabled")) {
				Sound s = new Sound(Main.config.getString("new-player-sound.sound"),
						Main.config.getDouble("new-player-sound.volume"),
						Main.config.getDouble("new-player-sound.pitch"));
				for(Player player : Bukkit.getOnlinePlayers()) {
					s.play(player);
				}
			}
		}

		if(Main.enabled == false) {
			Log.warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		if(!config.getBool("join")) return;

		Bot.newChain().async(() -> {
			DiscordWebhook webhook = new DiscordWebhook(config.getString("webhook-url"));

			webhook.setUsername(config.getString("server-username"));
			//webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
			webhook.setAvatarUrl(config.getString("server-icon-url"));

			if(!event.getPlayer().hasPlayedBefore()) {
				webhook.setContent(FormatMessage(event.getPlayer(), Lang.Get("join-first-time")
						.replace("%player%", event.getPlayer().getName())
				));
			}else {
				String msg = Lang.Get("join")
						.replace("%player%", event.getPlayer().getName());
				webhook.setContent(FormatMessage(event.getPlayer(), msg));
			}

			try {
				//Log.Info(plugin, "Executing webhook.");
				webhook.execute();
			} catch (IOException e) {
				e.printStackTrace();
				ConnectionFailed();
			}
		}).execute();
	}

	@SuppressWarnings("deprecation")
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if(event.getPlayer().hasPermission("essentials.silentquit")) {
			if(!Utils.IsStringNullOrEmpty(Main.config.getString("custom-leave"))) {
				String leave = Main.config.getString("custom-leave");
				leave = leave.replace("%username%", event.getPlayer().getName());
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(player.hasPermission("essentials.silentquit")) {
						player.sendMessage(Lang.Parse(Lang.ParsePlaceholders(leave + " &8(Vanished)", event.getPlayer())));
					}
				}
				if(Utils.IsStringNullOrEmpty(event.getQuitMessage())) {

				}
				else {
					event.setQuitMessage(null);
				}
			}
			return;
			//Let players with silentjoin see others with silentjoin join, but say vanished. same for quit
		}
		//if(event.getPlayer().hasPermission("essentials.silentquit")) return;
		if(kick) return;
		Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run(){
				if(!Utils.IsStringNullOrEmpty(Main.config.getString("custom-leave")) && !kick) {
					String leave = Main.config.getString("custom-leave");
					String pl = event.getPlayer().getName();
					leave = leave.replace("%username%", pl);
					leave = leave.replace("%cause%", "Disconnect");
					if(Utils.IsStringNullOrEmpty(event.getQuitMessage())) {
						for(Player player : Bukkit.getOnlinePlayers()) {
							player.sendMessage(Lang.Parse(Lang.ParsePlaceholders(leave, event.getPlayer())));
						}
					}
					else {
						event.setQuitMessage(leave);
					}
				}
			}
		}, 1L);

		if(Main.enabled == false) {
			Log.warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		if(!config.getBool("quit")) return;
		if(EssentialsHook.isHooked()) {
			IUser pl = EssentialsHook.getInstance().getUser(event.getPlayer());
			if(pl.isVanished()) return;
		}

		Bot.newChain().async(() -> {
			DiscordWebhook webhook = new DiscordWebhook(config.getString("webhook-url"));

			webhook.setUsername(config.getString("server-username"));
			String msg = Lang.Get("leave")
					.replace("%player%", event.getPlayer().getName());
			webhook.setContent(FormatMessage(event.getPlayer(), msg));
			//webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
			webhook.setAvatarUrl(config.getString("server-icon-url"));

			try {
				//Log.Info(plugin, "Executing webhook.");
				webhook.execute();
			} catch (IOException e) {
				e.printStackTrace();
				ConnectionFailed();
			}
		}).execute();
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerKick(PlayerKickEvent event) {
		if(event.getPlayer().hasPermission("essentials.silentquit")) return;
		if(event.isCancelled()) return;
		kick = true;
		if(!Utils.IsStringNullOrEmpty(Main.config.getString("custom-leave"))) {
			Log.info(plugin, "Kick event");
			String leave = Main.config.getString("custom-leave");
			String reason = event.getReason();
			String pl = event.getPlayer().getName();
			leave = leave.replace("%username%", pl);

			if(reason != null) {
				String sect = "custom-leave-causes";
				for(Object entry : Main.config.getConfigurationSection(sect).getKeys(false)) {
					if(reason.contains(Main.config.getString(String.format("%s.%s.keyword", sect, entry.toString())))) {
						leave = leave.replace("%cause%", Main.config.getString(String.format("%s.%s.value", sect, entry.toString())));
					}
				}
			}

			if (reason != null && reason.contains("You logged in from another location")) {
				leave = leave.replace("%cause%", "Disconnect");
			}
			if (reason != null && reason.contains("Timed out")) {
				leave = leave.replace("%cause%", "Timed out");
			}
			else if (reason != null && reason.contains("You have been kicked for idling more than")) {
				leave = leave.replace("%cause%", "AFK Kicked");
			}
			else if (reason != null && reason.contains("Internal Exception: io.netty.handler.timeout.ReadTimeoutException")) {
				leave = leave.replace("%cause%", "Timed Out");
			}
			else if (reason != null && reason.contains("Unfair Advantage")) {
				leave = leave.replace("%cause%", "Cheating?");
			}
			else {
				leave = leave.replace("%cause%", "Kicked");
			}

			if(reason != null) {
				String sect = "custom-leave-causes";
				for(Object entry : Main.config.getStringList("sect")) {
					if(reason.contains(Main.config.getString(String.format("%s.%s.keyword", sect, entry.toString())))) {
						leave = leave.replace("%cause%", Main.config.getString(String.format("%s.%s.value", sect, entry.toString())));
					}
				}
			}

			for(Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(Lang.Parse(Lang.ParsePlaceholders(leave, event.getPlayer())));
			}
			Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){

				@Override
				public void run(){
					kick = false;
				}
			}, 3L);

			Bot.newChain().async(() -> {
				DiscordWebhook webhook = new DiscordWebhook(config.getString("webhook-url"));

				webhook.setUsername(config.getString("server-username"));
				String msg = Lang.Get("leave")
						.replace("%player%", event.getPlayer().getName());
				webhook.setContent(FormatMessage(event.getPlayer(), msg));
				//webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
				webhook.setAvatarUrl(config.getString("server-icon-url"));

				try {
					//Log.Info(plugin, "Executing webhook.");
					webhook.execute();
				} catch (IOException e) {
					e.printStackTrace();
					ConnectionFailed();
				}
			}).execute();
		}
	}

	public void SendWebhook(Player player, String msg, Object... args) {
		if(Main.enabled == false) {
			Log.warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}

		/*Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){

		    @Override
		    public void run() {*/
		Bot.newChain().async(() -> {
			final String _message = msg;
			String message = new String(_message.replace(String.format("<chat=%s:[i]:>", player.getUniqueId().toString()), "[i]")
					.replace(String.format("<chat=%s:[I]:>", player.getUniqueId().toString()), "[i]") //Caps version
					.replace(String.format("<chat=%s:[item]:>", player.getUniqueId().toString()), "[item]")
					.replace(String.format("<chat=%s:[ITEM]:>", player.getUniqueId().toString()), "[item]")
					.replace(String.format("<chat=%s:[inv]:>", player.getUniqueId().toString()), "[inv]")
					.replace(String.format("<chat=%s:[INV]:>", player.getUniqueId().toString()), "[inv]")
					.replace(String.format("<chat=%s:[ender]:>", player.getUniqueId().toString()), "[ender]")
					.replace(String.format("<chat=%s:[ENDER]:>", player.getUniqueId().toString()), "[ender]")
					.replace(String.format("<chat=%s:[ping]:>", player.getUniqueId().toString()), "[ping]")
					.replace(String.format("<chat=%s:[PING]:>", player.getUniqueId().toString()), "[ping]")
					.replace(String.format("<chat=%s:[POS]:>", player.getUniqueId().toString()), "[pos]")
					.replace(String.format("<chat=%s:[pos]:>", player.getUniqueId().toString()), "[pos]").toCharArray());
			//Log.Warning(plugin, message);
			DiscordWebhook webhook = new DiscordWebhook(config.getString("webhook-url"));
			webhook.setUsername(
					FormatUsername(player, config.getString("player-username").replace("%player%", player.getName()))
			);
			if (Bukkit.getPluginManager().getPlugin("InteractiveChat") != null) {
				try {
					//Log.Info(plugin, message);
					ItemStack item = (ItemStack) args[0];
					if (message.contains("[item]") && args.length != 0 && item.hasItemMeta() || message.contains("[i]") && args.length != 0 && item.hasItemMeta()) {
						//Log.Warning(plugin, message);
						String id = "";
						if (item.getType() == Material.FILLED_MAP) {
							try {
								id = MapToImage.getMapAsImage(item);
								if (id == null) {
									id = ItemStackToPng.createItemStackImage(item);
								}
							} catch (Exception e) {

							}
						} else {
							id = ItemStackToPng.createItemStackImage(item);
						}
						try {
							webhook.addEmbed(new DiscordWebhook.EmbedObject()
									.setTitle(FormatMessage(player, "Click image to view better."))
									.setImage(url + id + ".png")
							);
							Log.debug(Main.plugin, url + id + ".png");
						} catch (Exception e) {
						}
					}
					if (item.hasItemMeta()) {
						if (item.getItemMeta().hasDisplayName()) {
							message = message.replace("[item]", "[" + item.getItemMeta().getDisplayName() + "]");
							message = message.replace("[ITEM]", "[" + item.getItemMeta().getDisplayName() + "]");
							message = message.replace("[i]", "[" + item.getItemMeta().getDisplayName() + "]");
							message = message.replace("[I]", "[" + item.getItemMeta().getDisplayName() + "]");
							//message = message.replace("<%s:[item]:>", "[" + item.getItemMeta().getDisplayName() + "]");
							//message = message.replace("<%s:[i]:>", "[" + item.getItemMeta().getDisplayName() + "]");
						} else if (ItemUtils.HasItemName(item)) {
							message = message.replace("[item]", "[" + ItemUtils.GetItemName(item) + "]");
							message = message.replace("[ITEM]", "[" + ItemUtils.GetItemName(item) + "]");
							message = message.replace("[i]", "[" + ItemUtils.GetItemName(item) + "]");
							message = message.replace("[I]", "[" + ItemUtils.GetItemName(item) + "]");
							//message = message.replace("<%s:[item]:>", "[" + ItemUtils.GetItemName(item) + "]");
							//message = message.replace("<%s:[i]:>", "[" + ItemUtils.GetItemName(item) + "]");
						} else {
							message = message.replace("[item]", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
							message = message.replace("[ITEM]", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
							message = message.replace("[i]", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
							message = message.replace("[I]", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
							//message = message.replace("<%s:[item]:>", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
							//message = message.replace("<%s:[i]:>", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
						}
					} else {
						message = message.replace("[item]", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
						message = message.replace("[ITEM]", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
						message = message.replace("[i]", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
						message = message.replace("[I]", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
						//message = message.replace("<%s:[item]:>", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
						//message = message.replace("<%s:[i]:>", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
					}
				} catch (Exception E) {
					E.printStackTrace();
				}

				if (message.contains("[ping]") || message.contains("[PING]")) {
					//Log.Warning(plugin, message);
					message = message.replace("[ping]", player.getPing() + "ms");
					message = message.replace("[PING]", player.getPing() + "ms");
					//message = message.replace(String.format("<%s:[ping]:>", player.getUniqueId().toString().toLowerCase()), player.getPing() + "ms");
				}

				if (message.contains("[pos]") || message.contains("[POS]")) {
					//Log.Warning(plugin, message);
					Location loc = player.getLocation();
					message = message.replace("[pos]", String.format("%s: %s, %s, %s", player.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ()));
					message = message.replace("[POS]", String.format("%s: %s, %s, %s", player.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ()));
					//message = message.replace(String.format("<%s:[pos]:>", player.getUniqueId().toString().toLowerCase()), String.format("%s, %s, %s", loc.getX(), loc.getY(), loc.getZ()));
				}

				if (message.contains("[inv]") || message.contains("[INV]")) {
					try {
						String inventory = Extra.inventoryString(player.getUniqueId());
						webhook.addEmbed(new DiscordWebhook.EmbedObject()
								.setTitle(FormatMessage(player, String.format("%s's Inventory", player.getName())))
								.setDescription(FormatMessage(player, inventory))
						);
					} catch (Exception e) {}
					message = message.replace("[inv]", "[Inventory]");
					message = message.replace("[INV]", "[Inventory]");
				}

				if (message.contains("[ender]") || message.contains("[ENDER]")) {
					try {
						String enderchest = Extra.enderchestString(player.getUniqueId());
						webhook.addEmbed(new DiscordWebhook.EmbedObject()
								.setTitle(FormatMessage(player, String.format("%s's Enderchest", player.getName())))
								.setDescription(FormatMessage(player, enderchest))
						);
					} catch (Exception e) {}
					message = message.replace("[ender]", "[Enderchest]");
					message = message.replace("[ENDER]", "[Enderchest]");
					//message = message.replace(String.format("<chat=%s:[ender]:>", player.getUniqueId().toString().toLowerCase()), "[Enderchest]");
				}

				webhook.setContent(FormatMessage(player, message));
				webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); // Fallback image, should the player not have a valid UUID. Might not work anymore..
				if (!Utils.IsStringNullOrEmpty(player.getUniqueId().toString())) {
					webhook.setAvatarUrl("https://visage.surgeplay.com/bust/512/" + player.getUniqueId() + ".png"); // Get player UUID the normal way.
				} else {
					Log.debug(plugin, "Failed second attempt to get UUID of player" + player.getName() + ".");
					Log.debug(plugin, "Cannot set the bot's picture.");
				}

				try {
					//Log.Info(plugin, "Executing webhook.");
					webhook.execute();
				} catch (IOException e) {
					e.printStackTrace();
					ConnectionFailed();
				}
			}
		}).execute();
	}

	String FormatUsername(Player player, String message) {
		message = message.replace("%player%", player.getName());
		message = Lang.ParsePlaceholders(message, player);
		message = Lang.Parse(message);
		return encodeStringToUnicodeSequence(Lang.RemoveColorCodesAndFormatting(message));
	}

	public static String FormatMessage(Player player, String message) {
		message = Lang.ParsePlaceholders(message, player);
		message = Lang.Parse(message);
		return encodeStringToUnicodeSequence(Lang.RemoveColorCodesAndFormatting(message));
	}

	public static String FixName(String string) {
		char[] charArray = string.toCharArray();
		boolean foundSpace = true;
		for(int i = 0; i < charArray.length; i++) {
			charArray[i] = Character.toLowerCase(charArray[i]);
			if(Character.isLetter(charArray[i])) {
				if(foundSpace) {
					charArray[i] = Character.toUpperCase(charArray[i]);
					foundSpace = false;
				}
			}
			else {
				foundSpace = true;
			}
		}
		string = String.valueOf(charArray);
		return string;
	}

	public static String encodeStringToUnicodeSequence(String txt) {
		StringBuilder result = new StringBuilder();
		if (txt != null && !txt.isEmpty()) {
			for (int i = 0; i < txt.length(); i++) {
				result.append(convertCodePointToUnicodeString(Character.codePointAt(txt, i)));
				if (Character.isHighSurrogate(txt.charAt(i))) {
					i++;
				}
			}
		}
		return result.toString();
	}

	private final static String UNICODE_PREFIX = "\\u";
	private static String convertCodePointToUnicodeString(int codePoint) {
		StringBuilder result = new StringBuilder(UNICODE_PREFIX);
		String codePointHexStr = Integer.toHexString(codePoint);
		codePointHexStr = codePointHexStr.startsWith("0") ? codePointHexStr.substring(1) : codePointHexStr;
		if (codePointHexStr.length() <= 4) {
			result.append(getPrecedingZerosStr(codePointHexStr.length()));
		}
		result.append(codePointHexStr);
		return result.toString();
	}

	private static String getPrecedingZerosStr(int codePointStrLength) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < 4 - codePointStrLength; i++) {
			result.append("0");
		}
		return result.toString();
	}

	public boolean IsAchievementIgnored(String achievementTitle) {
		List<String> list = Main.config.getStringList("hide-achievements");
		for(String entry : list) {
			if(Utils.IsStringNullOrEmpty(entry) ||
				Utils.IsStringNullOrEmpty(achievementTitle)) return false;
			if(entry.contains(achievementTitle)) return true;
		}
		return false;
	}

	public static void ConnectionFailed() {
		strikes++;
		Log.warning(plugin, "Strike " + strikes + " of 5.");
		if(strikes < 5) return;

		for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage("§7[§9Discord§7]§r §cChat Disconnected!");
		}
		Log.error(plugin, "§cConnection failed.");
		Main.error = true;
		Log.error(plugin, "Attempting to reconnect soon.");
		Log.error(plugin, "Disabled regular connections to avoid further failed connections.");
		Log.error(plugin, "Please reload the plugin to manually re-enable");

		Main.bot.SendMessage(null, "Chat Disconnected");

	}
}