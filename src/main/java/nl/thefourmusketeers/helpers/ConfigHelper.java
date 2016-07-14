package nl.thefourmusketeers.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import nl.thefourmusketeers.MusketeersPlugin;

/**
 * Helper providing functionality for using the configuration file.
 *
 * @author Daniël van de Giessen
 */
public class ConfigHelper {
    /**
     * Prefix for permissions in Bungee's configuration.
     */
    public static final String     PERMISSION_PREFIX = "thefourmusketeers.";

    /**
     * The filename of the configuration file.
     */
    private static final String    CONFIG_FILENAME   = "config.yml";

    /**
     * The plugin instance.
     */
    private final MusketeersPlugin plugin;

    /**
     * The configuration of the plugin.
     */
    private final Configuration    config;

    /**
     * The default configuration of the plugin.
     */
    private final Configuration    defaultConfig;


    /**
     * Constructs a new configuration helper.
     *
     * @throws IOException
     *             When there's a problem accessing the configuration file.
     */
    public ConfigHelper(final MusketeersPlugin plugin) throws IOException {
        this.plugin = plugin;

        // Data folder
        final File folder = this.plugin.getDataFolder();
        if (!folder.exists()) {
            this.plugin.getLogger().info("Data folder doesn't exist, creating...");
            folder.mkdirs();
        }

        // Check config file
        final File configFile = new File(folder, CONFIG_FILENAME);
        if (!configFile.exists()) {
            this.plugin.getLogger().info("Config file doesn't exist, creating...");
            Files.copy(this.plugin.getResourceAsStream(CONFIG_FILENAME), configFile.toPath());
        }

        // Load config
        this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

        // Load default config
        this.defaultConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.plugin.getResourceAsStream(CONFIG_FILENAME));
    }

    /**
     * @return The configuration instance.
     */
    public Configuration getConfig() {
        return this.config;
    }

    /**
     * @return The default configuration instance.
     */
    public Configuration getDefaultConfig() {
        return this.defaultConfig;
    }

    /**
     * Saves any changes to the configuration.
     */
    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config, new File(this.plugin.getDataFolder(), CONFIG_FILENAME));
        } catch (final IOException ex) {
            this.plugin.getLogger().severe("Failed to save config file!");
            ex.printStackTrace();
        }
    }
}
