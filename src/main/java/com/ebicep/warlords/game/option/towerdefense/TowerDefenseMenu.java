package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.game.option.towerdefense.attributes.upgradeable.Upgradeable;
import com.ebicep.warlords.game.option.towerdefense.events.TowerSellEvent;
import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMobInfo;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import com.ebicep.warlords.game.option.towerdefense.waves.FixedPlayerWave;
import com.ebicep.warlords.game.option.towerdefense.waves.TowerDefenseSpawnWaveAction;
import com.ebicep.warlords.game.option.towerdefense.waves.WaveAction;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TowerDefenseMenu {

    public static final String SUMMON_MENU_TITLE = "Summon Troops";

    public static void openTowerMenu(Player player, WarlordsEntity warlordsEntity, AbstractTower tower) {
        Menu menu = new Menu(tower.getName(), 9 * 6);

        UUID owner = tower.getOwner();
        WarlordsTower warlordsTower = tower.getWarlordsTower();

        WarlordsEntity warlordsPlayerOwner = Warlords.getPlayer(owner);
        TowerDefensePlayerInfo playerInfo = tower.getTowerDefenseOption().getPlayerInfo(warlordsPlayerOwner);

        Component name = warlordsPlayerOwner == null ? Component.text("Unknown", NamedTextColor.BLACK, TextDecoration.ITALIC) : warlordsPlayerOwner.getColoredName();
        PlayerStatisticsMinute.Entry stats = warlordsTower.getMinuteStats().total();
        ItemBuilder itemBuilder = new ItemBuilder(HeadUtils.getHead(owner))
                .name(ComponentBuilder.create("Owner: ", NamedTextColor.GRAY).append(name).build());
        long kills = stats.getKills();
        long damage = stats.getDamage();
        long healing = stats.getHealing();
        if (kills != 0) {
            itemBuilder.addLore(
                    ComponentBuilder.create("Kills: ", NamedTextColor.GRAY).text(NumberFormat.addCommaAndRound(kills), NamedTextColor.GOLD).build()
            );
            if (damage != 0 || healing != 0) {
                itemBuilder.addLore(Component.empty());
            }
        }
        if (damage != 0) {
            itemBuilder.addLore(ComponentBuilder.create("Damage: ", NamedTextColor.GRAY).text(NumberFormat.addCommaAndRound(damage), NamedTextColor.RED).build());
        }
        if (healing != 0) {
            itemBuilder.addLore(ComponentBuilder.create("Healing: ", NamedTextColor.GRAY).text(NumberFormat.addCommaAndRound(healing), NamedTextColor.GREEN).build());
        }

        menu.setItem(4, 0, itemBuilder.get(), (m, e) -> {});

        List<AbstractAbility> abilities = warlordsTower.getAbilities();
        for (int i = 0; i < abilities.size(); i++) {
            AbstractAbility ability = abilities.get(i);
            ItemBuilder builder = new ItemBuilder(ability.getItem());
            if (ability instanceof HitBox) {
                builder.addLore(
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                Component.text("to view range", NamedTextColor.YELLOW)
                        )
                );
            }
            menu.setItem(i + 1, 2,
                    builder.get(),
                    (m, e) -> {
                        if (e.isRightClick() && ability instanceof HitBox hitBox) {
                            BukkitTask renderTask = playerInfo.getRenderTask();
                            if (renderTask != null) {
                                renderTask.cancel();
                            }
                            // TODO resource pack annoucement + toggle showing particle/shadow/both
                            Display display = tower.getBottomCenterLocation().getWorld().spawn(
                                    new LocationBuilder(tower.getBottomCenterLocation())
                                            .addY(1)
                                    ,
                                    TextDisplay.class,
                                    d -> {
                                        d.setShadowRadius(hitBox.getHitBoxRadius().getCalculatedValue() + 1.5f);
                                        d.setShadowStrength(2);
                                        d.setTransformation(new Transformation(
                                                new Vector3f(),
                                                new AxisAngle4f(),
                                                new Vector3f(5),
                                                new AxisAngle4f()
                                        ));
                                    }
                            );
                            playerInfo.setRenderTask(new GameRunnable(tower.getGame()) {
                                int secondsElapsed = 0;

                                @Override
                                public void run() {
                                    hitBox.renderHitBox(tower.getBottomCenterLocation(), player);
                                    if (secondsElapsed++ > 5) {
                                        display.remove();
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(0, 20));
                        }
                    }
            );
        }

        if (tower instanceof Upgradeable upgradeable) {
            upgradeable.addToMenu(menu, player, warlordsEntity, (AbstractTower & Upgradeable) tower);
        }

        menu.setItem(5, 5,
                new ItemBuilder(Material.RED_CONCRETE)
                        .name(Component.text("Sell", NamedTextColor.RED))
                        .get(),
                (m, e) -> {
                    Menu.openConfirmationMenu(player,
                            "Confirm Sell",
                            3,
                            Collections.singletonList(Component.text("Sell", NamedTextColor.GRAY)),
                            Menu.GO_BACK,
                            (m2, e2) -> {
                                Bukkit.getPluginManager().callEvent(new TowerSellEvent(tower));
                                player.closeInventory();
                            },
                            (m2, e2) -> openTowerMenu(player, warlordsEntity, tower),
                            (m2) -> {
                            }
                    );
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

        // TODO buying special effects

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openSummonTroopsMenu(
            Player player,
            WarlordsEntity warlordsEntity,
            TowerDefenseSpawner spawner,
            TowerDefensePlayerInfo playerInfo
    ) {
        Menu menu = new Menu(SUMMON_MENU_TITLE, 9 * 6);

        int page = playerInfo.getCurrentMobMenuPage();

        FixedPlayerWave playerWave = playerInfo.getPlayerWave();
        List<WaveAction<TowerDefenseOption>> actions = playerWave.getActions();
        int lastSpawnIndex = playerWave.getWaveActionIndex();

        int x = 1;
        int y = 2;
        for (int i = ((page - 1) * 27); i < TowerDefenseMobInfo.VALUES.length; i++) {
            TowerDefenseMobInfo mobInfo = TowerDefenseMobInfo.VALUES[i];
            Mob mob = mobInfo.getMob();
            int cost = mobInfo.getCost();
            float expReward = mobInfo.getIncomeModifier();
            int spawnDelay = mobInfo.getSpawnDelay();
            menu.setItem(x, y,
                    new ItemBuilder(mobInfo.getMob().getHead())
                            .name(Component.text(mob.name, NamedTextColor.GREEN))
                            .lore(
                                    Component.text("MOB DESCRIPTION"),
                                    Component.empty(),
                                    ComponentBuilder.create("Summon Cost: ")
                                                    .text("❂ " + cost, NamedTextColor.GOLD)
                                                    .build(),
                                    ComponentBuilder.create("Health: ")
                                                    .text(NumberFormat.formatOptionalTenths(mob.maxHealth), NamedTextColor.GREEN)
                                                    .build(),
                                    ComponentBuilder.create("Damage: ")
                                                    .text(NumberFormat.formatOptionalTenths(mob.minMeleeDamage), NamedTextColor.RED)
                                                    .build(),
                                    ComponentBuilder.create("Speed: ")
                                                    .text(NumberFormat.formatOptionalHundredths(mob.walkSpeed), NamedTextColor.WHITE)
                                                    .build(),
                                    ComponentBuilder.create("Income: ")
                                                    .text(NumberFormat.formatOptionalTenths(expReward), NamedTextColor.DARK_AQUA)
                                                    .build(),
                                    Component.empty(),
                                    ComponentBuilder.create()
                                                    .text("LEFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                                                    .text("to add to queue")
                                                    .build(),
                                    ComponentBuilder.create()
                                                    .text("SHIFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                                                    .text("to fill queue")
                                                    .build()
                            )
                            .get(),
                    (m, e) -> {
                        if (e.isLeftClick()) {
                            if (TowerDefenseSpawner.MAX_PLAYER_SPAWN_AMOUNT == actions.size() / 2 - lastSpawnIndex / 2) {
                                player.sendMessage(Component.text("You have reached the maximum amount of mobs you can spawn at a time!", NamedTextColor.RED));
                                return;
                            }
                            int amountSpawned = playerWave.add(mob, spawnDelay, e.isShiftClick(), warlordsEntity);
                            if (amountSpawned > 0) {
                                playerInfo.addIncomeRate(expReward * amountSpawned);
                                warlordsEntity.sendMessage(ComponentBuilder.create(amountSpawned + "x ", NamedTextColor.WHITE)
                                                                           .text(mob.name, NamedTextColor.GREEN)
                                                                           .text(" spawned (", NamedTextColor.GRAY)
                                                                           .text("+" + (expReward * amountSpawned) + " income", NamedTextColor.DARK_AQUA)
                                                                           .text(")", NamedTextColor.GRAY)
                                                                           .build()
                                );
                            }
                            openSummonTroopsMenu(player, warlordsEntity, spawner, playerInfo);
                        }
                    }
            );
            if (x == 7 && y == 4) {
                break;
            }
            if (x % 7 == 0) {
                x = 1;
                y++;
            } else {
                x++;
            }
        }

        x = 2;
        for (int i = lastSpawnIndex; i < actions.size(); i++) {
            WaveAction<TowerDefenseOption> waveAction = actions.get(i);
            if (!(waveAction instanceof TowerDefenseSpawnWaveAction spawnAction)) {
                continue;
            }
            Mob mob = spawnAction.getMob();
            menu.setItem(x, 1,
                    new ItemBuilder(mob.getHead())
                            .name(Component.text(mob.name, NamedTextColor.GREEN))
                            .get(),
                    (m, e) -> {
                    }
            );

            x++;
        }

        if (page - 1 > 0) {
            menu.setItem(0, 3,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> {
                        playerInfo.setCurrentMobMenuPage(page - 1);
                        openSummonTroopsMenu(player, warlordsEntity, spawner, playerInfo);
                    }
            );
        }
        if (TowerDefenseMobInfo.VALUES.length > (page * 27)) {
            menu.setItem(8, 3,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> {
                        playerInfo.setCurrentMobMenuPage(page + 1);
                        openSummonTroopsMenu(player, warlordsEntity, spawner, playerInfo);
                    }
            );
        }


        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}
