package net.netheritearmourplus.effect;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.netheritearmourplus.NetheriteArmourPlus;
import net.netheritearmourplus.config.NapConfig;
import net.netheritearmourplus.permission.PermissionHelper;
import net.netheritearmourplus.player.PlayerPreferenceManager;
import net.netheritearmourplus.util.ArmoredElytraSupport;

import java.util.EnumSet;

/**
 * Applies and refreshes all managed effects for online players.
 */
public final class ArmorEffectService {

    // Java beacon effects on a full pyramid last 17 seconds.
    private static final int EFFECT_DURATION_TICKS = 340;
    private static final int REFRESH_THRESHOLD_TICKS = 80;

    private final PlayerPreferenceManager preferenceManager;

    public ArmorEffectService(PlayerPreferenceManager preferenceManager) {
        this.preferenceManager = preferenceManager;
    }

    public void tick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            refreshPlayer(player);
        }
    }

    public void refreshPlayer(ServerPlayer player) {
        NapConfig config = NetheriteArmourPlus.getConfig();
        if (!config.isEnabled()) {
            clearManagedEffects(player);
            return;
        }

        boolean hasArmorCombination = hasQualifiedArmorCombination(player);
        boolean hasFireProtectionSet = hasFullFireProtectionSet(player);
        EnumSet<NapEffectType> enabledEffects = preferenceManager.getEnabledEffects(player.getUUID());

        for (NapEffectType effect : NapEffectType.values()) {
            boolean hasRequiredArmor = hasArmorCombination
                    || effect == NapEffectType.FIRE_RESISTANCE && hasFireProtectionSet;
            boolean shouldApply = enabledEffects.contains(effect)
                    && hasRequiredArmor
                    && PermissionHelper.canUseEffect(player, config, effect);

            if (shouldApply) {
                ensureEffect(player, effect);
            } else {
                removeManagedEffect(player, effect);
            }
        }
    }

    public boolean hasQualifiedArmorCombination(Player player) {
        NapConfig config = NetheriteArmourPlus.getConfig();
        boolean hasHelmet = player.getItemBySlot(EquipmentSlot.HEAD).is(Items.NETHERITE_HELMET);
        boolean hasLeggings = player.getItemBySlot(EquipmentSlot.LEGS).is(Items.NETHERITE_LEGGINGS);
        boolean hasBoots = player.getItemBySlot(EquipmentSlot.FEET).is(Items.NETHERITE_BOOTS);
        if (!hasHelmet || !hasLeggings || !hasBoots) {
            return false;
        }

        ItemStack chestItem = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestItem.is(Items.NETHERITE_CHESTPLATE)) {
            return true;
        }

        return config.isArmoredElytraSupport() && ArmoredElytraSupport.isNetheriteArmoredElytra(chestItem);
    }

    public boolean hasQualifiedFireResistanceArmor(ServerPlayer player) {
        return hasQualifiedArmorCombination(player) || hasFullFireProtectionSet(player);
    }

    private boolean hasFullFireProtectionSet(ServerPlayer player) {
        Holder<Enchantment> fireProtection = player.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.FIRE_PROTECTION);

        return hasFireProtectionLevelFour(player.getItemBySlot(EquipmentSlot.HEAD), fireProtection)
                && hasFireProtectionLevelFour(player.getItemBySlot(EquipmentSlot.CHEST), fireProtection)
                && hasFireProtectionLevelFour(player.getItemBySlot(EquipmentSlot.LEGS), fireProtection)
                && hasFireProtectionLevelFour(player.getItemBySlot(EquipmentSlot.FEET), fireProtection);
    }

    private boolean hasFireProtectionLevelFour(ItemStack stack, Holder<Enchantment> fireProtection) {
        return !stack.isEmpty() && stack.getEnchantments().getLevel(fireProtection) >= 4;
    }

    private void ensureEffect(ServerPlayer player, NapEffectType effect) {
        MobEffectInstance current = player.getEffect(effect.effectHolder());
        if (current != null && current.getAmplifier() == effect.amplifier()) {
            if (!current.endsWithin(REFRESH_THRESHOLD_TICKS)) {
                return;
            }
        }

        player.addEffect(new MobEffectInstance(
                effect.effectHolder(),
                EFFECT_DURATION_TICKS,
                effect.amplifier(),
                false,
                false,
                false
        ));
    }

    private void clearManagedEffects(ServerPlayer player) {
        for (NapEffectType effect : NapEffectType.values()) {
            removeManagedEffect(player, effect);
        }
    }

    private void removeManagedEffect(ServerPlayer player, NapEffectType effect) {
        MobEffectInstance current = player.getEffect(effect.effectHolder());
        if (current == null) {
            return;
        }

        // Only remove instances that match the signature we apply ourselves,
        // so we do not wipe out unrelated potions or beacon effects.
        if (current.getAmplifier() != effect.amplifier()) {
            return;
        }
        if (current.isAmbient()) {
            return;
        }
        if (current.isVisible()) {
            return;
        }
        if (!current.endsWithin(EFFECT_DURATION_TICKS)) {
            return;
        }

        player.removeEffect(effect.effectHolder());
    }
}
