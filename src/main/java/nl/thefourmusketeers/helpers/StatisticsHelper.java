package nl.thefourmusketeers.helpers;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import nl.thefourmusketeers.MusketeersPlugin;

/**
 * Helper providing functions used for the statistics functionality.
 *
 * @author Daniël van de Giessen
 */
public class StatisticsHelper {
    /**
     * Cleans up player statistics missing a disconnect time.
     *
     * @param plugin The plugin instance.
     * @throws SQLException When the disconnect time could not be updated.
     */
    public static void cleanupStatistics(final MusketeersPlugin plugin) throws SQLException {
        // Use the latest known time the server was online
        plugin.getDatabase().prepareStatement(
            "UPDATE playerStatistics SET disconnectTime = MAX(connectTime, (SELECT MAX(time) FROM serverStatistics s WHERE s.server = playerStatistics.server AND s.playerCount > 0)) WHERE disconnectTime IS NULL"
        ).execute();
    }

    /**
     * Inserts all registered servers which are missing in the database.
     *
     * @param plugin The plugin instance.
     * @throws SQLException When the insertion of a new server failed.
     */
    public static void addServers(final MusketeersPlugin plugin) throws SQLException {
        // Get all servers
        final PreparedStatement addServer = plugin.getDatabase().prepareStatement(
            "INSERT OR IGNORE INTO servers ( name ) VALUES ( ? )"
        );
        for(final String serverName : plugin.getProxy().getServers().keySet()) {
            addServer.setString(1, serverName);
            addServer.execute();
        }
    }
}
