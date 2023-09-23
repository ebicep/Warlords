package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsUpgradeUnlockEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Settings;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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
    protected Upgrade masterUpgrade = new Upgrade(
            "Name Placeholder",
            "Subname Placeholder",
            "Description Placeholder",
            999999999,
            () -> {

            }
    );
    protected Upgrade masterUpgrade2 = new Upgrade(
            "Name Placeholder",
            "Subname Placeholder",
            "Description Placeholder",
            999999999,
            () -> {

            }
    );

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
        boolean hasSecondMaster = !masterUpgrade2.getName().equals("Name Placeholder");
        menu.setItem(
                hasSecondMaster ? 2 : 4,
                0,
                masterBranchItem(masterUpgrade),
                (m, e) -> {
                    if (e.isLeftClick()) {
                        purchaseMasterUpgrade(player, masterUpgrade, false);
                    } else if (!masterUpgrade.isUnlocked()) {
                        onAutoUpgrade(AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER, 0);
                    }
                }
        );
        if (hasSecondMaster) {
            menu.setItem(
                    6,
                    0,
                    masterBranchItem(masterUpgrade2),
                    (m, e) -> {
                        if (e.isLeftClick()) {
                            purchaseMasterUpgrade(player, masterUpgrade2, false);
                        } else if (!masterUpgrade2.isUnlocked()) {
                            onAutoUpgrade(AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER2, 0);
                        }
                    }
            );
        }


        menu.setItem(4, 3,
                new ItemBuilder(Material.DIAMOND)
                        .name(Component.text("Upgrades Remaining: ", NamedTextColor.GRAY).append(Component.text(maxUpgrades, NamedTextColor.GREEN)))
                        .get(),
                ACTION_DO_NOTHING
        );

        menu.setItem(3, 5, MENU_BACK, (m, e) -> abilityTree.openAbilityTree());
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        if (player.getEntity() instanceof Player) {
            menu.openForPlayer((Player) player.getEntity());
        }
    }

    public void purchaseMasterUpgrade(WarlordsPlayer player, Upgrade upgrade, boolean autoUpgraded) {
        purchaseMasterUpgrade(player, upgrade, autoUpgraded, false);
    }

    public void purchaseMasterUpgrade(WarlordsPlayer player, Upgrade upgrade, boolean autoUpgraded, boolean force) {
        if (!force) {
            if (masterUpgrade.equals(upgrade) && masterUpgrade2.isUnlocked() ||
                    masterUpgrade2.equals(upgrade) && masterUpgrade.isUnlocked()
            ) {
                player.sendMessage(Component.text("You already unlocked a master upgrade for this ability.", NamedTextColor.RED));
                return;
            }
            if (player.getAbilityTree().getMaxMasterUpgrades() <= 0) {
                player.sendMessage(Component.text("You cannot unlock this master upgrade, maximum master upgrades reached.", NamedTextColor.RED));
                return;
            }
            if (player.getCurrency() < upgrade.getCurrencyCost()) {
                player.sendMessage(Component.text("You do not have enough Insignia (❂) to buy this upgrade!", NamedTextColor.RED));
                return;
            }
            if (upgrade.isUnlocked()) {
                player.sendMessage(Component.text("You already unlocked this upgrade.", NamedTextColor.RED));
                return;
            }
        }

        if (!force && upgradesRequiredForMaster > 0) {
            String s = upgradesRequiredForMaster == 1 ? "" : "s";
            player.sendMessage(Component.text("You need to unlock " + upgradesRequiredForMaster + " more upgrade" + s + " before unlocking the master upgrade!",
                    NamedTextColor.RED
            ));
            return;
        }

        upgrade.getOnUpgrade().run();
        upgrade.setUnlocked(true);
        if (masterUpgrade.equals(upgrade)) {
            ability.setPveMasterUpgrade(true);
        } else if (masterUpgrade2.equals(upgrade)) {
            ability.setPveMasterUpgrade2(true);
        }

        abilityTree.setMaxMasterUpgrades(abilityTree.getMaxMasterUpgrades() - 1);
        player.subtractCurrency(upgrade.getCurrencyCost());
        Utils.playGlobalSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 500f, 0.8f);

        globalAnnouncement(player.getGame(), upgrade, ability, autoUpgraded);

        player.updateInventory(false);
        if (!autoUpgraded) {
            openUpgradeBranchMenu();
        }

        abilityTree.getUpgradeLog().add(new AbilityTree.UpgradeLog(
                        RecordTimeElapsedOption.getTicksElapsed(player.getGame()),
                        upgrade.getName(),
                        PlainTextComponentSerializer.plainText().serialize(upgrade.getDescription().stream().collect(Component.toComponent(Component.newline())))
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
                            autoUpgradeEntry.getUpgradeType() != AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER2 &&
                            autoUpgradeEntry.getBranchIndex() == branchIndex &&
                            !autoUpgradeEntry.getUpgradeType().getUpgradeFunction.apply(this).get(autoUpgradeEntry.getUpgradeIndex()).isUnlocked()
                    )
                    .toList();
            int branchUpgradesInQueue = branchEntries.size();
            if (upgradeType == AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER || upgradeType == AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER2) {
                if (upgradeType == AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER && autoUpgradeProfile.getAutoUpgradeEntries()
                                                                                                               .stream()
                                                                                                               .filter(entry -> entry.getBranchIndex() == branchIndex)
                                                                                                               .anyMatch(entry -> entry.getUpgradeType() == AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER2) ||
                        upgradeType == AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER2 && autoUpgradeProfile.getAutoUpgradeEntries()
                                                                                                                    .stream()
                                                                                                                    .filter(entry -> entry.getBranchIndex() == branchIndex)
                                                                                                                    .anyMatch(entry -> entry.getUpgradeType() == AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER)
                ) {
                    player.sendMessage(Component.text("You cannot queue this master upgrade, you already unlocked a master upgrade for this ability.", NamedTextColor.RED));
                    return;
                }
                int masterUpgradesInQueue = (int) autoUpgradeProfile
                        .getAutoUpgradeEntries()
                        .stream()
                        .filter(autoUpgradeEntry -> autoUpgradeEntry.getUpgradeType() == AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER &&
                                !abilityTree.getUpgradeBranches().get(autoUpgradeEntry.getBranchIndex()).getMasterUpgrade().isUnlocked()
                        )
                        .filter(autoUpgradeEntry -> autoUpgradeEntry.getUpgradeType() == AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER2 &&
                                !abilityTree.getUpgradeBranches().get(autoUpgradeEntry.getBranchIndex()).getMasterUpgrade2().isUnlocked()
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
//        if (maxUpgrades <= 0) {
//            player.sendMessage(Component.text("You cannot unlock this upgrade, maximum upgrades reached.", NamedTextColor.RED));
//            return;
//        }

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
        player.updateInventory(false);
        if (!autoUpgraded) {
            openUpgradeBranchMenu();
        }

        abilityTree.getUpgradeLog().add(new AbilityTree.UpgradeLog(
                        RecordTimeElapsedOption.getTicksElapsed(player.getGame()),
                        upgrade.getName(),
                PlainTextComponentSerializer.plainText().serialize(upgrade.getDescription().stream().collect(Component.toComponent(Component.newline())))
                )
        );
    }

    private ItemStack masterBranchItem(Upgrade upgrade) {
        List<Component> lore = new ArrayList<>();
        if (upgrade.getSubName() != null) {
            lore.add(Component.text(upgrade.getSubName(), upgrade.isUnlocked() ? NamedTextColor.RED : NamedTextColor.DARK_GRAY));
            lore.add(Component.empty());
        }
        for (Component component : upgrade.getDescription()) {
            lore.add(component.color(upgrade.isUnlocked() ? NamedTextColor.RED : NamedTextColor.DARK_GRAY));
        }
        lore.add(Component.empty());
        lore.add(Component.empty());
        lore.add(Component.text("Cost: ", NamedTextColor.GRAY)
                          .append(Component.text("❂ " + upgrade.getCurrencyCost(), NamedTextColor.GOLD))
        );
        ItemBuilder itemBuilder = new ItemBuilder(upgrade.isUnlocked() ? new ItemStack(Material.ORANGE_WOOL) : new ItemStack(Material.WHITE_WOOL))
                .name(Component.text(upgrade.getName(), NamedTextColor.GOLD, TextDecoration.BOLD))
                .lore(lore);
        if (!upgrade.isUnlocked()) {
            Component position = abilityTree.getAutoUpgradeProfile().getPosition(abilityTree, upgrade);
            if (position != null) {
                itemBuilder.addLore(
                        Component.text("Auto Upgrade Position: ", NamedTextColor.GRAY).append(position),
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                Component.text(" to remove from auto upgrade queue.", NamedTextColor.GRAY)
                        )
                );
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            } else {
                itemBuilder.addLore(
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                Component.text(" to add to auto upgrade queue.", NamedTextColor.GRAY)
                        )
                );
            }
        }
        return itemBuilder.get();
    }

    private ItemStack branchItem(Upgrade upgrade) {
        ItemBuilder itemBuilder = new ItemBuilder(upgrade.isUnlocked() ? new ItemStack(Material.ORANGE_WOOL) : new ItemStack(Material.LIGHT_GRAY_WOOL))
                .name(Component.text(upgrade.getName(), upgrade.isUnlocked() ? NamedTextColor.GOLD : NamedTextColor.RED));
        List<Component> lore = new ArrayList<>();
        for (Component component : upgrade.getDescription()) {
            lore.add(component.color(upgrade.isUnlocked() ? NamedTextColor.RED : NamedTextColor.DARK_GRAY));
        }
        lore.add(Component.empty());
        lore.add(Component.text("Cost: ", NamedTextColor.GRAY)
                          .append(Component.text("❂ " + upgrade.getCurrencyCost(), NamedTextColor.GOLD))
        );
        itemBuilder.lore(lore);
        if (!upgrade.isUnlocked()) {
            Component position = abilityTree.getAutoUpgradeProfile().getPosition(abilityTree, upgrade);
            if (position != null) {
                itemBuilder.addLore(
                        Component.text("Auto Upgrade Position: ", NamedTextColor.GRAY).append(position),
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                Component.text(" to remove from auto upgrade queue.", NamedTextColor.GRAY)
                        )
                );
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            } else {
                itemBuilder.addLore(
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                Component.text(" to add to auto upgrade queue.", NamedTextColor.GRAY)
                        )
                );
            }
        }
        return itemBuilder.get();
    }

    private void globalAnnouncement(Game game, Upgrade upgrade, T ability, boolean autoUpgraded) {
        Component prefix = autoUpgraded ? AutoUpgradeProfile.AUTO_UPGRADE_PREFIX : Component.empty();
        game.forEachOnlinePlayer((p, t) -> {
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(p.getUniqueId(), true);
            if (databasePlayer.getChatUpgradeMode() != Settings.ChatSettings.ChatUpgrade.ALL) {
                return;
            }
            if (upgrade.equals(masterUpgrade) || upgrade.equals(masterUpgrade2)) {
                p.sendMessage(Component.textOfChildren(
                        prefix,
                        Component.text(abilityTree.getWarlordsPlayer().getName(), NamedTextColor.AQUA),
                        Component.text(" has unlocked ", NamedTextColor.YELLOW),
                        Component.textOfChildren(
                                Component.text(ability.getName() + " - ", NamedTextColor.GOLD),
                                Component.text(upgrade.getName(), NamedTextColor.RED, TextDecoration.BOLD),
                                Component.text("!", NamedTextColor.GOLD)
                        ).hoverEvent(HoverEvent.showText(Component.textOfChildren(
                                Component.text(upgrade.getName(), NamedTextColor.RED, TextDecoration.BOLD),
                                Component.newline(),
                                upgrade.getDescription(NamedTextColor.GREEN).stream().collect(Component.toComponent(Component.newline()))
                        )))
                ));
            } else {
                p.sendMessage(Component.textOfChildren(
                        prefix,
                        Component.text(abilityTree.getWarlordsPlayer().getName(), NamedTextColor.AQUA),
                        Component.text(" has unlocked ", NamedTextColor.YELLOW),
                        Component.text(ability.getName() + " - " + upgrade.getName() + "!", NamedTextColor.GOLD)
                                 .hoverEvent(HoverEvent.showText(Component.textOfChildren(
                                         Component.text(upgrade.getName(), NamedTextColor.GOLD),
                                         Component.newline(),
                                         upgrade.getDescription(NamedTextColor.GREEN).stream().collect(Component.toComponent(Component.newline()))
                                 )))
                ));
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

    public Upgrade getMasterUpgrade2() {
        return masterUpgrade2;
    }
}
