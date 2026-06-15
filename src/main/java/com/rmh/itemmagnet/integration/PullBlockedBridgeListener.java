package com.rmh.itemmagnet.integration;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.api.event.ItemMagnetPullBlockedEvent;
import com.rmh.itemmagnet.config.BlockedProgressRule;
import com.rmh.itemmagnet.config.CmiIntegrationConfig;
import com.rmh.itemmagnet.config.CmiProgressOnBlockedConfig;
import com.rmh.itemmagnet.config.QuestsIntegrationConfig;
import com.rmh.itemmagnet.config.QuestsProgressOnBlockedConfig;
import com.rmh.itemmagnet.filter.PullBlockReason;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PullBlockedBridgeListener implements Listener {

    private static final long THROTTLE_MS = 1000L;

    private final ItemMagnetPlugin plugin;
    private final Map<String, Long> throttle = new ConcurrentHashMap<>();

    private final boolean cmiAvailable;
    private final Method cmiGetInstance;
    private final Method cmiGetPlayerManager;
    private final Method cmiGetUser;
    private final Class<?> cmiStatsClass;
    private final Class<?> cmiStatisticClass;
    private final Method cmiGetStat;
    private final Method cmiSetStat;

    private final boolean questsAvailable;
    private final Object questsPlugin;
    private final Method questsGetQuester;
    private final Method questGetId;
    private final Method questerGetCurrentQuests;
    private final Method questerIncrementObjective;
    private final Method questsGetLoadedQuests;

    public PullBlockedBridgeListener(ItemMagnetPlugin plugin) {
        this.plugin = plugin;

        Method getInstance = null;
        Method getPlayerManager = null;
        Method getUser = null;
        Class<?> statsClass = null;
        Class<?> statisticClass = null;
        Method getStat = null;
        Method setStat = null;
        boolean cmiReady = false;

        if (Bukkit.getPluginManager().getPlugin("CMI") != null) {
            try {
                Class<?> cmiClass = Class.forName("com.Zrips.CMI.CMI");
                getInstance = cmiClass.getMethod("getInstance");
                getPlayerManager = cmiClass.getMethod("getPlayerManager");
                Object playerManager = getPlayerManager.invoke(getInstance.invoke(null));
                getUser = playerManager.getClass().getMethod("getUser", Player.class);
                statsClass = Class.forName("com.Zrips.CMI.Modules.Statistics.CMIStats");
                statisticClass = Class.forName("com.Zrips.CMI.Modules.Statistics.StatsManager$CMIStatistic");
                getStat = statsClass.getMethod("getStat", getUser.getReturnType(), statisticClass, String.class);
                setStat = statsClass.getMethod("setStat", getUser.getReturnType(), statisticClass, String.class, long.class);
                cmiReady = true;
            } catch (ReflectiveOperationException exception) {
                plugin.getLogger().warning("CMI pull-blocked bridge unavailable: " + exception.getMessage());
            }
        }

        this.cmiAvailable = cmiReady;
        this.cmiGetInstance = getInstance;
        this.cmiGetPlayerManager = getPlayerManager;
        this.cmiGetUser = getUser;
        this.cmiStatsClass = statsClass;
        this.cmiStatisticClass = statisticClass;
        this.cmiGetStat = getStat;
        this.cmiSetStat = setStat;

        Object quests = null;
        Method getQuester = null;
        Method getId = null;
        Method getCurrentQuests = null;
        Method incrementObjective = null;
        Method getLoadedQuests = null;
        boolean questsReady = false;

        if (Bukkit.getPluginManager().getPlugin("Quests") != null) {
            try {
                Class<?> questsClass = Class.forName("me.pikamug.quests.BukkitQuestsPlugin");
                quests = Bukkit.getPluginManager().getPlugin("Quests");
                getQuester = questsClass.getMethod("getQuester", UUID.class);
                getLoadedQuests = questsClass.getMethod("getLoadedQuests");
                Class<?> questClass = Class.forName("me.pikamug.quests.modules.storage.Quest");
                getId = questClass.getMethod("getId");
                Class<?> questerClass = Class.forName("me.pikamug.quests.player.BukkitQuester");
                getCurrentQuests = questerClass.getMethod("getCurrentQuests");
                Class<?> customObjectiveClass = Class.forName("me.pikamug.quests.module.BukkitCustomObjective");
                incrementObjective = questerClass.getMethod(
                        "incrementObjective",
                        UUID.class,
                        customObjectiveClass,
                        questClass,
                        int.class
                );
                questsReady = true;
            } catch (ReflectiveOperationException exception) {
                plugin.getLogger().warning("Quests pull-blocked bridge unavailable: " + exception.getMessage());
            }
        }

        this.questsAvailable = questsReady;
        this.questsPlugin = quests;
        this.questsGetQuester = getQuester;
        this.questGetId = getId;
        this.questerGetCurrentQuests = getCurrentQuests;
        this.questerIncrementObjective = incrementObjective;
        this.questsGetLoadedQuests = getLoadedQuests;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPullBlocked(ItemMagnetPullBlockedEvent event) {
        Player player = event.getPlayer();
        PullBlockReason reason = event.getReason();
        String materialName = resolveMaterialName(event.getItemStack());
        UUID playerId = player.getUniqueId();

        QuestsIntegrationConfig questsConfig = plugin.getConfigManager().getMagnetConfig().getQuests();
        if (questsConfig.isEnabled() && questsConfig.getProgressOnBlocked().isEnabled()) {
            handleQuestsRules(player, playerId, reason, materialName, questsConfig.getProgressOnBlocked());
        }

        CmiIntegrationConfig cmiConfig = plugin.getConfigManager().getMagnetConfig().getCmi();
        if (cmiConfig.getProgressOnBlocked().isEnabled()) {
            handleCmiRules(player, playerId, reason, materialName, cmiConfig.getProgressOnBlocked());
        }
    }

    private void handleQuestsRules(
            Player player,
            UUID playerId,
            PullBlockReason reason,
            String materialName,
            QuestsProgressOnBlockedConfig config
    ) {
        if (!questsAvailable || config.getRules().isEmpty()) {
            return;
        }
        for (Map.Entry<String, BlockedProgressRule> entry : config.getRules().entrySet()) {
            BlockedProgressRule rule = entry.getValue();
            if (!rule.matches(reason, materialName)) {
                continue;
            }
            if (isThrottled(playerId, "quests:" + entry.getKey(), materialName)) {
                continue;
            }
            applyQuestProgress(player, rule);
        }
    }

    private void handleCmiRules(
            Player player,
            UUID playerId,
            PullBlockReason reason,
            String materialName,
            CmiProgressOnBlockedConfig config
    ) {
        if (!cmiAvailable || config.getRules().isEmpty()) {
            return;
        }
        for (Map.Entry<String, BlockedProgressRule> entry : config.getRules().entrySet()) {
            BlockedProgressRule rule = entry.getValue();
            if (!rule.matches(reason, materialName)) {
                continue;
            }
            if (isThrottled(playerId, "cmi:" + entry.getKey(), materialName)) {
                continue;
            }
            applyCmiProgress(player, rule);
        }
    }

    private void applyQuestProgress(Player player, BlockedProgressRule rule) {
        if (rule.getQuestId() == null || rule.getQuestId().isBlank()) {
            return;
        }
        try {
            Object quester = questsGetQuester.invoke(questsPlugin, player.getUniqueId());
            if (quester == null) {
                return;
            }
            Object targetQuest = findQuestById(rule.getQuestId());
            if (targetQuest == null) {
                return;
            }
            Map<?, ?> currentQuests = (Map<?, ?>) questerGetCurrentQuests.invoke(quester);
            if (!currentQuests.containsKey(targetQuest)) {
                return;
            }
            Method getCurrentObjectives = quester.getClass().getMethod("getCurrentObjectives", targetQuest.getClass(), boolean.class, boolean.class);
            Iterable<?> objectives = (Iterable<?>) getCurrentObjectives.invoke(quester, targetQuest, false, true);
            for (Object objective : objectives) {
                Method getType = objective.getClass().getMethod("getType");
                Object type = getType.invoke(objective);
                if (type == null || !"CUSTOM".equals(type.toString())) {
                    continue;
                }
                Method getModule = objective.getClass().getMethod("getModule");
                Object module = getModule.invoke(objective);
                if (module == null) {
                    continue;
                }
                questerIncrementObjective.invoke(
                        quester,
                        player.getUniqueId(),
                        module,
                        targetQuest,
                        Math.max(1, rule.getAmount())
                );
                return;
            }
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Quests pull-blocked progress failed: " + exception.getMessage());
        }
    }

    private Object findQuestById(String questId) throws ReflectiveOperationException {
        Iterable<?> quests = (Iterable<?>) questsGetLoadedQuests.invoke(questsPlugin);
        for (Object quest : quests) {
            String id = (String) questGetId.invoke(quest);
            if (questId.equals(id)) {
                return quest;
            }
        }
        return null;
    }

    private void applyCmiProgress(Player player, BlockedProgressRule rule) {
        if (rule.getStat() == null || rule.getStat().isBlank()) {
            return;
        }
        try {
            Object cmi = cmiGetInstance.invoke(null);
            Object playerManager = cmiGetPlayerManager.invoke(cmi);
            Object user = cmiGetUser.invoke(playerManager, player);
            if (user == null) {
                return;
            }
            Object statistic = Enum.valueOf(cmiStatisticClass.asSubclass(Enum.class), rule.getStat().toUpperCase(Locale.ROOT));
            String subStat = rule.getSubStat() == null ? "unknown" : rule.getSubStat();
            long current = (long) cmiGetStat.invoke(null, user, statistic, subStat);
            cmiSetStat.invoke(null, user, statistic, subStat, current + Math.max(1, rule.getAmount()));
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("CMI pull-blocked progress failed: " + exception.getMessage());
        }
    }

    private boolean isThrottled(UUID playerId, String ruleId, String materialName) {
        String key = playerId + ":" + ruleId + ":" + (materialName == null ? "any" : materialName);
        long now = System.currentTimeMillis();
        Long previous = throttle.put(key, now);
        return previous != null && now - previous < THROTTLE_MS;
    }

    private String resolveMaterialName(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return null;
        }
        return itemStack.getType().name();
    }
}
