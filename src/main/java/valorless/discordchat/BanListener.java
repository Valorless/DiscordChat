package valorless.discordchat;

import valorless.discordchat.utils.DurationFormatter;
import valorless.valorlessutils.ValorlessUtils.*;

import java.awt.Color;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.bukkit.event.RevokePunishmentEvent;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;

public class BanListener implements Listener { // Primary objective of BanListener is to listen for Ban commands.
	public enum BanType { ban, unban, tempban, ipban, ipunban }

	static String Name = "§7[§9Discord§7]§r ";
	
	/*
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String[] args = event.getMessage().split("\\s+");
		//Player sender = event.getPlayer();
		CommandSender sender = event.getPlayer();
		ProcessCommand(args, sender, false);
	}

	
	@EventHandler
	public void onServerCommand(ServerCommandEvent event) {
		String[] args = event.getCommand().split("\\s+");
		CommandSender console = event.getSender();
		args[0] = "/" + args[0];
		ProcessCommand(args, console, true);
	}

	public static void DiscordCommand(CustomConsoleSender sender, String message) {
		String[] args = message.split("\\s+");
		args[0] = "/" + args[0];
		ProcessCommand(args, sender, false);
	}*/
	
	@EventHandler
	public void onBanEvent(PunishmentEvent event) {
		Punishment punishment = event.getPunishment();
		if(punishment != null) {
			Date now = new Date();
			String target = punishment.getName();
			String sender = punishment.getOperator();
			String reason = punishment.getReason();
			String duration = DurationFormatter.formatDurationBetween(punishment.getStart(), punishment.getEnd());
			//String duration = FormatDuration(punishment.getDuration(true));
			if(punishment.getType() == PunishmentType.BAN && Main.bans.GetBool("bans")) {
				SendWebhook(BanType.ban, target, sender, reason, now, "");
			}
			if(punishment.getType() == PunishmentType.TEMP_BAN && Main.bans.GetBool("tempbans") ) {
				SendWebhook(BanType.tempban, target, sender, reason, now, duration);
			}
			if(punishment.getType() == PunishmentType.IP_BAN && Main.bans.GetBool("banips")) {
				SendWebhook(BanType.ipban, target, sender, reason, now, "");
			}
		}
	}
	
	@EventHandler
	public void onUnbanEvent(RevokePunishmentEvent event) {
		Punishment punishment = event.getPunishment();
		if(punishment != null) {
			Date now = new Date();
			String target = punishment.getName();
			String sender = punishment.getOperator();
			String reason = punishment.getReason();
			if(punishment.getType() == PunishmentType.BAN && Main.bans.GetBool("unbans")) {
				SendWebhook(BanType.unban, target, sender, reason, now, "");
			}
			if(punishment.getType() == PunishmentType.IP_BAN && Main.bans.GetBool("unbanips")) {
				SendWebhook(BanType.ipunban, target, sender, reason, now, "");
			}
		}
	}

	public static void ProcessCommand(String[] args, CommandSender sender, Boolean console) {
		if(args[0].equalsIgnoreCase("/ban") && args.length >= 2 && Main.bans.GetBool("bans") == true) {
			if(sender.hasPermission("minecraft.command.ban") || sender.hasPermission("essentials.ban")) {
				Date now = new Date();
				String target = args[1];
				if (target != "") {
					if(args.length >= 3) {
						String reason = "";
						for(int i = 2; i < args.length; i++) { reason = reason + " " + args[i]; }
						if(console) {
							SendWebhook(BanType.ban, target, "Console", reason, now, "");
						} else {
							SendWebhook(BanType.ban, target, sender.getName(), reason, now, "");
						}
					}
					else {
						if(console) { 
							SendWebhook(BanType.ban, target, "Console", "No reason given.", now, "");
						} else {
							SendWebhook(BanType.ban, target, sender.getName(), "No reason given.", now, "");
						}
					}
				}
			}
		}
		if(args[0].equalsIgnoreCase("/tempban") && args.length >= 3 && Main.bans.GetBool("tempbans") == true) {
			if(sender.hasPermission("essentials.tempban")) {
				Date now = new Date();
				String target = args[1];
				if (target != "") {
					if(args.length >= 4) {
						String reason = "";
						for(int i = 3; i < args.length; i++) { reason = reason + " " + args[i]; }
						if(console) { 
							SendWebhook(BanType.tempban, target, "Console", reason, now, args[2]);
						} else {
							SendWebhook(BanType.tempban, target, sender.getName(), reason, now, args[2]);
						}
					}
					else {
						if(console) { 
							SendWebhook(BanType.tempban, target, "Console", "No reason given.", now, args[2]);
						} else {
							SendWebhook(BanType.tempban, target, sender.getName(), "No reason given.", now, args[2]);
						}
					}
				}

			}
		}
		if(args[0].equalsIgnoreCase("/unban") && args.length >= 2 && Main.bans.GetBool("unbans") == true || 
				args[0].equalsIgnoreCase("/pardon") && args.length >= 2 && Main.bans.GetBool("unbans") == true) {
			if(sender.hasPermission("minecraft.command.pardon") || sender.hasPermission("essentials.unban")) {
				Date now = new Date();
				String target = args[1];
				if (target != "") {
					if(console) { 
						SendWebhook(BanType.unban, target, "Console", "", now, "");
					} else {
						SendWebhook(BanType.unban, target, sender.getName(), "", now, "");
					}
				}
			}
		}
		if(args[0].equalsIgnoreCase("/banip") && args.length >= 2 && Main.bans.GetBool("banips") == true) {
			if(sender.hasPermission("essentials.banip")) {
				Date now = new Date();
				String target = args[1];
				if (target != "") {
					if(args.length >= 3) {
						String reason = "";
						for(int i = 2; i < args.length; i++) { reason = reason + " " + args[i]; }
						if(console) { 
							SendWebhook(BanType.ipban, target, "Console", reason, now, "");
						} else {
							SendWebhook(BanType.ipban, target, sender.getName(), reason, now, "");
						}
					}
					else {
						if(console) { 
							SendWebhook(BanType.ipban, target, "Console", "No reason given.", now, "");
						} else {
							SendWebhook(BanType.ipban, target, sender.getName(), "No reason given.", now, "");
						}
					}
				}
			}
		}
		if(args[0].equalsIgnoreCase("/unbanip") && args.length >= 2 && Main.bans.GetBool("unbanips") == true ||
				args[0].equalsIgnoreCase("/pardon-ip") && args.length >= 2 && Main.bans.GetBool("unbanips") == true) {
			if(sender.hasPermission("minecraft.command.pardon-ip") || sender.hasPermission("essentials.unbanip")) {
				Date now = new Date();
				String target = args[1];
				if (target != "") {
					if(console) { 
						SendWebhook(BanType.ipunban, target, "Console", "", now, "");
					} else {
						SendWebhook(BanType.ipunban, target, sender.getName(), "", now, "");
					}
				}
			}
		}
	}

	public static String parsePlaceholders(String message, HashMap<String, String> placeholders) {
		for(String key : placeholders.keySet()) {
			message = message.replace(key, placeholders.get(key));
		}
		return message;
	}

	public static void SendWebhook(BanType type, String target, String sender, String reason, Date date, String duration) {
		Log.Info(Main.plugin, "Attempting to send ban to discord!");
		Log.Info(Main.plugin, "Type: " + type.name());
		Log.Info(Main.plugin, "Target: " + target);
		Log.Info(Main.plugin, "Sender: " + sender);
		Log.Info(Main.plugin, "Reason: " + reason);
		Log.Info(Main.plugin, "Date: " + date.toString());
		Log.Info(Main.plugin, "Duration: " + duration);
		
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> {
			HashMap<String, String> ph = new HashMap<String, String>();
			ph.put("%target%", (Bukkit.getPlayer(target) != null) ? Bukkit.getPlayer(target).getName() : target);
			ph.put("%sender%", sender);
			ph.put("%reason%", reason);
			ph.put("%duration%", duration);
			ph.put("%date%", date.toString());
			ph.put("%plugin%", Name);

			DiscordWebhook webhook = new DiscordWebhook(Main.bans.GetString("webhook-url"));
			webhook.setContent(parsePlaceholders(Main.bans.GetString("bot-message"), ph));
			webhook.setAvatarUrl(Main.bans.GetString("bot-picture"));
			webhook.setUsername(Main.bans.GetString("bot-name"));
			webhook.setTts(false);
			if(type == BanType.ban) {
				webhook.addEmbed(new DiscordWebhook.EmbedObject()
						.setTitle(parsePlaceholders(Main.bans.GetString("banned-title"), ph))
						.setDescription(parsePlaceholders(Main.bans.GetString("description"), ph))
						.setColor(Color.decode(Main.bans.GetString("ban-color")))
						.addField(parsePlaceholders(Main.bans.GetString("reason-line1"), ph), parsePlaceholders(Main.bans.GetString("reason-line2"), ph), false)
						.addField(parsePlaceholders(Main.bans.GetString("banned-by-line1"), ph), parsePlaceholders(Main.bans.GetString("banned-by-line2"), ph), false)
						.setThumbnail("https://minotar.net/armor/bust/" + target + "/100.png")
						.setFooter(parsePlaceholders(Main.bans.GetString("banned-on"), ph), "")
						.setUrl("https://mcnames.net/username/" + target)
						);
			}
			if(type == BanType.tempban)
			{
				webhook.addEmbed(new DiscordWebhook.EmbedObject()
						.setTitle(parsePlaceholders(Main.bans.GetString("tempbanned-title"), ph))
						.setDescription(parsePlaceholders(Main.bans.GetString("description"), ph))
						.setColor(Color.decode(Main.bans.GetString("tempban-color")))
						.addField(parsePlaceholders(Main.bans.GetString("reason-line1"), ph), parsePlaceholders(Main.bans.GetString("reason-line2"), ph), false)
						.addField(parsePlaceholders(Main.bans.GetString("banned-by-line1"), ph), parsePlaceholders(Main.bans.GetString("banned-by-line2"), ph), false)
						.addField(parsePlaceholders(Main.bans.GetString("duration-line1"), ph), parsePlaceholders(Main.bans.GetString("duration-line2"), ph), false)
						.setThumbnail("https://minotar.net/armor/bust/" + target + "/100.png")
						.setFooter(parsePlaceholders(Main.bans.GetString("banned-on"), ph), "")
						.setUrl("https://mcnames.net/username/" + target)
						);
			}
			if(type == BanType.unban)
			{
				webhook.addEmbed(new DiscordWebhook.EmbedObject()
						.setTitle(parsePlaceholders(Main.bans.GetString("unbanned-title"), ph))
						.setDescription(parsePlaceholders(Main.bans.GetString("description"), ph))
						.setColor(Color.decode(Main.bans.GetString("unban-color")))
						.addField(parsePlaceholders(Main.bans.GetString("unbanned-by-line1"), ph), parsePlaceholders(Main.bans.GetString("unbanned-by-line2"), ph), false)
						.setThumbnail("https://minotar.net/armor/bust/" + target + "/100.png")
						.setFooter(parsePlaceholders(Main.bans.GetString("unbanned-on"), ph), "")
						.setUrl("https://mcnames.net/username/" + target)
						);
			}
			if(type == BanType.ipban) {
				webhook.addEmbed(new DiscordWebhook.EmbedObject()
						.setTitle(parsePlaceholders(Main.bans.GetString("ip-banned-title"), ph))
						.setDescription(parsePlaceholders(Main.bans.GetString("description"), ph))
						.setColor(Color.decode(Main.bans.GetString("banip-color")))
						.addField(parsePlaceholders(Main.bans.GetString("reason-line1"), ph), parsePlaceholders(Main.bans.GetString("reason-line2"), ph), false)
						.addField(parsePlaceholders(Main.bans.GetString("banned-by-line1"), ph), parsePlaceholders(Main.bans.GetString("banned-by-line2"), ph), false)
						.setFooter(parsePlaceholders(Main.bans.GetString("banned-on"), ph), "")
						);
			}
			if(type == BanType.ipunban)
			{
				webhook.addEmbed(new DiscordWebhook.EmbedObject()
						.setTitle(parsePlaceholders(Main.bans.GetString("ip-unbanned-title"), ph))
						.setDescription(parsePlaceholders(Main.bans.GetString("description"), ph))
						.setColor(Color.decode(Main.bans.GetString("unbanip-color")))
						.addField(parsePlaceholders(Main.bans.GetString("unbanned-by-line1"), ph), parsePlaceholders(Main.bans.GetString("unbanned-by-line2"), ph), false)
						.setFooter(parsePlaceholders(Main.bans.GetString("unbanned-on"), ph), "")
						);
			}
			try {
				Log.Info(Main.plugin, "Executing webhook.");
				webhook.execute();
			} catch (IOException e) {
				e.printStackTrace();
				Log.Error(Main.plugin, "&cConnection failed.");
			}
		});
	}

	public static String FormatMessage(String message) {
		message = Lang.Parse(message);
		return encodeStringToUnicodeSequence(message);
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

	//Legacy method for formatting durations, may be used in the future if DurationFormatter does not work as intended.
	static String FormatDuration(String duration) {
		if(duration.contains("mo")) return duration.replace("mo", " Months");
		if(duration.contains("s")) return duration.replace("s", " Seconds");
		if(duration.contains("m")) return duration.replace("m", " Minutes");
		if(duration.contains("h")) return duration.replace("h", " Hours");
		if(duration.contains("d")) return duration.replace("d", " Days");
		if(duration.contains("w")) return duration.replace("w", " Weeks");
		if(duration.contains("y")) return duration.replace("y", " Years");
		return duration;
	}
}