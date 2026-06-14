package com.rmh.itemmagnet.api;

import com.rmh.itemmagnet.ItemMagnetPlugin;

public final class ItemMagnetApi {

    private static ItemMagnetPlugin plugin;

    private ItemMagnetApi() {
    }

    public static void init(ItemMagnetPlugin itemMagnetPlugin) {
        plugin = itemMagnetPlugin;
    }

    public static ItemMagnetPlugin getPlugin() {
        return plugin;
    }
}
