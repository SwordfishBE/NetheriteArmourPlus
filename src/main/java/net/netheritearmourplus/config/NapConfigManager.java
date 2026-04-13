package net.netheritearmourplus.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import net.netheritearmourplus.NetheriteArmourPlus;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Reads and writes the main mod config file.
 */
public final class NapConfigManager {

    private static final String CONFIG_FILE_NAME = "netheritearmourplus.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_FILE_NAME);

    public NapConfig load() {
        if (!Files.exists(configPath)) {
            NapConfig defaults = new NapConfig();
            save(defaults);
            return defaults;
        }

        try (Reader reader = Files.newBufferedReader(configPath)) {
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.setLenient(true);

            NapConfig config = GSON.fromJson(jsonReader, NapConfig.class);
            if (config == null) {
                NetheriteArmourPlus.LOGGER.warn("{} Config file was empty or invalid. Using defaults.", NetheriteArmourPlus.getLogPrefix());
                config = new NapConfig();
            }

            config.validate();
            save(config);
            NetheriteArmourPlus.LOGGER.debug("{} Config loaded: {}", NetheriteArmourPlus.getLogPrefix(), config);
            return config;
        } catch (IOException exception) {
            NetheriteArmourPlus.LOGGER.warn("{} Failed to read config file. Using defaults.", NetheriteArmourPlus.getLogPrefix(), exception);
            return new NapConfig();
        }
    }

    public void save(NapConfig config) {
        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                writer.write(buildFileContents(config));
            }
            NetheriteArmourPlus.LOGGER.debug("{} Config saved to {}", NetheriteArmourPlus.getLogPrefix(), configPath);
        } catch (IOException exception) {
            NetheriteArmourPlus.LOGGER.warn("{} Failed to save config file.", NetheriteArmourPlus.getLogPrefix(), exception);
        }
    }

    private String buildFileContents(NapConfig config) {
        return """
                {
                  "enabled": %s, // Master toggle for the mod. When false, no NAP effects are applied.
                  "useLuckPerms": %s // When true and LuckPerms is installed, each effect uses its own permission node. Otherwise the mod is OP only.
                }
                """.formatted(
                config.isEnabled(),
                config.isUseLuckPerms()
        );
    }
}
