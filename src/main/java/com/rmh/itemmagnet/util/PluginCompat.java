package com.rmh.itemmagnet.util;

import org.bukkit.plugin.java.JavaPlugin;

public final class PluginCompat {

    private PluginCompat() {
    }

    public static String getVersion(JavaPlugin plugin) {
        return plugin.getPluginMeta().getVersion();
    }

    public static String normalizeVersion(String version) {
        if (version == null) {
            return "";
        }
        String trimmed = version.trim();
        if (trimmed.startsWith("v") || trimmed.startsWith("V")) {
            return trimmed.substring(1);
        }
        return trimmed;
    }

    public static int compareVersions(String left, String right) {
        String normalizedLeft = normalizeVersion(left);
        String normalizedRight = normalizeVersion(right);
        if (normalizedLeft.isEmpty() && normalizedRight.isEmpty()) {
            return 0;
        }
        if (normalizedLeft.isEmpty()) {
            return -1;
        }
        if (normalizedRight.isEmpty()) {
            return 1;
        }

        String[] leftParts = splitVersionParts(normalizedLeft);
        String[] rightParts = splitVersionParts(normalizedRight);
        int maxLength = Math.max(leftParts.length, rightParts.length);
        for (int index = 0; index < maxLength; index++) {
            String leftPart = index < leftParts.length ? leftParts[index] : "0";
            String rightPart = index < rightParts.length ? rightParts[index] : "0";

            Integer leftNumber = parseNumericPart(leftPart);
            Integer rightNumber = parseNumericPart(rightPart);
            if (leftNumber != null && rightNumber != null) {
                int numericCompare = Integer.compare(leftNumber, rightNumber);
                if (numericCompare != 0) {
                    return numericCompare;
                }
                continue;
            }

            int textCompare = leftPart.compareToIgnoreCase(rightPart);
            if (textCompare != 0) {
                return textCompare;
            }
        }
        return 0;
    }

    public static boolean isNewerVersion(String remoteVersion, String localVersion) {
        return compareVersions(remoteVersion, localVersion) > 0;
    }

    private static String[] splitVersionParts(String version) {
        int suffixIndex = version.indexOf('-');
        String core = suffixIndex >= 0 ? version.substring(0, suffixIndex) : version;
        String suffix = suffixIndex >= 0 ? version.substring(suffixIndex + 1) : null;

        String[] coreParts = core.split("\\.");
        if (suffix == null || suffix.isBlank()) {
            return coreParts;
        }

        String[] combined = new String[coreParts.length + 1];
        System.arraycopy(coreParts, 0, combined, 0, coreParts.length);
        combined[coreParts.length] = suffix;
        return combined;
    }

    private static Integer parseNumericPart(String part) {
        if (part == null || part.isBlank()) {
            return 0;
        }
        for (int index = 0; index < part.length(); index++) {
            if (!Character.isDigit(part.charAt(index))) {
                return null;
            }
        }
        return Integer.parseInt(part);
    }
}
