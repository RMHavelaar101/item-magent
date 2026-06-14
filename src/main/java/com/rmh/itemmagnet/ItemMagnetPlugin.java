package com.rmh.itemmagnet;

import com.rmh.itemmagnet.command.ItemMagnetCommand;
import com.rmh.itemmagnet.config.ConfigManager;
import com.rmh.itemmagnet.item.MagnetItemService;
import com.rmh.itemmagnet.item.PdcKeys;
import com.rmh.itemmagnet.listener.MagnetListener;
import com.rmh.itemmagnet.magnet.AfkTracker;
import com.rmh.itemmagnet.magnet.MagnetService;
import com.rmh.itemmagnet.metrics.BStatsService;
import com.rmh.itemmagnet.metrics.UpdateChecker;
import com.rmh.itemmagnet.protection.LandsHook;
import com.rmh.itemmagnet.protection.ProtectionService;
import com.rmh.itemmagnet.protection.WorldGuardHook;
import com.rmh.itemmagnet.recipe.RecipeService;
import com.rmh.itemmagnet.unlock.UnlockService;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemMagnetPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private PdcKeys pdcKeys;
    private MagnetItemService itemService;
    private LandsHook landsHook;
    private WorldGuardHook worldGuardHook;
    private ProtectionService protectionService;
    private AfkTracker afkTracker;
    private MagnetService magnetService;
    private UnlockService unlockService;
    private RecipeService recipeService;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        configManager.reload();
        configManager.validateStartup();

        this.pdcKeys = new PdcKeys(this);
        this.itemService = new MagnetItemService(this, pdcKeys);
        this.landsHook = new LandsHook(this);
        this.worldGuardHook = new WorldGuardHook(this);
        this.protectionService = new ProtectionService(this, landsHook, worldGuardHook);
        this.afkTracker = new AfkTracker();
        this.unlockService = new UnlockService(this);
        this.recipeService = new RecipeService(this, itemService);
        this.magnetService = new MagnetService(this, itemService, protectionService, afkTracker);
        this.updateChecker = new UpdateChecker(this);

        recipeService.registerRecipes();
        magnetService.start();

        PluginCommand command = getCommand("itemmagnet");
        if (command != null) {
            ItemMagnetCommand executor = new ItemMagnetCommand(this, itemService, unlockService, protectionService);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }

        getServer().getPluginManager().registerEvents(new MagnetListener(this, itemService, unlockService), this);
        new BStatsService(this).register();
        updateChecker.start();

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

    public void reloadPlugin() {
        configManager.reload();
        landsHook.reload();
        worldGuardHook.reload();
        recipeService.registerRecipes();
        configManager.validateStartup();
    }

    public ConfigManager getConfigManager() {
        return configManager;
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
}
