package net.netheritearmourplus.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.netheritearmourplus.NetheriteArmourPlus;
import net.netheritearmourplus.effect.NapEffectType;
import net.netheritearmourplus.permission.PermissionHelper;

import java.util.EnumSet;
import java.util.StringJoiner;

/**
 * Registers /nap and its subcommands.
 */
public final class NapCommand {

    private NapCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var root = Commands.literal("nap")
                .executes(NapCommand::executeInfo)
                .then(Commands.literal("clear")
                        .executes(NapCommand::executeClear))
                .then(Commands.literal("reload")
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(NapCommand::executeReload))
                .then(Commands.literal("list")
                        .executes(NapCommand::executeInfo));

        for (NapEffectType effect : NapEffectType.values()) {
            root.then(Commands.literal(effect.commandName())
                    .then(Commands.argument("enabled", BoolArgumentType.bool())
                            .executes(context -> executeToggle(context, effect))));
        }

        dispatcher.register(root);
    }

    private static int executeInfo(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.literal(NetheriteArmourPlus.getLogPrefix() + " This command can only be used by a player."));
            return 0;
        }

        boolean armorActive = NetheriteArmourPlus.getArmorEffectService().hasQualifiedArmorCombination(player);
        boolean luckPermsMode = PermissionHelper.isUsingLuckPerms(NetheriteArmourPlus.getConfig());
        EnumSet<NapEffectType> enabledEffects = NetheriteArmourPlus.getPreferenceManager().getEnabledEffects(player.getUUID());

        context.getSource().sendSuccess(() -> Component.literal(
                "§6" + NetheriteArmourPlus.getLogPrefix() + "§r Armor combo: " + (armorActive ? "§aACTIVE" : "§cINACTIVE")
        ), false);
        context.getSource().sendSuccess(() -> Component.literal(
                "§6" + NetheriteArmourPlus.getLogPrefix() + "§r Permission mode: " + (luckPermsMode ? "§bLuckPerms" : "§eOP only")
        ), false);
        context.getSource().sendSuccess(() -> Component.literal(
                "§6" + NetheriteArmourPlus.getLogPrefix() + "§r Selected effects: §f" + formatSelectedEffects(enabledEffects)
        ), false);
        context.getSource().sendSuccess(() -> Component.literal(
                "§6" + NetheriteArmourPlus.getLogPrefix() + "§r Available keys: §7" + formatAvailableEffects()
        ), false);
        return 1;
    }

    private static int executeReload(CommandContext<CommandSourceStack> context) {
        NetheriteArmourPlus.reloadConfig();
        context.getSource().sendSuccess(() -> Component.literal(
                "§6" + NetheriteArmourPlus.getLogPrefix() + "§r Config reloaded."
        ), true);
        return 1;
    }

    private static int executeClear(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.literal(NetheriteArmourPlus.getLogPrefix() + " This command can only be used by a player."));
            return 0;
        }

        NetheriteArmourPlus.getPreferenceManager().clear(player.getUUID());
        NetheriteArmourPlus.getArmorEffectService().refreshPlayer(player);

        context.getSource().sendSuccess(() -> Component.literal(
                "§6" + NetheriteArmourPlus.getLogPrefix() + "§r All NetheriteArmourPlus effects have been cleared."
        ), false);
        return 1;
    }

    private static int executeToggle(CommandContext<CommandSourceStack> context, NapEffectType effect) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.literal(NetheriteArmourPlus.getLogPrefix() + " This command can only be used by a player."));
            return 0;
        }

        if (!PermissionHelper.canUseEffect(player, NetheriteArmourPlus.getConfig(), effect)) {
            context.getSource().sendFailure(Component.literal(
                    NetheriteArmourPlus.getLogPrefix() + " You do not have permission for " + effect.displayName() + "."
            ));
            return 0;
        }

        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        NetheriteArmourPlus.getPreferenceManager().setEnabled(player.getUUID(), effect, enabled);
        NetheriteArmourPlus.getArmorEffectService().refreshPlayer(player);

        context.getSource().sendSuccess(() -> Component.literal(
                "§6" + NetheriteArmourPlus.getLogPrefix() + "§r " + effect.displayName() + " " + (enabled ? "§aenabled" : "§cdisabled") + "§r."
        ), false);

        if (enabled && !NetheriteArmourPlus.getArmorEffectService().hasQualifiedArmorCombination(player)) {
            context.getSource().sendSuccess(() -> Component.literal(
                    "§6" + NetheriteArmourPlus.getLogPrefix() + "§r The effect is saved and will activate when your armor combination is valid."
            ), false);
        }

        return 1;
    }

    private static String formatSelectedEffects(EnumSet<NapEffectType> enabledEffects) {
        if (enabledEffects.isEmpty()) {
            return "none";
        }

        StringJoiner joiner = new StringJoiner(", ");
        for (NapEffectType effect : enabledEffects) {
            joiner.add(effect.displayName());
        }
        return joiner.toString();
    }

    private static String formatAvailableEffects() {
        StringJoiner joiner = new StringJoiner(", ");
        for (NapEffectType effect : NapEffectType.values()) {
            joiner.add(effect.commandName());
        }
        return joiner.toString();
    }
}
