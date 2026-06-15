package com.rmh.itemmagnet.command;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.HoldMode;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.config.MessagesConfig;
import com.rmh.itemmagnet.config.ReloadResult;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.filter.FilterPresetService;
import com.rmh.itemmagnet.filter.MaterialFilterRule;
import com.rmh.itemmagnet.filter.PlayerFilterGuiService;
import com.rmh.itemmagnet.filter.PlayerFilterStorage;
import com.rmh.itemmagnet.filter.PullBlockReason;
import com.rmh.itemmagnet.filter.PullEligibilityService;
import com.rmh.itemmagnet.gui.ConfigGuiService;
import com.rmh.itemmagnet.integration.IntegrationStatusService;
import com.rmh.itemmagnet.item.MagnetData;
import com.rmh.itemmagnet.item.MagnetItemService;
import com.rmh.itemmagnet.magnet.MagnetLocator;
import com.rmh.itemmagnet.magnet.MagnetSlot;
import com.rmh.itemmagnet.magnet.NearbyItemScanner;
import com.rmh.itemmagnet.magnet.RadiusCalculator;
import com.rmh.itemmagnet.metrics.StartupMessageService;
import com.rmh.itemmagnet.protection.ProtectionService;
import com.rmh.itemmagnet.unlock.UnlockService;
import com.rmh.itemmagnet.util.PluginCompat;
import com.rmh.itemmagnet.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ItemMagnetCommand implements CommandExecutor, TabCompleter {

    private static final String ALL_TIERS = "all";

    private static final Map<String, String> SUBCOMMAND_PERMISSIONS = new LinkedHashMap<>();

    static {
        SUBCOMMAND_PERMISSIONS.put("reload", "itemmagnet.reload");
        SUBCOMMAND_PERMISSIONS.put("version", "itemmagnet.admin");
        SUBCOMMAND_PERMISSIONS.put("give", "itemmagnet.give");
        SUBCOMMAND_PERMISSIONS.put("giveall", "itemmagnet.give");
        SUBCOMMAND_PERMISSIONS.put("unlock", "itemmagnet.unlock");
        SUBCOMMAND_PERMISSIONS.put("unlockall", "itemmagnet.unlock");
        SUBCOMMAND_PERMISSIONS.put("debug", "itemmagnet.debug");
        SUBCOMMAND_PERMISSIONS.put("config", "itemmagnet.config");
        SUBCOMMAND_PERMISSIONS.put("filter", "itemmagnet.filter");
        SUBCOMMAND_PERMISSIONS.put("import", "itemmagnet.import");
        SUBCOMMAND_PERMISSIONS.put("startup-message", "itemmagnet.admin");
        SUBCOMMAND_PERMISSIONS.put("help", null);
    }

    private static final Map<String, String> HELP_KEYS = new LinkedHashMap<>();

    static {
        HELP_KEYS.put("reload", "command.help-reload");
        HELP_KEYS.put("version", "command.help-version");
        HELP_KEYS.put("give", "command.help-give");
        HELP_KEYS.put("giveall", "command.help-giveall");
        HELP_KEYS.put("unlock", "command.help-unlock");
        HELP_KEYS.put("unlockall", "command.help-unlockall");
        HELP_KEYS.put("debug", "command.help-debug");
        HELP_KEYS.put("config", "command.help-config");
        HELP_KEYS.put("filter", "command.help-filter");
        HELP_KEYS.put("import", "command.help-import");
        HELP_KEYS.put("startup-message", "command.help-startup-message");
    }

    private final ItemMagnetPlugin plugin;
    private final MagnetItemService itemService;
    private final UnlockService unlockService;
    private final ProtectionService protectionService;
    private final MagnetLocator magnetLocator;
    private final ConfigGuiService configGuiService;
    private final PlayerFilterGuiService playerFilterGuiService;
    private final StartupMessageService startupMessageService;
    private final IntegrationStatusService integrationStatusService;

    public ItemMagnetCommand(
            ItemMagnetPlugin plugin,
            MagnetItemService itemService,
            UnlockService unlockService,
            ProtectionService protectionService,
            MagnetLocator magnetLocator,
            ConfigGuiService configGuiService,
            PlayerFilterGuiService playerFilterGuiService,
            StartupMessageService startupMessageService,
            IntegrationStatusService integrationStatusService
    ) {
        this.plugin = plugin;
        this.itemService = itemService;
        this.unlockService = unlockService;
        this.protectionService = protectionService;
        this.magnetLocator = magnetLocator;
        this.configGuiService = configGuiService;
        this.playerFilterGuiService = playerFilterGuiService;
        this.startupMessageService = startupMessageService;
        this.integrationStatusService = integrationStatusService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        return switch (sub) {
            case "reload" -> handleReload(sender);
            case "version" -> handleVersion(sender);
            case "give" -> handleGive(sender, args);
            case "giveall" -> handleGiveAll(sender, args);
            case "unlock" -> handleUnlock(sender, args);
            case "unlockall" -> handleUnlockAll(sender, args);
            case "debug" -> handleDebug(sender, args);
            case "config" -> handleConfig(sender);
            case "filter" -> handleFilter(sender, args);
            case "import" -> handleImport(sender, args);
            case "startup-message" -> handleStartupMessage(sender, args);
            case "help" -> {
                if (args.length >= 2 && "filter".equalsIgnoreCase(args[1])) {
                    sender.sendMessage(format(sender, "command.help-filter-detail", Map.of()));
                } else {
                    sendHelp(sender);
                }
                yield true;
            }
            default -> {
                sender.sendMessage(format(sender, "command.usage", Map.of()));
                yield true;
            }
        };
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("itemmagnet.reload")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        ReloadResult result = plugin.reloadPlugin();
        sender.sendMessage(format(sender, "general.reload-success", Map.of()));
        if (!result.restartRequiredKeys().isEmpty()) {
            sender.sendMessage(format(sender, "command.reload-restart-required", Map.of()));
            for (String key : result.restartRequiredKeys()) {
                sender.sendMessage(format(sender, "command.reload-restart-key", Map.of("key", key)));
            }
        }
        return true;
    }

    private boolean handleConfig(CommandSender sender) {
        if (!sender.hasPermission("itemmagnet.config")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(format(sender, "general.player-only", Map.of()));
            return true;
        }
        configGuiService.openMain(player);
        return true;
    }

    private boolean handleFilter(CommandSender sender, String[] args) {
        if (args.length >= 2 && "help".equalsIgnoreCase(args[1])) {
            sender.sendMessage(format(sender, "command.help-filter-detail", Map.of()));
            return true;
        }
        if (args.length >= 2 && "clear".equalsIgnoreCase(args[1])) {
            if (!sender.hasPermission("itemmagnet.filter")) {
                sender.sendMessage(format(sender, "general.no-permission", Map.of()));
                return true;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage(format(sender, "general.player-only", Map.of()));
                return true;
            }
            int removed = plugin.getPlayerFilterStorage().clear(player.getUniqueId(), player);
            sender.sendMessage(format(sender, "filter.cleared", Map.of("count", String.valueOf(removed))));
            return true;
        }
        if (!sender.hasPermission("itemmagnet.filter")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(format(sender, "general.player-only", Map.of()));
            return true;
        }
        playerFilterGuiService.open(player);
        return true;
    }

    private boolean handleStartupMessage(CommandSender sender, String[] args) {
        if (!sender.hasPermission("itemmagnet.admin")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }

        if (args.length == 1) {
            sender.sendMessage(format(sender, startupMessageService.isEnabled()
                    ? "command.startup-message-enabled"
                    : "command.startup-message-disabled", Map.of()));
            return true;
        }

        String mode = args[1].toLowerCase(Locale.ROOT);
        Boolean enabled = switch (mode) {
            case "on", "enable", "true" -> true;
            case "off", "disable", "false" -> false;
            case "toggle" -> !startupMessageService.isEnabled();
            default -> null;
        };

        if (enabled == null) {
            sender.sendMessage(format(sender, "command.startup-message-usage", Map.of()));
            return true;
        }

        startupMessageService.setEnabled(enabled);
        sender.sendMessage(format(sender, enabled
                ? "command.startup-message-set-on"
                : "command.startup-message-set-off", Map.of()));
        return true;
    }

    private boolean handleVersion(CommandSender sender) {
        if (!sender.hasPermission("itemmagnet.admin")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("version", PluginCompat.getVersion(plugin));
        placeholders.put("paper", Bukkit.getVersion());
        placeholders.put("hooks", integrationStatusService.formatHookStatusLine());
        sender.sendMessage(format(sender, "command.version", placeholders));
        sender.sendMessage(format(sender, "command.version-hooks", placeholders));
        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(format(sender, "command.give-usage", Map.of()));
            return true;
        }
        if (!sender.hasPermission("itemmagnet.give")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(format(sender, "command.player-not-found", Map.of("player", args[1])));
            return true;
        }
        if (ALL_TIERS.equalsIgnoreCase(args[2])) {
            return giveAllTiers(sender, target, args);
        }
        TierConfig tier = plugin.getConfigManager().getMagnetConfig().getTier(args[2].toLowerCase(Locale.ROOT));
        if (tier == null) {
            sender.sendMessage(format(sender, "command.unknown-tier", Map.of("tier", args[2])));
            return true;
        }
        int charge = parseChargeArg(sender, args, 3, tier);
        if (charge < 0) {
            return true;
        }
        ItemStack item = itemService.create(tier, charge);
        target.getInventory().addItem(item);
        plugin.onPlayerReceivedMagnet(target);
        sender.sendMessage(format(sender, "command.give-success", Map.of("tier", tier.getId(), "player", target.getName())));
        return true;
    }

    private boolean handleGiveAll(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(format(sender, "command.giveall-usage", Map.of()));
            return true;
        }
        if (!sender.hasPermission("itemmagnet.give")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(format(sender, "command.player-not-found", Map.of("player", args[1])));
            return true;
        }
        return giveAllTiers(sender, target, args);
    }

    private boolean giveAllTiers(CommandSender sender, Player target, String[] args) {
        int chargeArgIndex = args.length > 2 && ALL_TIERS.equalsIgnoreCase(args[2]) ? 3 : 2;
        for (TierConfig tier : plugin.getConfigManager().getMagnetConfig().getTiers().values()) {
            int charge = parseChargeArg(sender, args, chargeArgIndex, tier);
            if (charge < 0) {
                return true;
            }
            target.getInventory().addItem(itemService.create(tier, charge));
        }
        plugin.onPlayerReceivedMagnet(target);
        sender.sendMessage(format(sender, "command.giveall-success", Map.of("player", target.getName())));
        return true;
    }

    private boolean handleUnlock(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(format(sender, "command.unlock-usage", Map.of()));
            return true;
        }
        if (!sender.hasPermission("itemmagnet.unlock")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(format(sender, "command.player-not-found", Map.of("player", args[1])));
            return true;
        }
        if (ALL_TIERS.equalsIgnoreCase(args[2])) {
            return unlockAllTiers(sender, target);
        }
        TierConfig tier = plugin.getConfigManager().getMagnetConfig().getTier(args[2].toLowerCase(Locale.ROOT));
        if (tier == null) {
            sender.sendMessage(format(sender, "command.unknown-tier", Map.of("tier", args[2])));
            return true;
        }
        unlockService.grantUnlock(target, tier);
        target.sendMessage(format(target, "craft.unlocked", Map.of("tier", tier.getId())));
        sender.sendMessage(format(sender, "command.unlock-success", Map.of("tier", tier.getId(), "player", target.getName())));
        return true;
    }

    private boolean handleUnlockAll(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(format(sender, "command.unlockall-usage", Map.of()));
            return true;
        }
        if (!sender.hasPermission("itemmagnet.unlock")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(format(sender, "command.player-not-found", Map.of("player", args[1])));
            return true;
        }
        return unlockAllTiers(sender, target);
    }

    private boolean unlockAllTiers(CommandSender sender, Player target) {
        for (TierConfig tier : plugin.getConfigManager().getMagnetConfig().getTiers().values()) {
            unlockService.grantUnlock(target, tier);
            target.sendMessage(format(target, "craft.unlocked", Map.of("tier", tier.getId())));
        }
        sender.sendMessage(format(sender, "command.unlockall-success", Map.of("player", target.getName())));
        return true;
    }

    private boolean handleDebug(CommandSender sender, String[] args) {
        if (!sender.hasPermission("itemmagnet.debug")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(format(sender, "general.player-only", Map.of()));
            return true;
        }

        if (args.length >= 2 && "nearby".equalsIgnoreCase(args[1])) {
            return handleDebugNearby(player);
        }

        MagnetConfig config = plugin.getConfigManager().getMagnetConfig();
        var magnetOptional = magnetLocator.locate(player, config);
        if (magnetOptional.isEmpty()) {
            sender.sendMessage(format(sender, "command.no-magnet", Map.of()));
            return true;
        }

        MagnetSlot magnetSlot = magnetOptional.get();
        MagnetData magnet = magnetSlot.getData();
        long tick = plugin.getServer().getCurrentTick();
        TierConfig tier = magnet.getTier();
        double effectiveRadius = RadiusCalculator.calculateEffectiveRadius(config, tier, magnet, player, tick);
        boolean allowed = protectionService.canPull(player, player.getLocation());
        boolean afk = config.getAntiAfk().isEnabled()
                && plugin.getMagnetService().getAfkTracker().isAfk(
                player,
                config.getAntiAfk().getRequiredBlocksMoved(),
                config.getAntiAfk().getWindowSeconds()
        );
        boolean boostActive = magnet.isBoostActive(tick);
        long boostTicksRemaining = Math.max(0, magnet.getBoostExpiryTick() - tick);
        int boostSecondsRemaining = (int) Math.ceil(boostTicksRemaining / 20.0);
        boolean worldAllowed = config.getWorldFilter().isAllowed(player.getWorld().getName());
        boolean gamemodeBlocked = (config.isDisableInCreative() && player.getGameMode() == GameMode.CREATIVE)
                || (config.isDisableInSpectator() && player.getGameMode() == GameMode.SPECTATOR);
        PlayerFilterStorage filterStorage = plugin.getPlayerFilterStorage();
        String tierMode = tier.isWhitelistEnabled() ? "whitelist" : "blacklist";

        sender.sendMessage(format(sender, "command.debug-header", Map.of()));
        sender.sendMessage(format(sender, "command.debug-tier", Map.of("tier", tier.getId())));
        sender.sendMessage(format(sender, "command.debug-slot", Map.of("slot", String.valueOf(magnetSlot.getSlot()))));
        sender.sendMessage(format(sender, "command.debug-hold-mode", Map.of("hold_mode", formatHoldMode(config.getHoldMode()))));
        sender.sendMessage(format(sender, "command.debug-charge", Map.of(
                "charge", String.valueOf(magnet.getCharge()),
                "max_charge", String.valueOf(tier.getMaxCharge())
        )));
        sender.sendMessage(format(sender, "command.debug-boost", Map.of(
                "boost", String.valueOf(magnet.getBoostLevel()),
                "boost_active", String.valueOf(boostActive),
                "boost_seconds", String.valueOf(boostSecondsRemaining)
        )));
        sender.sendMessage(format(sender, "command.debug-radius", Map.of(
                "radius", String.format(Locale.ROOT, "%.2f", effectiveRadius),
                "base_radius", String.format(Locale.ROOT, "%.2f", tier.getRadius())
        )));
        sender.sendMessage(format(sender, "command.debug-protection", Map.of("allowed", String.valueOf(allowed))));
        sender.sendMessage(format(sender, "command.debug-afk", Map.of("afk", String.valueOf(afk))));
        sender.sendMessage(format(sender, "command.debug-world", Map.of("world_allowed", String.valueOf(worldAllowed))));
        sender.sendMessage(format(sender, "command.debug-gamemode", Map.of("gamemode_blocked", String.valueOf(gamemodeBlocked))));
        sender.sendMessage(format(sender, "command.debug-filter-header", Map.of()));
        sender.sendMessage(format(sender, "command.debug-filter-server", Map.of(
                "server_count", String.valueOf(config.getServerItemFilter().getRuleCount())
        )));
        sender.sendMessage(format(sender, "command.debug-filter-personal", Map.of(
                "personal_count", String.valueOf(filterStorage.getFilterRuleCount(player.getUniqueId()))
        )));
        sender.sendMessage(format(sender, "command.debug-filter-tier-mode", Map.of("tier_mode", tierMode)));
        return true;
    }

    private boolean handleDebugNearby(Player player) {
        MagnetConfig config = plugin.getConfigManager().getMagnetConfig();
        var magnetOptional = magnetLocator.locate(player, config);
        if (magnetOptional.isEmpty()) {
            player.sendMessage(format(player, "command.no-magnet", Map.of()));
            return true;
        }

        MagnetData magnet = magnetOptional.get().getData();
        TierConfig tier = magnet.getTier();
        long tick = plugin.getServer().getCurrentTick();
        double radius = RadiusCalculator.calculateEffectiveRadius(config, tier, magnet, player, tick);
        PullEligibilityService eligibility = plugin.getPullEligibilityService();

        List<Item> nearby = NearbyItemScanner.findItems(player, radius).stream()
                .sorted(Comparator.comparingDouble(item -> item.getLocation().distanceSquared(player.getLocation())))
                .limit(5)
                .toList();

        player.sendMessage(format(player, "command.debug-nearby-header", Map.of()));
        for (Item entity : nearby) {
            Material material = entity.getItemStack().getType();
            Optional<PullBlockReason> reason = eligibility.evaluateItemPull(
                    player, entity.getItemStack(), tier, entity.getLocation()
            );
            String status = reason.map(PullBlockReason::name).orElse("OK");
            if (reason.isEmpty() && !eligibility.hasInventorySpace(player, entity.getItemStack())) {
                status = PullBlockReason.INVENTORY_FULL.name();
            }
            player.sendMessage(format(player, "command.debug-nearby-entry", Map.of(
                    "material", material.name(),
                    "status", status
            )));
        }
        return true;
    }

    private boolean handleImport(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(format(sender, "command.import-filter-usage", Map.of()));
            return true;
        }

        String type = args[1].toLowerCase(Locale.ROOT);
        return switch (type) {
            case "blacklist" -> handleImportServerBlacklist(sender, args);
            case "filter" -> handleImportPersonalFilter(sender, args);
            case "filter-preset" -> handleImportFilterPreset(sender, args);
            default -> {
                sender.sendMessage(format(sender, "command.import-filter-usage", Map.of()));
                yield true;
            }
        };
    }

    private boolean handleImportServerBlacklist(CommandSender sender, String[] args) {
        if (!sender.hasPermission("itemmagnet.import")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(format(sender, "command.import-blacklist-usage", Map.of()));
            return true;
        }
        List<Material> materials = parseMaterialList(sender, joinArgs(args, 2));
        if (materials.isEmpty()) {
            return true;
        }
        List<String> list = new ArrayList<>(plugin.getConfig().getStringList("settings.item-blacklist"));
        int added = 0;
        for (Material material : materials) {
            if (!list.contains(material.name())) {
                list.add(material.name());
                added++;
            }
        }
        if (sender instanceof Player player) {
            plugin.getConfigPersistence().set(player, "settings.item-blacklist", list);
        } else {
            plugin.getConfigPersistence().set("settings.item-blacklist", list);
        }
        plugin.getConfigPersistence().saveAndReload();
        sender.sendMessage(format(sender, "command.import-blacklist-success", Map.of("count", String.valueOf(added))));
        return true;
    }

    private boolean handleImportPersonalFilter(CommandSender sender, String[] args) {
        if (!sender.hasPermission("itemmagnet.filter")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(format(sender, "general.player-only", Map.of()));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(format(sender, "command.import-filter-usage", Map.of()));
            return true;
        }
        List<Material> materials = parseMaterialList(sender, joinArgs(args, 2));
        if (materials.isEmpty()) {
            return true;
        }
        int before = plugin.getPlayerFilterStorage().getFilterRuleCount(player.getUniqueId());
        plugin.getPlayerFilterStorage().mergeMaterials(player.getUniqueId(), materials, player);
        int added = plugin.getPlayerFilterStorage().getFilterRuleCount(player.getUniqueId()) - before;
        sender.sendMessage(format(sender, "command.import-filter-success", Map.of("count", String.valueOf(added))));
        return true;
    }

    private boolean handleImportFilterPreset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("itemmagnet.filter")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(format(sender, "general.player-only", Map.of()));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(format(sender, "command.import-filter-preset-usage", Map.of()));
            return true;
        }
        FilterPresetService presetService = plugin.getFilterPresetService();
        MaterialFilterRule preset = presetService.getPreset(args[2]);
        if (preset == null) {
            sender.sendMessage(format(sender, "command.import-unknown-preset", Map.of("preset", args[2])));
            return true;
        }
        plugin.getPlayerFilterStorage().applyPreset(player.getUniqueId(), preset, player);
        sender.sendMessage(format(sender, "command.import-filter-preset-success", Map.of("preset", args[2])));
        return true;
    }

    private List<Material> parseMaterialList(CommandSender sender, String raw) {
        List<Material> materials = new ArrayList<>();
        for (String entry : raw.split(",")) {
            String trimmed = entry.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            Material material = Material.matchMaterial(trimmed);
            if (material == null) {
                sender.sendMessage(format(sender, "command.import-invalid-material", Map.of("material", trimmed)));
                continue;
            }
            materials.add(material);
        }
        return materials;
    }

    private String joinArgs(String[] args, int startIndex) {
        StringBuilder builder = new StringBuilder();
        for (int index = startIndex; index < args.length; index++) {
            if (index > startIndex) {
                builder.append(',');
            }
            builder.append(args[index]);
        }
        return builder.toString();
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(format(sender, "command.help-header", Map.of()));
        if (shouldFilterCommands(sender)) {
            for (Map.Entry<String, String> entry : SUBCOMMAND_PERMISSIONS.entrySet()) {
                if ("help".equals(entry.getKey())) {
                    continue;
                }
                if (entry.getValue() == null || sender.hasPermission(entry.getValue())) {
                    String helpKey = HELP_KEYS.get(entry.getKey());
                    if (helpKey != null) {
                        sender.sendMessage(format(sender, helpKey, Map.of()));
                    }
                }
            }
        } else {
            for (String helpKey : HELP_KEYS.values()) {
                sender.sendMessage(format(sender, helpKey, Map.of()));
            }
        }
        sender.sendMessage(format(sender, "command.help-fuel-tip", Map.of()));
    }

    private int parseChargeArg(CommandSender sender, String[] args, int index, TierConfig tier) {
        if (args.length > index) {
            try {
                return Integer.parseInt(args[index]);
            } catch (NumberFormatException exception) {
                sender.sendMessage(format(sender, "command.invalid-charge", Map.of("value", args[index])));
                return -1;
            }
        }
        return tier.getMaxCharge() / 2;
    }

    private String formatHoldMode(HoldMode holdMode) {
        return holdMode.name().toLowerCase(Locale.ROOT).replace('_', ' ');
    }

    private boolean shouldFilterCommands(CommandSender sender) {
        return plugin.getConfigManager().getMagnetConfig().getCommands().isFilterByPermission();
    }

    private boolean canSeeSubcommand(CommandSender sender, String subcommand) {
        if (!shouldFilterCommands(sender)) {
            return true;
        }
        String permission = SUBCOMMAND_PERMISSIONS.get(subcommand);
        return permission == null || sender.hasPermission(permission);
    }

    private String format(CommandSender sender, String key, Map<String, String> placeholders) {
        return TextUtil.color(plugin.getConfigManager().getMessagesConfig().format(key, placeholders));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subs = new ArrayList<>(SUBCOMMAND_PERMISSIONS.keySet());
            subs.add("help");
            return filter(subs.stream().filter(sub -> canSeeSubcommand(sender, sub)).collect(Collectors.toList()), args[0]);
        }
        if (!canSeeSubcommand(sender, args[0].toLowerCase(Locale.ROOT))) {
            return List.of();
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("unlock"))) {
            return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("giveall") || args[0].equalsIgnoreCase("unlockall"))) {
            return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("unlock"))) {
            List<String> tiers = new ArrayList<>(plugin.getConfigManager().getMagnetConfig().getTiers().keySet());
            tiers.add(ALL_TIERS);
            return filter(tiers, args[2]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("startup-message")) {
            return filter(List.of("on", "off", "toggle"), args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            return filter(List.of("nearby"), args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("filter")) {
            return filter(List.of("clear", "help"), args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("import")) {
            return filter(List.of("blacklist", "filter", "filter-preset"), args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("import") && "filter-preset".equalsIgnoreCase(args[1])) {
            return filter(plugin.getFilterPresetService().getPresetNames(), args[2]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("import") && "blacklist".equalsIgnoreCase(args[1])) {
            return filterMaterialNames(args[2]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("import") && "filter".equalsIgnoreCase(args[1])) {
            return filterMaterialNames(args[2]);
        }
        return List.of();
    }

    private List<String> filterMaterialNames(String partial) {
        int commaIndex = partial.lastIndexOf(',');
        String prefix = commaIndex >= 0 ? partial.substring(commaIndex + 1) : partial;
        String lowerPrefix = prefix.toLowerCase(Locale.ROOT);
        return java.util.Arrays.stream(Material.values())
                .map(Material::name)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(lowerPrefix))
                .limit(20)
                .map(name -> commaIndex >= 0 ? partial.substring(0, commaIndex + 1) + name : name)
                .collect(Collectors.toList());
    }

    private List<String> filter(List<String> options, String input) {
        String lower = input.toLowerCase(Locale.ROOT);
        return options.stream().filter(option -> option.toLowerCase(Locale.ROOT).startsWith(lower)).collect(Collectors.toList());
    }
}
