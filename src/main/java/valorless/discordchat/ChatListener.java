package valorless.discordchat;

import valorless.discordchat.discord.Bot;
import valorless.discordchat.hooks.EssentialsHook;
import valorless.discordchat.utils.InventoryImageGenerator;
import valorless.discordchat.utils.ItemStackToPng;
import valorless.discordchat.utils.MapToImage;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.config.Config;
import valorless.valorlessutils.utils.Utils;

import org.bukkit.plugin.java.JavaPlugin;

import net.ess3.api.IUser;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class ChatListener implements Listener { // Primary objective of BanListener is to listen for Ban commands.
	public static JavaPlugin plugin;
	String Name = "§7[§4Main§7]§r";
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
		
		if(EventPriority.valueOf(config.GetString("chat-event-priority")) == EventPriority.MONITOR) {
			ProccessMessage(event);
		}
    }
	
	@EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onAsyncPlayerChatEventHighest(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		if(EventPriority.valueOf(config.GetString("chat-event-priority")) == EventPriority.HIGHEST) {
			ProccessMessage(event);
		}
    }
	
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onAsyncPlayerChatEventHigh(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		if(EventPriority.valueOf(config.GetString("chat-event-priority")) == EventPriority.HIGH) {
			ProccessMessage(event);
		}
    }
	
	@EventHandler (priority = EventPriority.LOW, ignoreCancelled = false)
    public void onAsyncPlayerChatEventLow(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		if(EventPriority.valueOf(config.GetString("chat-event-priority")) == EventPriority.LOW) {
			ProccessMessage(event);
		}
    }
	
	@EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onAsyncPlayerChatEventLowest(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		if(EventPriority.valueOf(config.GetString("chat-event-priority")) == EventPriority.LOWEST) {
			ProccessMessage(event);
		}
    }
	
	@EventHandler (priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		if(EventPriority.valueOf(config.GetString("chat-event-priority")) == EventPriority.NORMAL) {
			ProccessMessage(event);
		}
    }
	
	void ProccessMessage(AsyncPlayerChatEvent event) {		
    	List<String> list = Main.filter.GetStringList("chat-filter");
    	String filtermsg = event.getMessage().toLowerCase();
    	
    	// Loop through each blocked word
    	for (String entry : list) {
    	    // Create a regex pattern for whole word matching
    	    String regex = "\\b" + Pattern.quote(entry.toLowerCase()) + "\\b";
    	    if (filtermsg.matches(".*" + regex + ".*")) {
    	        // If a match is found, block the message
    	        event.getPlayer().sendMessage(String.format(Main.filter.GetString("chat-filter-message"), entry));
    	        event.setCancelled(true);

    	        String msg = "§c[BLOCKED] " + event.getPlayer().getName() + ": " + event.getMessage();
    	        Log.Error(plugin, msg);

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
    		        event.getPlayer().sendMessage(String.format(Main.filter.GetString("chat-filter-message"), entry));
    		        event.setCancelled(true);

    		        String msg = "§c[BLOCKED] " + event.getPlayer().getName() + ": " + event.getMessage();
    		        Log.Error(plugin, msg);

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
				event.getPlayer().sendMessage(String.format(Main.filter.GetString("chat-filter-message"), entry));
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
				event.getPlayer().sendMessage(String.format(Main.filter.GetString("chat-filter-message"), entry));
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
	
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onAchievementGet(PlayerAdvancementDoneEvent event) {
		if(Main.enabled == false) {
			Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		if(!config.GetBool("achievement")) return;
		if(event.getPlayer().getWorld().getGameRuleValue(GameRule.ANNOUNCE_ADVANCEMENTS) == false) return;
		if(event.getAdvancement() == null) return;
		if(event.getAdvancement().getDisplay() == null) return;
		if(event.getAdvancement().getDisplay().getTitle() == null) return;
		if(event.getAdvancement().getDisplay().getDescription() == null) return;
		if(IsAchievementIgnored(event.getAdvancement().getDisplay().getTitle())) return;
		
		Player player = event.getPlayer();
		String title = "**%player%** has unlocked **%message%**."
				.replace("%player%", event.getPlayer().getName())
				.replace("%message%", event.getAdvancement().getDisplay().getTitle());
		
    	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));
    	webhook.setUsername(FormatUsername(player, config.GetString("player-username")));
        //webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
    	webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); // Fallback image, should the player not have a valid UUID. Might not work anymore..
        if(!Utils.IsStringNullOrEmpty(player.getUniqueId().toString())) {
        	webhook.setAvatarUrl("https://visage.surgeplay.com/bust/512/" + player.getUniqueId().toString() + ".png"); // Get player UUID the normal way.
        }else {
            	Log.Debug(plugin, "Failed second attempt to get UUID of player" + player.getName() + ".");
            	Log.Debug(plugin, "Cannot set the bot's picture.");
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
		
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
		//Log.Debug(plugin, event.getDeathMessage().toString());
		//Log.Debug(plugin, event.toString());
		if(Main.enabled == false) {
			Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		if(!config.GetBool("death")) return;
		if(event.getDeathMessage() == null) return;
		if(Utils.IsStringNullOrEmpty(event.getDeathMessage())) {
			event.setDeathMessage(String.format("%s died.", event.getEntity().getName()));
		}
		
		Player player = event.getEntity();
		
    	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));
    	webhook.setUsername(FormatUsername(player, config.GetString("player-username").replace("%player%", player.getName())));
    	String message = event.getDeathMessage().replace(event.getEntity().getName(), "**" + event.getEntity().getName() + "**");
        //webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
        webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); // Fallback image, should the player not have a valid UUID. Might not work anymore..
        if(!Utils.IsStringNullOrEmpty(player.getUniqueId().toString())) {
        	webhook.setAvatarUrl("https://visage.surgeplay.com/bust/512/" + player.getUniqueId().toString() + ".png"); // Get player UUID the normal way.
        }else {
            	Log.Debug(plugin, "Failed second attempt to get UUID of player" + player.getName() + ".");
            	Log.Debug(plugin, "Cannot set the bot's picture.");
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
    }
	
	@EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
		
		if(!Utils.IsStringNullOrEmpty(Main.config.GetString("custom-join"))) {
			String join = Main.config.GetString("custom-join");
			join = join.replace("%username%", event.getPlayer().getName());
			if(Utils.IsStringNullOrEmpty(event.getJoinMessage())) {
				for(Player player : Bukkit.getOnlinePlayers()) {
					player.sendMessage(Lang.Parse(Lang.ParsePlaceholders(join, event.getPlayer())));
				}
			}
			else {
				event.setJoinMessage(join);
			}
		}
		
		if(Main.enabled == false) {
			Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		if(!config.GetBool("join")) return;
		
    	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));
    	
    	webhook.setUsername(config.GetString("server-username"));
        //webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
        webhook.setAvatarUrl(config.GetString("server-icon-url"));
        
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
    }	
	@SuppressWarnings("deprecation")
	@EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run(){
            	if(!Utils.IsStringNullOrEmpty(Main.config.GetString("custom-leave")) && !kick) {            	    
        			String leave = Main.config.GetString("custom-leave");
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
			Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		if(!config.GetBool("quit")) return;
		if(EssentialsHook.isHooked()) {
    		IUser pl = EssentialsHook.getInstance().getUser(event.getPlayer());
    		if(pl.isVanished()) return;
    	}
		
    	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));
    	
    	webhook.setUsername(config.GetString("server-username"));
    	String msg = Lang.Get("leave")
    				.replace("%player%", event.getPlayer().getName());
    	webhook.setContent(FormatMessage(event.getPlayer(), msg));
        //webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
        webhook.setAvatarUrl(config.GetString("server-icon-url"));
        
        try {
        	//Log.Info(plugin, "Executing webhook.");
			webhook.execute();
		} catch (IOException e) {
			e.printStackTrace();
			ConnectionFailed();
		}
    }
	
	//@EventHandler //(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {
		kick = true;
		if(!Utils.IsStringNullOrEmpty(Main.config.GetString("custom-leave"))) {
			Log.Info(plugin, "Kick event");
			String leave = Main.config.GetString("custom-leave");
		    String reason = event.getReason();
			String pl = event.getPlayer().getName();
			leave = leave.replace("%username%", pl);
			
			if(reason != null) {
				String sect = "custom-leave-causes";
				for(Object entry : Main.config.GetConfigurationSection(sect).getKeys(false)) {
					if(reason.contains(Main.config.GetString(String.format("%s.%s.keyword", sect, entry.toString())))) {
						leave = leave.replace("%cause%", Main.config.GetString(String.format("%s.%s.value", sect, entry.toString())));
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
				for(Object entry : Main.config.GetStringList("sect")) {
					if(reason.contains(Main.config.GetString(String.format("%s.%s.keyword", sect, entry.toString())))) {
						leave = leave.replace("%cause%", Main.config.GetString(String.format("%s.%s.value", sect, entry.toString())));
					}
				}
			}
			
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(Lang.Parse(Lang.ParsePlaceholders(leave, event.getPlayer())));
			}
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){

	            @Override
	            public void run(){
	                kick = false;
	            }
	        }, 3L);
		}
	}
	
	public void SendWebhook(Player player, String msg, Object... args) {	
		if(Main.enabled == false) {
			Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		
		/*Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){

		    @Override
		    public void run() {*/
		Bot.newChain().async(() -> {
		    	final String _message = msg;
		    	String message = new String(_message.replace(String.format("<chat=%s:[i]:>", player.getUniqueId().toString()), "[i]")
		    			.replace(String.format("<chat=%s:[item]:>", player.getUniqueId().toString()), "[item]")
		    			.replace(String.format("<chat=%s:[inv]:>", player.getUniqueId().toString()), "[inv]")
		    			.replace(String.format("<chat=%s:[ender]:>", player.getUniqueId().toString()), "[ender]")
		    			.replace(String.format("<chat=%s:[ping]:>", player.getUniqueId().toString()), "[ping]")
		    			.replace(String.format("<chat=%s:[pos]:>", player.getUniqueId().toString()), "[pos]").toCharArray());
				//Log.Warning(plugin, message);
		    	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));
		    	webhook.setUsername(
		    			FormatUsername(player, config.GetString("player-username").replace("%player%", player.getName()))
		    			);
		    	if(Bukkit.getPluginManager().getPlugin("InteractiveChat") != null) {
		    		try {
		    			//Log.Info(plugin, message);
		    			ItemStack item = (ItemStack)args[0];
		    			if(message.contains("[item]") && args.length != 0 && item.hasItemMeta() || message.contains("[i]") && args.length != 0 && item.hasItemMeta()) {
		    				//Log.Warning(plugin, message);
		    				String id = "";
		    				if(item.getType() == Material.FILLED_MAP) {
		    					id = MapToImage.getMapAsImage(item);
		    					if(id == null) {
		    						id = ItemStackToPng.createItemStackImage(item);
		    					}
		    				}else {
		    					id = ItemStackToPng.createItemStackImage(item);
		    				}
		    				try {
		    					webhook.addEmbed(new DiscordWebhook.EmbedObject()
		    							.setTitle(FormatMessage(player, "Click image to view better."))
		    							.setImage(url + id + ".png")
		    							);
		    					Log.Debug(Main.plugin, url + id + ".png");
		    				}catch(Exception e) {}
		    			}
		    			if(item.hasItemMeta()) {
		    				if(item.getItemMeta().hasDisplayName()) {
		    					message = message.replace("[item]", "[" + item.getItemMeta().getDisplayName() + "]");
		    					message = message.replace("[i]", "[" + item.getItemMeta().getDisplayName() + "]");
		    					//message = message.replace("<%s:[item]:>", "[" + item.getItemMeta().getDisplayName() + "]");
		    					//message = message.replace("<%s:[i]:>", "[" + item.getItemMeta().getDisplayName() + "]");
		    				}else {
		    					message = message.replace("[item]", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
		    					message = message.replace("[i]", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
		    					//message = message.replace("<%s:[item]:>", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
		    					//message = message.replace("<%s:[i]:>", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
		    				}
		    			}else {
		    				message = message.replace("[item]", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
		    				message = message.replace("[i]", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
	    					//message = message.replace("<%s:[item]:>", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
	    					//message = message.replace("<%s:[i]:>", "[" + FixName(item.getType().toString().replace("_", " ") + "]"));
		    			}
		    		}catch(Exception E){
		    			E.printStackTrace();
		    		}
		    	}
		    	
		    	if(Bukkit.getPluginManager().getPlugin("InteractiveChat") != null) {
		    		if(message.contains("[ping]")) {
		    			//Log.Warning(plugin, message);
		    			message = message.replace("[ping]", player.getPing() + "ms");
		    			//message = message.replace(String.format("<%s:[ping]:>", player.getUniqueId().toString().toLowerCase()), player.getPing() + "ms");
		    		}

		    		if(message.contains("[pos]")) {
		    			//Log.Warning(plugin, message);
		    			Location loc = player.getLocation();
		    			message = message.replace("[pos]", String.format("%s: %s, %s, %s", player.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ()));
		    			//message = message.replace(String.format("<%s:[pos]:>", player.getUniqueId().toString().toLowerCase()), String.format("%s, %s, %s", loc.getX(), loc.getY(), loc.getZ()));
		    		}

		    		if(message.contains("[inv]")) {
		    			//Log.Warning(plugin, message);
		    			String id = InventoryImageGenerator.generate(player.getInventory().getContents(), 5, true);
		    			if(id != null) {
		    				try {
		    					webhook.addEmbed(new DiscordWebhook.EmbedObject()
		    							.setTitle(FormatMessage(player, String.format("%s's Inventory", player.getName())))
		    							.setImage(url + id + ".png")
		    							);
		    					Log.Debug(Main.plugin, url + id + ".png");
		    				}catch(Exception e) {}
		    			}
		    			message = message.replace("[inv]", "[Inventory]");
		    			//message = message.replace(String.format("<%s:[inv]:>", player.getUniqueId().toString().toLowerCase()), "[Enderchest]");
		    		}

		    		if(message.contains("[ender]")) {
		    			String id = InventoryImageGenerator.generate(player.getEnderChest().getContents(), 3, false);
		    			if(id != null) {
		    				try {
		    					webhook.addEmbed(new DiscordWebhook.EmbedObject()
		    							.setTitle(FormatMessage(player, String.format("%s's Enderchest", player.getName())))
		    							.setImage(url + id + ".png")
		    							);
		    					Log.Debug(Main.plugin, url + id + ".png");
		    				}catch(Exception e) {}
		    			}
		    			message = message.replace("[ender]", "[Enderchest]");
		    			//message = message.replace(String.format("<chat=%s:[ender]:>", player.getUniqueId().toString().toLowerCase()), "[Enderchest]");
		    		}
		    	}

		    	webhook.setContent(FormatMessage(player, message));
		    	webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); // Fallback image, should the player not have a valid UUID. Might not work anymore..
		    	if(!Utils.IsStringNullOrEmpty(player.getUniqueId().toString())) {
		    		webhook.setAvatarUrl("https://visage.surgeplay.com/bust/512/" + player.getUniqueId().toString() + ".png"); // Get player UUID the normal way.
		    	}else {
		    			Log.Debug(plugin, "Failed second attempt to get UUID of player" + player.getName() + ".");
		    			Log.Debug(plugin, "Cannot set the bot's picture.");
		    		}
		    	
		    	try {
		        	//Log.Info(plugin, "Executing webhook.");
					webhook.execute();
				} catch (IOException e) {
					e.printStackTrace();
					ConnectionFailed();
				}
		}).execute();
		
		           
		//});
		
    	
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
	
	public void UpdatePlaceholders(Player player) {
		
	}
	
	public boolean IsAchievementIgnored(String achievementTitle) {
		List<String> list = Main.config.GetStringList("hide-achievements");
		for(String entry : list) {
			if(Utils.IsStringNullOrEmpty(entry)) return false;
			if(Utils.IsStringNullOrEmpty(achievementTitle)) return false;
			if(entry.contains(achievementTitle)) return true;
		}
		return false;
	}
	
	public static void ConnectionFailed() {
		strikes++;
		Log.Warning(plugin, "Strike " + strikes + " of 5.");
		if(strikes < 5) return;
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage("§7[§9Discord§7]§r §cChat Disconnected!");
		}
		Log.Error(plugin, "§cConnection failed.");
    	Main.error = true;
        Log.Error(plugin, "Attempting to reconnect soon.");
        Log.Error(plugin, "Plugin disabled regular connections to avoid further failed connections.");
        Log.Error(plugin, "Please reload the plugin to manually re-enable");
        
        Main.bot.SendMessage(null, "Chat Disconnected");
		
	}
}