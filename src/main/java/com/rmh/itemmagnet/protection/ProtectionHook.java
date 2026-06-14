package com.rmh.itemmagnet.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ProtectionHook {

    boolean isAvailable();

    boolean canPull(Player player, Location location);
}
