package com.rmh.itemmagnet.filter;

import com.rmh.itemmagnet.ItemMagnetPlugin;
import com.rmh.itemmagnet.config.MagnetConfig;
import com.rmh.itemmagnet.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class PlayerFilterGuiService {

    private static final String TAG_PREFIX = "tag:";

    private final ItemMagnetPlugin plugin;
    private final PlayerFilterStorage storage;
    private final FilterPresetService presetService;

    public PlayerFilterGuiService(
            ItemMagnetPlugin plugin,
            PlayerFilterStorage storage,
            FilterPresetService presetService
    ) {
        this.plugin = plugin;
        this.storage = storage;
        this.presetService = presetService;
    }

    public void open(Player player) {
        openMain(player, 0);
    }

    public void openMain(Player player, int page) {
        MagnetConfig config = plugin.getConfigManager().getMagnetConfig();
        MaterialFilterRule serverFilter = config.getServerItemFilter();
        List<Material> serverMaterials = serverFilter.getMaterials().stream()
                .sorted(Comparator.comparing(Enum::name))
                .toList();
        List<String> serverTags = serverFilter.getTags();

        List<PersonalEntry> personalEntries = buildPersonalEntries(player);
        int totalPages = Math.max(1, (int) Math.ceil(personalEntries.size() / (double) ItemFilterGuiSlots.ITEMS_PER_PAGE));
        int safePage = Math.max(0, Math.min(page, totalPages - 1));

        PlayerFilterGuiHolder holder = new PlayerFilterGuiHolder(PlayerFilterGuiHolder.GuiMode.MAIN, safePage);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8ItemMagnet Filter"));
        holder.setInventory(inventory);

        inventory.setItem(4, displayItem(
                Material.HOPPER,
                "&ePersonal item filter",
                "&7Server rules: &f" + serverFilter.getRuleCount(),
                "&7Your choices: &f" + storage.getFilterRuleCount(player.getUniqueId()),
                "&7Page: &f" + (safePage + 1) + "/" + totalPages
        ));

        int serverSlot = ItemFilterGuiSlots.SERVER_RULES_START;
        int serverShown = 0;
        int serverCap = ItemFilterGuiSlots.SERVER_RULES_END - ItemFilterGuiSlots.SERVER_RULES_START + 1;
        for (Material material : serverMaterials) {
            if (serverShown >= serverCap - 1 && (serverMaterials.size() + serverTags.size()) > serverCap) {
                break;
            }
            inventory.setItem(serverSlot++, serverRuleItem(material));
            serverShown++;
        }
        for (String tag : serverTags) {
            if (serverShown >= serverCap) {
                break;
            }
            inventory.setItem(serverSlot++, serverRuleTagItem(tag));
            serverShown++;
        }
        int serverRemaining = serverFilter.getRuleCount() - serverShown;
        if (serverRemaining > 0) {
            inventory.setItem(ItemFilterGuiSlots.SERVER_RULES_END, displayItem(
                    Material.BARRIER,
                    "&8+" + serverRemaining + " more",
                    "&cBlocked by server — cannot be changed"
            ));
        }

        int startIndex = safePage * ItemFilterGuiSlots.ITEMS_PER_PAGE;
        int slot = ItemFilterGuiSlots.LIST_START;
        for (int index = startIndex; index < personalEntries.size() && slot <= ItemFilterGuiSlots.LIST_END; index++) {
            PersonalEntry entry = personalEntries.get(index);
            inventory.setItem(slot++, entry.isTag()
                    ? personalTagItem(entry.tag())
                    : personalMaterialItem(entry.material()));
        }

        inventory.setItem(ItemFilterGuiSlots.SLOT_ADD_FROM_HAND, displayItem(
                Material.LIME_DYE,
                "&aAdd item in hand",
                "&7Hold an item and click to blacklist it"
        ));
        inventory.setItem(ItemFilterGuiSlots.SLOT_PRESETS, displayItem(
                Material.BOOK,
                "&eFilter presets",
                "&7Click to merge a preset into your filter"
        ));

        if (safePage > 0) {
            inventory.setItem(ItemFilterGuiSlots.SLOT_PREVIOUS_PAGE, displayItem(Material.ARROW, "&7Previous page", ""));
        }
        if (safePage < totalPages - 1) {
            inventory.setItem(ItemFilterGuiSlots.SLOT_NEXT_PAGE, displayItem(Material.ARROW, "&7Next page", ""));
        }

        inventory.setItem(ItemFilterGuiSlots.SLOT_CLOSE, displayItem(Material.BARRIER, "&cClose", ""));

        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openPresetPicker(Player player) {
        PlayerFilterGuiHolder holder = new PlayerFilterGuiHolder(PlayerFilterGuiHolder.GuiMode.PRESET_PICKER, 0);
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Filter Presets"));
        holder.setInventory(inventory);

        inventory.setItem(4, displayItem(
                Material.BOOK,
                "&eFilter presets",
                "&7Click a preset to merge it into your personal filter.",
                "&7Server-blocked materials are skipped automatically."
        ));

        int slot = 10;
        for (Map.Entry<String, MaterialFilterRule> entry : presetService.getPresets().entrySet()) {
            if (slot > 43) {
                break;
            }
            MaterialFilterRule rule = entry.getValue();
            inventory.setItem(slot++, displayItem(
                    Material.CHEST,
                    "&e" + entry.getKey(),
                    "&7Materials: &f" + rule.getMaterials().size(),
                    "&7Tags: &f" + rule.getTags().size(),
                    "&7Total rules: &f" + rule.getRuleCount(),
                    "&aClick to merge"
            ));
        }

        inventory.setItem(49, displayItem(Material.ARROW, "&7Back", "&7Return to your filter"));
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    public void openPresetConfirm(Player player, String presetName) {
        MaterialFilterRule preset = presetService.getPreset(presetName);
        if (preset == null) {
            openPresetPicker(player);
            return;
        }
        PresetMergePreview preview = presetService.previewMerge(storage, player.getUniqueId(), preset);
        PlayerFilterGuiHolder holder = new PlayerFilterGuiHolder(
                PlayerFilterGuiHolder.GuiMode.PRESET_CONFIRM,
                0,
                presetName.toLowerCase(java.util.Locale.ROOT)
        );
        Inventory inventory = Bukkit.createInventory(holder, 54, TextUtil.color("&8Confirm Preset"));
        holder.setInventory(inventory);

        inventory.setItem(4, displayItem(
                Material.BOOK,
                "&ePreset: &f" + presetName,
                "&7New materials: &a+" + preview.newMaterials(),
                "&7New tags: &a+" + preview.newTags(),
                "&7Skipped (server): &f" + preview.skippedServerBlocked()
        ));
        inventory.setItem(20, displayItem(
                Material.LIME_WOOL,
                "&aMerge preset",
                "&7Add these rules to your personal filter"
        ));
        inventory.setItem(24, displayItem(
                Material.RED_WOOL,
                "&cCancel",
                "&7Return without changes"
        ));
        inventory.setItem(49, displayItem(Material.ARROW, "&7Back", "&7Return to preset list"));
        fillBorder(inventory);
        player.openInventory(inventory);
    }

    private void handlePresetConfirmClick(Player player, PlayerFilterGuiHolder holder, int slot) {
        if (slot == 49 || slot == 24) {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format(
                    "filter.preset-cancelled",
                    Map.of()
            )));
            openPresetPicker(player);
            return;
        }
        if (slot != 20) {
            return;
        }
        String presetName = holder.getPendingPresetId();
        MaterialFilterRule preset = presetService.getPreset(presetName);
        if (preset == null) {
            openPresetPicker(player);
            return;
        }
        storage.applyPreset(player.getUniqueId(), preset, player);
        player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format(
                "filter.preset-merged",
                Map.of("preset", presetName)
        )));
        openMain(player, 0);
    }

    public void handleClick(Player player, PlayerFilterGuiHolder holder, int slot) {
        if (holder.getMode() == PlayerFilterGuiHolder.GuiMode.PRESET_CONFIRM) {
            handlePresetConfirmClick(player, holder, slot);
            return;
        }
        if (holder.getMode() == PlayerFilterGuiHolder.GuiMode.PRESET_PICKER) {
            handlePresetPickerClick(player, slot);
            return;
        }
        handleMainClick(player, holder, slot);
    }

    private void handleMainClick(Player player, PlayerFilterGuiHolder holder, int slot) {
        if (slot == ItemFilterGuiSlots.SLOT_CLOSE) {
            player.closeInventory();
            return;
        }
        if (slot == ItemFilterGuiSlots.SLOT_PREVIOUS_PAGE) {
            openMain(player, holder.getPage() - 1);
            return;
        }
        if (slot == ItemFilterGuiSlots.SLOT_NEXT_PAGE) {
            openMain(player, holder.getPage() + 1);
            return;
        }
        if (slot == ItemFilterGuiSlots.SLOT_ADD_FROM_HAND) {
            handleAddFromHand(player, holder.getPage());
            return;
        }
        if (slot == ItemFilterGuiSlots.SLOT_PRESETS) {
            openPresetPicker(player);
            return;
        }
        if (slot >= ItemFilterGuiSlots.SERVER_RULES_START && slot <= ItemFilterGuiSlots.SERVER_RULES_END) {
            return;
        }
        if (slot < ItemFilterGuiSlots.LIST_START || slot > ItemFilterGuiSlots.LIST_END) {
            return;
        }

        ItemStack clicked = player.getOpenInventory().getTopInventory().getItem(slot);
        if (clicked == null || clicked.getType() == Material.AIR || clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            return;
        }

        if (clicked.getType() == Material.NAME_TAG && clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
            String tag = extractTagFromItem(clicked);
            if (tag != null && storage.removeTag(player.getUniqueId(), tag, player)) {
                player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format(
                        "filter.tag-removed",
                        Map.of("tag", tag)
                )));
            }
            openMain(player, holder.getPage());
            return;
        }

        Material material = clicked.getType();
        if (storage.removeMaterial(player.getUniqueId(), material, player)) {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format(
                    "filter.removed",
                    Map.of("material", material.name())
            )));
        }
        openMain(player, holder.getPage());
    }

    private void handlePresetPickerClick(Player player, int slot) {
        if (slot == 49) {
            openMain(player, 0);
            return;
        }
        ItemStack clicked = player.getOpenInventory().getTopInventory().getItem(slot);
        if (clicked == null || clicked.getType() != Material.CHEST || !clicked.hasItemMeta()) {
            return;
        }
        String presetName = TextUtil.stripColor(clicked.getItemMeta().getDisplayName()).trim();
        MaterialFilterRule preset = presetService.getPreset(presetName);
        if (preset == null) {
            return;
        }
        openPresetConfirm(player, presetName);
    }

    private void handleAddFromHand(Player player, int page) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() == Material.AIR) {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format(
                    "filter.empty-hand",
                    Map.of()
            )));
            return;
        }

        Material material = hand.getType();
        if (storage.isServerBlocked(material)) {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format(
                    "filter.already-server-blocked",
                    Map.of("material", material.name())
            )));
            return;
        }
        if (storage.isBlacklisted(player.getUniqueId(), material)) {
            player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format(
                    "filter.already-blacklisted",
                    Map.of("material", material.name())
            )));
            return;
        }

        storage.addMaterial(player.getUniqueId(), material, player);
        player.sendMessage(TextUtil.color(plugin.getConfigManager().getMessagesConfig().format(
                "filter.added",
                Map.of("material", material.name())
        )));
        openMain(player, page);
    }

    private List<PersonalEntry> buildPersonalEntries(Player player) {
        List<PersonalEntry> entries = new ArrayList<>();
        Set<Material> materials = storage.getBlacklistedMaterials(player.getUniqueId());
        for (Material material : materials.stream().sorted(Comparator.comparing(Enum::name)).toList()) {
            entries.add(PersonalEntry.material(material));
        }
        for (String tag : storage.getBlacklistedTags(player.getUniqueId()).stream().sorted().toList()) {
            entries.add(PersonalEntry.tag(tag));
        }
        return entries;
    }

    private ItemStack serverRuleItem(Material material) {
        return displayItem(
                material,
                "&8" + material.name(),
                "&cBlocked by server — cannot be changed"
        );
    }

    private ItemStack serverRuleTagItem(String tag) {
        return displayItem(
                Material.BARRIER,
                "&8" + tag,
                "&cBlocked by server — cannot be changed",
                "&7Tag rule"
        );
    }

    private ItemStack personalMaterialItem(Material material) {
        return displayItem(
                material,
                "&c" + material.name(),
                "&7Click to &aallow&7 this item again"
        );
    }

    private ItemStack personalTagItem(String tag) {
        return displayItem(
                Material.NAME_TAG,
                "&c" + tag,
                "&7Tag blacklist",
                "&7Click to remove this tag rule",
                "&0" + TAG_PREFIX + tag
        );
    }

    private String extractTagFromItem(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            for (String line : item.getItemMeta().getLore()) {
                String stripped = TextUtil.stripColor(line);
                if (stripped.startsWith(TAG_PREFIX)) {
                    return stripped.substring(TAG_PREFIX.length());
                }
            }
        }
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            String name = TextUtil.stripColor(item.getItemMeta().getDisplayName()).trim();
            return name.isEmpty() ? null : name;
        }
        return null;
    }

    private ItemStack displayItem(Material material, String name, String... loreLines) {
        List<String> lore = new ArrayList<>();
        for (String line : loreLines) {
            if (line != null && !line.isBlank()) {
                lore.add(TextUtil.color(line));
            }
        }
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(TextUtil.color(name));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private void fillBorder(Inventory inventory) {
        ItemStack filler = displayItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int index = 0; index < inventory.getSize(); index++) {
            if (inventory.getItem(index) == null) {
                inventory.setItem(index, filler);
            }
        }
    }

    private record PersonalEntry(Material material, String tag, boolean isTag) {
        static PersonalEntry material(Material material) {
            return new PersonalEntry(material, null, false);
        }

        static PersonalEntry tag(String tag) {
            return new PersonalEntry(null, tag, true);
        }
    }
}
