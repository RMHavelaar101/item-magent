package com.rmh.itemmagnet.protection;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.ClaimedLandPolicy;
import com.rmh.itemmagnet.config.TownyConfig;
import com.rmh.itemmagnet.config.WildernessPolicy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.UUID;

public final class TownyHook implements ProtectionHook {

    private final ItemMagnetPlugin plugin;
    private Object townyApi;
    private Method isWildernessMethod;
    private Method getTownMethod;
    private Method getResidentMethod;
    private Method hasTownMethod;
    private Method getTownUuidMethod;
    private Method getMayorUuidMethod;

    public TownyHook(ItemMagnetPlugin plugin) {
        this.plugin = plugin;
        initialize();
    }

    private void initialize() {
        if (Bukkit.getPluginManager().getPlugin("Towny") == null) {
            return;
        }
        try {
            Class<?> apiClass = Class.forName("com.palmergames.bukkit.towny.TownyAPI");
            townyApi = apiClass.getMethod("getInstance").invoke(null);
            isWildernessMethod = apiClass.getMethod("isWilderness", Location.class);
            getTownMethod = apiClass.getMethod("getTown", Location.class);
            getResidentMethod = apiClass.getMethod("getResident", UUID.class);

            Class<?> townClass = Class.forName("com.palmergames.bukkit.towny.object.Town");
            getTownUuidMethod = townClass.getMethod("getUUID");

            Class<?> residentClass = Class.forName("com.palmergames.bukkit.towny.object.Resident");
            hasTownMethod = residentClass.getMethod("hasTown");
            getMayorUuidMethod = residentClass.getMethod("getUUID");
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Failed to initialize Towny hook: " + exception.getMessage());
            townyApi = null;
        }
    }

    @Override
    public boolean isAvailable() {
        return townyApi != null;
    }

    @Override
    public boolean canPull(Player player, Location location) {
        if (!isAvailable()) {
            return true;
        }

        TownyConfig config = plugin.getConfigManager().getMagnetConfig().getTowny();
        if (!config.isEnabled()) {
            return true;
        }
        if (player.hasPermission("itemmagnet.bypass.towny")) {
            return true;
        }

        try {
            boolean wilderness = (boolean) isWildernessMethod.invoke(townyApi, location);
            if (wilderness) {
                return switch (config.getWilderness()) {
                    case ALLOW -> true;
                    case DENY -> false;
                    case PERMISSION -> player.hasPermission(config.getWildernessPermission());
                };
            }

            Object town = getTownMethod.invoke(townyApi, location);
            if (town == null) {
                return true;
            }

            return switch (config.getClaimedTown()) {
                case DENY -> false;
                case OWNER_ONLY -> isTownMayor(player, town);
                case MEMBER_ONLY -> isTownMember(player, town);
                case RESPECT_FLAGS -> isTownMember(player, town);
            };
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("Towny evaluation failed: " + exception.getMessage());
            return false;
        }
    }

    private boolean isTownMember(Player player, Object town) throws ReflectiveOperationException {
        Object resident = getResidentMethod.invoke(townyApi, player.getUniqueId());
        if (resident == null) {
            return false;
        }
        if (!(boolean) hasTownMethod.invoke(resident)) {
            return false;
        }
        Method getTownMethodOnResident = resident.getClass().getMethod("getTownOrNull");
        Object residentTown = getTownMethodOnResident.invoke(resident);
        if (residentTown == null) {
            return false;
        }
        UUID townUuid = (UUID) getTownUuidMethod.invoke(town);
        UUID residentTownUuid = (UUID) getTownUuidMethod.invoke(residentTown);
        return townUuid.equals(residentTownUuid);
    }

    private boolean isTownMayor(Player player, Object town) throws ReflectiveOperationException {
        Method getMayorMethod = town.getClass().getMethod("getMayor");
        Object mayor = getMayorMethod.invoke(town);
        if (mayor == null) {
            return false;
        }
        UUID mayorUuid = (UUID) getMayorUuidMethod.invoke(mayor);
        return player.getUniqueId().equals(mayorUuid);
    }

    public void reload() {
        initialize();
    }
}
