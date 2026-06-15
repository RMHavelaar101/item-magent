package com.rmh.itemmagnet.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class MagnetTierUnlockedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String tierId;

    public MagnetTierUnlockedEvent(Player player, String tierId) {
        this.player = player;
        this.tierId = tierId;
    }

    public Player getPlayer() {
        return player;
    }

    public String getTierId() {
        return tierId;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
