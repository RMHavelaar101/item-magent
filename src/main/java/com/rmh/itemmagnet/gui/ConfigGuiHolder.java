package com.rmh.itemmagnet.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ConfigGuiHolder implements InventoryHolder {

    private final ConfigGuiType type;
    private final String context;
    private Inventory inventory;

    public ConfigGuiHolder(ConfigGuiType type) {
        this(type, "");
    }

    public ConfigGuiHolder(ConfigGuiType type, String context) {
        this.type = type;
        this.context = context == null ? "" : context;
    }

    public ConfigGuiType getType() {
        return type;
    }

    public String getContext() {
        return context;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        if (inventory == null) {
            inventory = Bukkit.createInventory(this, 54);
        }
        return inventory;
    }
}
