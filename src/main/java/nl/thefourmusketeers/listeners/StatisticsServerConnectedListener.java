package nl.thefourmusketeers.listeners;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import nl.thefourmusketeers.MusketeersPlugin;

/**
 * Server connected listener for statistics functionality.
 *
 * @author Daniël van de Giessen
 */
public class StatisticsServerConnectedListener implements Listener {
    /**
     * The plugin instance
     */
    private final MusketeersPlugin plugin;

    /**
     * Constructs a new server connected listener.
     *
     * @param plugin
     *            The plugin instance.
     */
    public StatisticsServerConnectedListener(final MusketeersPlugin plugin) {
        this.plugin = plugin;
    }


    /**
     * Event handler for once a player is connected to a server, records the
     * connect time and other statistics.
     *
     * @param event
     *            The server connected event to be handled.
     */
    @EventHandler
    public void onServerConnected(final ServerConnectedEvent event) {
        // Update the player statistics
        try (
            final PreparedStatement playerStatistics = this.plugin.getDatabase().prepareStatement(
                "INSERT INTO playerStatistics (player, connectTime, server) VALUES (?, strftime('%s', 'now'), ?)"
            )
        ) {
            playerStatistics.setString(1, event.getPlayer().getUniqueId().toString());
            playerStatistics.setString(2, event.getServer().getInfo().getName());
            playerStatistics.execute();
        } catch (final SQLException ex) {
            this.plugin.getLogger().severe("Failed to write player connect statistics!");
            ex.printStackTrace();
        }
    }

}
