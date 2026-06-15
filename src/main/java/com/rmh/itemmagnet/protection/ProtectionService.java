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
    private final ResidenceHook residenceHook;
    private final PlotSquaredHook plotSquaredHook;
    private final SuperiorSkyblockHook superiorSkyblockHook;

    public ProtectionService(
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
        if (!residenceHook.canPull(player, itemLocation)) {
            return false;
        }
        if (!plotSquaredHook.canPull(player, itemLocation)) {
            return false;
        }
        if (!superiorSkyblockHook.canPull(player, itemLocation)) {
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
        if (!residenceHook.canPull(player, playerLocation)) {
            return false;
        }
        if (!plotSquaredHook.canPull(player, playerLocation)) {
            return false;
        }
        if (!superiorSkyblockHook.canPull(player, playerLocation)) {
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
        Location location = player.getLocation();
        return landsHook.canPull(player, location)
                && worldGuardHook.canPull(player, location)
                && townyHook.canPull(player, location)
                && griefPreventionHook.canPull(player, location)
                && residenceHook.canPull(player, location)
                && plotSquaredHook.canPull(player, location)
                && superiorSkyblockHook.canPull(player, location);
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

    public ResidenceHook getResidenceHook() {
        return residenceHook;
    }

    public PlotSquaredHook getPlotSquaredHook() {
        return plotSquaredHook;
    }

    public SuperiorSkyblockHook getSuperiorSkyblockHook() {
        return superiorSkyblockHook;
    }
}
