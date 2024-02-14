package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.game.option.towerdefense.events.TowerSellEvent;
import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMobInfo;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import com.ebicep.warlords.game.option.towerdefense.waves.FixedPlayerWave;
import com.ebicep.warlords.game.option.towerdefense.waves.TowerDefenseSpawnWaveAction;
import com.ebicep.warlords.game.option.towerdefense.waves.WaveAction;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class TowerDefenseMenu {

    public static final MobGroup[] TOWER_DEFENSE_MOB_GROUPS = {
            new MobGroup(new ItemBuilder(Material.ZOMBIE_HEAD)
                    .name(Component.text("Zombie", NamedTextColor.GREEN))
                    .get(),
                    TowerDefenseMobInfo.ZOMBIE
            ),
            new MobGroup(new ItemBuilder(Material.SKELETON_SKULL)
                    .name(Component.text("Skeleton", NamedTextColor.GREEN))
                    .get(),
                    TowerDefenseMobInfo.SKELETON
            ),
    };

    public static void openBuildMenu(Player player, AbstractTower tower) {
        Pair<Integer, Integer> ownerHeadPos = new Pair<>(2, 2);
        Pair<Integer, Integer> sellPos = new Pair<>(6, 3);

        Menu menu = new Menu(tower.getName(), 9 * 6);

        if (tower instanceof Upgradeable upgradeable) {
            ownerHeadPos.setA(7);
            ownerHeadPos.setB(1);
            sellPos.setA(7);
            sellPos.setB(3);
            upgradeable.addToMenu(menu, player, tower);
        }

        menu.setItem(ownerHeadPos.getA(), ownerHeadPos.getB(),
                new ItemBuilder(Material.PLAYER_HEAD) //HeadUtils.getHead(tower.getOwner()) TODO
                                                      .get(),
                (m, e) -> {

                }
        );
        menu.setItem(sellPos.getA(), sellPos.getB(),
                new ItemBuilder(Material.RED_CONCRETE)
                        .name(Component.text("Sell", NamedTextColor.RED))
                        .get(),
                (m, e) -> {
                    Bukkit.getPluginManager().callEvent(new TowerSellEvent(tower));
                    player.closeInventory();
                }
        );
        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openMarket(Player player, WarlordsEntity warlordsEntity, TowerDefensePlayerInfo playerInfo) {
        Menu menu = new Menu("Market", 9 * 6);

        menu.setItem(4, 0,
                new ItemBuilder(Material.BOOK)
                        .name(Component.text("INFO HERE"))
                        .get(),
                (m, e) -> {

                }
        );

        int currentRate = playerInfo.getCurrentInsigniaRate();
        List<TowerDefenseUtils.RateInfo> rateCosts = TowerDefenseUtils.INSIGNIA_RATE_EXP_COST;
        for (int i = 0; i < rateCosts.size(); i++) {
            TowerDefenseUtils.RateInfo rateInfo = rateCosts.get(i);
            int rate = rateInfo.rate();
            int expCost = rateInfo.expCost();
            if (currentRate != rate) {
                continue;
            }
            List<Component> lore = new ArrayList<>();
            lore.add(Component.empty());
            lore.add(Component.text("Current Rate: ", NamedTextColor.GRAY).append(Component.text(rate + " ❂/sec", NamedTextColor.GOLD)));
            Material material;
            BiConsumer<Menu, InventoryClickEvent> clickHandler;
            if (i == rateCosts.size() - 1) {
                material = rateInfo.material();
                lore.add(Component.empty());
                lore.add(Component.text("MAXED!", NamedTextColor.AQUA, TextDecoration.BOLD));
                clickHandler = (m, e) -> {

                };
            } else {
                TowerDefenseUtils.RateInfo nextRateInfo = rateCosts.get(i + 1);
                material = nextRateInfo.material();
                lore.add(Component.text("Next Rate: ").append(Component.text(nextRateInfo.rate() + " ❂/sec", NamedTextColor.GOLD)));
                lore.add(Component.empty());
                lore.add(Component.text("Cost: ").append(Component.text(expCost + " exp", NamedTextColor.DARK_AQUA)));
                clickHandler = (m, e) -> {
                    if (playerInfo.getCurrentExp() >= expCost) {
                        playerInfo.addCurrentExp(-expCost);
                        playerInfo.setCurrentInsigniaRate(nextRateInfo.rate());
                        openMarket(player, warlordsEntity, playerInfo);
                    }
                };
            }
            menu.setItem(4, 2,
                    new ItemBuilder(material)
                            .name(Component.text("Upgrade Insignia Rate", NamedTextColor.GREEN))
                            .lore(lore)
                            .get(),
                    clickHandler
            );
        }


        // TODO buying special effects

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openSummonTroopsMenu(Player player, WarlordsEntity warlordsEntity, TowerDefenseSpawner spawner, TowerDefensePlayerInfo playerInfo) {
        Menu menu = new Menu("Summon Troops", 9 * 6);

        FixedPlayerWave wave;
        List<FixedPlayerWave> playerWaves = playerInfo.getPlayerWaves();
        if (playerWaves.isEmpty() || playerWaves.get(playerWaves.size() - 1).isSent()) {
            playerWaves.add(wave = new FixedPlayerWave());
        } else {
            wave = playerWaves.get(playerWaves.size() - 1);
        }
        List<WaveAction<TowerDefenseOption>> actions = wave.getActions();
        int numberOfActions = actions.size();

        int x = 1;
        int y = 1;
        for (int i = 0; i < TOWER_DEFENSE_MOB_GROUPS.length; i++) {
            MobGroup mobGroup = TOWER_DEFENSE_MOB_GROUPS[i];
            Map<MobGroup, Integer> unlockedMobUpgrades = playerInfo.getUnlockedMobUpgrades();
            int unlockedIndex = unlockedMobUpgrades.getOrDefault(mobGroup, 0);
            TowerDefenseMobInfo[] mobInfos = mobGroup.mobsWithCost;
            Mob mob = mobInfos[unlockedIndex].getMob();
            int cost = mobInfos[unlockedIndex].getCost();
            int expReward = mobInfos[unlockedIndex].getExpReward();
            menu.setItem(x, y,
                    new ItemBuilder(mobGroup.head)
                            .lore(
                                    Component.text("MOB DESCRIPTION"),
                                    Component.empty(),
                                    ComponentBuilder.create("Summon Cost: ")
                                                    .text(cost + " ❂", NamedTextColor.GOLD)
                                                    .build(),
                                    ComponentBuilder.create("Health: ")
                                                    .text(NumberFormat.formatOptionalTenths(mob.maxHealth), NamedTextColor.GREEN)
                                                    .build(),
                                    ComponentBuilder.create("Damage: ")
                                                    .text(NumberFormat.formatOptionalTenths(mob.minMeleeDamage) + " - " + NumberFormat.formatOptionalTenths(mob.maxMeleeDamage),
                                                            NamedTextColor.RED
                                                    )
                                                    .build(),
                                    ComponentBuilder.create("Speed: ")
                                                    .text(NumberFormat.formatOptionalHundredths(mob.walkSpeed), NamedTextColor.WHITE)
                                                    .build(),
                                    ComponentBuilder.create("Exp Reward: ")
                                                    .text(NumberFormat.formatOptionalTenths(expReward), NamedTextColor.DARK_AQUA)
                                                    .build(),
                                    Component.empty(),
                                    ComponentBuilder.create()
                                                    .text("LEFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                                                    .text("to add")
                                                    .build(),
                                    ComponentBuilder.create()
                                                    .text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                                                    .text("to remove")
                                                    .build(),
                                    ComponentBuilder.create()
                                                    .text("SHIFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                                                    .text("for stack")
                                                    .build()
                            )
                            .get(),
                    (m, e) -> {
                        if (e.getClick() == ClickType.DROP) {
                            openMobUpgradeMenu(player, warlordsEntity, spawner, playerInfo, mobGroup);
                            return;
                        }
                        boolean shiftClick = e.isShiftClick();
                        if (e.isLeftClick()) {
                            if (TowerDefenseSpawner.MAX_PLAYER_SPAWN_AMOUNT == numberOfActions) {
                                player.sendMessage(Component.text("You have reached the maximum amount of mobs you can spawn at a time!", NamedTextColor.RED));
                                return;
                            }
                            wave.add(mob, shiftClick ? TowerDefenseSpawner.MAX_PLAYER_SPAWN_AMOUNT - numberOfActions : 1, warlordsEntity.getTeam());
                            openSummonTroopsMenu(player, warlordsEntity, spawner, playerInfo);
                        } else if (e.isRightClick()) {
                            if (numberOfActions == 0) {
                                return;
                            }
                            wave.removeLast(mob, shiftClick ? numberOfActions : 1);
                            openSummonTroopsMenu(player, warlordsEntity, spawner, playerInfo);
                        }
                    }
            );
            if (i == 4) {
                x++;
                y++;
            }
        }

        List<Component> lore = new ArrayList<>();
        Mob lastMob = null;
        int amount = 1;
        int counter = 1;
        for (int i = 0; i < actions.size(); i++) {
            WaveAction<TowerDefenseOption> action = actions.get(i);
            if (!(action instanceof TowerDefenseSpawnWaveAction spawnAction)) {
                continue;
            }
            Mob mob = spawnAction.getMob();
            if (lastMob == mob) {
                amount++;
            } else {
                if (lastMob != null) {
                    lore.add(ComponentBuilder.create(counter + ": ", NamedTextColor.GRAY)
                                             .text(lastMob.name, NamedTextColor.GOLD)
                                             .text(" x" + amount, NamedTextColor.GRAY)
                                             .build()
                    );
                    counter++;
                }
                lastMob = mob;
                amount = 1;
            }
            if (i == actions.size() - 1) {
                lore.add(ComponentBuilder.create(counter + ": ", NamedTextColor.GRAY)
                                         .text(lastMob.name, NamedTextColor.GOLD)
                                         .text(" x" + amount, NamedTextColor.GRAY)
                                         .build()
                );
            }
        }
        menu.setItem(8, 5, new ItemBuilder(Material.SPAWNER)
                        .name(Component.text("Send Wave", NamedTextColor.GREEN))
                        .amount(numberOfActions)
                        .lore(lore)
                        .get(),
                (m, e) -> {
                    wave.setSent(true);
                    spawner.startPlayerWave(wave);
                    player.sendMessage(Component.text("Wave sent!", NamedTextColor.GREEN));
                    player.closeInventory();
                }
        );
        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openMobUpgradeMenu(Player player, WarlordsEntity warlordsEntity, TowerDefenseSpawner spawner, TowerDefensePlayerInfo playerInfo, MobGroup mobGroup) {
        Menu menu = new Menu("Summon Troops", 9 * 4);

        Map<MobGroup, Integer> unlockedMobUpgrades = playerInfo.getUnlockedMobUpgrades();
        int unlockedIndex = unlockedMobUpgrades.getOrDefault(mobGroup, 0);
        TowerDefenseMobInfo[] mobs = mobGroup.mobsWithCost;
        for (int i = 0; i < mobs.length; i++) {
            TowerDefenseMobInfo mobInfo = mobs[i];
            Mob mob = mobInfo.getMob();
            int cost = mobInfo.getCost();
            int expReward = mobInfo.getExpReward();
            int unlockCost = mobInfo.getUnlockCost();
            boolean unlocked = i <= unlockedIndex;
            boolean previousUnlocked = i == unlockedIndex + 1;
            boolean canUnlock = playerInfo.getCurrentExp() >= unlockCost;
            menu.setItem(i + 2, 1,
                    new ItemBuilder(unlocked ? Material.GREEN_STAINED_GLASS_PANE : canUnlock ? Material.ORANGE_STAINED_GLASS : Material.RED_STAINED_GLASS)
                            .lore(
                                    Component.text("MOB DESCRIPTION"),
                                    Component.empty(),
                                    Component.text("MOB DESCRIPTION"),
                                    Component.empty(),
                                    ComponentBuilder.create("Summon Cost: ")
                                                    .text(cost + " ❂", NamedTextColor.GOLD)
                                                    .build(),
                                    ComponentBuilder.create("Health: ")
                                                    .text(NumberFormat.formatOptionalTenths(mob.maxHealth), NamedTextColor.GREEN)
                                                    .build(),
                                    ComponentBuilder.create("Damage: ")
                                                    .text(NumberFormat.formatOptionalTenths(mob.minMeleeDamage) + " - " + NumberFormat.formatOptionalTenths(mob.maxMeleeDamage),
                                                            NamedTextColor.RED
                                                    )
                                                    .build(),
                                    ComponentBuilder.create("Speed: ")
                                                    .text(NumberFormat.formatOptionalHundredths(mob.walkSpeed), NamedTextColor.WHITE)
                                                    .build(),
                                    ComponentBuilder.create("Exp Reward: ")
                                                    .text(NumberFormat.formatOptionalTenths(expReward), NamedTextColor.DARK_AQUA)
                                                    .build()
                            )
                            .get(),
                    (m, e) -> {
                        if (unlocked) {
                            return;
                        }
                        if (!previousUnlocked) {
                            player.sendMessage(Component.text("You must unlock the previous upgrade first!", NamedTextColor.RED));
                            return;
                        }
                        if (!canUnlock) {
                            player.sendMessage(Component.text("You do not have enough exp to unlock this upgrade!", NamedTextColor.RED));
                            return;
                        }
                        player.sendMessage(Component.text("Upgrade message here", NamedTextColor.GREEN)); // TODO
                        playerInfo.addCurrentExp(-unlockCost);
                        unlockedMobUpgrades.put(mobGroup, unlockedIndex + 1);
                        openMobUpgradeMenu(player, warlordsEntity, spawner, playerInfo, mobGroup);
                    }
            );
        }


        menu.setItem(3, 3, Menu.MENU_BACK, (m, e) -> openSummonTroopsMenu(player, warlordsEntity, spawner, playerInfo));
        menu.setItem(4, 3, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    record MobGroup(ItemStack head, TowerDefenseMobInfo[] mobsWithCost) {
    }

}
