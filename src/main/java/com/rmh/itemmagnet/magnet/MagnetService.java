package com.rmh.itemmagnet.magnet;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.AntiAfkConfig;
import com.rmh.itemmagnet.config.FuelConfig;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.config.MessagesConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.item.MagnetData;
import com.rmh.itemmagnet.item.MagnetItemService;
import com.rmh.itemmagnet.protection.ProtectionService;
import com.rmh.itemmagnet.util.TextUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class MagnetService extends BukkitRunnable {

    private final ItemMagnetPlugin plugin;
    private final MagnetItemService itemService;
    private final ProtectionService protectionService;
    private final AfkTracker afkTracker;
    private final Map<UUID, Long> denyCooldowns = new HashMap<>();
    private final Map<UUID, Double> drainAccumulator = new HashMap<>();

    public MagnetService(
            ItemMagnetPlugin plugin,
            MagnetItemService itemService,
            ProtectionService protectionService,
            AfkTracker afkTracker
    ) {
        this.plugin = plugin;
        this.itemService = itemService;
        this.protectionService = protectionService;
        this.afkTracker = afkTracker;
    }

    public void start() {
        int interval = plugin.getConfigManager().getMagnetConfig().getScanIntervalTicks();
        runTaskTimer(plugin, interval, interval);
    }

    @Override
    public void run() {
        MagnetConfig config = plugin.getConfigManager().getMagnetConfig();
        long currentTick = plugin.getServer().getCurrentTick();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            processPlayer(player, config, currentTick);
        }
    }

    private void processPlayer(Player player, MagnetConfig config, long currentTick) {
        if (!player.hasPermission("itemmagnet.use")) {
            return;
        }

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        Optional<MagnetData> magnetOptional = itemService.read(mainHand);
        if (magnetOptional.isEmpty()) {
            return;
        }

        if (config.isSneakToDisable() && player.isSneaking()) {
            return;
        }

        MagnetData magnet = magnetOptional.get();
        if (!RadiusCalculator.isHeightAllowed(config, player)) {
            sendMessage(player, "magnet.y-disabled", Map.of());
            return;
        }

        AntiAfkConfig antiAfk = config.getAntiAfk();
        boolean afkBlocked = antiAfk.isEnabled()
                && afkTracker.isAfk(player, antiAfk.getRequiredBlocksMoved(), antiAfk.getWindowSeconds());
        if (afkBlocked) {
            return;
        }

        if (magnet.getCharge() <= 0) {
            return;
        }

        if (!magnet.isBoostActive(currentTick) && magnet.getBoostLevel() > 0) {
            magnet.setBoostLevel(0);
            magnet.setBoostExpiryTick(0);
            itemService.write(mainHand, magnet);
            player.getInventory().setItemInMainHand(mainHand);
        }

        applyBaseDrain(player, magnet, config);
        if (magnet.getCharge() <= 0) {
            sendMessage(player, "magnet.depleted", Map.of());
            itemService.write(mainHand, magnet);
            player.getInventory().setItemInMainHand(mainHand);
            return;
        }

        double radius = RadiusCalculator.calculateEffectiveRadius(config, magnet.getTier(), magnet, player, currentTick);
        int processed = 0;

        if (!afkBlocked || !antiAfk.isDisableAutoFuelWhenAfk()) {
            processed += absorbFuel(player, mainHand, magnet, config, currentTick);
        }

        for (Item entity : player.getWorld().getEntitiesByClass(Item.class)) {
            if (processed >= config.getMaxItemsPerTick()) {
                break;
            }
            if (!entity.isValid() || entity.isDead()) {
                continue;
            }
            if (entity.getLocation().distance(player.getLocation()) > radius) {
                continue;
            }

            ItemStack stack = entity.getItemStack();
            if (isFuelItem(config, stack.getType())) {
                continue;
            }
            if (magnet.getTier().getBlacklist().contains(stack.getType())) {
                continue;
            }
            if (!protectionService.canPull(player, entity.getLocation())) {
                sendDenyMessage(player, config);
                continue;
            }

            Location next = PullPhysics.stepToward(entity.getLocation(), player.getLocation(), config.getPullStepBlocks());
            if (next.equals(entity.getLocation())) {
                continue;
            }

            entity.teleport(next);
            spawnTrail(config, next);
            processed++;

            if (PullPhysics.distance(next, player.getLocation()) <= config.getPickupDistance()) {
                drainForItemPull(player, magnet, config);
            } else {
                drainForItemPull(player, magnet, config);
            }
        }

        itemService.write(mainHand, magnet);
        player.getInventory().setItemInMainHand(mainHand);
    }

    private int absorbFuel(Player player, ItemStack mainHand, MagnetData magnet, MagnetConfig config, long currentTick) {
        int absorbed = 0;
        for (Item entity : player.getWorld().getEntitiesByClass(Item.class)) {
            Material type = entity.getItemStack().getType();
            if (!isFuelItem(config, type)) {
                continue;
            }
            if (entity.getLocation().distance(player.getLocation()) > config.getFuelRadius()) {
                continue;
            }
            if (!protectionService.canPull(player, entity.getLocation())) {
                continue;
            }

            FuelConfig fuelConfig = config.getFuel().get(type.name());
            if (fuelConfig == null) {
                continue;
            }

            Location next = PullPhysics.stepToward(entity.getLocation(), player.getLocation(), config.getPullStepBlocks());
            if (!next.equals(entity.getLocation())) {
                entity.teleport(next);
                spawnTrail(config, next);
            }

            if (PullPhysics.distance(entity.getLocation(), player.getLocation()) > 1.0) {
                continue;
            }

            int maxCharge = magnet.getTier().getMaxCharge();
            if (magnet.getCharge() >= maxCharge) {
                break;
            }

            int amount = entity.getItemStack().getAmount();
            int chargeToAdd = amount * fuelConfig.getChargePerItem();
            magnet.setCharge(Math.min(maxCharge, magnet.getCharge() + chargeToAdd));

            if (fuelConfig.getBoostLevelAdd() > 0) {
                long expiry = currentTick + (fuelConfig.getBoostDurationSeconds() * 20L);
                magnet.setBoostLevel(magnet.getBoostLevel() + fuelConfig.getBoostLevelAdd());
                magnet.setBoostExpiryTick(Math.max(magnet.getBoostExpiryTick(), expiry));
                sendMessage(player, "magnet.boost-active", Map.of("seconds", String.valueOf(fuelConfig.getBoostDurationSeconds())));
            }

            entity.remove();
            absorbed++;
            sendMessage(player, "magnet.fuel-absorbed", Map.of(
                    "charge", String.valueOf(magnet.getCharge()),
                    "max_charge", String.valueOf(maxCharge)
            ));
        }
        return absorbed;
    }

    private void applyBaseDrain(Player player, MagnetData magnet, MagnetConfig config) {
        double drainPerTick = magnet.getTier().getBaseDrainPerSecond()
                / (20.0 / config.getScanIntervalTicks())
                * RadiusCalculator.getDrainMultiplier(config, player, magnet);
        double accumulated = drainAccumulator.getOrDefault(player.getUniqueId(), 0.0) + drainPerTick;
        int whole = (int) accumulated;
        if (whole > 0) {
            magnet.setCharge(Math.max(0, magnet.getCharge() - whole));
            accumulated -= whole;
        }
        drainAccumulator.put(player.getUniqueId(), accumulated);
    }

    private void drainForItemPull(Player player, MagnetData magnet, MagnetConfig config) {
        int drain = (int) Math.ceil(magnet.getTier().getExtraDrainPerItem() * RadiusCalculator.getDrainMultiplier(config, player, magnet));
        magnet.setCharge(Math.max(0, magnet.getCharge() - drain));
    }

    private boolean isFuelItem(MagnetConfig config, Material material) {
        return config.getFuel().containsKey(material.name());
    }

    private void spawnTrail(MagnetConfig config, Location location) {
        Particle particle = config.getParticleType();
        location.getWorld().spawnParticle(particle, location, 2, 0.05, 0.05, 0.05, 0.01);
    }

    private void sendDenyMessage(Player player, MagnetConfig config) {
        long now = System.currentTimeMillis();
        long cooldownMillis = config.getDenyMessageCooldownTicks() * 50L;
        Long last = denyCooldowns.get(player.getUniqueId());
        if (last != null && now - last < cooldownMillis) {
            return;
        }
        denyCooldowns.put(player.getUniqueId(), now);
        sendMessage(player, "magnet.denied-location", Map.of());
    }

    private void sendMessage(Player player, String key, Map<String, String> placeholders) {
        MessagesConfig messages = plugin.getConfigManager().getMessagesConfig();
        player.sendMessage(TextUtil.component(TextUtil.color(messages.format(key, placeholders))));
    }

    public AfkTracker getAfkTracker() {
        return afkTracker;
    }
}
