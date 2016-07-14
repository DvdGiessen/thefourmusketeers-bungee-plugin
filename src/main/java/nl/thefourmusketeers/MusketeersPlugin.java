package nl.thefourmusketeers;

import java.sql.Connection;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.config.Configuration;
import nl.thefourmusketeers.commands.WhitelistCommand;
import nl.thefourmusketeers.helpers.ConfigHelper;
import nl.thefourmusketeers.helpers.DatabaseHelper;
import nl.thefourmusketeers.helpers.MessageHelper;
import nl.thefourmusketeers.helpers.StatisticsHelper;
import nl.thefourmusketeers.listeners.StatisticsServerConnectedListener;
import nl.thefourmusketeers.listeners.StatisticsServerDisconnectListener;
import nl.thefourmusketeers.listeners.WhitelistServerConnectListener;
import nl.thefourmusketeers.tasks.StatisticsTask;

/**
 * Plugin class for The Four Musketeers.
 *
 * @author Daniël van de Giessen
 */
public class MusketeersPlugin extends Plugin {
    /**
     * Config helper.
     */
    private ConfigHelper configHelper;

    /**
     * Database helper.
     */
    private DatabaseHelper databaseHelper;

    /**
     * Message helper.
     */
    private MessageHelper messageHelper;

    /**
     * Enables the plugin by loading the resources and registering the various
     * functionalities.
     */
    @Override
    public void onEnable() {
        // Load configuration file
        try {
            this.configHelper = new ConfigHelper(this);
        } catch (final Exception ex) {
            this.getLogger().severe("Failed to load configuration file!");
            ex.printStackTrace();
            return;
        }

        // Connect to database
        try {
            this.databaseHelper = new DatabaseHelper(this);
        } catch (final Exception ex) {
            this.getLogger().severe("Failed to connect to database!");
            ex.printStackTrace();
            return;
        }

        // Prepare the message helper
        this.messageHelper = new MessageHelper(this);

        // Setup plugin functionality
        final PluginManager p = this.getProxy().getPluginManager();
        final TaskScheduler s = this.getProxy().getScheduler();

        // Whitelist functionality
        p.registerListener(this, new WhitelistServerConnectListener(this));
        p.registerCommand(this, new WhitelistCommand(this));

        // Statistics functionality is registered only if the required startup
        // procedures are completed without an error
        try {
            // Clean up statistics
            StatisticsHelper.cleanupStatistics(this);
            StatisticsHelper.addServers(this);

            // Register functionality
            p.registerListener(this, new StatisticsServerConnectedListener(this));
            p.registerListener(this, new StatisticsServerDisconnectListener(this));
            s.schedule(this, new StatisticsTask(this), 0, StatisticsTask.TASK_INTERVAL, StatisticsTask.TASK_INTERVAL_TIME_UNIT);
        } catch (final Exception ex) {
            this.getLogger().severe("Failed to enable statistics!");
            ex.printStackTrace();
        }
    }

    /**
     * Disables the plugin by unregistering all functionality and closing the
     * database handle.
     */
    @Override
    public void onDisable() {
        // Scheduled tasks
        this.getProxy().getScheduler().cancel(this);

        // Plugin functionality
        final PluginManager p = this.getProxy().getPluginManager();
        p.unregisterCommands(this);
        p.unregisterListeners(this);

        // Database
        this.databaseHelper.close();
    }

    /**
     * @return The configuration of the plugin.
     */
    public Configuration getConfig() {
        return this.configHelper.getConfig();
    }

    /**
     * @return The default configuration of the plugin.
     */
    public Configuration getDefaultConfig() {
        return this.configHelper.getDefaultConfig();
    }

    /**
     * Saves any changes to the configuration of the plugin.
     */
    public void saveConfig() {
        this.configHelper.saveConfig();
    }

    /**
     * @return The database used by the plugin.
     */
    public Connection getDatabase() {
        return this.databaseHelper.getDatabase();
    }

    /**
     * @return The database used by the plugin.
     */
    public MessageHelper getMessageHelper() {
        return this.messageHelper;
    }
}
