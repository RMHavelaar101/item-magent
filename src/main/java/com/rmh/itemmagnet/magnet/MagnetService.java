package com.rmh.itemmagnet.magnet;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.api.event.ItemMagnetPullBlockedEvent;
import com.rmh.itemmagnet.api.event.ItemMagnetDepleteEvent;
import com.rmh.itemmagnet.api.event.ItemMagnetExperiencePullEvent;
import com.rmh.itemmagnet.api.event.ItemMagnetFuelAbsorbEvent;
import com.rmh.itemmagnet.api.event.ItemMagnetPullEvent;
import com.rmh.itemmagnet.config.AntiAfkConfig;
import com.rmh.itemmagnet.config.FuelConfig;
import com.rmh.itemmagnet.config.InventoryFullBehavior;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.config.MessagesConfig;
import com.rmh.itemmagnet.config.TierConfig;
import com.rmh.itemmagnet.filter.PlayerFilterStorage;
import com.rmh.itemmagnet.filter.PullBlockReason;
import com.rmh.itemmagnet.filter.PullEligibilityService;
import com.rmh.itemmagnet.item.LoreContext;
import com.rmh.itemmagnet.item.MagnetData;
import com.rmh.itemmagnet.item.MagnetItemService;
import com.rmh.itemmagnet.protection.ProtectionService;
import com.rmh.itemmagnet.sound.SoundService;
import com.rmh.itemmagnet.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class MagnetService extends BukkitRunnable {

    private final ItemMagnetPlugin plugin;
    private final MagnetItemService itemService;
    private final ProtectionService protectionService;
    private final AfkTracker afkTracker;
    private final MagnetLocator magnetLocator;
    private final PlayerFilterStorage playerFilterStorage;
    private PullEligibilityService pullEligibilityService;
    private SoundService soundService;
    private final Map<String, Long> blockedEventDedupe = new HashMap<>();
    private final Map<UUID, Long> denyCooldowns = new HashMap<>();
    private final Map<UUID, Long> afkMessageCooldowns = new HashMap<>();
    private final Map<UUID, Double> drainAccumulator = new HashMap<>();

    public MagnetService(
            ItemMagnetPlugin plugin,
            MagnetItemService itemService,
            ProtectionService protectionService,
            AfkTracker afkTracker,
            MagnetLocator magnetLocator,
            PlayerFilterStorage playerFilterStorage,
            PullEligibilityService pullEligibilityService
    ) {
        this.plugin = plugin;
        this.itemService = itemService;
        this.protectionService = protectionService;
        this.afkTracker = afkTracker;
        this.magnetLocator = magnetLocator;
        this.playerFilterStorage = playerFilterStorage;
        this.pullEligibilityService = pullEligibilityService;
        refreshSoundService();
    }

    public void updatePullEligibilityService(PullEligibilityService pullEligibilityService) {
        this.pullEligibilityService = pullEligibilityService;
    }

    public PullEligibilityService getPullEligibilityService() {
        return pullEligibilityService;
    }

    public void refreshSoundService() {
        this.soundService = new SoundService(plugin.getConfigManager().getMagnetConfig());
    }

    public void start() {
        int interval = plugin.getConfigManager().getMagnetConfig().getScanIntervalTicks();
        runTaskTimer(plugin, interval, interval);
    }

    public void restart() {
        cancel();
        start();
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
        if (config.isDisableInCreative() && player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (config.isDisableInSpectator() && player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (!config.getWorldFilter().isAllowed(player.getWorld().getName())) {
            return;
        }

        Optional<MagnetSlot> slotOptional = magnetLocator.locate(player, config);
        if (slotOptional.isEmpty()) {
            return;
        }

        MagnetSlot magnetSlot = slotOptional.get();
        ItemStack magnetStack = magnetSlot.getItemStack();
        MagnetData magnet = magnetSlot.getData();
        TierConfig tier = magnet.getTier();

        if (!player.hasPermission("itemmagnet.use." + tier.getId())) {
            return;
        }

        if (config.isSneakToDisable() && player.isSneaking()) {
            return;
        }

        if (!RadiusCalculator.isHeightAllowed(config, player)) {
            sendMessage(player, "magnet.y-disabled", Map.of());
            return;
        }

        AntiAfkConfig antiAfk = config.getAntiAfk();
        boolean afkBlocked = antiAfk.isEnabled()
                && afkTracker.isAfk(player, antiAfk.getRequiredBlocksMoved(), antiAfk.getWindowSeconds());
        if (afkBlocked) {
            if (antiAfk.isNotifyOnce()) {
                if (afkTracker.shouldNotifyAfk(player, antiAfk.getRequiredBlocksMoved(), antiAfk.getWindowSeconds())) {
                    sendMessage(player, "magnet.afk-disabled", Map.of());
                    afkTracker.markAfkNotified(player);
                }
            } else {
                sendThrottledMessage(player, afkMessageCooldowns, config.getDenyMessageCooldownTicks(), "magnet.afk-disabled", Map.of());
            }
            return;
        }

        if (magnet.getCharge() <= 0) {
            return;
        }

        if (!magnet.isBoostActive(currentTick) && magnet.getBoostLevel() > 0) {
            magnet.setBoostLevel(0);
            magnet.setBoostExpiryTick(0);
            writeMagnet(player, magnetSlot, magnet);
        }

        applyBaseDrain(player, magnet, config);
        if (magnet.getCharge() <= 0) {
            sendMessage(player, "magnet.depleted", Map.of());
            soundService.playDepleted(player);
            Bukkit.getPluginManager().callEvent(new ItemMagnetDepleteEvent(player, tier));
            writeMagnet(player, magnetSlot, magnet);
            return;
        }

        double radius = RadiusCalculator.calculateEffectiveRadius(config, tier, magnet, player, currentTick);
        int processed = 0;

        if (!afkBlocked || !antiAfk.isDisableAutoFuelWhenAfk()) {
            processed += absorbFuel(player, magnetSlot, magnet, config, currentTick, radius);
        }

        List<Item> nearbyItems = NearbyItemScanner.findItems(player, radius);
        for (Item entity : nearbyItems) {
            if (processed >= config.getMaxItemsPerTick()) {
                break;
            }

            ItemStack stack = entity.getItemStack();
            if (isFuelItem(config, stack.getType())) {
                continue;
            }

            Optional<PullBlockReason> blockReason = pullEligibilityService.evaluateItemPull(
                    player, stack, tier, entity.getLocation()
            );
            if (blockReason.isPresent()) {
                fireBlockedEvent(player, entity, stack, blockReason.get(), currentTick);
                if (blockReason.get() == PullBlockReason.PROTECTION) {
                    sendDenyMessage(player, config);
                }
                continue;
            }

            if (shouldPauseForInventory(player, config, stack)) {
                fireBlockedEvent(player, entity, stack, PullBlockReason.INVENTORY_FULL, currentTick);
                continue;
            }

            ItemMagnetPullEvent pullEvent = new ItemMagnetPullEvent(player, entity, stack);
            Bukkit.getPluginManager().callEvent(pullEvent);
            if (pullEvent.isCancelled()) {
                fireBlockedEvent(player, entity, stack, PullBlockReason.EVENT_CANCELLED, currentTick);
                continue;
            }

            Location next = PullPhysics.stepToward(entity.getLocation(), player.getLocation(), config.getPullStepBlocks());
            if (next.equals(entity.getLocation())) {
                continue;
            }

            entity.teleport(next);
            spawnTrail(config, next);
            soundService.playPull(player);
            maybeSwingArm(player, magnetSlot, config);
            processed++;
            drainForItemPull(player, magnet, config);
        }

        if (config.isPullExperience() && tier.isPullExperience()) {
            processed = pullExperienceOrbs(player, magnet, config, radius, processed);
        }

        writeMagnet(player, magnetSlot, magnet, config, currentTick);
    }

    private int pullExperienceOrbs(
            Player player,
            MagnetData magnet,
            MagnetConfig config,
            double radius,
            int processed
    ) {
        List<ExperienceOrb> experienceOrbs = NearbyExperienceScanner.findExperienceOrbs(player, radius);
        for (ExperienceOrb orb : experienceOrbs) {
            if (processed >= config.getMaxItemsPerTick()) {
                break;
            }
            if (!protectionService.canPull(player, orb.getLocation())) {
                sendDenyMessage(player, config);
                continue;
            }

            int experience = orb.getExperience();
            ItemMagnetExperiencePullEvent pullEvent = new ItemMagnetExperiencePullEvent(player, orb, experience);
            Bukkit.getPluginManager().callEvent(pullEvent);
            if (pullEvent.isCancelled()) {
                continue;
            }

            if (PullPhysics.distance(orb.getLocation(), player.getLocation()) <= config.getPickupDistance()) {
                orb.remove();
                player.giveExp(experience);
                spawnTrail(config, player.getLocation());
                soundService.playPull(player);
                processed++;
                drainForItemPull(player, magnet, config);
                continue;
            }

            Location next = PullPhysics.stepToward(orb.getLocation(), player.getLocation(), config.getPullStepBlocks());
            if (next.equals(orb.getLocation())) {
                continue;
            }

            orb.teleport(next);
            spawnTrail(config, next);
            soundService.playPull(player);
            processed++;
            drainForItemPull(player, magnet, config);
        }
        return processed;
    }

    private boolean shouldPauseForInventory(Player player, MagnetConfig config, ItemStack stack) {
        InventoryFullBehavior behavior = config.getInventoryFullBehavior();
        if (behavior == InventoryFullBehavior.CONTINUE) {
            return false;
        }
        if (pullEligibilityService.hasInventorySpace(player, stack)) {
            return false;
        }
        if (behavior == InventoryFullBehavior.NOTIFY_ONCE) {
            sendThrottledMessage(player, denyCooldowns, config.getDenyMessageCooldownTicks(), "magnet.inventory-full", Map.of());
        }
        return true;
    }

    private void fireBlockedEvent(Player player, Item entity, ItemStack stack, PullBlockReason reason, long currentTick) {
        String dedupeKey = player.getUniqueId() + ":" + entity.getEntityId() + ":" + reason.name() + ":" + currentTick;
        if (blockedEventDedupe.containsKey(dedupeKey)) {
            return;
        }
        blockedEventDedupe.put(dedupeKey, currentTick);
        if (blockedEventDedupe.size() > 500) {
            blockedEventDedupe.clear();
        }
        Bukkit.getPluginManager().callEvent(new ItemMagnetPullBlockedEvent(player, entity, stack, reason));
    }

    private int absorbFuel(
            Player player,
            MagnetSlot magnetSlot,
            MagnetData magnet,
            MagnetConfig config,
            long currentTick,
            double effectiveRadius
    ) {
        int absorbed = 0;
        double scanRadius = config.isFuelUseEffectiveRadius()
                ? Math.max(config.getFuelRadius(), effectiveRadius)
                : config.getFuelRadius();
        List<Item> fuelItems = NearbyItemScanner.findItems(player, scanRadius);
        for (Item entity : fuelItems) {
            Material type = entity.getItemStack().getType();
            if (!isFuelItem(config, type)) {
                continue;
            }
            if (!protectionService.canPull(player, entity.getLocation())) {
                continue;
            }

            FuelConfig fuelConfig = config.getFuel().get(type.name());
            if (fuelConfig == null) {
                continue;
            }

            int maxCharge = magnet.getTier().getMaxCharge();
            if (magnet.getCharge() >= maxCharge) {
                sendThrottledMessage(player, denyCooldowns, config.getDenyMessageCooldownTicks(), "magnet.fuel-full", Map.of());
                break;
            }

            Location next = PullPhysics.stepToward(entity.getLocation(), player.getLocation(), config.getPullStepBlocks());
            if (!next.equals(entity.getLocation())) {
                entity.teleport(next);
                spawnTrail(config, next);
            }

            if (PullPhysics.distance(entity.getLocation(), player.getLocation()) > 1.0) {
                continue;
            }

            int amount = entity.getItemStack().getAmount();
            ItemMagnetFuelAbsorbEvent fuelEvent = new ItemMagnetFuelAbsorbEvent(player, entity, type, amount);
            Bukkit.getPluginManager().callEvent(fuelEvent);
            if (fuelEvent.isCancelled()) {
                continue;
            }

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
            soundService.playFuel(player, type);
            sendMessage(player, "magnet.fuel-absorbed", Map.of(
                    "charge", String.valueOf(magnet.getCharge()),
                    "max_charge", String.valueOf(maxCharge)
            ));
        }
        return absorbed;
    }

    private void writeMagnet(Player player, MagnetSlot magnetSlot, MagnetData magnet, MagnetConfig config, long currentTick) {
        ItemStack stack = magnetSlot.getItemStack();
        TierConfig tier = magnet.getTier();
        double effectiveRadius = RadiusCalculator.calculateEffectiveRadius(config, tier, magnet, player, currentTick);
        LoreContext loreContext = new LoreContext(effectiveRadius, tier.getRadius());
        itemService.write(stack, magnet, loreContext);
        player.getInventory().setItem(magnetSlot.getSlot(), stack);
    }

    private void writeMagnet(Player player, MagnetSlot magnetSlot, MagnetData magnet) {
        writeMagnet(player, magnetSlot, magnet, plugin.getConfigManager().getMagnetConfig(), plugin.getServer().getCurrentTick());
    }

    private void maybeSwingArm(Player player, MagnetSlot magnetSlot, MagnetConfig config) {
        if (!config.isPullArmSwing()) {
            return;
        }
        if (magnetSlot.getSlot() == 40) {
            player.swingOffHand();
            return;
        }
        player.swingMainHand();
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
        soundService.playDenied(player);
    }

    private void sendThrottledMessage(
            Player player,
            Map<UUID, Long> cooldownMap,
            int cooldownTicks,
            String key,
            Map<String, String> placeholders
    ) {
        long now = System.currentTimeMillis();
        long cooldownMillis = cooldownTicks * 50L;
        Long last = cooldownMap.get(player.getUniqueId());
        if (last != null && now - last < cooldownMillis) {
            return;
        }
        cooldownMap.put(player.getUniqueId(), now);
        sendMessage(player, key, placeholders);
    }

    private void sendMessage(Player player, String key, Map<String, String> placeholders) {
        MessagesConfig messages = plugin.getConfigManager().getMessagesConfig();
        player.sendMessage(TextUtil.component(TextUtil.color(messages.format(key, placeholders))));
    }

    public AfkTracker getAfkTracker() {
        return afkTracker;
    }

    public SoundService getSoundService() {
        return soundService;
    }

    public MagnetLocator getMagnetLocator() {
        return magnetLocator;
    }
}
