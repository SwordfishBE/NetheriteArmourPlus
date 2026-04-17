package net.netheritearmourplus.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;
import net.netheritearmourplus.NetheriteArmourPlus;
import net.netheritearmourplus.effect.NapEffectType;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Persists player-selected effects between sessions.
 */
public final class PlayerPreferenceManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "netheritearmourplus-player-settings.json";

    private final Path filePath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    private final Map<UUID, EnumSet<NapEffectType>> enabledEffectsByPlayer = new LinkedHashMap<>();

    public void load() {
        enabledEffectsByPlayer.clear();

        if (!Files.exists(filePath)) {
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(filePath)) {
            StoredPreferences storedPreferences = GSON.fromJson(reader, StoredPreferences.class);
            if (storedPreferences == null || storedPreferences.players == null) {
                return;
            }

            for (Map.Entry<String, Set<String>> entry : storedPreferences.players.entrySet()) {
                try {
                    UUID playerId = UUID.fromString(entry.getKey());
                    Set<String> storedEffects = entry.getValue();
                    if (storedEffects == null) {
                        NetheriteArmourPlus.LOGGER.warn("{} Ignoring null effect list for player '{}' in saved preferences.", NetheriteArmourPlus.getLogPrefix(), entry.getKey());
                        continue;
                    }

                    EnumSet<NapEffectType> effects = EnumSet.noneOf(NapEffectType.class);
                    for (String effectId : storedEffects) {
                        if (effectId == null) {
                            NetheriteArmourPlus.LOGGER.warn("{} Ignoring null effect entry for player '{}' in saved preferences.", NetheriteArmourPlus.getLogPrefix(), entry.getKey());
                            continue;
                        }

                        NapEffectType effect = NapEffectType.fromStorageId(effectId);
                        if (effect != null) {
                            effects.add(effect);
                        }
                    }
                    if (!effects.isEmpty()) {
                        enabledEffectsByPlayer.put(playerId, effects);
                    }
                } catch (IllegalArgumentException ignored) {
                    NetheriteArmourPlus.LOGGER.warn("{} Ignoring invalid player UUID '{}' in saved preferences.", NetheriteArmourPlus.getLogPrefix(), entry.getKey());
                }
            }

            NetheriteArmourPlus.LOGGER.debug("{} Loaded {} player preference entries.", NetheriteArmourPlus.getLogPrefix(), enabledEffectsByPlayer.size());
        } catch (IOException | JsonParseException | IllegalStateException exception) {
            NetheriteArmourPlus.LOGGER.warn("{} Failed to load player preferences.", NetheriteArmourPlus.getLogPrefix(), exception);
        }
    }

    public EnumSet<NapEffectType> getEnabledEffects(UUID playerId) {
        EnumSet<NapEffectType> effects = enabledEffectsByPlayer.get(playerId);
        return effects == null ? EnumSet.noneOf(NapEffectType.class) : EnumSet.copyOf(effects);
    }

    public void setEnabled(UUID playerId, NapEffectType effect, boolean enabled) {
        EnumSet<NapEffectType> effects = enabledEffectsByPlayer.computeIfAbsent(playerId, ignored -> EnumSet.noneOf(NapEffectType.class));

        if (enabled) {
            effects.add(effect);
        } else {
            effects.remove(effect);
            if (effects.isEmpty()) {
                enabledEffectsByPlayer.remove(playerId);
            }
        }

        save();
    }

    public void clear(UUID playerId) {
        enabledEffectsByPlayer.remove(playerId);
        save();
    }

    private void save() {
        StoredPreferences storedPreferences = new StoredPreferences();
        for (Map.Entry<UUID, EnumSet<NapEffectType>> entry : enabledEffectsByPlayer.entrySet()) {
            Set<String> effects = new LinkedHashSet<>();
            for (NapEffectType effect : entry.getValue()) {
                effects.add(effect.storageId());
            }
            storedPreferences.players.put(entry.getKey().toString(), effects);
        }

        try {
            Files.createDirectories(filePath.getParent());
            try (Writer writer = Files.newBufferedWriter(filePath)) {
                GSON.toJson(storedPreferences, writer);
            }
            NetheriteArmourPlus.LOGGER.debug("{} Saved player preferences to {}", NetheriteArmourPlus.getLogPrefix(), filePath);
        } catch (IOException exception) {
            NetheriteArmourPlus.LOGGER.warn("{} Failed to save player preferences.", NetheriteArmourPlus.getLogPrefix(), exception);
        }
    }

    private static final class StoredPreferences {
        private final Map<String, Set<String>> players = new LinkedHashMap<>();
    }
}
