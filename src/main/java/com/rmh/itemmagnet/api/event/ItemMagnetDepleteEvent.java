package com.rmh.itemmagnet.api.event;

import com.rmh.itemmagnet.config.TierConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class ItemMagnetDepleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final TierConfig tier;

    public ItemMagnetDepleteEvent(Player player, TierConfig tier) {
        this.player = player;
        this.tier = tier;
    }

    public Player getPlayer() {
        return player;
    }

    public TierConfig getTier() {
        return tier;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
