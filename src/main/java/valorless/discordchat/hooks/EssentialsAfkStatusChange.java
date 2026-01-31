package valorless.discordchat.hooks;

import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.ess3.api.events.AfkStatusChangeEvent;
import net.ess3.api.events.AfkStatusChangeEvent.Cause;
import valorless.discordchat.ChatListener;
import valorless.discordchat.DiscordWebhook;
import valorless.discordchat.Main;
import valorless.valorlessutils.ValorlessUtils.Log;

public class EssentialsAfkStatusChange implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler (priority = EventPriority.HIGHEST)
    public void onAfkStatusChange(AfkStatusChangeEvent event) {
		if(Main.enabled == false) {
			Log.Warning(Main.plugin, "Please change my config.yml before using me.\nYou can reload me when needed with /dcm reload.");
		}
		
		boolean afk = event.getValue();
		Player player = event.getAffected().getBase();
		if(event.getAffected().isVanished()) return;
		if(event.getAffected().isHidden()) return;
		if(event.getCause() == Cause.QUIT) return;
		String yesAfk = "**%s** is now AFK.";
		String noAfk = "**%s** is no longer AFK.";
		
    	DiscordWebhook webhook = new DiscordWebhook(Main.config.GetString("webhook-url"));
    	
    	webhook.setUsername(Main.config.GetString("server-username"));
    	webhook.setContent(ChatListener.FormatMessage(player, "%timestamp% " + String.format(afk ? yesAfk : noAfk, player.getName())));
        webhook.setAvatarUrl(Main.config.GetString("server-icon-url"));
        
        try {
			webhook.execute();
		} catch (IOException e) {
			e.printStackTrace();
			ChatListener.ConnectionFailed();
		}
		
	}

}
