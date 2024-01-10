package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsUpgradeUnlockEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Settings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
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

import java.time.Instant;
import java.util.*;

import static com.ebicep.warlords.menu.Menu.*;

public abstract class AbstractUpgradeBranch<T extends AbstractAbility> {

    private static final LinkedHashMap<Spendable, Long> ALTERNATIVE_MASTERY_COST = new LinkedHashMap<>() {{
        put(Currencies.ASCENDANT_SHARD, 1L);
    }};

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


    // TODO changed abilities that have a min/max damage to interface and/or use floatmodifiable
    // for boosting ability variables in the constructor
    public void runOnce() {

    }

    public void openUpgradeBranchMenu() {
        WarlordsPlayer warlordsPlayer = abilityTree.getWarlordsPlayer();
        if (!(warlordsPlayer.getEntity() instanceof Player player)) {
            return;
        }
        Menu menu = new Menu("Upgrades", 9 * 6);

        addBranchToMenu(menu, treeA, 2, 4);
        addBranchToMenu(menu, treeB, 6, 4);
        boolean hasSecondMaster = !masterUpgrade2.getName().equals("Name Placeholder");
        menu.setItem(
                hasSecondMaster ? 3 : 4,
                0,
                masterBranchItem(masterUpgrade, false).get(),
                (m, e) -> {
                    if (e.isLeftClick()) {
                        purchaseMasterUpgrade(warlordsPlayer, masterUpgrade, false);
                    } else if (!masterUpgrade.isUnlocked()) {
                        onAutoUpgrade(AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER, 0);
                    }
                }
        );
        if (hasSecondMaster) {
            DatabaseManager.getPlayer(warlordsPlayer.getUuid(), databasePlayer -> {
                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                Map<Specializations, Map<Integer, Instant>> alternativeMasteriesUnlocked = pveStats.getAlternativeMasteriesUnlocked();
                Map<Integer, Instant> unlockedMasteries = alternativeMasteriesUnlocked.get(warlordsPlayer.getSpecClass());
                int upgradeBranchIndex = abilityTree.getUpgradeBranches().indexOf(this);
                boolean unlocked = unlockedMasteries != null && unlockedMasteries.get(upgradeBranchIndex) != null;
                ItemBuilder masterBranchItem = masterBranchItem(masterUpgrade2, !unlocked);
                if (!unlocked) {
                    masterBranchItem.name(
                            masterBranchItem.getName()
                                            .append(Component.text(" [LOCKED]", NamedTextColor.RED, TextDecoration.BOLD))
                    );
                    masterBranchItem.addLore(PvEUtils.getCostLore(ALTERNATIVE_MASTERY_COST, true));
                }
                menu.setItem(
                        5,
                        0,
                        masterBranchItem.get(),
                        (m, e) -> {
                            if (!unlocked) {
                                for (Map.Entry<Spendable, Long> spendableIntegerEntry : ALTERNATIVE_MASTERY_COST.entrySet()) {
                                    Spendable spendable = spendableIntegerEntry.getKey();
                                    Long cost = spendableIntegerEntry.getValue();
                                    if (spendable.getFromPlayer(databasePlayer) < cost) {
                                        warlordsPlayer.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                                            .append(spendable.getCostColoredName(cost))
                                                                            .append(Component.text(" to permanently unlock this master upgrade.", NamedTextColor.RED))
                                        );
                                        warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                        return;
                                    }
                                }
                                Menu.openConfirmationMenu(player,
                                        "Confirm Purchase",
                                        3,
                                        Component.text("Permanently Unlock Mastery", NamedTextColor.GREEN),
                                        PvEUtils.getCostLore(ALTERNATIVE_MASTERY_COST, true),
                                        Component.text("Go Back", NamedTextColor.RED),
                                        Collections.emptyList(),
                                        (m2, e2) -> {
                                            alternativeMasteriesUnlocked.putIfAbsent(warlordsPlayer.getSpecClass(), new HashMap<>());
                                            alternativeMasteriesUnlocked.get(warlordsPlayer.getSpecClass()).put(upgradeBranchIndex, Instant.now());
                                            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                            openUpgradeBranchMenu();
                                        },
                                        (m2, e2) -> openUpgradeBranchMenu(),
                                        (m2) -> {
                                        }
                                );
                                return;
                            }
                            if (e.isLeftClick()) {
                                purchaseMasterUpgrade(warlordsPlayer, masterUpgrade2, false);
                            } else if (!masterUpgrade2.isUnlocked()) {
                                onAutoUpgrade(AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER2, 0);
                            }
                        }
                );
            });
        }


        menu.setItem(4, 3,
                new ItemBuilder(Material.DIAMOND)
                        .name(Component.text("Insignia: ", NamedTextColor.GRAY)
                                       .append(Component.text("❂ " + NumberFormat.addCommas(warlordsPlayer.getCurrency()), NamedTextColor.GOLD)))
                        .lore(
                                Component.text("Upgrades Remaining: ", NamedTextColor.GRAY).append(Component.text(maxUpgrades, NamedTextColor.GREEN)),
                                Component.text("Free Upgrades Available: ", NamedTextColor.GRAY)
                                         .append(Component.text(freeUpgrades, NamedTextColor.GREEN)),
                                Component.empty()
                        )
                        .addLore(WordWrap.wrap(Component.text("Note: Free Upgrades are only available for the first upgrade in each branch.", NamedTextColor.GRAY), 160))
                        .get(),
                ACTION_DO_NOTHING
        );

        menu.setItem(3, 5, MENU_BACK, (m, e) -> abilityTree.openAbilityTree());
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public void purchaseMasterUpgrade(WarlordsPlayer player, Upgrade upgrade, boolean autoUpgraded) {
        purchaseMasterUpgrade(player, upgrade, autoUpgraded, false);
    }

    public void purchaseMasterUpgrade(WarlordsPlayer player, Upgrade upgrade, boolean autoUpgraded, boolean force) {
        boolean isMasterUpgrade = masterUpgrade.equals(upgrade);
        boolean isMasterUpgrade2 = masterUpgrade2.equals(upgrade);
        if (!force) {
            if (isMasterUpgrade && masterUpgrade2.isUnlocked() ||
                    isMasterUpgrade2 && masterUpgrade.isUnlocked()
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
        if (isMasterUpgrade) {
            ability.setPveMasterUpgrade(true);
        } else if (isMasterUpgrade2) {
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
                    player.sendMessage(Component.text("You cannot queue this master upgrade, you already have a master upgrade queue for this ability.", NamedTextColor.RED));
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

        boolean isFree = freeUpgrades > 0 && (treeA.indexOf(upgrade) == 0 || treeB.indexOf(upgrade) == 0);
        if (player.getCurrency() < upgrade.getCurrencyCost() && !isFree) {
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

        if (isFree) {
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

    private ItemBuilder masterBranchItem(Upgrade upgrade, boolean stopAtDescription) {
        ItemBuilder itemBuilder = new ItemBuilder(upgrade.isUnlocked() ? new ItemStack(Material.ORANGE_WOOL) : new ItemStack(Material.WHITE_WOOL))
                .name(Component.text(upgrade.getName(), NamedTextColor.GOLD, TextDecoration.BOLD));
        if (upgrade.getSubName() != null) {
            itemBuilder.addLore(
                    Component.text(upgrade.getSubName(), upgrade.isUnlocked() ? NamedTextColor.RED : NamedTextColor.DARK_GRAY),
                    Component.empty()
            );
        }
        for (Component component : upgrade.getDescription()) {
            itemBuilder.addLore(component.color(upgrade.isUnlocked() ? NamedTextColor.RED : NamedTextColor.DARK_GRAY));
        }
        if (stopAtDescription) {
            return itemBuilder;
        }
        itemBuilder.addLore(
                Component.empty(),
                Component.text("Cost: ", NamedTextColor.GRAY).append(Component.text("❂ " + upgrade.getCurrencyCost(), NamedTextColor.GOLD))
        );
        if (!upgrade.isUnlocked()) {
            Component position = abilityTree.getAutoUpgradeProfile().getPosition(abilityTree, upgrade);
            if (position != null) {
                itemBuilder.addLore(
                        Component.text("Auto Upgrade Position: ", NamedTextColor.GRAY).append(position),
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("RIGHT-CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                Component.text(" to remove from auto upgrade queue.", NamedTextColor.GRAY)
                        )
                );
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            } else {
                itemBuilder.addLore(
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("RIGHT-CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                Component.text(" to add to auto upgrade queue.", NamedTextColor.GRAY)
                        )
                );
            }
        }
        return itemBuilder;
    }

    private ItemStack branchItem(Upgrade upgrade) {
        boolean unlocked = upgrade.isUnlocked();
        ItemBuilder itemBuilder = new ItemBuilder(unlocked ? new ItemStack(Material.ORANGE_WOOL) : new ItemStack(Material.LIGHT_GRAY_WOOL))
                .name(Component.text(upgrade.getName(), unlocked ? NamedTextColor.GOLD : NamedTextColor.RED))
                .lore(upgrade.getDescription(unlocked ? NamedTextColor.GREEN : NamedTextColor.GRAY));
        if (!unlocked) {
            boolean isFree = freeUpgrades > 0 && (treeA.indexOf(upgrade) == 0 || treeB.indexOf(upgrade) == 0);
            itemBuilder.addLore(Component.empty(),
                    Component.text("Cost: ", NamedTextColor.GRAY)
                             .append(Component.text("❂ " + upgrade.getCurrencyCost() + (isFree ? " (Free)" : ""), NamedTextColor.GOLD))
            );
        }
        if (!unlocked) {
            Component position = abilityTree.getAutoUpgradeProfile().getPosition(abilityTree, upgrade);
            if (position != null) {
                itemBuilder.addLore(
                        Component.text("Auto Upgrade Position: ", NamedTextColor.GRAY).append(position),
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("RIGHT-CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                Component.text(" to remove from auto upgrade queue.", NamedTextColor.GRAY)
                        )
                );
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            } else {
                itemBuilder.addLore(
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("RIGHT-CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
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
        this.freeUpgrades = Math.min(2, this.freeUpgrades);
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
