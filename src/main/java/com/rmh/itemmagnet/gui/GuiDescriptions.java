package com.rmh.itemmagnet.gui;

public final class GuiDescriptions {

    private GuiDescriptions() {
    }

    public static final String SCAN_INTERVAL = "Ticks between magnet scans. Lower = snappier, more CPU.";
    public static final String MAX_ITEMS = "Max items pulled per player per scan tick.";
    public static final String PULL_STEP = "Blocks items move toward the player each step.";
    public static final String SNEAK_DISABLE = "Sneaking pauses item pulling.";
    public static final String FUEL_RADIUS = "Minimum radius for auto-absorbing fuel drops.";
    public static final String FUEL_EFFECTIVE = "Also absorb fuel within effective pull radius.";
    public static final String HOLD_MODE = "Where the magnet must be to activate.";
    public static final String PULL_EXPERIENCE = "Pull XP orbs toward the player.";
    public static final String ARM_SWING = "Swing the hand when pulling items (visual feedback).";
    public static final String SOUNDS_ENABLED = "Play configured sounds for pull, fuel, deny, deplete.";
    public static final String AFK_ENABLED = "Require movement or magnet pauses.";
    public static final String AFK_NOTIFY_ONCE = "Send one AFK message until player moves again.";
    public static final String PRESET_WARNING = "Active preset overrides matching keys on reload.";
    public static final String TIER_MATERIAL = "Material for NEW magnets only. Re-give items after change.";
    public static final String DISPLAY_NAME = "Item name shown on magnets. Use & color codes. Re-give items to update existing magnets.";
    public static final String PROXIMITY_LORE = "Optional ambient messages when holding a magnet in coordinate zones.";
    public static final String PROXIMITY_REQUIRE_MAGNET = "Player must be using an active magnet tier in the zone.";
    public static final String PROXIMITY_COOLDOWN = "Seconds between messages per player per zone.";
    public static final String PROXIMITY_SCAN = "Ticks between proximity checks (separate from pull scan).";
    public static final String PROXIMITY_ZONE_MESSAGES = "Message lines and tier overrides: edit in config.yml.";
}
