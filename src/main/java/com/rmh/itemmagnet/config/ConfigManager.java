package com.rmh.itemmagnet.config;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class ConfigManager {

    private final ItemMagnetPlugin plugin;
    private MagnetConfig magnetConfig;
    private MessagesConfig messagesConfig;

    public ConfigManager(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        FileConfiguration config = plugin.getConfig();
        applyPresetIfConfigured(config);
        this.magnetConfig = parseMagnetConfig(config);
        this.messagesConfig = loadMessages();
    }

    public MagnetConfig getMagnetConfig() {
        return magnetConfig;
    }

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    private void applyPresetIfConfigured(FileConfiguration config) {
        String preset = config.getString("preset", "none");
        if (preset == null || preset.isBlank() || preset.equalsIgnoreCase("none")) {
            return;
        }

        String resourcePath = "presets/" + preset.toLowerCase(Locale.ROOT) + ".yml";
        try (InputStream stream = plugin.getResource(resourcePath)) {
            if (stream == null) {
                plugin.getLogger().warning("Preset not found: " + preset);
                return;
            }
            YamlConfiguration presetConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(stream, StandardCharsets.UTF_8)
            );
            for (String key : presetConfig.getKeys(true)) {
                if (!presetConfig.isConfigurationSection(key)) {
                    config.set(key, presetConfig.get(key));
                }
            }
            plugin.getLogger().info("Applied preset: " + preset);
        } catch (Exception exception) {
            plugin.getLogger().warning("Failed to apply preset " + preset + ": " + exception.getMessage());
        }
    }

    private MagnetConfig parseMagnetConfig(FileConfiguration config) {
        ConfigurationSection settings = config.getConfigurationSection("settings");
        int scanInterval = settings != null ? settings.getInt("scan-interval-ticks", 2) : 2;
        int maxItems = settings != null ? settings.getInt("max-items-per-tick", 10) : 10;
        double pullStep = settings != null ? settings.getDouble("pull-step-blocks", 0.4) : 0.4;
        double pickupDistance = settings != null ? settings.getDouble("pickup-distance", 1.5) : 1.5;
        boolean sneakDisable = settings == null || settings.getBoolean("sneak-to-disable", true);
        double fuelRadius = settings != null ? settings.getDouble("fuel-radius", 3) : 3;
        boolean fuelUseEffectiveRadius = settings == null || settings.getBoolean("fuel-use-effective-radius", true);
        boolean pullArmSwing = settings != null && settings.getBoolean("pull-arm-swing", false);
        boolean showChargeBar = settings == null || settings.getBoolean("show-charge-bar", true);
        Particle particle = parseParticle(settings != null ? settings.getString("particle-type", "REVERSE_PORTAL") : "REVERSE_PORTAL");
        int denyCooldown = settings != null ? settings.getInt("deny-message-cooldown-ticks", 40) : 40;
        boolean pullExperience = settings == null || settings.getBoolean("pull-experience", true);
        HoldMode holdMode = parseHoldMode(settings != null ? settings.getString("hold-mode", "MAIN_HAND") : "MAIN_HAND");
        MultiMagnetPolicy multiMagnetPolicy = parseMultiMagnetPolicy(
                settings != null ? settings.getString("multi-magnet-policy", "BEST_TIER") : "BEST_TIER"
        );
        boolean disableInCreative = settings == null || settings.getBoolean("disable-in-creative", true);
        boolean disableInSpectator = settings == null || settings.getBoolean("disable-in-spectator", true);
        WorldFilterConfig worldFilter = parseWorldFilter(settings != null ? settings.getConfigurationSection("world-filter") : null);
        SoundsConfig sounds = parseSounds(settings != null ? settings.getConfigurationSection("sounds") : null);

        MetricsConfig metrics = parseMetrics(config.getConfigurationSection("metrics"));
        AntiAfkConfig antiAfk = parseAntiAfk(config.getConfigurationSection("anti-afk"));
        HeightConfig height = parseHeight(config.getConfigurationSection("height"));
        Map<String, FuelConfig> fuel = parseFuel(config.getConfigurationSection("fuel"));
        LandsConfig lands = parseLands(config.getConfigurationSection("integrations.lands"));
        WorldGuardConfig worldGuard = parseWorldGuard(config.getConfigurationSection("integrations.worldguard"));
        TownyConfig towny = parseTowny(config.getConfigurationSection("integrations.towny"));
        GriefPreventionConfig griefPrevention = parseGriefPrevention(config.getConfigurationSection("integrations.griefprevention"));
        Map<String, TierConfig> tiers = parseTiers(config.getConfigurationSection("tiers"));
        CommandsConfig commands = parseCommands(config.getConfigurationSection("commands"));

        return new MagnetConfig(
                config.getString("preset", "none"),
                scanInterval,
                maxItems,
                pullStep,
                pickupDistance,
                sneakDisable,
                fuelRadius,
                fuelUseEffectiveRadius,
                pullArmSwing,
                showChargeBar,
                particle,
                denyCooldown,
                pullExperience,
                holdMode,
                multiMagnetPolicy,
                disableInCreative,
                disableInSpectator,
                worldFilter,
                sounds,
                metrics,
                antiAfk,
                height,
                fuel,
                lands,
                worldGuard,
                towny,
                griefPrevention,
                tiers,
                commands
        );
    }

    private CommandsConfig parseCommands(ConfigurationSection section) {
        if (section == null) {
            return new CommandsConfig(true);
        }
        return new CommandsConfig(section.getBoolean("filter-by-permission", true));
    }

    private MetricsConfig parseMetrics(ConfigurationSection section) {
        if (section == null) {
            return new MetricsConfig(true, 0, UpdateCheckMode.ON_STARTUP);
        }
        UpdateCheckMode mode = UpdateCheckMode.ON_STARTUP;
        try {
            mode = UpdateCheckMode.valueOf(section.getString("update-check", "ON_STARTUP").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
        }
        return new MetricsConfig(
                section.getBoolean("bstats-enabled", true),
                section.getInt("bstats-plugin-id", 0),
                mode
        );
    }

    private AntiAfkConfig parseAntiAfk(ConfigurationSection section) {
        if (section == null) {
            return new AntiAfkConfig(false, 2, 60, true, true);
        }
        return new AntiAfkConfig(
                section.getBoolean("enabled", false),
                section.getDouble("required-blocks-moved", 2),
                section.getInt("window-seconds", 60),
                section.getBoolean("disable-auto-fuel-when-afk", true),
                section.getBoolean("notify-once", true)
        );
    }

    private HeightConfig parseHeight(ConfigurationSection section) {
        if (section == null) {
            return new HeightConfig(false, -64, 320, new UndergroundConfig(false, 50, -2, 1.25), new SurfaceConfig(0));
        }
        ConfigurationSection underground = section.getConfigurationSection("underground");
        ConfigurationSection surface = section.getConfigurationSection("surface");
        return new HeightConfig(
                section.getBoolean("use-y-range", false),
                section.getInt("min-y", -64),
                section.getInt("max-y", 320),
                new UndergroundConfig(
                        underground != null && underground.getBoolean("enabled", false),
                        underground != null ? underground.getInt("threshold-y", 50) : 50,
                        underground != null ? underground.getDouble("radius-modifier", -2) : -2,
                        underground != null ? underground.getDouble("drain-multiplier", 1.25) : 1.25
                ),
                new SurfaceConfig(surface != null ? surface.getDouble("radius-modifier", 0) : 0)
        );
    }

    private Map<String, FuelConfig> parseFuel(ConfigurationSection section) {
        Map<String, FuelConfig> fuel = new LinkedHashMap<>();
        if (section == null) {
            return fuel;
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection fuelSection = section.getConfigurationSection(key);
            if (fuelSection == null) {
                continue;
            }
            fuel.put(key.toUpperCase(Locale.ROOT), new FuelConfig(
                    fuelSection.getInt("charge-per-item", 50),
                    fuelSection.getInt("radius-bonus", 0),
                    fuelSection.getInt("boost-level-add", 0),
                    fuelSection.getInt("boost-duration-seconds", 0),
                    parseSound(fuelSection.getString("sound"))
            ));
        }
        return fuel;
    }

    private LandsConfig parseLands(ConfigurationSection section) {
        if (section == null) {
            return new LandsConfig(false, CheckLocation.BOTH, WildernessPolicy.ALLOW, "itemmagnet.wilderness",
                    ClaimedLandPolicy.RESPECT_FLAGS, false);
        }
        return new LandsConfig(
                section.getBoolean("enabled", false),
                parseCheckLocation(section.getString("check-at", "BOTH")),
                parseWilderness(section.getString("wilderness", "ALLOW")),
                section.getString("wilderness-permission", "itemmagnet.wilderness"),
                parseClaimedLand(section.getString("claimed-land", "RESPECT_FLAGS")),
                section.getBoolean("require-player-in-allowed-land", false)
        );
    }

    private WorldGuardConfig parseWorldGuard(ConfigurationSection section) {
        if (section == null) {
            return new WorldGuardConfig(false, CheckLocation.BOTH, true, RegionMode.NONE, List.of(), Map.of());
        }
        Map<String, WorldGuardWorldConfig> worlds = new HashMap<>();
        ConfigurationSection worldsSection = section.getConfigurationSection("worlds");
        if (worldsSection != null) {
            for (String world : worldsSection.getKeys(false)) {
                ConfigurationSection worldSection = worldsSection.getConfigurationSection(world);
                if (worldSection == null) {
                    continue;
                }
                worlds.put(world, new WorldGuardWorldConfig(
                        parseRegionMode(worldSection.getString("region-mode", "NONE")),
                        worldSection.getStringList("regions")
                ));
            }
        }
        return new WorldGuardConfig(
                section.getBoolean("enabled", false),
                parseCheckLocation(section.getString("check-at", "BOTH")),
                section.getBoolean("respect-item-pickup-flag", true),
                parseRegionMode(section.getString("region-mode", "NONE")),
                section.getStringList("regions"),
                worlds
        );
    }

    private Map<String, TierConfig> parseTiers(ConfigurationSection section) {
        Map<String, TierConfig> tiers = new LinkedHashMap<>();
        if (section == null) {
            return tiers;
        }
        for (String tierId : section.getKeys(false)) {
            ConfigurationSection tierSection = section.getConfigurationSection(tierId);
            if (tierSection == null) {
                continue;
            }
            Material material = Material.matchMaterial(tierSection.getString("material", "FLINT_AND_STEEL"));
            if (material == null) {
                plugin.getLogger().warning("Invalid material for tier " + tierId);
                continue;
            }
            List<Material> blacklist = new ArrayList<>();
            for (String entry : tierSection.getStringList("blacklist")) {
                Material blacklisted = Material.matchMaterial(entry);
                if (blacklisted != null) {
                    blacklist.add(blacklisted);
                }
            }
            boolean whitelistEnabled = tierSection.getBoolean("whitelist-enabled", false);
            List<Material> whitelist = new ArrayList<>();
            for (String entry : tierSection.getStringList("whitelist")) {
                Material allowed = Material.matchMaterial(entry);
                if (allowed != null) {
                    whitelist.add(allowed);
                }
            }
            UnlockConfig unlock = parseUnlock(tierSection.getConfigurationSection("unlock"));
            RecipeConfig recipe = parseRecipe(tierSection.getConfigurationSection("recipe"));
            tiers.put(tierId.toLowerCase(Locale.ROOT), new TierConfig(
                    tierId.toLowerCase(Locale.ROOT),
                    material,
                    tierSection.getString("display-name", tierId),
                    tierSection.getStringList("lore"),
                    tierSection.getBoolean("enchant-glint", true),
                    tierSection.getDouble("radius", 6),
                    tierSection.getInt("max-charge", 1000),
                    tierSection.getDouble("base-drain-per-second", 1),
                    tierSection.getDouble("extra-drain-per-item", 2),
                    tierSection.getDouble("boost-drain-multiplier", 1.15),
                    tierSection.getDouble("min-radius", 1),
                    tierSection.getDouble("max-radius", 16),
                    blacklist,
                    whitelistEnabled,
                    whitelist,
                    tierSection.getBoolean("pull-experience", true),
                    unlock,
                    recipe
            ));
        }
        return tiers;
    }

    private UnlockConfig parseUnlock(ConfigurationSection section) {
        if (section == null) {
            return new UnlockConfig(UnlockType.NONE, null, null, null, null, 0, null);
        }
        UnlockType type = UnlockType.NONE;
        try {
            type = UnlockType.valueOf(section.getString("type", "NONE").toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
        }
        return new UnlockConfig(
                type,
                section.getString("permission"),
                section.getString("advancement"),
                section.getString("stat"),
                section.getString("sub"),
                section.getLong("amount", 0),
                section.getString("rank")
        );
    }

    private RecipeConfig parseRecipe(ConfigurationSection section) {
        if (section == null) {
            return new RecipeConfig(false, true, List.of(), Map.of());
        }
        Map<Character, Material> ingredients = new HashMap<>();
        ConfigurationSection ingredientSection = section.getConfigurationSection("ingredients");
        if (ingredientSection != null) {
            for (String symbol : ingredientSection.getKeys(false)) {
                if (symbol.length() != 1) {
                    continue;
                }
                Material material = Material.matchMaterial(ingredientSection.getString(symbol, ""));
                if (material != null) {
                    ingredients.put(symbol.charAt(0), material);
                }
            }
        }
        return new RecipeConfig(
                section.getBoolean("enabled", true),
                section.getBoolean("hidden", true),
                section.getStringList("shape"),
                ingredients
        );
    }

    private MessagesConfig loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(messagesFile);
        try (InputStream stream = plugin.getResource("messages.yml")) {
            if (stream != null) {
                YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(stream, StandardCharsets.UTF_8)
                );
                yaml.setDefaults(defaults);
            }
        } catch (Exception ignored) {
        }

        String prefix = yaml.getString("prefix", "&8[&cItemMagnet&8]&r ");
        Map<String, String> messages = new HashMap<>();
        flattenMessages("", yaml, messages);
        return new MessagesConfig(prefix, messages);
    }

    private void flattenMessages(String path, ConfigurationSection section, Map<String, String> messages) {
        for (String key : section.getKeys(false)) {
            String fullPath = path.isEmpty() ? key : path + "." + key;
            if (section.isConfigurationSection(key)) {
                flattenMessages(fullPath, section.getConfigurationSection(key), messages);
            } else {
                messages.put(fullPath, section.getString(key, ""));
            }
        }
    }

    private Particle parseParticle(String name) {
        try {
            return Particle.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            plugin.getLogger().warning("Invalid particle type: " + name + ", using REVERSE_PORTAL");
            return Particle.REVERSE_PORTAL;
        }
    }

    private CheckLocation parseCheckLocation(String value) {
        try {
            return CheckLocation.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return CheckLocation.BOTH;
        }
    }

    private WildernessPolicy parseWilderness(String value) {
        try {
            return WildernessPolicy.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return WildernessPolicy.ALLOW;
        }
    }

    private ClaimedLandPolicy parseClaimedLand(String value) {
        try {
            return ClaimedLandPolicy.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return ClaimedLandPolicy.RESPECT_FLAGS;
        }
    }

    private RegionMode parseRegionMode(String value) {
        try {
            return RegionMode.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return RegionMode.NONE;
        }
    }

    private HoldMode parseHoldMode(String value) {
        try {
            return HoldMode.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return HoldMode.MAIN_HAND;
        }
    }

    private MultiMagnetPolicy parseMultiMagnetPolicy(String value) {
        try {
            return MultiMagnetPolicy.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return MultiMagnetPolicy.BEST_TIER;
        }
    }

    private WorldFilterConfig parseWorldFilter(ConfigurationSection section) {
        if (section == null) {
            return new WorldFilterConfig(RegionMode.NONE, List.of());
        }
        return new WorldFilterConfig(
                parseRegionMode(section.getString("mode", "NONE")),
                section.getStringList("worlds")
        );
    }

    private SoundsConfig parseSounds(ConfigurationSection section) {
        if (section == null) {
            return new SoundsConfig(false, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        }
        return new SoundsConfig(
                section.getBoolean("enabled", false),
                parseSound(section.getString("pull")),
                parseSound(section.getString("fuel")),
                parseSound(section.getString("depleted")),
                parseSound(section.getString("denied"))
        );
    }

    private Optional<org.bukkit.Sound> parseSound(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(org.bukkit.Sound.valueOf(name.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException exception) {
            plugin.getLogger().warning("Invalid sound in config: " + name);
            return Optional.empty();
        }
    }

    private TownyConfig parseTowny(ConfigurationSection section) {
        if (section == null) {
            return new TownyConfig(false, WildernessPolicy.ALLOW, "itemmagnet.wilderness", ClaimedLandPolicy.RESPECT_FLAGS);
        }
        return new TownyConfig(
                section.getBoolean("enabled", false),
                parseWilderness(section.getString("wilderness", "ALLOW")),
                section.getString("wilderness-permission", "itemmagnet.wilderness"),
                parseClaimedLand(section.getString("claimed-town", "RESPECT_FLAGS"))
        );
    }

    private GriefPreventionConfig parseGriefPrevention(ConfigurationSection section) {
        if (section == null) {
            return new GriefPreventionConfig(false, ClaimedLandPolicy.RESPECT_FLAGS);
        }
        return new GriefPreventionConfig(
                section.getBoolean("enabled", false),
                parseClaimedLand(section.getString("claimed-land", "RESPECT_FLAGS"))
        );
    }

    public void validateStartup() {
        if (magnetConfig.getTiers().isEmpty()) {
            plugin.getLogger().severe("No magnet tiers configured!");
        }
        if (magnetConfig.getLands().isEnabled() && plugin.getServer().getPluginManager().getPlugin("Lands") == null) {
            plugin.getLogger().warning("Lands integration enabled but Lands plugin is not installed.");
        }
        if (magnetConfig.getWorldGuard().isEnabled() && plugin.getServer().getPluginManager().getPlugin("WorldGuard") == null) {
            plugin.getLogger().warning("WorldGuard integration enabled but WorldGuard is not installed.");
        }
        if (magnetConfig.getTowny().isEnabled() && plugin.getServer().getPluginManager().getPlugin("Towny") == null) {
            plugin.getLogger().warning("Towny integration enabled but Towny is not installed.");
        }
        if (magnetConfig.getGriefPrevention().isEnabled() && plugin.getServer().getPluginManager().getPlugin("GriefPrevention") == null) {
            plugin.getLogger().warning("GriefPrevention integration enabled but GriefPrevention is not installed.");
        }
    }
}
