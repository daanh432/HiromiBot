package nl.daanh.hiromibot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class EmbedUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(EmbedUtils.class);
    private static Supplier<EmbedBuilder> embedBuilderSupplier = EmbedBuilder::new;

    /**
     * Set default settings for the embed builder
     *
     * @param embedBuilderSupplier - Supply the default embed layout
     */
    public static void setEmbedBuilder(Supplier<EmbedBuilder> embedBuilderSupplier) {
        EmbedUtils.embedBuilderSupplier = embedBuilderSupplier;
        LOGGER.info("Default embed template has been set.");
    }

    /**
     * Send an embed with a description
     *
     * @param message - The message to display
     * @return The {@link EmbedBuilder}
     */
    public static EmbedBuilder embedMessage(String message) {
        return defaultEmbed().setDescription(message);
    }

    /**
     * Send an embed with a description and title
     *
     * @param message - The message to display
     * @param title   - The title for the embed
     * @return The {@link EmbedBuilder}
     */
    public static EmbedBuilder embedMessage(String message, String title) {
        return defaultEmbed().setTitle(title).setDescription(message);
    }

    /**
     * Send an embed with a description and title with url
     *
     * @param message - The message to display
     * @param title   - The title for the embed
     * @param url     - URL to map the title to
     * @return The {@link EmbedBuilder}
     */
    public static EmbedBuilder embedMessage(String message, String title, String url) {
        return defaultEmbed().setTitle(title, url).setDescription(message);
    }

    /**
     * Send an image to a channel
     *
     * @param imageUrl - URL of the image to link to the embed
     * @return The {@link EmbedBuilder}
     */
    public static EmbedBuilder embedImage(String imageUrl) {
        return defaultEmbed().setImage(imageUrl);
    }

    /**
     * Send an image to a channel with a title
     *
     * @param imageUrl - URL of the image to link to the embed
     * @param title    - Title to include with the embed
     * @return The {@link EmbedBuilder}
     */
    public static EmbedBuilder embedImage(String imageUrl, String title) {
        return defaultEmbed().setImage(imageUrl).setTitle(title);
    }

    /**
     * Send an image to a channel with a title and url
     *
     * @param imageUrl - URL of the image to link to the embed
     * @param title    - Title to include with the embed
     * @param url      - URL to map the title to
     * @return The {@link EmbedBuilder}
     */
    public static EmbedBuilder embedImage(String imageUrl, String title, String url) {
        return defaultEmbed().setImage(imageUrl).setTitle(title, url);
    }

    /**
     * Get the default embed
     *
     * @return The {@link EmbedBuilder}
     */
    public static EmbedBuilder defaultEmbed() {
        return embedBuilderSupplier.get();
    }
}
