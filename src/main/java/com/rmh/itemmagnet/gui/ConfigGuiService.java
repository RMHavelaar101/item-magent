package com.rmh.itemmagnet.gui;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.ConfigPersistence;
import com.rmh.itemmagnet.config.ReloadResult;
import com.rmh.itemmagnet.filter.ItemFilterGuiSlots;
import com.rmh.itemmagnet.filter.MaterialFilterResolver;
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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ConfigGuiService {

    public static final int SLOT_BACK = 45;
    public static final int SLOT_SAVE = 49;
    public static final int SLOT_CLOSE = 53;
    private static final String ITEM_BLACKLIST_PATH = "settings.item-blacklist";
    private static final String ITEM_BLACKLIST_TAGS_PATH = "settings.item-blacklist-tags";
    private static final String CHAT_ADD_TAG_PREFIX = "add-tag:";
    private static final int SLOT_FILTER_VIEW_MATERIALS = 48;
    private static final int SLOT_FILTER_VIEW_TAGS = 50;

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

        place(inventory, 10, nav(player, Material.COMPASS, "&eSettings", GuiDescriptions.SCAN_INTERVAL));
        place(inventory, 12, nav(player, Material.NOTE_BLOCK, "&eSounds", GuiDescriptions.SOUNDS_ENABLED));
        place(inventory, 14, nav(player, Material.CLOCK, "&eAnti-AFK", GuiDescriptions.AFK_ENABLED));
        place(inventory, 16, nav(player, Material.STONE, "&eHeight", "Y-range and underground modifiers."));
        place(inventory, 20, nav(player, Material.REDSTONE, "&eFuel", "Charge, boost, and sounds per fuel item."));
        place(inventory, 22, nav(player, Material.IRON_INGOT, "&eTiers", "Names, radius, drain, and stats per tier."));
        place(inventory, 24, nav(player, Material.SHIELD, "&eIntegrations", "Lands, WorldGuard, Towny, GriefPrevention."));
        place(inventory, 28, nav(player, Material.SCULK_SENSOR, "&dProximity Lore", GuiDescriptions.PROXIMITY_LORE));
        place(inventory, 29, nav(player, Material.HOPPER, "&eItem Filter", "Server-wide materials never pulled."));
        place(inventory, 31, toggle(player, "commands", Material.LEVER, "commands.filter-by-permission",
                "Filter help/tab by permission", "Hide admin commands from players without perms."));
        place(inventory, 40, toggle(player, "startup-message", Material.WRITABLE_BOOK, "startup-message.enabled",
                "Startup thank-you message", GuiDescriptions.STARTUP_MESSAGE));
        if (ConfigGuiPermissions.canReset(player)) {
            inventory.setItem(47, configItem(Material.TNT, "&cReset all to defaults", "action:reset-defaults",
                    "&7Restore config.yml from plugin defaults.",
                    "&cThis cannot be undone.",
                    "&7Shift-click to confirm."));
        }

        placeFooter(inventory, player);
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openSettings(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.SETTINGS);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Settings"));
        holder.setInventory(inventory);

        place(inventory, 10, number(player, "settings", Material.REPEATER, "settings.scan-interval-ticks", 1, 20, 1, GuiDescriptions.SCAN_INTERVAL));
        place(inventory, 11, number(player, "settings", Material.HOPPER, "settings.max-items-per-tick", 1, 50, 1, GuiDescriptions.MAX_ITEMS));
        place(inventory, 12, decimal(player, "settings", Material.SLIME_BALL, "settings.pull-step-blocks", 0.1, 2.0, 0.1, GuiDescriptions.PULL_STEP));
        place(inventory, 13, toggle(player, "settings", Material.LEVER, "settings.sneak-to-disable", "Sneak to disable", GuiDescriptions.SNEAK_DISABLE));
        place(inventory, 14, decimal(player, "settings", Material.REDSTONE, "settings.fuel-radius", 1, 32, 1, GuiDescriptions.FUEL_RADIUS));
        place(inventory, 15, toggle(player, "settings", Material.REDSTONE_BLOCK, "settings.fuel-use-effective-radius",
                "Fuel uses pull radius", GuiDescriptions.FUEL_EFFECTIVE));
        place(inventory, 16, cycleHoldMode(player));
        place(inventory, 19, toggle(player, "settings", Material.EXPERIENCE_BOTTLE, "settings.pull-experience", "Pull XP orbs", GuiDescriptions.PULL_EXPERIENCE));
        place(inventory, 20, toggle(player, "settings", Material.ARMOR_STAND, "settings.pull-arm-swing", "Arm swing on pull", GuiDescriptions.ARM_SWING));
        place(inventory, 21, toggle(player, "settings", Material.IRON_BARS, "settings.show-charge-bar", "Charge durability bar", "Show charge as item durability bar."));
        place(inventory, 22, number(player, "settings", Material.BELL, "settings.deny-message-cooldown-ticks", 10, 200, 10, "Cooldown between deny/AFK repeat messages."));
        place(inventory, 23, toggle(player, "settings", Material.BEDROCK, "settings.disable-in-creative", "Disable in creative", "Turn off magnets for creative players."));
        place(inventory, 24, toggle(player, "settings", Material.ENDER_EYE, "settings.disable-in-spectator", "Disable in spectator", "Turn off magnets for spectators."));

        placeFooter(inventory, player);
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openSounds(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.SOUNDS);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Sounds"));
        holder.setInventory(inventory);

        place(inventory, 10, toggle(player, "sounds", Material.LEVER, "settings.sounds.enabled", "Sounds enabled", GuiDescriptions.SOUNDS_ENABLED));
        inventory.setItem(12, displayItem(Material.ITEM_FRAME, "&fPull sound", "&7" + plugin.getConfig().getString("settings.sounds.pull", "ENTITY_ITEM_PICKUP")));
        inventory.setItem(13, displayItem(Material.REDSTONE, "&fFuel sound (fallback)", "&7" + plugin.getConfig().getString("settings.sounds.fuel", "BLOCK_REDSTONE_BLOCK_CLICK")));
        inventory.setItem(14, displayItem(Material.BEACON, "&fDepleted sound", "&7" + plugin.getConfig().getString("settings.sounds.depleted", "BLOCK_BEACON_DEACTIVATE")));
        inventory.setItem(15, displayItem(Material.NOTE_BLOCK, "&fDenied sound", "&7" + plugin.getConfig().getString("settings.sounds.denied", "BLOCK_NOTE_BLOCK_BASS")));
        inventory.setItem(22, displayItem(Material.BOOK, "&7Per-fuel sounds", "&7Edit in Fuel menu or config.yml"));

        placeFooter(inventory, player);
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openAntiAfk(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.ANTI_AFK);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Anti-AFK"));
        holder.setInventory(inventory);

        place(inventory, 10, toggle(player, "anti-afk", Material.LEVER, "anti-afk.enabled", "AFK detection", GuiDescriptions.AFK_ENABLED));
        place(inventory, 12, decimal(player, "anti-afk", Material.GRASS_BLOCK, "anti-afk.required-blocks-moved", 0.5, 20, 0.5, "Horizontal blocks required in window."));
        place(inventory, 13, number(player, "anti-afk", Material.CLOCK, "anti-afk.window-seconds", 10, 300, 10, "Seconds to measure movement."));
        place(inventory, 14, toggle(player, "anti-afk", Material.REDSTONE, "anti-afk.notify-once", "Notify once", GuiDescriptions.AFK_NOTIFY_ONCE));
        place(inventory, 15, toggle(player, "anti-afk", Material.COAL, "anti-afk.disable-auto-fuel-when-afk", "Block auto fuel when AFK", "Stops ground fuel absorb while AFK."));

        placeFooter(inventory, player);
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openHeight(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.HEIGHT);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Height"));
        holder.setInventory(inventory);

        place(inventory, 10, toggle(player, "height", Material.LEVER, "height.use-y-range", "Use Y range", "Fully disable magnet outside min/max Y."));
        place(inventory, 12, number(player, "height", Material.STONE, "height.min-y", -64, 320, 1, "Minimum Y level."));
        place(inventory, 13, number(player, "height", Material.GLOWSTONE, "height.max-y", -64, 320, 1, "Maximum Y level."));
        place(inventory, 15, toggle(player, "height", Material.DEEPSLATE, "height.underground.enabled", "Underground modifiers", GuiDescriptions.PRESET_WARNING));
        place(inventory, 16, number(player, "height", Material.SCULK, "height.underground.threshold-y", -64, 320, 1, "Y at or below = underground."));
        place(inventory, 19, decimal(player, "height", Material.IRON_INGOT, "height.underground.radius-modifier", -10, 10, 0.5, "Radius bonus underground."));
        place(inventory, 20, decimal(player, "height", Material.GOLD_INGOT, "height.underground.drain-multiplier", 0.5, 3, 0.05, "Charge drain multiplier underground."));

        placeFooter(inventory, player);
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openFuelList(Player player) {
        if (!ConfigGuiPermissions.canAccessSection(player, "fuel")) {
            openMain(player);
            return;
        }

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

        placeFooter(inventory, player);
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openFuelEdit(Player player, String fuelKey) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.FUEL_EDIT, fuelKey);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Fuel: " + fuelKey));
        holder.setInventory(inventory);

        String base = "fuel." + fuelKey + ".";
        place(inventory, 10, number(player, "fuel", Material.REDSTONE, base + "charge-per-item", 1, 5000, 10, "Charge added per item."));
        place(inventory, 11, number(player, "fuel", Material.COMPASS, base + "radius-bonus", 0, 20, 1, "Radius bonus per boost level."));
        place(inventory, 12, number(player, "fuel", Material.BLAZE_POWDER, base + "boost-level-add", 0, 10, 1, "Boost levels added."));
        place(inventory, 13, number(player, "fuel", Material.CLOCK, base + "boost-duration-seconds", 0, 600, 10, "Boost duration in seconds."));
        inventory.setItem(14, displayItem(Material.NOTE_BLOCK, "&fSound", "&7" + plugin.getConfig().getString(base + "sound", "(fallback)"),
                "&7Edit sound name in config.yml"));

        inventory.setItem(SLOT_BACK, displayItem(Material.ARROW, "&7Back to fuel list", ""));
        placeFooter(inventory, player);
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openTierList(Player player) {
        if (!ConfigGuiPermissions.canAccessSection(player, "tiers")) {
            openMain(player);
            return;
        }

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

        placeFooter(inventory, player);
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openTierEdit(Player player, String tierId) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.TIER_EDIT, tierId);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Tier: " + tierId));
        holder.setInventory(inventory);

        String base = "tiers." + tierId + ".";
        String displayName = plugin.getConfig().getString(base + "display-name", "&e" + tierId);
        place(inventory, 9, configItem(player, "tiers", Material.NAME_TAG, "&fDisplay name", "chat:" + base + "display-name",
                "&7" + GuiDescriptions.DISPLAY_NAME,
                "&7Current: " + TextUtil.color(displayName),
                "&7Click, then type in chat"));
        place(inventory, 10, decimal(player, "tiers", Material.COMPASS, base + "radius", 1, 64, 0.5, "Base pull radius."));
        place(inventory, 11, decimal(player, "tiers", Material.IRON_INGOT, base + "min-radius", 0, 64, 0.5, "Minimum effective radius."));
        place(inventory, 12, decimal(player, "tiers", Material.GOLD_INGOT, base + "max-radius", 1, 128, 0.5, "Maximum effective radius."));
        place(inventory, 13, decimal(player, "tiers", Material.REDSTONE, base + "base-drain-per-second", 0, 100, 0.5, "Passive charge drain per second."));
        place(inventory, 14, decimal(player, "tiers", Material.COAL, base + "extra-drain-per-item", 0, 50, 0.5, "Extra drain per pulled item."));
        place(inventory, 15, number(player, "tiers", Material.EXPERIENCE_BOTTLE, base + "max-charge", 100, 100000, 100, "Maximum charge capacity."));
        place(inventory, 16, toggle(player, "tiers", Material.EXPERIENCE_BOTTLE, base + "pull-experience", "Pull XP", GuiDescriptions.PULL_EXPERIENCE));
        inventory.setItem(19, displayItem(Material.PAPER, "&fMaterial", "&7" + plugin.getConfig().getString(base + "material"), "&e" + GuiDescriptions.TIER_MATERIAL));

        inventory.setItem(SLOT_BACK, displayItem(Material.ARROW, "&7Back to tier list", ""));
        placeFooter(inventory, player);
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openIntegrations(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.INTEGRATIONS);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Integrations"));
        holder.setInventory(inventory);

        place(inventory, 10, toggle(player, "integrations", Material.GRASS_BLOCK, "integrations.lands.enabled", "Lands enabled", "Check Lands claims."));
        place(inventory, 11, toggle(player, "integrations", Material.BARRIER, "integrations.worldguard.enabled", "WorldGuard enabled", "Respect WG regions."));
        place(inventory, 12, toggle(player, "integrations", Material.MAP, "integrations.towny.enabled", "Towny enabled", "Check Towny plots."));
        place(inventory, 13, toggle(player, "integrations", Material.GOLDEN_SHOVEL, "integrations.griefprevention.enabled", "GriefPrevention enabled", "Check GP claims."));
        place(inventory, 19, toggle(player, "integrations", Material.BRICKS, "integrations.residence.enabled", "Residence enabled", "Check Residence claims."));
        place(inventory, 20, toggle(player, "integrations", Material.GRASS_BLOCK, "integrations.plotsquared.enabled", "PlotSquared enabled", "Check plot membership."));
        place(inventory, 21, toggle(player, "integrations", Material.ENDER_PEARL, "integrations.superiorskyblock.enabled", "SuperiorSkyblock enabled", "Check island permissions."));
        place(inventory, 22, toggle(player, "integrations", Material.BOOK, "integrations.quests.enabled", "Quests unlock bridge", "Grant tiers on quest complete."));

        placeFooter(inventory, player);
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

        place(inventory, 10, toggle(player, "proximity-lore", Material.LEVER, "proximity-lore.enabled", "Proximity lore enabled", GuiDescriptions.PROXIMITY_LORE));
        place(inventory, 12, number(player, "proximity-lore", Material.REPEATER, "proximity-lore.scan-interval-ticks", 5, 200, 5, GuiDescriptions.PROXIMITY_SCAN));
        place(inventory, 14, toggle(player, "proximity-lore", Material.IRON_INGOT, "proximity-lore.require-active-magnet", "Require active magnet", GuiDescriptions.PROXIMITY_REQUIRE_MAGNET));
        place(inventory, 16, number(player, "proximity-lore", Material.CLOCK, "proximity-lore.cooldown-seconds", 5, 600, 5, GuiDescriptions.PROXIMITY_COOLDOWN));
        place(inventory, 22, nav(player, Material.MAP, "&eZones", "Edit zone coordinates and radius."));

        placeFooter(inventory, player);
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openProximityLoreZones(Player player) {
        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.PROXIMITY_LORE_ZONES);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Proximity Zones"));
        holder.setInventory(inventory);

        ConfigurationSection zones = plugin.getConfig().getConfigurationSection("proximity-lore.zones");
        int slot = 10;
        if (zones != null && ConfigGuiPermissions.canAccessSection(player, "proximity-lore")) {
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
        placeFooter(inventory, player);
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

        place(inventory, 10, configItem(player, "proximity-lore", Material.GRASS_BLOCK, "&fWorld", "chat:" + base + "world",
                "&7Bukkit world name",
                "&7Current: &f" + world,
                "&7Click, then type in chat"));
        place(inventory, 12, number(player, "proximity-lore", Material.COMPASS, base + "x", -30000000, 30000000, 1, "Zone center X."));
        place(inventory, 13, number(player, "proximity-lore", Material.LADDER, base + "y", -64, 320, 1, "Zone center Y."));
        place(inventory, 14, number(player, "proximity-lore", Material.COMPASS, base + "z", -30000000, 30000000, 1, "Zone center Z."));
        place(inventory, 16, number(player, "proximity-lore", Material.STRING, base + "radius", 1, 128, 1, "Horizontal match radius."));
        place(inventory, 17, number(player, "proximity-lore", Material.SCAFFOLDING, base + "y-tolerance", 1, 64, 1, "Vertical tolerance from center Y."));

        inventory.setItem(SLOT_BACK, displayItem(Material.ARROW, "&7Back to zone list", ""));
        placeFooter(inventory, player);
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openItemFilter(Player player) {
        openItemFilterView(player, "materials", 0);
    }

    public void openItemFilter(Player player, int page) {
        openItemFilterView(player, "materials", page);
    }

    private void openItemFilterView(Player player, String view, int page) {
        if (!ConfigGuiPermissions.canAccessSection(player, "item-filter")) {
            openMain(player);
            return;
        }

        boolean tagsView = "tags".equals(view);
        List<String> entries = tagsView
                ? new ArrayList<>(plugin.getConfig().getStringList(ITEM_BLACKLIST_TAGS_PATH))
                : plugin.getConfig().getStringList(ITEM_BLACKLIST_PATH).stream()
                .map(Material::matchMaterial)
                .filter(material -> material != null)
                .map(Material::name)
                .sorted()
                .toList();

        if (tagsView) {
            entries.sort(String::compareToIgnoreCase);
        }

        int totalPages = Math.max(1, (int) Math.ceil(entries.size() / (double) ItemFilterGuiSlots.ITEMS_PER_PAGE));
        int safePage = Math.max(0, Math.min(page, totalPages - 1));
        String context = buildFilterContext(view, safePage);

        ConfigGuiHolder holder = new ConfigGuiHolder(ConfigGuiType.ITEM_FILTER, context);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color(tagsView ? "&8Server Tag Filter" : "&8Server Item Filter"));
        holder.setInventory(inventory);

        inventory.setItem(4, displayItem(Material.HOPPER,
                tagsView ? "&eServer tag blacklist" : "&eServer item blacklist",
                tagsView ? "&7Tag rules never pulled by any magnet." : "&7Materials listed here are never pulled by any magnet.",
                "&7Blacklisted: &f" + entries.size(),
                "&7Page: &f" + (safePage + 1) + "/" + totalPages,
                "&eSave & Reload to apply."));

        int startIndex = safePage * ItemFilterGuiSlots.ITEMS_PER_PAGE;
        int slot = ItemFilterGuiSlots.LIST_START;
        for (int index = startIndex; index < entries.size() && slot <= ItemFilterGuiSlots.LIST_END; index++) {
            if (tagsView) {
                String tag = entries.get(index);
                inventory.setItem(slot++, displayItem(
                        Material.NAME_TAG,
                        "&c" + tag,
                        "&7Click to &aallow&7 this tag again"
                ));
            } else {
                Material material = Material.matchMaterial(entries.get(index));
                if (material != null) {
                    inventory.setItem(slot++, displayItem(
                            material,
                            "&c" + material.name(),
                            "&7Click to &aallow&7 this item again"
                    ));
                }
            }
        }

        if (ConfigGuiPermissions.canEdit(player, tagsView ? ITEM_BLACKLIST_TAGS_PATH : ITEM_BLACKLIST_PATH, "item-filter")) {
            inventory.setItem(ItemFilterGuiSlots.SLOT_ADD_FROM_HAND, displayItem(
                    tagsView ? Material.PAPER : Material.LIME_DYE,
                    tagsView ? "&aAdd tag (chat)" : "&aAdd item in hand",
                    tagsView ? "&7Click and type a tag id in chat" : "&7Hold an item and click to blacklist it"
            ));
        } else {
            inventory.setItem(ItemFilterGuiSlots.SLOT_ADD_FROM_HAND, displayItem(
                    Material.GRAY_DYE,
                    tagsView ? "&8Add tag (chat)" : "&8Add item in hand",
                    "&cNo permission to edit"
            ));
        }

        inventory.setItem(SLOT_FILTER_VIEW_MATERIALS, displayItem(
                Material.CHEST,
                tagsView ? "&7Materials view" : "&aMaterials view",
                "&7Click to edit material blacklist"
        ));
        inventory.setItem(SLOT_FILTER_VIEW_TAGS, displayItem(
                Material.NAME_TAG,
                tagsView ? "&aTags view" : "&7Tags view",
                "&7Click to edit tag blacklist"
        ));

        if (safePage > 0) {
            inventory.setItem(ItemFilterGuiSlots.SLOT_PREVIOUS_PAGE, displayItem(Material.ARROW, "&7Previous page", ""));
        }
        if (safePage < totalPages - 1) {
            inventory.setItem(ItemFilterGuiSlots.SLOT_NEXT_PAGE, displayItem(Material.ARROW, "&7Next page", ""));
        }

        inventory.setItem(SLOT_BACK, backButton());
        placeFooter(inventory, player);
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
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.reload-failed", Map.of())));
            return;
        }
        int changes = plugin.getConfigChangeTracker().getChangedPaths().size();
        player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.reload-applied",
                Map.of("count", String.valueOf(changes)))));
        if (result.restartRequiredKeys().isEmpty()) {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.reload-no-restart-needed", Map.of())));
        } else {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.reload-restart-required", Map.of())));
            for (String key : result.restartRequiredKeys()) {
                player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("command.reload-restart-key",
                        Map.of("key", key))));
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
            if (!ConfigGuiPermissions.canSave(player)) {
                sendNoPermission(player);
                return;
            }
            saveAndReload(player);
            player.closeInventory();
            return;
        }

        if (holder.getType() == ConfigGuiType.ITEM_FILTER) {
            handleItemFilterClick(player, holder, slot);
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
            if (ConfigGuiPermissions.canAccessSection(player, "tiers")) {
                openTierEdit(player, path.substring("open-tier:".length()));
            }
            return;
        }
        if (path.startsWith("open-zone:")) {
            if (ConfigGuiPermissions.canAccessSection(player, "proximity-lore")) {
                openProximityLoreZoneEdit(player, path.substring("open-zone:".length()));
            }
            return;
        }
        if (path.startsWith("chat:")) {
            String configPath = path.substring(5);
            if (!canEditPath(player, configPath, holder)) {
                sendNoPermission(player);
                return;
            }
            beginChatEdit(player, holder, configPath);
            return;
        }
        if ("action:reset-defaults".equals(path)) {
            handleResetDefaults(player, shiftClick);
            return;
        }

        if (!canEditPath(player, path, holder)) {
            sendNoPermission(player);
            return;
        }

        FileConfiguration config = plugin.getConfig();
        if (path.endsWith(".enabled") || path.contains("filter-by-permission") || path.contains("sneak-to-disable")
                || path.contains("pull-experience") || path.contains("pull-arm-swing") || path.contains("show-charge-bar")
                || path.contains("disable-in") || path.contains("fuel-use-effective") || path.contains("use-y-range")
                || path.contains("underground.enabled") || path.contains("pull-experience")
                || path.equals("proximity-lore.enabled") || path.equals("proximity-lore.require-active-magnet")
                || path.equals("startup-message.enabled")) {
            boolean current = config.getBoolean(path, false);
            persistence.set(player, path, !current);
            refreshCurrentMenu(player, holder);
            return;
        }

        if (path.contains("hold-mode")) {
            cycleString(player, path, List.of("MAIN_HAND", "HOTBAR", "INVENTORY"));
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
            persistence.set(player, path, Math.max(0, value));
            refreshCurrentMenu(player, holder);
            return;
        }

        int intDelta = rightClick ? -1 : 1;
        if (shiftClick) {
            intDelta *= 5;
        }
        int intValue = config.getInt(path, 0) + intDelta;
        if (path.contains("proximity-lore.zones.") && (path.endsWith(".x") || path.endsWith(".y") || path.endsWith(".z"))) {
            persistence.set(player, path, intValue);
        } else {
            persistence.set(player, path, Math.max(0, intValue));
        }
        refreshCurrentMenu(player, holder);
    }

    private void handleItemFilterClick(Player player, ConfigGuiHolder holder, int slot) {
        String view = parseFilterView(holder.getContext());
        int page = parseFilterPage(holder.getContext());
        boolean tagsView = "tags".equals(view);

        if (slot == SLOT_FILTER_VIEW_MATERIALS) {
            openItemFilterView(player, "materials", page);
            return;
        }
        if (slot == SLOT_FILTER_VIEW_TAGS) {
            openItemFilterView(player, "tags", page);
            return;
        }
        if (slot == ItemFilterGuiSlots.SLOT_PREVIOUS_PAGE) {
            openItemFilterView(player, view, page - 1);
            return;
        }
        if (slot == ItemFilterGuiSlots.SLOT_NEXT_PAGE) {
            openItemFilterView(player, view, page + 1);
            return;
        }
        if (slot == ItemFilterGuiSlots.SLOT_ADD_FROM_HAND) {
            if (!ConfigGuiPermissions.canEdit(player, tagsView ? ITEM_BLACKLIST_TAGS_PATH : ITEM_BLACKLIST_PATH, "item-filter")) {
                sendNoPermission(player);
                return;
            }
            if (tagsView) {
                beginAddTagChat(player, holder);
                return;
            }
            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand == null || hand.getType() == Material.AIR) {
                player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("filter.empty-hand", Map.of())));
                return;
            }
            Material material = hand.getType();
            List<String> list = new ArrayList<>(plugin.getConfig().getStringList(ITEM_BLACKLIST_PATH));
            if (!list.contains(material.name())) {
                list.add(material.name());
                persistence.set(player, ITEM_BLACKLIST_PATH, list);
                player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("filter.added",
                        Map.of("material", material.name()))));
            }
            openItemFilterView(player, view, page);
            return;
        }
        if (slot < ItemFilterGuiSlots.LIST_START || slot > ItemFilterGuiSlots.LIST_END) {
            return;
        }

        if (!ConfigGuiPermissions.canEdit(player, tagsView ? ITEM_BLACKLIST_TAGS_PATH : ITEM_BLACKLIST_PATH, "item-filter")) {
            sendNoPermission(player);
            return;
        }

        ItemStack clicked = player.getOpenInventory().getTopInventory().getItem(slot);
        if (clicked == null || clicked.getType() == Material.AIR || clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            return;
        }

        if (tagsView) {
            String tag = TextUtil.stripColor(clicked.getItemMeta().getDisplayName()).trim();
            List<String> list = new ArrayList<>(plugin.getConfig().getStringList(ITEM_BLACKLIST_TAGS_PATH));
            if (list.removeIf(entry -> entry.equalsIgnoreCase(tag))) {
                persistence.set(player, ITEM_BLACKLIST_TAGS_PATH, list);
                player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("filter.tag-removed",
                        Map.of("tag", tag))));
            }
            openItemFilterView(player, view, page);
            return;
        }

        Material material = clicked.getType();
        List<String> list = new ArrayList<>(plugin.getConfig().getStringList(ITEM_BLACKLIST_PATH));
        if (list.remove(material.name())) {
            persistence.set(player, ITEM_BLACKLIST_PATH, list);
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("filter.removed",
                    Map.of("material", material.name()))));
        }
        openItemFilterView(player, view, page);
    }

    private void beginAddTagChat(Player player, ConfigGuiHolder holder) {
        chatSessions.put(player.getUniqueId(), new ConfigGuiChatSession(
                player.getUniqueId(),
                CHAT_ADD_TAG_PREFIX + ITEM_BLACKLIST_TAGS_PATH,
                holder.getType(),
                holder.getContext()
        ));
        player.closeInventory();
        player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.add-tag-prompt", Map.of())));
    }

    private void handleResetDefaults(Player player, boolean shiftClick) {
        if (!ConfigGuiPermissions.canReset(player)) {
            sendNoPermission(player);
            return;
        }
        if (!shiftClick) {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig()
                    .format("gui.reset-confirm", Map.of())));
            return;
        }

        ReloadResult result = persistence.resetToDefaults();
        if (!result.success()) {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig()
                    .format("gui.reset-failed", Map.of())));
            return;
        }

        player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig()
                .format("gui.reset-success", Map.of())));
        sendReloadFeedback(player, result);
        player.closeInventory();
    }

    private void cycleString(Player player, String path, List<String> options) {
        String current = plugin.getConfig().getString(path, options.get(0));
        int index = options.indexOf(current.toUpperCase(Locale.ROOT));
        int next = (index + 1) % options.size();
        persistence.set(player, path, options.get(next));
    }

    private void routeNavigation(Player player, ConfigGuiHolder holder, int slot) {
        if (holder.getType() != ConfigGuiType.FUEL_LIST) {
            return;
        }
        if (!ConfigGuiPermissions.canAccessSection(player, "fuel")) {
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
            case ITEM_FILTER -> openMain(player);
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
            case "item-filter" -> openItemFilter(player);
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
        if (session.getConfigPath().startsWith(CHAT_ADD_TAG_PREFIX)) {
            handleAddTagChatInput(player, session, trimmed);
            return;
        }
        persistence.set(player, session.getConfigPath(), trimmed);
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
            case ITEM_FILTER -> openItemFilterView(player, parseFilterView(session.getReturnContext()), parseFilterPage(session.getReturnContext()));
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
            case ITEM_FILTER -> openItemFilterView(player, parseFilterView(holder.getContext()), parseFilterPage(holder.getContext()));
        }
    }

    private ItemStack nav(Player player, Material material, String name, String description) {
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
            case "&eItem Filter" -> "item-filter";
            default -> "main";
        };
        if (!ConfigGuiPermissions.canAccessSection(player, sectionForNav(navTarget))) {
            return null;
        }
        ItemStack stack = displayItem(material, name, "&7" + description, "&7Click to open");
        setPath(stack, "nav:" + navTarget);
        return stack;
    }

    private ItemStack toggle(Player player, String sectionId, Material material, String path, String name, String description) {
        if (!ConfigGuiPermissions.canAccessSection(player, sectionId)) {
            return null;
        }
        boolean enabled = plugin.getConfig().getBoolean(path, false);
        if (!ConfigGuiPermissions.canEdit(player, path, sectionId)) {
            return displayItem(Material.GRAY_DYE, "&8" + name, "&7" + description,
                    "&7Current: " + (enabled ? "&aON" : "&cOFF"), "&cNo permission to edit");
        }
        return configItem(material, (enabled ? "&a" : "&c") + name, path, "&7" + description,
                "&7Current: " + (enabled ? "&aON" : "&cOFF"), "&7Click to toggle");
    }

    private ItemStack number(Player player, String sectionId, Material material, String path, int min, int max, int step, String description) {
        if (!ConfigGuiPermissions.canAccessSection(player, sectionId)) {
            return null;
        }
        int value = plugin.getConfig().getInt(path, min);
        if (!ConfigGuiPermissions.canEdit(player, path, sectionId)) {
            return displayItem(Material.GRAY_DYE, "&8" + shortKey(path), "&7" + description,
                    "&7Value: &f" + value, "&cNo permission to edit");
        }
        return configItem(material, "&f" + shortKey(path), path, "&7" + description,
                "&7Value: &f" + value, "&7Left +1, Right -1, Shift x5");
    }

    private ItemStack decimal(Player player, String sectionId, Material material, String path, double min, double max, double step, String description) {
        if (!ConfigGuiPermissions.canAccessSection(player, sectionId)) {
            return null;
        }
        double value = plugin.getConfig().getDouble(path, min);
        if (!ConfigGuiPermissions.canEdit(player, path, sectionId)) {
            return displayItem(Material.GRAY_DYE, "&8" + shortKey(path), "&7" + description,
                    "&7Value: &f" + value, "&cNo permission to edit");
        }
        return configItem(material, "&f" + shortKey(path), path, "&7" + description,
                "&7Value: &f" + value, "&7Left +step, Right -step");
    }

    private ItemStack cycleHoldMode(Player player) {
        String path = "settings.hold-mode";
        if (!ConfigGuiPermissions.canAccessSection(player, "settings")) {
            return null;
        }
        String value = plugin.getConfig().getString(path, "MAIN_HAND");
        if (!ConfigGuiPermissions.canEdit(player, path, "settings")) {
            return displayItem(Material.GRAY_DYE, "&8Hold mode", "&7" + GuiDescriptions.HOLD_MODE,
                    "&7Current: &f" + value, "&cNo permission to edit");
        }
        return configItem(Material.PLAYER_HEAD, "&fHold mode", path, "&7" + GuiDescriptions.HOLD_MODE, "&7Current: &f" + value, "&7Click to cycle");
    }

    private ItemStack configItem(Player player, String sectionId, Material material, String name, String path, String... loreLines) {
        if (!ConfigGuiPermissions.canAccessSection(player, sectionId)) {
            return null;
        }
        if (!ConfigGuiPermissions.canEdit(player, path.startsWith("chat:") ? path.substring(5) : path, sectionId)) {
            List<String> lore = new ArrayList<>();
            for (String line : loreLines) {
                if (line != null && !line.isBlank()) {
                    lore.add(TextUtil.color(line));
                }
            }
            lore.add(TextUtil.color("&cNo permission to edit"));
            ItemStack stack = displayItem(Material.GRAY_DYE, "&8" + name.replace("&f", "").replace("&a", "").replace("&c", ""), lore.toArray(String[]::new));
            return stack;
        }
        return configItem(material, name, path, loreLines);
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

    private void placeFooter(Inventory inventory, Player player) {
        inventory.setItem(SLOT_BACK, backButton());
        if (ConfigGuiPermissions.canSave(player)) {
            inventory.setItem(SLOT_SAVE, saveButton());
        }
        inventory.setItem(SLOT_CLOSE, displayItem(Material.BARRIER, "&cClose", "&7Close without saving."));
    }

    private void place(Inventory inventory, int slot, ItemStack stack) {
        if (stack != null) {
            inventory.setItem(slot, stack);
        }
    }

    private void sendNoPermission(Player player) {
        player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("general.no-permission", Map.of())));
    }

    private boolean canEditPath(Player player, String path, ConfigGuiHolder holder) {
        return ConfigGuiPermissions.canEdit(player, path, sectionForPath(path, holder));
    }

    private String sectionForPath(String path, ConfigGuiHolder holder) {
        if (path.startsWith("commands.")) {
            return "commands";
        }
        if (path.startsWith("startup-message.")) {
            return "startup-message";
        }
        if ("action:reset-defaults".equals(path)) {
            return "reset";
        }
        return sectionForHolder(holder);
    }

    private String sectionForHolder(ConfigGuiHolder holder) {
        return switch (holder.getType()) {
            case SETTINGS -> "settings";
            case SOUNDS -> "sounds";
            case ANTI_AFK -> "anti-afk";
            case HEIGHT -> "height";
            case FUEL_LIST, FUEL_EDIT -> "fuel";
            case TIER_LIST, TIER_EDIT -> "tiers";
            case INTEGRATIONS -> "integrations";
            case PROXIMITY_LORE, PROXIMITY_LORE_ZONES, PROXIMITY_LORE_ZONE_EDIT -> "proximity-lore";
            case ITEM_FILTER -> "item-filter";
            case MAIN -> "main";
        };
    }

    private String sectionForNav(String navTarget) {
        return switch (navTarget) {
            case "settings" -> "settings";
            case "sounds" -> "sounds";
            case "afk" -> "anti-afk";
            case "height" -> "height";
            case "fuel" -> "fuel";
            case "tiers" -> "tiers";
            case "integrations" -> "integrations";
            case "proximity", "proximity-zones" -> "proximity-lore";
            case "item-filter" -> "item-filter";
            default -> "main";
        };
    }

    private int parsePage(String context) {
        if (context == null || context.isBlank()) {
            return 0;
        }
        if (context.contains(":")) {
            return parseFilterPage(context);
        }
        try {
            return Integer.parseInt(context);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private String parseFilterView(String context) {
        if (context == null || !context.contains(":")) {
            return "materials";
        }
        return context.substring(0, context.indexOf(':'));
    }

    private int parseFilterPage(String context) {
        if (context == null || context.isBlank()) {
            return 0;
        }
        String pagePart = context.contains(":") ? context.substring(context.indexOf(':') + 1) : context;
        try {
            return Integer.parseInt(pagePart);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private String buildFilterContext(String view, int page) {
        return view + ":" + page;
    }

    private void handleAddTagChatInput(Player player, ConfigGuiChatSession session, String tagInput) {
        String path = session.getConfigPath().substring(CHAT_ADD_TAG_PREFIX.length());
        var resolved = MaterialFilterResolver.resolve(List.of(), List.of(tagInput), plugin.getLogger());
        if (resolved.getTags().isEmpty()) {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.add-tag-invalid", Map.of("tag", tagInput))));
            reopenMenu(player, session);
            return;
        }
        String normalized = resolved.getTags().get(0);
        List<String> list = new ArrayList<>(plugin.getConfig().getStringList(path));
        if (!list.contains(normalized)) {
            list.add(normalized);
            persistence.set(player, path, list);
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format("gui.add-tag-success",
                    Map.of("tag", normalized))));
        }
        reopenMenu(player, session);
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
