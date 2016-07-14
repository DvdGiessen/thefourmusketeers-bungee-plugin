package nl.thefourmusketeers.tasks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.config.ServerInfo;
import nl.thefourmusketeers.MusketeersPlugin;

/**
 * Task supporting statistics functionality.
 *
 * @author Daniël van de Giessen
 */
public class StatisticsTask implements Runnable {
    /**
     * The interval between subsequent executions of this task.
     */
    public static final int TASK_INTERVAL = 30;

    /**
     * The unit in which TASK_INTERVAL should be interpreted.
     */
    public static final TimeUnit TASK_INTERVAL_TIME_UNIT = TimeUnit.SECONDS;

    /**
     * The plugin instance.
     */
    private final MusketeersPlugin plugin;

    /**
     * Constructs a new statistics task.
     *
     * @param plugin
     *            The plugin instance.
     */
    public StatisticsTask(final MusketeersPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Updates the server statistics when the task is executed.
     */
    @Override
    public void run() {
        // Prepared statements
        try (
            final PreparedStatement serverStatisticsLatest = this.plugin.getDatabase().prepareStatement(
                "SELECT time FROM serverStatistics WHERE server = ? AND playerCount = ? AND time = (SELECT MAX(time) FROM serverStatistics WHERE server = ?)"
            );
            final PreparedStatement serverStatisticsInsert = this.plugin.getDatabase().prepareStatement(
                "INSERT INTO serverStatistics (server, time, playerCount) VALUES (?, strftime('%s', 'now'), ?)"
            );
            final PreparedStatement serverStatisticsUpdate = this.plugin.getDatabase().prepareStatement(
                "UPDATE serverStatistics SET time = strftime('%s', 'now') WHERE server = ? AND time = ?"
            );
        ) {
            // For each server
            for(final ServerInfo server : this.plugin.getProxy().getServers().values()) {
                // Server details
                final String serverName = server.getName();
                final int playerCount = server.getPlayers().size();

                // Check if a suitable row exists
                serverStatisticsLatest.setString(1, serverName);
                serverStatisticsLatest.setInt(2, playerCount);
                serverStatisticsLatest.setString(3, serverName);

                // Existing timestamp
                final ResultSet latestTimestamp = serverStatisticsLatest.executeQuery();
                if(latestTimestamp.isClosed()) {
                    // We insert a new row
                    serverStatisticsInsert.setString(1, serverName);
                    serverStatisticsInsert.setInt(2, playerCount);
                    serverStatisticsInsert.execute();
                } else {
                    // We update the existing row
                    serverStatisticsUpdate.setString(1, serverName);
                    serverStatisticsUpdate.setInt(2, latestTimestamp.getInt(1));
                    serverStatisticsUpdate.execute();
                }
            }
        } catch (final SQLException ex) {
            this.plugin.getLogger().severe("Failed to write server statistics!");
            ex.printStackTrace();
        }
    }
}
