package valorless.discordchat.linking;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/**
 * Bukkit event fired when a Minecraft account is unlinked from a Discord account.
 *
 * <p>This event carries the Minecraft player UUID and Discord user id that were
 * involved in the unlink action.</p>
 */
public class UnlinkEvent extends Event {

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
	
    /** UUID of the unlinked Minecraft player. */
    private final UUID player;
    /** Discord user id that was unlinked from the player. */
    private final Long discordID;
	
    /**
     * Create a new unlink event instance.
     *
     * @param player unlinked Minecraft player UUID
     * @param discordID unlinked Discord user id
     */
    public UnlinkEvent(UUID player, Long discordID) {
		this.player = player;
		this.discordID = discordID;
    }

    /**
     * Get the unlinked Minecraft player UUID.
     *
     * @return unlinked player UUID
     */
	public UUID getPlayer() {
		return player;
	}

	/**
	 * Get the unlinked Discord user id.
	 *
	 * @return unlinked Discord user id
	 */
	public Long getDiscordID() {
		return discordID;
	}
}
