package net.netheritearmourplus.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.network.chat.Component;

/**
 * Optional Mod Menu integration. Cloth Config stays optional too.
 */
public final class NetheriteArmourPlusModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
            return parent -> null;
        }

        return parent -> {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.getCurrentServer() != null && !minecraft.hasSingleplayerServer()) {
                return new AlertScreen(
                        () -> minecraft.setScreen(parent),
                        Component.literal("NetheriteArmourPlus Config"),
                        Component.literal("NetheriteArmourPlus uses a server-side config. While connected to a multiplayer server, Mod Menu on your client cannot change the server's settings. Edit the server config directly or use this screen in singleplayer.")
                );
            }

            return NetheriteArmourPlusClothConfigScreen.create(parent);
        };
    }
}
