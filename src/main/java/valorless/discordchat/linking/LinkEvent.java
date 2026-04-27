package valorless.discordchat.linking;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * Bukkit event fired when a Minecraft account is linked to a Discord account.
 *
 * <p>This event carries the Minecraft player UUID, Discord user id, and the
 * Discord channel id where the link action originated.</p>
 */
public class LinkEvent extends Event {

    /** Required HandlerList for custom Bukkit events. */
    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    /**
     * Get the static handler list required by Bukkit for this custom event type.
     *
     * @return event handler list
     */
    public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
    /** UUID of the linked Minecraft player. */
    private final UUID player;
    /** Discord user id linked to the player. */
    private final Long discordID;
    /** Discord channel id where the link action was performed. */
    private final Long channelID;
	
    /**
     * Create a new link event instance.
     *
     * @param player linked Minecraft player UUID
     * @param discordID linked Discord user id
     * @param channelID Discord channel id associated with the link action
     */
    public LinkEvent(UUID player, Long discordID, Long channelID) {
		this.player = player;
		this.discordID = discordID;
		this.channelID = channelID;
    }

    /**
     * Get the linked Minecraft player UUID.
     *
     * @return linked player UUID
     */
	public UUID getPlayer() {
		return player;
	}

	/**
	 * Get the linked Discord user id.
	 *
	 * @return linked Discord user id
	 */
	public Long getDiscordID() {
		return discordID;
	}
	
	/**
	 * Get the Discord channel id associated with this link action.
	 *
	 * @return channel id of the link source
	 */
	public Long getChannelID() {
		return channelID;
	}
}
