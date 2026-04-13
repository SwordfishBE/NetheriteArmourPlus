package net.netheritearmourplus;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.netheritearmourplus.command.NapCommand;
import net.netheritearmourplus.config.NapConfig;
import net.netheritearmourplus.config.NapConfigManager;
import net.netheritearmourplus.effect.ArmorEffectService;
import net.netheritearmourplus.player.PlayerPreferenceManager;
import net.netheritearmourplus.permission.PermissionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main mod entrypoint.
 */
public final class NetheriteArmourPlus implements ModInitializer {

    public static final String MOD_ID = "netheritearmourplus";
    public static final String MOD_NAME = "NetheriteArmourPlus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static NapConfigManager configManager;
    private static PlayerPreferenceManager preferenceManager;
    private static ArmorEffectService armorEffectService;
    private static NapConfig config;

    @Override
    public void onInitialize() {
        configManager = new NapConfigManager();
        config = configManager.load();
        preferenceManager = new PlayerPreferenceManager();
        preferenceManager.load();
        armorEffectService = new ArmorEffectService(preferenceManager);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                NapCommand.register(dispatcher)
        );
        ServerTickEvents.END_SERVER_TICK.register(server -> armorEffectService.tick(server));

        logPermissionMode();
        LOGGER.info("{} Mod initialized. Version: {}", getLogPrefix(), getModVersion());
    }

    public static NapConfig getConfig() {
        return config;
    }

    public static PlayerPreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    public static ArmorEffectService getArmorEffectService() {
        return armorEffectService;
    }

    public static NapConfig loadConfigForEditing() {
        NapConfig editableConfig = new NapConfig();
        editableConfig.setEnabled(config.isEnabled());
        editableConfig.setUseLuckPerms(config.isUseLuckPerms());
        return editableConfig;
    }

    public static void applyEditedConfig(NapConfig editedConfig) {
        editedConfig.validate();
        configManager.save(editedConfig);
        config = editedConfig;
        logPermissionMode();
    }

    public static void reloadConfig() {
        config = configManager.load();
        logPermissionMode();
    }

    public static String getLogPrefix() {
        return "[" + MOD_NAME + "]";
    }

    public static String getModVersion() {
        return FabricLoader.getInstance()
                .getModContainer(MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private static void logPermissionMode() {
        if (config.isUseLuckPerms() && !PermissionHelper.isLuckPermsAvailable()) {
            LOGGER.warn("{} LuckPerms mode is enabled in config, but LuckPerms is not installed. Falling back to OP only.", getLogPrefix());
            return;
        }

        if (PermissionHelper.isUsingLuckPerms(config)) {
            LOGGER.debug("{} Using LuckPerms effect permissions.", getLogPrefix());
            return;
        }

        LOGGER.debug("{} Using OP-only effect permissions.", getLogPrefix());
    }
}
