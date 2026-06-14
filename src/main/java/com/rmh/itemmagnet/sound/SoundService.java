package com.rmh.itemmagnet.sound;

import com.rmh.itemmagnet.config.FuelConfig;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.config.SoundsConfig;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SoundService {

    private static final long FUEL_SOUND_COOLDOWN_MS = 250L;

    private final SoundsConfig sounds;
    private final MagnetConfig config;
    private final Map<UUID, Long> fuelSoundCooldowns = new ConcurrentHashMap<>();

    public SoundService(MagnetConfig config) {
        this.config = config;
        this.sounds = config.getSounds();
    }

    public void playPull(Player player) {
        play(player, sounds.getPull());
    }

    public void playFuel(Player player) {
        playFuel(player, null);
    }

    public void playFuel(Player player, Material fuelMaterial) {
        if (!canPlayFuelSound(player)) {
            return;
        }
        Optional<Sound> sound = resolveFuelSound(fuelMaterial);
        play(player, sound);
    }

    public void playDepleted(Player player) {
        play(player, sounds.getDepleted());
    }

    public void playDenied(Player player) {
        play(player, sounds.getDenied());
    }

    private Optional<Sound> resolveFuelSound(Material fuelMaterial) {
        if (fuelMaterial != null) {
            FuelConfig fuelConfig = config.getFuel().get(fuelMaterial.name());
            if (fuelConfig != null && fuelConfig.getSound().isPresent()) {
                return fuelConfig.getSound();
            }
        }
        return sounds.getFuel();
    }

    private boolean canPlayFuelSound(Player player) {
        long now = System.currentTimeMillis();
        Long last = fuelSoundCooldowns.get(player.getUniqueId());
        if (last != null && now - last < FUEL_SOUND_COOLDOWN_MS) {
            return false;
        }
        fuelSoundCooldowns.put(player.getUniqueId(), now);
        return true;
    }

    private void play(Player player, Optional<Sound> soundOptional) {
        if (!sounds.isEnabled()) {
            return;
        }
        soundOptional.ifPresent(sound -> player.playSound(player.getLocation(), sound, 0.6f, 1.0f));
    }
}
