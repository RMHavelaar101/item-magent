package com.rmh.itemmagnet.unlock;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.config.UnlockConfig;
import com.rmh.itemmagnet.config.UnlockType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class UnlockService {

    private final ItemMagnetPlugin plugin;
    private final Set<String> commandUnlocks = new HashSet<>();

    public UnlockService(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isUnlocked(Player player, TierConfig tier) {
        UnlockConfig unlock = tier.getUnlock();
        return switch (unlock.getType()) {
            case NONE -> true;
            case PERMISSION -> player.hasPermission(unlock.getPermission());
            case ADVANCEMENT -> hasAdvancement(player, unlock.getAdvancement());
            case CMI_STAT -> checkCmiStat(player, unlock);
            case CMI_RANK -> checkCmiRank(player, unlock);
            case COMMAND -> commandUnlocks.contains(unlockKey(player, tier))
                    || player.hasPermission("itemmagnet.unlock." + tier.getId());
        };
    }

    public void grantUnlock(Player player, TierConfig tier) {
        commandUnlocks.add(unlockKey(player, tier));
        discoverRecipe(player, tier);
    }

    private String unlockKey(Player player, TierConfig tier) {
        return player.getUniqueId() + ":" + tier.getId();
    }

    public void discoverRecipesOnJoin(Player player) {
        for (TierConfig tier : plugin.getConfigManager().getMagnetConfig().getTiers().values()) {
            if (isUnlocked(player, tier)) {
                discoverRecipe(player, tier);
            }
        }
    }

    public void discoverRecipe(Player player, TierConfig tier) {
        NamespacedKey key = new NamespacedKey(plugin, tier.getId());
        player.discoverRecipe(key);
    }

    private boolean hasAdvancement(Player player, String advancementKey) {
        if (advancementKey == null || advancementKey.isBlank()) {
            return false;
        }
        NamespacedKey key = NamespacedKey.fromString(advancementKey);
        if (key == null) {
            return false;
        }
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement == null) {
            return false;
        }
        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        return progress.isDone();
    }

    private boolean checkCmiStat(Player player, UnlockConfig unlock) {
        if (Bukkit.getPluginManager().getPlugin("CMI") == null) {
            return false;
        }
        try {
            Class<?> cmiClass = Class.forName("com.Zrips.CMI.CMI");
            Object cmi = cmiClass.getMethod("getInstance").invoke(null);
            Object user = cmiClass.getMethod("getPlayerManager").invoke(cmi);
            user = user.getClass().getMethod("getUser", Player.class).invoke(user, player);
            if (user == null) {
                return false;
            }
            Class<?> statsClass = Class.forName("com.Zrips.CMI.Modules.Statistics.CMIStats");
            Class<?> statisticClass = Class.forName("com.Zrips.CMI.Modules.Statistics.StatsManager$CMIStatistic");
            Object statistic = Enum.valueOf(statisticClass.asSubclass(Enum.class), unlock.getStat().toUpperCase(Locale.ROOT));
            long value = (long) statsClass.getMethod("getStat", user.getClass(), statisticClass, String.class)
                    .invoke(null, user, statistic, unlock.getSub());
            return value >= unlock.getAmount();
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("CMI stat unlock check failed: " + exception.getMessage());
            return false;
        }
    }

    private boolean checkCmiRank(Player player, UnlockConfig unlock) {
        if (unlock.getRank() == null) {
            return false;
        }
        return player.hasPermission("cmi.rank." + unlock.getRank());
    }
}
