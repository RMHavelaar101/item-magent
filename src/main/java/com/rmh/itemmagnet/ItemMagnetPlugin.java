package com.rmh.itemmagnet;

import com.rmh.itemmagnet.api.ItemMagnetApi;
import com.rmh.itemmagnet.command.ItemMagnetCommand;
import com.rmh.itemmagnet.config.ConfigChangeTracker;
import com.rmh.itemmagnet.config.ConfigManager;
import com.rmh.itemmagnet.config.ConfigPersistence;
import com.rmh.itemmagnet.config.ReloadResult;
import com.rmh.itemmagnet.gui.ConfigGuiListener;
import com.rmh.itemmagnet.gui.ConfigGuiService;
import com.rmh.itemmagnet.integration.ItemMagnetPlaceholderExpansion;
import com.rmh.itemmagnet.item.MagnetItemService;
import com.rmh.itemmagnet.item.PdcKeys;
import com.rmh.itemmagnet.listener.CraftListener;
import com.rmh.itemmagnet.listener.MagnetListener;
import com.rmh.itemmagnet.magnet.AfkTracker;
import com.rmh.itemmagnet.magnet.MagnetLocator;
import com.rmh.itemmagnet.magnet.MagnetService;
import com.rmh.itemmagnet.metrics.BStatsService;
import com.rmh.itemmagnet.metrics.UpdateChecker;
import com.rmh.itemmagnet.protection.GriefPreventionHook;
import com.rmh.itemmagnet.protection.LandsHook;
import com.rmh.itemmagnet.protection.ProtectionService;
import com.rmh.itemmagnet.protection.TownyHook;
import com.rmh.itemmagnet.protection.WorldGuardHook;
import com.rmh.itemmagnet.recipe.RecipeService;
import com.rmh.itemmagnet.unlock.UnlockService;
import com.rmh.itemmagnet.unlock.UnlockStorage;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class ItemMagnetPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private ConfigChangeTracker configChangeTracker;
    private ConfigPersistence configPersistence;
    private ConfigGuiService configGuiService;
    private PdcKeys pdcKeys;
    private MagnetItemService itemService;
    private LandsHook landsHook;
    private WorldGuardHook worldGuardHook;
    private TownyHook townyHook;
    private GriefPreventionHook griefPreventionHook;
    private ProtectionService protectionService;
    private AfkTracker afkTracker;
    private MagnetLocator magnetLocator;
    private MagnetService magnetService;
    private UnlockStorage unlockStorage;
    private UnlockService unlockService;
    private RecipeService recipeService;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        ItemMagnetApi.init(this);

        this.configManager = new ConfigManager(this);
        this.configChangeTracker = new ConfigChangeTracker();
        this.configPersistence = new ConfigPersistence(this, configChangeTracker);
        configManager.reload();
        configManager.validateStartup();

        this.pdcKeys = new PdcKeys(this);
        this.itemService = new MagnetItemService(this, pdcKeys);
        this.landsHook = new LandsHook(this);
        this.worldGuardHook = new WorldGuardHook(this);
        this.townyHook = new TownyHook(this);
        this.griefPreventionHook = new GriefPreventionHook(this);
        this.protectionService = new ProtectionService(this, landsHook, worldGuardHook, townyHook, griefPreventionHook);
        this.afkTracker = new AfkTracker();
        this.magnetLocator = new MagnetLocator(itemService);
        this.unlockStorage = new UnlockStorage(this);
        this.unlockService = new UnlockService(this, unlockStorage);
        unlockService.load();
        this.recipeService = new RecipeService(this, itemService);
        this.magnetService = new MagnetService(this, itemService, protectionService, afkTracker, magnetLocator);
        this.updateChecker = new UpdateChecker(this);
        this.configGuiService = new ConfigGuiService(this, configPersistence);

        recipeService.registerRecipes();
        magnetService.start();

        PluginCommand command = getCommand("itemmagnet");
        if (command != null) {
            ItemMagnetCommand executor = new ItemMagnetCommand(
                    this,
                    itemService,
                    unlockService,
                    protectionService,
                    magnetLocator,
                    configGuiService
            );
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        getServer().getPluginManager().registerEvents(new MagnetListener(this, itemService, unlockService, magnetLocator), this);
        getServer().getPluginManager().registerEvents(new CraftListener(this, itemService, unlockService), this);
        getServer().getPluginManager().registerEvents(new ConfigGuiListener(this, configGuiService), this);
        new BStatsService(this).register();
        updateChecker.start();
        ItemMagnetPlaceholderExpansion.tryRegister(this, magnetLocator);

        getLogger().info("ItemMagnet enabled with " + configManager.getMagnetConfig().getTiers().size() + " tiers.");
    }

    @Override
    public void onDisable() {
        if (magnetService != null) {
            magnetService.cancel();
        }
        if (recipeService != null) {
            recipeService.unregisterRecipes();
        }
    }

    public ReloadResult reloadPlugin() {
        configManager.reload();
        landsHook.reload();
        worldGuardHook.reload();
        townyHook.reload();
        griefPreventionHook.reload();
        recipeService.registerRecipes();
        magnetService.refreshSoundService();
        magnetService.restart();
        configManager.validateStartup();
        return ReloadResult.success(
                List.of("settings", "fuel", "tiers", "integrations", "messages"),
                configChangeTracker.getRestartRequiredKeys()
        );
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ConfigChangeTracker getConfigChangeTracker() {
        return configChangeTracker;
    }

    public ConfigPersistence getConfigPersistence() {
        return configPersistence;
    }

    public ConfigGuiService getConfigGuiService() {
        return configGuiService;
    }

    public MagnetItemService getItemService() {
        return itemService;
    }

    public ProtectionService getProtectionService() {
        return protectionService;
    }

    public MagnetService getMagnetService() {
        return magnetService;
    }

    public UnlockService getUnlockService() {
        return unlockService;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    public MagnetLocator getMagnetLocator() {
        return magnetLocator;
    }
}
