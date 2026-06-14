package com.rmh.itemmagnet.unlock;

import com.rmh.itemmagnet.config.TierConfig;
import org.bukkit.entity.Player;

public interface UnlockProvider {

    boolean isUnlocked(Player player, TierConfig tier);

    void grantUnlock(Player player, TierConfig tier);
}
