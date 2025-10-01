package me.eternalhuman.packetboard.util.version;

import com.viaversion.viaversion.ViaVersionPlugin;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class VersionUtil {

    public static final int SERVER_VERSION = MinecraftProtocolVersion.getCurrentVersion();

    static {
        Bukkit.getLogger().info("[PacketBoard] Server version: "
                + MinecraftVersion.getCurrentVersion() + " (protocol " + SERVER_VERSION + ")");
        Bukkit.getLogger().info("[PacketBoard] Please report any bugs to the developer: https://github.com/EternalHuman/PacketBoard/issues");
    }

    public static int getPlayerVersion(@NonNull UUID id) {
        boolean isVia = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
        return isVia ? JavaPlugin.getPlugin(ViaVersionPlugin.class).getApi().getPlayerProtocolVersion(id).getVersion() : SERVER_VERSION;
    }
}
