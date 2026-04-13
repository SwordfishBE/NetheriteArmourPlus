package net.netheritearmourplus.permission;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import net.netheritearmourplus.config.NapConfig;
import net.netheritearmourplus.effect.NapEffectType;

/**
 * Resolves whether a player is allowed to use a specific effect.
 */
public final class PermissionHelper {

    private static final boolean LUCKPERMS_AVAILABLE = FabricLoader.getInstance().isModLoaded("luckperms");

    private PermissionHelper() {
    }

    public static boolean isLuckPermsAvailable() {
        return LUCKPERMS_AVAILABLE;
    }

    public static boolean isUsingLuckPerms(NapConfig config) {
        return config.isUseLuckPerms() && LUCKPERMS_AVAILABLE;
    }

    public static boolean canUseEffect(ServerPlayer player, NapConfig config, NapEffectType effect) {
        if (isUsingLuckPerms(config)) {
            return Permissions.check(player, effect.permissionNode(), false);
        }

        return net.minecraft.commands.Commands.LEVEL_GAMEMASTERS.check(player.permissions());
    }
}
