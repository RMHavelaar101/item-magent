package com.rmh.itemmagnet.gui;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ConfigGuiPermissionsTest {

    private Player player;
    private final Map<String, Boolean> permissions = new HashMap<>();

    @BeforeEach
    void setUp() {
        player = Mockito.mock(Player.class);
        when(player.isPermissionSet(anyString())).thenAnswer(invocation -> permissions.containsKey(invocation.getArgument(0)));
        when(player.hasPermission(anyString())).thenAnswer(invocation -> permissions.getOrDefault(invocation.getArgument(0), false));
    }

    private void grant(String permission) {
        permissions.put(permission, true);
    }

    private void deny(String permission) {
        permissions.put(permission, false);
    }

    @Test
    void configPermissionAllowsEditByDefault() {
        grant(ConfigGuiPermissions.BASE);
        assertTrue(ConfigGuiPermissions.canEdit(player, "settings.scan-interval-ticks", "settings"));
        assertTrue(ConfigGuiPermissions.canAccessSection(player, "settings"));
    }

    @Test
    void explicitSectionDenyBlocksEdit() {
        grant(ConfigGuiPermissions.BASE);
        deny(ConfigGuiPermissions.sectionPermission("settings"));
        assertFalse(ConfigGuiPermissions.canEdit(player, "settings.scan-interval-ticks", "settings"));
        assertFalse(ConfigGuiPermissions.canAccessSection(player, "settings"));
    }

    @Test
    void fieldPermissionOverridesSectionDeny() {
        grant(ConfigGuiPermissions.BASE);
        deny(ConfigGuiPermissions.sectionPermission("settings"));
        grant(ConfigGuiPermissions.fieldPermission("settings.scan-interval-ticks"));
        assertTrue(ConfigGuiPermissions.canEdit(player, "settings.scan-interval-ticks", "settings"));
    }

    @Test
    void saveRequiresDedicatedPermission() {
        grant(ConfigGuiPermissions.BASE);
        assertFalse(ConfigGuiPermissions.canSave(player));
        grant(ConfigGuiPermissions.SAVE);
        assertTrue(ConfigGuiPermissions.canSave(player));
    }
}
