package valorless.discordchat.api;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import valorless.discordchat.ChatListener;
import valorless.discordchat.DiscordWebhook;
import valorless.discordchat.Lang;
import valorless.discordchat.Main;
import valorless.valorlessutils.logging.Log;

/**
 * Public API helpers for sending messages through DiscordChat integrations.
 *
 * <p>This class provides static convenience methods for sending webhook messages,
 * forwarding bot messages, and reading the current bot activity message.</p>
 */
public class DiscordChat {
	
	/**
	 * Send a message to Discord through the configured webhook and echo it to online players.
	 *
	 * @param msg message content to send
	 * @return {@code true} after attempting delivery
	 */
	public static boolean sendMessage(String msg) {
		if(Main.enabled == false) {
			Log.warning(Main.plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		DiscordWebhook webhook = new DiscordWebhook(Main.config.getString("webhook-url"));

		webhook.setUsername(Main.config.getString("console-username"));

		webhook.setContent(ChatListener.FormatMessage(null, Lang.Get("console-message")
				.replace("%message%", Lang.RemoveColorCodesAndFormatting(msg))));
		for(Player player:Bukkit.getServer().getOnlinePlayers())
		{
			player.sendMessage(Lang.Get("console-prefix") + msg);
		}
		webhook.setAvatarUrl(Main.config.getString("console-icon-url"));

		try {
			webhook.execute();
		} catch (IOException e) {
			e.printStackTrace();
			Log.error(Main.plugin, "§cConnection failed.");
		}
		return true;
	}

	/**
	 * Send a message through the active Discord bot to configured channels.
	 *
	 * @param msg message content to send
	 * @return {@code true} if send was queued successfully, otherwise {@code false}
	 */
	public static boolean sendBotMessage(String msg) {
		return Main.bot.SendMessage(null, msg);
	}
	
	/**
	 * Get the current resolved Discord bot activity message.
	 *
	 * @return activity message with runtime placeholders already applied
	 */
	public static String getBotActivityMessage() {
		return Main.bot.activityMessage();
	}
}
