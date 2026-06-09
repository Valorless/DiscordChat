package valorless.discordchat;

import valorless.discordchat.utils.DurationFormatter;

import java.awt.Color;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.bukkit.event.RevokePunishmentEvent;
import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import valorless.valorlessutils.logging.Log;

/**
 * Listens to AdvancedBan punishment events and forwards formatted notifications to Discord.
 */
public class BanListener implements Listener { // Primary objective of BanListener is to listen for Ban commands.
	/**
	 * Supported punishment notification types used to choose webhook embed templates.
	 */
	public enum BanType { ban, unban, tempban, ipban, ipunban }
	
	/**
	 * Handle newly issued punishments and dispatch matching Discord webhook messages.
	 *
	 * @param event AdvancedBan punishment event
	 */
	@EventHandler
	public void onBanEvent(PunishmentEvent event) {
		Punishment punishment = event.getPunishment();
		if(punishment != null) {
			Log.error(Main.plugin, String.format("Admin '%s' has issued a %s to player '%s' for reason: '%s'",
					punishment.getOperator(),
					punishment.getType().name(),
					punishment.getName(),
					punishment.getReason()));
			Date now = new Date();
			String target = punishment.getName();
			String sender = punishment.getOperator();
			String reason = punishment.getReason();
			if(reason == null || reason.isEmpty()) reason = "No reason given.";
			if(reason.equalsIgnoreCase("@BBBAuto")) return; // Don't send auto bans to discord, as they can be very spammy and often have no reason.
			String duration = DurationFormatter.formatDurationBetween(punishment.getStart(), punishment.getEnd());
			//String duration = FormatDuration(punishment.getDuration(true));
			if(punishment.getType() == PunishmentType.BAN && Main.bans.getBool("bans")) {
				SendWebhook(BanType.ban, target, sender, reason, now, "Forever");
			}
			if(punishment.getType() == PunishmentType.TEMP_BAN && Main.bans.getBool("tempbans") ) {
				SendWebhook(BanType.tempban, target, sender, reason, now, duration);
			}
			if(punishment.getType() == PunishmentType.IP_BAN && Main.bans.getBool("banips")) {
				SendWebhook(BanType.ipban, target, sender, reason, now, "Forever");
			}
		}
	}
	
	/**
	 * Handle revoked punishments and dispatch matching Discord webhook messages.
	 *
	 * @param event AdvancedBan revoke punishment event
	 */
	@EventHandler
	public void onUnbanEvent(RevokePunishmentEvent event) {
		Punishment punishment = event.getPunishment();
		if(punishment != null) {
			Log.error(Main.plugin, String.format("Admin '%s' has issued a unban to player '%s'.",
					punishment.getOperator(),
					punishment.getName()));
			Date now = new Date();
			String target = punishment.getName();
			String sender = punishment.getOperator();
			String reason = punishment.getReason();
			if(punishment.getType() == PunishmentType.BAN && Main.bans.getBool("unbans")) {
				SendWebhook(BanType.unban, target, sender, reason, now, "");
			}
			if(punishment.getType() == PunishmentType.IP_BAN && Main.bans.getBool("unbanips")) {
				SendWebhook(BanType.ipunban, target, sender, reason, now, "");
			}
		}
	}

	/**
	 * Replace placeholder keys in a message template with resolved values.
	 *
	 * @param message message template
	 * @param placeholders map of placeholder key to value
	 * @return message with placeholders replaced
	 */
	public static String parsePlaceholders(String message, HashMap<String, String> placeholders) {
		for(String key : placeholders.keySet()) {
			message = message.replace(key, placeholders.get(key));
		}
		return message;
	}

	/**
	 * Build and send a ban-related Discord webhook asynchronously.
	 *
	 * @param type ban notification type
	 * @param target punished player name or identifier
	 * @param sender actor that applied or revoked the punishment
	 * @param reason punishment reason
	 * @param date timestamp associated with the event
	 * @param duration formatted duration for temporary punishments
	 */
	public static void SendWebhook(BanType type, String target, String sender, String reason, Date date, String duration) {
		Log.info(Main.plugin, "Attempting to send ban to discord!");
		Log.info(Main.plugin, "Type: " + type.name());
		Log.info(Main.plugin, "Target: " + target);
		Log.info(Main.plugin, "Sender: " + sender);
		Log.info(Main.plugin, "Reason: " + reason);
		Log.info(Main.plugin, "Date: " + date.toString());
		Log.info(Main.plugin, "Duration: " + duration);
		
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> {
			HashMap<String, String> ph = new HashMap<String, String>();
			ph.put("%target%", (Bukkit.getPlayer(target) != null) ? Bukkit.getPlayer(target).getName() : target);
			ph.put("%sender%", sender);
			ph.put("%reason%", reason);
			ph.put("%duration%", duration);
			ph.put("%date%", date.toString());
			ph.put("%plugin%", Lang.Parse(Lang.Get("prefix")));

			DiscordWebhook webhook = new DiscordWebhook(Main.bans.getString("webhook-url"));
			webhook.setContent(parsePlaceholders(Main.bans.getString("bot-message"), ph));
			webhook.setAvatarUrl(Main.bans.getString("bot-picture"));
			webhook.setUsername(Main.bans.getString("bot-name"));
			webhook.setTts(false);
			if(type == BanType.ban) {
				webhook.addEmbed(new DiscordWebhook.EmbedObject()
						.setTitle(parsePlaceholders(Main.bans.getString("banned-title"), ph))
						.setDescription(parsePlaceholders(Main.bans.getString("description"), ph))
						.setColor(Color.decode(Main.bans.getString("ban-color")))
						.addField(parsePlaceholders(Main.bans.getString("reason-line1"), ph), parsePlaceholders(Main.bans.getString("reason-line2"), ph), false)
						.addField(parsePlaceholders(Main.bans.getString("banned-by-line1"), ph), parsePlaceholders(Main.bans.getString("banned-by-line2"), ph), false)
						.setThumbnail("https://minotar.net/armor/bust/" + target + "/100.png")
						.setFooter(parsePlaceholders(Main.bans.getString("banned-on"), ph), "")
						.setUrl("https://mcnames.net/username/" + target)
						);
			}
			if(type == BanType.tempban)
			{
				webhook.addEmbed(new DiscordWebhook.EmbedObject()
						.setTitle(parsePlaceholders(Main.bans.getString("tempbanned-title"), ph))
						.setDescription(parsePlaceholders(Main.bans.getString("description"), ph))
						.setColor(Color.decode(Main.bans.getString("tempban-color")))
						.addField(parsePlaceholders(Main.bans.getString("reason-line1"), ph), parsePlaceholders(Main.bans.getString("reason-line2"), ph), false)
						.addField(parsePlaceholders(Main.bans.getString("banned-by-line1"), ph), parsePlaceholders(Main.bans.getString("banned-by-line2"), ph), false)
						.addField(parsePlaceholders(Main.bans.getString("duration-line1"), ph), parsePlaceholders(Main.bans.getString("duration-line2"), ph), false)
						.setThumbnail("https://minotar.net/armor/bust/" + target + "/100.png")
						.setFooter(parsePlaceholders(Main.bans.getString("banned-on"), ph), "")
						.setUrl("https://mcnames.net/username/" + target)
						);
			}
			if(type == BanType.unban)
			{
				webhook.addEmbed(new DiscordWebhook.EmbedObject()
						.setTitle(parsePlaceholders(Main.bans.getString("unbanned-title"), ph))
						.setDescription(parsePlaceholders(Main.bans.getString("description"), ph))
						.setColor(Color.decode(Main.bans.getString("unban-color")))
						.addField(parsePlaceholders(Main.bans.getString("unbanned-by-line1"), ph), parsePlaceholders(Main.bans.getString("unbanned-by-line2"), ph), false)
						.setThumbnail("https://minotar.net/armor/bust/" + target + "/100.png")
						.setFooter(parsePlaceholders(Main.bans.getString("unbanned-on"), ph), "")
						.setUrl("https://mcnames.net/username/" + target)
						);
			}
			if(type == BanType.ipban) {
				webhook.addEmbed(new DiscordWebhook.EmbedObject()
						.setTitle(parsePlaceholders(Main.bans.getString("ip-banned-title"), ph))
						.setDescription(parsePlaceholders(Main.bans.getString("description"), ph))
						.setColor(Color.decode(Main.bans.getString("banip-color")))
						.addField(parsePlaceholders(Main.bans.getString("reason-line1"), ph), parsePlaceholders(Main.bans.getString("reason-line2"), ph), false)
						.addField(parsePlaceholders(Main.bans.getString("banned-by-line1"), ph), parsePlaceholders(Main.bans.getString("banned-by-line2"), ph), false)
						.setFooter(parsePlaceholders(Main.bans.getString("banned-on"), ph), "")
						);
			}
			if(type == BanType.ipunban)
			{
				webhook.addEmbed(new DiscordWebhook.EmbedObject()
						.setTitle(parsePlaceholders(Main.bans.getString("ip-unbanned-title"), ph))
						.setDescription(parsePlaceholders(Main.bans.getString("description"), ph))
						.setColor(Color.decode(Main.bans.getString("unbanip-color")))
						.addField(parsePlaceholders(Main.bans.getString("unbanned-by-line1"), ph), parsePlaceholders(Main.bans.getString("unbanned-by-line2"), ph), false)
						.setFooter(parsePlaceholders(Main.bans.getString("unbanned-on"), ph), "")
						);
			}
			try {
				Log.info(Main.plugin, "Executing webhook.");
				webhook.execute();
			} catch (IOException e) {
				e.printStackTrace();
				Log.error(Main.plugin, "&cConnection failed.");
			}
		});
	}

	/**
	 * Parse language placeholders and encode the final message as unicode escape sequences.
	 *
	 * @param message raw message to format
	 * @return formatted and unicode-escaped message
	 */
	public static String FormatMessage(String message) {
		message = Lang.Parse(message);
		return encodeStringToUnicodeSequence(message);
	}

	/**
	 * Encode each code point in a string as a unicode escape sequence.
	 *
	 * @param txt input text
	 * @return unicode-escaped text
	 */
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

	/** Prefix used when building unicode escape sequences. */
	private final static String UNICODE_PREFIX = "\\u";

	/**
	 * Convert a single unicode code point to its escaped string representation.
	 *
	 * @param codePoint unicode code point
	 * @return escaped unicode string
	 */
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

	/**
	 * Create the leading zero padding needed for a 4-character unicode escape payload.
	 *
	 * @param codePointStrLength current hex string length
	 * @return zero padding string
	 */
	private static String getPrecedingZerosStr(int codePointStrLength) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < 4 - codePointStrLength; i++) {
			result.append("0");
		}
		return result.toString();
	}

	/**
	 * Legacy duration formatter kept for compatibility and fallback troubleshooting.
	 *
	 * @param duration compact duration token string
	 * @return human-readable duration label
	 */
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