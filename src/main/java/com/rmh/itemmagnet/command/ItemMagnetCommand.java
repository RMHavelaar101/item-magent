package com.rmh.itemmagnet.command;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.MessagesConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.item.MagnetData;
import com.rmh.itemmagnet.item.MagnetItemService;
import com.rmh.itemmagnet.magnet.RadiusCalculator;
import com.rmh.itemmagnet.protection.ProtectionService;
import com.rmh.itemmagnet.unlock.UnlockService;
import com.rmh.itemmagnet.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class ItemMagnetCommand implements CommandExecutor, TabCompleter {

    private final ItemMagnetPlugin plugin;
    private final MagnetItemService itemService;
    private final UnlockService unlockService;
    private final ProtectionService protectionService;

    public ItemMagnetCommand(
            ItemMagnetPlugin plugin,
            MagnetItemService itemService,
            UnlockService unlockService,
            ProtectionService protectionService
    ) {
        this.plugin = plugin;
        this.itemService = itemService;
        this.unlockService = unlockService;
        this.protectionService = protectionService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MessagesConfig messages = plugin.getConfigManager().getMessagesConfig();
        if (args.length == 0) {
            sender.sendMessage(format(sender, "command.usage", Map.of()));
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        return switch (sub) {
            case "reload" -> handleReload(sender, messages);
            case "version" -> handleVersion(sender, messages);
            case "give" -> handleGive(sender, args, messages);
            case "unlock" -> handleUnlock(sender, args, messages);
            case "debug" -> handleDebug(sender, messages);
            case "help" -> {
                sender.sendMessage(format(sender, "command.usage", Map.of()));
                yield true;
            }
            default -> {
                sender.sendMessage(format(sender, "command.usage", Map.of()));
                yield true;
            }
        };
    }

    private boolean handleReload(CommandSender sender, MessagesConfig messages) {
        if (!sender.hasPermission("itemmagnet.reload")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        plugin.reloadPlugin();
        sender.sendMessage(format(sender, "general.reload-success", Map.of()));
        return true;
    }

    private boolean handleVersion(CommandSender sender, MessagesConfig messages) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("version", plugin.getPluginMeta().getVersion());
        placeholders.put("paper", Bukkit.getVersion());
        placeholders.put("lands", String.valueOf(protectionService.getLandsHook().isAvailable()));
        placeholders.put("worldguard", String.valueOf(protectionService.getWorldGuardHook().isAvailable()));
        placeholders.put("cmi", String.valueOf(Bukkit.getPluginManager().getPlugin("CMI") != null));
        sender.sendMessage(format(sender, "command.version", placeholders));
        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args, MessagesConfig messages) {
        if (!sender.hasPermission("itemmagnet.give")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(format(sender, "command.usage", Map.of()));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(format(sender, "command.player-not-found", Map.of("player", args[1])));
            return true;
        }
        TierConfig tier = plugin.getConfigManager().getMagnetConfig().getTier(args[2].toLowerCase(Locale.ROOT));
        if (tier == null) {
            sender.sendMessage(format(sender, "command.unknown-tier", Map.of("tier", args[2])));
            return true;
        }
        int charge = args.length >= 4 ? Integer.parseInt(args[3]) : tier.getMaxCharge() / 2;
        ItemStack item = itemService.create(tier, charge);
        target.getInventory().addItem(item);
        sender.sendMessage(format(sender, "command.give-success", Map.of("tier", tier.getId(), "player", target.getName())));
        return true;
    }

    private boolean handleUnlock(CommandSender sender, String[] args, MessagesConfig messages) {
        if (!sender.hasPermission("itemmagnet.unlock")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(format(sender, "command.usage", Map.of()));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(format(sender, "command.player-not-found", Map.of("player", args[1])));
            return true;
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

    private boolean handleDebug(CommandSender sender, MessagesConfig messages) {
        if (!sender.hasPermission("itemmagnet.debug")) {
            sender.sendMessage(format(sender, "general.no-permission", Map.of()));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(format(sender, "general.player-only", Map.of()));
            return true;
        }

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        var magnetOptional = itemService.read(mainHand);
        if (magnetOptional.isEmpty()) {
            sender.sendMessage(format(sender, "command.usage", Map.of()));
            return true;
        }

        MagnetData magnet = magnetOptional.get();
        long tick = plugin.getServer().getCurrentTick();
        double radius = RadiusCalculator.calculateEffectiveRadius(
                plugin.getConfigManager().getMagnetConfig(),
                magnet.getTier(),
                magnet,
                player,
                tick
        );
        boolean allowed = protectionService.canPull(player, player.getLocation());
        boolean afk = plugin.getConfigManager().getMagnetConfig().getAntiAfk().isEnabled()
                && plugin.getMagnetService().getAfkTracker().isAfk(
                player,
                plugin.getConfigManager().getMagnetConfig().getAntiAfk().getRequiredBlocksMoved(),
                plugin.getConfigManager().getMagnetConfig().getAntiAfk().getWindowSeconds()
        );

        sender.sendMessage(format(sender, "command.debug-header", Map.of()));
        sender.sendMessage(format(sender, "command.debug-tier", Map.of("tier", magnet.getTier().getId())));
        sender.sendMessage(format(sender, "command.debug-charge", Map.of(
                "charge", String.valueOf(magnet.getCharge()),
                "max_charge", String.valueOf(magnet.getTier().getMaxCharge())
        )));
        sender.sendMessage(format(sender, "command.debug-boost", Map.of(
                "boost", String.valueOf(magnet.getBoostLevel()),
                "expiry", String.valueOf(magnet.getBoostExpiryTick())
        )));
        sender.sendMessage(format(sender, "command.debug-radius", Map.of("radius", String.format(Locale.ROOT, "%.2f", radius))));
        sender.sendMessage(format(sender, "command.debug-protection", Map.of("allowed", String.valueOf(allowed))));
        sender.sendMessage(format(sender, "command.debug-afk", Map.of("afk", String.valueOf(afk))));
        return true;
    }

    private String format(CommandSender sender, String key, Map<String, String> placeholders) {
        return TextUtil.color(plugin.getConfigManager().getMessagesConfig().format(key, placeholders));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(List.of("reload", "version", "give", "unlock", "debug", "help"), args[0]);
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("unlock"))) {
            return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("unlock"))) {
            return filter(new ArrayList<>(plugin.getConfigManager().getMagnetConfig().getTiers().keySet()), args[2]);
        }
        return List.of();
    }

    private List<String> filter(List<String> options, String input) {
        String lower = input.toLowerCase(Locale.ROOT);
        return options.stream().filter(option -> option.toLowerCase(Locale.ROOT).startsWith(lower)).collect(Collectors.toList());
    }
}
