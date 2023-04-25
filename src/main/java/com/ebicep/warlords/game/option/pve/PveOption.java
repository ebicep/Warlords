package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.*;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Zombie;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface PveOption {

    int playerCount();

    default int mobCount() {
        return (int) getMobs()
                .stream()
                .filter(mob -> mob.getWarlordsNPC().getTeam() == Team.RED)
                .count();
    }

    Set<AbstractMob<?>> getMobs();

    int getTicksElapsed();

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
                    if (upgradeType == AutoUpgradeProfile.AutoUpgradeEntry.UpgradeType.MASTER) {
                        upgradeBranch.purchaseMasterUpgrade(warlordsPlayer, true);
                    } else {
                        upgradeBranch.purchaseUpgrade(upgradeList, warlordsPlayer, upgrade, entry.getUpgradeIndex(), true);
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

    PveRewards<?> getRewards();

    ConcurrentHashMap<AbstractMob<?>, Integer> getMobsMap();

    Game getGame();

    private void addMobDrop(WarlordsEntity event, MobDrops event1) {
        getRewards().getPlayerRewards(event.getUuid())
                    .getMobDropsGained()
                    .merge(event1, 1L, Long::sum);
    }

}
