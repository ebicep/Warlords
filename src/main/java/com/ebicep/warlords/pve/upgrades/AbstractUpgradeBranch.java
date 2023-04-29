package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsUpgradeUnlockEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.*;

public abstract class AbstractUpgradeBranch<T extends AbstractAbility> {

    protected AbilityTree abilityTree;
    protected T ability;
    protected ItemStack itemStack;
    protected String itemName;

    protected List<Upgrade> treeA = new ArrayList<>();
    protected List<Upgrade> treeB = new ArrayList<>();
    protected Upgrade masterUpgrade;

    private int maxUpgrades = 6;
    private int upgradesRequiredForMaster = 6;
    private int freeUpgrades = 0;

    public AbstractUpgradeBranch(AbilityTree abilityTree, T ability) {
        this.abilityTree = abilityTree;
        this.ability = ability;
        this.itemStack = abilityTree.getWarlordsPlayer().getItemStackForAbility(ability);
        this.itemName = ability.getName();
    }

    public void openUpgradeBranchMenu() {
        WarlordsPlayer player = abilityTree.getWarlordsPlayer();
        Menu menu = new Menu("Upgrades", 9 * 6);

        addBranchToMenu(menu, treeA, 2, 4);
        addBranchToMenu(menu, treeB, 6, 4);
        menu.setItem(
                4,
                0,
                masterBranchItem(masterUpgrade),
                (m, e) -> {
                    if (e.isLeftClick()) {
                        purchaseMasterUpgrade(player, false);
                    } else if (!masterUpgrade.isUnlocked()) {
                        onAutoUpgrade(AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER, 0);
                    }
                }
        );

        menu.setItem(4, 3,
                new ItemBuilder(Material.DIAMOND)
                        .name(ChatColor.GRAY + "Upgrades Remaining: " + ChatColor.GREEN + maxUpgrades)
                        .get(),
                ACTION_DO_NOTHING
        );

        menu.setItem(3, 5, MENU_BACK, (m, e) -> abilityTree.openAbilityTree());
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        if (player.getEntity() instanceof Player) {
            menu.openForPlayer((Player) player.getEntity());
        }
    }

    public void purchaseMasterUpgrade(WarlordsPlayer player, boolean autoUpgraded) {
        if (player.getAbilityTree().getMaxMasterUpgrades() <= 0) {
            player.sendMessage(Component.text("You cannot unlock this master upgrade, maximum master upgrades reached.", NamedTextColor.RED));
            return;
        }
        if (player.getCurrency() < masterUpgrade.getCurrencyCost()) {
            player.sendMessage(Component.text("You do not have enough Insignia (❂) to buy this upgrade!", NamedTextColor.RED));
            return;
        }
        if (masterUpgrade.isUnlocked()) {
            player.sendMessage(Component.text("You already unlocked this upgrade.", NamedTextColor.RED));
            return;
        }

        if (upgradesRequiredForMaster <= 0) {
            masterUpgrade.getOnUpgrade().run();
            masterUpgrade.setUnlocked(true);

            player.getAbilityTree().setMaxMasterUpgrades(abilityTree.getMaxMasterUpgrades() - 1);
            player.subtractCurrency(masterUpgrade.getCurrencyCost());
            Utils.playGlobalSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 500f, 0.8f);

            globalAnnouncement(player.getGame(), masterUpgrade, ability, autoUpgraded);
        } else {
            String s = upgradesRequiredForMaster == 1 ? "" : "s";
            player.sendMessage(Component.text("You need to unlock " + upgradesRequiredForMaster + " more upgrade" + s + " before unlocking the master upgrade!",
                    NamedTextColor.RED
            ));
            return;
        }

        ability.updateDescription((Player) player.getEntity());
        if (!autoUpgraded) {
            openUpgradeBranchMenu();
        }

        abilityTree.getUpgradeLog().add(new AbilityTree.UpgradeLog(
                        RecordTimeElapsedOption.getTicksElapsed(player.getGame()),
                        masterUpgrade.getName(),
                        masterUpgrade.getDescription()
                )
        );
    }

    private void onAutoUpgrade(AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType upgradeType, int upgradeIndex) {
        int branchIndex = abilityTree.getUpgradeBranches().indexOf(this);
        AutoUpgradeProfile autoUpgradeProfile = abilityTree.getAutoUpgradeProfile();
        boolean contains = autoUpgradeProfile
                .getAutoUpgradeEntries()
                .stream()
                .anyMatch(autoUpgradeEntry -> autoUpgradeEntry.getBranchIndex() == branchIndex &&
                        autoUpgradeEntry.getUpgradeType() == upgradeType &&
                        autoUpgradeEntry.getUpgradeIndex() == upgradeIndex
                );
        WarlordsPlayer player = abilityTree.getWarlordsPlayer();
        if (contains) {
            AutoUpgradeProfile.AutoUpgradeEntry lastEntry = autoUpgradeProfile
                    .getAutoUpgradeEntries()
                    .get(autoUpgradeProfile.getAutoUpgradeEntries().size() - 1);
            boolean isLastEntry = lastEntry.getBranchIndex() == branchIndex &&
                    lastEntry.getUpgradeType() == upgradeType &&
                    lastEntry.getUpgradeIndex() == upgradeIndex;
            if (isLastEntry) {
                //remove
                autoUpgradeProfile.getAutoUpgradeEntries().remove(autoUpgradeProfile.getAutoUpgradeEntries().size() - 1);
                openUpgradeBranchMenu();
            } else {
                //error message
                player.sendMessage(Component.text("You can only remove the last upgrade in the auto upgrade queue.", NamedTextColor.RED));
            }
        } else {
            //checks
            List<AutoUpgradeProfile.AutoUpgradeEntry> branchEntries = autoUpgradeProfile
                    .getAutoUpgradeEntries()
                    .stream()
                    .filter(autoUpgradeEntry -> autoUpgradeEntry.getUpgradeType() != AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER &&
                            autoUpgradeEntry.getBranchIndex() == branchIndex &&
                            !autoUpgradeEntry.getUpgradeType().getUpgradeFunction.apply(this).get(autoUpgradeEntry.getUpgradeIndex()).isUnlocked()
                    )
                    .toList();
            int branchUpgradesInQueue = branchEntries.size();
            if (upgradeType == AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER) {
                int masterUpgradesInQueue = (int) autoUpgradeProfile
                        .getAutoUpgradeEntries()
                        .stream()
                        .filter(autoUpgradeEntry -> autoUpgradeEntry.getUpgradeType() == AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER &&
                                !abilityTree.getUpgradeBranches().get(autoUpgradeEntry.getBranchIndex()).getMasterUpgrade().isUnlocked()
                        )
                        .count();
                if (masterUpgradesInQueue >= player.getAbilityTree().getMaxMasterUpgrades()) {
                    player.sendMessage(Component.text("You cannot queue this master upgrade, maximum master upgrades reached.", NamedTextColor.RED));
                    return;
                }
                if (upgradesRequiredForMaster - branchUpgradesInQueue > 0) {
                    player.sendMessage(Component.text("You cannot queue this master upgrade, you need to unlock or queue all upgrades first.", NamedTextColor.RED));
                    return;
                }
            } else {
                if (upgradeIndex != 0) {
                    if (!upgradeType.getUpgradeFunction.apply(this).get(upgradeIndex - 1).isUnlocked() &&
                            branchEntries.stream().noneMatch(autoUpgradeEntry -> autoUpgradeEntry.getUpgradeType() == upgradeType &&
                                    autoUpgradeEntry.getUpgradeIndex() == upgradeIndex - 1)
                    ) {
                        player.sendMessage(Component.text("You need to unlock or queue the previous upgrade first!", NamedTextColor.RED));
                        return;
                    }
                }
                if (branchUpgradesInQueue >= maxUpgrades) {
                    player.sendMessage(Component.text("You cannot queue this upgrade, maximum upgrades for this ability reached.", NamedTextColor.RED));
                    return;
                }
            }
            autoUpgradeProfile.addEntry(branchIndex, upgradeIndex, upgradeType);
            openUpgradeBranchMenu();
        }
        DatabaseManager.updatePlayer(player.getUuid(), databasePlayer -> {

        });
    }

    private void addBranchToMenu(Menu menu, List<Upgrade> tree, int x, int y) {
        WarlordsPlayer player = abilityTree.getWarlordsPlayer();
        for (int i = 0; i < tree.size(); i++) {
            Upgrade upgrade = tree.get(i);
            int finalI = i;
            menu.setItem(
                    x,
                    y - i,
                    branchItem(upgrade),
                    (m, e) -> {
                        if (e.isLeftClick()) {
                            purchaseUpgrade(tree, player, upgrade, finalI, false);
                        } else if (!upgrade.isUnlocked()) {
                            AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType upgradeType = null;
                            if (tree == treeA) {
                                upgradeType = AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.A;
                            } else if (tree == treeB) {
                                upgradeType = AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.B;
                            }
                            if (upgradeType != null) {
                                onAutoUpgrade(upgradeType, finalI);
                            }
                        }
                    }
            );
        }
    }

    public void purchaseUpgrade(List<Upgrade> tree, WarlordsPlayer player, Upgrade upgrade, int upgradeIndex, boolean autoUpgraded) {
        if (upgrade.isUnlocked()) {
            player.sendMessage(Component.text("You already unlocked this upgrade.", NamedTextColor.RED));
            return;
        }
        if (upgradeIndex != 0) {
            if (!tree.get(upgradeIndex - 1).isUnlocked()) {
                player.sendMessage(Component.text("You need to unlock the previous upgrade first!", NamedTextColor.RED));
                return;
            }
        }

        if (player.getCurrency() < upgrade.getCurrencyCost() && freeUpgrades <= 0) {
            player.sendMessage(Component.text("You do not have enough Insignia (❂) to buy this upgrade!", NamedTextColor.RED));
            return;
        }
        if (maxUpgrades <= 0) {
            player.sendMessage(Component.text("You cannot unlock this upgrade, maximum upgrades reached.", NamedTextColor.RED));
            return;
        }

        upgrade.getOnUpgrade().run();
        upgrade.setUnlocked(true);
        maxUpgrades--;
        upgradesRequiredForMaster--;

        if (freeUpgrades > 0) {
            freeUpgrades--;
        } else {
            player.subtractCurrency(upgrade.getCurrencyCost());
        }
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 1.3f);

        Bukkit.getPluginManager().callEvent(new WarlordsUpgradeUnlockEvent(player, upgrade));
        globalAnnouncement(player.getGame(), upgrade, ability, autoUpgraded);
        updateInventory(player);
        if (!autoUpgraded) {
            openUpgradeBranchMenu();
        }

        abilityTree.getUpgradeLog().add(new AbilityTree.UpgradeLog(
                        RecordTimeElapsedOption.getTicksElapsed(player.getGame()),
                        upgrade.getName(),
                        upgrade.getDescription()
                )
        );
    }

    private ItemStack masterBranchItem(Upgrade upgrade) {
        ArrayList<String> lore = new ArrayList<>();
        if (upgrade.getSubName() != null) {
            lore.add((upgrade.isUnlocked() ? ChatColor.RED : ChatColor.DARK_GRAY) + upgrade.getSubName());
            lore.add("");
        }
        lore.add((upgrade.isUnlocked() ? ChatColor.GREEN : ChatColor.GRAY) + upgrade.getDescription() +
                "\n\n" + ChatColor.GRAY + "Cost: " + ChatColor.GOLD + "❂ " + upgrade.getCurrencyCost());
        ItemBuilder itemBuilder = new ItemBuilder(masterUpgrade.isUnlocked() ? new ItemStack(Material.ORANGE_WOOL) : new ItemStack(Material.WHITE_WOOL))
                .name(Component.text(masterUpgrade.getName(), NamedTextColor.GOLD, TextDecoration.BOLD))
                .loreLEGACY(lore);
        if (!upgrade.isUnlocked()) {
            String position = abilityTree.getAutoUpgradeProfile().getPosition(abilityTree, upgrade);
            if (position != null) {
                itemBuilder.addLore(
                        ChatColor.GRAY + "Auto Upgrade Position: " + position,
                        "",
                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK" + ChatColor.GRAY + " to remove from auto upgrade queue."
                );
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
                itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
            } else {
                itemBuilder.addLore(
                        "",
                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK" + ChatColor.GRAY + " to add to auto upgrade queue."
                );
            }
        }
        return itemBuilder.get();
    }

    private ItemStack branchItem(Upgrade upgrade) {
        ItemBuilder itemBuilder = new ItemBuilder(upgrade.isUnlocked() ?
                                                  new ItemStack(Material.ORANGE_WOOL) :
                                                  new ItemStack(Material.LIGHT_GRAY_WOOL))
                .name((upgrade.isUnlocked() ? ChatColor.GOLD : ChatColor.RED) + upgrade.getName())
                .loreLEGACY((upgrade.isUnlocked() ? ChatColor.GREEN : ChatColor.GRAY) + upgrade.getDescription() +
                        "\n\n" + ChatColor.GRAY + "Cost: " + ChatColor.GOLD + "❂ " + upgrade.getCurrencyCost());
        if (!upgrade.isUnlocked()) {
            String position = abilityTree.getAutoUpgradeProfile().getPosition(abilityTree, upgrade);
            if (position != null) {
                itemBuilder.addLore(
                        ChatColor.GRAY + "Auto Upgrade Position: " + position,
                        "",
                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK" + ChatColor.GRAY + " to remove from auto upgrade queue."
                );
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
                itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
            } else {
                itemBuilder.addLore(
                        "",
                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK" + ChatColor.GRAY + " to add to auto upgrade queue."
                );
            }
        }
        return itemBuilder.get();
    }

    private void globalAnnouncement(Game game, Upgrade upgrade, T ability, boolean autoUpgraded) {
        String prefix = autoUpgraded ? AutoUpgradeProfile.AUTO_UPGRADE_PREFIX : "";
        game.forEachOnlinePlayer((p, t) -> {
            if (upgrade.getName().equals("Master Upgrade") || (upgrade.getSubName() != null && upgrade.getSubName().contains("Master Upgrade"))) {
                p.sendMessage(Component.text(prefix + ChatColor.AQUA + abilityTree.getWarlordsPlayer().getName() + " §ehas unlocked ")
                                       .append(Component.text(ChatColor.GOLD + ability.getName() + " - §c§l" + upgrade.getName() + "§e!")
                                                        .hoverEvent(HoverEvent.showText(Component.text("§c§l" + upgrade.getName() + "\n" + upgrade.getDescription()))))
                );
            } else {
                p.sendMessage(Component.text(prefix + ChatColor.AQUA + abilityTree.getWarlordsPlayer().getName() + " §ehas unlocked ")
                                       .append(Component.text(ChatColor.GOLD + ability.getName() + " - " + upgrade.getName() + "§e!")
                                                        .hoverEvent(HoverEvent.showText(Component.text("§c§l" + upgrade.getName() + "\n" + upgrade.getDescription()))))
                );
            }
        });
    }

    public T getAbility() {
        return ability;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getItemName() {
        return itemName;
    }

    public void updateInventory(WarlordsPlayer wp) {
        wp.updateInventory(false);
        ability.updateDescription((Player) wp.getEntity());
    }

    public int getMaxUpgrades() {
        return maxUpgrades;
    }

    public void setMaxUpgrades(int maxUpgrades) {
        this.maxUpgrades = maxUpgrades;
    }

    public int getFreeUpgrades() {
        return freeUpgrades;
    }

    public void setFreeUpgrades(int freeUpgrades) {
        this.freeUpgrades = freeUpgrades;
    }

    public List<Upgrade> getTreeA() {
        return treeA;
    }

    public List<Upgrade> getTreeB() {
        return treeB;
    }

    public Upgrade getMasterUpgrade() {
        return masterUpgrade;
    }
}
