package nl.thefourmusketeers.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import nl.thefourmusketeers.MusketeersPlugin;
import nl.thefourmusketeers.helpers.ConfigHelper;

/**
 * Whitelist command implementation.
 *
 * @author Daniël van de Giessen
 */
public class WhitelistCommand extends Command {
    /**
     * The name of the command.
     */
    public static final String     NAME = "whitelist";

    /**
     * The plugin instance
     */
    private final MusketeersPlugin plugin;

    /**
     * Constructs a new whitelist command.
     *
     * @param plugin
     *            The plugin instance.
     */
    public WhitelistCommand(final MusketeersPlugin plugin) {
        super(NAME);
        this.plugin = plugin;
    }

    /**
     * Execute the whitelist command with the specified sender and arguments.
     *
     * @param sender
     *            The executor of this command.
     * @param args
     *            Arguments used to invoke this command.
     */
    @Override
    public void execute(final CommandSender sender, final String[] args) {
        // Usage
        if (args.length == 0) {
            this.plugin.getMessageHelper().messagePlayer(sender, "whitelist-usage");
            return;
        }

        // First argument specifies command
        switch(args[0].toLowerCase()) {
            case "on":
            case "off":
            case "reload":
                this.plugin.getMessageHelper().messagePlayer(sender, "whitelist-unsupported");
                break;

            case "list":
                // Check the permissions
                if(!sender.hasPermission(ConfigHelper.PERMISSION_PREFIX + "whitelist.list")) {
                    this.plugin.getMessageHelper().messagePlayer(sender, "access-denied");
                    return;
                }

                // Show the list
                try (
                    final PreparedStatement playerExists = this.plugin.getDatabase().prepareStatement(
                        "SELECT name FROM players"
                    )
                ) {
                    final ResultSet results = playerExists.executeQuery();
                    final List<String> list = new ArrayList<String>();
                    while(results.next()) {
                        list.add(results.getString("name"));
                    }
                    this.plugin.getMessageHelper().messagePlayer(sender, "whitelist-list", list);
                } catch (final SQLException ex) {
                    this.plugin.getLogger().severe("Failed to list whitelist");
                    ex.printStackTrace();
                }
                break;

            case "add":
                // Check the permissions
                if(!sender.hasPermission(ConfigHelper.PERMISSION_PREFIX + "whitelist.admin")) {
                    this.plugin.getMessageHelper().messagePlayer(sender, "access-denied");
                    return;
                }

                // TODO add to whitelist
                this.plugin.getMessageHelper().messagePlayer(sender, "whitelist-unsupported");
                break;

            case "remove":
                // Check the permissions
                if(!sender.hasPermission(ConfigHelper.PERMISSION_PREFIX + "whitelist.admin")) {
                    this.plugin.getMessageHelper().messagePlayer(sender, "access-denied");
                    return;
                }

                // TODO mark as removed in whitelist
                this.plugin.getMessageHelper().messagePlayer(sender, "whitelist-unsupported");
                break;

            default:
                // Invalid command, show the usage
                this.plugin.getMessageHelper().messagePlayer(sender, "whitelist-usage");
                break;
        }
    }
}