package com.rmh.itemmagnet.api.event;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public final class ItemMagnetPullEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Item itemEntity;
    private final ItemStack itemStack;
    private boolean cancelled;

    public ItemMagnetPullEvent(Player player, Item itemEntity, ItemStack itemStack) {
        this.player = player;
        this.itemEntity = itemEntity;
        this.itemStack = itemStack;
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
