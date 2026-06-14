package com.rmh.itemmagnet.api.event;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class ItemMagnetFuelAbsorbEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Item itemEntity;
    private final Material fuelType;
    private final int amount;
    private boolean cancelled;

    public ItemMagnetFuelAbsorbEvent(Player player, Item itemEntity, Material fuelType, int amount) {
        this.player = player;
        this.itemEntity = itemEntity;
        this.fuelType = fuelType;
        this.amount = amount;
    }

    public Player getPlayer() {
        return player;
    }

    public Item getItemEntity() {
        return itemEntity;
    }

    public Material getFuelType() {
        return fuelType;
    }

    public int getAmount() {
        return amount;
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
