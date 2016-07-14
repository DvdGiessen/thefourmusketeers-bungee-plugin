package nl.thefourmusketeers.helpers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import nl.thefourmusketeers.MusketeersPlugin;

/**
 * Helper providing functionality for using the SQLite database.
 *
 * @author Daniël van de Giessen
 */
public class DatabaseHelper implements AutoCloseable {
    /**
     * The default filename for the database file.
     */
    private static final String    DATABASE_FILENAME     = "database.sqlite";

    /**
     * The default filename for the database file.
     */
    private static final String    DATABASE_SQL_FILENAME = "database.sql";

    /**
     * The plugin instance.
     */
    private final MusketeersPlugin plugin;

    /**
     * The database connection used by the plugin.
     */
    private final Connection       database;

    /**
     * Constructs a new configuration helper.
     *
     * @throws Exception
     *             When connecting to the database fails.
     * @throws ClassNotFoundException
     *             When the SQLite JDBC driver is not available.
     */
    public DatabaseHelper(final MusketeersPlugin plugin) throws Exception {
        this.plugin = plugin;

        // Data folder
        final File folder = plugin.getDataFolder();
        if (!folder.exists()) {
            plugin.getLogger().info("Data folder doesn't exist, creating...");
            folder.mkdirs();
        }

        // Check database file
        final String databasePath = plugin.getConfig().getString("database", null);
        final File databaseFile;
        if (databasePath == null) {
            databaseFile = new File(folder, DATABASE_FILENAME);
        } else {
            databaseFile = new File(databasePath);
        }
        final boolean databaseExists = databaseFile.exists();
        plugin.getLogger().info("Database file is " + databaseFile.toPath() + ", which " + (databaseExists ? "exists" : "doesn't exist"));

        // Load database
        Class.forName("org.sqlite.JDBC");
        this.database = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.toPath());

        // Set up SQLite settings
        this.database.prepareStatement("PRAGMA foreign_keys = ON").execute();

        // If required, initiate the database using the create statements
        if (!databaseExists) {
            plugin.getLogger().info("Setting up new database...");
            final Scanner s = new Scanner(plugin.getResourceAsStream(DATABASE_SQL_FILENAME));
            s.useDelimiter(";\n+");
            while(s.hasNext()) {
                this.database.prepareStatement(s.next()).execute();
            }
            s.close();
        }
    }

    /**
     * @return The database connection.
     */
    public Connection getDatabase() {
        return this.database;
    }

    /**
     * Closes the database connection.
     */
    @Override
    public void close() {
        this.plugin.getLogger().info("Closing database connection...");
        try {
            this.database.close();
        } catch (final SQLException ex) {
            this.plugin.getLogger().severe("Failed to close database handle!");
            ex.printStackTrace();
        }
    }
}
