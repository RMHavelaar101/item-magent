package com.rmh.itemmagnet.magnet;

import com.rmh.itemmagnet.item.MagnetData;
import org.bukkit.inventory.ItemStack;

public final class MagnetSlot {

    private final int slot;
    private final ItemStack itemStack;
    private final MagnetData data;

    public MagnetSlot(int slot, ItemStack itemStack, MagnetData data) {
        this.slot = slot;
        this.itemStack = itemStack;
        this.data = data;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public MagnetData getData() {
        return data;
    }
}
