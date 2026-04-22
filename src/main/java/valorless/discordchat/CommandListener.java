package valorless.discordchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import valorless.discordchat.discord.Bot;
import valorless.discordchat.linking.Linking;
import valorless.discordchat.storage.Storage;
import valorless.discordchat.utils.Extra;
import valorless.discordchat.utils.Utils;
import valorless.valorlessutils.logging.Log;

import static valorless.discordchat.ChatListener.FormatMessage;

public class CommandListener implements CommandExecutor {

	public static JavaPlugin plugin;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Log.debug(plugin, "Sender: " + sender.getName());
		Log.debug(plugin, "Command: " + command.toString());
		Log.debug(plugin, "Label: " + label);
		for(String a : args) {
			Log.debug(plugin, "Argument: " + a);
		}

		if(sender instanceof Player player) {
			if(args.length == 0) {
				player.sendMessage(Lang.Parse(Main.bot.getInviteLink()));
				return true;
			}
			else 
				if (args.length >= 1){
					if(args[0].equalsIgnoreCase("mute")) {
						if(Main.muted.hasKey(player.getName())) {
							Main.muted.set(player.getName(), !Main.muted.getBool(player.getName()));
						}else {
							Main.muted.set(player.getName(), true);
						}
					}
					if(args[0].equalsIgnoreCase("link")) {
						if(args.length >= 2) {
							// Attempt to link the player's Minecraft account with the provided Discord ID.
							if(Linking.isLinked(player.getUniqueId()) ) {
								player.sendMessage(Lang.Parse(
										 Lang.Get("prefix") +
												 Storage.Accounts.dataFile.getString("lang.already-linked.minecraft")
								));
								return true;
							}
							Long id = null;
							try {
								id = Long.parseLong(args[1]);
							} catch (NumberFormatException e) {
								id = Main.bot.getUserIDByUsername(args[1]);
							}
							if(id == null) {
								player.sendMessage(Lang.Parse(
										Lang.Get("prefix") +
												Storage.Accounts.dataFile.getString("lang.not-found.minecraft")
								));
								//player.sendMessage(Name + " §cDiscord user not found.\nIf this issue persists, please us the Discord ID instead.");
								return true;
							}
							Linking.addLink(player.getUniqueId(), id, null);
							return true;
						}
						sender.sendMessage(Lang.Parse("Usage: /discord link <discord id/username>"));
						return true;
					}
					if(args[0].equalsIgnoreCase("unlink")) {
						Boolean result = Linking.unlink(player.getUniqueId());
						if(result) {
							player.sendMessage(Lang.Parse(
									Lang.Get("prefix") +
											Storage.Accounts.dataFile.getString("lang.unlinked.minecraft")
							));
							//player.sendMessage(Name + " §aYour Discord account has been unlinked from your Minecraft account.");
						}else {
							player.sendMessage(Lang.Parse(
									Lang.Get("prefix") +
											Storage.Accounts.dataFile.getString("lang.not-found-account.minecraft")
							));
							//player.sendMessage(Name + " §cNo linked Discord account found for your Minecraft account.");
						}
						return true;
					}
					if(args[0].equalsIgnoreCase("reload") && player.hasPermission("discordchat.reload")) {
						Main.config.reload();
						Main.filter.reload();
						Lang.lang.reload();
						Bot.ReloadConfig();
						Main.bot.Shutdown();
						Main.bot = new Bot();
						Storage.Accounts.dataFile.reload();
						player.sendMessage(Lang.Get("prefix") + "§aReloaded.");
						Log.info(plugin, "&aReloaded!");
						Main.enabled = true;
						if(Main.error) {
							Main.error = false;
							DiscordWebhook webhook = new DiscordWebhook(Main.config.getString("webhook-url"));

							webhook.setUsername(Main.config.getString("server-username"));
							webhook.setContent(Lang.Get("server-reconnect"));
							webhook.setAvatarUrl(Main.config.getString("server-icon-url"));

							try {
								webhook.execute();
							} catch (IOException e) {
								e.printStackTrace();
								Log.error(plugin, "Connection failed.");
								Main.error = true;
								Log.error(plugin, "Attempting to reconnect soon.");
								Log.error(plugin, "Plugin disabled regular connections to avoid further failed connections.");
								Log.error(plugin, "Please reload the plugin to manually re-enable");
							}
						}
						Main.username = Main.config.getString("server-username");
						if(Main.config.getString("webhook-url") == "") {
							Log.info(plugin, "Disabled!");
							Log.warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
							Main.enabled = false;
						}
						return true;
					}
					return true;
				}
		} else {
			if(args[0].equalsIgnoreCase("reload")) {
				Main.config.reload();
				Lang.lang.reload();
				sender.sendMessage(Lang.Get("prefix") +"§aReloaded.");
				Log.info(plugin, "&aReloaded!");
				Bot.ReloadConfig();
				Main.bot.Shutdown();
				Main.bot = new Bot();
				Storage.Accounts.dataFile.reload();
				Main.enabled = true;
				if(Main.error) {
					Main.error = false;
					DiscordWebhook webhook = new DiscordWebhook(Main.config.getString("webhook-url"));

					webhook.setUsername(Main.config.getString("server-username"));
					webhook.setContent(Lang.Get("server-reconnect"));
					webhook.setAvatarUrl(Main.config.getString("server-icon-url"));

					try {
						webhook.execute();
					} catch (IOException e) {
						e.printStackTrace();
						Log.error(plugin, "Connection failed.");
						Main.error = true;
						Log.error(plugin, "Attempting to reconnect soon.");
						Log.error(plugin, "Plugin disabled regular connections to avoid further failed connections.");
						Log.error(plugin, "Please reload the plugin to manually re-enable");
					}
				}
				Main.username = Main.config.getString("server-username");
				if(Main.config.getString("webhook-url") == "") {
					Log.info(plugin, "Disabled!");
					Log.warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
					Main.enabled = false;
				}

				return true;
			}
			else 
				if(args[0].equalsIgnoreCase("inventorytest")) {
					String url = Main.config.getString("webserver.url") + Main.config.getString("webserver.media-location");
					DiscordWebhook webhook = new DiscordWebhook(Main.config.getString("webhook-url"));

					webhook.setUsername(Main.config.getString("server-username"));
					//webhook.setContent("Inventory Test");
					ItemStack[] items = Utils.RandomInventory();

					List<Storage.Inventories.InventorySlot> slots = new ArrayList<>();
					for(ItemStack item : items) {
						if(item != null && item.getType() != Material.AIR) {
							String name = null;
							String display = null;
							if(item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
								display = Lang.RemoveColorCodesAndFormatting(item.getItemMeta().getDisplayName()); // Use custom display name if available
							}
							if(item.getItemMeta() != null && item.getItemMeta().hasItemName()) {
								name = Lang.RemoveColorCodesAndFormatting(item.getItemMeta().getItemName()); // Use custom item name if available
							}
							String type = Main.translator.Translate(item.getType().getTranslationKey());
							slots.add(new Storage.Inventories.InventorySlot(display, name, type, item.getAmount())); // Store item name and amount
						}
					}
					Storage.Inventories.InventoryEntry inv = new Storage.Inventories.InventoryEntry(slots);

					try {
						String inventory = Extra.inventoryString(inv);
						webhook.addEmbed(new DiscordWebhook.EmbedObject()
								.setTitle(FormatMessage(null, "Console's Inventory"))
								.setDescription(FormatMessage(null, inventory))
						);
					} catch (Exception e) {}

					webhook.setAvatarUrl(Main.config.getString("server-icon-url"));

					try {
						webhook.execute();
					} catch (IOException e) {
						e.printStackTrace();
						Log.error(plugin, "Connection failed.");
						Main.error = true;
						Log.error(plugin, "Attempting to reconnect soon.");
						Log.error(plugin, "Plugin disabled regular connections to avoid further failed connections.");
						Log.error(plugin, "Please reload the plugin to manually re-enable");
					}
					return true;
				}

			// Console Message
			String message = "";
            for (String arg : args) {
                message = message + " " + arg;
            }
			if(Main.enabled == false) {
				Log.warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
			}
			DiscordWebhook webhook = new DiscordWebhook(Main.config.getString("webhook-url"));

			webhook.setUsername(Main.config.getString("console-username"));

			webhook.setContent(FormatMessage(null, Lang.Get("console-message")
					.replace("%message%", Lang.RemoveColorCodesAndFormatting(message))));
			for(Player player:Bukkit.getServer().getOnlinePlayers())
			{
				player.sendMessage(Lang.Get("console-prefix") + message);
			}
			sender.sendMessage(Lang.Get("console-prefix") + message);
			webhook.setAvatarUrl(Main.config.getString("console-icon-url"));

			try {
				webhook.execute();
			} catch (IOException e) {
				e.printStackTrace();
				Log.error(plugin, "§cConnection failed.");
			}
			return true;

		}
		return false;
	}
}
