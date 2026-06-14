package com.rmh.itemmagnet.item;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public final class PdcKeys {

    public final NamespacedKey isMagnet;
    public final NamespacedKey tierId;
    public final NamespacedKey charge;
    public final NamespacedKey boostLevel;
    public final NamespacedKey boostExpiryTick;

    public PdcKeys(ItemMagnetPlugin plugin) {
        this.isMagnet = new NamespacedKey(plugin, "is_magnet");
        this.tierId = new NamespacedKey(plugin, "tier_id");
        this.charge = new NamespacedKey(plugin, "charge");
        this.boostLevel = new NamespacedKey(plugin, "boost_level");
        this.boostExpiryTick = new NamespacedKey(plugin, "boost_expiry_tick");
    }

    public static final PersistentDataType<Byte, Byte> BYTE = PersistentDataType.BYTE;
    public static final PersistentDataType<String, String> STRING = PersistentDataType.STRING;
    public static final PersistentDataType<Integer, Integer> INTEGER = PersistentDataType.INTEGER;
    public static final PersistentDataType<Long, Long> LONG = PersistentDataType.LONG;
}
