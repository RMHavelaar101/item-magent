package com.rmh.itemmagnet.filter;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class PlayerFilterGuiHolder implements InventoryHolder {

    public enum GuiMode {
        MAIN,
        PRESET_PICKER,
        PRESET_CONFIRM
    }

    private final GuiMode mode;
    private final int page;
    private final String pendingPresetId;
    private Inventory inventory;

    public PlayerFilterGuiHolder(GuiMode mode, int page, String pendingPresetId) {
        this.mode = mode;
        this.page = page;
        this.pendingPresetId = pendingPresetId;
    }

    public PlayerFilterGuiHolder(GuiMode mode, int page) {
        this(mode, page, null);
    }

    public PlayerFilterGuiHolder(int page) {
        this(GuiMode.MAIN, page, null);
    }

    public GuiMode getMode() {
        return mode;
    }

    public int getPage() {
        return page;
    }

    public String getPendingPresetId() {
        return pendingPresetId;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
