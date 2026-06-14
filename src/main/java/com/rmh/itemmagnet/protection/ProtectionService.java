package com.rmh.itemmagnet.protection;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.CheckLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class ProtectionService {

    private final ItemMagnetPlugin plugin;
    private final LandsHook landsHook;
    private final WorldGuardHook worldGuardHook;
    private final TownyHook townyHook;
    private final GriefPreventionHook griefPreventionHook;

    public ProtectionService(
            ItemMagnetPlugin plugin,
            LandsHook landsHook,
            WorldGuardHook worldGuardHook,
            TownyHook townyHook,
            GriefPreventionHook griefPreventionHook
    ) {
        this.plugin = plugin;
        this.landsHook = landsHook;
        this.worldGuardHook = worldGuardHook;
        this.townyHook = townyHook;
        this.griefPreventionHook = griefPreventionHook;
    }

    public boolean canPull(Player player, Location itemLocation) {
        if (!player.hasPermission("itemmagnet.use")) {
            return false;
        }

        CheckLocation landsCheck = plugin.getConfigManager().getMagnetConfig().getLands().getCheckAt();
        CheckLocation wgCheck = plugin.getConfigManager().getMagnetConfig().getWorldGuard().getCheckAt();

        if (shouldCheckAtItem(landsCheck) && !landsHook.canPull(player, itemLocation)) {
            return false;
        }
        if (shouldCheckAtItem(wgCheck) && !worldGuardHook.canPull(player, itemLocation)) {
            return false;
        }
        if (!townyHook.canPull(player, itemLocation)) {
            return false;
        }
        if (!griefPreventionHook.canPull(player, itemLocation)) {
            return false;
        }

        Location playerLocation = player.getLocation();
        if (shouldCheckAtPlayer(landsCheck) && !landsHook.canPull(player, playerLocation)) {
            return false;
        }
        if (shouldCheckAtPlayer(wgCheck) && !worldGuardHook.canPull(player, playerLocation)) {
            return false;
        }
        if (!townyHook.canPull(player, playerLocation)) {
            return false;
        }
        if (!griefPreventionHook.canPull(player, playerLocation)) {
            return false;
        }

        if (plugin.getConfigManager().getMagnetConfig().getLands().isRequirePlayerInAllowedLand()) {
            if (!landsHook.canPull(player, playerLocation)) {
                return false;
            }
        }

        return true;
    }

    public boolean canUseAtPlayerLocation(Player player) {
        return landsHook.canPull(player, player.getLocation())
                && worldGuardHook.canPull(player, player.getLocation())
                && townyHook.canPull(player, player.getLocation())
                && griefPreventionHook.canPull(player, player.getLocation());
    }

    private boolean shouldCheckAtItem(CheckLocation location) {
        return location == CheckLocation.ITEM || location == CheckLocation.BOTH;
    }

    private boolean shouldCheckAtPlayer(CheckLocation location) {
        return location == CheckLocation.PLAYER || location == CheckLocation.BOTH;
    }

    public LandsHook getLandsHook() {
        return landsHook;
    }

    public WorldGuardHook getWorldGuardHook() {
        return worldGuardHook;
    }

    public TownyHook getTownyHook() {
        return townyHook;
    }

    public GriefPreventionHook getGriefPreventionHook() {
        return griefPreventionHook;
    }
}
