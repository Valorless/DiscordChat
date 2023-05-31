package valorless.discordchatmonitor;

import valorless.valorlessutils.config.Config;
import valorless.valorlessutils.ValorlessUtils.Log;
import valorless.valorlessutils.ValorlessUtils.Utils;

public class Lang {
	
	public static Config lang;
		
	public static class Placeholders{
		public static String plugin = "§7[§aHaven§bBags§7]§r";
	}
	
	public static String Parse(String text) {
		if(!Utils.IsStringNullOrEmpty(text)) {
			text = text.replace("&", "§");
			text = text.replace("\\n", "\n");
			if(text.contains("%plugin%")) { text = text.replace("%plugin%", Placeholders.plugin); }
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
}