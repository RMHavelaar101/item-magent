package com.rmh.itemmagnet.api.event;

import com.rmh.itemmagnet.filter.PullBlockReason;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public final class ItemMagnetPullBlockedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Item itemEntity;
    private final ItemStack itemStack;
    private final PullBlockReason reason;

    public ItemMagnetPullBlockedEvent(Player player, Item itemEntity, ItemStack itemStack, PullBlockReason reason) {
        this.player = player;
        this.itemEntity = itemEntity;
        this.itemStack = itemStack;
        this.reason = reason;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getItemEntity() {
        return itemEntity;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public PullBlockReason getReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
