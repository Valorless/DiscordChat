package valorless.discordchat.linking;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class LinkEvent extends Event {

    /** Required HandlerList for custom Bukkit events. */
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
    private final UUID player;
    private final Long discordID;
    private final Long channelID;
	
    public LinkEvent(UUID player, Long discordID, Long channelID) {
		this.player = player;
		this.discordID = discordID;
		this.channelID = channelID;
    }

	public UUID getPlayer() {
		return player;
	}

	public Long getDiscordID() {
		return discordID;
	}
	
	public Long getChannelID() {
		return channelID;
	}
}
