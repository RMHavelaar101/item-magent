package com.rmh.itemmagnet.gui;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.ConfigPersistence;
import com.rmh.itemmagnet.config.ReloadResult;
import com.rmh.itemmagnet.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigGuiService {

    public static final int SLOT_BACK = 45;
    public static final int SLOT_SAVE = 49;
    public static final int SLOT_CLOSE = 53;

    private final ItemMagnetPlugin plugin;
    private final ConfigPersistence persistence;
    private final NamespacedKey pathKey;
    private final Map<UUID, ConfigGuiChatSession> chatSessions = new ConcurrentHashMap<>();

    public ConfigGuiService(ItemMagnetPlugin plugin, ConfigPersistence persistence) {
        this.plugin = plugin;
        this.persistence = persistence;
        this.pathKey = new NamespacedKey(plugin, "gui-path");
    }

    public void openMain(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.MAIN);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8ItemMagnet Config"));
        holder.setInventory(inventory);

        String preset = plugin.getConfig().getString("preset", "none");
        inventory.setItem(4, displayItem(Material.NETHER_STAR, "&cItemMagnet Config",
                "&7Edit server settings in-game.",
                preset.equalsIgnoreCase("none") ? "&7Preset: &fnone" : "&ePreset active: &f" + preset,
                preset.equalsIgnoreCase("none") ? "" : "&e" + GuiDescriptions.PRESET_WARNING));

        inventory.setItem(10, nav(Material.COMPASS, "&eSettings", GuiDescriptions.SCAN_INTERVAL));
        inventory.setItem(12, nav(Material.NOTE_BLOCK, "&eSounds", GuiDescriptions.SOUNDS_ENABLED));
        inventory.setItem(14, nav(Material.CLOCK, "&eAnti-AFK", GuiDescriptions.AFK_ENABLED));
        inventory.setItem(16, nav(Material.STONE, "&eHeight", "Y-range and underground modifiers."));
        inventory.setItem(20, nav(Material.REDSTONE, "&eFuel", "Charge, boost, and sounds per fuel item."));
        inventory.setItem(22, nav(Material.IRON_INGOT, "&eTiers", "Names, radius, drain, and stats per tier."));
        inventory.setItem(24, nav(Material.SHIELD, "&eIntegrations", "Lands, WorldGuard, Towny, GriefPrevention."));
        inventory.setItem(28, nav(Material.SCULK_SENSOR, "&dProximity Lore", GuiDescriptions.PROXIMITY_LORE));
        inventory.setItem(31, toggle(Material.LEVER, "commands.filter-by-permission",
                "Filter help/tab by permission", "Hide admin commands from players without perms."));

        inventory.setItem(SLOT_SAVE, displayItem(Material.LIME_CONCRETE, "&aSave & Reload", "&7Apply pending GUI edits."));
        inventory.setItem(SLOT_CLOSE, displayItem(Material.BARRIER, "&cClose", "&7Close without saving."));
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openSettings(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.SETTINGS);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Settings"));
        holder.setInventory(inventory);

        inventory.setItem(10, number(Material.REPEATER, "settings.scan-interval-ticks", 1, 20, 1, GuiDescriptions.SCAN_INTERVAL));
        inventory.setItem(11, number(Material.HOPPER, "settings.max-items-per-tick", 1, 50, 1, GuiDescriptions.MAX_ITEMS));
        inventory.setItem(12, decimal(Material.SLIME_BALL, "settings.pull-step-blocks", 0.1, 2.0, 0.1, GuiDescriptions.PULL_STEP));
        inventory.setItem(13, toggle(Material.LEVER, "settings.sneak-to-disable", "Sneak to disable", GuiDescriptions.SNEAK_DISABLE));
        inventory.setItem(14, decimal(Material.REDSTONE, "settings.fuel-radius", 1, 32, 1, GuiDescriptions.FUEL_RADIUS));
        inventory.setItem(15, toggle(Material.REDSTONE_BLOCK, "settings.fuel-use-effective-radius",
                "Fuel uses pull radius", GuiDescriptions.FUEL_EFFECTIVE));
        inventory.setItem(16, cycleHoldMode());
        inventory.setItem(19, toggle(Material.EXPERIENCE_BOTTLE, "settings.pull-experience", "Pull XP orbs", GuiDescriptions.PULL_EXPERIENCE));
        inventory.setItem(20, toggle(Material.ARMOR_STAND, "settings.pull-arm-swing", "Arm swing on pull", GuiDescriptions.ARM_SWING));
        inventory.setItem(21, toggle(Material.IRON_BARS, "settings.show-charge-bar", "Charge durability bar", "Show charge as item durability bar."));
        inventory.setItem(22, number(Material.BELL, "settings.deny-message-cooldown-ticks", 10, 200, 10, "Cooldown between deny/AFK repeat messages."));
        inventory.setItem(23, toggle(Material.BEDROCK, "settings.disable-in-creative", "Disable in creative", "Turn off magnets for creative players."));
        inventory.setItem(24, toggle(Material.ENDER_EYE, "settings.disable-in-spectator", "Disable in spectator", "Turn off magnets for spectators."));

        inventory.setItem(SLOT_BACK, backButton());
        inventory.setItem(SLOT_SAVE, saveButton());
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openSounds(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.SOUNDS);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Sounds"));
        holder.setInventory(inventory);

        inventory.setItem(10, toggle(Material.LEVER, "settings.sounds.enabled", "Sounds enabled", GuiDescriptions.SOUNDS_ENABLED));
        inventory.setItem(12, displayItem(Material.ITEM_FRAME, "&fPull sound", "&7" + plugin.getConfig().getString("settings.sounds.pull", "ENTITY_ITEM_PICKUP")));
        inventory.setItem(13, displayItem(Material.REDSTONE, "&fFuel sound (fallback)", "&7" + plugin.getConfig().getString("settings.sounds.fuel", "BLOCK_REDSTONE_BLOCK_CLICK")));
        inventory.setItem(14, displayItem(Material.BEACON, "&fDepleted sound", "&7" + plugin.getConfig().getString("settings.sounds.depleted", "BLOCK_BEACON_DEACTIVATE")));
        inventory.setItem(15, displayItem(Material.NOTE_BLOCK, "&fDenied sound", "&7" + plugin.getConfig().getString("settings.sounds.denied", "BLOCK_NOTE_BLOCK_BASS")));
        inventory.setItem(22, displayItem(Material.BOOK, "&7Per-fuel sounds", "&7Edit in Fuel menu or config.yml"));

        inventory.setItem(SLOT_BACK, backButton());
        inventory.setItem(SLOT_SAVE, saveButton());
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openAntiAfk(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.ANTI_AFK);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Anti-AFK"));
        holder.setInventory(inventory);

        inventory.setItem(10, toggle(Material.LEVER, "anti-afk.enabled", "AFK detection", GuiDescriptions.AFK_ENABLED));
        inventory.setItem(12, decimal(Material.GRASS_BLOCK, "anti-afk.required-blocks-moved", 0.5, 20, 0.5, "Horizontal blocks required in window."));
        inventory.setItem(13, number(Material.CLOCK, "anti-afk.window-seconds", 10, 300, 10, "Seconds to measure movement."));
        inventory.setItem(14, toggle(Material.REDSTONE, "anti-afk.notify-once", "Notify once", GuiDescriptions.AFK_NOTIFY_ONCE));
        inventory.setItem(15, toggle(Material.COAL, "anti-afk.disable-auto-fuel-when-afk", "Block auto fuel when AFK", "Stops ground fuel absorb while AFK."));

        inventory.setItem(SLOT_BACK, backButton());
        inventory.setItem(SLOT_SAVE, saveButton());
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openHeight(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.HEIGHT);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Height"));
        holder.setInventory(inventory);

        inventory.setItem(10, toggle(Material.LEVER, "height.use-y-range", "Use Y range", "Fully disable magnet outside min/max Y."));
        inventory.setItem(12, number(Material.STONE, "height.min-y", -64, 320, 1, "Minimum Y level."));
        inventory.setItem(13, number(Material.GLOWSTONE, "height.max-y", -64, 320, 1, "Maximum Y level."));
        inventory.setItem(15, toggle(Material.DEEPSLATE, "height.underground.enabled", "Underground modifiers", GuiDescriptions.PRESET_WARNING));
        inventory.setItem(16, number(Material.SCULK, "height.underground.threshold-y", -64, 320, 1, "Y at or below = underground."));
        inventory.setItem(19, decimal(Material.IRON_INGOT, "height.underground.radius-modifier", -10, 10, 0.5, "Radius bonus underground."));
        inventory.setItem(20, decimal(Material.GOLD_INGOT, "height.underground.drain-multiplier", 0.5, 3, 0.05, "Charge drain multiplier underground."));

        inventory.setItem(SLOT_BACK, backButton());
        inventory.setItem(SLOT_SAVE, saveButton());
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openFuelList(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.FUEL_LIST);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Fuel"));
        holder.setInventory(inventory);

        ConfigurationSection fuel = plugin.getConfig().getConfigurationSection("fuel");
        int slot = 10;
        if (fuel != null) {
            for (String key : fuel.getKeys(false)) {
                if (slot > 34) {
                    break;
                }
                Material material = Material.matchMaterial(key);
                inventory.setItem(slot++, displayItem(
                        material != null ? material : Material.REDSTONE,
                        "&e" + key,
                        "&7Charge: &f" + fuel.getInt(key + ".charge-per-item"),
                        "&7Boost: &f+" + fuel.getInt(key + ".boost-level-add"),
                        "&7Click to edit"
                ));
            }
        }

        inventory.setItem(SLOT_BACK, backButton());
        inventory.setItem(SLOT_SAVE, saveButton());
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openFuelEdit(Player player, String fuelKey) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.FUEL_EDIT, fuelKey);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Fuel: " + fuelKey));
        holder.setInventory(inventory);

        String base = "fuel." + fuelKey + ".";
        inventory.setItem(10, number(Material.REDSTONE, base + "charge-per-item", 1, 5000, 10, "Charge added per item."));
        inventory.setItem(11, number(Material.COMPASS, base + "radius-bonus", 0, 20, 1, "Radius bonus per boost level."));
        inventory.setItem(12, number(Material.BLAZE_POWDER, base + "boost-level-add", 0, 10, 1, "Boost levels added."));
        inventory.setItem(13, number(Material.CLOCK, base + "boost-duration-seconds", 0, 600, 10, "Boost duration in seconds."));
        inventory.setItem(14, displayItem(Material.NOTE_BLOCK, "&fSound", "&7" + plugin.getConfig().getString(base + "sound", "(fallback)"),
                "&7Edit sound name in config.yml"));

        inventory.setItem(SLOT_BACK, displayItem(Material.ARROW, "&7Back to fuel list", ""));
        inventory.setItem(SLOT_SAVE, saveButton());
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openTierList(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.TIER_LIST);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Tiers"));
        holder.setInventory(inventory);

        ConfigurationSection tiers = plugin.getConfig().getConfigurationSection("tiers");
        int slot = 10;
        if (tiers != null) {
            for (String tierId : tiers.getKeys(false)) {
                if (slot > 34) {
                    break;
                }
                ConfigurationSection tier = tiers.getConfigurationSection(tierId);
                Material material = Material.FLINT_AND_STEEL;
                if (tier != null) {
                    Material parsed = Material.matchMaterial(tier.getString("material", "FLINT_AND_STEEL"));
                    if (parsed != null) {
                        material = parsed;
                    }
                }
                String displayName = tier != null
                        ? tier.getString("display-name", "&e" + tierId)
                        : "&e" + tierId;
                ItemStack tierItem = displayItem(material,
                        TextUtil.color(displayName),
                        "&7Id: &f" + tierId,
                        tier != null ? "&7Radius: &f" + tier.getDouble("radius") : "",
                        "&7Click to edit");
                setPath(tierItem, "open-tier:" + tierId);
                inventory.setItem(slot++, tierItem);
            }
        }

        inventory.setItem(SLOT_BACK, backButton());
        inventory.setItem(SLOT_SAVE, saveButton());
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openTierEdit(Player player, String tierId) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.TIER_EDIT, tierId);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Tier: " + tierId));
        holder.setInventory(inventory);

        String base = "tiers." + tierId + ".";
        String displayName = plugin.getConfig().getString(base + "display-name", "&e" + tierId);
        inventory.setItem(9, configItem(Material.NAME_TAG, "&fDisplay name", "chat:" + base + "display-name",
                "&7" + GuiDescriptions.DISPLAY_NAME,
                "&7Current: " + TextUtil.color(displayName),
                "&7Click, then type in chat"));
        inventory.setItem(10, decimal(Material.COMPASS, base + "radius", 1, 64, 0.5, "Base pull radius."));
        inventory.setItem(11, decimal(Material.IRON_INGOT, base + "min-radius", 0, 64, 0.5, "Minimum effective radius."));
        inventory.setItem(12, decimal(Material.GOLD_INGOT, base + "max-radius", 1, 128, 0.5, "Maximum effective radius."));
        inventory.setItem(13, decimal(Material.REDSTONE, base + "base-drain-per-second", 0, 100, 0.5, "Passive charge drain per second."));
        inventory.setItem(14, decimal(Material.COAL, base + "extra-drain-per-item", 0, 50, 0.5, "Extra drain per pulled item."));
        inventory.setItem(15, number(Material.EXPERIENCE_BOTTLE, base + "max-charge", 100, 100000, 100, "Maximum charge capacity."));
        inventory.setItem(16, toggle(Material.EXPERIENCE_BOTTLE, base + "pull-experience", "Pull XP", GuiDescriptions.PULL_EXPERIENCE));
        inventory.setItem(19, displayItem(Material.PAPER, "&fMaterial", "&7" + plugin.getConfig().getString(base + "material"), "&e" + GuiDescriptions.TIER_MATERIAL));

        inventory.setItem(SLOT_BACK, displayItem(Material.ARROW, "&7Back to tier list", ""));
        inventory.setItem(SLOT_SAVE, saveButton());
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openIntegrations(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.INTEGRATIONS);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Integrations"));
        holder.setInventory(inventory);

        inventory.setItem(10, toggle(Material.GRASS_BLOCK, "integrations.lands.enabled", "Lands enabled", "Check Lands claims."));
        inventory.setItem(11, toggle(Material.BARRIER, "integrations.worldguard.enabled", "WorldGuard enabled", "Respect WG regions."));
        inventory.setItem(12, toggle(Material.MAP, "integrations.towny.enabled", "Towny enabled", "Check Towny plots."));
        inventory.setItem(13, toggle(Material.GOLDEN_SHOVEL, "integrations.griefprevention.enabled", "GriefPrevention enabled", "Check GP claims."));

        inventory.setItem(SLOT_BACK, backButton());
        inventory.setItem(SLOT_SAVE, saveButton());
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openProximityLore(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.PROXIMITY_LORE);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Proximity Lore"));
        holder.setInventory(inventory);

        ConfigurationSection zones = plugin.getConfig().getConfigurationSection("proximity-lore.zones");
        int zoneCount = zones != null ? zones.getKeys(false).size() : 0;

        inventory.setItem(4, displayItem(Material.SCULK_SENSOR, "&dProximity Lore",
                "&7" + GuiDescriptions.PROXIMITY_LORE,
                "&7Zones configured: &f" + zoneCount,
                "&e" + GuiDescriptions.PROXIMITY_ZONE_MESSAGES));

        inventory.setItem(10, toggle(Material.LEVER, "proximity-lore.enabled", "Proximity lore enabled", GuiDescriptions.PROXIMITY_LORE));
        inventory.setItem(12, number(Material.REPEATER, "proximity-lore.scan-interval-ticks", 5, 200, 5, GuiDescriptions.PROXIMITY_SCAN));
        inventory.setItem(14, toggle(Material.IRON_INGOT, "proximity-lore.require-active-magnet", "Require active magnet", GuiDescriptions.PROXIMITY_REQUIRE_MAGNET));
        inventory.setItem(16, number(Material.CLOCK, "proximity-lore.cooldown-seconds", 5, 600, 5, GuiDescriptions.PROXIMITY_COOLDOWN));
        inventory.setItem(22, nav(Material.MAP, "&eZones", "Edit zone coordinates and radius."));

        inventory.setItem(SLOT_BACK, backButton());
        inventory.setItem(SLOT_SAVE, saveButton());
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openProximityLoreZones(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.PROXIMITY_LORE_ZONES);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Proximity Zones"));
        holder.setInventory(inventory);

        ConfigurationSection zones = plugin.getConfig().getConfigurationSection("proximity-lore.zones");
        int slot = 10;
        if (zones != null) {
            for (String zoneId : zones.getKeys(false)) {
                if (slot > 34) {
                    break;
                }
                ConfigurationSection zone = zones.getConfigurationSection(zoneId);
                String world = zone != null ? zone.getString("world", "world") : "world";
                int x = zone != null ? zone.getInt("x", 0) : 0;
                int y = zone != null ? zone.getInt("y", 64) : 64;
                int z = zone != null ? zone.getInt("z", 0) : 0;
                int radius = zone != null ? zone.getInt("radius", 8) : 8;
                int messageCount = zone != null ? zone.getStringList("messages").size() : 0;
                ItemStack zoneItem = displayItem(
                        Material.ENDER_PEARL,
                        "&e" + zoneId,
                        "&7World: &f" + world,
                        "&7Center: &f" + x + ", " + y + ", " + z,
                        "&7Radius: &f" + radius,
                        "&7Messages: &f" + messageCount,
                        "&7Click to edit coords"
                );
                setPath(zoneItem, "open-zone:" + zoneId);
                inventory.setItem(slot++, zoneItem);
            }
        }

        inventory.setItem(40, displayItem(Material.BOOK, "&7Add zones in config.yml",
                "&7Define new zone IDs under proximity-lore.zones",
                "&7Then Save & Reload."));

        inventory.setItem(SLOT_BACK, displayItem(Material.ARROW, "&7Back to proximity lore", ""));
        inventory.setItem(SLOT_SAVE, saveButton());
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openProximityLoreZoneEdit(Player player, String zoneId) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.PROXIMITY_LORE_ZONE_EDIT, zoneId);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Zone: " + zoneId));
        holder.setInventory(inventory);

        String base = "proximity-lore.zones." + zoneId + ".";
        String world = plugin.getConfig().getString(base + "world", "world");
        int messageCount = plugin.getConfig().getStringList(base + "messages").size();
        ConfigurationSection tierMessages = plugin.getConfig().getConfigurationSection(base + "tier-messages");
        int tierOverrideCount = tierMessages != null ? tierMessages.getKeys(false).size() : 0;

        inventory.setItem(4, displayItem(Material.ENDER_PEARL, "&e" + zoneId,
                "&7Messages: &f" + messageCount,
                "&7Tier overrides: &f" + tierOverrideCount,
                "&e" + GuiDescriptions.PROXIMITY_ZONE_MESSAGES));

        inventory.setItem(10, configItem(Material.GRASS_BLOCK, "&fWorld", "chat:" + base + "world",
                "&7Bukkit world name",
                "&7Current: &f" + world,
                "&7Click, then type in chat"));
        inventory.setItem(12, number(Material.COMPASS, base + "x", -30000000, 30000000, 1, "Zone center X."));
        inventory.setItem(13, number(Material.LADDER, base + "y", -64, 320, 1, "Zone center Y."));
        inventory.setItem(14, number(Material.COMPASS, base + "z", -30000000, 30000000, 1, "Zone center Z."));
        inventory.setItem(16, number(Material.STRING, base + "radius", 1, 128, 1, "Horizontal match radius."));
        inventory.setItem(17, number(Material.SCAFFOLDING, base + "y-tolerance", 1, 64, 1, "Vertical tolerance from center Y."));

        inventory.setItem(SLOT_BACK, displayItem(Material.ARROW, "&7Back to zone list", ""));
        inventory.setItem(SLOT_SAVE, saveButton());
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public ReloadResult saveAndReload(Player player) {
        ReloadResult result = persistence.saveAndReload();
        sendReloadFeedback(player, result);
        return result;
    }

    public void sendReloadFeedback(Player player, ReloadResult result) {
        if (!result.success()) {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.reload-failed", java.util.Map.of())));
            return;
        }
        int changes = plugin.getConfigChangeTracker().getChangedPaths().size();
        player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.reload-applied",
                java.util.Map.of("count", String.valueOf(changes)))));
        if (result.restartRequiredKeys().isEmpty()) {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.reload-no-restart-needed", java.util.Map.of())));
        } else {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.reload-restart-required", java.util.Map.of())));
            for (String key : result.restartRequiredKeys()) {
                player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("command.reload-restart-key",
                        java.util.Map.of("key", key))));
            }
        }
        plugin.getConfigChangeTracker().clear();
    }

    public void handleClick(Player player, ConfigGuiHolder holder, int slot, boolean rightClick, boolean shiftClick) {
        if (slot == SLOT_CLOSE) {
            player.closeInventory();
            return;
        }
        if (slot == SLOT_BACK) {
            navigateBack(player, holder);
            return;
        }
        if (slot == SLOT_SAVE) {
            saveAndReload(player);
            player.closeInventory();
            return;
        }

        ItemStack clicked = player.getOpenInventory().getTopInventory().getItem(slot);
        if (clicked == null || !clicked.hasItemMeta()) {
            routeNavigation(player, holder, slot);
            return;
        }

        String path = getPath(clicked);
        if (path == null || path.isBlank()) {
            routeNavigation(player, holder, slot);
            return;
        }

        if (path.startsWith("nav:")) {
            openByNav(player, path.substring(4));
            return;
        }
        if (path.startsWith("open-tier:")) {
            openTierEdit(player, path.substring("open-tier:".length()));
            return;
        }
        if (path.startsWith("open-zone:")) {
            openProximityLoreZoneEdit(player, path.substring("open-zone:".length()));
            return;
        }
        if (path.startsWith("chat:")) {
            beginChatEdit(player, holder, path.substring(5));
            return;
        }

        FileConfiguration config = plugin.getConfig();
        if (path.endsWith(".enabled") || path.contains("filter-by-permission") || path.contains("sneak-to-disable")
                || path.contains("pull-experience") || path.contains("pull-arm-swing") || path.contains("show-charge-bar")
                || path.contains("disable-in") || path.contains("fuel-use-effective") || path.contains("use-y-range")
                || path.contains("underground.enabled") || path.contains("pull-experience")
                || path.equals("proximity-lore.enabled") || path.equals("proximity-lore.require-active-magnet")) {
            boolean current = config.getBoolean(path, false);
            persistence.set(path, !current);
            refreshCurrentMenu(player, holder);
            return;
        }

        if (path.contains("hold-mode")) {
            cycleString(path, List.of("MAIN_HAND", "HOTBAR", "INVENTORY"));
            refreshCurrentMenu(player, holder);
            return;
        }

        double step = shiftClick ? 5 : 1;
        if (path.contains("pull-step") || path.contains("radius-modifier") || path.contains("drain-multiplier")
                || path.contains("base-drain") || path.contains("extra-drain") || path.contains("required-blocks")
                || path.contains(".radius") || path.contains("min-radius") || path.contains("max-radius")) {
            double delta = (rightClick ? -step : step);
            if (path.contains("pull-step") || path.contains("drain-multiplier") || path.contains("base-drain")
                    || path.contains("extra-drain") || path.contains("required-blocks") || path.contains("radius-modifier")
                    || path.contains(".radius") || path.contains("min-radius") || path.contains("max-radius")) {
                delta = rightClick ? -0.5 : 0.5;
                if (shiftClick) {
                    delta *= 2;
                }
            }
            double value = config.getDouble(path, 0) + delta;
            persistence.set(path, Math.max(0, value));
            refreshCurrentMenu(player, holder);
            return;
        }

        int intDelta = rightClick ? -1 : 1;
        if (shiftClick) {
            intDelta *= 5;
        }
        int intValue = config.getInt(path, 0) + intDelta;
        if (path.contains("proximity-lore.zones.") && (path.endsWith(".x") || path.endsWith(".y") || path.endsWith(".z"))) {
            persistence.set(path, intValue);
        } else {
            persistence.set(path, Math.max(0, intValue));
        }
        refreshCurrentMenu(player, holder);
    }

    private void cycleString(String path, List<String> options) {
        String current = plugin.getConfig().getString(path, options.get(0));
        int index = options.indexOf(current.toUpperCase(Locale.ROOT));
        int next = (index + 1) % options.size();
        persistence.set(path, options.get(next));
    }

    private void routeNavigation(Player player, ConfigGuiHolder holder, int slot) {
        if (holder.getType() != ConfigGuiType.FUEL_LIST) {
            return;
        }
        ItemStack clicked = player.getOpenInventory().getTopInventory().getItem(slot);
        if (clicked == null || !clicked.hasItemMeta() || clicked.getItemMeta().getDisplayName() == null) {
            return;
        }
        String name = TextUtil.stripColor(clicked.getItemMeta().getDisplayName()).toUpperCase(Locale.ROOT);
        openFuelEdit(player, name);
    }

    private void navigateBack(Player player, ConfigGuiHolder holder) {
        switch (holder.getType()) {
            case FUEL_EDIT -> openFuelList(player);
            case TIER_EDIT -> openTierList(player);
            case PROXIMITY_LORE_ZONE_EDIT -> openProximityLoreZones(player);
            case PROXIMITY_LORE_ZONES -> openProximityLore(player);
            default -> openMain(player);
        }
    }

    private void openByNav(Player player, String target) {
        switch (target) {
            case "settings" -> openSettings(player);
            case "sounds" -> openSounds(player);
            case "afk" -> openAntiAfk(player);
            case "height" -> openHeight(player);
            case "fuel" -> openFuelList(player);
            case "tiers" -> openTierList(player);
            case "integrations" -> openIntegrations(player);
            case "proximity" -> openProximityLore(player);
            case "proximity-zones" -> openProximityLoreZones(player);
            default -> openMain(player);
        }
    }

    public boolean hasChatSession(Player player) {
        return chatSessions.containsKey(player.getUniqueId());
    }

    public void clearChatSession(Player player) {
        chatSessions.remove(player.getUniqueId());
    }

    public void handleChatInput(Player player, String message) {
        ConfigGuiChatSession session = chatSessions.remove(player.getUniqueId());
        if (session == null) {
            return;
        }
        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.rename-cancelled", Map.of())));
            reopenMenu(player, session);
            return;
        }
        String trimmed = message.trim();
        if (trimmed.isEmpty()) {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.rename-empty", Map.of())));
            reopenMenu(player, session);
            return;
        }
        persistence.set(session.getConfigPath(), trimmed);
        player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.rename-success",
                Map.of("name", TextUtil.color(trimmed)))));
        reopenMenu(player, session);
    }

    private void beginChatEdit(Player player, ConfigGuiHolder holder, String configPath) {
        chatSessions.put(player.getUniqueId(), new ConfigGuiChatSession(
                player.getUniqueId(),
                configPath,
                holder.getType(),
                holder.getContext()
        ));
        player.closeInventory();
        player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.rename-prompt", Map.of())));
    }

    private void reopenMenu(Player player, ConfigGuiChatSession session) {
        switch (session.getReturnMenu()) {
            case TIER_EDIT -> openTierEdit(player, session.getReturnContext());
            case TIER_LIST -> openTierList(player);
            case PROXIMITY_LORE_ZONE_EDIT -> openProximityLoreZoneEdit(player, session.getReturnContext());
            case PROXIMITY_LORE_ZONES -> openProximityLoreZones(player);
            case PROXIMITY_LORE -> openProximityLore(player);
            default -> openMain(player);
        }
    }

    private void refreshCurrentMenu(Player player, ConfigGuiHolder holder) {
        switch (holder.getType()) {
            case MAIN -> openMain(player);
            case SETTINGS -> openSettings(player);
            case SOUNDS -> openSounds(player);
            case ANTI_AFK -> openAntiAfk(player);
            case HEIGHT -> openHeight(player);
            case FUEL_LIST -> openFuelList(player);
            case FUEL_EDIT -> openFuelEdit(player, holder.getContext());
            case TIER_LIST -> openTierList(player);
            case TIER_EDIT -> openTierEdit(player, holder.getContext());
            case INTEGRATIONS -> openIntegrations(player);
            case PROXIMITY_LORE -> openProximityLore(player);
            case PROXIMITY_LORE_ZONES -> openProximityLoreZones(player);
            case PROXIMITY_LORE_ZONE_EDIT -> openProximityLoreZoneEdit(player, holder.getContext());
        }
    }

    private ItemStack nav(Material material, String name, String description) {
        String navTarget = switch (name) {
            case "&eSettings" -> "settings";
            case "&eSounds" -> "sounds";
            case "&eAnti-AFK" -> "afk";
            case "&eHeight" -> "height";
            case "&eFuel" -> "fuel";
            case "&eTiers" -> "tiers";
            case "&eIntegrations" -> "integrations";
            case "&dProximity Lore" -> "proximity";
            case "&eZones" -> "proximity-zones";
            default -> "main";
        };
        ItemStack stack = displayItem(material, name, "&7" + description, "&7Click to open");
        setPath(stack, "nav:" + navTarget);
        return stack;
    }

    private ItemStack toggle(Material material, String path, String name, String description) {
        boolean enabled = plugin.getConfig().getBoolean(path, false);
        return configItem(material, (enabled ? "&a" : "&c") + name, path, "&7" + description,
                "&7Current: " + (enabled ? "&aON" : "&cOFF"), "&7Click to toggle");
    }

    private ItemStack number(Material material, String path, int min, int max, int step, String description) {
        int value = plugin.getConfig().getInt(path, min);
        return configItem(material, "&f" + shortKey(path), path, "&7" + description,
                "&7Value: &f" + value, "&7Left +1, Right -1, Shift x5");
    }

    private ItemStack decimal(Material material, String path, double min, double max, double step, String description) {
        double value = plugin.getConfig().getDouble(path, min);
        return configItem(material, "&f" + shortKey(path), path, "&7" + description,
                "&7Value: &f" + value, "&7Left +step, Right -step");
    }

    private ItemStack cycleHoldMode() {
        String path = "settings.hold-mode";
        String value = plugin.getConfig().getString(path, "MAIN_HAND");
        return configItem(Material.PLAYER_HEAD, "&fHold mode", path, "&7" + GuiDescriptions.HOLD_MODE, "&7Current: &f" + value, "&7Click to cycle");
    }

    private String shortKey(String path) {
        return path.substring(path.lastIndexOf('.') + 1);
    }

    private ItemStack backButton() {
        return displayItem(Material.ARROW, "&7Back", "");
    }

    private ItemStack saveButton() {
        return displayItem(Material.LIME_CONCRETE, "&aSave & Reload", "&7Writes config.yml and reloads plugin.");
    }

    private ItemStack displayItem(Material material, String name, String... loreLines) {
        return configItem(material, name, null, loreLines);
    }

    private ItemStack configItem(Material material, String name, String path, String... loreLines) {
        List<String> lore = new ArrayList<>();
        for (String line : loreLines) {
            if (line != null && !line.isBlank()) {
                lore.add(TextUtil.color(line));
            }
        }
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(TextUtil.color(name));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        setPath(stack, path);
        return stack;
    }

    private void setPath(ItemStack stack, String path) {
        if (path == null || path.isBlank()) {
            return;
        }
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(pathKey, PersistentDataType.STRING, path);
        stack.setItemMeta(meta);
    }

    private String getPath(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return null;
        }
        return stack.getItemMeta().getPersistentDataContainer().get(pathKey, PersistentDataType.STRING);
    }

    private void fillBorder(Inventory inventory) {
        ItemStack filler = displayItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        }
    }
}
