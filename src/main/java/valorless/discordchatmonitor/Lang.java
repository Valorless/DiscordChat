package valorless.discordchatmonitor;

import valorless.valorlessutils.config.Config;

import java.util.Date;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import valorless.discordchatmonitor.hooks.PlaceholderAPIHook;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.ValorlessUtils.Utils;

public class Lang {
	
	public static Config lang;
	public static Placeholders placeholders;
	
	public static void SetPlaceholders(Placeholders p) {
		placeholders = p;
	}
	
	public static String Parse(String text) {
		if(!Utils.IsStringNullOrEmpty(text)) {
			if(placeholders != null) {
				text = text.replace("%plugin%", placeholders.plugin);
				text = text.replace("%message%", placeholders.message);
				text = text.replace("%player%", placeholders.player.getName());
				text = text.replace("%player-count%", String.valueOf(placeholders.playerCount));
				text = text.replace("%player-count-max%", String.valueOf(placeholders.playerCountMax));
			}
			text = text.replace("&", "§");
			text = text.replace("\\n", "\n");
			
			Date now = new Date();
			text = text.replace("%timestamp%", String.format("[<t:%s:T>]", now.getTime() / 1000));
		}
		return text;
	}

	public static String Get(String key) {
		if(lang.Get(key) == null) {
			Log.Error(DiscordChatMonitor.plugin, String.format("Lang.yml is missing the key '%s'!", key));
			return "§4error";
		}
		return Parse(lang.GetString(key));
	}
	
	/* // Reverting back to the old Placeholder system.
	public static String Get(String key, Object arg) {
		if(lang.Get(key) == null) {
			Log.Error(DiscordChatMonitor.plugin, String.format("Lang.yml is missing the key '%s'!", key));
			return "§4error";
		}
		return Parse(String.format(lang.GetString(key), arg.toString()));
	}
	
	public static String Get(String key, Object arg1, Object arg2) {
		if(lang.Get(key) == null) {
			Log.Error(DiscordChatMonitor.plugin, String.format("Lang.yml is missing the key '%s'!", key));
			return "§4error";
		}
		return Parse(String.format(lang.GetString(key), arg1.toString(), arg2.toString()));
	}
	
	public static String Get(String key, Object arg1, Object arg2, Object arg3) {
		if(lang.Get(key) == null) {
			Log.Error(DiscordChatMonitor.plugin, String.format("Lang.yml is missing the key '%s'!", key));
			return "§4error";
		}
		return Parse(String.format(lang.GetString(key), arg1.toString(), arg2.toString(), arg3.toString()));
	}
	*/
	
	public static String RemoveColorCodesAndFormatting(String text) {
		if(!Utils.IsStringNullOrEmpty(text)) {
			text = text.replace("§1", "");
			text = text.replace("§2", "");
			text = text.replace("§3", "");
			text = text.replace("§4", "");
			text = text.replace("§5", "");
			text = text.replace("§6", "");
			text = text.replace("§7", "");
			text = text.replace("§8", "");
			text = text.replace("§9", "");
			text = text.replace("§0", "");
			text = text.replace("§a", "");
			text = text.replace("§b", "");
			text = text.replace("§c", "");
			text = text.replace("§d", "");
			text = text.replace("§e", "");
			text = text.replace("§f", "");
			text = text.replace("§o", "");
			text = text.replace("§l", "");
			text = text.replace("§k", "");
			text = text.replace("§m", "");
			text = text.replace("§n", "");
			text = text.replace("§r", "");
			text = text.replace("§x", "");
		}
		return text;
	}
	
	public static String ParsePlaceholders(String text, Player player) {
		if(PlaceholderAPIHook.isHooked()) {
			return PlaceholderAPI.setPlaceholders(player, text);
		}else {
			return text;
		}
	}
}
