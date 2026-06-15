package com.rmh.itemmagnet.support;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class IntegrationTestConfigLoader {

    private IntegrationTestConfigLoader() {
    }

    public static void reloadWithBundledConfig(ItemMagnetPlugin plugin) throws Exception {
        File dataFolder = plugin.getDataFolder();
        dataFolder.mkdirs();
        File configFile = new File(dataFolder, "config.yml");
        copyMainResource("config.yml", configFile);
        copyMainResource("messages.yml", new File(dataFolder, "messages.yml"));

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(configFile);
        plugin.getConfigManager().reloadParsedConfig(yaml);
        plugin.getUnlockService().load();
    }

    private static void copyMainResource(String name, File target) throws Exception {
        try (InputStream input = openMainResource(name)) {
            Files.copy(input, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static InputStream openMainResource(String name) throws Exception {
        Path built = Path.of("build/resources/main", name);
        if (Files.exists(built)) {
            return Files.newInputStream(built);
        }
        Path source = Path.of("src/main/resources", name);
        if (Files.exists(source)) {
            return Files.newInputStream(source);
        }
        throw new IllegalStateException("Missing ItemMagnet resource for tests: " + name);
    }
}
