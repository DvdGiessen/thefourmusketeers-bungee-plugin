package nl.thefourmusketeers.listeners;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import nl.thefourmusketeers.MusketeersPlugin;

/**
 * Server connect listener for whitelist functionality.
 *
 * @author Daniël van de Giessen
 */
public class WhitelistServerConnectListener implements Listener {
    /**
     * The plugin instance.
     */
    private final MusketeersPlugin plugin;

    /**
     * Constructs a new server connect listener.
     *
     * @param plugin
     *            The plugin instance.
     */
    public WhitelistServerConnectListener(final MusketeersPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Event handler for server connects, checks the whitelist and blocks the
     * connection if the player is not whitelisted.
     *
     * @param event
     *            The server connect event to be handled.
     */
    @EventHandler
    public void onServerConnect(final ServerConnectEvent event) {
        // No need to handle cancelled events
        if (event.isCancelled()) {
            return;
        }

        // Is the whitelist enabled?
        if (this.plugin.getConfig().getBoolean("whitelist.enabled", false)) {
            // Get the player
            final ProxiedPlayer p = event.getPlayer();

            // Check the user against the database
            boolean isWhitelisted = false;
            try (
                final PreparedStatement playerExists = this.plugin.getDatabase().prepareStatement(
                    "SELECT COUNT(*) FROM players WHERE uuid = ? AND name = ?"
                )
            ) {
                playerExists.setString(1, p.getUniqueId().toString());
                playerExists.setString(2, p.getName());
                isWhitelisted = playerExists.executeQuery().getBoolean(1);
            } catch (final SQLException ex) {
                this.plugin.getLogger().severe("Failed check database for player " + p.getDisplayName());
                ex.printStackTrace();
            }

            // Disconnect if not whitelisted
            if(!isWhitelisted) {
                this.plugin.getLogger().info(event.getPlayer().getDisplayName() + " is not whitelisted, access blocked");
                event.setCancelled(true);
                event.getPlayer().disconnect(this.plugin.getMessageHelper().buildMessage("whitelist"));

                // Notify admins?
                if (this.plugin.getConfig().getBoolean("whitelist.notify-admins", false)) {
                    this.plugin.getMessageHelper().messagePermission("whitelist.notify", "whitelist-notify", p.getDisplayName());
                }
            }
        }
    }

}
