package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyMode;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.DefaultFontInfo;
import com.ebicep.warlords.util.java.NumberFormat;
import de.rapha149.signgui.SignGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.*;

public class AbilityTree {

    private final WarlordsPlayer warlordsPlayer;
    private final List<AbstractUpgradeBranch<?>> upgradeBranches = new ArrayList<>();
    private final List<UpgradeLog> upgradeLog = new ArrayList<>();
    private AutoUpgradeProfile autoUpgradeProfile = null;

    private int maxMasterUpgrades = 3;

    public AbilityTree(WarlordsPlayer warlordsPlayer) {
        this.warlordsPlayer = warlordsPlayer;
    }

    public void openAbilityTree() {
        if (!(warlordsPlayer.getEntity() instanceof Player player)) {
            return;
        }

        Menu menu = new Menu("Upgrades", 9 * 5);

        for (int i = 0; i < upgradeBranches.size(); i++) {
            AbstractUpgradeBranch<?> upgradeBranch = upgradeBranches.get(i);
            menu.setItem(
                    i + 2,
                    2,
                    new ItemBuilder(upgradeBranch.getItemStack())
                            .name(Component.text(upgradeBranch.getItemName(), NamedTextColor.GOLD))
                            .lore(
                                    Component.text("Upgrades Remaining: ", NamedTextColor.GRAY).append(Component.text(upgradeBranch.getMaxUpgrades(), NamedTextColor.GREEN)),
                                    Component.text("Free Upgrades Available: ", NamedTextColor.GRAY).append(Component.text(upgradeBranch.getFreeUpgrades(), NamedTextColor.GREEN)),
                                    Component.empty()
                            )
                            .addLore(getUpgradeTreeInfo(upgradeBranch, upgradeBranch.getTreeA()))
                            .addLore(Component.empty())
                            .addLore(getUpgradeTreeInfo(upgradeBranch, upgradeBranch.getTreeB()))
                            .addLore(Component.empty())
                            .addLore(getMasterUpgradeTreeInfo(upgradeBranch, upgradeBranch.getMasterUpgrade()))
                            .addLore(Component.empty())
                            .addLore(getMasterUpgradeTreeInfo(upgradeBranch, upgradeBranch.getMasterUpgrade2()))
                            .addLore(
                                    Component.empty(),
                                    Component.text(">> Click to open ability upgrade tree. <<", NamedTextColor.GRAY)
                            )
                            .get(),
                    (m, e) -> upgradeBranch.openUpgradeBranchMenu()
            );
        }
        menu.setItem(4, 0,
                new ItemBuilder(Material.GOLD_INGOT)
                        .name(Component.text("Insignia: ", NamedTextColor.GRAY)
                                       .append(Component.text("❂ " + NumberFormat.addCommas(warlordsPlayer.getCurrency()), NamedTextColor.GOLD))
                        )
                        .lore(
                                Component.text("Master Upgrades Remaining: ", NamedTextColor.GRAY).append(Component.text(maxMasterUpgrades, NamedTextColor.GOLD)),
                                Component.empty(),
                                Component.text("■ Upgrade is locked", NamedTextColor.RED),
                                Component.text("■ Upgrade is unlocked", NamedTextColor.GREEN),
                                Component.text("■ You have enough insignia to upgrade", NamedTextColor.YELLOW),
                                Component.text("■ You don't meet the requirements to upgrade", NamedTextColor.GRAY)
                        )
                        .get(),
                ACTION_DO_NOTHING
        );


        DatabaseManager.getPlayer(warlordsPlayer.getUuid(), databasePlayer -> {
            List<AutoUpgradeProfile> autoUpgradeProfiles = databasePlayer
                    .getPveStats()
                    .getAutoUpgradeProfiles()
                    .computeIfAbsent(warlordsPlayer.getSpecClass(), k -> new ArrayList<>());
            getAutoUpgradeProfile();
            List<Component> lore = new ArrayList<>();
            for (int i = 0; i < autoUpgradeProfiles.size(); i++) {
                AutoUpgradeProfile l = autoUpgradeProfiles.get(i);
                DifficultyMode difficulty = l.getDifficultyMode();
                lore.add(Component.text((i + 1) + ". " + l.getName() + " (" + difficulty.getShortName() + ")",
                        l.equals(autoUpgradeProfile) ? NamedTextColor.AQUA : NamedTextColor.GRAY
                ));
            }
            menu.setItem(1, 4,
                    new ItemBuilder(Material.BOOK)
                            .name(Component.text("Change Profile", NamedTextColor.GREEN))
                            .lore(lore)
                            .get(),
                    (m, e) -> {
                        int index = autoUpgradeProfiles.indexOf(autoUpgradeProfile);
                        int nextLoadout = index >= autoUpgradeProfiles.size() - 1 ? 0 : index + 1;
                        autoUpgradeProfile = autoUpgradeProfiles.get(nextLoadout);
                        openAbilityTree();
                    }
            );
            menu.setItem(2, 4,
                    new ItemBuilder(Material.WRITABLE_BOOK)
                            .name(Component.text("Create Profile", NamedTextColor.GREEN))
                            .lore(WordWrap.wrap(Component.text("Create a new profile to customize your experience.", NamedTextColor.GRAY), 150))
                            .get(),
                    (m, e) -> {
                        if (autoUpgradeProfiles.size() >= 8) {
                            warlordsPlayer.sendMessage(Component.text("You can only have up to 8 profiles per spec!", NamedTextColor.RED));
                        } else {
                            new SignGUI()
                                    .lines("", "Enter", "Profile Name", "")
                                    .onFinish((p, lines) -> {
                                        String name = lines[0];
                                        if (!name.matches("[a-zA-Z0-9 ]+")) {
                                            warlordsPlayer.sendMessage(Component.text("Invalid name!", NamedTextColor.RED));
                                            warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                            return null;
                                        }
                                        if (autoUpgradeProfiles.stream().anyMatch(i -> i.getName().equalsIgnoreCase(name))) {
                                            warlordsPlayer.sendMessage(Component.text("You already have a profile with that name!", NamedTextColor.RED));
                                            warlordsPlayer.playSound(warlordsPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                            return null;
                                        }
                                        AutoUpgradeProfile newProfile = new AutoUpgradeProfile(name);
                                        autoUpgradeProfiles.add(newProfile);
                                        autoUpgradeProfile = newProfile;
                                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                        openAbilityTreeAfterTick();
                                        return null;
                                    }).open(player);
                        }
                    }
            );
            menu.setItem(3, 4,
                    new ItemBuilder(Material.NAME_TAG)
                            .name(Component.text("Rename Profile", NamedTextColor.GREEN))
                            .lore(WordWrap.wrap(Component.text("Rename the current profile.", NamedTextColor.GRAY), 150))
                            .get(),
                    (m, e) -> {
                        new SignGUI()
                                .lines("", "Enter", "Profile Name", "")
                                .onFinish((p, lines) -> {
                                    String name = lines[0];
                                    if (!name.matches("[a-zA-Z0-9 ]+")) {
                                        player.sendMessage(Component.text("Invalid name!", NamedTextColor.RED));
                                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                        return null;
                                    }
                                    if (autoUpgradeProfiles.stream().anyMatch(l -> l.getName().equalsIgnoreCase(name))) {
                                        player.sendMessage(Component.text("You already have a profile with that name!", NamedTextColor.RED));
                                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 2, 0.5f);
                                        return null;
                                    }
                                    autoUpgradeProfile.setName(name);
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    openAbilityTreeAfterTick();
                                    return null;
                                }).open(player);
                    }
            );
            menu.setItem(5, 4,
                    new ItemBuilder(Material.LAVA_BUCKET)
                            .name(Component.text("Delete Profile", NamedTextColor.RED))
                            .lore(WordWrap.wrap(Component.text("Delete the current profile.", NamedTextColor.GRAY), 150))
                            .get(),
                    (m, e) -> {
                        if (autoUpgradeProfiles.size() == 1) {
                            player.sendMessage(Component.text("You must have at least one profile!", NamedTextColor.RED));
                            return;
                        }
                        Menu.openConfirmationMenu(
                                player,
                                "Delete Profile",
                                3,
                                Arrays.asList(
                                        Component.textOfChildren(
                                                Component.text("Delete Profile: ", NamedTextColor.GRAY),
                                                Component.text(autoUpgradeProfile.getName(), NamedTextColor.GOLD)
                                        ),
                                        Component.empty(),
                                        Component.textOfChildren(
                                                Component.text("WARNING: ", NamedTextColor.RED),
                                                Component.text("This cannot be undone!", NamedTextColor.GRAY)
                                        )
                                ),
                                Menu.GO_BACK,
                                (m2, e2) -> {
                                    autoUpgradeProfiles.remove(autoUpgradeProfile);
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    openAbilityTree();
                                },
                                (m2, e2) -> openAbilityTree(),
                                (m2) -> {
                                }
                        );
                    }
            );
            lore.clear();
            for (int i = 0; i < autoUpgradeProfiles.size(); i++) {
                lore.add(Component.text((i + 1) + ". " + autoUpgradeProfiles.get(i).getName(),
                        autoUpgradeProfiles.get(i).equals(autoUpgradeProfile) ? NamedTextColor.AQUA : NamedTextColor.GRAY
                ));
            }
            menu.setItem(6, 4,
                    new ItemBuilder(Material.TRIPWIRE_HOOK)
                            .name(Component.text("Change Profile Priority", NamedTextColor.GREEN))
                            .lore(lore)
                            .addLore(Component.empty())
                            .addLore(
                                    WordWrap.wrap(Component.text("Change the priority of the current profile, for when you have " +
                                                    "multiple profile with the same filters.", NamedTextColor.GRAY),
                                            170
                                    )
                            )
                            .get(),
                    (m, e) -> {
                        int loadoutIndex = autoUpgradeProfiles.indexOf(autoUpgradeProfile);
                        int newLoadoutIndex;
                        if (loadoutIndex == autoUpgradeProfiles.size() - 1) {
                            newLoadoutIndex = 0;
                        } else {
                            newLoadoutIndex = loadoutIndex + 1;
                        }
                        autoUpgradeProfiles.remove(autoUpgradeProfile);
                        autoUpgradeProfiles.add(newLoadoutIndex, autoUpgradeProfile);
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                        openAbilityTree();
                    }
            );
            lore.clear();
            DifficultyMode[] difficultyModes = DifficultyMode.VALUES;
            for (DifficultyMode value : difficultyModes) {
                lore.add(Component.text(value.name, autoUpgradeProfile.getDifficultyMode() == value ? NamedTextColor.AQUA : NamedTextColor.GRAY));
            }
            menu.setItem(7, 4,
                    new ItemBuilder(Material.COMPARATOR)
                            .name(Component.text("Bind to Mode", NamedTextColor.GREEN))
                            .lore(lore)
                            .get(),
                    (m, e) -> {
                        autoUpgradeProfile.setDifficultyMode(autoUpgradeProfile.getDifficultyMode().next());
                        openAbilityTree();
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    }
            );
        }, () -> {
            autoUpgradeProfile = new AutoUpgradeProfile();
        });
        if (autoUpgradeProfile == null) {
            autoUpgradeProfile = new AutoUpgradeProfile();
        }
        menu.setItem(
                0,
                4,
                new ItemBuilder(Material.BOOKSHELF)
                        .name(Component.text("Auto Upgrade Queue", NamedTextColor.GREEN))
                        .lore(autoUpgradeProfile.getAutoUpgradeEntries().isEmpty() ?
                              WordWrap.wrap(Component.text("You have no upgrades queued. ", NamedTextColor.GRAY)
                                                     .append(Component.text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD))
                                                     .append(Component.text("upgrades to add/remove them.")), 130) :
                              autoUpgradeProfile.getLore(this)
                        )
                        .addLore(
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("RIGHT-CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" to clear the queue.", NamedTextColor.GRAY)
                                )
                        )
                        .get(),
                (m, e) -> {
                    if (e.isRightClick()) {
                        autoUpgradeProfile.getAutoUpgradeEntries().clear();
                        openAbilityTree();
                    }
                }
        );
        menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private void openAbilityTreeAfterTick() {
        new BukkitRunnable() {
            @Override
            public void run() {
                openAbilityTree();
            }
        }.runTaskLater(Warlords.getInstance(), 2);
    }

    public List<Component> getUpgradeTreeInfo(AbstractUpgradeBranch<?> upgradeBranch, List<Upgrade> upgrades) {
        if (upgrades.isEmpty()) {
            return Collections.singletonList(Component.text("No upgrades available."));
        }
        String upgradeName = upgrades.get(0).getName();
        Upgrade lastUpgraded = null;
        for (int i = upgrades.size() - 1; i >= 0; i--) {
            Upgrade upgrade = upgrades.get(i);
            if (upgrade.isUnlocked()) {
                lastUpgraded = upgrade;
                break;
            }
        }
        List<Component> output = new ArrayList<>();
        TextComponent.Builder toAdd = Component.text();
        toAdd.append(Component.text(upgradeName.substring(0, upgradeName.indexOf(" - ")) + " ", lastUpgraded == null ? NamedTextColor.RED : NamedTextColor.GOLD));
        int currency = getWarlordsPlayer().getCurrency();
        int maxUpgrades = upgradeBranch.getMaxUpgrades();
        for (Upgrade upgrade : upgrades) {
            if (upgrade.isUnlocked()) {
                toAdd.append(Component.text("■", NamedTextColor.GREEN));
            } else {
                if (maxUpgrades <= 0) {
                    toAdd.append(Component.text("■", NamedTextColor.RED));
                } else if (currency >= upgrade.getCurrencyCost()) {
                    currency -= upgrade.getCurrencyCost();
                    toAdd.append(Component.text("■", NamedTextColor.YELLOW));
                } else {
                    toAdd.append(Component.text("■", NamedTextColor.GRAY));
                }
                maxUpgrades--;
            }
        }
        output.add(toAdd.build());
        if (lastUpgraded != null) {
            for (Component component : lastUpgraded.getDescription(NamedTextColor.GREEN)) {
                output.add(Component.text(" ").append(component));
            }
        }
        /*
//        output.append("\n");
        for (int i = upgrades.size() - 1; i >= 0; i--) {
            Upgrade upgrade = upgrades.get(i);
            if (upgrade.isUnlocked()) {
//                Upgrade nextUpgrade = i + 1 < upgrades.size() ? upgrades.get(i + 1) : null;
//                if (upgradeAfter != null) {
//                    String[] upgradeLore = upgrade.getDescription().split("\n");
//                    String[] nextUpgradeLore = upgradeAfter.getDescription().split("\n");
//                    for (int j = 0; j < Math.max(upgradeLore.length, nextUpgradeLore.length); j++) {
//                        if (j < upgradeLore.length && j < nextUpgradeLore.length) {
//                            getBlockedLore(output, upgradeLore[j], nextUpgradeLore[j]);
//                        } else if (j < upgradeLore.length) {
//                            getBlockedLore(output, upgradeLore[j], "");
//                        } else {
//                            getBlockedLore(output, "", nextUpgradeLore[j]);
//                        }
//                        output.append("\n");
//                    }
//                } else {
//                    output.append(("\n" + upgrade.getDescription()).replaceAll("\n", "\n " + ChatColor.GREEN));
//                }
                output.append(("\n" + upgrade.getDescription()).replaceAll("\n", "\n " + ChatColor.GREEN));
//                if (nextUpgrade != null) {
//                    //output.append("\n");
//                    output.append(("\n" + nextUpgrade.getDescription()).replaceAll("\n", "\n " + ChatColor.GRAY));
//                }
                break;
            }
        }
         */
        return output;
    }

    public List<Component> getMasterUpgradeTreeInfo(AbstractUpgradeBranch<?> upgradeBranch, Upgrade upgrade) {
        if (upgrade == null) {
            return Collections.singletonList(Component.text("No upgrades"));
        }
        List<Component> output = new ArrayList<>();
        TextComponent.Builder toAdd = Component.text();
        toAdd.append(Component.text(upgrade.getName() + " ", upgrade.isUnlocked() ? NamedTextColor.GOLD : NamedTextColor.RED, TextDecoration.BOLD));
        if (upgrade.isUnlocked()) {
            toAdd.append(Component.text("■", NamedTextColor.GREEN));
        } else {
            if (maxMasterUpgrades <= 0) {
                toAdd.append(Component.text("■", NamedTextColor.RED));
            } else if (upgradeBranch.getMaxUpgrades() == 0 && getWarlordsPlayer().getCurrency() >= upgrade.getCurrencyCost()) {
                toAdd.append(Component.text("■", NamedTextColor.YELLOW));

            } else {
                toAdd.append(Component.text("■", NamedTextColor.GRAY));

            }
        }
        output.add(toAdd.build());
        if (upgrade.isUnlocked()) {
            for (Component component : upgrade.getDescription(NamedTextColor.GREEN)) {
                output.add(Component.text(" ").append(component));
            }
        }
        return output;
    }

    public WarlordsPlayer getWarlordsPlayer() {
        return warlordsPlayer;
    }

    public void getBlockedLore(StringBuilder output, String upgradeLore, String nextUpgradeLore) {
        int loreLength = 250;
        int upgradeLoreLength = DefaultFontInfo.getStringLength(upgradeLore);
        int nextUpgradeLoreLength = DefaultFontInfo.getStringLength(nextUpgradeLore);
        int space = loreLength - upgradeLoreLength - nextUpgradeLoreLength;
        if (space < 0) {
            return;
        }
        output.append(upgradeLore);
        output.append(" ".repeat(Math.round(space / 3f) + 1));
        output.append(nextUpgradeLore);
    }

    public List<AbstractUpgradeBranch<?>> getUpgradeBranches() {
        return upgradeBranches;
    }

    public List<UpgradeLog> getUpgradeLog() {
        return upgradeLog;
    }

    public int getMaxMasterUpgrades() {
        return maxMasterUpgrades;
    }

    public void setMaxMasterUpgrades(int maxMasterUpgrades) {
        this.maxMasterUpgrades = maxMasterUpgrades;
    }

    public AutoUpgradeProfile getAutoUpgradeProfile() {
        if (autoUpgradeProfile == null) {
            DatabaseManager.getPlayer(warlordsPlayer.getUuid(), this::resetAutoUpgradeProfile);
        }
        return autoUpgradeProfile;
    }

    public void resetAutoUpgradeProfile(DatabasePlayer databasePlayer) {
        List<AutoUpgradeProfile> autoUpgradeProfiles = databasePlayer
                .getPveStats()
                .getAutoUpgradeProfiles()
                .computeIfAbsent(warlordsPlayer.getSpecClass(), k -> new ArrayList<>());
        if (autoUpgradeProfiles.isEmpty()) {
            autoUpgradeProfile = new AutoUpgradeProfile();
            autoUpgradeProfiles.add(autoUpgradeProfile);
        } else if (autoUpgradeProfile == null || !autoUpgradeProfiles.contains(autoUpgradeProfile)) {
            Game game = warlordsPlayer.getGame();
            if (game == null) {
                autoUpgradeProfile = autoUpgradeProfiles.get(0);
            } else {
                PveOption pveOption = game.getOptions()
                                          .stream()
                                          .filter(PveOption.class::isInstance)
                                          .map(PveOption.class::cast)
                                          .findFirst()
                                          .orElse(null);
                if (pveOption == null) {
                    autoUpgradeProfile = autoUpgradeProfiles.get(0);
                } else {
                    autoUpgradeProfile = autoUpgradeProfiles
                            .stream()
                            .filter(profile -> {
                                DifficultyMode difficultyMode = profile.getDifficultyMode();
                                return difficultyMode == null || (difficultyMode.validGameMode(game.getGameMode()) &&
                                        difficultyMode.validDifficulty(pveOption.getDifficulty()));
                            })
                            .findFirst()
                            .orElse(autoUpgradeProfiles.get(0));
                }
            }
        }
    }

    public static class UpgradeLog {

        @Field("time_elapsed")
        private int gameTimeLeft;
        @Field("name")
        private String upgradeName;
        @Field("description")
        private String upgradeDescription;

        public UpgradeLog(int gameTimeLeft, String upgradeName, String upgradeDescription) {
            this.gameTimeLeft = gameTimeLeft;
            this.upgradeName = upgradeName;
            this.upgradeDescription = upgradeDescription;
        }
    }
}
