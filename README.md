# NetheriteArmourPlus

`NetheriteArmourPlus` is a Fabric mod for Minecraft that lets players toggle special effects while wearing specific Netherite armor combinations.

## Features

- Players can enable or disable each effect individually with `/nap`.
- Player preferences are saved, so selected effects stay enabled after relogging.
- Effects only apply while the player is wearing a valid armor combination.
- Works in singleplayer and on dedicated servers.
- Supports optional LuckPerms permissions per effect.
- Supports optional Mod Menu + Cloth Config integration on the client.

## Valid Armor Combinations

Effects only apply when the player wears one of these combinations:

- `Netherite Helmet + Netherite Chestplate + Netherite Leggings + Netherite Boots`
- `Netherite Helmet + Netherite Armored Elytra + Netherite Leggings + Netherite Boots`

If one or more required armor pieces are removed, the effects are removed immediately.

`NetheriteArmourPlus` supports Netherite armored elytra items created by the [**Armored Elytra**](https://modrinth.com/datapack/elytra-armor) mod by DorkixAzIgazi, the [**Armored Elytra**](https://www.vanillatweaks.net) datapack by Vanilla Tweaks and the [**Plated_Elytra**](https://mc.voodoobeard.com) datapack by VoodooBeard.

## Available Effects

These effects can be toggled with `/nap`:

- `haste1`
- `haste2`
- `speed1`
- `speed2`
- `jump_boost1`
- `jump_boost2`
- `fire_resistance`
- `water_breathing`
- `invisibility`
- `slow_falling`
- `night_vision`

## Commands

### Player commands

```text
/nap
/nap list
/nap clear
/nap <effect> true
/nap <effect> false
```

Examples:

```text
/nap speed1 true
/nap night_vision true
/nap clear
```

### Admin command

```text
/nap reload
```

Reloads the config file from disk.

## Permissions

If `useLuckPerms` is set to `false`, the mod is `OP only`.

If `useLuckPerms` is set to `true` and LuckPerms is installed, players need the matching permission node for each effect they want to use:

```text
netheritearmourplus.effect.haste1
netheritearmourplus.effect.haste2
netheritearmourplus.effect.speed1
netheritearmourplus.effect.speed2
netheritearmourplus.effect.jump_boost1
netheritearmourplus.effect.jump_boost2
netheritearmourplus.effect.fire_resistance
netheritearmourplus.effect.water_breathing
netheritearmourplus.effect.invisibility
netheritearmourplus.effect.slow_falling
netheritearmourplus.effect.night_vision
```

## Config

Config file:

```text
config/netheritearmourplus.json
```

Default config:

```json
{
  "enabled": true,
  "useLuckPerms": false,
  "armoredElytraSupport": true
}
```

### Config options

- `enabled`: Master toggle for the mod.
- `useLuckPerms`: If `true`, LuckPerms permission nodes are used when LuckPerms is installed. Otherwise the mod falls back to OP-only access.
- `armoredElytraSupport`: If `true`, supported Netherite armored elytra items count as the required chest piece.

## Optional Client Integration

- `Mod Menu` is optional.
- `Cloth Config` is optional.
- Dedicated servers do not need either dependency.
- Clients without Cloth Config can still use the mod normally, they just do not get the config GUI.

Result:

- Singleplayer + Cloth Config: full config screen
- Dedicated server: works without Cloth Config
- Client without Cloth Config: mod still works, just no GUI

## Building from Source

```bash
git clone https://github.com/SwordfishBE/NetheriteArmourPlus.git
cd NetheriteArmourPlus
chmod +x gradlew
./gradlew build
# Output: build/libs/netheritearmourplus-<version>.jar
```

---

## License

Released under the [AGPL-3.0 License](LICENSE).
