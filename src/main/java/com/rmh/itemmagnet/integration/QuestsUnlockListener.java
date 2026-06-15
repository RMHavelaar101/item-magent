package com.rmh.itemmagnet.integration;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.unlock.UnlockService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;

public final class QuestsUnlockListener implements Listener {

    private final ItemMagnetPlugin plugin;
    private final UnlockService unlockService;
    private final boolean questsAvailable;
    private final Method getPlayerMethod;
    private final Method getQuestMethod;
    private final Method getIdMethod;

    public QuestsUnlockListener(ItemMagnetPlugin plugin, UnlockService unlockService) {
        this.plugin = plugin;
        this.unlockService = unlockService;

        Method playerMethod = null;
        Method questMethod = null;
        Method idMethod = null;
        boolean available = false;

        if (Bukkit.getPluginManager().getPlugin("Quests") != null) {
            try {
                Class<? extends Event> eventClass = Class.forName("me.pikamug.quests.events.QuestCompleteEvent")
                        .asSubclass(Event.class);
                playerMethod = eventClass.getMethod("getPlayer");
                questMethod = eventClass.getMethod("getQuest");
                Class<?> questClass = Class.forName("me.pikamug.quests.modules.storage.Quest");
                idMethod = questClass.getMethod("getId");
                available = true;
                registerEvent(eventClass);
            } catch (ReflectiveOperationException exception) {
                plugin.getLogger().warning("Quests unlock bridge unavailable: " + exception.getMessage());
            }
        }

        this.questsAvailable = available;
        this.getPlayerMethod = playerMethod;
        this.getQuestMethod = questMethod;
        this.getIdMethod = idMethod;
    }

    public boolean isAvailable() {
        return questsAvailable;
    }

    private void registerEvent(Class<? extends Event> eventClass) {
        Bukkit.getPluginManager().registerEvent(
                eventClass,
                this,
                EventPriority.MONITOR,
                (listener, event) -> handleQuestComplete(event),
                plugin,
                false
        );
    }

    private void handleQuestComplete(org.bukkit.event.Event event) {
        var questsConfig = plugin.getConfigManager().getMagnetConfig().getQuests();
        if (!questsConfig.isEnabled() || questsConfig.getUnlockOnComplete().isEmpty()) {
            return;
        }

        try {
            Player player = (Player) getPlayerMethod.invoke(event);
            Object quest = getQuestMethod.invoke(event);
            String questId = (String) getIdMethod.invoke(quest);
            String tierId = questsConfig.getUnlockOnComplete().get(questId);
            if (tierId == null) {
                return;
            }
            TierConfig tier = plugin.getConfigManager().getMagnetConfig().getTier(tierId);
            if (tier != null) {
                unlockService.grantUnlock(player, tier);
            }
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Quests unlock bridge failed: " + exception.getMessage());
        }
    }

    public void register() {
        // Event registration happens in constructor when Quests is present.
    }
}
