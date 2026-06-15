package com.rmh.itemmagnet.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProximityLoreZone {

    private final String id;
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final double radius;
    private final double yTolerance;
    private final List<String> messages;
    private final Map<String, List<String>> tierMessages;

    public ProximityLoreZone(
            String id,
            String world,
            double x,
            double y,
            double z,
            double radius,
            double yTolerance,
            List<String> messages,
            Map<String, List<String>> tierMessages
    ) {
        this.id = id;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.yTolerance = yTolerance;
        this.messages = List.copyOf(messages);
        this.tierMessages = Collections.unmodifiableMap(new LinkedHashMap<>(tierMessages));
    }

    public String getId() {
        return id;
    }

    public String getWorld() {
        return world;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getRadius() {
        return radius;
    }

    public double getYTolerance() {
        return yTolerance;
    }

    public List<String> getMessages() {
        return messages;
    }

    public Map<String, List<String>> getTierMessages() {
        return tierMessages;
    }

    public List<String> resolveMessages(String tierId) {
        if (tierId != null) {
            List<String> tierSpecific = tierMessages.get(tierId);
            if (tierSpecific != null && !tierSpecific.isEmpty()) {
                return tierSpecific;
            }
        }
        return messages;
    }
}
