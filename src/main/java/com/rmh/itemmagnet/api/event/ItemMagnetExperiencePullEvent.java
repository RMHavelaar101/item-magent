package com.rmh.itemmagnet.api.event;

import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class ItemMagnetExperiencePullEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final ExperienceOrb experienceOrb;
    private final int experience;
    private boolean cancelled;

    public ItemMagnetExperiencePullEvent(Player player, ExperienceOrb experienceOrb, int experience) {
        this.player = player;
        this.experienceOrb = experienceOrb;
        this.experience = experience;
    }

    public Player getPlayer() {
        return player;
    }

    public ExperienceOrb getExperienceOrb() {
        return experienceOrb;
    }

    public int getExperience() {
        return experience;
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
