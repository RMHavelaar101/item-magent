package com.rmh.itemmagnet;

import com.rmh.itemmagnet.api.ItemMagnetApi;
import com.rmh.itemmagnet.command.ItemMagnetCommand;
import com.rmh.itemmagnet.config.ConfigChangeTracker;
import com.rmh.itemmagnet.config.ConfigManager;
import com.rmh.itemmagnet.config.ConfigPersistence;
import com.rmh.itemmagnet.config.ReloadResult;
import com.rmh.itemmagnet.gui.ConfigGuiListener;
import com.rmh.itemmagnet.gui.ConfigGuiService;
import com.rmh.itemmagnet.integration.IntegrationStatusService;
import com.rmh.itemmagnet.integration.ItemMagnetPlaceholderExpansion;
import com.rmh.itemmagnet.integration.QuestsUnlockListener;
import com.rmh.itemmagnet.item.MagnetItemService;
import com.rmh.itemmagnet.item.PdcKeys;
import com.rmh.itemmagnet.listener.CraftListener;
import com.rmh.itemmagnet.listener.MagnetListener;
import com.rmh.itemmagnet.magnet.AfkTracker;
import com.rmh.itemmagnet.magnet.MagnetLocator;
import com.rmh.itemmagnet.magnet.MagnetService;
import com.rmh.itemmagnet.magnet.ProximityLoreService;
import com.rmh.itemmagnet.metrics.BStatsService;
import com.rmh.itemmagnet.metrics.StartupMessageService;
import com.rmh.itemmagnet.metrics.UpdateChecker;
import com.rmh.itemmagnet.protection.GriefPreventionHook;
import com.rmh.itemmagnet.protection.LandsHook;
import com.rmh.itemmagnet.protection.PlotSquaredHook;
import com.rmh.itemmagnet.protection.ProtectionService;
import com.rmh.itemmagnet.protection.ResidenceHook;
import com.rmh.itemmagnet.protection.SuperiorSkyblockHook;
import com.rmh.itemmagnet.protection.TownyHook;
import com.rmh.itemmagnet.protection.WorldGuardHook;
import com.rmh.itemmagnet.recipe.RecipeService;
import com.rmh.itemmagnet.unlock.UnlockService;
import com.rmh.itemmagnet.unlock.UnlockStorage;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ItemMagnetPlugin extends JavaPlugin {

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
    private ResidenceHook residenceHook;
    private PlotSquaredHook plotSquaredHook;
    private SuperiorSkyblockHook superiorSkyblockHook;
    private ProtectionService protectionService;
    private IntegrationStatusService integrationStatusService;
    private QuestsUnlockListener questsUnlockListener;
    private AfkTracker afkTracker;
    private MagnetLocator magnetLocator;
    private MagnetService magnetService;
    private ProximityLoreService proximityLoreService;
    private UnlockStorage unlockStorage;
    private UnlockService unlockService;
    private RecipeService recipeService;
    private UpdateChecker updateChecker;
    private StartupMessageService startupMessageService;

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
        this.residenceHook = new ResidenceHook(this);
        this.plotSquaredHook = new PlotSquaredHook(this);
        this.superiorSkyblockHook = new SuperiorSkyblockHook(this);
        this.protectionService = new ProtectionService(
                this,
                landsHook,
                worldGuardHook,
                townyHook,
                griefPreventionHook,
                residenceHook,
                plotSquaredHook,
                superiorSkyblockHook
        );
        this.integrationStatusService = new IntegrationStatusService(
                this,
                landsHook,
                worldGuardHook,
                townyHook,
                griefPreventionHook,
                residenceHook,
                plotSquaredHook,
                superiorSkyblockHook
        );
        this.afkTracker = new AfkTracker();
        this.magnetLocator = new MagnetLocator(itemService);
        this.unlockStorage = new UnlockStorage(this);
        this.unlockService = new UnlockService(this, unlockStorage);
        unlockService.load();
        this.recipeService = new RecipeService(this, itemService);
        this.magnetService = new MagnetService(this, itemService, protectionService, afkTracker, magnetLocator);
        this.proximityLoreService = new ProximityLoreService(this, magnetLocator);
        this.updateChecker = new UpdateChecker(this);
        this.startupMessageService = new StartupMessageService(this);
        this.configGuiService = new ConfigGuiService(this, configPersistence);
        this.questsUnlockListener = new QuestsUnlockListener(this, unlockService);

        recipeService.registerRecipes();
        magnetService.start();
        proximityLoreService.start();
        questsUnlockListener.register();

        ItemMagnetCommand executor = new ItemMagnetCommand(
                this,
                itemService,
                unlockService,
                protectionService,
                magnetLocator,
                configGuiService,
                startupMessageService,
                integrationStatusService
        );

        PluginCommand command = getCommand("itemmagnet");
        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        PluginCommand imCommand = getCommand("im");
        if (imCommand != null) {
            imCommand.setExecutor(executor);
            imCommand.setTabCompleter(executor);
        }

        getServer().getPluginManager().registerEvents(new MagnetListener(this, itemService, unlockService, magnetLocator), this);
        getServer().getPluginManager().registerEvents(new CraftListener(this, itemService, unlockService), this);
        getServer().getPluginManager().registerEvents(new ConfigGuiListener(this, configGuiService), this);
        new BStatsService(this).register();
        updateChecker.start();
        ItemMagnetPlaceholderExpansion.tryRegister(this, magnetLocator);

        startupMessageService.logIfEnabled();
        getLogger().info("ItemMagnet enabled with " + configManager.getMagnetConfig().getTiers().size() + " tiers.");
        getLogger().info("Integration hooks: " + integrationStatusService.formatHookStatusLine());
    }

    @Override
    public void onDisable() {
        if (magnetService != null) {
            safeCancel(magnetService);
        }
        if (proximityLoreService != null) {
            safeCancel(proximityLoreService);
        }
        if (recipeService != null) {
            recipeService.unregisterRecipes();
        }
    }

    private void safeCancel(org.bukkit.scheduler.BukkitRunnable runnable) {
        try {
            runnable.cancel();
        } catch (IllegalStateException ignored) {
            // Task was never scheduled (e.g. proximity lore disabled).
        }
    }

    public ReloadResult reloadPlugin() {
        configManager.reload();
        landsHook.reload();
        worldGuardHook.reload();
        townyHook.reload();
        griefPreventionHook.reload();
        residenceHook.reload();
        plotSquaredHook.reload();
        superiorSkyblockHook.reload();
        recipeService.registerRecipes();
        magnetService.refreshSoundService();
        magnetService.restart();
        proximityLoreService.restart();
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

    public IntegrationStatusService getIntegrationStatusService() {
        return integrationStatusService;
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

    public StartupMessageService getStartupMessageService() {
        return startupMessageService;
    }

    public MagnetLocator getMagnetLocator() {
        return magnetLocator;
    }
}
