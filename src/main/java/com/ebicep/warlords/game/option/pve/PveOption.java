package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.*;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.WeaponOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.AutoUpgradeProfile;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zombie;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public interface PveOption extends Option {

    default void mobTick() {
        for (AbstractMob<?> mob : new ArrayList<>(getMobs())) {
            mob.whileAlive(getMobsMap().get(mob) - getTicksElapsed(), this);
            if (mob.isShowBossBar()) {
                mob.bossBar(getGame(), true);
            }
        }
    }

    Set<AbstractMob<?>> getMobs();

    ConcurrentHashMap<AbstractMob<?>, Integer> getMobsMap();

    int getTicksElapsed();

    Game getGame();

    int playerCount();

    default int mobCount() {
        return (int) getMobs()
                .stream()
                .filter(mob -> mob.getWarlordsNPC().getTeam() == Team.RED)
                .count();
    }

    default int getWaveCounter() {
        return 1;
    }

    default DifficultyIndex getDifficulty() {
        return DifficultyIndex.NORMAL;
    }

    default void spawnNewMob(AbstractMob<?> mob) {
        spawnNewMob(mob, Team.RED);
    }

    void spawnNewMob(AbstractMob<?> mob, Team team);

    default boolean isPauseMobSpawn() {
        return false;
    }

    default void setPauseMobSpawn(boolean pauseMobSpawn) {
    }

    default Listener getBaseListener() {
        return new Listener() {
            @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
            public void onWin(WarlordsGameTriggerWinEvent event) {
                getRewards().storeRewards();
            }

            @EventHandler
            public void onEvent(WarlordsDamageHealingEvent event) {
                WarlordsEntity attacker = event.getAttacker();
                WarlordsEntity receiver = event.getWarlordsEntity();

                if (event.isDamageInstance()) {
                    if (attacker instanceof WarlordsNPC) {
                        AbstractMob<?> mob = ((WarlordsNPC) attacker).getMob();
                        if (getMobsMap().containsKey(mob)) {
                            mob.onAttack(attacker, receiver, event);
                        }
                    }

                    if (receiver instanceof WarlordsNPC) {
                        AbstractMob<?> mob = ((WarlordsNPC) receiver).getMob();
                        if (getMobsMap().containsKey(mob)) {
                            mob.onDamageTaken(receiver, attacker, event);
                        }
                    }
                }
            }

            @EventHandler
            public void onAddCurrency(WarlordsAddCurrencyFinalEvent event) {
                WarlordsEntity player = event.getWarlordsEntity();
                if (!(player instanceof WarlordsPlayer warlordsPlayer)) {
                    return;
                }
                AbilityTree abilityTree = ((WarlordsPlayer) player).getAbilityTree();
                if (abilityTree == null) {
                    return;
                }
                AutoUpgradeProfile autoUpgradeProfile = abilityTree.getAutoUpgradeProfile();
                if (autoUpgradeProfile == null) {
                    return;
                }
                List<AutoUpgradeProfile.AutoUpgradeEntry> autoUpgradeEntries = autoUpgradeProfile.getAutoUpgradeEntries();
                for (AutoUpgradeProfile.AutoUpgradeEntry entry : autoUpgradeEntries) {
                    AbstractUpgradeBranch<?> upgradeBranch = abilityTree.getUpgradeBranches().get(entry.getBranchIndex());
                    AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType upgradeType = entry.getUpgradeType();
                    List<Upgrade> upgradeList = upgradeType.getUpgradeFunction.apply(upgradeBranch);
                    Upgrade upgrade = upgradeList.get(entry.getUpgradeIndex());
                    if (upgrade.isUnlocked()) {
                        continue;
                    }
                    if (player.getCurrency() < upgrade.getCurrencyCost() && upgradeBranch.getFreeUpgrades() <= 0) {
                        return;
                    }
                    switch (upgradeType) {
                        case A, B -> upgradeBranch.purchaseUpgrade(upgradeList, warlordsPlayer, upgrade, entry.getUpgradeIndex(), true);
                        case MASTER -> upgradeBranch.purchaseMasterUpgrade(warlordsPlayer, upgradeBranch.getMasterUpgrade(), true);
                        case MASTER2 -> upgradeBranch.purchaseMasterUpgrade(warlordsPlayer, upgradeBranch.getMasterUpgrade2(), true);
                    }
                }
            }

            @EventHandler
            public void onMobTarget(EntityTargetLivingEntityEvent event) {
                Entity entity = ((CraftEntity) event.getEntity()).getHandle();
                if (!(entity instanceof LivingEntity entityLiving)) {
                    return;
                }
                if (getMobsMap().keySet().stream().noneMatch(abstractMob -> Objects.equals(abstractMob.getLivingEntity(), entityLiving))) {
                    return;
                }
                if (entityLiving instanceof Mob) {
                    LivingEntity newTarget = event.getTarget();
                    LivingEntity oldTarget = ((Mob) entityLiving).getTarget();
                    if (entityLiving.hasPotionEffect(PotionEffectType.BLINDNESS) && newTarget != null) {
                        event.setCancelled(true);
                        return;
                    }
                    if (newTarget == null) {
                        if (oldTarget instanceof Player) {
                            //setting target to player zombie
                            getGame().warlordsPlayers()
                                     .filter(warlordsPlayer -> warlordsPlayer.getUuid().equals(oldTarget.getUniqueId()))
                                     .findFirst()
                                     .ifPresent(warlordsPlayer -> {
                                         if (!(warlordsPlayer.getEntity() instanceof Player)) {
                                             event.setTarget(warlordsPlayer.getEntity());
                                         }
                                     });
                        }
                    } else {
                        if (oldTarget instanceof Zombie) {
                            //makes sure player that rejoins is still the target
                            getGame().warlordsPlayers()
                                     .filter(warlordsPlayer -> warlordsPlayer.getEntity().equals(oldTarget))
                                     .findFirst()
                                     .ifPresent(warlordsPlayer -> event.setCancelled(true));
                        }
                        if (newTarget.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }

            @EventHandler
            public void onWeaponDrop(WarlordsGiveWeaponEvent event) {
                getRewards().getPlayerRewards(event.getWarlordsEntity().getUuid())
                            .getWeaponsFound()
                            .add(event.getWeapon());
            }

            @EventHandler(priority = EventPriority.MONITOR)
            public void onMobDrop(WarlordsGiveMobDropEvent event) {
                if (!event.getStolenBy().isEmpty()) {
                    return;
                }
                addMobDrop(event.getWarlordsEntity(), event.getMobDrop());
            }

            @EventHandler
            public void onMobDrop(WarlordsGiveStolenMobDropEvent event) {
                addMobDrop(event.getWarlordsEntity(), event.getMobDrop());
            }

            @EventHandler
            public void onItemDrop(WarlordsGiveItemEvent event) {
                getRewards().getPlayerRewards(event.getWarlordsEntity().getUuid())
                            .getItemsFound()
                            .add(event.getItem());
            }

            @EventHandler
            public void onItemDrop(WarlordsGiveBlessingFoundEvent event) {
                getRewards().getPlayerRewards(event.getWarlordsEntity().getUuid())
                            .addBlessingsFound();
            }
        };
    }

    @Override
    default void onGameEnding(@Nonnull Game game) {
        getMobs().forEach(mob -> mob.bossBar(game, false));
    }

    @Override
    default void onPlayerQuit(Player player) {
        getMobs().forEach(mob -> player.hideBossBar(mob.getBossBar()));
    }

    @Override
    default void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer warlordsPlayer) {
            for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
                ability.setInPve(true);
            }
            if (player.getEntity() instanceof Player) {
                getGame().setPlayerTeam((OfflinePlayer) player.getEntity(), Team.BLUE);
                player.setTeam(Team.BLUE);
                player.updateArmor();
            }
            DatabaseManager.getPlayer(player.getUuid(), databasePlayer -> {
                //weapons
                DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                Optional<AbstractWeapon> optionalWeapon = pveStats
                        .getWeaponInventory()
                        .stream()
                        .filter(AbstractWeapon::isBound)
                        .filter(abstractWeapon -> abstractWeapon.getSpecializations() == player.getSpecClass())
                        .findFirst();
                optionalWeapon.ifPresent(abstractWeapon -> {
                    warlordsPlayer.getCosmeticSettings().setWeaponSkin(abstractWeapon.getSelectedWeaponSkin());
                    warlordsPlayer.setWeapon(abstractWeapon);
                    abstractWeapon.applyToWarlordsPlayer(warlordsPlayer, this);
                    player.updateEntity();
                    player.getSpec().updateCustomStats();
                });
            });
        }
    }

    @Override
    default void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        AbstractWeapon weapon = warlordsPlayer.getWeapon();
        if (weapon == null) {
            WeaponOption.showWeaponStats(warlordsPlayer, player);
        } else {
            WeaponOption.showPvEWeapon(warlordsPlayer, player);
        }

        player.getInventory().setItem(7, new ItemBuilder(Material.GOLD_NUGGET).name(Component.text("Upgrade Talisman", NamedTextColor.GREEN)).get());
        if (warlordsPlayer.getWeapon() instanceof AbstractLegendaryWeapon) {
            ((AbstractLegendaryWeapon) warlordsPlayer.getWeapon()).updateAbilityItem(warlordsPlayer, player);
        }
    }

    @Override
    default void onSpecChange(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            ((WarlordsPlayer) player).resetAbilityTree();
        }
    }

    PveRewards<?> getRewards();

    private void addMobDrop(WarlordsEntity event, MobDrops event1) {
        getRewards().getPlayerRewards(event.getUuid())
                    .getMobDropsGained()
                    .merge(event1, 1L, Long::sum);
    }

    default List<Component> healthScoreboard(Game game) {
        List<Component> list = new ArrayList<>();
        for (WarlordsEntity we : PlayerFilter.playingGame(game).filter(e -> e instanceof WarlordsPlayer)) {
            float healthRatio = we.getHealth() / we.getMaxHealth();
            NamedTextColor healthColor;
            if (healthRatio >= .5) {
                healthColor = NamedTextColor.GREEN;
            } else if (healthRatio >= .25) {
                healthColor = NamedTextColor.YELLOW;
            } else {
                healthColor = NamedTextColor.RED;
            }

            list.add(Component.text(we.getName() + ": ")
                              .append(Component.text(we.isDead() ? "DEAD" : "❤ " + Math.round(we.getHealth()), we.isDead() ? NamedTextColor.DARK_RED : healthColor))
                              .append(Component.text(" / "))
                              .append(Component.text("⚔ " + we.getMinuteStats().total().getKills(), NamedTextColor.RED)));
        }
        return list;
    }

}
