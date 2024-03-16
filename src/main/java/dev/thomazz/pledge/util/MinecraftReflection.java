package dev.thomazz.pledge.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.Arrays;

@UtilityClass
public class MinecraftReflection {
    private final String BASE = Bukkit.getServer().getClass().getPackage().getName();
    private final String NMS = MinecraftReflection.BASE.replace("org.bukkit.craftbukkit", "net.minecraft.server");

    public Class<?> gamePacket(String className) throws Exception {
        try {
            return Class.forName(MinecraftReflection.NMS + "." + className); // Legacy structure
        } catch (Exception ignored) {
            return Class.forName("net.minecraft.network.protocol.game." + className);
        }
    }

    public Class<?> getMinecraftClass(String... names) {
        String[] packageNames = new String[] {
            MinecraftReflection.getMinecraftPackage(),
            MinecraftReflection.getMinecraftPackageLegacy()
        };

        for (String packageName : packageNames) {
            for(String name : names) {
                try {
                    return Class.forName(packageName + "." + name);
                } catch (Throwable ignored) {
                }
            }
        }

        throw new RuntimeException("Could not find minecraft class: " + Arrays.toString(names));
    }

    public String getCraftBukkitPackage() {
        return Bukkit.getServer().getClass().getPackage().getName();
    }

    public String getMinecraftPackage() {
        return "net.minecraft";
    }

    public String getMinecraftPackageLegacy() {
        return MinecraftReflection.getCraftBukkitPackage().replace("org.bukkit.craftbukkit", "net.minecraft.server");
    }

    public Object getServerConnection() throws Exception {
        Object minecraftServer = Bukkit.getServer().getClass().getDeclaredMethod("getServer").invoke(Bukkit.getServer());
        Field connectionField = ReflectionUtil.getFieldByClassNames(minecraftServer.getClass().getSuperclass(), "ServerConnection");
        return connectionField.get(minecraftServer);
    }
}
