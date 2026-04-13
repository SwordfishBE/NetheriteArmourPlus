package net.netheritearmourplus.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.netheritearmourplus.NetheriteArmourPlus;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * All player-toggleable effects supported by /nap.
 */
public enum NapEffectType {
    HASTE_1("haste1", "Haste 1", 0, false, () -> MobEffects.HASTE),
    HASTE_2("haste2", "Haste 2", 1, false, () -> MobEffects.HASTE),
    SPEED_1("speed1", "Speed 1", 0, false, () -> MobEffects.SPEED),
    SPEED_2("speed2", "Speed 2", 1, false, () -> MobEffects.SPEED),
    JUMP_BOOST_1("jump_boost1", "Jump Boost 1", 0, false, () -> MobEffects.JUMP_BOOST),
    JUMP_BOOST_2("jump_boost2", "Jump Boost 2", 1, false, () -> MobEffects.JUMP_BOOST),
    FIRE_RESISTANCE("fire_resistance", "Fire Resistance", 0, false, () -> MobEffects.FIRE_RESISTANCE),
    WATER_BREATHING("water_breathing", "Water Breathing", 0, false, () -> MobEffects.WATER_BREATHING),
    INVISIBILITY("invisibility", "Invisibility", 0, false, () -> MobEffects.INVISIBILITY),
    SLOW_FALLING("slow_falling", "Slow Falling", 0, false, () -> MobEffects.SLOW_FALLING),
    NIGHT_VISION("night_vision", "Night Vision", 0, false, () -> MobEffects.NIGHT_VISION);

    private final String commandName;
    private final String displayName;
    private final String permissionNode;
    private final int amplifier;
    private final boolean requiresNetheriteBoots;
    private final Supplier<Holder<MobEffect>> effectHolderSupplier;

    NapEffectType(
            String commandName,
            String displayName,
            int amplifier,
            boolean requiresNetheriteBoots,
            Supplier<Holder<MobEffect>> effectHolderSupplier
    ) {
        this.commandName = commandName;
        this.displayName = displayName;
        this.permissionNode = NetheriteArmourPlus.MOD_ID + ".effect." + commandName;
        this.amplifier = amplifier;
        this.requiresNetheriteBoots = requiresNetheriteBoots;
        this.effectHolderSupplier = effectHolderSupplier;
    }

    public String commandName() {
        return commandName;
    }

    public String displayName() {
        return displayName;
    }

    public String permissionNode() {
        return permissionNode;
    }

    public int amplifier() {
        return amplifier;
    }

    public boolean requiresNetheriteBoots() {
        return requiresNetheriteBoots;
    }

    public Holder<MobEffect> effectHolder() {
        return effectHolderSupplier.get();
    }

    public String storageId() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static NapEffectType fromStorageId(String id) {
        return Arrays.stream(values())
                .filter(effect -> effect.storageId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }
}
