package de.timing.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Utility class for message formatting using MiniMessage.
 */
public class MessageUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    /**
     * Parse a MiniMessage string into a Component.
     */
    public static Component parse(String message) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(message);
    }

    /**
     * Parse a MiniMessage string with placeholder replacement.
     */
    public static Component parse(String message, String placeholder, String value) {
        if (message == null || message.isEmpty()) {
            return Component.empty();
        }
        return MINI_MESSAGE.deserialize(message.replace(placeholder, value));
    }

    /**
     * Parse a MiniMessage string with time placeholder.
     */
    public static Component parseWithTime(String message, int seconds) {
        return parse(message, "{time}", formatTime(seconds));
    }

    /**
     * Format seconds into a human-readable time string.
     */
    public static String formatTime(int totalSeconds) {
        if (totalSeconds < 0) {
            return "0s";
        }
        
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0) {
            sb.append(minutes).append("m ");
        }
        sb.append(seconds).append("s");

        return sb.toString().trim();
    }

    /**
     * Convert legacy color codes (&) to MiniMessage format.
     */
    public static String legacyToMiniMessage(String legacy) {
        if (legacy == null || legacy.isEmpty()) {
            return "";
        }
        Component component = LEGACY_SERIALIZER.deserialize(legacy);
        return MINI_MESSAGE.serialize(component);
    }

    /**
     * Serialize a Component to MiniMessage string.
     */
    public static String serialize(Component component) {
        if (component == null) {
            return "";
        }
        return MINI_MESSAGE.serialize(component);
    }

    /**
     * Create a simple colored message.
     */
    public static Component color(String color, String message) {
        return parse("<" + color + ">" + message + "</" + color + ">");
    }

    /**
     * Create a prefix for plugin messages.
     */
    public static Component prefix() {
        return parse("<gray>[<gradient:gold:yellow>Timing</gradient>]</gray> ");
    }

    /**
     * Create a prefixed message.
     */
    public static Component prefixed(String message) {
        return prefix().append(parse(message));
    }

    /**
     * Create an error message.
     */
    public static Component error(String message) {
        return prefixed("<red>" + message + "</red>");
    }

    /**
     * Create a success message.
     */
    public static Component success(String message) {
        return prefixed("<green>" + message + "</green>");
    }

    /**
     * Create an info message.
     */
    public static Component info(String message) {
        return prefixed("<yellow>" + message + "</yellow>");
    }
}
