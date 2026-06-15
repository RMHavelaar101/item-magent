package com.rmh.itemmagnet.item;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.FuelConfig;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.item.LoreContext;
import com.rmh.itemmagnet.util.TextUtil;
import com.rmh.itemmagnet.magnet.FuelTransferHelper;
import com.rmh.itemmagnet.magnet.MagnetSlot;
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
        write(item, data, LoreContext.baseOnly(tier.getRadius()));
        return item;
    }

    public void write(ItemStack item, MagnetData data) {
        write(item, data, LoreContext.baseOnly(data.getTier().getRadius()));
    }

    public void write(ItemStack item, MagnetData data, LoreContext loreContext) {
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
        placeholders.put("radius", loreContext.formatRadius());
        placeholders.put("base_radius", loreContext.formatBaseRadius());

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

        if (config.isShowChargeBar()
                && data.getTier().getMaxCharge() > 0
                && data.getTier().getMaterial().getMaxDurability() > 0
                && meta instanceof Damageable damageable) {
            int max = data.getTier().getMaxCharge();
            int remaining = Math.max(0, data.getCharge());
            damageable.setMaxDamage(max);
            damageable.setDamage(max - remaining);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        item.setItemMeta(meta);
    }

    public FuelTransferResult transferFuel(Player player, MagnetSlot magnetSlot, ItemStack fuelStack, int fuelSlot) {
        ItemStack magnetStack = magnetSlot.getItemStack();
        Optional<MagnetData> magnetDataOptional = read(magnetStack);
        if (magnetDataOptional.isEmpty() || fuelStack == null || fuelStack.getType() == Material.AIR) {
            return FuelTransferResult.of(FuelTransferStatus.NO_MAGNET);
        }

        FuelConfig fuelConfig = plugin.getConfigManager().getMagnetConfig()
                .getFuel()
                .get(fuelStack.getType().name());
        if (fuelConfig == null) {
            return FuelTransferResult.of(FuelTransferStatus.INVALID_FUEL);
        }

        MagnetData data = magnetDataOptional.get();
        int maxCharge = data.getTier().getMaxCharge();
        if (data.getCharge() >= maxCharge) {
            return FuelTransferResult.of(FuelTransferStatus.FULL);
        }

        int transferable = Math.min(fuelStack.getAmount(), (int) Math.ceil((maxCharge - data.getCharge()) / (double) fuelConfig.getChargePerItem()));
        if (transferable <= 0) {
            return FuelTransferResult.of(FuelTransferStatus.FULL);
        }

        int addedCharge = transferable * fuelConfig.getChargePerItem();
        data.setCharge(Math.min(maxCharge, data.getCharge() + addedCharge));

        int boostDurationSeconds = 0;
        if (fuelConfig.getBoostLevelAdd() > 0) {
            long expiry = plugin.getServer().getCurrentTick() + (fuelConfig.getBoostDurationSeconds() * 20L);
            data.setBoostLevel(data.getBoostLevel() + fuelConfig.getBoostLevelAdd());
            data.setBoostExpiryTick(Math.max(data.getBoostExpiryTick(), expiry));
            boostDurationSeconds = fuelConfig.getBoostDurationSeconds();
        }

        write(magnetStack, data);
        player.getInventory().setItem(magnetSlot.getSlot(), magnetStack);

        fuelStack.setAmount(fuelStack.getAmount() - transferable);
        writeFuelToSlot(player, fuelSlot, fuelStack);
        return FuelTransferResult.success(boostDurationSeconds);
    }

    private void writeFuelToSlot(Player player, int fuelSlot, ItemStack fuelStack) {
        if (fuelSlot == FuelTransferHelper.OFF_HAND_SLOT) {
            player.getInventory().setItemInOffHand(fuelStack.getAmount() > 0 ? fuelStack : null);
            return;
        }
        player.getInventory().setItem(fuelSlot, fuelStack.getAmount() > 0 ? fuelStack : null);
    }

    public FuelConfig getFuelConfig(Material material) {
        return plugin.getConfigManager().getMagnetConfig().getFuel().get(material.name());
    }
}
