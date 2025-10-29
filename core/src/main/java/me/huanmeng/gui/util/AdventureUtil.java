package me.huanmeng.gui.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Utility class for bridging between Kyori Adventure components and legacy Bukkit text systems.
 * <p>
 * This class provides compatibility between the modern Adventure API (used in Paper and modern Bukkit versions)
 * and older servers that don't natively support Adventure components. It uses reflection to dynamically load
 * Adventure classes and provides conversion methods.
 *
 * <p>
 * <b>Key Features:</b>
 * <ul>
 *   <li>Convert Adventure {@link Component} to platform-native component objects</li>
 *   <li>Convert Adventure components to legacy formatted strings (with § color codes)</li>
 *   <li>Automatic detection of Adventure API availability</li>
 * </ul>
 *
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * Component component = Component.text("Hello ").append(Component.text("World").color(NamedTextColor.RED));
 *
 * // Convert to platform component (for Paper API)
 * Object platformComponent = AdventureUtil.toComponent(component);
 *
 * // Convert to legacy string (for older Bukkit)
 * String legacyString = AdventureUtil.toLegacyString(component); // "Hello §cWorld"
 * }</pre>
 *
 * <p>
 * <b>Implementation Notes:</b> This class uses MethodHandles and reflection to avoid hard
 * dependencies on Adventure classes that may not be present on all server platforms. The reflection
 * is performed once during class loading for optimal performance.
 *
 *
 * @author huanmeng_qwq
 * @see Component
 * @see net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
 * @see net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
 */
public class AdventureUtil {
    /** The Adventure Component class, loaded via reflection */
    public static Class<?> componentClass;
    /** The ComponentSerializer interface class */
    private static Class<?> componentSerializerClass;
    /** The GsonComponentSerializer class */
    private static Class<?> gsonComponentSerializerClass;
    /** Singleton instance of GsonComponentSerializer */
    private static Object gsonComponentSerializerInstance;
    /** MethodHandle for serializing components to JSON */
    private static MethodHandle componentSerialize;
    /** MethodHandle for deserializing JSON to components */
    private static MethodHandle componentDeserialize;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            componentClass = Class.forName("net{}kyori{}adventure{}text{}Component".replace("{}", "."));
            componentSerializerClass = Class.forName("net{}kyori{}adventure{}text{}serializer{}ComponentSerializer".replace("{}", "."));
            gsonComponentSerializerClass = Class.forName("net{}kyori{}adventure{}text{}serializer{}gson{}GsonComponentSerializer".replace("{}", "."));

            gsonComponentSerializerInstance = gsonComponentSerializerClass.getMethod("gson").invoke(null);
            componentSerialize = lookup.unreflect(componentSerializerClass.getDeclaredMethod("serialize", componentClass));
            componentDeserialize = lookup.unreflect(componentSerializerClass.getMethod("deserialize", Object.class));

        } catch (Exception ignored) {
            // Adventure API not available on this platform
        }
    }

    /**
     * Converts an Adventure {@link Component} to the platform's native component type.
     * <p>
     * This method is useful for passing Adventure components to Paper API methods that expect
     * platform component objects. The conversion is performed via JSON serialization/deserialization.
     *
     * <p>
     * If the provided component is already an instance of the platform's component class,
     * it is returned unchanged.
     *
     *
     * @param component the Adventure component to convert, never null
     * @return the platform-native component object, or null if conversion fails or Adventure is unavailable
     */
    public static Object toComponent(Component component) {
        if (componentClass.isInstance(component)) {
            return component;
        }
        try {
            final String jsonString = GsonComponentSerializer.gson().serialize(component);
            return componentDeserialize.bindTo(gsonComponentSerializerInstance).invoke(jsonString);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * Converts an Adventure {@link Component} to a legacy formatted string with section symbols (§).
     * <p>
     * This method serializes the component using Adventure's legacy serializer, which converts
     * modern text formatting (colors, decorations, etc.) into Minecraft's legacy format codes.
     *
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * Component comp = Component.text("Error: ").color(NamedTextColor.RED)
     *                           .append(Component.text("Something went wrong"));
     * String legacy = AdventureUtil.toLegacyString(comp);
     * // Result: "§cError: §rSomething went wrong"
     * }</pre>
     *
     *
     * @param component the Adventure component to convert, never null
     * @return the legacy formatted string with § color codes, never null
     */
    public static String toLegacyString(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
}
