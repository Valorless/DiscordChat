package valorless.discordchat;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import valorless.discordchat.discord.Bot;
import valorless.discordchat.linking.Linking;
import valorless.discordchat.utils.InventoryImageGenerator;
import valorless.valorlessutils.ValorlessUtils.Log;

public class CommandListener implements CommandExecutor {

	public static JavaPlugin plugin;
	String Name = "§7[§9DiscordChat§7]§r";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Log.Debug(plugin, "Sender: " + sender.getName());
		Log.Debug(plugin, "Command: " + command.toString());
		Log.Debug(plugin, "Label: " + label);
		for(String a : args) {
			Log.Debug(plugin, "Argument: " + a);
		}

		if(sender instanceof Player player) {
			if(args.length == 0) {
				player.sendMessage(Lang.Parse(Main.bot.getInviteLink()));
				return true;
			}
			else 
				if (args.length >= 1){
					if(args[0].equalsIgnoreCase("mute")) {
						if(Main.muted.HasKey(player.getName())) {
							Main.muted.Set(player.getName(), !Main.muted.GetBool(player.getName()));
						}else {
							Main.muted.Set(player.getName(), true);
						}
					}
					if(args[0].equalsIgnoreCase("link")) {
						if(args.length >= 2) {
							// Attempt to link the player's Minecraft account with the provided Discord ID.
							if( Linking.isLinked(player.getUniqueId()) ) {
								player.sendMessage(Name + " §cYour Minecraft account is already linked to a Discord account.");
								return true;
							}
							Long id = null;
							try {
								id = Long.parseLong(args[1]);
							} catch (NumberFormatException e) {
								id = Main.bot.getUserIDByUsername(args[1]);
							}
							if(id == null) {
								player.sendMessage(Name + " §cDiscord user not found.\nIf this issue persists, please us the Discord ID instead.");
								return true;
							}
							Linking.addLink(player.getUniqueId(), id, null);
							return true;
						}
						return true;
					}
					if(args[0].equalsIgnoreCase("unlink")) {
						Boolean result = Linking.unlink(player.getUniqueId());
						if(result) {
							player.sendMessage(Name + " §aYour Discord account has been unlinked from your Minecraft account.");
						}else {
							player.sendMessage(Name + " §cNo linked Discord account found for your Minecraft account.");
						}
						return true;
					}
					if(args[0].equalsIgnoreCase("reload") && player.hasPermission("discordchat.reload")) {
						Main.config.Reload();
						Main.filter.Reload();
						Lang.lang.Reload();
						Bot.ReloadConfig();
						Main.bot.Shutdown();
						Main.bot = new Bot();
						player.sendMessage(Name +" §aReloaded.");
						Log.Info(plugin, "Reloaded!");
						Main.enabled = true;
						if(Main.error) {
							Main.error = false;
							DiscordWebhook webhook = new DiscordWebhook(Main.config.GetString("webhook-url"));

							webhook.setUsername(Main.config.GetString("server-username"));
							webhook.setContent(Lang.Get("server-reconnect"));
							webhook.setAvatarUrl(Main.config.GetString("server-icon-url"));

							try {
								webhook.execute();
							} catch (IOException e) {
								e.printStackTrace();
								Log.Error(plugin, "Connection failed.");
								Main.error = true;
								Log.Error(plugin, "Attempting to reconnect soon.");
								Log.Error(plugin, "Plugin disabled regular connections to avoid further failed connections.");
								Log.Error(plugin, "Please reload the plugin to manually re-enable");
							}
						}
						Main.username = Main.config.GetString("server-username");
						if(Main.config.GetString("webhook-url") == "") {
							Log.Info(plugin, "Disabled!");
							Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
							Main.enabled = false;
						}
						return true;
					}
					return true;
				}
		} else {
			if(args[0].equalsIgnoreCase("reload")) {
				Main.config.Reload();
				Lang.lang.Reload();
				sender.sendMessage(Name +" §aReloaded.");
				Log.Info(plugin, "Reloaded!");
				Bot.ReloadConfig();
				Main.bot.Shutdown();
				Main.bot = new Bot();
				Main.enabled = true;
				if(Main.error) {
					Main.error = false;
					DiscordWebhook webhook = new DiscordWebhook(Main.config.GetString("webhook-url"));

					webhook.setUsername(Main.config.GetString("server-username"));
					webhook.setContent(Lang.Get("server-reconnect"));
					webhook.setAvatarUrl(Main.config.GetString("server-icon-url"));

					try {
						webhook.execute();
					} catch (IOException e) {
						e.printStackTrace();
						Log.Error(plugin, "Connection failed.");
						Main.error = true;
						Log.Error(plugin, "Attempting to reconnect soon.");
						Log.Error(plugin, "Plugin disabled regular connections to avoid further failed connections.");
						Log.Error(plugin, "Please reload the plugin to manually re-enable");
					}
				}
				Main.username = Main.config.GetString("server-username");
				if(Main.config.GetString("webhook-url") == "") {
					Log.Info(plugin, "Disabled!");
					Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
					Main.enabled = false;
				}

				return true;
			}
			else 
				if(args[0].equalsIgnoreCase("inventorytest")) {
					String url = Main.config.GetString("webserver.url") + Main.config.GetString("webserver.media-location");
					DiscordWebhook webhook = new DiscordWebhook(Main.config.GetString("webhook-url"));

					webhook.setUsername(Main.config.GetString("server-username"));
					//webhook.setContent("Inventory Test");
					String id = InventoryImageGenerator.generate(InventoryImageGenerator.RandomInventory(), 3, false);
					
					if(id != null) {
						try {
							webhook.addEmbed(new DiscordWebhook.EmbedObject()
									.setTitle(ChatListener.encodeStringToUnicodeSequence("Console's Inventory"))
									.setImage(url + id + ".png")
									);
							Log.Debug(Main.plugin, url + id + ".png");
						}catch(Exception e) {}
					}
					webhook.setAvatarUrl(Main.config.GetString("server-icon-url"));

					try {
						webhook.execute();
					} catch (IOException e) {
						e.printStackTrace();
						Log.Error(plugin, "Connection failed.");
						Main.error = true;
						Log.Error(plugin, "Attempting to reconnect soon.");
						Log.Error(plugin, "Plugin disabled regular connections to avoid further failed connections.");
						Log.Error(plugin, "Please reload the plugin to manually re-enable");
					}
					return true;
				}

			// Console Message
			String message = "";
			for(int i = 0; i < args.length; i++) { message = message + " " + args[i]; }
			if(Main.enabled == false) {
				Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
			}
			DiscordWebhook webhook = new DiscordWebhook(Main.config.GetString("webhook-url"));

			webhook.setUsername(Main.config.GetString("console-username"));

			webhook.setContent(ChatListener.FormatMessage(null, Lang.Get("console-message")
					.replace("%message%", Lang.RemoveColorCodesAndFormatting(message))));
			for(Player player:Bukkit.getServer().getOnlinePlayers())
			{
				player.sendMessage(Lang.Get("console-prefix") + message);
			}
			sender.sendMessage(Lang.Get("console-prefix") + message);
			webhook.setAvatarUrl(Main.config.GetString("console-icon-url"));

			try {
				webhook.execute();
			} catch (IOException e) {
				e.printStackTrace();
				Log.Error(plugin, "§cConnection failed.");
			}
			return true;

		}
		return false;
	}
}
