package valorless.discordchatmonitor;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import valorless.valorlessutils.ValorlessUtils.*;
import valorless.valorlessutils.config.Config;

public class CommandListener implements Listener { // Primary objective of CommandListener is to listen for DiscordBans commands.
	public static JavaPlugin plugin;
	String Name = "§7[§4DiscordChatMonitor§7]§r";
	public static Config config;
	
	public void onEnable() {
	}
	
	@EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String[] args = event.getMessage().split("\\s+");
		Player sender = event.getPlayer();
		
		if(args[0].equalsIgnoreCase("/dcm")) {
			if(args.length == 1) {
			sender.sendMessage(Name + " DiscordChatMonitor by Valorless. Send events and messages to Discord.");
			}
			else 
			if (args.length >= 2){
				if(args[1].equalsIgnoreCase("reload") && sender.hasPermission("discordchatmonitor.reload")) {
					config.Reload();
					Lang.lang.Reload();
					sender.sendMessage(Name +" §aReloaded.");
					Log.Info(plugin, "Reloaded!");
					DiscordChatMonitor.enabled = true;
					DiscordChatMonitor.username = config.GetString("server-username");
					if(config.GetString("webhook-url") == "") {
						Log.Info(plugin, "Disabled!");
						Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
						DiscordChatMonitor.enabled = false;
					}
				}
			}
		}
		
	}
	
	@EventHandler
    public void onServerCommand(ServerCommandEvent event) {
		String[] args = event.getCommand().split("\\s+");
		args[0] = "/" + args[0];
		String message = "";
		for(int i = 1; i < args.length; i++) { message = message + " " + args[i]; }
		if(args[0].equalsIgnoreCase("/server") && message != "") {
			if(DiscordChatMonitor.enabled == false) {
				Log.Warning(plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
			}
	    	DiscordWebhook webhook = new DiscordWebhook(config.GetString("webhook-url"));
	    	
	    	webhook.setUsername(config.GetString("console-username"));
	        webhook.setContent(Lang.RemoveColorCodesAndFormatting(message));
	        for(Player player:Bukkit.getServer().getOnlinePlayers())
			{
				player.sendMessage(Lang.Get("console-prefix") + message);
			}
			event.getSender().sendMessage(Lang.Get("console-prefix") + message);
	        webhook.setAvatarUrl(config.GetString("console-icon-url"));
	        
	        try {
				webhook.execute();
			} catch (IOException e) {
				e.printStackTrace();
				Log.Error(plugin, "§cConnection failed.");
			}
		}
		
	}

}
