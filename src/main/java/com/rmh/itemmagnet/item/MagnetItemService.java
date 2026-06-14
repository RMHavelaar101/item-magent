package com.rmh.itemmagnet.item;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.FuelConfig;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.util.TextUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class MagnetItemService {

    private final ItemMagnetPlugin plugin;
    private final PdcKeys keys;

    public MagnetItemService(ItemMagnetPlugin plugin, PdcKeys keys) {
        this.plugin = plugin;
        this.keys = keys;
    }

    public boolean isMagnet(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        Byte marker = item.getItemMeta().getPersistentDataContainer().get(keys.isMagnet, PdcKeys.BYTE);
        return marker != null && marker == (byte) 1;
    }

    public Optional<MagnetData> read(ItemStack item) {
        if (!isMagnet(item)) {
            return Optional.empty();
        }
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String tierId = pdc.getOrDefault(keys.tierId, PdcKeys.STRING, "");
        TierConfig tier = plugin.getConfigManager().getMagnetConfig().getTier(tierId);
        if (tier == null) {
            return Optional.empty();
        }
        int charge = pdc.getOrDefault(keys.charge, PdcKeys.INTEGER, 0);
        int boostLevel = pdc.getOrDefault(keys.boostLevel, PdcKeys.INTEGER, 0);
        long boostExpiry = pdc.getOrDefault(keys.boostExpiryTick, PdcKeys.LONG, 0L);
        return Optional.of(new MagnetData(tier, charge, boostLevel, boostExpiry));
    }

    public ItemStack create(TierConfig tier, int charge) {
        ItemStack item = new ItemStack(tier.getMaterial());
        MagnetData data = new MagnetData(tier, Math.min(charge, tier.getMaxCharge()), 0, 0);
        write(item, data);
        return item;
    }

    public void write(ItemStack item, MagnetData data) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(keys.isMagnet, PdcKeys.BYTE, (byte) 1);
        pdc.set(keys.tierId, PdcKeys.STRING, data.getTier().getId());
        pdc.set(keys.charge, PdcKeys.INTEGER, Math.max(0, Math.min(data.getCharge(), data.getTier().getMaxCharge())));
        pdc.set(keys.boostLevel, PdcKeys.INTEGER, Math.max(0, data.getBoostLevel()));
        pdc.set(keys.boostExpiryTick, PdcKeys.LONG, data.getBoostExpiryTick());

        MagnetConfig config = plugin.getConfigManager().getMagnetConfig();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("charge", String.valueOf(data.getCharge()));
        placeholders.put("max_charge", String.valueOf(data.getTier().getMaxCharge()));
        placeholders.put("boost", data.getBoostLevel() > 0 ? "+" + data.getBoostLevel() : "None");

        meta.setDisplayName(TextUtil.color(TextUtil.applyPlaceholders(data.getTier().getDisplayName(), placeholders)));
        List<String> lore = new ArrayList<>();
        for (String line : data.getTier().getLore()) {
            lore.add(TextUtil.color(TextUtil.applyPlaceholders(line, placeholders)));
        }
        meta.setLore(lore);

        if (data.getTier().isEnchantGlint()) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (config.isShowChargeBar() && data.getTier().getMaxCharge() > 0 && meta instanceof Damageable damageable) {
            int max = data.getTier().getMaxCharge();
            int remaining = Math.max(0, data.getCharge());
            damageable.setMaxDamage(max);
            damageable.setDamage(max - remaining);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        item.setItemMeta(meta);
    }

    public boolean transferFuel(Player player, ItemStack mainHand, ItemStack offHand) {
        Optional<MagnetData> magnetDataOptional = read(mainHand);
        if (magnetDataOptional.isEmpty() || offHand == null || offHand.getType() == Material.AIR) {
            return false;
        }

        FuelConfig fuelConfig = plugin.getConfigManager().getMagnetConfig()
                .getFuel()
                .get(offHand.getType().name());
        if (fuelConfig == null) {
            return false;
        }

        MagnetData data = magnetDataOptional.get();
        int maxCharge = data.getTier().getMaxCharge();
        if (data.getCharge() >= maxCharge) {
            return false;
        }

        int transferable = Math.min(offHand.getAmount(), (int) Math.ceil((maxCharge - data.getCharge()) / (double) fuelConfig.getChargePerItem()));
        if (transferable <= 0) {
            return false;
        }

        int addedCharge = transferable * fuelConfig.getChargePerItem();
        data.setCharge(Math.min(maxCharge, data.getCharge() + addedCharge));

        if (fuelConfig.getBoostLevelAdd() > 0) {
            long expiry = plugin.getServer().getCurrentTick() + (fuelConfig.getBoostDurationSeconds() * 20L);
            data.setBoostLevel(data.getBoostLevel() + fuelConfig.getBoostLevelAdd());
            data.setBoostExpiryTick(Math.max(data.getBoostExpiryTick(), expiry));
        }

        write(mainHand, data);
        player.getInventory().setItemInMainHand(mainHand);

        offHand.setAmount(offHand.getAmount() - transferable);
        player.getInventory().setItemInOffHand(offHand.getAmount() > 0 ? offHand : null);
        return true;
    }

    public FuelConfig getFuelConfig(Material material) {
        return plugin.getConfigManager().getMagnetConfig().getFuel().get(material.name());
    }
}
