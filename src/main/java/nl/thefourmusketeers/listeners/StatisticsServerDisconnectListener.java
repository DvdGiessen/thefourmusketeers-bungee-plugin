package nl.thefourmusketeers.listeners;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import nl.thefourmusketeers.MusketeersPlugin;

/**
 * Server disconnect listener for statistics functionality.
 *
 * @author Daniël van de Giessen
 */
public class StatisticsServerDisconnectListener implements Listener {
    /**
     * The plugin instance
     */
    private final MusketeersPlugin plugin;

    /**
     * Constructs a new server disconnect listener.
     *
     * @param plugin
     *            The plugin instance.
     */
    public StatisticsServerDisconnectListener(final MusketeersPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for server connects, records the disconnect time for the
     * player.
     *
     * @param event
     *            The server connect event to be handled.
     */
    @EventHandler
    public void onServerDisconnect(final ServerDisconnectEvent event) {
        // Update the player statistics
        try (
            final PreparedStatement playerStatistics = this.plugin.getDatabase().prepareStatement(
                "UPDATE playerStatistics SET disconnectTime = strftime('%s', 'now') WHERE disconnectTime IS NULL AND player = ? AND server = ?"
            )
        ) {
            playerStatistics.setString(1, event.getPlayer().getUniqueId().toString());
            playerStatistics.setString(2, event.getTarget().getName());
            playerStatistics.execute();
        } catch (final SQLException ex) {
            this.plugin.getLogger().severe("Failed to write player disconnect statistics!");
            ex.printStackTrace();
        }
    }
}
