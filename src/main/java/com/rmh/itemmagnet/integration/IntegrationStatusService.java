package com.rmh.itemmagnet.integration;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.protection.GriefPreventionHook;
import com.rmh.itemmagnet.protection.LandsHook;
import com.rmh.itemmagnet.protection.PlotSquaredHook;
import com.rmh.itemmagnet.protection.ResidenceHook;
import com.rmh.itemmagnet.protection.SuperiorSkyblockHook;
import com.rmh.itemmagnet.protection.TownyHook;
import com.rmh.itemmagnet.protection.WorldGuardHook;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.Map;

public final class IntegrationStatusService {

    private final ItemMagnetPlugin plugin;
    private final LandsHook landsHook;
    private final WorldGuardHook worldGuardHook;
    private final TownyHook townyHook;
    private final GriefPreventionHook griefPreventionHook;
    private final ResidenceHook residenceHook;
    private final PlotSquaredHook plotSquaredHook;
    private final SuperiorSkyblockHook superiorSkyblockHook;

    public IntegrationStatusService(
            ItemMagnetPlugin plugin,
            LandsHook landsHook,
            WorldGuardHook worldGuardHook,
            TownyHook townyHook,
            GriefPreventionHook griefPreventionHook,
            ResidenceHook residenceHook,
            PlotSquaredHook plotSquaredHook,
            SuperiorSkyblockHook superiorSkyblockHook
    ) {
        this.plugin = plugin;
        this.landsHook = landsHook;
        this.worldGuardHook = worldGuardHook;
        this.townyHook = townyHook;
        this.griefPreventionHook = griefPreventionHook;
        this.residenceHook = residenceHook;
        this.plotSquaredHook = plotSquaredHook;
        this.superiorSkyblockHook = superiorSkyblockHook;
    }

    public Map<String, Boolean> getHookStatus() {
        Map<String, Boolean> status = new LinkedHashMap<>();
        status.put("lands", landsHook.isAvailable());
        status.put("worldguard", worldGuardHook.isAvailable());
        status.put("towny", townyHook.isAvailable());
        status.put("griefprevention", griefPreventionHook.isAvailable());
        status.put("residence", residenceHook.isAvailable());
        status.put("plotsquared", plotSquaredHook.isAvailable());
        status.put("superiorskyblock", superiorSkyblockHook.isAvailable());
        status.put("placeholderapi", Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null);
        status.put("cmi", Bukkit.getPluginManager().getPlugin("CMI") != null);
        status.put("luckperms", Bukkit.getPluginManager().getPlugin("LuckPerms") != null);
        status.put("mcmmo", Bukkit.getPluginManager().getPlugin("mcMMO") != null);
        status.put("quests", Bukkit.getPluginManager().getPlugin("Quests") != null);
        return status;
    }

    public String formatHookStatusLine() {
        Map<String, Boolean> status = getHookStatus();
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Boolean> entry : status.entrySet()) {
            if (!first) {
                builder.append(" | ");
            }
            builder.append(capitalize(entry.getKey())).append(": ").append(entry.getValue());
            first = false;
        }
        return builder.toString();
    }

    private String capitalize(String key) {
        if (key == null || key.isEmpty()) {
            return key;
        }
        return Character.toUpperCase(key.charAt(0)) + key.substring(1);
    }
}
