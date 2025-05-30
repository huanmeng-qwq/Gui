package me.huanmeng.gui.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class AdventureUtil {
    public static Class<?> componentClass;
    private static Class<?> componentSerializerClass;
    private static Class<?> gsonComponentSerializerClass;
    private static Object gsonComponentSerializerInstance;
    private static MethodHandle componentSerialize;
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
        }
    }

    public static Object toComponent(Component component) {
        try {
            final String jsonString = GsonComponentSerializer.gson().serialize(component);
            return componentDeserialize.bindTo(gsonComponentSerializerInstance).invoke(jsonString);
        } catch (Throwable e) {
            return null;
        }
    }

    public static String toLegacyString(Component component) {
        return LegacyComponentSerializer.legacySection().serialize(component);
    }
}
