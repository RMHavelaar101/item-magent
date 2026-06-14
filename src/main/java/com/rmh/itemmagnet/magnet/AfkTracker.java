package com.rmh.itemmagnet.magnet;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AfkTracker {

    private final Map<UUID, MovementRecord> records = new ConcurrentHashMap<>();

    public void recordMovement(Player player) {
        Location location = player.getLocation();
        MovementRecord record = records.computeIfAbsent(player.getUniqueId(), ignored -> new MovementRecord(location, System.currentTimeMillis()));
        double distance = horizontalDistance(record.getLocation(), location);
        if (distance > 0.05) {
            record.setLocation(location.clone());
            record.setLastMovedAt(System.currentTimeMillis());
            record.addDistance(distance);
            record.setAfkNotified(false);
        }
    }

    public boolean isAfk(Player player, double requiredBlocksMoved, int windowSeconds) {
        MovementRecord record = records.get(player.getUniqueId());
        if (record == null) {
            return true;
        }
        long windowMillis = windowSeconds * 1000L;
        if (System.currentTimeMillis() - record.getLastMovedAt() > windowMillis) {
            record.resetDistance();
            return true;
        }
        return record.getDistanceInWindow() < requiredBlocksMoved;
    }

    public boolean shouldNotifyAfk(Player player, double requiredBlocksMoved, int windowSeconds) {
        if (!isAfk(player, requiredBlocksMoved, windowSeconds)) {
            return false;
        }
        MovementRecord record = records.get(player.getUniqueId());
        if (record == null) {
            return true;
        }
        return !record.isAfkNotified();
    }

    public void markAfkNotified(Player player) {
        MovementRecord record = records.get(player.getUniqueId());
        if (record != null) {
            record.setAfkNotified(true);
        }
    }

    public void clear(Player player) {
        records.remove(player.getUniqueId());
    }

    private double horizontalDistance(Location first, Location second) {
        double dx = first.getX() - second.getX();
        double dz = first.getZ() - second.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static final class MovementRecord {
        private Location location;
        private long lastMovedAt;
        private double distanceInWindow;
        private boolean afkNotified;

        private MovementRecord(Location location, long lastMovedAt) {
            this.location = location.clone();
            this.lastMovedAt = lastMovedAt;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public long getLastMovedAt() {
            return lastMovedAt;
        }

        public void setLastMovedAt(long lastMovedAt) {
            this.lastMovedAt = lastMovedAt;
        }

        public double getDistanceInWindow() {
            return distanceInWindow;
        }

        public void addDistance(double distance) {
            this.distanceInWindow += distance;
        }

        public void resetDistance() {
            this.distanceInWindow = 0;
        }

        public boolean isAfkNotified() {
            return afkNotified;
        }

        public void setAfkNotified(boolean afkNotified) {
            this.afkNotified = afkNotified;
        }
    }
}
