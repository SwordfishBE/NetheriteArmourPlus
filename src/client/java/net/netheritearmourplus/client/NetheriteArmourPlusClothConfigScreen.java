package net.netheritearmourplus.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.netheritearmourplus.NetheriteArmourPlus;
import net.netheritearmourplus.config.NapConfig;

/**
 * Builds the optional Cloth Config screen for Mod Menu.
 */
public final class NetheriteArmourPlusClothConfigScreen {

    private NetheriteArmourPlusClothConfigScreen() {
    }

    public static Screen create(Screen parent) {
        NapConfig editedConfig = NetheriteArmourPlus.loadConfigForEditing();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("NetheriteArmourPlus Config"));

        ConfigEntryBuilder entries = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

        general.addEntry(entries.startBooleanToggle(
                        Component.literal("Enable NetheriteArmourPlus"),
                        editedConfig.isEnabled())
                .setDefaultValue(true)
                .setTooltip(Component.literal("Master toggle for all NetheriteArmourPlus effects."))
                .setSaveConsumer(editedConfig::setEnabled)
                .build());

        general.addEntry(entries.startBooleanToggle(
                        Component.literal("Use LuckPerms effect permissions"),
                        editedConfig.isUseLuckPerms())
                .setDefaultValue(false)
                .setTooltip(Component.literal("When enabled and LuckPerms is installed, each /nap effect uses its own permission node. Otherwise the mod is OP only."))
                .setSaveConsumer(editedConfig::setUseLuckPerms)
                .build());

        builder.setSavingRunnable(() -> NetheriteArmourPlus.applyEditedConfig(editedConfig));
        return builder.build();
    }
}
