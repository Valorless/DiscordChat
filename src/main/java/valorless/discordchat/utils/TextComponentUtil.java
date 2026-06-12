package valorless.discordchat.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * Utility class for reading string values from Adventure {@link Component} objects.
 */
public class TextComponentUtil {

    private TextComponentUtil() {}

    /**
     * Extracts the plain text content of a {@link Component}, stripping all formatting
     * (colors, decorations, click/hover events, etc.).
     *
     * @param component the component to read; returns an empty string if {@code null}
     * @return the plain text string of the component
     */
    public static String toPlainText(Component component) {
        if (component == null) return "";
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    /**
     * Serializes a {@link Component} to a legacy {@code &}-prefixed color-coded string
     * (e.g. {@code &aHello &bWorld}).
     *
     * @param component the component to serialize; returns an empty string if {@code null}
     * @return the legacy-formatted string using {@code &} color codes
     */
    public static String toLegacyAmpersand(Component component) {
        if (component == null) return "";
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    /**
     * Serializes a {@link Component} to a legacy {@code §}-prefixed color-coded string
     * (e.g. {@code §aHello §bWorld}).
     *
     * @param component the component to serialize; returns an empty string if {@code null}
     * @return the legacy-formatted string using {@code §} section-sign color codes
     */
    public static String toLegacySection(Component component) {
        if (component == null) return "";
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
}

