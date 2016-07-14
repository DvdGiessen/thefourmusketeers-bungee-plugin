package nl.thefourmusketeers.helpers;

import java.util.Arrays;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import nl.thefourmusketeers.MusketeersPlugin;

/**
 * Helper providing functions used for sending messages to players.
 *
 * @author Daniël van de Giessen
 */
public class MessageHelper {
    /**
     * The prefix for every message key.
     */
    private static final String    MESSAGE_KEY_PREFIX = "messages.";

    /**
     * The prefix string for every message.
     */
    private final String           messagePrefix;

    /**
     * The plugin instance.
     */
    private final MusketeersPlugin plugin;

    /**
     * Constructs a new message helper.
     */
    public MessageHelper(final MusketeersPlugin plugin) {
        this.plugin = plugin;
        this.messagePrefix = this.getConfigString("messages-prefix", "");
    }

    /**
     * Sends a message to a single player.
     *
     * @param player
     *            The player to which the message should be sent.
     * @param messageKey
     *            The key for the message to be sent.
     * @param arguments
     *            Arguments to be added to the message.
     */
    public void messagePlayer(final CommandSender player, final String messageKey, final Iterable<? extends String> arguments) {
        this.messagePlayer(player, this.buildMessage(messageKey, arguments));
    }

    /**
     * Sends a message to a single player.
     *
     * @param player
     *            The player to which the message should be sent.
     * @param messageKey
     *            The key for the message to be sent.
     * @param arguments
     *            Arguments to be added to the message.
     */
    public void messagePlayer(final CommandSender player, final String messageKey, final String... arguments) {
        this.messagePlayer(player, this.buildMessage(messageKey, arguments));
    }

    /**
     * Sends a message to a single player.
     *
     * @param player
     *            The player to which the message should be sent.
     * @param messageKey
     *            The key for the message to be sent.
     * @param arguments
     *            Arguments to be added to the message.
     */
    public void messagePlayer(final CommandSender player, final TextComponent message) {
        player.sendMessage(message);
    }

    /**
     * Sends a message to all players with the given permission.
     *
     * @param permission
     *            The permission players should have to receive the message.
     * @param messageKey
     *            The key for the message to be sent.
     * @param arguments
     *            Arguments to be added to the message.
     */
    public void messagePermission(final String permission, final String messageKey, final Iterable<? extends String> arguments) {
        this.messagePermission(permission, this.buildMessage(messageKey, arguments));
    }

    /**
     * Sends a message to all players with the given permission.
     *
     * @param permission
     *            The permission players should have to receive the message.
     * @param messageKey
     *            The key for the message to be sent.
     * @param arguments
     *            Arguments to be added to the message.
     */
    public void messagePermission(final String permission, final String messageKey, final String... arguments) {
        this.messagePermission(permission, this.buildMessage(messageKey, arguments));
    }

    /**
     * Sends a message to all players with the given permission.
     *
     * @param permission
     *            The permission players should have to receive the message.
     * @param messageKey
     *            The key for the message to be sent.
     * @param arguments
     *            Arguments to be added to the message.
     */
    public void messagePermission(final String permission, final TextComponent message) {
        final String permissionString = ConfigHelper.PERMISSION_PREFIX + permission;
        for (final ProxiedPlayer player : this.plugin.getProxy().getPlayers()) {
            if (player.hasPermission(permissionString)) {
                player.sendMessage(message);
            }
        }
    }

    /**
     * Sends a message to all players connected to the network.
     *
     * @param messageKey
     *            The key for the message to be sent.
     * @param arguments
     *            Arguments to be added to the message.
     */
    public void messageEveryone(final String messageKey, final Iterable<? extends String> arguments) {
        this.messageEveryone(this.buildMessage(messageKey, arguments));
    }

    /**
     * Sends a message to all players connected to the network.
     *
     * @param messageKey
     *            The key for the message to be sent.
     * @param arguments
     *            Arguments to be added to the message.
     */
    public void messageEveryone(final String messageKey, final String... arguments) {
        this.messageEveryone(this.buildMessage(messageKey, arguments));
    }

    /**
     * Sends a message to all players connected to the network.
     *
     * @param messageKey
     *            The key for the message to be sent.
     * @param arguments
     *            Arguments to be added to the message.
     */
    public void messageEveryone(final TextComponent message) {
        for (final ProxiedPlayer player : this.plugin.getProxy().getPlayers()) {
            player.sendMessage(message);
        }
    }

    /**
     * Builds a message for sending.
     *
     * @param messageKey
     *            The key for the message to be prepared.
     * @param arguments
     *            Arguments to be added to the message.
     * @return The message as a TextComponent.
     */
    public TextComponent buildMessage(final String messageKey, final String... arguments) {
        return this.buildMessage(messageKey, Arrays.asList(arguments));
    }

    /**
     * Builds a message for sending.
     *
     * @param messageKey
     *            The key for the message to be prepared.
     * @param arguments
     *            Arguments to be added to the message.
     * @return The message as a TextComponent.
     */
    public TextComponent buildMessage(final String messageKey, final Iterable<? extends String> arguments) {
        // StringBuilder which starts with the prefix
        final StringBuilder sb = new StringBuilder(this.messagePrefix);

        // Add the message
        sb.append(this.getConfigString(MESSAGE_KEY_PREFIX + messageKey, ""));

        // Add all arguments, delimited by spaces
        for (final String argument : arguments) {
            sb.append(" ");
            sb.append(argument);
        }

        // Return the message as a TextComponent
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', sb.toString()));
    }

    /**
     * Gets a string from the configuration file, or from the default
     * configuration file if not defined in the configuration file.
     */
    private String getConfigString(final String path, final String def) {
        // Get the string from the config file
        String str = this.plugin.getConfig().getString(path, null);

        // If the string is undefined, use the default config file
        if (str == null) {
            str = this.plugin.getDefaultConfig().getString(path, def);
        }

        return str;
    }
}
