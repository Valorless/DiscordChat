package valorless.discordchatmonitor;

import valorless.discordchatmonitor.hooks.*;
import valorless.discordchatmonitor.uuid.UUIDFetcher;
import valorless.valorlessutils.ValorlessUtils.*;
import valorless.valorlessutils.config.Config;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatListener implements Listener { // Primary objective of BanListener is to listen for Ban commands.
	public static JavaPlugin plugin;
	String Name = "§7[§4DiscordChatMonitor§7]§r";
	public static Config config;
	
	public void onEnable() {
	}
	
	@EventHandler //(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) {
			return;
		}
		
		Placeholders ph = new Placeholders();
		ph.player = event.getPlayer();
		ph.message = event.getMessage();
		Lang.SetPlaceholders(ph);
		
		SendWebhook(event.getPlayer(), Lang.Get("message"));
    }
	
	@EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
		if(DiscordChatMonitor.enabled == false) {
			Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		if(!config.GetBool("death")) return;
		if(event.getDeathMessage() == null) return;
		
    	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));
    	webhook.setUsername(config.GetString("server-username"));
    	String message = event.getDeathMessage().replace(event.getEntity().getName(), "**" + event.getEntity().getName() + "**");
        //webhook.setContent(message);
        webhook.setContent(FormatMessage(event.getEntity(), message));
        //webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
        webhook.setAvatarUrl(config.GetString("server-icon-url"));
        
        try {
        	//Log.Info(plugin, "Executing webhook.");
			webhook.execute();
		} catch (IOException e) {
			e.printStackTrace();
			Log.Error(plugin, "§cConnection failed.");
        	DiscordChatMonitor.error = true;
        	Log.Error(plugin, "Plugin disabled to avoid further failed connections.");
        	Log.Error(plugin, "Please reload the plugin to re-enable");
		}
    }
	
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
		if(DiscordChatMonitor.enabled == false) {
			Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		if(!config.GetBool("join")) return;
		
		Placeholders ph = new Placeholders();
		ph.player = event.getPlayer();
		ph.playerCount = Bukkit.getOnlinePlayers().size();
		ph.playerCountMax = Bukkit.getMaxPlayers();
		Lang.SetPlaceholders(ph);
		
    	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));
    	
    	webhook.setUsername(config.GetString("server-username"));
    	if(config.GetBool("join-quit-player-count")) {
    		ph.message = Lang.Get("join");
    		Lang.SetPlaceholders(ph); //Re-set placeholders to use the join message. We don't want this otherwise.
    		webhook.setContent(FormatMessage(event.getPlayer(), Lang.Get("with-player-count")));
    	}else {
    		webhook.setContent(FormatMessage(event.getPlayer(), Lang.Get("join")));
    	}
        //webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
        webhook.setAvatarUrl(config.GetString("server-icon-url"));
        
        if(!event.getPlayer().hasPlayedBefore()) {
            webhook.setContent(FormatMessage(event.getPlayer(), Lang.Get("join-first-time")));
        }
        
        try {
        	//Log.Info(plugin, "Executing webhook.");
			webhook.execute();
		} catch (IOException e) {
			e.printStackTrace();
			Log.Error(plugin, "§cConnection failed.");
        	DiscordChatMonitor.error = true;
        	Log.Error(plugin, "Plugin disabled to avoid further failed connections.");
        	Log.Error(plugin, "Please reload the plugin to re-enable");
		}
    }
	
	@EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
		if(DiscordChatMonitor.enabled == false) {
			Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		if(!config.GetBool("quit")) return;

		Placeholders ph = new Placeholders();
		ph.player = event.getPlayer();
		ph.playerCount = Bukkit.getOnlinePlayers().size()-1;
		ph.playerCountMax = Bukkit.getMaxPlayers();
		Lang.SetPlaceholders(ph);
		
    	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));
    	
    	webhook.setUsername(config.GetString("server-username"));
    	if(config.GetBool("join-quit-player-count")) {
    		ph.message = Lang.Get("leave");
    		Lang.SetPlaceholders(ph); //Re-set placeholders to use the join message. We don't want this otherwise.
    		webhook.setContent(FormatMessage(event.getPlayer(), Lang.Get("with-player-count")));
    	}else {
    		webhook.setContent(FormatMessage(event.getPlayer(), Lang.Get("leave")));
    	}
        //webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); 
        webhook.setAvatarUrl(config.GetString("server-icon-url"));
        
        try {
        	//Log.Info(plugin, "Executing webhook.");
			webhook.execute();
		} catch (IOException e) {
			e.printStackTrace();
			Log.Error(plugin, "§cConnection failed.");
        	DiscordChatMonitor.error = true;
        	Log.Error(plugin, "Plugin disabled to avoid further failed connections.");
        	Log.Error(plugin, "Please reload the plugin to re-enable");
		}
    }
	
	public void SendWebhook(Player player, String message) {
		if(DiscordChatMonitor.enabled == false) {
			Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
    	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));

		Placeholders ph = new Placeholders();
		ph.player = player;
		Lang.SetPlaceholders(ph);
		
    	/*if (LuckPermsHook.instance != null) {
    	    LuckPerms api = LuckPermsHook.GetProvider();
    	    User user = api.getPlayerAdapter(Player.class).getUser(player);
    	    // String prefix = user.getCachedData().getMetaData();
    	    Set<String> groups = user.getNodes(NodeType.INHERITANCE).stream()
    	    	    .map(InheritanceNode::getGroupName)
    	    	    .collect(Collectors.toSet());
    	    List<String> grps = new ArrayList<String>();
    	    grps.addAll(groups);
    	    if(grps.get(0).equalsIgnoreCase("staff")) {
    	    	webhook.setUsername(config.GetString("staff-prefix")  + player.getName());
    	    }else if(grps.get(0).equalsIgnoreCase("default")) {
    	    	webhook.setUsername(config.GetString("default-prefix")  + player.getName());
    	    }else {
    	    	webhook.setUsername("[" + FixName(grps.get(0)) + "] "  + player.getName());
    	    }
    	}else {*/
    		webhook.setUsername(FormatUsername(player, config.GetString("player-username")));
    	//}
        webhook.setContent(FormatMessage(player, message));
        webhook.setAvatarUrl("https://minotar.net/armor/bust/" + player.getName() + "/100.png"); // Fallback image, should the player not have a valid UUID. Might not work anymore..
        if(!Utils.IsStringNullOrEmpty(player.getUniqueId().toString())) {
        	webhook.setAvatarUrl("https://visage.surgeplay.com/bust/512/" + player.getUniqueId().toString() + ".png"); // Get player UUID the normal way.
        }else {
        	Log.Debug(plugin, "Failed to get UUID of player " + player.getName() + ", attempting another way.");
        	
        	if(!Utils.IsStringNullOrEmpty(UUIDFetcher.getUUID(player.getName()).toString())) {
            	webhook.setAvatarUrl("https://visage.surgeplay.com/bust/512/" + UUIDFetcher.getUUID(player.getName()).toString() + ".png"); // Attempt to fetch player UUID from Mojang API.
        	}else {
            	Log.Debug(plugin, "Failed second attempt to get UUID of player" + player.getName() + ".");
            	Log.Debug(plugin, "Cannot set the bot's picture.");
        	}
       }
        
        try {
        	//Log.Info(plugin, "Executing webhook.");
			webhook.execute();
		} catch (IOException e) {
			e.printStackTrace();
			Log.Error(plugin, "§cConnection failed.");
        	DiscordChatMonitor.error = true;
        	Log.Error(plugin, "Plugin disabled to avoid further failed connections.");
        	Log.Error(plugin, "Please reload the plugin to re-enable");
		}
    }
	
	String FormatUsername(Player player, String message) {
		message = Lang.ParsePlaceholders(message, player);
		message = Lang.Parse(message);
		return encodeStringToUnicodeSequence(Lang.RemoveColorCodesAndFormatting(message));
	}
	
	String FormatMessage(Player player, String message) {
		message = Lang.ParsePlaceholders(message, player);
		message = Lang.Parse(message);
		return encodeStringToUnicodeSequence(Lang.RemoveColorCodesAndFormatting(message));
	}
	
	String FixName(String string) {
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
}